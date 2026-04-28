package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Senha;

public interface CriptografoSenhas {
    Senha criptografar(String senhaLimpa);
    boolean comparar(String senhaLimpa, Senha senhaCriptografada);
}