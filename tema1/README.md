# 💸 FinSync API

> API REST de gestão financeira pessoal com categorização inteligente de gastos e análise por IA (Gemini).

---

## 📌 Sobre o Projeto

O **FinSync** é uma API backend desenvolvida em **Java com Spring Boot** que permite registrar, categorizar e analisar transações financeiras. O diferencial é a integração com a IA do **Google Gemini** via **Spring AI**, que responde perguntas sobre o padrão de consumo do usuário com base nas suas transações reais.

---

## ✨ Funcionalidades

- 📥 **Ingestão de transações** a partir de fontes externas (simuladas), com deduplicação automática
- 🧠 **Categorização automática** de gastos por palavras-chave (`FOOD`, `TRANSPORT`, `ENTERTAINMENT`, `OTHER`)
- 🤖 **Insights financeiros via IA** — faça perguntas em linguagem natural sobre seus gastos
- 🔐 **Autenticação HTTP Basic** com Spring Security
- 👤 **Cadastro e login de usuários**
- 📊 **Listagem de transações** por usuário, ordenadas por data

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/financas/tema1/
├── ai/                  # DTOs de request/response da IA
├── application/         # Estratégias de categorização e normalização
│   ├── CategoryStrategy.java
│   ├── DefaultCategoryStrategy.java
│   └── TransactionNormalizer.java
├── config/              # Configuração de segurança (Spring Security)
├── controller/          # Controllers REST
│   ├── AuthController.java
│   ├── FinancialAiController.java
│   ├── InsightController.java
│   └── transaction/FinancialTransactionController.java
├── domain/              # Entidades JPA (User, Transaction, Category)
├── DTO/                 # Objetos de transferência de dados
├── repository/          # Interfaces JPA
├── service/             # Lógica de negócio
│   ├── FinancialAiService.java
│   ├── IngestionService.java
│   └── UserService.java
└── transaction/         # TransactionType enum
```

---

## 🛠️ Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 3 | Framework web |
| Spring Security | Autenticação HTTP Basic |
| Spring AI | Integração com Google Gemini |
| Spring Data JPA | Persistência de dados |
| H2 Database | Banco em memória (dev) |
| JUnit 5 + AssertJ | Testes unitários |
| Virtual Threads | Ingestão paralela de transações |

---

## 🚀 Como Rodar

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Chave de API do Google Gemini

### Configuração

No arquivo `src/main/resources/application.properties`, configure sua chave da API:

```properties
spring.ai.model.chat=google-genai
spring.ai.google.genai.api-key=${GOOGLE_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
spring.ai.google.genai.chat.options.temperature=0.3
```

### Executando

```bash
# Clone o repositório
git clone https://github.com/lauluah/FinSync-API.git
cd FinSync-API

# Checkout na branch main
git checkout main

# Rode o projeto
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## 📡 Endpoints

### Autenticação

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/auth/register` | Cadastra novo usuário | ❌ |
| `POST` | `/auth/login` | Login HTTP Basic | ❌ |

#### Exemplo — Cadastro
```http
POST /auth/register
{
  "email": "usuario@email.com",
  "password": "senha123"
}
```

---

### Transações

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/transactions` | Cria uma transação | ✅ |
| `GET` | `/api/transactions/user/{userId}` | Lista transações do usuário | ✅ |

#### Exemplo — Criar transação
```http
POST /api/transactions
{
  "userId": 1,
  "description": "iFood - Jantar",
  "amount": "45.90",
  "category": "FOOD",
  "date": "2026-04-20",
  "type": "EXPENSE"
}
```

---

### IA / Insights

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/ai/insights` | Pergunta à IA sobre seus gastos | ✅ |
| `POST` | `/insights/resume` | Resumo do padrão de consumo | ✅ |

#### Exemplo — Pergunta à IA
```http
POST /api/ai/insights
{
  "question": "Quanto gastei com alimentação este mês?"
}
```

#### Exemplo — Resposta da IA
```json
{
  "userId": 1,
  "question": "Quanto gastei com alimentação este mês?",
  "answer": "Com base nas suas transações, você gastou R$ 345,90 em alimentação em abril de 2026, distribuídos em 3 transações na categoria FOOD."
}
```

---

## 🧠 Como a Categorização Funciona

A `DefaultCategoryStrategy` normaliza a descrição da categoria por palavras-chave:

| Categoria | Palavras-chave reconhecidas |
|---|---|
| `FOOD` | `food`, `alimentacao`, `drink` |
| `TRANSPORT` | `transport`, `transporte`, `uber` |
| `ENTERTAINMENT` | `entertain`, `lazer`, `jogo` |
| `OTHER` | Qualquer outro valor (fallback) |

> A busca é **case-insensitive** e por **substring**, então `"Food & Drinks"` → `FOOD`, `"Uber99"` → `TRANSPORT`, etc.

---

## 🧪 Testes

```bash
./mvnw test
```

Os testes unitários cobrem:
- `DefaultCategoryStrategy` — todos os casos de normalização de categoria
- `FinancialAiService` — construção de contexto e chamada à IA

---

## 🔒 Segurança

A API utiliza **HTTP Basic Authentication**. Todas as rotas sob `/api/**` exigem autenticação. As rotas `/auth/register` e `/auth/login` são públicas.

Senhas são armazenadas com hash **BCrypt**.

---

## 📄 Licença

Este projeto foi desenvolvido como projeto de estudo. Livre para uso e modificação.

---

<p align="center">Feito com ☕ e Spring Boot</p>
