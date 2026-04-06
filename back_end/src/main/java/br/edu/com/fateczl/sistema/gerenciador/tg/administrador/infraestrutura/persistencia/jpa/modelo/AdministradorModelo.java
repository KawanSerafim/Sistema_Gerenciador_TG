package br.edu.com.fateczl.sistema.gerenciador.tg.administrador.infraestrutura.persistencia.jpa.modelo;

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
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(
            length = 36,
            name = "conta_usuario_id",
            nullable = false,
            unique = true
    )
    private String contaUsuarioId;
}