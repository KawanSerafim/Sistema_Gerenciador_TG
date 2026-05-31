package br.edu.com.fateczl.sistema.gerenciador.tg.professor.infraestrutura.api.controlador;

import br.edu.com.fateczl.sistema.gerenciador.tg.contausuario.aplicacao.portas.GeradorToken;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.CadastrarProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarProfessoresPorCargoCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.aplicacao.casodeuso.ListarCargosProfessorCaso;
import br.edu.com.fateczl.sistema.gerenciador.tg.professor.dominio.objetosvalor.CargoProfessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professores")
public class ProfessorControlador {

    private final ListarCargosProfessorCaso listarCargosProfessorCaso;
    private final ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso;
    private final CadastrarProfessorCaso cadastrarProfessorCaso;
    private final GeradorToken geradorToken;

    public ProfessorControlador(
            ListarCargosProfessorCaso listarCargosProfessorCaso,
            ListarProfessoresPorCargoCaso listarProfessoresPorCargoCaso,
            CadastrarProfessorCaso cadastrarProfessorCaso, GeradorToken geradorToken
    ){
            this.listarCargosProfessorCaso = listarCargosProfessorCaso;
            this.listarProfessoresPorCargoCaso = listarProfessoresPorCargoCaso;
            this.cadastrarProfessorCaso = cadastrarProfessorCaso;
            this.geradorToken = geradorToken;
    }

    @PostMapping
    public ResponseEntity<CadastrarProfessorCaso.Resposta> cadastrarProfessor(
            @RequestBody CadastrarProfessorRequisicao requisicao,
            @RequestHeader("Authorization") String headerAutorizacao
    ) {
        // Extrai a identidade de quem está fazendo a requisição
        String emailUsuarioLogado = extrairEmailJwt(headerAutorizacao);
        //Monta o comando com os dados de email do usuario logado e da requisição
        CadastrarProfessorCaso.Comando comando = new CadastrarProfessorCaso.Comando(
                emailUsuarioLogado,
                requisicao.nome(),
                requisicao.matricula(),
                requisicao.email(),
                requisicao.senha(),
                requisicao.cargo()
        );
        // Aciona o ouvinte para enviar o email ao professor
        var resposta = cadastrarProfessorCaso.executar(comando);
        // Retorna 201
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/cargos")
    public ResponseEntity<ListarCargosProfessorCaso.Resposta> listarCargos(){
        ListarCargosProfessorCaso.Resposta resposta = listarCargosProfessorCaso.executar();

        return ResponseEntity.ok(resposta);
    }

    //Get (professores/api/?cargo=nome_do_cargo
    @GetMapping
    public ResponseEntity<ListarProfessoresPorCargoCaso.Resposta> listarProfessoresPorCargos
            (@RequestParam("cargo") String cargo){

        ListarProfessoresPorCargoCaso.Comando comando =
                    new ListarProfessoresPorCargoCaso.Comando(cargo);
            ListarProfessoresPorCargoCaso.Resposta resposta =
                    listarProfessoresPorCargoCaso.executar(comando);

            return ResponseEntity.ok(resposta);
    }

    // Extrair do header de autorização o jwt e pega o email do usuário logado
    private String extrairEmailJwt(String headerAutorizacao){
        // Extrai a identidade de quem está fazendo a requisição de forma segura
        String token = headerAutorizacao.replace("Bearer ", "");
        //Pega o email do token jwt
        return geradorToken.extrairTopico(token);
    }


    // --------- DTOS ----------
    public record CadastrarProfessorRequisicao(
            String nome,
            String matricula,
            String email,
            String senha,
            CargoProfessor cargo
    ){}
}
