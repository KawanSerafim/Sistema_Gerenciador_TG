package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.configuracao.seguranca;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.entidade.ContaUsuario;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.repositorio.ContaUsuarioRepositorio;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ServicoDetalhesUsuarioImpl implements UserDetailsService {
    private final ContaUsuarioRepositorio repositorio;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return repositorio.buscarPorEmail(new Email(username))
                .map(this::mapearParaDetalhes)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Conta não encontrada: " + username
                ));
    }

    private UserDetails mapearParaDetalhes(ContaUsuario conta) {
        var permissoesSpring = conta.autoridades().stream()
                .map(autoridade -> new SimpleGrantedAuthority(
                        autoridade.name())
                ).collect(Collectors.toSet());

        return new DetalhesUsuarioImpl(
                conta.emailTexto(),
                conta.senhaTexto(),
                permissoesSpring,
                conta.status()
        );
    }
}