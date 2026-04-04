package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Turno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametrosCursoModelo {
    @ElementCollection
    @CollectionTable(
            name = "curso_turnos",
            joinColumns = @JoinColumn(name = "curso_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false)
    private Set<Turno> turnos;

    @ElementCollection
    @CollectionTable(
            name = "curso_disciplinas",
            joinColumns = @JoinColumn(name = "curso_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "disciplina", nullable = false)
    private Set<Disciplina> disciplinas;

    @ElementCollection
    @CollectionTable(
            name = "curso_ajustes_tipo_tg",
            joinColumns = @JoinColumn(name = "curso_id")
    )
    private Set<AjusteTipoTgModelo> ajusteTipoTg;
}