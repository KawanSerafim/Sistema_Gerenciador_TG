package br.edu.com.fateczl.sistema.gerenciador.tg.turma.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.StatusTurma;
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
    @Column(length = 36, updatable = false)
    private String id;

    @Column(length = 36, name = "curso_id", nullable = false)
    private String cursoId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Disciplina disciplina;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Turno turno;

    @Column(columnDefinition = "SMALLINT", nullable = false)
    private Integer ano;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private Integer semestre;

    @Column(length = 36, name = "professor_id", nullable = false)
    private String professorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false, name = "status_turma")
    // Valor padrão ao criar uma turma nova
    private StatusTurma statusTurma = StatusTurma.ATIVA;
}