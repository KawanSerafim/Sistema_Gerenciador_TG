package br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.solicitacaoorientacao.dominio.objetosvalor.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_orientacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoOrientacaoModelo {

    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(length = 36, name = "grupo_id", nullable = false)
    private String grupoId;

    @Column(length = 36, name = "professor_id", nullable = false)
    private String professorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "status_solicitacao", nullable = false)
    private StatusSolicitacao status;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
}
