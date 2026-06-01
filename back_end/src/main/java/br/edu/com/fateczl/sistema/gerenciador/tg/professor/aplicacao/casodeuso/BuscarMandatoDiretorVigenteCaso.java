package br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso;

import br.edu.com.fateczl.sistema.gerenciador.tg.administrador.dominio.repositorio.AdministradorRepositorio;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.CodigoErro;
import br.edu.com.fateczl.sistema.gerenciador.tg.compartilhado.dominio.excecoes.RegraNegocioExcecao;
import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.dominio.objetosvalor.Email;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.repositorio.MandatoDiretorRepositorio;

import java.time.LocalDate;
import java.util.Optional;

public class BuscarMandatoDiretorVigenteCaso {
    private final MandatoDiretorRepositorio repositorio;
    private final AdministradorRepositorio administradorRepositorio;
    public BuscarMandatoDiretorVigenteCaso(
            MandatoDiretorRepositorio repositorio,
            AdministradorRepositorio administradorRepositorio) {
        this.repositorio = repositorio;
        this.administradorRepositorio = administradorRepositorio;
    }

    public record Comando(String emailAdministradorLogado){}

    public record Resposta(
            String id,
            String professorId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String assinaturaBase64
    ){}
    //Retorna Optional para caso não tenha mandato vigente
    public Optional<Resposta> executar(Comando comando){
        //Verifica se usuario logado é administrador
        administradorRepositorio.buscarPorEmail
                        (new Email(comando.emailAdministradorLogado()))
                .orElseThrow(() -> new RegraNegocioExcecao(CodigoErro.AU_003_ACAO_NAO_PERMITIDA_MOTIVO,
                        "atribuir mandato diretor a professor",
                        "usuário logado não tem permissão de administrador"));

        return repositorio.buscarMandatoVigente()
                .map(mandatoDiretor ->
                    new Resposta(
                        mandatoDiretor.idTexto(),
                        mandatoDiretor.professorIdTexto(),
                        mandatoDiretor.dataInicio(),
                        mandatoDiretor.dataFim(),
                        mandatoDiretor.assinaturaBase64()
                    )
                );
    }
}
