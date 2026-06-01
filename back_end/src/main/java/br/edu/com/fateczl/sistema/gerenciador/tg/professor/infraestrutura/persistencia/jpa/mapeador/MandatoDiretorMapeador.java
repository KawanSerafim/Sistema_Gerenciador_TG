package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.mapeador;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.MandatoDiretorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo.MandatoDiretorModelo;

import java.util.UUID;

public class MandatoDiretorMapeador {

    private MandatoDiretorMapeador() {}

    public static MandatoDiretorModelo paraModelo(MandatoDiretor mandato) {
        return new MandatoDiretorModelo(
                mandato.id().texto(),
                mandato.professorId().texto(),
                mandato.dataInicio(),
                mandato.dataFim(),
                mandato.assinaturaBase64(),
                mandato.ativo()
                );

    }

    public static MandatoDiretor paraDominio(MandatoDiretorModelo modelo) {
        return MandatoDiretor.carregar(
                new MandatoDiretorId(UUID.fromString(modelo.getId())),
                new ProfessorId(UUID.fromString(modelo.getProfessorId())),
                modelo.getDataInicio(),
                modelo.getDataFim(),
                modelo.getAssinaturaBase64(),
                modelo.isAtivo()
        );
    }
}
