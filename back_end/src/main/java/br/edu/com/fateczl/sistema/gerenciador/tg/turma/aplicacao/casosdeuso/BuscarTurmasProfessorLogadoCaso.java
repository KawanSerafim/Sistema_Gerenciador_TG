package br.edu.com.fateczl.sistema.gerenciador.tg.turma.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.AutorizacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import org.springframework.security.core.context.SecurityContextHolder;


import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.AU_002_ACAO_NAO_PERMITIDA;
import static br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO;

public class BuscarTurmasProfessorLogadoCaso {
    //Caso de uso base
    private final BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso;
    private final ProfessorRepositorio professorRepositorio;

    public BuscarTurmasProfessorLogadoCaso(
            BuscarTurmasPorProfessorTgIdCaso buscarTurmasPorProfessorTgIdCaso,
            ProfessorRepositorio professorRepositorio
    ) {
        this.buscarTurmasPorProfessorTgIdCaso = buscarTurmasPorProfessorTgIdCaso;
        this.professorRepositorio = professorRepositorio;
    }

    public BuscarTurmasPorProfessorTgIdCaso.Resposta executar() {
        // Lógica exclusiva deste caso de uso: Descobrir quem é o usuario do JWT
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();

        // Busca e já lança a exceção direto se não encontrar professor com esse email
        Professor professorEncontrado = professorRepositorio.buscarPorEmail(new Email(emailLogado))
                .orElseThrow(() -> new GenericaExcecao(GN_001_REGISTRO_NAO_ENCONTRADO, "professor"));

        // Apenas professor tg tem turmas, se não for impede de prosseguir
        if (!professorEncontrado.podeSerProfessorTg()){
            throw new AutorizacaoExcecao(AU_002_ACAO_NAO_PERMITIDA,"listar turmas do professor tg");
        }
        // Reaproveita 100% da lógica do outro caso de uso (DRY aplicado com sucesso!)
        return buscarTurmasPorProfessorTgIdCaso.executar(
                new BuscarTurmasPorProfessorTgIdCaso.Comando(professorEncontrado.idTexto()));
    }
}
