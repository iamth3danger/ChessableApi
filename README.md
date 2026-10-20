# Chessable

A message-driven backend that turns a chess player's own games into personalized
tactics puzzles.

Instead of serving generic puzzles, it pulls a user's real game history from the
[Chess.com public API](https://www.chess.com/news/view/published-data-api),
scans every position for tactical patterns the player had available, verifies
each candidate against the Stockfish engine, and keeps only the ones that hold
up. The result is a puzzle set built from mistakes the player actually made.

**Status:** work in progress. The ingestion, tactic-detection, mate-detection,
and engine-verification stages are working end to end; puzzle delivery and the
front end are still being built.

---

## Why it's built this way

Tactic detection is cheap; engine verification is expensive. A single game has
~80 positions, and each one may produce several candidate tactics, but confirming
that a candidate is really winning means handing the position to Stockfish and
waiting.

Doing that synchronously inside a web request would be hopeless, so the work is
split across services that communicate over RabbitMQ. Candidate generation runs
ahead at full speed, engine verification drains its queue at whatever rate the
hardware allows, and the API never blocks on either. Each stage can be scaled or
restarted independently.

---

## Architecture

Six Maven modules — three runnable Spring Boot services, three shared libraries.

```
                   ┌──────────────┐
  HTTP ──────────► │     api      │  REST layer. Accepts accounts and
                   └──────┬───────┘  puzzle requests, publishes jobs,
                          │          serves finished puzzles.
                    accountQueue
                          ▼
                   ┌──────────────┐
                   │    worker    │  Orchestrator.
                   └──┬────────┬──┘
          ┌───────────┘        └───────────┐
          │ 1. fetch games                 │ 2. generate candidates
          ▼                                ▼
   Chess.com API                   tactic-library
   (PGN ingestion)                 (pattern detection)
                                           │
                                   tactic.queue / mate.queue
                                           ▼
                                   ┌──────────────┐
                                   │  stockfish   │  Engine pool, 10 concurrent
                                   └──────┬───────┘  UCI processes.
                                          │
                              tactic.verification.queue
                              mate.response.queue
                                          ▼
                                   ┌──────────────┐
                                   │    worker    │  Persists confirmed
                                   └──────┬───────┘  puzzles.
                                          ▼
                                     PostgreSQL
```

### Runnable services

| Module | What it does |
| --- | --- |
| **`api`** | Spring Boot REST API on port 8080. Account CRUD, puzzle retrieval, missed-mate endpoints, and a producer that enqueues analysis jobs. |
| **`worker`** | The orchestrator. Fetches games from Chess.com, walks each position to generate tactic and mate candidates, publishes them for verification, and persists whatever comes back confirmed. |
| **`stockfish`** | Owns the engine. Maintains a pool of `Stockfish` processes in a `BlockingQueue` and answers verification requests with `concurrency = "10"` listeners, so ten positions are analyzed at once without ever exceeding the pool. |

### Shared libraries

| Module | What it does |
| --- | --- |
| **`library`** | The shared domain — JPA entities (`Account`, `AccountGame`, `Puzzle`, `AccountPuzzle`, `MissedMate`), repositories, service layer, the RabbitMQ message contracts both sides serialize against, and the Chess.com PGN fetcher. |
| **`tactic-library`** | Pure chess logic, no Spring. Detects forks, pins, positional pins, skewers, and discovered attacks. |
| **`instance`** | Small value types shared across module boundaries (`TacticInstance`, `PieceMask`, task/engine status enums). |

### How tactic detection works

`tactic-library` is the part I'd point at first. Rather than hardcoding each
pattern, it builds on a ray abstraction: `AbstractRayGenerator` with
`Horizontal`, `Vertical`, and `Diagonal` implementations produce the lines a
sliding piece controls from a square, and `RayFactory` picks the right set for a
given piece.

Every tactic is then expressed in terms of those rays. A skewer and a pin are the
same geometry read in opposite order — high-value piece in front versus behind —
so both fall out of one traversal. Patterns implement a common `Tactic`
interface and are constructed through `TacticFactory`, which keeps adding a new
pattern to a single class rather than a rewrite.

---

## Tech stack

- **Java 24** / **Spring Boot 3.5.5**
- **RabbitMQ** — message broker between all three services
- **PostgreSQL** with Spring Data JPA / Hibernate, HikariCP pooling
- **Stockfish** driven over UCI through `ProcessBuilder`
- **Jackson** for message and PGN serialization
- **Maven** (per-module builds)

---

## Running it

### Prerequisites

- JDK 24 (every module sets the compiler release to 24)
- Maven 3.8+
- PostgreSQL 14+ with a database and user created
- RabbitMQ 3.x running on `localhost:5672`
- Stockfish installed at `/usr/games/stockfish`
  (`sudo apt install stockfish` on Debian/Ubuntu)

### 1. Configure credentials

Credentials come from a `.env` file at the repo root, which is gitignored:

```bash
cp .env.example .env
```

Then edit `.env` with your database details. Each service loads it through
`spring.config.import` in its `application.properties`, so no other wiring is
needed. Everything has a sensible default except `DB_PASSWORD`, which is
deliberately left blank so a missing config fails loudly at startup instead of
silently connecting somewhere unexpected.

### 2. Build

`library`, `tactic-library`, and `instance` are dependencies of the services, so
they go into your local Maven repo first:

```bash
for m in library tactic-library instance; do (cd $m && mvn clean install); done
```

### 3. Start the services

Each service runs in its own terminal. `stockfish` and `worker` should be up
before `api` so no published job sits unconsumed:

```bash
cd stockfish && mvn spring-boot:run
cd worker    && mvn spring-boot:run
cd api       && mvn spring-boot:run
```

Hibernate is set to `ddl-auto=update`, so the schema is created on first run.
RabbitMQ exchanges, queues, and bindings are declared in each service's
`RabbitConfig`, so the topology sets itself up on connect.

---

## API

Base path `/chessable`.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/accounts` | List all accounts |
| `GET` | `/accounts/{id}` | Fetch one account |
| `POST` | `/accounts` | Create an account |
| `PUT` | `/accounts` | Update an account |
| `DELETE` | `/accounts/{id}` | Delete an account |
| `GET` | `/users/{userId}/puzzles/next` | Next unsolved puzzle |
| `GET` | `/users/{userId}/puzzles/count` | Remaining puzzle count |
| `PUT` | `/users/{userId}/puzzles/{puzzleId}/solve` | Mark solved (`SOLVED` / `PENDING` / `MISSED`) |
| `POST` | `/users/{userId}/puzzles/enqueue` | Queue a fresh analysis run |
| `GET` | `/users/{userId}/missed-mates` | Mates the player missed |
| `GET` | `/users/{userId}/missed-mates-retry` | Re-serve missed mates for retry |

---

## Roadmap

- [ ] Front end for solving puzzles
- [ ] Puzzle difficulty rating tied to the player's own rating band
- [ ] Lichess ingestion alongside Chess.com
- [ ] Parent POM so the whole project builds in one command
- [ ] Docker Compose for Postgres, RabbitMQ, and the services
- [ ] Make the Stockfish binary path configurable instead of hardcoded
