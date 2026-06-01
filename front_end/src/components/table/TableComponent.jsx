import Table from 'react-bootstrap/Table';
import './TableComponent.css'
import { useMemo, useState } from 'react';
import TableFilter from './TableFilter';
import { Pagination } from 'react-bootstrap';
/** 
 * columns: lista de objetos contendo: header, accessor e opcionalmente um render 
 * 
 * data: lista de objetos contendo: id, valor1, valor2, valorN
 * Props opcionais de paginação:
 * paginaAtual (number): A página atual (começando em 0)
 * totalPaginas (number): O total de páginas retornadas pelo backend
 * setPaginaAtual (function): Função para alterar a página atual
**/
const TableComponent = ({ colunas, dados,
    // Props opcionais de paginação (com valores padrão seguros)
    paginaAtual = 0,
    totalPaginas = 1,
    setPaginaAtual
}) => {
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
        <div className="d-flex flex-column w-100">
            <Table responsive className="custom-table custom-table-hover">
                <thead className="text-wrap">
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
            {/* LÓGICA DE RENDERIZAÇÃO OPCIONAL DA PAGINAÇÃO */}
            {/* Só exibe se a tela pai enviou a função 'setPaginaAtual' e se houver dados */}
            {setPaginaAtual && dados.length > 0 && (
                <div className="d-flex justify-content-center">
                    <Pagination>
                        <Pagination.Prev
                            disabled={paginaAtual === 0}
                            onClick={() => setPaginaAtual(prev => Math.max(0, prev - 1))}
                        />
                        <Pagination.Item active>
                            Página {paginaAtual + 1} de {totalPaginas}
                        </Pagination.Item>
                        <Pagination.Next
                            disabled={paginaAtual === totalPaginas - 1}
                            onClick={() => setPaginaAtual(prev => Math.min(totalPaginas - 1, prev + 1))}
                        />
                    </Pagination>
                </div>
            )}
        </div>
    )
}
export default TableComponent;