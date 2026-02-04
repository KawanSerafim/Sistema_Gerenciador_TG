import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import './custom.bootstrap.scss';
import { BrowserRouter, Route, Routes } from 'react-router';
import CadastroAluno from './pages/aluno/cadastroAluno/CadastroAluno.jsx';
import CadastroProfessor from './pages/professor/CadastroProfessor.jsx';
import FormarGrupo from './pages/aluno/formarGrupo/FormarGrupo.jsx';
import ConfirmarEmail from './pages/utils/ConfirmarEmail.jsx';
import CadastrarCurso from './pages/curso/CadastrarCurso.jsx';

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
      </Routes>
    </BrowserRouter>
  </StrictMode>,
)
