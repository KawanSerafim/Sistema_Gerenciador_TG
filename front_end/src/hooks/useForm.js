import { useState } from 'react';

export function useForm(initialValues, validateFunction) {
    const [values, setValues] = useState(initialValues);
    const [errors, setErrors] = useState({});

    // Atualiza o estado conforme o usuário digita
    const handleChange = (e) => {
        const { name, value } = e.target;
        setValues({
            ...values,
            [name]: value,
        });

        // Limpa o erro daquele campo específico assim que o usuário volta a digitar
        if (errors[name]) {
            setErrors({
                ...errors,
                [name]: null,
            });
        }
    };

    // Intercepta o envio do formulário
    const handleSubmit = (submitCallback) => (e) => {
        e.preventDefault();

        // Roda a função de validação genérica passada para o hook
        const validationErrors = validateFunction(values);
        setErrors(validationErrors);

        // Se o objeto de erros estiver vazio, significa que passou na validação
        if (Object.keys(validationErrors).length === 0) {
            submitCallback(values); // Executa a chamada para a sua API
        }
    };

    return { values, errors, handleChange, handleSubmit, setErrors };
}