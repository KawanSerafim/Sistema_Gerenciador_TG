package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.repositorios;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.mapeador.MandatoDiretorMapeador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MandatoDiretorRepositorioImpl implements MandatoDiretorRepositorio {

    private final MandatoDiretorJpaRepositorio repositorio;

    @Override
    @Transactional
    public void salvar(MandatoDiretor mandato) {
        var modelo = MandatoDiretorMapeador.paraModelo(mandato);
        repositorio.save(modelo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MandatoDiretor> buscarMandatoVigente() {
        return repositorio.buscarMandatoVigenteNaData(LocalDate.now())
                .map(MandatoDiretorMapeador::paraDominio);
    }
}
