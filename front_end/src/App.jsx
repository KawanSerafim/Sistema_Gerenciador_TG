
import UserNavBar from './components/usernavbar/UserNavBar';
import './App.css';
import SolicitarOrientacao from './pages/aluno/solicitarOrientacao/solicitarOrientacao';


function App() {
  return (
    <>
      <UserNavBar
        /*Deve verificar qual o nome do usuario logado para ser passado ao componente*/
        userName='Sam'
      />
      <SolicitarOrientacao />
    </>
  );
}

export default App;