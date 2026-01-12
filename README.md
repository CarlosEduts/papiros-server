# Papiros Server

![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen) ![License](https://img.shields.io/badge/license-MIT-blue)

O **Papiros Server** é a API RESTful central do ecossistema Papiros. Desenvolvida em Java com Spring Boot, esta
aplicação é responsável por gerenciar toda a lógica de negócios, persistência de dados e segurança da plataforma de
artigos.

O client front-end para esta API pode ser encontrado
aqui: [Papiros Client](https://github.com/CarlosEduts/papiros-client).

## Tecnologias

O projeto utiliza uma arquitetura baseada no ecossistema Spring:

**Core & Framework**

- Java (17+)
- Spring Boot
- Maven (Gerenciamento de dependências)

**Segurança**

- Spring Security
- JWT
- Spring Security Test

**Dados & Infraestrutura**

- MySQL
- Docker

**Documentação**

- Springdoc OpenAPI (Swagger UI)

## Variáveis de Ambiente

Para que a aplicação funcione corretamente, é necessário configurar as seguintes variáveis de ambiente.

| Variável      | Descrição                                    | Valor Padrão |
|:--------------|:---------------------------------------------|:-------------|
| `DB_HOST`     | Endereço do servidor MySQL (ex: `localhost`) | `localhost`  |
| `DB_NAME`     | Nome do esquema/banco de dados               | -            |
| `DB_USER`     | Usuário de autenticação do MySQL             | `root`       |
| `DB_PASSWORD` | Senha do usuário do banco de dados           | -            |
| `JWT_SECRET`  | Chave secreta para assinatura dos tokens JWT | -            |

## Documentação da API

Com a aplicação em execução, a documentação interativa (Swagger UI) pode ser acessada em:

> http://localhost:8080/swagger-ui/index.html

*(Ajuste a porta 8080 caso sua configuração seja diferente)*

## Roadmap e Status

O projeto encontra-se atualmente em estágio **Em Desenvolvimento**.

* [x] Configuração inicial do Spring Boot e Segurança
* [x] Implementação de Autenticação JWT
* [x] CRUD de Artigos, Comentários e Gostei (Regras de Negócio)
* [ ] Configuração de Docker para ambiente de Produção
* [ ] Deploy em ambiente de Produção

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

## Contato e Feedback

Obrigado por acessar este repositório!

Estou sempre aberto a sugestões e melhorias. Se você tiver alguma dúvida sobre a implementação ou quiser trocar uma
ideia sobre as tecnologias utilizadas (Spring Boot, Docker, JWT), sinta-se à vontade para entrar em contato.

Se este projeto te ajudou de alguma forma, considere deixar uma ⭐ para apoiar!