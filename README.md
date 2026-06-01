# Sistema Gerenciador de Trabalho de Graduação 🎓

Um sistema completo desenvolvido para modernizar e automatizar o ciclo de vida do Trabalho de Graduação (TG). O sistema conecta Alunos, Professores Orientadores, Coordenadores, Diretores e Professores da Disciplina em uma plataforma única, gerenciando desde a importação de alunos e formação de grupos até o agendamento de bancas, notificação automática, emissão de certificados e geração de atas oficiais.

## 🛠️ Tecnologias Utilizadas

### Backend
* **Java 25** com **Spring Boot 3**
* **MySQL** (Banco de Dados Relacional)
* **Spring Data JPA** / Hibernate
* **Spring Security** + **JWT** (JSON Web Token) para autenticação e autorização
* **JavaMailSender** para envio de e-mails transacionais assíncronos (códigos OTP, convites de banca, certificados e anexos)
* **Apache POI** para leitura e processamento de planilhas Excel (Importação de alunos)
* **Thymeleaf** operando como motor de template interno (processamento de corpos de e-mail e estruturação de documentos HTML)
* **OpenHTMLToPDF** para a conversão de templates HTML em documentos PDF nativos e responsivos sob demanda (ex: Ata da Banca e Certificados de Participação)

### Frontend
* **React.js** com **Vite**
* **JavaScript** / JSX
* **React Bootstrap** + CSS customizado para estilização responsiva
* **React Hook Form** + **Zod** para gerenciamento e validação de formulários complexos
* **Implementação própria de cliente HTTP** configurado com interceptadores globais para injeção de JWT e tratamento de downloads de arquivos binários (Blob).
* **Runtime Config Pattern** (`config.js`) para injeção dinâmica de variáveis de ambiente em produção sem necessidade de recompilação.

---

## 🏛️ Arquitetura do Backend

O backend foi construído seguindo os princípios rigorosos da **Arquitetura Limpa (Clean Architecture)** e **Domain-Driven Design (DDD)**, garantindo um código altamente testável, manutenível e isolado de frameworks. 

O projeto está dividido nas seguintes camadas principais:

1. **Domínio (`dominio`)**: O coração do software. Contém as Regras de Negócio, Entidades puras do Java (ex: `Aluno`, `Turma`, `GrupoTg`, `MandatoDiretor`), Objetos de Valor (Value Objects) e as assinaturas (interfaces) dos Repositórios. **Não possui nenhuma dependência do Spring**.
2. **Aplicação (`aplicacao`)**: Contém os **Casos de Uso** (ex: `FinalizarTurmasCaso`, `MarcarBancaCaso`, `AtribuirNotasBancaCaso`). Orquestra o fluxo de dados, executa as lógicas de negócio e dispara **Eventos de Domínio** (Observer Pattern) para lidar com efeitos colaterais (como envio automático de e-mails e certificados) sem travar a transação principal.
3. **Infraestrutura (`infraestrutura`)**: A borda externa do sistema. Aqui vivem as implementações técnicas:
   * **API / Controladores**: Endpoints REST (`@RestController`).
   * **Persistência / JPA**: Modelos de banco de dados mapeados para tabelas, Mapeadores (Mappers) e as implementações reais dos Repositórios usando Spring Data.
   * **Adaptadores de Saída**: Implementações para envio de e-mail (`JavaMailSender`), acesso ao sistema de arquivos (upload de TG) e geração de PDFs.
   * **Configurações**: Injeção de dependências (`@Configuration`), processamento assíncrono (`@EnableAsync`), Filtros do Spring Security e configurações dinâmicas de CORS.

---

## ⚙️ Variáveis de Ambiente e Configuração

O sistema foi projetado para ler variáveis de ambiente do Sistema Operacional no Back-end e utilizar configuração em tempo de execução no Front-end.

### Backend (Variáveis Obrigatórias no SO)
Para o Spring Boot iniciar corretamente, garanta que as variáveis abaixo existam no seu ambiente (elas alimentam o `application.properties` dinamicamente):

```env
# Servidor e Banco de Dados
SERVER_PORT=8080
DB_PORT=3306
DB_USER=root
DB_PASSWORD=sua_senha

# Segurança e Sessão
JWT_SECRET=chave_secreta_super_segura_para_o_jwt_do_tg_manager
JWT_EXPIRATION_MS=1500000  # (25 minutos)
CORS_ORIGINS=http://localhost:3000,http://localhost:5173

# E-mail (SMTP)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=seu_email_remetente@gmail.com
SMTP_PASSWORD=sua_senha_de_app
SMTP_FROM=tgmanager@cps.sp.gov.br

# Sistema de Arquivos
FILE_UPLOAD_DIR=C:/uploads/tg_manager  # (No Linux use /var/www/uploads/...)

```

### Frontend (Configuração Dinâmica)

O projeto aboliu o uso de arquivos `.env` em favor do padrão **Runtime Config**, permitindo mudanças de ambiente sem necessidade de recompilar o código React.

Para configurar a conexão da interface (tanto local quanto em produção), edite o arquivo `public/config.js` na pasta do frontend:

```javascript
// public/config.js
window.APP_CONFIG = {
    // Endereço e porta do seu backend
    API_URL: "http://localhost:8080" 
};

```

*(Nota: Não adicione `/api` ao final da URL. O cliente HTTP interno do sistema injetará o sufixo automaticamente em todas as requisições).*

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos

* **Java Development Kit (JDK) 25**
* Node.js (v18+) e NPM ou Yarn
* MySQL Server rodando localmente (porta padrão 3306)

### 1. Rodando o Backend

1. Abra o terminal e navegue até a pasta `back_end`.
2. Certifique-se de que o MySQL está rodando e as variáveis de ambiente (acima) estão exportadas na sua máquina.
3. Execute o comando do Maven Wrapper para baixar as dependências e iniciar o servidor:
* **Windows**: `.\mvnw spring-boot:run`
* **Linux/Mac**: `./mvnw spring-boot:run`


4. A API estará disponível na porta definida (ex: `http://localhost:8080`).

### 2. Rodando o Frontend

1. Abra um novo terminal e navegue até a pasta `front_end`.
2. Instale as dependências:

```bash
   npm install

```

3. Inicie o servidor de desenvolvimento do Vite:

```bash
   npm run dev

```

4. A aplicação estará disponível no seu navegador (geralmente em `http://localhost:5173`).

---

## 📦 Build e Deploy (Produção)

O sistema está preparado para deploy em ambientes corporativos e redes locais.

### 1. Build do Backend

Na pasta do backend, empacote o projeto usando o Maven:

```bash
mvn clean package

```

O arquivo final será gerado em `target/sistema.gerenciador.tg-1.0.0.jar`.
Defina as variáveis de ambiente listadas acima no servidor oficial e execute:

```bash
java -jar target/sistema.gerenciador.tg-1.0.0.jar

```

### 2. Build do Frontend

Na pasta do frontend, gere os arquivos estáticos:

```bash
npm run build

```

O código compilado (HTML/CSS/JS puros) será gerado na pasta `dist/`.

**Configuração no Servidor:**
Basta hospedar a pasta `dist` em um servidor web (Apache/Nginx). Se o IP da API mudar, apenas edite o arquivo `config.js` solto dentro da pasta `dist/` com o novo endereço, e o sistema estará conectado imediatamente.

> 📄 **Para instruções detalhadas para equipes de infraestrutura, além do script SQL de Carga Inicial (`CARGA_INICIAL.sql`), consulte o arquivo `GUIA_DE_DEPLOY.md` anexo a este repositório.**

---

## 🗺️ Principais Rotas da API (Endpoints REST)

*(A maioria das rotas exige o cabeçalho `Authorization: Bearer <token>`)*

* **Autenticação & Segurança (`/api/auth`)**
* `POST /login`: Autentica o usuário e devolve o Token JWT.
* `POST /recuperar-senha`: Solicita o código OTP via e-mail.
* `POST /redefinir-senha`: Valida o código OTP e altera a senha da conta.


* **Gestão de Alunos (`/api/alunos`)**
* `POST /importar`: Processa a criação de múltiplos alunos via arquivo `.xlsx`.
* `GET /sem-grupo`: Lista alunos ativos no semestre que ainda não formaram grupo.


* **Gestão da Diretoria (`/api/diretores`)**
* `GET /atual`: Busca o mandato do diretor vigente.
* `POST /atribuir`: Atribui um novo mandato a um professor (recebendo a assinatura oficial em formato Base64 para os certificados).
* `POST /retirar`: Encerra o mandato vigente (Auditoria via Soft-delete/Inativação de status).


* **Gestão de Professores (`/api/professores`)**
* `POST /`: Cadastro individual de professores (Orientador, Coordenador, etc).
* `GET /cargo/{cargo}`: Lista professores baseados em seu papel no sistema.


* **Turmas & Disciplinas (`/api/turmas`)**
* `POST /`: Cria uma nova turma para o semestre corrente.
* `POST /finalizar`: Encerra turmas ativas em lote, trancando as ações do semestre e preservando o histórico acadêmico (*Soft State*).


* **Grupos de TG (`/api/grupos`)**
* `POST /`: Alunos formam um novo grupo de TG, definindo tema e membros.
* `POST /enviar-tg`: Upload do arquivo PDF contendo o trabalho final do grupo.
* `GET /orientador`: Retorna a visão do Orientador sobre os grupos que ele supervisiona.


* **Bancas de Avaliação (`/api/bancas`)**
* `POST /marcar`: Orientador agenda a data e compõe os membros. (Dispara convite assíncrono por e-mail com o trabalho em anexo).
* `GET /{id}/ata/download`: Geração sob demanda da **Ata da Banca** em PDF.
* `POST /avaliar`: Atribui as notas da banca. Dispara o Evento de Domínio que gera os **Certificados de Participação em PDF** (com assinatura do diretor) e os envia por e-mail automaticamente.



---

*Desenvolvido como projeto de Trabalho de Graduação.*

*Desenvolvido por: Kawan Serafim de Souza, Thiago Silva Antenor e Felype Dantas dos Santos.*
