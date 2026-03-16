import React, { useState } from 'react';
import { Form, ListGroup } from 'react-bootstrap';

const AutocompleteFilter = ({ opcoesDisponiveis, aoFiltrar, placeholder }) => {
    const [busca, setBusca] = useState("");
    const [sugestoes, setSugestoes] = useState([]);
    const [mostrarSugestoes, setMostrarSugestoes] = useState(false);

    // Atualiza a tabela a cada letra digitada e filtra as sugestões
    const handleChange = (e) => {
        const termo = e.target.value;
        setBusca(termo);
        aoFiltrar(termo); // Avisa a tabela principal que o texto mudou

        if (termo.length > 0) {
            const filtrados = opcoesDisponiveis.filter(opcao =>
                String(opcao).toLowerCase().includes(termo.toLowerCase())
            );
            setSugestoes(filtrados);
        } else {
            // Se apagar tudo, exibe os primeiros (ex: 5)
            setSugestoes(opcoesDisponiveis.slice(0, 5));
        }
    };

    const handleFocus = () => {
        setMostrarSugestoes(true);
        if (!busca) {
            setSugestoes(opcoesDisponiveis.slice(0, 5));
        }
    };

    const handleBlur = () => {
        // Delay para dar tempo de o clique na sugestão ser registrado
        setTimeout(() => setMostrarSugestoes(false), 200);
    };

    const selecionarSugestao = (valor) => {
        setBusca(valor);
        aoFiltrar(valor); // Filtra a tabela pelo valor exato clicado
        setMostrarSugestoes(false);
    };

    return (
        <div style={{ position: 'relative' }} className="mt-1 text-start">
            <Form.Control
                size="lm"
                type="text"
                value={busca}
                onChange={handleChange}
                onFocus={handleFocus}
                onBlur={handleBlur}
                placeholder={placeholder || "Pesquisar..."}
                autoComplete="off"
            />
            {mostrarSugestoes && sugestoes.length > 0 && (
                <ListGroup
                    className="position-absolute w-100 shadow"
                    style={{
                        zIndex: 1050, // Fica por cima do cabeçalho da tabela
                        maxHeight: '150px',
                        overflowY: 'auto',
                        top: '100%'
                    }}
                >
                    {sugestoes.map((sugestao, idx) => (
                        <ListGroup.Item
                            key={idx}
                            action
                            className="p-2 fs-6"
                            onClick={() => selecionarSugestao(sugestao)}
                            style={{ cursor: 'pointer' }}
                        >
                            {sugestao}
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            )}
        </div>
    );
};
export default AutocompleteFilter