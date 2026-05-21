package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.entidade.Aluno;
import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.GenericaExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.ContaUsuarioId;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.entidade.GrupoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.dominio.repositorio.GrupoTgRepositorio;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class EnviarTrabalhoGraduacaoCaso {
    private final GrupoTgRepositorio grupoTgRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final ArmazenamentoArquivoPorta armazenamentoPorta;

    public EnviarTrabalhoGraduacaoCaso(
            GrupoTgRepositorio grupoTgRepositorio,
            AlunoRepositorio alunoRepositorio,
            ArmazenamentoArquivoPorta armazenamentoPorta
    ) {
        this.grupoTgRepositorio = grupoTgRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.armazenamentoPorta = armazenamentoPorta;
    }

    public record Comando(
            String idAlunoLogado,
            String nomeArquivoOriginal,
            byte[] conteudoArquivo // Arquivo em memória
    ) {
        // Sobrescreve o equals para checar os bytes do arquivo um por um
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Comando comando = (Comando) o;

            return Objects.equals(idAlunoLogado, comando.idAlunoLogado) &&
                    Objects.equals(nomeArquivoOriginal, comando.nomeArquivoOriginal) &&
                    Arrays.equals(conteudoArquivo, comando.conteudoArquivo);
        }

        // Sobrescreve o hashCode para usar os bytes do arquivo no cálculo
        @Override
        public int hashCode() {
            int result = Objects.hash(idAlunoLogado, nomeArquivoOriginal);
            result = 31 * result + Arrays.hashCode(conteudoArquivo);
            return result;
        }

        // Sobrescreve o toString para não imprimir milhões de bytes no seu console por acidente!
        @Override
        public String toString() {
            return "Comando{" +
                    "idAlunoLogado='" + idAlunoLogado + '\'' +
                    ", nomeArquivoOriginal='" + nomeArquivoOriginal + '\'' +
                    ", conteudoArquivo=[Tamanho: " +
                    (conteudoArquivo != null ? conteudoArquivo.length : 0) + " bytes]" +
                    '}';
        }
    }

    public void executar(Comando comando) {
        // Acha o aluno
        Aluno aluno = alunoRepositorio.buscarPorContaId(new ContaUsuarioId(UUID.fromString(comando.idAlunoLogado())))
                .orElseThrow(() -> new GenericaExcecao(CodigoErro.GN_001_REGISTRO_NAO_ENCONTRADO, "aluno"));

        // Valida se o formato é aceito
        if (!comando.nomeArquivoOriginal().matches(".*\\.(pdf|doc|docx|odt)$")) {
            throw new RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                    "arquivo de trabalho graduacao",
                    "Estar em um dos formatos validos: 'pdf, doc, docx, odt'.");
        }
        // Acha o grupo do aluno
        GrupoTg grupo = grupoTgRepositorio.buscarPorAlunoId(aluno.id())
                .orElseThrow(() -> new RegraNegocioExcecao(CodigoErro.RN_001_ESTADO_INVALIDO_PARA_ACAO,
                        "aluno","pertencer a um grupo"));

        // Salva no disco
        String caminhoSalvo = armazenamentoPorta.salvarArquivo(
                comando.nomeArquivoOriginal(),
                comando.conteudoArquivo()
        );

        // Atualiza a entidade de negócio
        grupo.vincularArquivoTrabalhoTg(caminhoSalvo);

        // Salva a entidade no banco de dados (que agora tem o caminho do arquivo)
        grupoTgRepositorio.salvar(grupo);
    }
}
