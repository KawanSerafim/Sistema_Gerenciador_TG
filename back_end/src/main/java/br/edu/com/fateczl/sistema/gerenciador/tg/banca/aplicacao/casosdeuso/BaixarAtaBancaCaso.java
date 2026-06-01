package br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.casosdeuso;


import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas.GeradorAtaBancaPdf;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BaixarAtaBancaCaso {
    private final BancaRepositorio bancaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final GeradorAtaBancaPdf geradorAtaBancaPdf;

    public BaixarAtaBancaCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ProfessorRepositorio professorRepositorio,
            CursoRepositorio cursoRepositorio,
            GeradorAtaBancaPdf geradorAtaBancaPdf) {
        this.bancaRepositorio = bancaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.geradorAtaBancaPdf = geradorAtaBancaPdf;
    }

    public record Comando(String idBanca) {}
    public record Saida(byte[] conteudo, String nomeArquivo) {}

    public Saida executar(Comando comando) {
        Banca banca = bancaRepositorio.buscarPorId(new BancaId(UUID.fromString(comando.idBanca())))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "Banca"));

        GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(banca.grupoId())
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "Grupo TG"));

        Curso curso = cursoRepositorio.buscarPorId(grupo.cursoId())
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "Curso"));

        // Prepara Nomes formato ABNT (SOBRENOME, Nome)
        List<String> nomesAlunos = alunoRepositorio.buscarTodosPorIds(grupo.alunosIds()).stream()
                .map(a -> formatarNomeABNT(a.nomeTexto()))
                .toList();

        // Mapeia Membros com as Instituições
        List<GeradorAtaBancaPdf.MembroBancaAta> membrosBanca = new ArrayList<>();

        if (grupo.orientadorId() != null) {
            professorRepositorio.buscarPorId(grupo.orientadorId())
                    .ifPresent(p -> membrosBanca.add(new GeradorAtaBancaPdf.MembroBancaAta(p.nomeTexto(),
                            "Fatec Zona Leste")));
        }

        banca.avaliadoresInternos().forEach(profId -> {
            if (grupo.orientadorId() == null || !profId.equals(grupo.orientadorId())) {
                professorRepositorio.buscarPorId(profId)
                        .ifPresent(p -> membrosBanca.add(new GeradorAtaBancaPdf.MembroBancaAta(p.nomeTexto(),
                                "Fatec Zona Leste")));
            }
        });

        banca.avaliadoresExternos().forEach(ext ->
                membrosBanca.add(new GeradorAtaBancaPdf.MembroBancaAta(ext.nome(), "Empresa Externa"))
        );

        // Prepara as datas separadas
        LocalDateTime dataBanca = banca.dataHora() != null ? banca.dataHora() : LocalDateTime.now();
        //Separa em strings com o formato: DD/mesPorEstenso/AAAA
        List<String> dataSeparada = formatarDatas(dataBanca);



        // Delega para a Porta do Thymeleaf + HTMLToPDF
        GeradorAtaBancaPdf.DadosAta dadosAta = new GeradorAtaBancaPdf.DadosAta(
                grupo.nomeTemaTg(),
                curso.nomeTexto(),
                dataSeparada.getFirst(), dataSeparada.get(1), dataSeparada.get(2),
                nomesAlunos,
                membrosBanca
        );
        byte[] pdfBytes = geradorAtaBancaPdf.gerar(dadosAta);

        String nomeArquivo = "ata_da_banca" + grupo.nomeTemaTg().replaceAll("\\s+", "_") + ".pdf";

        return new Saida(pdfBytes, nomeArquivo);
    }
    // ----- METODOS AUXILIARES -----

    private String formatarNomeABNT(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) return "";
        String[] partes = nomeCompleto.trim().split("\\s+");
        if (partes.length == 1) return partes[0].toUpperCase();

        String ultimoNome = partes[partes.length - 1].toUpperCase();
        StringBuilder resto = new StringBuilder();
        for (int i = 0; i < partes.length - 1; i++) {
            resto.append(partes[i]).append(" ");
        }
        return ultimoNome + ", " + resto.toString().trim();
    }

    private List<String> formatarDatas(LocalDateTime dataBanca){
        List<String> dataSeparada = new ArrayList<>();
        String dia = String.format("%02d", dataBanca.getDayOfMonth());
        dataSeparada.add(dia);
        String[] meses = {
                "janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        };
        String mesExtenso = meses[dataBanca.getMonthValue() - 1];
        dataSeparada.add(mesExtenso);
        String ano = String.valueOf(dataBanca.getYear());
        dataSeparada.add(ano);
        return dataSeparada;

    }
}
