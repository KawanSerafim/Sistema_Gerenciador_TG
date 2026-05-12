package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.mapeador.BancaMapeador;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.modelo.BancaModelo;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BancaRepositorioImpl implements BancaRepositorio {

    private final BancaJpaRepositorio jpaRepositorio;

    @Override
    @Transactional
    public void salvar(Banca banca) {
        BancaModelo modelo = BancaMapeador.paraModelo(banca);
        jpaRepositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Banca> buscarPorId(BancaId id) {
        return jpaRepositorio.findById(id.texto())
                .map(BancaMapeador::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeBancaParaGrupo(GrupoTgId grupoId) {
        // Usa a String primitiva limpa para o Spring Data JPA fazer o Select
        return jpaRepositorio.existsByGrupoId(grupoId.texto());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Banca> buscarPorGrupoId(GrupoTgId id) {
        String idTexto = id.texto();
        return jpaRepositorio.findByGrupoId(idTexto)
                .map(BancaMapeador::paraDominio);
    }
}
