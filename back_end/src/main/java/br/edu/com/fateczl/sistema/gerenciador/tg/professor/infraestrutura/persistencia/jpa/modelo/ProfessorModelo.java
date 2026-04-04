package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "professor")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProfessorModelo {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(name = "conta_usuario_id", nullable = true, unique = true)
    private String contaUsuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CargoProfessor cargo;
}