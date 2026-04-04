package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alunos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AlunoModelo {
    @Id
    @Column(updatable = false)
    private String id;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(name = "conta_usuario_id", nullable = true, unique = true)
    private String contaUsuarioId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "alunos_turmas",
            joinColumns = @JoinColumn(name = "aluno_id")
    )
    @Column(name = "turma_id", nullable = false)
    private Set<String> turmasIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAluno status;
}