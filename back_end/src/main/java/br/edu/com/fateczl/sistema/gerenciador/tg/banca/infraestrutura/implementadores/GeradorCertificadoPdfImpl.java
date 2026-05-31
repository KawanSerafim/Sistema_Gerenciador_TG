package br.edu.com.fateczl.sistema.gerenciador.tg.banca.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.banca.aplicacao.portas.GeradorCertificadoPdf;
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
import java.util.List;

@Component
public class GeradorCertificadoPdfImpl implements GeradorCertificadoPdf {

    private final TemplateEngine templateEngine;

    private static final Logger log = LoggerFactory.getLogger(
            GeradorCertificadoPdfImpl.class
    );

    public GeradorCertificadoPdfImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] gerar(String tema, String alunosStr, String curso, String dataDefesa,
                        List<MembroCertificado> membros, String dataEmissao,
                        String nomeDiretor, String base64AssinaturaDiretor) {
        try {
            Context context = new Context();
            context.setVariable("tema", tema);
            context.setVariable("alunos", alunosStr);
            context.setVariable("curso", curso);
            context.setVariable("dataDefesa", dataDefesa);
            context.setVariable("membros", membros);
            context.setVariable("dataEmissao", dataEmissao);
            context.setVariable("nomeDiretor", nomeDiretor);
            context.setVariable("assinaturaDiretor", base64AssinaturaDiretor); // Assinatura dinâmica do BD

            // Logos fixos nos assets
            //LOGO DA UNIDADE FATEC
            context.setVariable("logoFatec",
                    carregarImagemBase64("templates/assets/logo_fatec.png"));
            //LOGO DO ESTADO
            context.setVariable("logoSp",
                    carregarImagemBase64("templates/assets/logo_sp.png"));

            String htmlProcessado = templateEngine.process("certificado_banca", context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlProcessado, null);
            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Certificado", e);
        }
    }

    private String carregarImagemBase64(String caminhoRelativoClassPath) {
        try {
            ClassPathResource resource = new ClassPathResource(caminhoRelativoClassPath);
            byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            log.error("Aviso: Não foi possível carregar a imagem: :{} - erro: :{}",
                    caminhoRelativoClassPath,
                    e.getMessage());
            return "";
        }
    }
}
