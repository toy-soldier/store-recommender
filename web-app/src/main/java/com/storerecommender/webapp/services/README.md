# Agent

## Overview

The **Agent** is the **intelligence layer** of the Store Recommender System.  It is responsible for interpreting user-submitted grocery lists and transforming them into **structured product recommendations**.

The agent acts as an orchestrator that coordinates:

* Natural-language parsing
* Product matching and recommendation
* Inventory and pricing enrichment

All AI-related logic is intentionally isolated within this component.

---

## Features

The Agent is the service layer of the **Web App**, responsible for:

* Parsing free-form grocery lists into structured line items
* Filtering the store catalog to a relevant subset per grocery item
* Recommending matching store products
* Fetching inventory and pricing details from the API Server
* Producing a final, structured recommendation payload
* Supporting **dummy / mocked execution modes** for development and testing

---

## Architecture

The agent sits behind the Web App and contains the application's business logic:

```bash
User
  ↓
Web App (uploads and recommendations UI)
  └── Agent (service layer inside web-app: parsing, fuzzy filtering, LLM calls)
        ↓
API Server (product catalog / inventory)
```

The agent does **not** store data persistently and does **not** render UI.
Its sole responsibility is **pipeline execution**.

---

## Data Flow Summary

1. On startup, `InventoryService` loads and caches the store catalog from the API Server.
2. The Web App sends the uploaded grocery list contents to `AgentService`.
3. `ParserService` parses the grocery list into structured line items using an LLM.
4. `FuzzyFilterService` filters the cached catalog to a relevant subset for each parsed line item.
5. `RecommenderService` recommends matching products from this subset using an LLM.
6. `InventoryService` fetches pricing and inventory data from the API Server for each recommended SKU.
7. `AgentService` returns the structured response to the Web App.
8. The Web App renders the final confirmation page for the user.

This flow enforces a clean separation between **reasoning**, **data access**, and **presentation**.

---

## Service Design

`AgentService` is intentionally thin — it chains service calls in order and returns the result. It contains no business logic of its own.

Each service owns its domain end-to-end:

* **`InventoryService`** — all product and inventory operations: catalog caching at startup and enrichment after recommendations. The only place `ApiServerClient` is injected.
* **`FuzzyFilterService`** — stateless pre-LLM catalog pruning.
* **`ParserService`** — LLM-based parsing; owns the parser prompt and model selection.
* **`RecommenderService`** — LLM-based recommendation; owns the recommender prompt and model selection.

Keeping `AgentService` dumb means business logic stays in the service that understands it. It also keeps `ApiServerClient` injected in exactly one place — if the orchestrator owned enrichment, it would have to reach into inventory concerns directly.

---

## Tech Stack

* **OpenAI API** — structured-output LLM calls for parsing and recommendations
* **Spring AI** — for interacting with the LLMs
* **Spring Retry** — automatic retry with backoff for LLM and API Server calls
* **FuzzyWuzzy** — fuzzy string matching for catalog filtering

---

## Environment Setup

To enable OpenAI-powered parsing and recommendations, ensure the environment file exists at the project root:

```bash
.env
```

with the following entry:

```bash
OPENAI_API_KEY=<your OpenAI API key>
```

If `OPENAI_API_KEY` is absent or set to `dummy`, the agent runs in **dummy mode**.  In this mode, the agent returns **pre-generated parser and recommender responses** that were captured from real LLM executions during development.

## Dummy Mode

In **dummy mode**, responses are selected based on the uploaded grocery list filename.  The following table summarizes the mapping:

|                        | Grocery Lists                   | Parser Responses                   | Recommender Responses                   |
|------------------------|---------------------------------|------------------------------------|-----------------------------------------|
| Samples / responses in | *resources/dummy/grocery-lists* | *resources/dummy/parser-responses* | *resources/dummy/recommender-responses* |
| Sample 1               | list01.txt                      | list01.json                        | list01.json                             |
| Sample 2               | list02.txt                      | list02.json                        | list02.json                             |
| Sample 3               | list03.txt                      | list03.json                        | list03.json                             |
| Sample 4               | list04.txt                      | list04.json                        | list04.json                             |
| Sample 5               | list05.txt                      | list05.json                        | list05.json                             |
| Sample 6               | list06.txt                      | list06.json                        | list06.json                             |
| Sample 7               | list07.txt                      | list07.json                        | list07.json                             |

If no matching response file exists for the uploaded filename, the agent throws an error.

---

## Model Architecture

The agent uses a **two-stage LLM architecture** designed to mirror production-grade AI pipelines.

### 1. Parsing Model (`ParserService`)

* Interprets free-form grocery list text.
* Extracts structured fields such as product name, quantity, and unit.
* Uses **schema-validated structured outputs** to enforce a consistent structure.
* Precision-critical stage — prioritizes correctness over cost.

### 2. Recommendation Model (`RecommenderService`)

* Operates on structured parsed input and a constrained catalog subset.
* Selects the best matching SKUs and assigns confidence scores.
* Uses **structured outputs**, ensuring strict contracts between components.
* Lower reasoning requirements due to constrained input and candidate set.

In production, this stage would typically be replaced by a **vector database or RAG pipeline**.

---

## Error Handling & Design Notes

* External service failures (LLMs or API Server) are retried automatically via Spring Retry before propagating as errors.
* If an individual enrichment call fails, the suggestion is logged and skipped —
  the result contains fewer suggestions but remains valid.
* Model refusals are considered extremely unlikely for this domain and are surfaced
  at the service boundary if they occur.

This approach favors **clarity and robustness** over excessive defensive branching.

---

## Testing

Unit tests for the Agent are located together with the tests for the Web App.  Tests cover:

* Grocery list parsing
* Fuzzy catalog filtering
* Recommendation generation
* Inventory enrichment logic
* Dummy / mocked execution paths

Mocks are used to isolate LLM calls and API Server dependencies.
