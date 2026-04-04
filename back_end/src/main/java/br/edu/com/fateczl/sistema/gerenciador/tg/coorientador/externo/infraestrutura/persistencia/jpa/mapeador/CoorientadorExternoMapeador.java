package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.CoorientadorExternoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.modelo.CoorientadorExternoModelo;

import java.util.UUID;

public class CoorientadorExternoMapeador {
    private CoorientadorExternoMapeador() {}

    public static CoorientadorExternoModelo paraModelo(
            CoorientadorExterno dominio
    ) {
        return new CoorientadorExternoModelo(
                dominio.idTexto(),
                dominio.nomeTexto(),
                dominio.identificacao()
        );
    }

    public static CoorientadorExterno paraDominio(
            CoorientadorExternoModelo modelo
    ) {
        return CoorientadorExterno.carregar(
                new CoorientadorExternoId(UUID.fromString(modelo.getId())),
                new Nome(modelo.getNome()),
                new Origem(modelo.getOrigem())
        );
    }
}