package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.entidade.Turma;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.PeriodoLetivo;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.repositorio.TurmaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.mapeador.TurmaMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        return repositorio.findById(id.valor().toString())
                .map(TurmaMapeador::paraDominio);
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
                cursoId.valor().toString(),
                disciplina,
                turno,
                ano,
                semestre
        ).map(TurmaMapeador::paraDominio);
    }
}