package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.infraestrutura.implementadores;

import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.portas.PublicadorEventos;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicadorEventosImpl implements PublicadorEventos {
    private final ApplicationEventPublisher publicador;

    @Override
    public void publicar(Object evento) {
        if(evento == null) {
            return;
        }

        publicador.publishEvent(evento);
    }
}