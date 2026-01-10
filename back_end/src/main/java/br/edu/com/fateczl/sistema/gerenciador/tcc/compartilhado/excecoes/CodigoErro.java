package br.edu.com.fateczl.sistema.gerenciador.tcc.compartilhado.excecoes;

public enum CodigoErro {
    // VALIDAÇÃO ---------------------------------------------------------------

    VD_001_CAMPO_OBRIGATORIO("VD", "O campo '%s' é obrigatório."),
    VD_002_FORMATO_INVALIDO("VD", "O campo '%s' deve estar no formato '%s'."),
    VD_003_ASSOCIACAO_OBRIGATORIA("VD", "O campo '%s' deve ter uma associação" +
            " ao campo '%s'."),
    VD_004_DATA_INVALIDA("VD", "O campo '%s' deve respeitar a condição: '%s'."),
    VD_005_PADRAO_INVALIDO("VD", "O campo '%s' deve respeitar o padrão '%s'."),
    VD_006_COLECAO_OBRIGATORIA_VAZIA("VD", "A coleção '%s' deve ter um valor."),
    VD_007_CAMPO_NAO_SUPORTADO("VD", "O campo '%s' não é suportado. Motivo: " +
            "'%s'"),
    VD_008_ARQUIVO_INVALIDO("VD", "O arquivo não é válido."),

    // REGRA DE NEGÓCIO --------------------------------------------------------

    RN_001_ESTADO_INVALIDO_PARA_ACAO("RN", "O campo '%s' deve ter o estado " +
            "'%s' para ser considerado válido para essa ação."),
    RN_002_REGISTRO_DUPLICADO("RN", "O campo '%s' tem valor duplicado."),
    RN_003_CONDICAO_ACAO_NAO_ATENDIDA("RN", "O campo '%s' não atende a " +
            "condição '%s'."),

    // VALIDAÇÃO ---------------------------------------------------------------

    GN_001_REGISTRO_NAO_ENCONTRADO("GN", "O registro não foi encontrado.");

    // CONSTRUTOR E COMPORTAMENTOS ---------------------------------------------

    private final String prefixo;
    private final String template;

    CodigoErro(String prefixo, String template) {
        this.prefixo = prefixo;
        this.template = template;
    }

    public String formatar(Object... args) {
        return String.format(template, args);
    }

    public String getPrefixo() {
        return prefixo;
    }
}