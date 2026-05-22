import { obrigatorio } from "../../utils/utils";
import { apiClient } from "../apiClient";

export const grupoService = {
  /**
   *  Cria um novo grupo de TG
   * @param {Object} payloadGrupo - Objeto contendo os dados do grupo a ser criado
   * @returns {Promise<any>} - Promise com a resposta da criação do grupo
   */
  criarGrupo: async (payloadGrupo) => {
    return await apiClient(`/gruposTg`, {
      method: "POST",
      body: JSON.stringify(payloadGrupo),
    });
  },
  listarVisaoGrupos: async (pagina = 0, tamanho = 10, somenteSemGrupo = false) => {
    try {
      // Ajuste a rota para suportar as Query Params
      const url = `/gruposTg/visao-gruposTg?pagina=${pagina}&tamanho=${tamanho}&somenteSemGrupo=${somenteSemGrupo}`;
      const resposta = await apiClient(url, { method: "GET" });

      // Retorna o objeto Pagina completo
      return resposta?.grupos || resposta;
    } catch (erro) {
      console.error("Erro ao buscar visão dos grupos:", erro);
      throw erro;
    }
  },
  vincularCoorientadorExterno: async (payload = obrigatorio("payload")) => {
    return await apiClient(`/gruposTg/coorientadores-externos`, {
      method: "PATCH",
      body: JSON.stringify(payload),
    });
  },
  /**
  * Busca os grupos orientados pelo professor logado.
  * @param {string|number} ano (Opcional) Ano letivo
  * @param {string|number} semestre (Opcional) Semestre letivo
  * @returns {Promise<Array>} Lista de grupos
  */
  buscarGruposOrientados: async (ano, semestre) => {
    let url = "/gruposTg/grupos-orientados"; // Ajuste o prefixo se o seu @RequestMapping for diferente
    const params = [];

    if (ano) params.push(`ano=${ano}`);
    if (semestre) params.push(`semestre=${semestre}`);

    if (params.length > 0) {
      url += `?${params.join("&")}`;
    }

    return await apiClient(url, {
      method: "GET",
    });
  },

  // Função para enviar o Trabalho de Graduação
  enviarTrabalhoGraduacao: async (arquivo) => {
    // Usa a interface FormData nativa do JS para envio de arquivos
    const formData = new FormData();

    // A chave "file" DEVE ser exatamente o mesmo nome que está no 
    // @RequestParam("file") do controlador do backend
    formData.append("file", arquivo);

    // O apiClient vai injetar o token e remover o "application/json" automaticamente
    return await apiClient("/gruposTg/trabalho", {
      method: "POST",
      body: formData
    });
  },
  baixarTrabalhoBanca: async (idBanca) => {
    // Passamos uma opção customizada exclusiva para capturar a resposta bruta do fetch
    // Aproveitamos toda a lógica de URL base, Injeção de Token e Erros do seu apiClient

    const respostaBruta = await apiClient(`/gruposTg/${idBanca}/trabalho`, {
      method: "GET",
      // Passamos um marcador personalizado para identificar que não queremos o parse JSON do corpo
      skipJsonParse: true
    });

    // Como alteramos o comportamento do retorno para receber o objeto Response do Fetch:
    const blob = await respostaBruta.blob();

    // Captura o cabeçalho enviado pelo Java
    const disposition = respostaBruta.headers.get('Content-Disposition');

    // Fallback genérico caso ainda dê algum problema de CORS
    let filename = 'trabalho_graduacao.docx';

    if (disposition && disposition.includes('filename=')) {
      // Divide a string no 'filename=' e remove as aspas que o Java costuma injetar
      filename = disposition.split('filename=')[1].replaceAll('"', '');
    }

    return { blob, filename };
  }
};
