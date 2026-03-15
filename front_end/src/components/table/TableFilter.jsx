import React from 'react';
import { Form } from 'react-bootstrap';

const TableFilter = ({ coluna, dados, aoFiltrar }) => {
    // Se a coluna não for filtrável, não renderiza nada
    if (!coluna.filtravel) return null;

    // Se for do tipo select, buscamos os valores únicos nos dados para montar as options
    if (coluna.tipoFiltro === 'select') {
        const valoresUnicos = [...new Set(dados.map(item => item[coluna.accessor]))];

        return (
            <Form.Select
                size="sm"
                className="mt-1"
                onChange={(e) => aoFiltrar(coluna.accessor, e.target.value)}
            >
                <option value="">Todos</option>
                {valoresUnicos.map(val => (
                    <option key={val} value={val}>{val}</option>
                ))}
            </Form.Select>
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