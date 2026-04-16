package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.persistencia.jpa.modelo;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.StatusContaUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contas_usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ContaUsuarioModelo {
    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusContaUsuario status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            // Nome da nova tabela que o Hibernate vai criar
            name = "contas_usuario_autoridades",
            // Chave estrangeira
            joinColumns = @JoinColumn(name = "conta_usuario_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "autoridade", nullable = false)
    private Set<Autoridade> autoridades = new HashSet<>();
}