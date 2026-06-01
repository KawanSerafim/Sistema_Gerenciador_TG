package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.MandatoDiretor;

import java.util.Optional;

public interface MandatoDiretorRepositorio {
    Optional<MandatoDiretor> buscarMandatoVigente();
    void salvar(MandatoDiretor mandato);
}
