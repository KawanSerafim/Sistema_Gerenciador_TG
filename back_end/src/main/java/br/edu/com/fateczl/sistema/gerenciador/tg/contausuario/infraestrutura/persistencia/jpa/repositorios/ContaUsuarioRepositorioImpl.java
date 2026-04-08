package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.mapeador.ContaUsuarioMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContaUsuarioRepositorioImpl implements ContaUsuarioRepositorio {
    private final ContaUsuarioJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(ContaUsuario contaUsuario) {
        var modelo = ContaUsuarioMapeador.paraModelo(contaUsuario);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContaUsuario> buscarPorId(ContaUsuarioId id) {
        return repositorio.findById(id.texto())
                .map(ContaUsuarioMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContaUsuario> buscarPorEmail(Email email) {
        return repositorio.findByEmail(email.valor())
                .map(ContaUsuarioMapeador::paraDominio);
    }
}