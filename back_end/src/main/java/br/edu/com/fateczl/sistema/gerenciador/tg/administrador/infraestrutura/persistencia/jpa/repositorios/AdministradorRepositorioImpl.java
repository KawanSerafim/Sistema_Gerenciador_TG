package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.entidade.Administrador;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.objetovalor.AdministradorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.mapeador.AdministradorMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdministradorRepositorioImpl implements AdministradorRepositorio {
    private final AdministradorJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(Administrador administrador) {
        var modelo = AdministradorMapeador.paraModelo(administrador);
        repositorio.save(modelo);
    }

    @Override
    @Transactional
    public Optional<Administrador> buscarPorId(AdministradorId id) {
        return repositorio.findById(id.valor().toString())
                .map(AdministradorMapeador::paraDominio);
    }

    @Override
    @Transactional
    public Optional<Administrador> buscarPorEmail(Email email) {
        return repositorio.findByEmailDaConta(email.valor())
                .map(AdministradorMapeador::paraDominio);
    }
}