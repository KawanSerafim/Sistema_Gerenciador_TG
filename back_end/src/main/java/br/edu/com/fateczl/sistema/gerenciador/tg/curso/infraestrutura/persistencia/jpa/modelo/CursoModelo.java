package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cursos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CursoModelo {
    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(length = 150, nullable = false)
    private String nome;

    @Column(length = 36, name = "coordenador_id", nullable = false)
    private String coordenadorId;

    @Embedded
    private ParametrosCursoModelo parametros;
}