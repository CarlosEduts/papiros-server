# Papiros Server

O **Papiros Server** é a API central do ecossistema Papiros. Desenvolvido com Java e Spring Boot, o servidor gerencia a persistência de dados, autenticação de usuários e as regras de negócio para a plataforma de artigos.

> **Status:** 🛠️ Em Desenvolvimento

## 🚀 Tecnologias Utilizadas

* **Java 17+** (ou versão superior)
* **Spring Boot 3**
* **Spring Security & JWT** (Para autenticação e autorização)
* **Maven** (Gerenciador de dependências)

---

## 🔐 Autenticação

A API utiliza **JSON Web Tokens (JWT)** para proteger rotas sensíveis.

1. O usuário realiza o registro e o login.
2. O login retorna um token de acesso.
3. O token deve ser enviado no Header de todas as requisições protegidas:
   `Authorization: Bearer <seu_token>`

---

## 📑 Documentação da API

### Autenticação (`/auth`)

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/register` | Cria uma nova conta de usuário. |
| `POST` | `/auth/login` | Autentica o usuário e retorna o token JWT. |

**Exemplo de Corpo (Register):**

```json
{
  "name": "Nome do Usuário",
  "username": "user123",
  "password": "password!123"
}
```

**Exemplo de Corpo (Login):**

```json
{
  "username": "user123",
  "password": "password!123"
}
```

---

### Artigos (`/articles`)

Todas as rotas abaixo (exceto se configurado o contrário) exigem o Header `Authorization`.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/articles` | Lista todos os artigos. |
| `GET` | `/articles/{id}` | Busca os detalhes de um artigo específico. |
| `POST` | `/articles` | Cria um novo artigo. |
| `PUT` | `/articles/{id}` | Atualiza o título ou conteúdo de um artigo. |
| `DELETE` | `/articles/{id}` | Remove um artigo permanentemente. |
| `POST` | `/articles/{id}/like` | Registra um "curtir" no artigo. |

**Exemplo de Criação de Artigo:**

```json
{
  "title": "Título do Artigo",
  "content": "Conteúdo completo aqui..."
}
```

---

### Comentários (`/articles/{id}/comments`)

Gerencie a interação em artigos específicos através dos IDs.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/articles/{id}/comments` | Adiciona um comentário a um artigo. |
| `PUT` | `/articles/{id}/comments/{commentId}` | Edita um comentário existente. |
| `DELETE` | `/articles/{id}/comments/{commentId}` | Remove um comentário. |

**Exemplo de Criação de Comentário:**

```json
{
  "content": "Conteúdo do comentário aqui..."
}
```
