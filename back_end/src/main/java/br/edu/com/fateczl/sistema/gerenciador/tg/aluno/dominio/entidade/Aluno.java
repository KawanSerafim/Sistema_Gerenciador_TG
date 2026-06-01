package br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.AlunoId;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.StatusAluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.objetosvalor.TipoRedeSocial;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Matricula;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.objetosvalor.Nome;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.turma.dominio.objetosvalor.TurmaId;

import java.util.*;

public class Aluno {
    private final AlunoId id;
    private final Nome nome;
    private final Matricula matricula;
    private ContaUsuarioId contaUsuarioId;
    private StatusAluno status;
    private List<TurmaId> turmasIds;
    //Redes Sociais é campo opcional
    private Map<TipoRedeSocial, String> redesSociais;

    private Aluno(
            AlunoId id,
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaUsuarioId,
            StatusAluno status,
            List<TurmaId> turmasIds,
            Map<TipoRedeSocial, String> redesSociais
    ) {
        this.id = assegurarPresenca(id, "ID");
        this.nome = assegurarPresenca(nome, "nome");
        this.matricula = assegurarPresenca(matricula, "matrícula");
        this.contaUsuarioId = contaUsuarioId;
        this.status = assegurarPresenca(status, "status");
        this.turmasIds = new ArrayList<>(
                assegurarPresenca(turmasIds, "IDs das turmas")
        );
        //Tratamento para campo opcional garante que nunca será null internamente
        this.redesSociais = redesSociais != null ?
                new HashMap<>(redesSociais) : new HashMap<>();
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static Aluno novo(
            AlunoId id,
            Nome nome,
            Matricula matricula,
            TurmaId turmaInicial
    ) {
        if(turmaInicial == null) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "turma inicial"
            );
        }

        return new Aluno(
                id,
                nome,
                matricula,
                null,
                StatusAluno.PRE_CADASTRO,
                List.of(turmaInicial),
                //Inicia vazio por padrão
                new HashMap<>()
        );
    }

    public static Aluno carregar(
            AlunoId id,
            Nome nome,
            Matricula matricula,
            ContaUsuarioId contaUsuarioId,
            StatusAluno status,
            List<TurmaId> turmasIds,
            // Recebe o mapa do banco
            Map<TipoRedeSocial, String> redesSociais
    ) {
        return new Aluno(
                id,
                nome,
                matricula,
                contaUsuarioId,
                status,
                turmasIds,
                redesSociais
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

    private List<TurmaId> assegurarPresencaTurmas(List<TurmaId> turmasIds) {
        if(turmasIds == null || turmasIds.isEmpty()) {
            throw new ValidacaoExcecao(
                    CodigoErro.VD_001_CAMPO_OBRIGATORIO,
                    "IDs das turmas"
            );
        }
        return turmasIds;
    }

    // MÉTODO DE VALIDAÇÃO -----------------------------------------------------

    public void validarSolicitacaoAcesso() {
        if(status != StatusAluno.PRE_CADASTRO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status do aluno", "PRE_CADASTRO"
            );
        }
    }

    // MÉTODOS ESPECIALIZADOS --------------------------------------------------

    public void matricularEmTurma(TurmaId novaTurmaId) {
        assegurarPresenca(novaTurmaId, "ID da turma");

        if(turmasIds.contains(novaTurmaId)) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_002_REGISTRO_DUPLICADO,
                    "ID da turma"
            );
        }
        this.turmasIds.add(novaTurmaId);
    }

    public void concluirCadastro() {
        if(status != StatusAluno.AGUARDANDO_CONFIRMACAO) {
            throw new RegraNegocioExcecao(
                    CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "status do aluno", "AGUARDANDO_CONFIRMACAO"
            );
        }
        this.status = StatusAluno.CADASTRADO;
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    public void vincularConta(ContaUsuarioId novaContaUsuarioId) {
        this.contaUsuarioId = assegurarPresenca(
                novaContaUsuarioId,
                "ID da conta de usuário"
        );

        if(status == StatusAluno.PRE_CADASTRO) {
            this.status = StatusAluno.AGUARDANDO_CONFIRMACAO;
        }
    }

    public void atualizarTurmas(List<TurmaId> novasTurmasIds) {
        this.turmasIds = new ArrayList<>(
                assegurarPresencaTurmas(novasTurmasIds)
        );
    }

    public void atualizarRedesSociais(Map<TipoRedeSocial, String> novasRedesSociais) {
        this.redesSociais = novasRedesSociais
                != null ? new HashMap<>(novasRedesSociais) : new HashMap<>();
    }

    public void adicionarRedeSocial(TipoRedeSocial tipo, String url) {
        assegurarPresenca(tipo, "tipo da rede social");
        assegurarPresenca(url, "URL da rede social");
        this.redesSociais.put(tipo, url);
    }

    public void removerRedeSocial(TipoRedeSocial tipo) {
        assegurarPresenca(tipo, "tipo da rede social");
        this.redesSociais.remove(tipo);
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String nomeTexto() { return nome.valor(); }
    public String matriculaTexto() { return matricula.valor(); }
    public String contaUsuarioIdTexto() { return contaUsuarioId.texto(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public AlunoId id() { return id; }
    public Nome nome() { return nome; }
    public Matricula matricula() { return matricula; }
    public ContaUsuarioId contaUsuarioId() { return contaUsuarioId; }
    public StatusAluno status() { return status; }
    public List<TurmaId> turmasIds() {
        return Collections.unmodifiableList(turmasIds);
    }
    public Map<TipoRedeSocial, String> redesSociais() { return Collections.unmodifiableMap(redesSociais);}
}