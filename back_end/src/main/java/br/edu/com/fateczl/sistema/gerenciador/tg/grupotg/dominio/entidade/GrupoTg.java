package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.*;

public class GrupoTg {
    private final GrupoTgId id;
    private ProfessorId orientadorId;
    private String coorientadorIdTexto;
    private final CursoId cursoId;
    private Set<Disciplina> disciplinas;
    private TemaTg temaTg;
    private TipoTg tipoTg;
    private List<AlunoId> alunosIds;

    private GrupoTg(GrupoTgId id, ProfessorId orientadorId,
                    String coorientadorIdTexto, CursoId cursoId,
                    Set<Disciplina> disciplinas, TemaTg temaTg, TipoTg tipoTg,
                    List<AlunoId> alunosIds) {
        this.id = assegurarPresenca(id, "ID");
        this.orientadorId = orientadorId;
        this.coorientadorIdTexto = coorientadorIdTexto;
        this.cursoId = assegurarPresenca(cursoId, "ID do curso");
        this.disciplinas = assegurarPresencaDisciplinas(disciplinas);
        this.temaTg = assegurarPresenca(temaTg, "tema de TG");
        this.tipoTg = assegurarPresenca(tipoTg, "tipo de TG");
        this.alunosIds = new ArrayList<>((assegurarPresencaAlunos(alunosIds)));
    }

    // Métodos Factory ---------------------------------------------------------

    public static GrupoTg novo(GrupoTgId id, CursoId cursoId,
                               Set<Disciplina> disciplinas,
                               TemaTg temaTg, TipoTg tipoTg,
                               List<AlunoId> alunosIds) {
        return new GrupoTg(id, null, null, cursoId, disciplinas, temaTg, tipoTg,
                alunosIds);
    }

    public static GrupoTg carregar(GrupoTgId id, ProfessorId orientadorId,
                                   String coorientadorIdTexto, CursoId cursoId,
                                   Set<Disciplina> disciplinas, TemaTg temaTg,
                                   TipoTg tipoTg, List<AlunoId> alunosIds) {
        return new GrupoTg(id, orientadorId, coorientadorIdTexto, cursoId,
                disciplinas, temaTg, tipoTg, alunosIds);
    }

    // Métodos especiais -------------------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo);
        }
        return objeto;
    }

    private Set<Disciplina> assegurarPresencaDisciplinas(
            Set<Disciplina> disciplinas) {
        if(disciplinas == null || disciplinas.isEmpty()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "disciplinas");
        }
        return EnumSet.copyOf(disciplinas);
    }

    private List<AlunoId> assegurarPresencaAlunos(List<AlunoId> alunosIds) {
        if(alunosIds == null || alunosIds.isEmpty()) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "alunos");
        }
        return alunosIds;
    }

    public void vincularOrientador(ProfessorId professorId) {
        this.orientadorId = assegurarPresenca(professorId, "ID do orientador");
    }

    public void vincularCoorientador(String coorientadorIdTexto) {
        this.coorientadorIdTexto = assegurarPresenca(coorientadorIdTexto,
                "ID do coorientador");
    }

    // Métodos de Atualização --------------------------------------------------

    public void atualizarDisciplina(Set<Disciplina> novasDisciplinas) {
        this.disciplinas = assegurarPresencaDisciplinas(novasDisciplinas);
    }

    public void atualizarTemaTg(TemaTg novoTemaTg) {
        this.temaTg = assegurarPresenca(novoTemaTg, "tema de TG");
    }

    public void atualizarTipoTg(TipoTg novoTipoTg) {
        this.tipoTg = assegurarPresenca(novoTipoTg, "tipo de TG");
    }

    public void atualizarAlunos(List<AlunoId> novosAlunosIds) {
        this.alunosIds = new ArrayList<>(
                assegurarPresencaAlunos(novosAlunosIds));
    }

    // Métodos Getters ---------------------------------------------------------

    public GrupoTgId id() { return id; }
    public String idTexto() { return id.valor().toString(); }
    public ProfessorId orientadorId() { return orientadorId; }
    public String coorientadorIdTexto() { return coorientadorIdTexto; }
    public CursoId cursoId() { return cursoId; }
    public Set<Disciplina> disciplinas() {
        return Collections.unmodifiableSet(disciplinas);
    }
    public TemaTg temaTg() { return temaTg; }
    public TipoTg tipoTg() { return tipoTg; }
    public List<AlunoId> alunosIds() {
        return Collections.unmodifiableList(alunosIds);
    }
}