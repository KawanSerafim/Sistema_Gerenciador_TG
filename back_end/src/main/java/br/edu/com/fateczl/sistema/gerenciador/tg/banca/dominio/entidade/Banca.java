package br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.MembroExterno;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.ValidacaoExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.objetosvalor.GrupoTgId;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.ProfessorId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Banca {
    private final BancaId id;
    private final GrupoTgId grupoId;
    private LocalDateTime dataHora;
    private String local;
    private List<ProfessorId> avaliadoresInternos;
    private List<MembroExterno> avaliadoresExternos;

    private Banca(
            BancaId id,
            GrupoTgId grupoId,
            LocalDateTime dataHora,
            String local,
            List<ProfessorId> avaliadoresInternos,
            List<MembroExterno> avaliadoresExternos
    ) {
        this.id = assegurarPresenca(id, "ID da banca");
        this.grupoId = assegurarPresenca(grupoId, "ID do grupo");
        this.dataHora = assegurarPresenca(dataHora, "data e hora");
        this.local = assegurarPresenca(local, "local");

        // Validação de regra de negócio: a banca precisa de membros
        validarComposicaoMinima(avaliadoresInternos, avaliadoresExternos);

        this.avaliadoresInternos = new ArrayList<>(avaliadoresInternos != null ? avaliadoresInternos : List.of());
        this.avaliadoresExternos = new ArrayList<>(avaliadoresExternos != null ? avaliadoresExternos : List.of());
    }

    // MÉTODOS FACTORY ---------------------------------------------------------

    public static Banca novo(
            BancaId id,
            GrupoTgId grupoId,
            LocalDateTime dataHora,
            String local,
            List<ProfessorId> avaliadoresInternos,
            List<MembroExterno> avaliadoresExternos
    ) {
        return new Banca(id, grupoId, dataHora, local, avaliadoresInternos, avaliadoresExternos);
    }

    public static Banca carregar(
            BancaId id,
            GrupoTgId grupoId,
            LocalDateTime dataHora,
            String local,
            List<ProfessorId> avaliadoresInternos,
            List<MembroExterno> avaliadoresExternos
    ) {
        return new Banca(id, grupoId, dataHora, local, avaliadoresInternos, avaliadoresExternos);
    }

    // MÉTODOS PARA GARANTIR PRESENÇA E VALIDAÇÃO ------------------------------

    private <T> T assegurarPresenca(T objeto, String campo) {
        if (objeto == null || (objeto instanceof String s && s.isBlank())) {
            throw new ValidacaoExcecao(CodigoErro.VD_001_CAMPO_OBRIGATORIO, campo);
        }
        return objeto;
    }

    private void validarComposicaoMinima(List<ProfessorId> internos, List<MembroExterno> externos) {
        boolean temInterno = internos != null && !internos.isEmpty();
        boolean temExterno = externos != null && !externos.isEmpty();

        //Se não tem interno e nem externo não tem nenhum avaliador
        if (!temInterno && !temExterno) {
            throw new RegraNegocioExcecao(
                    CodigoErro.GN_002_QUANTIDADE_INFERIOR,
                    "composição da banca", 1
            );
        }
    }

    // MÉTODOS DE ATUALIZAÇÃO --------------------------------------------------

    public void alterarAgendamento(LocalDateTime novaDataHora, String novoLocal) {
        this.dataHora = assegurarPresenca(novaDataHora, "nova data e hora");
        this.local = assegurarPresenca(novoLocal, "novo local");
    }

    public void atualizarAvaliadores(List<ProfessorId> novosInternos, List<MembroExterno> novosExternos) {
        validarComposicaoMinima(novosInternos, novosExternos);
        this.avaliadoresInternos = new ArrayList<>(novosInternos != null ? novosInternos : List.of());
        this.avaliadoresExternos = new ArrayList<>(novosExternos != null ? novosExternos : List.of());
    }

    // MÉTODOS GETTERS DE DELEGAÇÃO --------------------------------------------

    public String idTexto() { return id.texto(); }
    public String grupoIdTexto() { return grupoId.texto(); }

    // MÉTODOS GETTERS ---------------------------------------------------------

    public BancaId id() { return id; }
    public GrupoTgId grupoId() { return grupoId; }
    public LocalDateTime dataHora() { return dataHora; }
    public String local() { return local; }

    public List<ProfessorId> avaliadoresInternos() {
        return Collections.unmodifiableList(avaliadoresInternos);
    }

    public List<MembroExterno> avaliadoresExternos() {
        return Collections.unmodifiableList(avaliadoresExternos);
    }
}
