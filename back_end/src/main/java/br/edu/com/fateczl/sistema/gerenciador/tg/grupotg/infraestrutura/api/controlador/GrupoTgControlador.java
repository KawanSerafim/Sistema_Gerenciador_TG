package br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.curso.dominio.objetosvalor.TipoTg;
import br.edu.com.fateczl.sistema.gerenciador.tg.grupotg.aplicacao.casosdeuso.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/gruposTg")
public class GrupoTgControlador {

    private final BuscarVisaoGruposProfessorCaso buscarVisaoGruposProfessorCaso;
    private final GerarGrupoTgCaso gerarGrupoTgCaso;
    private final GeradorToken geradorToken;
    private final VincularCoorientadorExternoCaso vincularCoorientadorExternoCaso;
    private final BuscarGruposOrientadosCaso buscarGruposOrientadosCaso;
    private final BuscarGrupoAlunoCaso buscarGrupoAlunoCaso;
    private final EnviarTrabalhoGraduacaoCaso enviarTrabalhoGraduacaoCaso;
    private final BaixarTrabalhoBancaCaso baixarTrabalhoBancaCaso;

    public GrupoTgControlador(
            BuscarVisaoGruposProfessorCaso buscarVisaoGruposProfessorCaso,
            GerarGrupoTgCaso gerarGrupoTgCaso,
            GeradorToken geradorToken,
            VincularCoorientadorExternoCaso vincularCoorientadorExternoCaso,
            BuscarGruposOrientadosCaso buscarGruposOrientadosCaso,
            BuscarGrupoAlunoCaso buscarGrupoAlunoCaso,
            EnviarTrabalhoGraduacaoCaso enviarTrabalhoGraduacaoCaso,
            BaixarTrabalhoBancaCaso baixarTrabalhoBancaCaso
    ) {
        this.buscarVisaoGruposProfessorCaso = buscarVisaoGruposProfessorCaso;
        this.gerarGrupoTgCaso = gerarGrupoTgCaso;
        this.geradorToken = geradorToken;
        this.vincularCoorientadorExternoCaso = vincularCoorientadorExternoCaso;
        this.buscarGruposOrientadosCaso = buscarGruposOrientadosCaso;
        this.buscarGrupoAlunoCaso = buscarGrupoAlunoCaso;
        this.enviarTrabalhoGraduacaoCaso = enviarTrabalhoGraduacaoCaso;
        this.baixarTrabalhoBancaCaso = baixarTrabalhoBancaCaso;
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
    @PatchMapping("/coorientadores-externos")
    public ResponseEntity<Void> vincularCoorientadorExterno(
            @RequestBody VincularCoorientadorRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Extrai quem é o aluno logado
        String token = headerAutorizacao.replace("Bearer ", "");
        String idContaAlunoLogado = geradorToken.extrairId(token);

        // Monta o Comando
        var comando = new VincularCoorientadorExternoCaso.Comando(
                idContaAlunoLogado,
                requisicao.nome(),
                requisicao.origem()
        );

        // Executa a regra de negócio
        vincularCoorientadorExternoCaso.executar(comando);

        // Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grupos-orientados")
    public ResponseEntity<List<BuscarGruposOrientadosCaso.GrupoOrientadoDTO>> listarMeusGrupos(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer semestre,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");
        String emailLogado = geradorToken.extrairTopico(token);

        // Monta o comando repassando os parâmetros de filtro
        var comando = new BuscarGruposOrientadosCaso.Comando(emailLogado, ano, semestre);
        var resposta = buscarGruposOrientadosCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }

    /**
     * Busca informações do grupo do aluno logado
     * @param headerAutorizacao
     * @return
     */
    @GetMapping("/aluno")
    public ResponseEntity<BuscarGrupoAlunoCaso.MeuGrupoDetalhadoDTO> consultarMeuGrupo(
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");
        String idAlunoLogado = geradorToken.extrairId(token);

        var comando = new BuscarGrupoAlunoCaso.Comando(idAlunoLogado);
        var resposta = buscarGrupoAlunoCaso.executar(comando);

        return ResponseEntity.ok(resposta);
    }
    // =========== Envio de trabalho de graduacao =========//
    
    @PostMapping(value = "/trabalho", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarTrabalho(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String headerAutorizacao
    ) throws IOException {
        String token = headerAutorizacao.replace("Bearer ", "");
        String idAlunoLogado = geradorToken.extrairId(token);

        // Pega os bytes da requisição
        var comando = new EnviarTrabalhoGraduacaoCaso.Comando(
                idAlunoLogado,
                file.getOriginalFilename(),
                file.getBytes()
        );

        enviarTrabalhoGraduacaoCaso.executar(comando);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idBanca}/trabalho")
    public ResponseEntity<byte[]> baixarTrabalhoDaBanca(
            @PathVariable String idBanca,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        String token = headerAutorizacao.replace("Bearer ", "");
        String emailProfessor = geradorToken.extrairTopico(token);

        var comando = new BaixarTrabalhoBancaCaso.Comando(emailProfessor, idBanca);
        var saida = baixarTrabalhoBancaCaso.executar(comando);

        // Diz ao navegador: "Isto é um anexo, faça o download com este nome!"
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + saida.nomeArquivo() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(saida.conteudo());
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
