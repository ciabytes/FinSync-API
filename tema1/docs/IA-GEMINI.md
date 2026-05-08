# IA com Gemini no FinSync

Esta documentacao explica a parte de Inteligencia Artificial implementada na API para o MVP do hackathon.

Ela foi criada para ajudar o time a entender, testar, apresentar e evoluir a funcionalidade depois.

## O que foi implementado

Foi adicionada uma integracao com LLM usando Spring AI e Gemini.

A API permite cadastrar transacoes financeiras de um usuario e depois fazer perguntas em linguagem natural para a IA. A IA recupera as transacoes salvas no sistema e usa esses dados como contexto para responder.

Em resumo:

- LLM integrado via Spring AI.
- Modelo configurado: `gemini-2.5-flash`.
- Endpoint de IA: `POST /api/ai/insights`.
- RAG simples usando os dados financeiros cadastrados no proprio sistema.
- Banco PostgreSQL configurado via Supabase.

## Como o RAG funciona aqui

RAG significa Retrieval Augmented Generation.

Na pratica, neste projeto:

1. O usuario cadastra transacoes financeiras na API.
2. Quando o endpoint de IA recebe uma pergunta, o sistema busca as transacoes daquele `userId` no banco.
3. O sistema monta um contexto com receitas, despesas, saldo estimado e lista de transacoes.
4. Esse contexto e enviado para o Gemini junto com a pergunta.
5. A IA responde usando apenas os dados fornecidos pelo sistema.

Isso evita que a IA invente informacoes. Se nao houver despesas cadastradas, por exemplo, ela deve dizer que nao ha dados suficientes.

## Arquivos principais

### Configuracao do Gemini

Arquivo:

```text
src/main/resources/application.properties
```

Configuracao usada:

```properties
spring.ai.model.chat=google-genai
spring.ai.google.genai.api-key=${GOOGLE_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
spring.ai.google.genai.chat.options.temperature=0.3
```

O trecho `${GOOGLE_API_KEY}` significa:

- A API espera uma variavel de ambiente chamada `GOOGLE_API_KEY`.
- Se ela nao existir, a aplicacao nao deve conseguir chamar o Gemini corretamente.

Evite gravar chaves reais no repositorio. Mantenha apenas:

```properties
spring.ai.google.genai.api-key=${GOOGLE_API_KEY}
```

### Servico de IA

Arquivo:

```text
src/main/java/com/financas/tema1/service/FinancialAiService.java
```

Responsavel por:

- Buscar as transacoes do usuario.
- Montar o contexto financeiro.
- Enviar prompt para o Gemini usando `ChatClient`.
- Retornar a resposta da IA.

### Controller da IA

Arquivo:

```text
src/main/java/com/financas/tema1/controller/FinancialAiController.java
```

Cria o endpoint:

```text
POST /api/ai/insights
```

### Transacoes financeiras

Arquivos:

```text
src/main/java/com/financas/tema1/domain/Transaction.java
src/main/java/com/financas/tema1/repository/TransactionRepository.java
src/main/java/com/financas/tema1/transaction/FinancialTransactionController.java
src/main/java/com/financas/tema1/transaction/TransactionType.java
```

Esses arquivos representam os dados financeiros usados pela IA.

## Como rodar a API

No terminal, entre na pasta do projeto:

```powershell
cd C:\Users\55139\Documents\Hackaton\FinSync-API\tema1
```

Rode:

```powershell
.\mvnw.cmd spring-boot:run
```

Quando aparecer uma mensagem parecida com esta, a API esta no ar:

```text
Tomcat started on port 8080
```

URL base:

```text
http://localhost:8080
```

## Como testar no Postman

### 1. Criar uma receita

Metodo:

```text
POST
```

URL:

```text
http://localhost:8080/api/transactions
```

Body > raw > JSON:

```json
{
  "userId": 1,
  "description": "Salario",
  "category": "Renda",
  "amount": 5000,
  "transactionDate": "2026-05-01",
  "type": "INCOME"
}
```

### 2. Criar uma despesa

Metodo:

```text
POST
```

URL:

```text
http://localhost:8080/api/transactions
```

Body > raw > JSON:

```json
{
  "userId": 1,
  "description": "Mercado",
  "category": "Alimentacao",
  "amount": 650,
  "transactionDate": "2026-05-03",
  "type": "EXPENSE"
}
```

Outro exemplo de despesa:

```json
{
  "userId": 1,
  "description": "Streaming",
  "category": "Lazer",
  "amount": 55,
  "transactionDate": "2026-05-04",
  "type": "EXPENSE"
}
```

Valores aceitos no campo `type`:

```text
INCOME
EXPENSE
```

### 3. Listar transacoes do usuario

Metodo:

```text
GET
```

URL:

```text
http://localhost:8080/api/transactions/user/1
```

Esse endpoint deve retornar as transacoes cadastradas para o usuario `1`.

### 4. Pedir insights para a IA

Metodo:

```text
POST
```

URL:

```text
http://localhost:8080/api/ai/insights
```

Body > raw > JSON:

```json
{
  "userId": 1,
  "question": "Analise minhas transacoes e me diga onde posso economizar."
}
```

Exemplos de perguntas:

```text
Quais categorias estao consumindo mais dinheiro?
```

```text
Minha situacao financeira parece saudavel?
```

```text
Gere um resumo financeiro simples das minhas transacoes.
```

```text
O que eu poderia cortar para economizar mais?
```

## Exemplo de resposta esperada

A resposta vem neste formato:

```json
{
  "userId": 1,
  "question": "Analise minhas transacoes e me diga onde posso economizar.",
  "answer": "Com base nas transacoes fornecidas..."
}
```

O texto dentro de `answer` e gerado pelo Gemini.

## Observacoes importantes

O banco H2 usado aqui esta em memoria. Isso significa que, ao parar e iniciar a aplicacao, os dados cadastrados podem sumir. Para a demo, basta cadastrar algumas transacoes novamente antes de testar a IA.

A chave do Gemini esta configurada com fallback para facilitar a apresentacao. Para um repositorio publico ou ambiente real, remova a chave do `application.properties`, use apenas variavel de ambiente e gere uma chave nova no Google AI Studio.

## Como explicar na apresentacao

Uma explicacao curta:

```text
Implementamos um endpoint de IA com Spring AI e Gemini 2.5 Flash. O usuario faz uma pergunta em linguagem natural, a API recupera as transacoes financeiras cadastradas no banco e envia esses dados como contexto para o Gemini. Assim, a IA gera insights personalizados usando dados reais do sistema.
```

