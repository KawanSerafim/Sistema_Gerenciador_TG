import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

// Estilos globais
import App from './App.jsx'
import './custom.bootstrap.scss';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)