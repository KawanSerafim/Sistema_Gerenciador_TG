package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Disciplina;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.CursoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TemaTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.TipoCoorientador;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.util.*;

public class GrupoTg {
    private final GrupoTgId id;
    private ProfessorId orientadorId;
    private String coorientadorIdTexto;
    private TipoCoorientador tipoCoorientador;
    private final CursoId cursoId;
    private Set<Disciplina> disciplinas;
    private TemaTg temaTg;
    private TipoTg tipoTg;
    private List<AlunoId> alunosIds;

    private GrupoTg(
            GrupoTgId id,
            ProfessorId orientadorId,
            String coorientadorIdTexto,
            TipoCoorientador tipoCoorientador,
            CursoId cursoId,
            Set<Disciplina> disciplinas,
            TemaTg temaTg,
            TipoTg tipoTg,
            List<AlunoId> alunosIds
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.orientadorId = orientadorId;
        this.coorientadorIdTexto = coorientadorIdTexto;
        this.tipoCoorientador = tipoCoorientador;
        this.cursoId = assegurarPresenca(cursoId, "ID do curso");
        this.disciplinas = assegurarPresencaDisciplinas(disciplinas);
        this.temaTg = assegurarPresenca(temaTg, "tema de TG");
        this.tipoTg = assegurarPresenca(tipoTg, "tipo de TG");
        this.alunosIds = new ArrayList<>((assegurarPresencaAlunos(alunosIds)));
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static GrupoTg novo(
            GrupoTgId id,
            CursoId cursoId,
            Set<Disciplina> disciplinas,
            TemaTg temaTg,
            TipoTg tipoTg,
            List<AlunoId> alunosIds
    ) {
        return new GrupoTg(
                id,
                null, null, null,
                cursoId,
                disciplinas,
                temaTg,
                tipoTg,
                alunosIds);
    }

    public static GrupoTg carregar(
            GrupoTgId id,
            ProfessorId orientadorId,
            String coorientadorIdTexto,
            TipoCoorientador tipoCoorientador,
            CursoId cursoId,
            Set<Disciplina> disciplinas,
            TemaTg temaTg,
            TipoTg tipoTg,
            List<AlunoId> alunosIds
    ) {
        return new GrupoTg(
                id,
                orientadorId,
                coorientadorIdTexto,
                tipoCoorientador,
                cursoId,
                disciplinas,
                temaTg,
                tipoTg,
                alunosIds
        );
    }

    // MÉTODOS PARA GARANTIR PRESENÇA ------------------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if(objeto == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    campo
            );
        }
        return objeto;
    }

    private Set<Disciplina> assegurarPresencaDisciplinas(
            Set<Disciplina> disciplinas
    ) {
        if(disciplinas == null || disciplinas.isEmpty()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "disciplinas"
            );
        }
        return EnumSet.copyOf(disciplinas);
    }

    private List<AlunoId> assegurarPresencaAlunos(List<AlunoId> alunosIds) {
        if(alunosIds == null || alunosIds.isEmpty()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "alunos"
            );
        }
        return alunosIds;
    }

    // MÉTODOS DE VINCULAÇÃO ---------------------------------------------------

    public void vincularOrientador(ProfessorId professorId) {
        this.orientadorId = assegurarPresenca(professorId, "ID do orientador");
    }

    public void vincularCoorientador(
            String novoCoorientadorIdTexto,
            TipoCoorientador tipo
    ) {

        // Não pode ter coorientador se não tiver orientador principal
        if (this.orientadorId == null) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo", "precisa ter um orientador principal antes do coorientador"
            );
        }

        // Não pode sobrescrever um coorientador que já existe
        if (this.coorientadorIdTexto != null && !this.coorientadorIdTexto.trim().isEmpty()) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "grupo", "já possui um coorientador vinculado"
            );
        }
            this.coorientadorIdTexto = assegurarPresenca(
                    novoCoorientadorIdTexto,
                    "ID do coorientador"
            );

            this.coorientadorIdTexto = assegurarPresenca(
                novoCoorientadorIdTexto,
                "ID do coorientador"
        );

        this.tipoCoorientador = assegurarPresenca(tipo, "tipo de coorientador");
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

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
                assegurarPresencaAlunos(novosAlunosIds)
        );
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String orientadorIdTexto() { return this.orientadorId != null ? this.orientadorId.texto() : null; }
    public String cursoIdTexto() { return cursoId.texto(); }
    public String nomeTemaTg() { return temaTg.nome(); }
    public String descricaoTemaTg() { return temaTg.descricao(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public GrupoTgId id() { return id; }
    public ProfessorId orientadorId() { return orientadorId; }
    public String coorientadorIdTexto() { return coorientadorIdTexto; }
    public TipoCoorientador tipoCoorientador() { return tipoCoorientador; }
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