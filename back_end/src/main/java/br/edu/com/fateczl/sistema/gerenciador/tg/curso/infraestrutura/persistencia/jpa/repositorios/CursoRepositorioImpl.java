package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.mapeador.CursoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CursoRepositorioImpl implements CursoRepositorio {
    private final CursoJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(Curso curso) {
        var modelo = CursoMapeador.paraModelo(curso);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorId(CursoId id) {
        return repositorio.findById(id.texto())
                .map(CursoMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorNome(Nome nome) {
        return repositorio.findByNome(nome.valor())
                .map(CursoMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> buscarPorCoordenadorId(ProfessorId professorId) {
        return repositorio.findByCoordenadorId(professorId.texto())
                .map(CursoMapeador::paraDominio);
    }
}