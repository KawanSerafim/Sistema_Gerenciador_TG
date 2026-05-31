# Sistema Gerenciador de Trabalho de Graduação 🎓

Um sistema completo desenvolvido para modernizar e automatizar o ciclo de vida do Trabalho de Graduação (TG). O sistema conecta Alunos, Professores Orientadores, Coordenadores e Professores da Disciplina em uma plataforma única, gerenciando desde a importação de alunos e formação de grupos até o agendamento de bancas, notificação automática e geração de atas oficiais.

## 🛠️ Tecnologias Utilizadas

### Backend
* **Java 17+** com **Spring Boot 3**
* **MySQL** (Banco de Dados Relacional)
* **Spring Data JPA** / Hibernate
* **Spring Security** + **JWT** (JSON Web Token) para autenticação e autorização
* **JavaMailSender** para envio de e-mails transacionais assíncronos (códigos OTP, convites de banca e anexos)
* **Apache POI** para leitura e processamento de planilhas Excel (Importação de alunos)
* **Thymeleaf** operando como motor de template interno (processamento de corpos de e-mail e estruturação de documentos HTML)
* **OpenHTMLToPDF** para a conversão de templates HTML em documentos PDF nativos e responsivos sob demanda (ex: Ata da Banca)

### Frontend
* **React.js** com **Vite**
* **JavaScript** / JSX
* **React Bootstrap** + CSS customizado para estilização responsiva
* **React Hook Form** + **Zod** para gerenciamento e validação de formulários complexos
* **Implementação própria de  cliente HTTP** configurado com interceptadores (interceptors) globais para injeção de JWT e tratamento de downloads de arquivos binários (Blob).

---

## 🏛️ Arquitetura do Backend

O backend foi construído seguindo os princípios rigorosos da **Arquitetura Limpa (Clean Architecture)** e **Domain-Driven Design (DDD)**, garantindo um código altamente testável, manutenível e isolado de frameworks. 

O projeto está dividido nas seguintes camadas principais:

1. **Domínio (`dominio`)**: O coração do software. Contém as Regras de Negócio, Entidades puras do Java (ex: `Aluno`, `Turma`, `GrupoTg`), Objetos de Valor (Value Objects) e as assinaturas (interfaces) dos Repositórios. **Não possui nenhuma dependência do Spring**.
2. **Aplicação (`aplicacao`)**: Contém os **Casos de Uso** (ex: `FinalizarTurmasCaso`, `MarcarBancaCaso`). Orquestra o fluxo de dados, executa as lógicas de negócio e dispara **Eventos de Domínio** (Observer Pattern) para lidar com efeitos colaterais (como envio de e-mails) sem travar a transação principal.
3. **Infraestrutura (`infraestrutura`)**: A borda externa do sistema. Aqui vivem as implementações técnicas:
   * **API / Controladores**: Endpoints REST (`@RestController`).
   * **Persistência / JPA**: Modelos de banco de dados mapeados para tabelas, Mapeadores (Mappers) e as implementações reais dos Repositórios usando Spring Data.
   * **Adaptadores de Saída**: Implementações para envio de e-mail (`JavaMailSender`), acesso ao sistema de arquivos (upload de TG) e geração de PDFs.
   * **Configurações**: Injeção de dependências (`@Configuration`), processamento assíncrono (`@EnableAsync`) e Filtros do Spring Security.

---

## ⚙️ Variáveis de Ambiente e Configuração

Para rodar o projeto localmente, você precisará configurar os ambientes do Backend e do Frontend.

### Backend (`back_end/src/main/resources/application.properties`)
Crie ou configure o arquivo `application.properties` com as seguintes variáveis estruturais:

```properties
# Configurações do Banco de Dados (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/tg_manager_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=seu_usuario_mysql
spring.datasource.password=sua_senha_mysql

# Configuração do Hibernate (Geração automática de tabelas)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuração JWT (Token de Segurança)
# Use uma string longa e segura para o secret
api.security.token.secret=chave_secreta_super_segura_para_o_jwt_do_tg_manager

# Configuração do Servidor de E-mail (Exemplo com Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email_remetente@gmail.com
spring.mail.password=sua_senha_de_app_do_google
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

```

### Frontend (`front_end/.env`)

Baseado no arquivo `.env.example`, crie um arquivo `.env` na raiz da pasta `front_end` e aponte para a URL do seu backend local:

```env
VITE_API_URL=http://localhost:8080/api

```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

* Java Development Kit (JDK) 17 ou superior
* Node.js (v18+) e NPM ou Yarn
* MySQL Server rodando localmente (porta padrão 3306)

### 1. Rodando o Backend

1. Abra o terminal e navegue até a pasta `back_end`.
2. Certifique-se de que o MySQL está rodando e as credenciais batem com as do seu `application.properties`.
3. Execute o comando do Maven Wrapper para baixar as dependências e iniciar o servidor:

* **Windows**: `.\mvnw spring-boot:run`
* **Linux/Mac**: `./mvnw spring-boot:run`

4. A API estará disponível e escutando na porta `http://localhost:8080`.

### 2. Rodando o Frontend

1. Abra um novo terminal e navegue até a pasta `front_end`.
2. Instale as dependências do ecossistema Node:

```bash
npm install

```

3. Inicie o servidor de desenvolvimento do Vite:

```bash
npm run dev

```

4. A aplicação estará disponível no seu navegador (geralmente em `http://localhost:5173`).

---

## 🗺️ Principais Rotas da API (Endpoints REST)

Abaixo estão os principais conjuntos de rotas expostas pelo sistema (Todas as rotas, exceto as de login e recuperação de senha, exigem o cabeçalho `Authorization: Bearer <token>`):

* **Autenticação & Segurança (`/api/auth`)**
* `POST /login`: Autentica o usuário e devolve o Token JWT.
* `POST /recuperar-senha`: Solicita o código OTP via e-mail.
* `POST /redefinir-senha`: Valida o código OTP e altera a senha da conta.


* **Gestão de Alunos (`/api/alunos`)**
* `POST /importar`: Recebe um arquivo `.xlsx` e processa a criação de múltiplos alunos em lote.
* `GET /sem-grupo`: Lista alunos ativos no semestre que ainda não formaram grupo.


* **Gestão de Professores (`/api/professores`)**
* `POST /`: Cadastro individual de professores (Orientador, Coordenador, etc).
* `GET /cargo/{cargo}`: Lista professores baseados em seu papel no sistema.


* **Turmas & Disciplinas (`/api/turmas`)**
* `POST /`: Cria uma nova turma para o semestre corrente.
* `POST /finalizar`: Encerra turmas ativas em lote, trancando as ações do semestre e preservando o histórico acadêmico (*Soft State*).


* **Grupos de TG (`/api/grupos`)**
* `POST /`: Alunos formam um novo grupo de TG, definindo tema e membros.
* `POST /enviar-tg`: Upload do arquivo PDF contendo o trabalho final do grupo.
* `GET /orientador`: Retorna a visão do Orientador sobre os grupos que ele supervisiona (incluindo status dinâmicos e notas).
* `GET /professor-tg`: Retorna a visão panorâmica e paginada para gestão centralizada da disciplina.


* **Bancas de Avaliação (`/api/bancas`)**
* `POST /marcar`: Orientador agenda a data, hora, local e compõe os membros. (Dispara notificação assíncrona por e-mail com anexo do trabalho).
* `GET /{id}/ata/download`: Geração e download sob demanda do documento oficial (Ata da Banca) em formato PDF.
* `POST /avaliar`: Atribui as notas da banca e finaliza o ciclo de defesa do trabalho.



---

*Desenvolvido como projeto de Trabalho de Graduação.*

*Desenvolvido por: Kawan Serafim de Souza, Thiago Silva Antenor e Felype Dantas dos Santos.*