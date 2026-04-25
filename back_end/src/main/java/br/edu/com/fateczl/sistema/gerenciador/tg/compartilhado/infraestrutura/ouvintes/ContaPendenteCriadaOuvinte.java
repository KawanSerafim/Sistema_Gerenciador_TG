package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.ouvintes;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaPendenteCriadaEvento;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.casosdeuso.EnviarEmailConfirmacaoCaso;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ContaPendenteCriadaOuvinte {
    private static final Logger log = LoggerFactory.getLogger(ContaPendenteCriadaOuvinte.class);
    private final EnviarEmailConfirmacaoCaso enviarEmailCaso;

    // Injeção de dependência do Caso de Uso que você achou
    public ContaPendenteCriadaOuvinte(EnviarEmailConfirmacaoCaso enviarEmailCaso) {
        this.enviarEmailCaso = enviarEmailCaso;
    }

    // A mágica acontece nesta anotação!
    @EventListener
    public void lidarCom(ContaPendenteCriadaEvento evento) {
        log.info("Evento recebido! Iniciando envio de e-mail para: {}", evento.email());

        try {
            // Cria o comando exigido pelo Caso de Uso de E-mail
            var comando = new EnviarEmailConfirmacaoCaso.Comando(evento.email());

            // Executa o envio
            enviarEmailCaso.executar(comando);

            log.info("E-mail enviado com sucesso para: {}", evento.email());
        } catch (Exception e) {
            // É importante fazer um try/catch aqui para que, se o e-mail falhar,
            // a transação do banco de dados do cadastro não seja desfeita (rollback)
            log.error("Falha ao enviar e-mail de confirmação para {}: {}", evento.email(), e.getMessage());
        }
    }
}
