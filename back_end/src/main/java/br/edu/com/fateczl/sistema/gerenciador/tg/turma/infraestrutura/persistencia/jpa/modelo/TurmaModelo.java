package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "turmas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TurmaModelo {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(name = "curso_id", nullable = false)
    private String cursoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Disciplina disciplina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Turno turno;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private Integer semestre;

    @Column(name = "professor_id", nullable = false)
    private String professorId;
}