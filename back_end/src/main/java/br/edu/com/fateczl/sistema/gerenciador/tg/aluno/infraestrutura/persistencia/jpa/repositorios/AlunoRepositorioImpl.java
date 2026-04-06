package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.mapeador.AlunoMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo.AlunoModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import lombok.RequiredArgsConstructor;
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
    public Optional<Aluno> buscarPorContaId(ContaUsuarioId contaUsuarioId) {
        String contaUsuarioIdTexto = contaUsuarioId.texto();

        return repositorio.findByContaUsuarioId(contaUsuarioIdTexto)
                .map(AlunoMapeador::paraDominio);
    }
}