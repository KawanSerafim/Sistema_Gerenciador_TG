package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.repositorio;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.entidade.CoorientadorExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.dominio.objetosvalor.Origem;

import java.util.Optional;

public interface CoorientadorExternoRepositorio {
    CoorientadorExterno salvar(CoorientadorExterno coorientadorExterno);
    Optional<CoorientadorExterno> buscarPorNomeEOrigem(Nome nome,
                                                       Origem origem);
}