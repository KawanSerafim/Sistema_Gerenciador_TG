package br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.ouvintes;

import br.edu.com.fateczl.sistema.gerenciador.tg.aluno.dominio.repositorio.AlunoRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.aplicacao.eventos.ContaAtividadeEvento;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AtivarAlunoOuvinte {
    private final AlunoRepositorio alunoRepositorio;

    public AtivarAlunoOuvinte(AlunoRepositorio alunoRepositorio) {
        this.alunoRepositorio = alunoRepositorio;
    }

    @EventListener
    public void processar(ContaAtividadeEvento evento) {
        alunoRepositorio.buscarPorContaId(evento.contaUsuarioId())
                .ifPresent(aluno -> {
                    aluno.concluirCadastro();
                    alunoRepositorio.salvar(aluno);
                });
    }
}