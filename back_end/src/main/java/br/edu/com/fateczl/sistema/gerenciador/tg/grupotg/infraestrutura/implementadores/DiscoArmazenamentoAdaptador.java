package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.portas.ArmazenamentoArquivoPorta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class DiscoArmazenamentoAdaptador implements ArmazenamentoArquivoPorta {
    // Lê o caminho do application.properties (ex: file.upload-dir=/var/uploads/tg)
    @Value("${file.upload-dir}")
    private String diretorioUpload;

    @Override
    public String salvarArquivo(String nomeOriginal, byte[] conteudoArquivo) {
        try {
            // Cria a pasta se não existir
            Path caminhoDiretorio = Paths.get(diretorioUpload);
            if (!Files.exists(caminhoDiretorio)) {
                Files.createDirectories(caminhoDiretorio);
            }

            // Gera um nome único para não dar conflito (ex: 3eeb34-monografia.pdf)
            String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            String nomeUnico = UUID.randomUUID() + extensao;

            Path caminhoArquivo = caminhoDiretorio.resolve(nomeUnico);

            // Salva fisicamente no disco
            Files.write(caminhoArquivo, conteudoArquivo);

            return nomeUnico; // Retorna só o nome salvo

        } catch (Exception e) {
            throw new RuntimeException("Falha ao salvar arquivo no disco", e);
        }
    }
}
