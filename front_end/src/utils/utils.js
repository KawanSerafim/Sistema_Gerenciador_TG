export function bloquearCaracteresInputNumber(e) {
    //Bloqueia "e", "E", "+", "-", ".", ","
    if (["e", "E", "+", "-", ".", ","].includes(e.key)) {
        e.preventDefault();
    }
}

export function bloquearCaracteresInputNome(e) {
    // Permite apenas letras, espaços e alguns caracteres especiais (hífen, apóstrofo)
    const regex = /^[a-záàâãéèêíïóôõöúçñA-ZÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ\s'-]*$/;

    if (!regex.test(e.target.value + e.key)) {
        e.preventDefault();
    }
}

export function validarNome(nome) {
    // Regex que valida nomes com acentos, hífens e apóstrofos
    const regex = /^[a-záàâãéèêíïóôõöúçñA-ZÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ\s'-]{2,}$/;
    return regex.test(nome.trim());
}