package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.mapeador.AlunoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo.AlunoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlunoRepositorioImpl implements AlunoRepositorio {
    private final AlunoJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(Aluno aluno) {
        var modelo = AlunoMapeador.paraModelo(aluno);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorMatricula(Matricula matricula) {
        return repositorio.findByMatricula(matricula.valor())
                .map(AlunoMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Aluno>> buscarPorMatriculas(
            List<Matricula> matriculas
    ) {
        List<String> matriculasTextos = matriculas.stream()
                .map(Matricula::valor)
                .toList();

        List<AlunoModelo> modelos = repositorio.findByMatriculaIn(
                matriculasTextos
        );

        if(modelos.isEmpty()) {
            return Optional.empty();
        }

        List<Aluno> alunos = modelos.stream()
                .map(AlunoMapeador::paraDominio)
                .toList();

        return Optional.of(alunos);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorContaId(ContaUsuarioId id) {
        return repositorio.findByContaUsuarioId(id.texto())
                .map(AlunoMapeador::paraDominio);
    }

    /**
     * Busca a lista de alunos que possuem o id de turma informado
     * @param turmaId TurmaId da turma de alunos desejada
     * @param pagina Integer numero da pagina
     * @param tamanho Integer quantidade de itens da pagina
     * @return (Pagina<Aluno>) Pagina de alunos ou lista vazia
     */
    @Override
    @Transactional(readOnly = true)
    public Pagina<Aluno> buscarPorTurmaId(TurmaId turmaId, Integer pagina, Integer tamanho) {
        Pageable paginavel = PageRequest.of(pagina, tamanho);
        Page<AlunoModelo> paginaSpring = repositorio.findByTurmasIdsContaining(turmaId.texto(), paginavel);


        // Mapeia de Modelo para Entidade
        List<Aluno> alunosDominio = paginaSpring.getContent().stream()
                .map(AlunoMapeador::paraDominio) // Aquele mapeador que arrumamos com EnumMap!
                .toList();

        // Retorna o seu Record puro
        return new Pagina<>(
                alunosDominio,
                paginaSpring.getNumber(),
                paginaSpring.getTotalPages(),
                paginaSpring.getTotalElements()
        );
    }

    /**
     * Busca a lista de alunoDtos que possuem o id de turma informado e não estão em um grupo
     * @param turmaId TurmaId da turma de alunoDtos desejada
     * @return (List<Aluno>) lista de alunoDtos ou lista vazia
     */
    @Override
    @Transactional(readOnly = true)
    public List<Aluno> buscarSemGrupoPorTurmasIds(List<TurmaId> turmaId) {
        //Converte a lista de TurmaIds em Lista de strings
        List<String> idsStr = turmaId.stream()
                .map(TurmaId::texto)
                .toList();
        return repositorio.findAlunosSemGrupoPorTurmasIds(idsStr)
                .stream().map(AlunoMapeador::paraDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorId(AlunoId alunoId) {
        return repositorio.findById(alunoId.texto())
                .map(AlunoMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Aluno> buscarTodosPorIds(List<AlunoId> alunoIds) {
        List<String> idsTexto = alunoIds.stream()
                .map(AlunoId::texto)
                .toList();
        return repositorio.findAllById(idsTexto)
                .stream()
                .map(AlunoMapeador::paraDominio)
                .toList();
    }


}