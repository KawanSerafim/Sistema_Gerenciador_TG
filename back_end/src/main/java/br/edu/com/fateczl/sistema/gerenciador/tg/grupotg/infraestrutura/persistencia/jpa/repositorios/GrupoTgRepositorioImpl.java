package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.mapeador.AlunoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.mapeador.GrupoTgMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GrupoTgRepositorioImpl implements GrupoTgRepositorio {
    private final GrupoTgJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(GrupoTg grupoTg) {
        var modelo = GrupoTgMapeador.paraModelo(grupoTg);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GrupoTg> buscarPorAlunoECurso(
            AlunoId alunoId,
            CursoId cursoId
    ) {
        return repositorio.findByAlunoAndCurso(alunoId.texto(), cursoId.texto())
                .map(GrupoTgMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrupoTg> buscarPorTurmasIds(List<TurmaId> turmasIds) {
        //Transforma cada id de TurmaId em string e gera lista
        List<String> idsStr = turmasIds.stream().map(TurmaId::texto).toList();

        return repositorio.findGruposByTurmasIds(idsStr)
                .stream().map(GrupoTgMapeador::paraDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GrupoTg> buscarPorAlunoId(AlunoId alunoId) {
        return repositorio.findByAluno(alunoId).map(GrupoTgMapeador::paraDominio);
    }
}