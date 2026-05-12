package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.persistencia.jpa.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bancas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BancaModelo {
    @Id
    private String id; // UUID em String

    @Column(name = "grupo_id", nullable = false)
    private String grupoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String local;

    // tabela auxiliar "banca_avaliadores_internos" com a coluna do ID do professor
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "banca_avaliadores_internos", joinColumns = @JoinColumn(name = "banca_id"))
    @Column(name = "professor_id")
    private List<String> avaliadoresInternosIds;

    // tabela auxiliar "banca_avaliadores_externos" com as colunas (nome, email, telefone)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "banca_avaliadores_externos", joinColumns = @JoinColumn(name = "banca_id"))
    private List<MembroExternoModelo> avaliadoresExternos;

}
