package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.modelo.CoorientadorExternoModelo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoorientadorExternoRepositorioImpl
        implements CoorientadorExternoRepositorio {
    private final CoorientadorExternoJpaRepositorio repositorio;

    @Override
    public void salvar(CoorientadorExterno coorientadorExterno) {
        var modelo = new CoorientadorExternoModelo(
                coorientadorExterno.idTexto(),
                coorientadorExterno.nomeTexto(),
                coorientadorExterno.identificacao()
        );
        repositorio.save(modelo);
    }

    @Override
    public Optional<CoorientadorExterno> buscarPorNomeEOrigem(
            Nome nome,
            Origem origem
    ) {
        return repositorio.findByNomeAndOrigem(nome.valor(), origem.valor())
                .map(this::paraDominio);
    }

    private CoorientadorExterno paraDominio(CoorientadorExternoModelo modelo) {
        return CoorientadorExterno.carregar(
                new CoorientadorExternoId(UUID.fromString(modelo.getId())),
                new Nome(modelo.getNome()),
                new Origem(modelo.getOrigem())
        );
    }
}