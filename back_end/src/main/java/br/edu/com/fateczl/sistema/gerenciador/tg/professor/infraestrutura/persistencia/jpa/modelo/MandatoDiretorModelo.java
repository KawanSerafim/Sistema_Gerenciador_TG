package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.persistencia.jpa.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "mandatos_diretor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MandatoDiretorModelo {
    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "professor_id", length = 36, nullable = false)
    private String professorId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    // Usado para textos gigantes no banco (A imagem em Base64)
    @Lob
    @Column(name = "assinatura_base64", columnDefinition = "LONGTEXT")
    private String assinaturaBase64;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
