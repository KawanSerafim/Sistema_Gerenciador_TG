package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.mapeador.TurmaMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TurmaRepositorioImpl implements TurmaRepositorio {
    private final TurmaJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(Turma turma) {
        var modelo = TurmaMapeador.paraModelo(turma);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Turma> buscarPorId(TurmaId id) {
        return repositorio.findById(id.texto())
                .map(TurmaMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turma> buscarTodasPorIds(Set<TurmaId> ids) {
        var idsTexto = ids.stream()
                .map(TurmaId::texto)
                .toList();

        return repositorio.findAllById(idsTexto).stream()
                .map(TurmaMapeador::paraDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Turma> buscarPorCursoIdEDisciplinaETurnoEAnoESemestre(
            CursoId cursoId,
            Disciplina disciplina,
            Turno turno,
            PeriodoLetivo periodoLetivo
    ) {
        var ano = periodoLetivo.anoValor();
        var semestre = periodoLetivo.semestreValor();

        return repositorio.findByCursoIdAndDisciplinaAndTurnoAndAnoAndSemestre(
                cursoId.texto(),
                disciplina,
                turno,
                ano,
                semestre
        ).map(TurmaMapeador::paraDominio);
    }

    @Override
    public List<Turma> buscarPorProfessorTgId(ProfessorId professorId) {
        return repositorio.findByProfessorId(professorId.texto())
                .stream().map(TurmaMapeador::paraDominio)
                .toList();
    }
}