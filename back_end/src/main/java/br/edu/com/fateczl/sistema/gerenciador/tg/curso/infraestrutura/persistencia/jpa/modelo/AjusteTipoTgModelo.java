package br.edu.com.fateczl.sistema.gerenciador.tg.curso.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AjusteTipoTgModelo {
    @Enumerated(EnumType.STRING)
    @Column(length = 30, name = "tipo_tg", nullable = false)
    private TipoTg tipoTg;

    @Column(
            columnDefinition = "TINYINT",
            name = "max_alunos_grupo",
            nullable = false
    )
    private Integer maxAlunosGrupo;
}