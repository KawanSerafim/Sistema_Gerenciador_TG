package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.servicos;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Autoridade;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;

import java.util.Set;

public class IdentificadorAutoridadesProfessor {
    public Set<Autoridade> identificar(CargoProfessor cargo) {
        return switch(cargo) {
            case COORDENADOR_CURSO -> Set.of(
                    Autoridade.ROLE_COORDENADOR_CURSO,
                    // TODO: Verificar se coordenador tbm vai ter acesso a role de prof tg Autoridade.ROLE_PROFESSOR_TG,
                    Autoridade.ROLE_ORIENTADOR
            );
            case PROFESSOR_TG -> Set.of(
                    Autoridade.ROLE_PROFESSOR_TG,
                    Autoridade.ROLE_ORIENTADOR
            );
            default -> Set.of(Autoridade.ROLE_ORIENTADOR);
        };
    }
}