package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TipoCoorientador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "grupos_tg")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GrupoTgModelo {
    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(length = 36, name = "orientador_id", nullable = false)
    private String orientadorId;

    @Column(length = 36, name = "coorientador_id")
    private String coorientadorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, name = "tipo_coorientador")
    private TipoCoorientador tipoCoorientador;

    @Column(length = 36, name = "curso_id", nullable = false)
    private String cursoId;

    @ElementCollection
    @CollectionTable(
            name = "grupo_tg_disciplinas",
            joinColumns = @JoinColumn(name = "grupo_tg_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(length = 30, name = "disciplina", nullable = false)
    private Set<Disciplina> disciplinas;

    @Column(length = 150, name = "nome_tema_tg", nullable = false)
    private String nomeTemaTg;

    @Column(
            columnDefinition = "TEXT",
            name = "descricao_tema_tg",
            nullable = false
    )
    private String descricaoTemaTg;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, name = "tipo_tg", nullable = false)
    private TipoTg tipoTg;

    @ElementCollection
    @CollectionTable(
            name = "grupo_tg_alunos",
            joinColumns = @JoinColumn(name = "grupo_tg_id")
    )
    @Column(length = 36, nullable = false)
    private List<String> alunosIds;
}