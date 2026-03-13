package br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor;

public enum CargoProfessor {
    ORIENTADOR,
    PROFESSOR_TG,
    COORDENADOR_CURSO;

    public boolean podeSerOrientador() {
        return true;
    }

    public boolean podeSerProfessorTg() {
        return this == CargoProfessor.PROFESSOR_TG;
    }

    public boolean podeSerCoordenadorCurso() {
        return this == CargoProfessor.COORDENADOR_CURSO;
    }
}