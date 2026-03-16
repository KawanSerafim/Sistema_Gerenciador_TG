import React from 'react';
import { Form } from 'react-bootstrap';
import AutocompleteFilter from './AutoCompleteFilter';


const TableFilter = ({ coluna, dados, aoFiltrar }) => {
    // Se a coluna não for filtrável, não renderiza nada
    if (!coluna.filtravel) return null;

    //Extrai valores únicos mesmo se a coluna for um Array (como grupo e membros)
    const extrairValoresUnicos = () => {
        const todosValores = dados.flatMap(item => {
            const valor = item[coluna.accessor];
            // Se for array (["João", "Maria"]), espalha os nomes. Se não, retorna normal.
            return Array.isArray(valor) ? valor : valor;
        });

        // Remove nulos, indefinidos e repetidos
        return [...new Set(todosValores)].filter(Boolean);
    };

    // Filtro tipo SELECT nativo
    if (coluna.tipoFiltro === 'select') {
        const valoresUnicos = extrairValoresUnicos();

        return (
            <Form.Select size="sm" className="mt-1" onChange={(e) => aoFiltrar(coluna.accessor, e.target.value)}>
                <option value="">Todos</option>
                {valoresUnicos.map(val => (
                    <option key={val} value={val}>{val}</option>
                ))}
            </Form.Select>
        );
    }

    // Filtro tipo AUTOCOMPLETE
    if (coluna.tipoFiltro === 'autocomplete') {
        const valoresUnicos = extrairValoresUnicos();

        return (
            <AutocompleteFilter
                opcoesDisponiveis={valoresUnicos}
                aoFiltrar={(valorEnviado) => aoFiltrar(coluna.accessor, valorEnviado)}
                placeholder={`Buscar...`}
            />
        );
    }


    // Padrão: Campo de texto
    return (
        <Form.Control
            size="sm"
            type="text"
            className="mt-1"
            placeholder={`Filtrar...`}
            onChange={(e) => aoFiltrar(coluna.accessor, e.target.value)}
        />
    );
};
export default TableFilter