package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AdministradorModelo {
    @Id
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "conta_usuario_id", nullable = false, unique = true)
    private String contaUsuarioId;
}