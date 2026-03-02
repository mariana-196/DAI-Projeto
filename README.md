# Plataforma Unificadora de Mobilidade - TUB

Este repositório contém o desenvolvimento técnico da Plataforma de Gestão Urbana (PGU) para os *Transportes Urbanos de Braga (TUB)*. O projeto visa agregar, monitorizar e gerir sistemas operacionais para promover uma gestão integrada e orientada por dados.

## Verticais Selecionadas
O nosso grupo foca o desenvolvimento nos seguintes módulos específicos do caderno de especificações técnicas:
* **3.3 Integração com Plataforma de Bilhética:** Ingestão e tratamento de dados de validação de títulos, perfis de utilização e estatísticas de uso da rede.
* **3.4 Integração com Contagem de Passageiros:** Monitorização em tempo real do fluxo de entradas e saídas e padrões de ocupação.
* **3.5 Integração com Painéis de Mensagem Dinâmica (DMS):** Visualização do estado operacional e conteúdos de painéis e-paper, LED e TFT.

## Stack Tecnológica e Standards
Para garantir a interoperabilidade e modularidade exigida pelo cliente, adotamos os seguintes padrões:
* **Arquitetura:** Monolítica modular orientada a serviços (Service-Oriented) com deploy em contentores **Docker**.
* **Modelo de Dados:** Harmonização seguindo **Smart Data Models** (FIWARE) e protocolo **NGSI-LD**.
* **Segurança:** Implementação de protocolos **TLS 1.2+**, encriptação **AES-256** e conformidade com a **Diretiva NIS2**.

## Estrutura do Repositório
* `/src`: Código fonte da plataforma e adaptadores das verticais.
* `/docs`: Documentação técnica e esquemas de dados JSON (conforme o modelo **OpenAPI**).
* `/deploy`: Ficheiros de configuração para o ambiente de execução (Build inicial).

## Equipa de Desenvolvimento
* **Chefe de Programadores:** Mariana Sousa
* **Programadores:** Gonçalo Marques; Gonçalo Fernandes; Marta Oliveira; Rafael Marques; Renato Sousa
