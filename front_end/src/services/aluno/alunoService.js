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
    // Confirme com seu par qual é a rota exata (ex: /turmas/upload-alunos)
    return await apiClient(`/alunos/importar-alunos`, {
      method: "POST",
      body: formData
      //Sem content-type pois o apiClient ja lida com isso
    });
  }
};
