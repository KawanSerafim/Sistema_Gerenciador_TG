import Table from 'react-bootstrap/Table';
import './TableComponent.css'
import { useMemo, useState } from 'react';
import TableFilter from './TableFilter';
/** 
 * columns: lista de objetos contendo: header, accessor e opcionalmente um render 
 * 
 * data: lista de objetos contendo: id, valor1, valor2, valorN
**/
const TableComponent = ({ colunas, dados }) => {
    // Estado que armazena os filtros ativos: { nome: "Thiago", tipo: "Artigo" }
    const [filtros, setFiltros] = useState({});

    // Função para atualizar o estado de filtro de uma coluna específica
    const handleChangeFiltro = (accessor, value) => {
        setFiltros(prev => ({
            ...prev,
            [accessor]: value
        }));
    };

    // LÓGICA DE FILTRO (Boas Práticas: Dados Derivados)
    const dadosFiltrados = useMemo(() => {
        return dados.filter(row => {
            return Object.entries(filtros).every(([accessor, value]) => {
                // Se o filtro estiver vazio, passa tudo
                if (!value) return true;

                const valorCelula = String(row[accessor] || "").toLowerCase();
                const valorBuscado = String(value).toLowerCase();

                return valorCelula.includes(valorBuscado);
            });
        });
    }, [dados, filtros]);

    return (
        <Table responsive className="custom-table custom-table-hover">
            <thead className="text-nowrap">
                <tr className='fs-5 fw-medium'>
                    {colunas.map((col) => (
                        <th key={col.accessor} className="table-header">
                            <div className="">
                                {col.header}
                            </div>
                            <TableFilter
                                coluna={col}
                                dados={dados}
                                aoFiltrar={handleChangeFiltro}
                            />
                        </th>
                    ))}
                </tr>
            </thead>
            <tbody>

                {dadosFiltrados.length > 0 ? (
                    dadosFiltrados.map((linha, index) => (
                        <tr key={linha.id || index}>
                            {colunas.map(col => (
                                <td key={col.accessor} data-label={col.header} className='fs-5'>
                                    {col.render ? col.render(linha) : linha[col.accessor]}
                                </td>
                            ))}
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan={colunas.length} className="text-center py-4 text-muted" >
                            Nenhum registro encontrado.
                        </td>
                    </tr>
                )}


            </tbody>
        </Table >
    )
}
export default TableComponent;