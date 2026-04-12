package br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.RemetenteEmail;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RemetenteEmailImpl implements RemetenteEmail {
    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(
            RemetenteEmailImpl.class
    );

    @Value("${spring.mail.username}")
    private String remetenteOficial;

    public RemetenteEmailImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void enviarEmailTexto(
            Email destinatario,
            String assunto,
            String mensagem
    ) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setFrom(remetenteOficial);
            helper.setTo(destinatario.valor());
            helper.setSubject(assunto);
            helper.setText(mensagem, true);

            mailSender.send(message);
            log.info(
                    "Email [{}] enviado com sucesso para: {}",
                    assunto,
                    destinatario.valor()
            );
        } catch(MessagingException e) {
            log.error(
                    "Falha crítica ao enviar email para {}: {}",
                    destinatario.valor(),
                    e.getMessage()
            );
        }
    }
}