import { BrowserRouter, Route, Routes } from 'react-router-dom';

// Importação das suas Telas
import CadastroAluno from './pages/aluno/cadastroAluno/CadastroAluno.jsx';
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
import SolicitarOrientacao from './pages/aluno/solicitarOrientacao/SolicitarOrientacao.jsx';
import VisaoBancasArtigos from './pages/professor/visaoBancasArtigos/VisaoBancasArtigos.jsx';
import RotaProtegida from './components/seguranca/RotaProtegida.jsx';
import CadastroProfessor from './pages/professor/cadastroProfessor/CadastroProfessor.jsx';
import Login from './pages/utils/login/Login.jsx';
import Inicio from './pages/utils/home/Inicio.jsx'
import AcessoNegado from './components/erros/AcessoNegado.jsx';
import RecuperacaoSenha from './pages/utils/RecuperacaoSenha.jsx';

// Importação de Componentes Globais
import UserNavBar from './components/usernavbar/UserNavBar';
import './App.css';
import VincularCoorientadorExterno from './pages/aluno/vincularCoOrientadorExterno/VincularCoOrientadorExterno.jsx';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* ========================================= */}
        {/* ROTAS PÚBLICAS (Acesso Livre)             */}
        {/* ========================================= */}
        <Route path="/acesso-negado" element={<AcessoNegado />} />
        <Route path="/confirmarEmail" element={<ConfirmarEmail />} />
        <Route path="/aluno/cadastro" element={<CadastroAluno />} />
        <Route path="/" element={<Login />} />
        <Route path="/recuperar-senha" element={<RecuperacaoSenha />} />

        {/* ========================================= */}
        {/* HOME UNIVERSAL (Basta estar logado)       */}
        {/* ========================================= */}
        <Route path="/inicio" element={
          <RotaProtegida>
            <Inicio />
          </RotaProtegida>
        } />

        {/* ========================================= */}
        {/* ROTAS DO ADMINISTRADOR                    */}
        {/* ========================================= */}
        <Route path="/curso/cadastro" element={
          <RotaProtegida roleNecessaria="ROLE_ADMIN">
            <CadastrarCurso />
          </RotaProtegida>
        } />
        <Route path="/admin/cadastrar-professor" element={
          <RotaProtegida roleNecessaria="ROLE_ADMIN">
            <CadastroProfessor />
          </RotaProtegida>
        } />

        {/* ========================================= */}
        {/* ROTAS DO COORDENADOR                      */}
        {/* ========================================= */}
        <Route path="/coordenador/cadastrarTurmaTG" element={
          <RotaProtegida roleNecessaria="ROLE_COORDENADOR_CURSO">
            <CadastrarTurma />
          </RotaProtegida>
        } />

        {/* ========================================= */}
        {/* ROTAS DO PROFESSOR DE TG                  */}
        {/* ========================================= */}
        <Route path="/professor/enviarTurma" element={
          <RotaProtegida roleNecessaria="ROLE_PROFESSOR_TG">
            <EnviarTurma />
          </RotaProtegida>
        } />

        <Route path="/professor/visaoAlunosEnviados" element={
          <RotaProtegida roleNecessaria="ROLE_PROFESSOR_TG">
            <VisaoAlunosEnviados />
          </RotaProtegida>
        } />

        <Route path="/professor/visaoGrupos" element={
          <RotaProtegida roleNecessaria="ROLE_PROFESSOR_TG">
            <VisaoGrupos />
          </RotaProtegida>
        } />

        <Route path="/professor/finalizarDisciplinas" element={
          <RotaProtegida roleNecessaria="ROLE_PROFESSOR_TG">
            <FinalizarDisciplinas />
          </RotaProtegida>
        } />

        {/* ========================================= */}
        {/* ROTAS DO ORIENTADOR                       */}
        {/* ========================================= */}
        <Route path="/professor/marcarBanca" element={
          <RotaProtegida roleNecessaria="ROLE_ORIENTADOR">
            <MarcarBanca />
          </RotaProtegida>
        } />
        <Route path="/professor/visaoSolicitacoesOrientacao" element={
          <RotaProtegida roleNecessaria="ROLE_ORIENTADOR">
            <VisaoSolicitacaoOrientacao />
          </RotaProtegida>
        } />
        <Route path="/professor/visaoBancas" element={
          <RotaProtegida roleNecessaria="ROLE_ORIENTADOR">
            <VisaoBancasArtigos />
          </RotaProtegida>
        } />
        <Route path="/professor/certificados" element={
          <RotaProtegida roleNecessaria="ROLE_ORIENTADOR">
            <GerarCertificado />
          </RotaProtegida>
        } />

        {/* ========================================= */}
        {/* ROTAS DO ALUNO LOGADO                     */}
        {/* ========================================= */}
        <Route path="/aluno/formarGrupo" element={
          <RotaProtegida roleNecessaria="ROLE_ALUNO">
            <FormarGrupo />
          </RotaProtegida>
        } />
        <Route path="/aluno/solicitarOrientacao" element={
          <RotaProtegida roleNecessaria="ROLE_ALUNO">
            <SolicitarOrientacao />
          </RotaProtegida>
        } />
        <Route path="/aluno/vincularCoOrientadorExterno" element={
          <RotaProtegida roleNecessaria="ROLE_ALUNO">
            <VincularCoorientadorExterno />
          </RotaProtegida>
        } />
        <Route path="/aluno/enviarTG" element={
          <RotaProtegida roleNecessaria="ROLE_ALUNO">
            <CarregarTG />
          </RotaProtegida>
        } />
      </Routes>
    </BrowserRouter>
  );
}

export default App;