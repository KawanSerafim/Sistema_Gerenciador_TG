export function bloquearCaracteresEspeciais(e) {
    //Bloqueia "e", "E", "+", "-", ".", ","
    if (["e", "E", "+", "-", ".", ","].includes(e.key)) {
        e.preventDefault();
    }
}