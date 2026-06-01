package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.modelo;

import jakarta.persistence.Embeddable;

@Embeddable
public class MembroExternoModelo {

    private String nome;
    private String email;
    private String telefone;

    // Construtor vazio exigido pelo JPA
    public MembroExternoModelo() {}

    public MembroExternoModelo(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
