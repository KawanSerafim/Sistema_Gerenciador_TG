package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.ouvintes;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.MembroExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.BancaMarcadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores.RemetenteEmailImpl;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class NotificarMembrosBancaOuvinte {
    private final BancaRepositorio bancaRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final ContaUsuarioRepositorio contaUsuarioRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final RemetenteEmail remetenteEmail;
    private final ArmazenamentoArquivoPorta armazenamentoPorta;

    public NotificarMembrosBancaOuvinte(
            BancaRepositorio bancaRepositorio,
            ProfessorRepositorio professorRepositorio,
            ContaUsuarioRepositorio contaUsuarioRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            RemetenteEmail remetenteEmail,
            ArmazenamentoArquivoPorta armazenamentoPorta) {
        this.bancaRepositorio = bancaRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.contaUsuarioRepositorio = contaUsuarioRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.remetenteEmail = remetenteEmail;
        this.armazenamentoPorta = armazenamentoPorta;
    }

    private static final Logger log = LoggerFactory.getLogger(
            NotificarMembrosBancaOuvinte.class
    );

    @EventListener
    //Busca no banco de dados assincrona
    @Async
    //Usa transactional para que a conexão entre banco de dados não seja fechada
    @Transactional
    public void processar(BancaMarcadaEvento evento) {
        bancaRepositorio.buscarPorId(new BancaId(UUID.fromString(evento.idBanca()))).ifPresent(banca -> {

            GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(banca.grupoId()).orElseThrow();
            // Resgata o Arquivo
            byte[] trabalhoBytes;
            String nomeArquivo;

            if (grupo.caminhoArquivoTrabalho() != null) {
                trabalhoBytes = armazenamentoPorta.recuperarArquivo(grupo.caminhoArquivoTrabalho());
                String extensao = grupo.caminhoArquivoTrabalho().substring(grupo.caminhoArquivoTrabalho()
                        .lastIndexOf("."));
                nomeArquivo = "TG_" + grupo.nomeTemaTg()
                        .replaceAll("\\s+", "_") + extensao;
            } else {
                nomeArquivo = "Trabalho_Final.pdf";
                trabalhoBytes = null;
            }

            // Monta o corpo do E-mail
            String data = banca.dataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
            String assunto = "Aviso: Agendamento de Banca de TG";
            //Prepara o email se membro da banca ou integrante de grupo
            String htmlAvaliadores = montarHtmlEmail(grupo.nomeTemaTg(), data,
                    banca.local(), trabalhoBytes != null);
            String htmlAlunos = montarHtmlEmail(grupo.nomeTemaTg(), data,
                    banca.local(), false);


            // Notifica Avaliadores Internos (COM ANEXO)
            for (ProfessorId profId : banca.avaliadoresInternos()) {
                professorRepositorio.buscarPorId(profId).ifPresent(prof -> {
                    ContaUsuario conta = contaUsuarioRepositorio.buscarPorId(prof.contaUsuarioId())
                            .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                                    "Conta do professor da banca"));
                    if (trabalhoBytes != null) {
                        remetenteEmail.enviarEmailComAnexo(new Email(conta.emailTexto()), assunto, htmlAvaliadores,
                                trabalhoBytes, nomeArquivo);
                    } else {
                        remetenteEmail.enviarEmail(new Email(conta.emailTexto()), assunto, htmlAvaliadores);
                    }
                });
            }

            // Notifica Convidados Externos (COM ANEXO)
            for (MembroExterno membroExterno : banca.avaliadoresExternos()) {
                try {
                    if (trabalhoBytes != null) {
                        remetenteEmail.enviarEmailComAnexo(new Email(membroExterno.email()), assunto,
                                htmlAlunos, trabalhoBytes, nomeArquivo);
                    } else {
                        remetenteEmail.enviarEmail(new Email(membroExterno.email()), assunto, htmlAlunos);
                    }
                } catch (Exception e) {
                    log.error("Ignorando formatação de email inválida {}: {}", membroExterno.email(), e.getMessage());
                }
            }

            // Envia versão SEM O AVISO DE ANEXO para alunos
            grupo.alunosIds().forEach(alunoId -> {
                alunoRepositorio.buscarPorId(alunoId).ifPresent(aluno -> {
                    if (aluno.contaUsuarioId() != null) {
                        contaUsuarioRepositorio.buscarPorId(aluno.contaUsuarioId()).ifPresent(conta ->
                                remetenteEmail.enviarEmail(conta.email(), assunto, htmlAlunos)
                        );
                    }
                });
            });
        });
    }
// ---- METODOS AUXILIARES --------//

    private String montarHtmlEmail(String tema, String dataHora, String local, boolean temAnexo) {
        String avisoAnexo = temAnexo
                ? "<p style='font-size: 15px; color: #333;'>O arquivo em PDF contendo o Trabalho de Graduação finalizado encontra-se " +
                "<strong>anexado a este e-mail</strong> para sua avaliação.</p>"
                : "";

        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                <h2 style="color: #b30000; text-align: center;">Agendamento de Banca - FATEC</h2>
                <p style="font-size: 16px; color: #333;">Olá,</p>
                <p style="font-size: 16px; color: #333;">Foi registrado o agendamento de uma defesa de Trabalho de Graduação e você faz parte dos membros convocados (ou é o autor).</p>
               \s
                <div style="background-color: #f8f9fa; padding: 15px; border-left: 4px solid #b30000; margin: 25px 0;">
                    <p style="margin: 5px 0; font-size: 15px;"><strong>Tema do Trabalho:</strong> %s</p>
                    <p style="margin: 5px 0; font-size: 15px;"><strong>Data e Hora:</strong> %s</p>
                    <p style="margin: 5px 0; font-size: 15px;"><strong>Local / Sala:</strong> %s</p>
                </div>
               \s
                %s
               \s
                <hr style="border: 0; border-top: 1px solid #eee; margin: 30px 0 20px 0;">
                <p style="font-size: 12px; color: #999; text-align: center;">Este é um e-mail automático do Sistema Gerenciador de TG.</p>
            </div>
           \s""".formatted(tema, dataHora, local, avisoAnexo);
    }
}
