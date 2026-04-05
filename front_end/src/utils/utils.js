export function bloquearCaracteresInputNumber(e) {
    //Bloqueia "e", "E", "+", "-", ".", ","
    if (["e", "E", "+", "-", ".", ","].includes(e.key)) {
        e.preventDefault();
    }
}

export function bloquearCaracteresInputNome(e) {
    // Permite teclas de controle (Backspace, setinhas, Tab, etc) e atalhos (Ctrl+C/V)
    const teclasPermitidas = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Home', 'End'];
    if (teclasPermitidas.includes(e.key) || e.ctrlKey || e.metaKey) return;

    // Se não for letra ou espaço, barra a tecla na hora!
    const apenasLetras = /^[a-zA-ZÀ-ÿ\s]*$/;
    if (!apenasLetras.test(e.key)) {
        e.preventDefault();
    }
}

export function validarNome(nome) {
    // Regex que valida nomes com acentos, hífens e apóstrofos
    const regex = /^[a-záàâãéèêíïóôõöúçñA-ZÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ\s'-]{2,}$/;
    return regex.test(nome.trim());
}

export const obrigatorio = (nomeParam) => {
    throw new Error(`Parametro "${nomeParam}" é obrigatório`);
};
