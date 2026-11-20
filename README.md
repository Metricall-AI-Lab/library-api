# library-api

A small Spring Boot 3 service for lending books, with Postgres, Flyway migrations and seed data.
It exists to try [Forkstage](https://forkstage.com): a disposable copy of the whole stack for every task.

## The API

| Method | Path | What it does |
|---|---|---|
| GET | `/health` | `{"status":"ok"}` |
| GET | `/books` | every book, seeded with four |
| GET | `/books/{id}` | one book, or 404 |
| POST | `/books` | `{"title","author"}` → 201 |
| POST | `/books/{id}/lend` | `{"to":"name"}` marks it lent |
| POST | `/books/{id}/return` | clears the loan |

One known bug is left in on purpose, so an agent has something to find: lending a book that is
already out succeeds and silently overwrites who has it (`BookRepository.lend`). The smoke tests
do not cover it.

## Run it locally

```bash
docker compose up --build
curl localhost:8080/books
```

## Try it with Forkstage

```bash
npm install -g forkstage
forkstage login       # once per machine
forkstage init        # writes forkstage.yaml from the compose file
forkstage bake        # builds and snapshots the stack, about a minute the first time
forkstage up          # a fork in about 100 ms, with a public address
forkstage test        # runs tests/smoke.sh inside the fork
forkstage down        # strike it; the proof stays
```

Configuration: the app reads `DATABASE_URL` (`postgres://user:password@host:port/db`) and turns
it into the Spring datasource settings; `PORT` overrides 8080.
