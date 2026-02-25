import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import './custom.bootstrap.scss';
import { BrowserRouter, Route, Routes } from 'react-router';
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

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />} />
        <Route path="/aluno/cadastro" element={<CadastroAluno />} />
        <Route path="/aluno/formarGrupo" element={<FormarGrupo />} />
        <Route path="/professor/cadastro" element={<CadastroProfessor />} />
        <Route path="/confirmarEmail" element={<ConfirmarEmail />} />
        <Route path="/curso/cadastro" element={<CadastrarCurso />} />
        <Route path="/professor/visaoSolicitarOrientacao" element={<VisaoSolicitacaoOrientacao />} />
        <Route path="/professor/enviarTurma" element={<EnviarTurma />} />
        <Route path="/professor/visaoGrupos" element={<VisaoGrupos />} />
        <Route path="/professor/visaoAlunosEnviados" element={<VisaoAlunosEnviados />} />
        <Route path="/coordenador/cadastrarTurmaTG" element={<CadastrarTurma />} />
        <Route path="/aluno/enviarTG" element={<CarregarTG />} />
        <Route path="/professor/certificados" element={<GerarCertificado />} />
        <Route path="/professor/marcarBanca" element={<MarcarBanca />} />
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
