package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.mapeador.GrupoTgMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GrupoTgRepositorioImpl implements GrupoTgRepositorio {
    private final GrupoTgJpaRepositorio repositorio;

    @Override
    public void salvar(GrupoTg grupoTg) {
        var modelo = GrupoTgMapeador.paraModelo(grupoTg);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GrupoTg> buscarPorAlunoIdECursoId(
            AlunoId alunoId,
            CursoId cursoId
    ) {
        return repositorio.findByAlunoAndCurso(
                alunoId.valor().toString(),
                cursoId.valor().toString()
        ).map(GrupoTgMapeador::paraDominio);
    }
}