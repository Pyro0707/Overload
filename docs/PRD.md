# Product Requirements Document — Overload

**Project name:** Overload
**Owner:** Lohith
**Status:** Final v1
**Last updated:** July 2026

---

## 1. Overview

Overload is a self-hosted web app for logging gym workouts and tracking progressive overload over time. Built as a personal training tool and as a portfolio/interview project demonstrating backend engineering, database design, and containerization skills (targeting Tier-1 internship applications, including Oracle).

## 2. Problem Statement

Generic fitness apps are bloated, ad-heavy, or don't structure data the way a lifter actually needs it — separating a *routine template* (what you plan to do) from a *session log* (what you actually did) so that progressive overload can be tracked set-by-set over time. Spreadsheets work but don't scale well and give no history/charting.

## 3. Goals

- Log workouts (exercises, sets, reps, weight) quickly during a gym session.
- See historical performance per exercise to make informed progressive-overload decisions (e.g. "did I lift more than last week?").
- Keep routine templates (e.g. a 6-day push/pull/legs/arms split) separate from actual logged sessions.
- Ship a working, containerized, portfolio-ready project end to end.

### Non-goals (v1)

- Social features (following, leaderboards, community feed, sharing)
- Mobile app (web-first, responsive is enough)
- Nutrition/diet tracking
- AI-generated workout suggestions or auto-progression (deferred to a later phase)
- Wearable integration (Apple Watch/WearOS)
- Coach/client features
- Supersets (deferred — Phase 2)
- Monthly Report / Year in Review retention features (deferred indefinitely)

## 4. Target User

Primarily Lohith himself — a lifter who trains 5–6x/week on a structured split and wants a fast, no-friction way to log sets and review progress. Secondary audience: anyone reviewing this as a portfolio project (recruiters, interviewers).

## 5. Core User Stories

| # | As a user, I want to... | So that... |
|---|--------------------------|-------------|
| 1 | Register and log in | My workout data is private and persists across sessions |
| 2 | Create/edit a routine template (exercises per day) | I don't have to re-enter my split every session |
| 3 | Start a workout session from a routine | Logging is fast — exercises are pre-populated |
| 4 | See my previous weight/reps auto-filled for each exercise | I can immediately tell if I need to push harder than last time |
| 5 | Log sets (weight, reps, RPE optional) per exercise during a session | I capture what I actually lifted |
| 6 | Add or remove a set on the fly during a session | I'm not locked into the exact set count from my routine template |
| 7 | Get an automatic rest timer after logging a set | I stay on schedule without needing a separate timer app |
| 8 | Get notified immediately when a set beats a previous best | I know I hit a PR in the moment, not just after reviewing history later |
| 9 | See suggested warm-up sets for my target working weight | I don't have to calculate my own ramp-up sets |
| 10 | See a plate breakdown for a target barbell weight | I don't have to do math mid-set |
| 11 | View past sessions for a given exercise | I can see if I'm progressing (weight/reps trending up) |
| 12 | See a simple chart of an exercise's history | Progress is visible at a glance, not just numbers in a table |
| 13 | See my current training streak on the dashboard | I stay motivated to keep a consistent schedule |
| 14 | Mark/see personal records (PRs) | I know when I've hit a new best |
| 15 | Add custom exercises | My routine isn't limited to a fixed exercise list |
| 16 | Add a note to an exercise or session | I can remember context (new gym, felt weak, equipment issue) for later review |

## 6. Feature Scope (v1)

- User registration & login (JWT-based auth)
- CRUD for exercises (name, muscle group, optional notes)
- CRUD for routine templates and their exercises (routine_exercises)
- Start/complete a workout session
- **Auto-fill previous performance** — when logging a set, pre-populate weight/reps from the last session for that exercise
- Log sets within a session (set_logs: weight, reps, order, timestamp)
- **Dynamic set add/remove** — add or remove individual set rows during a session, not locked to the routine template's set count
- **Automatic rest timer** — countdown starts the moment a set is marked complete
- **Live PR notification** — the moment a logged set beats the prior best for that exercise, show an immediate in-session toast/badge ("🔥 New PR!"), rather than surfacing it only on the Progress screen afterward
- **Warm-up set calculator** — given a target working weight, suggest a warm-up ramp-up (e.g. 40%/60%/80% of target)
- **Plate calculator** — given a target barbell weight, show the plate combination needed
- Session/exercise notes (free-text field)
- Workout history view (list past sessions, filter by exercise)
- Basic progress chart per exercise (weight or volume over time)
- **Streak tracking** — calculate and display current consecutive training streak on the dashboard
- PR detection/display per exercise (both live, per (8), and in the Progress screen's Personal Records section)

## 7. Data Model (high-level)

- `users` — account info, credentials
- `exercises` — exercise catalog (name, muscle group)
- `routine_exercises` — links a routine/day to exercises (the template)
- `workout_sessions` — a logged instance of training on a given date, includes optional `notes`
- `set_logs` — individual sets tied to a session + exercise (weight, reps, order, optional `notes`)

Note: streak logic derives from `workout_sessions.date` (consecutive days/weeks with a completed session); no new table needed. Live PR detection is a query against `set_logs` for the max weight/reps per `exercise_id` prior to the current set — also no schema change, just service-layer logic reused between the live-toast trigger and the Progress screen's PR list.

## 8. Tech Stack

- **Backend:** Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security + JWT
- **Database:** PostgreSQL
- **Frontend:** Plain HTML/CSS/JS (v1) — calls backend REST API
- **Containerization:** Docker + Docker Compose
- **Tools:** Git/GitHub, Postman, IntelliJ

## 9. Build Order

1. Backend REST API (auth, exercises, routines, sessions, sets) with Postgres
2. Dockerize (backend + Postgres via Compose)
3. Frontend (plain HTML/JS) consuming the API
4. Auto-fill previous performance + dynamic set add/remove + rest timer
5. Live PR notification + warm-up calculator + plate calculator
6. History view + basic charts + streak logic
7. PR tracking logic (Progress screen)

## 10. Success Metrics

- App is fully functional end-to-end: register → build routine → log session (with auto-fill, rest timer, live PR alerts) → view history/chart/streak
- Runs via a single `docker-compose up`
- Clean enough architecture and README to walk an interviewer through it confidently

## 11. Future Phases (out of scope for v1)

- Swagger/OpenAPI docs, Flyway migrations, GitHub Actions CI
- Automated tests (unit + integration), global exception handling, input validation, logging
- Redis caching, notifications (push), AI-based workout suggestions/auto-progression, monitoring (Prometheus/Grafana), cloud deployment
- Social features (following, leaderboards, community feed, sharing, Strava integration)
- Supersets
- Wearable integration
- Monthly Report / Year in Review

## 12. Open Questions

- Should routines support multiple splits (e.g. switch between a PPL split and a different one) or just one active routine at a time?
- Is RPE (rate of perceived exertion) tracking worth including in v1, or purely optional/deferred to Phase 2?
- Should PR detection (for the Progress screen list) be computed on write (when a set is logged) or on read (queried on demand)? Live PR notification will need on-write comparison regardless, so this may resolve itself once that's built.
- What defines a "streak day" — any day with a logged session, or does it need to match the routine's scheduled training days?
