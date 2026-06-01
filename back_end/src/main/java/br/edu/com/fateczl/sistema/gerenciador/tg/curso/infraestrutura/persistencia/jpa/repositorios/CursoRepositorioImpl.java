package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Pagina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.entidade.Curso;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.repositorio.CursoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.mapeador.CursoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo.CursoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    @Transactional(readOnly = true)
    public Pagina<Curso> buscarTodos(int numeroPagina, int tamanhoPagina) {
        // Traduz os números puros para o objeto do Spring Data
        PageRequest pageRequest = PageRequest.of(numeroPagina, tamanhoPagina);

        // Faz a busca no banco, o spring coloca o LIMIT
        Page<CursoModelo> paginaSpring = repositorio.findAll(pageRequest);

        // Converte a lista de Modelos para Entidades
        List<Curso> cursosDominio = paginaSpring.getContent().stream()
                .map(CursoMapeador::paraDominio)
                .toList();

        // Junta o curso e as informações da pagina e retorna ela
        return new Pagina<>(
                cursosDominio,
                paginaSpring.getNumber(),
                paginaSpring.getTotalPages(),
                paginaSpring.getTotalElements()
        );
    }


}