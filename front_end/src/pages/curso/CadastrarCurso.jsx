import { useState } from "react";
import { Container, Form, FormCheck, FormControl, FormGroup, FormLabel, FormSelect, Button, Alert } from "react-bootstrap";
import "./cadastrarCurso.css"
import UserNavBar from "../../components/usernavbar/UserNavBar";

// Zod e RHF para validações
import { useFieldArray, useForm } from "react-hook-form";
import {z} from "zod";
import { zodResolver } from "@hookform/resolvers/zod";


//Schema de validação
const cursoSchema = z.object({
    nome: z.string()
        .min(1,"Nome do curso é um campo obrigatório"),
        
    turno: z.array(z.string()).min(1,"Selecione pelo menos um turno"),
    disciplina: z.array(z.string()).min(1,"Selecione pelo menos uma disciplina"),
     coordenador: z.string().min(1, "Selecione pelo menos um tipo coordenador"),
    tiposTG: z.array(z.object({
        id: z.string(),
        label: z.string(),
        ativo: z.boolean(),
        qntMax: z.coerce.number().min(0).optional(),
    }))
    .superRefine((tgs, ctx) => {
        // Filtra apenas os ativos para fazer as validações
        const ativos = tgs.filter(tg => tg.ativo);

        // Se nenhum estiver ativo, lança o erro
        if (ativos.length === 0) {
            ctx.addIssue({
                code: z.custom,
                message: "Selecione pelo menos um tipo de trabalho de conclusão (TG)",
                //Caminho para o acesso ao root para exibição do errro
                path: []
            });
            return; // Se não tem nenhum, nem adianta verificar as quantidades
        }

        // 3. Se tem ativos, verifica se todos têm quantidade > 0
        const temQuantidadeZerada = ativos.some(tg => !tg.qntMax || tg.qntMax <= 0);
        if (temQuantidadeZerada) {
            ctx.addIssue({
                code: z.custom,
                message: "Informe a quantidade máxima (maior que zero) para os TGs selecionados",
                //Parametro para o acesso ao root para exibição do errro
                path: []
            });
        }
    })
    // 4. Só DEPOIS que tudo está validado, transformamos para o backend!
    .transform(tgs => tgs.filter(tg => tg.ativo))
    })

const CadastrarCurso = () => {
    const [exibirSucesso, setExibirSucesso] = useState(false)


const {
    register,
    handleSubmit,
    control,
    watch,
    reset,
    formState: { errors }
} = useForm({
    resolver: zodResolver(cursoSchema),
    defaultValues: { // Tudo DEVE ficar dentro daqui!
        nome: "",
        turno: [],
        disciplina: [],
        coordenador: "",
        tiposTG: [ // Agora sim, está dentro do defaultValues
            { id: 'software', label: 'Desenvolvimento de Software', ativo: false, qntMax: 0 },
            { id: 'monografia', label: 'Monografia', ativo: false, qntMax: 0 },
            { id: 'artigo', label: 'Artigo', ativo: false, qntMax: 0 },
            { id: 'plano-negocios', label: 'Plano de Negócios', ativo: false, qntMax: 0 }
        ]
    }
}); // Fechamento do useForm

    
    //Control o array de tipos TG
    const { fields} = useFieldArray({
        control,
        //Nome do array no Schema
        name: "tiposTG"
    });

    //Observador de mudanças no array tipoTG, para saber quais inputs habilitar 
    const tiposTGWatched = watch("tiposTG");


    const enviarParaBackend = (dadosValidados) => {
     // O JSON aqui já sai perfeito, apenas com os TGs que foram realmente selecionados!
        console.log("Enviando para a API:", dadosValidados);
        setExibirSucesso(true);
        reset(); // Limpa o formulário
        setTimeout(() => setExibirSucesso(false), 5000);
    }

    return (
        <>
            <UserNavBar
                userName="Administrador"
                maxWidth="800px"
            />
            <Container className="mt-5" style={{ maxWidth: '800px' }}>
                <h2 className='bg-primary text-white p-3 fs-1 rounded-top-4 text-center m-0'>Cadastro de Curso</h2>
                <Form
                    noValidate
                    className='form-bg border border-secondary-subtle border-top-0 p-4 rounded-bottom-4 shadow-sm'
                    id="formCurso"
                    onSubmit={handleSubmit(enviarParaBackend)}

                >
                    {/* Nome */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicName">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Nome do Curso</FormLabel>
                        <FormControl 
                        type="text" 
                        placeholder="Digite o nome do curso" 
                        {...register("nome")}
                        isInvalid={!!errors.nome} 
                        className='bg-white text-black fw-normal fs-5' />
                        <Form.Control.Feedback type="invalid">{errors.nome?.message}</Form.Control.Feedback>
                    </FormGroup>
                    {/* Turno e Disciplinas */}
                    <FormGroup className="mb-3" controlId="formBasicTurnoDisciplinas">
                        <div className="row">
                            {/* Turno */}
                            <div className="col-md-6">

                                <FormLabel className='text-secondary fs-4 fw-medium d-block mb-2'>Turno</FormLabel>
                                <div className="mb-2 fw-medium fs-5">
                                    <FormCheck
                                        inline
                                        title="Manhã"
                                        label="Manhã"
                                        name="Manhã"
                                        type="checkbox"
                                        id="checkbox-manha"
                                        className=""
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                    <FormCheck
                                        inline
                                        title="Tarde"
                                        label="Tarde"
                                        name="Tarde"
                                        type="checkbox"
                                        id="checkbox-tarde"
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                    <FormCheck
                                        inline
                                        title="Noite"
                                        label="Noite"
                                        name="Noite"
                                        type="checkbox"
                                        id="checkbox-noite"
                                        {...register("turno")}
                                        isInvalid={!!errors.turno}
                                    />
                                </div>
                                {errors.turno && <div className="text-danger small fw-bold">{errors.turno.message}</div>}
                            </div>
                            {/* Disciplinas */}
                            <div className="col-md-6">
                                <FormLabel className='text-secondary fs-4 fw-medium d-block mb-2'>Disciplinas</FormLabel>
                                <div className="mb-3 fw-medium fs-5">
                                    <FormCheck
                                        inline
                                        title="TG1"
                                        label="TG1"
                                        name="TG1"
                                        type="checkbox"
                                        id="checkbox-tg1"
                                        {...register("disciplina")}
                                        isInvalid={!!errors.disciplina}
                                    />
                                    <FormCheck
                                        inline
                                        title="TG2"
                                        label="TG2"
                                        name="TG2"
                                        type="checkbox"
                                        id="checkbox-tg2"
                                        {...register("disciplina")}
                                        isInvalid={!!errors.disciplina}
                                    />
                                </div>
                        {errors.disciplina && <div className="text-danger small fw-bold">{errors.disciplina.message}</div>}
                            </div>
                        </div>
                    </FormGroup>

                    {/* Tipos de trab de graduacao */}
                    <FormGroup className="mb-3 d-flex flex-column" controlId="formBasicTipo">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Tipo de Trabalho de Graduação</FormLabel>
                        {/* Checkboxes de tipo tg e inputs de numero de integrantes */}
                        <div className="">
                            {fields.map((opcao,index) => {
                            // Verifica se este checkbox específico está marcado para habilitar o input
                            const isAtivo = tiposTGWatched[index]?.ativo;
                            return (
                                <div key={opcao.id} className="d-flex align-items-center gap-3 mb-3 border-bottom pb-2">
                                    <Form.Check
                                        type="checkbox"
                                        title={opcao.label}
                                        id={opcao.id}
                                        label={opcao.label.toUpperCase()}
                                        className="fw-bold"
                                        {...register(`tiposTG.${index}.ativo`)}
                                    />
                                    <div className="d-flex align-items-center gap-2 ms-auto">
                                        <FormLabel
                                            className='text-secondary fs-6 fw-bold'
                                            title={'Quantidade maxima de ' + opcao.label}
                                        >Quantidade maxima de integrantes do grupo: </FormLabel>
                                        <Form.Control
                                            type="number"
                                            title={'Quantidade maxima de ' + opcao.label}
                                            disabled={!isAtivo}
                                            className={isAtivo ? "bg-white fw-medium" : "bg-light"}
                                            style={{ maxWidth: '10rem' }}
                                            placeholder="0"
                                            {...register(`tiposTG.${index}.qntMax`)}
                                            isInvalid={isAtivo && errors.tiposTG}
                                            min="0"
                                        />
                                    </div>
                                </div>
                            )})}
                        </div>
                        {errors.tiposTG && 
                            <div className="text-danger fw-bold">
                                {errors.tiposTG.message || errors.tiposTG.root?.message}
                            </div>
                        }
                    </FormGroup>

                    {/* Selecionar Coordenador */}
                    <FormGroup className="mb-3" controlId="formCoordenador">
                        <FormLabel className='text-secondary fs-4 fw-medium'>Coordenador do Curso</FormLabel>
                        <FormSelect 
                            {...register("coordenador")} 
                            isInvalid={!!errors.coordenador}
                            className='bg-white text-black fw-normal fs-5'
                        >
                            <option value='' hidden>Selecione o coordenador do curso</option>
                            <option value='coord1'>Coordenador 1</option>
                            <option value='coord2'>Coordenador 2</option>
                        </FormSelect>
                        <Form.Control.Feedback type="invalid">{errors.coordenador?.message}</Form.Control.Feedback>
                    </FormGroup>

                    {/* Botão de Cadastrar */}
                    <FormGroup className="text-center">
                        <Button
                            variant="primary"
                            type="submit"
                            id='btn-cadastro' className='mb-2 fs-5 fw-medium w-100'
                        >
                            Cadastrar
                        </Button>
                    </FormGroup>
                </Form>
                {exibirSucesso && (
                    <Alert variant="sucess" onClose={() => setExibirSucesso(false)} dismissible className="mt-3 fw-bold shadow-sm" >
                        Curso cadastrado com sucesso!
                    </Alert>
                )}
            </Container>
        </>
    )

}
export default CadastrarCurso;