package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio.CoorientadorExternoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.mapeador.CoorientadorExternoMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoorientadorExternoRepositorioImpl
        implements CoorientadorExternoRepositorio {
    private final CoorientadorExternoJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(CoorientadorExterno coorientadorExterno) {
        var modelo = CoorientadorExternoMapeador.paraModelo(
                coorientadorExterno
        );
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoorientadorExterno> buscarPorNomeEOrigem(
            Nome nome,
            Origem origem
    ) {
        return repositorio.findByNomeAndOrigem(nome.valor(), origem.valor())
                .map(CoorientadorExternoMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoorientadorExterno> buscarPorId(CoorientadorExternoId coorientadorExternoId) {
        return repositorio.findById(coorientadorExternoId.texto())
                .map(CoorientadorExternoMapeador::paraDominio);
    }
}