package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.mapeador.ProfessorMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo.ProfessorModelo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfessorRepositorioImpl implements ProfessorRepositorio {
    private final ProfessorJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(Professor professor) {
        var modelo = ProfessorMapeador.paraModelo(professor);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Professor> buscarPorMatricula(Matricula matricula) {
        return repositorio.findByMatricula(matricula.valor())
                .map(ProfessorMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Professor> buscarPorEmail(Email email) {
        return repositorio.findByEmailDaConta(email.valor())
                .map(ProfessorMapeador::paraDominio);
    }

    /**
     * Lista os professores por cargoProfessor
     * @param cargoProfessor - enum cargoProfessor
     * @return (List<Professor>) lista de professores
     */
    @Transactional(readOnly = true)
    public List<Professor> listarPorCargoProfessor(CargoProfessor cargoProfessor) {

        //Pega no BD como modelo
        List<ProfessorModelo> professoresModelo = repositorio.findByCargo(cargoProfessor);
        //Para cada modelo chama o mapeador e transforma em entidade dominio, junto tudo em lista
        return professoresModelo.stream()
                    .map(ProfessorMapeador::paraDominio).toList();


    }
}