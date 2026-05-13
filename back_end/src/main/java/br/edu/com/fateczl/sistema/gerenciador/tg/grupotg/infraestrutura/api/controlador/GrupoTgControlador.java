package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.BuscarVisaoGruposProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.GerarGrupoTgCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.VincularCoorientadorExternoCaso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gruposTg")
public class GrupoTgControlador {

    private final BuscarVisaoGruposProfessorCaso buscarVisaoGruposProfessorCaso;
    private final GerarGrupoTgCaso gerarGrupoTgCaso;
    private final GeradorToken geradorToken;
    private final VincularCoorientadorExternoCaso vincularCoorientadorExternoCaso;

    public GrupoTgControlador(
            BuscarVisaoGruposProfessorCaso buscarVisaoGruposProfessorCaso,
            GerarGrupoTgCaso gerarGrupoTgCaso,
            GeradorToken geradorToken,
            VincularCoorientadorExternoCaso vincularCoorientadorExternoCaso
    ) {
        this.buscarVisaoGruposProfessorCaso = buscarVisaoGruposProfessorCaso;
        this.gerarGrupoTgCaso = gerarGrupoTgCaso;
        this.geradorToken = geradorToken;
        this.vincularCoorientadorExternoCaso = vincularCoorientadorExternoCaso;
    }

    /**
     * Rota para buscar grupos tg por lista de turmasId
     * @param headerAutorizacao - Cabeçalho de autorização para extrair token jwt
     * @return (ResponseEntity) - 200 com DTO de gruposTG
     */
    @GetMapping("/visao-gruposTg")
    public ResponseEntity<BuscarVisaoGruposProfessorCaso.Resposta> buscarGruposPorTurmas(
            @RequestHeader("Authorization") String headerAutorizacao,
            @RequestParam("pagina") Integer pagina,
            @RequestParam("tamanho") Integer tamanho,
            @RequestParam("somenteSemGrupo") Boolean somenteSemGrupo
    ) {
        // Limpa o token
        String token = headerAutorizacao.replace("Bearer ", "");

        // Usa metodo da implementação do jwt para pegar o email (topico) do usuario logado
        String emailUsuarioLogado = geradorToken.extrairTopico(token);

        // Monta o comando e executa o caso de uso
        var comando = new BuscarVisaoGruposProfessorCaso.
                Comando(emailUsuarioLogado, somenteSemGrupo, pagina, tamanho);
        var resposta = buscarVisaoGruposProfessorCaso.executar(comando);

        // 4. Devolve o HTTP 200 OK com o JSON pronto
        return ResponseEntity.ok(resposta);
    }

    /**
     * Rota para criar o grupoTG
     * @param requisicao DTO recebe: disciplinas, tema, descricaoTema, tipoTg e lista de alunos integrantes
     * @param headerAutorizacao string do header de auth que contem o token jwt
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> gerarGrupo(
            @RequestBody GerarGrupoRequisicao requisicao,
            // Pega o token direto do cabeçalho HTTP
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Tira o prefixo "Bearer " do token
        String token = headerAutorizacao.replace("Bearer ", "");

        // Extrai o ID usando a sua ferramenta
        String idContaUsuarioLogada = geradorToken.extrairId(token);

        var comando = new GerarGrupoTgCaso.Comando(
                idContaUsuarioLogada,
                requisicao.tema(),
                requisicao.descricaoTema(),
                requisicao.tipoTg(),
                requisicao.matriculasAlunos()
        );

        gerarGrupoTgCaso.executar(comando);

        // Retorna 201 Created com corpo vazio
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ROTA: VINCULAR COORIENTADOR EXTERNO
    // ========================================================================
    @PatchMapping("/{idGrupo}/coorientadores-externos")
    public ResponseEntity<Void> vincularCoorientadorExterno(
            @PathVariable String idGrupo,
            @RequestBody VincularCoorientadorRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Extrai quem é o aluno logado
        String token = headerAutorizacao.replace("Bearer ", "");
        String idContaAlunoLogado = geradorToken.extrairId(token);

        // Monta o Comando
        var comando = new VincularCoorientadorExternoCaso.Comando(
                idContaAlunoLogado,
                idGrupo,
                requisicao.nome(),
                requisicao.origem()
        );

        // Executa a regra de negócio
        vincularCoorientadorExternoCaso.executar(comando);

        // Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs =====
    public record GerarGrupoRequisicao(
            String tema,
            String descricaoTema,
            TipoTg tipoTg,
            List<String> matriculasAlunos
    ){}

    public record VincularCoorientadorRequisicao(
            String nome,
            String origem
    ) {}

}
