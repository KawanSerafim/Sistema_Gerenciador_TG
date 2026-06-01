package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.ouvintes;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas.GeradorCertificadoPdf;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.MembroExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.BancaAvaliadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class GerarCertificadosBancaOuvinte {
    private static final Logger log = LoggerFactory.getLogger(GerarCertificadosBancaOuvinte.class);

    private final BancaRepositorio bancaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final MandatoDiretorRepositorio mandatoDiretorRepositorio;
    private final GeradorCertificadoPdf geradorCertificadoPdf;
    private final RemetenteEmail remetenteEmail;

    public GerarCertificadosBancaOuvinte(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio,
            CursoRepositorio cursoRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio,
            MandatoDiretorRepositorio mandatoDiretorRepositorio,
            GeradorCertificadoPdf geradorCertificadoPdf,
            RemetenteEmail remetenteEmail) {
        this.bancaRepositorio = bancaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.mandatoDiretorRepositorio = mandatoDiretorRepositorio;
        this.geradorCertificadoPdf = geradorCertificadoPdf;
        this.remetenteEmail = remetenteEmail;
    }

    @EventListener
    @Async
    @Transactional
    public void processar(BancaAvaliadaEvento evento) {
        log.info("Iniciando geração de certificados para a banca: {}", evento.bancaId());

        try {
            // Busca os dados centrais
            Banca banca = bancaRepositorio.buscarPorId(new BancaId(UUID.fromString(evento.bancaId()))).orElseThrow();
            GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(banca.grupoId()).orElseThrow();
            Curso curso = cursoRepositorio.buscarPorId(grupo.cursoId()).orElseThrow();

            // Garante que exista um Diretor Vigente para assinar
            MandatoDiretor mandatoAtual = mandatoDiretorRepositorio.buscarMandatoVigente()
                    .orElseThrow(() -> new RuntimeException(
                            "Nenhum mandato de diretor vigente encontrado. Certificados não gerados."));
            Professor diretor = professorRepositorio.buscarPorId(mandatoAtual.professorId()).orElseThrow();

            // Prepara a string de nomes dos alunos do grupo
            List<String> nomesAlunos = alunoRepositorio.buscarTodosPorIds(grupo.alunosIds()).stream()
                    .map(Aluno::nomeTexto).toList();
            String stringAlunos = formatarListaNomes(nomesAlunos);

            //  Prepara a lista de Membros da Banca
            List<GeradorCertificadoPdf.MembroCertificado> membrosParaPdf = new ArrayList<>();
            List<Professor> avaliadoresInternosParaEmail = new ArrayList<>();

            Professor orientador = professorRepositorio.buscarPorId(grupo.orientadorId()).orElseThrow();
            membrosParaPdf.add(new GeradorCertificadoPdf.MembroCertificado(
                    "Orientadora", "Prof. " + orientador.nomeTexto()));

            banca.avaliadoresInternos().forEach(profId -> {
                if (!profId.equals(orientador.id())) {
                    professorRepositorio.buscarPorId(profId).ifPresent(p -> {
                        membrosParaPdf.add(new GeradorCertificadoPdf.MembroCertificado(
                                "Banca", "Prof. " + p.nomeTexto()));
                        avaliadoresInternosParaEmail.add(p);
                    });
                }
            });

            banca.avaliadoresExternos().forEach(ext ->
                    membrosParaPdf.add(new GeradorCertificadoPdf.MembroCertificado("Banca", ext.nome()))
            );

            // Prepara Datas
            String dataDefesa = banca.dataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String dataEmissao = LocalDate.now().getDayOfMonth()
                    + " de " + obterMesExtenso(LocalDate.now()) + " de " + LocalDate.now().getYear();

            // GERA O PDF EM MEMÓRIA (Bytes)
            byte[] pdfBytes = geradorCertificadoPdf.gerar(
                    grupo.nomeTemaTg(),
                    stringAlunos,
                    curso.nomeTexto(),
                    dataDefesa,
                    membrosParaPdf,
                    dataEmissao,
                    "Prof. Dr. " + diretor.nomeTexto(),
                    mandatoAtual.assinaturaBase64() // Injeta a imagem validada no banco
            );

            String nomeArquivo = "Certificado_Banca_" +
                    grupo.nomeTemaTg().replaceAll("\\s+", "_") + ".pdf";
            String htmlEmail = montarHtmlEmailCertificado(grupo.nomeTemaTg());
            String assunto = "Certificado de Participação em Banca - FATEC ZL";

            // Dispara os e-mails com o PDF anexado

            //Orientador
            ContaUsuario contaOrientador = contaUsuarioRepositorio.buscarPorId(orientador.contaUsuarioId())
                    .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                            "Conta do orientador do grupo"));

            remetenteEmail.enviarEmailComAnexo(contaOrientador.emailTexto(), assunto, htmlEmail, pdfBytes, nomeArquivo);

            for (Professor avaliador : avaliadoresInternosParaEmail) {
                ContaUsuario contaAvaliador = contaUsuarioRepositorio.buscarPorId(avaliador.contaUsuarioId())
                        .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                                "Conta do professor da banca"));
                remetenteEmail.enviarEmailComAnexo(contaAvaliador.emailTexto(), assunto, htmlEmail, pdfBytes, nomeArquivo);
            }

            for (MembroExterno ext : banca.avaliadoresExternos()) {
                remetenteEmail.enviarEmailComAnexo(ext.email(), assunto, htmlEmail, pdfBytes, nomeArquivo);
            }

            log.info("Certificados enviados com sucesso para a banca: {}", banca.idTexto());

        } catch (Exception e) {
            log.error("Falha ao gerar certificados da banca: {} - :{}",evento.bancaId(), e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private String formatarListaNomes(List<String> nomes) {
        if (nomes.isEmpty()) return "";
        if (nomes.size() == 1) return nomes.get(0);
        if (nomes.size() == 2) return nomes.get(0) + " e " + nomes.get(1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nomes.size() - 1; i++) {
            sb.append(nomes.get(i)).append(", ");
        }
        // Remove a última vírgula e espaço, e adiciona o "e"
        sb.delete(sb.length() - 2, sb.length());
        sb.append(" e ").append(nomes.getLast());
        return sb.toString();
    }

    private String obterMesExtenso(LocalDate data) {
        String[] meses = {"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        return meses[data.getMonthValue() - 1];
    }

    private String montarHtmlEmailCertificado(String tema) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                <h2 style="color: #b30000; text-align: center;">Certificado de Participação - FATEC</h2>
                <p style="font-size: 16px; color: #333;">Prezado(a) Professor(a),</p>
                <p style="font-size: 16px; color: #333;">Agradecemos imensamente a sua valiosa contribuição na avaliação do Trabalho de Graduação:</p>
               \s
                <div style="background-color: #f8f9fa; padding: 15px; border-left: 4px solid #b30000; margin: 25px 0;">
                    <p style="margin: 5px 0; font-size: 15px; font-weight: bold; text-align: center;">%s</p>
                </div>
               \s
                <p style="font-size: 15px; color: #333;">O seu <strong>Certificado Oficial de Participação na Banca Examinadora</strong> encontra-se anexado a este e-mail em formato PDF.</p>
               \s
                <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0 20px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Atenciosamente,<br>Coordenação de TG - Faculdade de Tecnologia da Zona Leste</p>
            </div>
           \s""".formatted(tema);
    }
}
