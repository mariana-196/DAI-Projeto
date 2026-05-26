# Plataforma de Gestão Urbana - TUB

Este repositório contém o desenvolvimento técnico da Plataforma de Gestão Urbana (PGU) para os *Transportes Urbanos de Braga (TUB)*. O projeto visa agregar, monitorizar e gerir sistemas operacionais para promover uma gestão integrada e orientada por dados.

## Verticais Selecionadas
O nosso grupo foca o desenvolvimento nos seguintes módulos específicos do caderno de especificações técnicas:
* 3.3 Integração com Plataforma de Bilhética:** Ingestão e tratamento de dados de validação de títulos, perfis de utilização e estatísticas de uso da rede.

* 3.4 Integração com Contagem de Passageiros:** Monitorização em tempo real do fluxo de entradas e saídas e padrões de ocupação.

* 3.5 Integração com Painéis de Mensagem Dinâmica (DMS):** Visualização do estado operacional e conteúdos de painéis e-paper, LED e TFT.

## Estrutura do Repositório
* `/Backend`: Código-fonte da API (Java/Spring Boot) e ficheiros de configuração da Base de Dados (Docker).
* `/Frontend`: Aplicação cliente (HTML/JS/Vue) com as interfaces gráficas da plataforma.
* `/.vscode`: Configurações do ambiente de desenvolvimento.


##  Como Executar o Projeto Localmente

### 1. Iniciar a Base de Dados (Docker) - Primeiramente abrir o Docker Desktop
1. Aceder à pasta `Backend/tub-backend/database`.
2. Abrir o terminal integrado no VS Code, clique com o botão direito sobre a pasta e selecione a opção "Open in Integrated Terminal".
3. Executar o comando: `docker-compose up -d`

### 2. Iniciar o Servidor Backend (Spring Boot)
1. Localize a classe principal da aplicação em `Backend/tub-backend/src/main/java/AppJava`.
2. Inicie a execução da classe (botão *Run/Play* no IDE). 
3. Aguarde pela mensagem `Started App` na consola.

### 3. Iniciar a Interface Cliente (Frontend)
1. Abra a pasta `Frontend` no VS Code.
2. Localizar um ficheiro HTML da interface (por exemplo: auditoria.html, bilhetica.html, etc.).
3.	Clicar com o botão direito sobre o ficheiro e selecionar a opção "Open with Live Server" (requer a extensão Live Server instalada).
4.	A aplicação (Frontend) será automaticamente aberta no navegador web predefinido, totalmente operacional e ligada ao Backend.

## Equipa de Desenvolvimento
* Chefe de Programadores:** Mariana Sousa
* Programadores:** João Monteiro; Gonçalo Marques; Marta Oliveira; Renato Sousa


