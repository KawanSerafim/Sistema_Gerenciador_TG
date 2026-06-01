package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.entidade.Banca;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.objetosvalor.BancaId;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.dominio.repositorio.BancaRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.entidade.Professor;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.ProfessorRepositorio;

import java.util.UUID;

public class BaixarTrabalhoBancaCaso {
    private final BancaRepositorio bancaRepositorio;
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final ArmazenamentoArquivoPorta armazenamentoPorta;

    public BaixarTrabalhoBancaCaso(
            BancaRepositorio bancaRepositorio,
            GrupoTgRepositorio grupoTgRepositorio,
            ProfessorRepositorio professorRepositorio,
            ArmazenamentoArquivoPorta armazenamentoPorta) {
        this.bancaRepositorio = bancaRepositorio;
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.armazenamentoPorta = armazenamentoPorta;
    }

    public record Comando(String emailProfessorLogado, String idBanca) {}

    // Usamos um Record de saída para devolver os bytes e o nome do arquivo juntos
    public record Saida(byte[] conteudo, String nomeArquivo) {}

    public Saida executar(Comando comando) {
        Professor professor = professorRepositorio.buscarPorEmail(new Email(comando.emailProfessorLogado()))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "Professor não encontrado"));

        Banca banca = bancaRepositorio.buscarPorId(new BancaId(UUID.fromString(comando.idBanca())))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "Banca não encontrada"));

        GrupoTg grupo = grupoTgRepositorio.buscarPorIdGrupo(banca.grupoId())
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO,
                        "Grupo não encontrado"));

        // VALIDAÇÃO DE SEGURANÇA: Ele é o orientador ou avaliador interno?
        boolean ehOrientador = grupo.orientadorId().equals(professor.id());
        boolean ehAvaliador = banca.avaliadoresInternos().contains(professor.id());

        if (!ehOrientador && !ehAvaliador) {
            throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,"Baixar trablho tg",
                    "Acesso negado. Apenas membros desta banca podem baixar o trabalho.");
        }

        // Verifica se o arquivo já foi enviado
        if (grupo.caminhoArquivoTrabalho() == null) {
            throw new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                    "Baixar trabalho tg",
                    "O grupo ainda não enviou o trabalho final.");
        }

        // Lê os bytes
        byte[] bytes = armazenamentoPorta.recuperarArquivo(grupo.caminhoArquivoTrabalho());

        // Monta um nome amigável para o professor baixar
        String extensao = grupo.caminhoArquivoTrabalho().substring(grupo.caminhoArquivoTrabalho().lastIndexOf("."));
        String nomeAmigavel = grupo.nomeTemaTg() + extensao;

        return new Saida(bytes, nomeAmigavel);
    }
}
