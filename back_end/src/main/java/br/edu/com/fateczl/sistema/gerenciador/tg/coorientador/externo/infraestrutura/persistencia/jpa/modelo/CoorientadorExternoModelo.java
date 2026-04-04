package br.edu.com.fateczl.sistema.gerenciador.tg.coorientador.externo.infraestrutura.persistencia.jpa.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coorientadores_externos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CoorientadorExternoModelo {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String origem;
}