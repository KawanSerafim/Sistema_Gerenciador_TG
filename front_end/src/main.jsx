import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './custom.bootstrap.scss';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import CadastroAluno from './pages/aluno/cadastroAluno/CadastroAluno.jsx';
import CadastroProfessor from './pages/professor/cadastroProfessor/CadastroProfessor.jsx';
import FormarGrupo from './pages/aluno/formarGrupo/FormarGrupo.jsx';
import ConfirmarEmail from './pages/utils/ConfirmarEmail.jsx';
import CadastrarCurso from './pages/curso/CadastrarCurso.jsx';
import VisaoSolicitacaoOrientacao from './pages/professor/visaoSolicitacaoOrientacao/VisaoSolicitacaoOrientacao.jsx';
import EnviarTurma from "./pages/professor/enviarTurma/EnviarTurma.jsx"
import VisaoGrupos from './pages/professor/visaoGrupos/VisaoGrupos.jsx';
import VisaoAlunosEnviados from './pages/professor/visaoAlunosEnviados/VisaoAlunosEnviados.jsx';
import CadastrarTurma from './pages/professor/coordenador/cadastrarTurma/CadastrarTurma.jsx';
import CarregarTG from './pages/aluno/carregarTG/CarregarTG.jsx';
import GerarCertificado from './pages/professor/gerarCertificado/GerarCertificado.jsx';
import MarcarBanca from './pages/professor/marcarBanca/MarcarBanca.jsx';

import FinalizarDisciplinas from './pages/professor/finalizarDisciplinas/FinalizarDisciplinas.jsx';
import Login from './pages/utils/login/Login.jsx';
import SolicitarOrientacao from './pages/aluno/solicitarOrientacao/SolicitarOrientacao.jsx';
import Home from './pages/aluno/home/Home.jsx';
import CadastrarProfessor from './pages/adm/CadastrarProfessor.jsx';
import VisaoBancasArtigos from './pages/professor/visaoBancasArtigos/VisaoBancasArtigos.jsx';
import InicioProfessor from './pages/professor/home/InicioProfessor.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/adm/cadastarProfessor" element={<CadastrarProfessor />} />
        <Route path="/professor/cadastro" element={<CadastroProfessor />} />
        <Route path="/confirmarEmail" element={<ConfirmarEmail />} />
        <Route path="/curso/cadastro" element={<CadastrarCurso />} />
        <Route path="/professor/" element={<InicioProfessor />} />
        <Route path="/coordenador/cadastrarTurmaTG" element={<CadastrarTurma />} />
        <Route path="/professor/enviarTurma" element={<EnviarTurma />} />
        <Route path="/professor/visaoAlunosEnviados" element={<VisaoAlunosEnviados />} />
        <Route path="/professor/visaoSolicitacoesOrientacao" element={<VisaoSolicitacaoOrientacao />} />
        <Route path="/professor/visaoGrupos" element={<VisaoGrupos />} />
        <Route path="/professor/certificados" element={<GerarCertificado />} />
        <Route path="/professor/marcarBanca" element={<MarcarBanca />} />
        <Route path="/professor/visaoBancas" element={<VisaoBancasArtigos />} />
        <Route path="/professor/finalizarDisciplinas" element={<FinalizarDisciplinas />} />
        <Route path="/aluno/cadastro" element={<CadastroAluno />} />
        <Route path="/aluno/" element={<Home />} />
        <Route path="/aluno/formarGrupo" element={<FormarGrupo />} />
        <Route path="/aluno/solicitarOrientacao" element={<SolicitarOrientacao />} />
        <Route path="/aluno/enviarTG" element={<CarregarTG />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
