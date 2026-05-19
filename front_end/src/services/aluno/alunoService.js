import { apiClient } from "../apiClient";

export const alunoService = {
  /**
   * Busca todos os alunos do curso
   * @parm {number} cursoId - ID do curso
   * @returns {Promise} - Promise com a lista de alunos
   */
  buscaAlunosPorTurma: async (turmaId) => {
    try {
      const resposta = await apiClient(`/turma/${turmaId}/alunos`, {
        method: "GET",
      });
      return resposta.data;
    } catch (erro) {
      console.error("Erro ao buscar alunos por turma:", erro);
      throw erro;
    }
  },

  enviarPlanilhaAlunos: async (formData) => {
    return await apiClient(`/alunos/importar`, {
      method: "POST",
      body: formData
      //Sem content-type pois o apiClient ja lida com isso
    });
  },

  buscarAlunosPorTurmaId: async (turmaId, pagina, tamanho) => {
    const resposta = await apiClient(`/alunos/importar?turmaId=${turmaId}&pagina=${pagina}&tamanho=${tamanho}`, {
      method: "GET"
    });
    return resposta?.alunos || [];
  },

  buscarAlunosElegiveisParaGrupo: async () => {
    const resposta = await apiClient(`/alunos/sem-grupo`, {
      method: "GET"
    });
    console.log(resposta)
    return resposta?.alunoDtos || [];
  },

  /**
   * Busca informações do grupo tg do aluno logado, se ele tiver grupo
   * 
   */
  buscarGrupoAluno: async () => {
    return await apiClient("/gruposTg/aluno", {
      method: "GET",
    });
  }
};
