import { useState } from "react";

/**
 * Hook para lidar com modal 
 * @param {*} initialData 
 * @returns show, selectedData, handleOpen(), handleClose()
 */
export const useModal = (initialData = []) => {
    const [show, setShow] = useState(false);
    const [selectedData, setSelectedData] = useState(initialData);

    //Abir o modal e setar os dados
    const handleOpen = (data) => {
        setSelectedData(data);
        setShow(true);
    };

    //Fechar o modal
    const handleClose = () => {
        setShow(false);
        //Limpa os dados ao fechar
        setSelectedData([]);
    }

    //Retorna tudo que o componente precisa
    return {
        show,
        selectedData,
        handleOpen,
        handleClose
    };
}