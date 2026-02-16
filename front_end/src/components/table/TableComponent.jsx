import Table from 'react-bootstrap/Table';
import './TableComponent.css'
/** 
 * columns: [{ }]
 * data: [{ }]
**/
const TableComponent = ({ columns, data }) => {


    return (
        <Table responsive className="custom-table custom-table-hover">
            <thead>
                <tr className='fs-3 fw-medium'>
                    {columns.map((col, index) => (
                        <th key={index} className="table-header">
                            {col.header}
                        </th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {data.map((row, rowIndex) => (
                    <tr key={row.id || rowIndex} className='fs-4 fw-medium'>
                        {/* Para cada row itere sobre as colunas e preencha com o accessor adequado*/}
                        {columns.map((col, colIndex) => (
                            <td key={`${row.id}-${colIndex}`}>
                                {/* Se o 'cell' (render) for uma função, executa ela passando a linha inteira.
                                Se não, apenas imprime o valor da propriedade (accessor). */}
                                {col.render ? col.render(row) : row[col.accessor]}
                            </td>
                        ))}
                    </tr>
                ))}
            </tbody>
        </Table >
    )
}
export default TableComponent;