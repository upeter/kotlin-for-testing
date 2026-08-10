# Alignment Plan — Demonstrating Harness Value

> **Status: planning document.** Not a feature doc — intentionally carries no
> `documents:` frontmatter so `doc_staleness.py` ignores it.

## 1. Thesis

A coding-agent harness does not make the best run better; it **raises the floor
and tightens the distribution** of independent runs. Teams ship their worst
agent run, not their best. A single unharnessed run can beat a harnessed one on
tokens and still look fine — that is a lucky draw from a wide distribution, not
evidence against the harness.

The presentation therefore measures **variance across repeated runs of the same
task**, not the outcome of one run.

## 2. Method overview

- **One feature, implemented repeatedly.** Every run implements the identical
  feature from the identical starting commit with the identical prompt.
- **Four cumulative harness conditions** (C0–C3). Each demo compares condition
  C(n) against C(n−1): *full harness so far, minus only the new ingredient* —
  isolating the marginal value of that ingredient.
- **N = 5 runs per condition** (20 runs total). N=5 is the credibility floor.
  Categorical outcomes ("compliant: 1/5 vs 5/5") are robust at this N; token
  numbers are presented as ranges, never as statistics.
- **Recordings, not live runs.** Per demo: a distribution chart, one
  representative failure clip from the minus-condition, one transcript moment
  of the harnessed agent using the ingredient. ~2 minutes per demo.
- **Session evidence** via the `jsonl-session-report` skill: per-run tokens,
  tool calls, iterations, wrong turns, and whether the agent consulted a
  doc/rule before acting.

### Framing rule

Never say or imply the agent "learns" or "improves" across runs. Runs are
independent and clean-room. What improves is the **distribution** as the
harness grows. Say it exactly that way.

## 3. The repeated feature

### The task prompt (verbatim, identical bytes for all 20 runs)

> Extend schedule assignment: talks are assigned to rooms and time slots for
> the conference program, and the public program search must reflect the
> schedule. Apply the documented acceptance workflow and scheduling
> constraints.

**Leakage rule.** The prompt is a realistic ticket and must **not** restate
the contract. The contract — quorum thresholds, boundary semantics, conflict
rules, vacate-on-drop, index consistency — lives only in the harness docs,
present in C1+ only. (An earlier draft stated the rules inside the prompt;
that turns the prompt itself into feedforward and equalizes all conditions,
destroying Demos 1 and 3.)

**Anticipated objection — "the bare agent never had a chance."** Address it
head-on in the talk, before Demo 1: this *is* the pre-harness reality.
Requirements live in wikis and heads; the harness is precisely the mechanism
that gets them to the agent.

**Task character.** `assignSchedule` already exists (`TalkService:81`,
`TalkController:44`) with only an `endTime > startTime` check. The task is a
**hardening/extension** of existing behavior, not greenfield — higher-variance
and more realistic. The plan and prompt say "extend," never "implement."

### Failure surfaces

| Surface | Contract (doc-only, C1+) | Why the bare agent fails | Caught by |
|---|---|---|---|
| A. Facts that exist nowhere in code | `ACCEPTED` requires ≥3 ratings with mean ≥3.5; `REJECTED` requires a moderation message; boundary semantics (a slot ending 10:00 does **not** conflict with one starting 10:00); a talk leaving `ACCEPTED` vacates its slot | Invents a plausible quorum, guesses boundary semantics. **Caution:** at ~2.4k LOC the codebase itself teaches conventions (error model, DTO style are copyable from existing code), so Demo 1 must rest on doc-only *semantics*, not style. The compliance checklist targets invented semantics. | Feedforward: context + rules (Demo 1) |
| B. Miss-prone invariants | No room overlap; a speaker — primary **or co-speaker** — cannot occupy two overlapping slots; only `ACCEPTED` talks schedulable; Kover 80%; (if UI in scope, §3b) mirror-never-share | The co-speaker check is the engineered miss: everyone checks the primary speaker, few check co-speakers — even when documented. Agents also chronically under-test edge branches. Expect 2–3/5 vs 5/5, not 0/5 — a believable delta beats a perfect one. | Mechanical feedback: tests + Kover + detekt/ktlint + Konsist hooks (Demo 2) |
| C. Cross-module semantic obligation, intrinsic to the task | The public program search must surface only talks that are `ACCEPTED` **and scheduled**: assigning a slot upserts the search document; vacating or dropping out of `ACCEPTED` removes it | Compiler green, touched-module tests green, every gate quiet — and the program search shows a rejected talk. The coupling must stay invisible: route ingestion via an outbox / separate module with **no compile-time link** to the code path being edited, or the C2 agent stumbles onto it | Knowledge: feature docs + staleness gate + codebase graph (Demo 3) |

Surface C is deliberately *not* "re-embed on content change" — schedule
assignment doesn't change content, so that obligation would be bolted-on and a
skeptic would notice. Schedule-aware search makes the obligation inherent to
the exact feature every run implements.

### Prerequisite build work (before any runs)

1. Rating-quorum rules and status-transition gating in `TalkService`
   (surface A) — extends existing entities only.
2. Scheduling-conflict invariants incl. co-speaker overlap (surface B) —
   extends existing `ScheduleSlot`/`Talk`.
3. Vector-ingestion module with pgvector (PostgreSQL via Testcontainers
   already present), indexing **accepted + scheduled** talks, fed via an
   outbox with no compile-time link to `TalkService` (surface C).
4. Optionally the UI module (§3b) — decide before building C0.
5. Each addition gets its `docs/` page (via the `generate-docs` skill) **at
   introduction time**, so the harnessed conditions are never stale.
6. **Resolve the Maven/Gradle contradiction** between root CLAUDE.md
   (`./mvnw verify`) and the dev-rules section (`./gradlew` only). A
   self-contradictory harness invalidates the harnessed conditions.

## 3b. The UI module — role and decision

A Compose Multiplatform frontend in a separate module, HTTP-coupled to the
backend, with **mirrored** DTOs (each side owns its copy of the JSON contract;
the doc is the source of truth). Status: **recommended if budget allows, not
mandatory.** The fallback (no UI) keeps Demo 3 on search-API output and gives
Konsist the weaker outbox-not-from-controller rule.

What the UI buys:

- **The mirror-never-share rule — a natural architecture failure for Demo 2.**
  DRY is buried deep in model priors: given a frontend touch, agents *will*
  reach for a frontend→backend compile dependency or a shared DTO module.
  The house rule ("mirror, never share — contract coupling is worse than
  duplication") is documented (Demo 1 material) and mechanically enforced by
  Konsist + Gradle dependency rules (Demo 2 material). No engineered failure
  needed; the model's own instincts produce the violation.
- **Konsist gets real cross-module work:** frontend packages must not import
  backend types; no `jakarta.persistence` anywhere in the client; controllers
  return DTOs, never entities.
- **Demo 3 becomes legible:** "gates green, but the program page shows a
  rejected talk" beats a JSON diff, especially for non-engineers.

Conditions for it to work:

- **Scenery doesn't demo.** The feature spec must include a thin frontend
  touch — the program page shows the schedule, one screen consuming the
  changed endpoint — or no run ever meets the sharing temptation and the
  Konsist rules demonstrate nothing.
- **Do not build a DTO parity check.** A custom Konsist test could compare the
  mirrored DTO sets structurally — that would move contract drift from the
  "knowledge" category into "mechanical feedback." Demo 3 no longer relies on
  contract drift, so this is survivable, but make the omission deliberate.
  (Usable as a closer line instead: mature harnesses gradually convert
  knowledge into mechanical checks.)
- **Costs, stated honestly:** forces the Gradle resolution and a heavier
  toolchain; every run grows (backend + frontend + the mirroring fork);
  UI layout variance is noise w.r.t. the thesis — exclude pure styling from
  the diff-similarity metric (§7).
- **Expected pushback:** "mirrored DTOs violate DRY." Answer: it is a *house
  decision* (consumer-contract orthodoxy), and encoding house decisions is
  precisely what a harness is for.

## 4. Conditions

| Condition | Adds | Demo |
|---|---|---|
| **C0 — bare** | Nothing. No CLAUDE.md, no rules, no hooks, no docs. Build files only. | Baseline for Demo 1 |
| **C1 — feedforward** | CLAUDE.md hierarchy (stack, layout, build commands) + coding rules (fail-fast, no defaults, no suppression) + feature docs as passive context | Demo 1: C1 vs C0 |
| **C2 — mechanical feedback** | **In-loop agent hooks** via `.claude/settings.json`, two-tier (§4b): PostToolUse on Edit/Write — auto-format + file-scoped lint feedback; Stop hook — `just verify` (check-only, project-scoped: compile with `allWarningsAsErrors`, unit tests, Kover 80%, Konsist), blocking completion until green | Demo 2: C2 vs C1 |
| **C3 — knowledge feedback** | `doc_staleness.py check` added to the Stop gate + codebase-memory MCP graph (`trace_path`, blast radius) via committed `.mcp.json` + `generate-docs` skill | Demo 3: C3 vs C2 |

Each condition is a branch (see §6) whose HEAD contains exactly that harness
state. "Minus only the new ingredient" is enforced by construction: C(n−1) *is*
C(n) without the ingredient.

## 4b. Feedback trigger ladder

**Governing principle.** Feedback value decays with distance from the causing
edit — but *project-scoped* checks fired *per edit* produce thrash, because
intermediate states during multi-file work are legitimately broken. Match each
check's cost and scope to the earliest trigger where signal beats noise:
file-scoped + cheap → per edit; project-scoped → end of turn; slow or
environmental → outside the loop.

| Trigger | Mode | Checks | Rationale |
|---|---|---|---|
| PreToolUse | **prevent** (deny) | Gate-tamper protection: `config/`, thresholds, hook config, verification suite | Policy, not quality — zero cost; catches what no later check can (a tampered gate reports green) |
| PostToolUse (Edit/Write) | **react** — auto-fix, silent | `ktlintFormat`, touched file only | Sub-second; also kills cosmetic variance across runs, sharpening the diff-similarity metric (§7) |
| PostToolUse (Edit/Write) | **react** — inform, don't block | ktlint check + detekt, touched file only | Error lands in context in the same turn, while the file is "hot"; never block per-edit — compile/tests fail *by design* mid-refactor |
| Stop | **verify** — block until green | `just verify` (check-only): compile `allWarningsAsErrors`, unit tests, Kover 80%, Konsist; +`doc_staleness check` at C3 | Project-scoped truth belongs at end of turn. Loop guard required: `stop_hook_active` + iteration cap (the cap doubles as the "iterations to green" metric, §7) |
| SessionStart | **feedforward** | `doc_staleness check` output injected into context | Same tool, both roles: feedback at Stop, feedforward at session start — one-line proof for the closer that the categories are stages, not silos |
| pre-commit | safety net | `ci-quiet` minus slow scans | Catches *human* commits; agents already passed Stop. Today it runs everything incl. trivy (network) with `always_run` — the latency that trains people toward `--no-verify`, which is how gates die |
| CI (to be added) | **audit** — final authority | trivy, semgrep, deptry, integration tests (Testcontainers, minutes) | ~1000× cost spread between ktlint and integration tests must not share one trigger |

**Current misfits being fixed:** everything fires at exactly one point
(pre-commit, monolithic `ci-quiet`) — too late for cheap checks, too heavy for
slow ones; and `ci-quiet` includes the *mutating* `code-format` step — a
verification gate must not modify the code it verifies, hence the check-only
`just verify` target (formatting is handled per-edit).

## 5. Run protocol (clean-room)

Per run:

1. Reset a fresh worktree to the condition branch HEAD. No leftover files, no
   resumed sessions.
2. Launch a fresh agent session with the committed prompt
   (`demos/prompt.md` — identical bytes for all 20 runs), pinned model
   version.
3. Let the run finish without human steering.
4. Archive: session JSONL, final diff, gate results.
5. Score the run (§7) mechanically — no judgment calls during scoring.

**Gate-tamper protection.** Nothing may allow an agent to get green by editing
the gates: deny-rules in `.claude/settings.json` (or a PreToolUse hook)
protect `config/` (detekt etc.), the Kover threshold, hook configuration,
`demos/`, and the post-hoc verification suite — which additionally lives
outside the run's worktree reach. Without this, one audience question
("couldn't it just lower the threshold?") undermines Demo 2.

Scripted as justfile targets so the methodology itself is auditable in-repo:

- `just demo-run <condition> <run-nr>` — reset, launch, archive.
- `just demo-score <condition> <run-nr>` — apply the scorecard.
- `just demo-report` — aggregate all runs into the comparison tables/charts.

### Honesty rules (state these in the talk)

- Report selection transparently: "this failure occurred in 4 of 5 bare runs;
  all transcripts are in the repo."
- Publish repo, prompts, and all 20 transcripts. Recordings invite
  "cherry-picked"; auditability disarms it.
- Never present N=5 token numbers as statistics — ranges only.
- The prompt never restates the contract (§3 leakage rule); deliver the
  "pre-harness reality" framing *before* Demo 1, not as a rebuttal after.

## 6. Branch & commit layout

```
main ────────────────────────────── development line
demo/baseline      C0 harness state (no harness), feature NOT implemented
demo/c1-feedforward  = baseline + CLAUDE.md + rules + docs
demo/c2-hooks        = c1 + hook configuration
demo/c3-knowledge    = c2 + staleness gate + MCP graph + skills
demo/run/<cond>-<n>  one result commit per run, branched from its condition
demos/prompt.md      the committed task prompt (identical for every run)
```

Rules: the bare branch genuinely *lacks* the harness files (skeptics will diff
branches). One result commit per run. Switching between any two results during
the talk is `git switch` + `just demo-report` — seconds.

## 7. Scorecard (per run, mechanical)

| Metric | How measured |
|---|---|
| Constraint compliance | Checklist of surfaces A/B/C rules, verified by a fixed post-hoc test suite run against the result commit (suite lives outside the run's reach) |
| Gates passed | ktlint / detekt / compiler / Kover / Konsist / staleness — pass/fail each |
| Doc sync | `doc_staleness.py check` + does the doc describe the new behavior |
| Iterations to green | From session JSONL: failed-gate → retry cycles |
| Harness usage | From JSONL: did the agent read the doc / cite a rule / call `trace_path` before editing |
| Tokens | From JSONL; reported as a range per condition, listed **last** |
| Cross-run diff similarity | Pairwise similarity of the 5 diffs within a condition — the "five agents, five architectures vs five agents, one architecture" visual. If the UI is in scope, exclude pure styling/layout files: UI cosmetic variance is noise w.r.t. the thesis |

## 8. Demo shot list

Each demo: chart → failure clip → transcript moment. 20–40 s per clip.

**Demo 1 — Feedforward (C1 vs C0).** Failure class: *plausible but wrong.*
- Chart: compliance on surface A, 5 runs each (expected shape: ~1/5 vs 5/5).
- Clip: bare agent confidently inventing a quorum rule / adding a silent
  default fallback; compiles, looks fine.
- Transcript moment: C1 agent quoting the business rule from the feature doc
  before writing code.
- Visual: five C0 file trees side by side vs five C1 file trees.

**Demo 2 — Mechanical feedback (C2 vs C1).** Failure class: *incomplete or
non-compliant, caught deterministically.*
- Chart: invariant coverage on surface B (expected: 2–3/5 at C1 despite the
  rules being *in context* vs 5/5 at C2 — feedforward guides, feedback
  guarantees).
- Clip (UI in scope): agent reaches for the DRY move — frontend→backend
  dependency or shared DTO module — and the Konsist/Gradle gate blocks it.
  Clip (no UI): C2 agent hitting the Kover wall on the co-speaker invariant.
- Transcript moment, two distinct beats (§4b): the *fast* correction —
  file-scoped lint feedback mid-turn, fixed in seconds — and the *deep* one —
  the Stop gate catching the missed invariant; gate output in, fix out.
- One slide, no demo: SonarQube-class CI tools are the same category at higher
  latency; in-loop hooks shape agent behavior, post-push analysis cannot.

**Demo 3 — Knowledge feedback (C3 vs C2).** Failure class: *mechanically green
but semantically broken.*
- Chart: surface C consistency, 5 runs each (expected: 0/5 at C2 — every gate
  green, search stale — vs 5/5 at C3).
- Clip: C2 run finishing with all gates green; cut to the failure — the
  program page showing a rejected talk (UI variant) or stale search-API
  results (no-UI variant).
- Transcript moment: C3 agent tracing blast radius via the graph / reading the
  engagement between `TalkService` and the ingestion module, then updating
  both sides *and* the doc (staleness gate).
- This demo answers "but couldn't SonarQube have caught that?" — no gate can;
  only curated knowledge does.

**Escalation logic across demos:** each demo's failure slips past everything
shown before it. Demo 1's failure would pass a human skim; Demo 2's slips past
feedforward; Demo 3's slips past every mechanical gate.

## 8b. Live bookend run

At the very start of the talk, a **live C3 run** of the same feature is set in
motion; it runs in the background during the presentation and is revealed at
the end as an honest, non-tweaked result.

**The line that makes it the thesis, not a stunt** (say at the reveal): *the
only reason a single live run can be bet on is the variance reduction this
talk is about — a live bare run could do anything.*

### Start protocol (visible, ~60 seconds)

1. Fresh worktree from the C3 branch, on screen.
2. Show the committed `demos/prompt.md` — identical bytes to all recorded runs.
3. Launch, show a timestamp, then **minimize** — never stream run output on
   the projector mid-talk; it steals attention and invites live debugging.

### Reveal protocol (after Demo 3 — earlier, the audience can't yet read a
scorecard)

1. `just demo-score` live — must complete in under a minute, so it runs the
   fast verification suite only, not the integration tier.
2. Plot the live run as **point #21 into the C3 distribution chart**. Landing
   inside the cluster is the variance claim demonstrating itself.
3. If the run hit the Stop gate and self-corrected during the talk, show that
   retry from the JSONL — a blocked-then-fixed run is *more* convincing than a
   clean pass; it is Demo 2 happening for real.

### Timing gate

Go/no-go is empirical: the 5 recorded C3 runs give the duration distribution.
**p95 (not mean) must fit inside the slot minus reveal time**; the Stop-gate
iteration cap bounds the tail. If p95 is tight, trim the live variant's
frontend touch — same contract, same gates.

### Contingency checklist (infrastructure is the real risk, not the agent)

- [ ] Testcontainers images pre-pulled; Gradle daemon + caches warmed.
- [ ] Session pre-authenticated; model pinned; rate-limit headroom checked.
- [ ] Phone hotspot as network fallback.
- [ ] One recorded same-day run under the identical protocol as the
      **declared** fallback — announce it upfront ("if the venue wifi kills
      it, here's this morning's"); announcing preserves the honesty framing.
- [ ] Notifications off; run window off the projector.

### The rehearsal question

Expect: *"how many times did you rehearse this?"* The honest answer —
"rehearsals count as runs and are archived like any other" — is only available
if rehearsal runs are actually archived under the §5 protocol. Do so; it turns
the toughest audience question into another proof point.

## 9. Closer — the broader picture

Demo 3 hands over the transition: the ingredients that carried the demos —
rules, docs, skills, graph — are **artifacts**, not configuration. From there:

- Distribution: skills/plugins via marketplaces; a team's hard-won conventions
  become installable.
- Metacontext across repos: org-level rules and docs layered above per-repo
  harnesses; the same variance-tightening effect at portfolio scale.
- The staleness gate generalizes: knowledge artifacts need feedback loops of
  their own, or they rot into the very failure mode of Demo 3.
- Direction of travel: mature harnesses gradually convert knowledge into
  mechanical checks (e.g., a DTO-parity Konsist test would mechanize the wire
  contract, §3b) — the categories are stages, not silos.

## 9b. Harness gap analysis (repo state, 2026-07-16)

**Present:** CLAUDE.md hierarchy (root + 3 scoped) with `AGENTS.md` pointer;
path-scoped `.claude/rules/` (auto-loaded — C1 is legitimate); `docs/` +
staleness tooling; skills `generate-docs` and `jsonl-session-report` (already
in-repo); quality tooling via justfile (ktlint, detekt, semgrep, Konsist,
Kover, deptry, trivy); pre-commit running `just ci-quiet`.

**The pattern:** feedforward is mature; the *feedback* half — which Demos 2
and 3 depend on — currently lives outside the agent loop entirely. Pre-commit
fires only if the agent commits (and is bypassable); running `just ci` is
something the agent must *choose* to do, i.e. feedforward-dependent feedback.

Missing, ranked:

| # | Gap | Threatens | Fix |
|---|---|---|---|
| 1 | No `.claude/settings.json` — no in-loop hooks (PostToolUse lint, Stop gate) | **Demo 2's premise** ("hooks guarantee") | Build for C2 (§4) |
| 2 | No committed `.mcp.json` — codebase-memory MCP referenced but not reproducible | **Demo 3 / auditability** ("clone and rerun") | Commit for C3 |
| 3 | No gate-tamper protection (agent can edit detekt config / Kover threshold / verification suite) | Demo 2 credibility | Deny-rules, §5 |
| 4 | Harness not packaged as a plugin; no `marketplace.json` | **The closer** — turns the marketplace slide into a 20-second install demo in a fresh repo | Package rules + skills + hooks + `.mcp.json` as a plugin in a small marketplace repo |
| 5 | No CI at all (`.github/` empty) | The in-loop-vs-post-push latency slide (SonarQube point) has no concrete counterpart; no `claude-code-action` PR review | Minimal workflow: `just ci` + optional review action |
| 6 | No reviewer subagent (`.claude/agents/`) | Optional — "review as feedback" artifact; one more distributable ingredient class | Cheap, doc-aware reviewer |
| 7 | No slash commands (`.claude/commands/`) | Optional — completes the artifact taxonomy (`/demo-run`) | Trivial |
| 8 | No org/user metacontext layer | Closer only | Slide, nothing to build |

## 10. Open items

- [ ] **Decide UI module in/out (§3b)** — gates the build order; decide first.
- [ ] Resolve Maven vs Gradle contradiction in the harness (§3.6).
- [ ] Build prerequisite features + docs (§3, items 1–5).
- [ ] Close gaps 1–3 (§9b): `.claude/settings.json` hooks per the trigger
      ladder (§4b) + deny-rules, committed `.mcp.json`.
- [ ] Add check-only `just verify` target (§4b) and slim pre-commit to the
      safety-net tier (drop trivy/semgrep/deptry to CI).
- [ ] Package the harness as a plugin + marketplace repo (§9b gap 4, closer).
- [ ] Minimal CI workflow (§9b gap 5, latency slide).
- [x] ~~Copy `jsonl-session-report` skill into the repo~~ — already present;
      still align §7 metrics to what it actually extracts.
- [ ] Implement `just demo-run` / `demo-score` / `demo-report`.
- [ ] Pin model version; commit `demos/prompt.md`.
- [ ] Pilot: 1 run at C0 and C3 to validate the scorecard before the full 20.
- [ ] Live bookend (§8b): verify p95 timing gate from the C3 runs; prepare the
      contingency checklist; archive all rehearsal runs under the §5 protocol.
