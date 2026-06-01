package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.ouvintes.NotificarMembrosBancaOuvinte;
import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas.GeradorAtaBancaPdf;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class GeradorAtaBancaPdfImpl implements GeradorAtaBancaPdf {
    private final TemplateEngine templateEngine;

    public GeradorAtaBancaPdfImpl(TemplateEngine templateEngine){
        this.templateEngine = templateEngine;
    }

    private static final Logger log = LoggerFactory.getLogger(
            GeradorAtaBancaPdfImpl.class
    );

    @Override
    public byte[] gerar(DadosAta dadosAta) {
        try {
            // Injeta os dados no modelo
            Context context = new Context();
            context.setVariable("tema", dadosAta.tema());
            context.setVariable("curso", dadosAta.curso());
            context.setVariable("dia", dadosAta.dia());
            context.setVariable("mes", dadosAta.mes());
            context.setVariable("ano", dadosAta.ano());
            context.setVariable("alunos", dadosAta.alunos());
            context.setVariable("membrosBanca", dadosAta.membrosBanca());

            // Injeta o logo do centro paula souza em formato Base64
            context.setVariable("logoCps", carregarImagemBase64("templates/assets/logo_cps.png"));

            // Processa o HTML
            String htmlProcessado = templateEngine.process("ata_banca", context);

            // Converte para PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlProcessado, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            //Dado a especificidade do erro lança uma exceção runtime com o stack trace
            throw new RuntimeException("Erro ao gerar o PDF da Ata da Banca", e);
        }
    }
    private String carregarImagemBase64(String caminhoRelativoClassPath) {
        try {
            ClassPathResource resource = new ClassPathResource(caminhoRelativoClassPath);
            byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            log.error("Aviso: Não foi possível carregar a imagem do logo. Gerando ata sem logo. :{}", e.getMessage());
            return "";
        }
    }
}
