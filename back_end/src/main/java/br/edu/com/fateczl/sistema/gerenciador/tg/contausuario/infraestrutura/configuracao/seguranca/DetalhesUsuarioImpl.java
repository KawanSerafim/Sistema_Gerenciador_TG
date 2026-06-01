package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.configuracao.seguranca;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record DetalhesUsuarioImpl(
        String nomeUsuario,
        String senha,
        Collection<? extends GrantedAuthority> autoridades,
        StatusContaUsuario statusContaUsuario
) implements UserDetails {

    @Override
    public String getUsername() { return this.nomeUsuario; }

    @Override
    public String getPassword() { return this.senha; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autoridades != null ? List.copyOf(autoridades) : List.of();
    }

    @Override
    public boolean isAccountNonLocked() { return isUsuarioAtivo(); }

    @Override
    public boolean isEnabled() { return isUsuarioAtivo(); }

    private boolean isUsuarioAtivo() {
        return this.statusContaUsuario != null
                && this.statusContaUsuario == StatusContaUsuario.ATIVO;
    }
}