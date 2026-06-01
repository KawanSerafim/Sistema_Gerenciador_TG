package br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor;


public record MembroExterno(
        String nome,
        String email,
        String telefone
) {
    public MembroExterno {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do membro externo é obrigatório");
        }
        if (email == null) {
            throw new IllegalArgumentException("E-mail do membro externo é obrigatório");
        }
    }
}
