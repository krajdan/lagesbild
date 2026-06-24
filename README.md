# Lägesbild

Ett kunskapsbaserat systemstöd för brottsförebyggande och trygghetsfrämjande arbete
i samverkan — byggt som en fristående prototyp i samma anda som EMBRACE.

Rapportera en händelse på under 90 sekunder, se en gemensam **lägesbild** på karta,
analysera **hot-spots** och **hot-times**, och dokumentera **insatser** som kan följas
upp mot utvecklingen över tid.

> Byggd som arbetsprov: en liten men körbar version av domänen, för att visa fullstack
> (Spring Boot + frontend), produktionsägande (Docker, CI/CD, health checks) och ett
> agentiskt AI-arbetssätt (hela tjänsten planerad och byggd med Claude Code i VS Code).

---

## Snabbstart (noll konfiguration)

Kräver **JDK 21** och **Maven**.

```bash
mvn spring-boot:run
```

Öppna sedan **http://localhost:8080**.

Appen startar mot en in-memory H2-databas och seedar automatiskt en realistisk
lägesbild för Örebro (~680 händelser + ett par insatser), så kartan, veckopulsen och
analyserna är levande direkt.

---

## Vyer

- **Lägesbild** — karta med värmekarta/punkter, filter på område, typ (brott/otrygghet)
  och period, samt en KPI-rad med antal, 30-dagarsförändring och aktuell hot-spot.
- **Analys** — *veckopulsen* (7×24, veckodag × timme) som visar hot-times, trend per
  vecka och de mest drabbade områdena uppdelat på brott och otrygghet.
- **Insatser** — dokumenterade åtgärder kopplade till områden, med status och ägare.
- **Rapportera** — formulär där platsen väljs genom att klicka på kartan.

---

## Arkitektur

```
Frontend (en fil, /static/index.html)         Backend (Spring Boot 3, Java 21)
  Leaflet + leaflet.heat (karta/heatmap)         REST /api/incidents, /api/analysis,
  Chart.js (trend, områden)        ── HTTP ──>    /api/interventions, /api/meta
  vanilla JS (veckopuls)                          Spring Data JPA
                                                  H2 (dev) / PostgreSQL (prod)
                                                  DataSeeder (CommandLineRunner)
                                                  /actuator/health
```

Frontend serveras av Spring Boot från `src/main/resources/static`, så hela tjänsten
är en process och en deploy.

### API

| Metod | Väg | Beskrivning |
|-------|-----|-------------|
| GET | `/api/incidents` | Lista händelser. Filter: `area`, `kind`, `from`, `to` |
| POST | `/api/incidents` | Skapa händelse |
| DELETE | `/api/incidents/{id}` | Ta bort händelse |
| GET | `/api/analysis/overview` | Summering, hot-times, områden, trend |
| GET | `/api/interventions` | Lista insatser |
| POST | `/api/interventions` | Skapa insats |
| GET | `/api/polisen/events` | Live-händelser från Polisens öppna API (cachat) |
| GET | `/api/polisen/status` | Flödets status: live/offline, antal, senast uppdaterad |
| GET | `/api/meta` | Områden, typer, aktörer, statusar |
| GET | `/actuator/health` | Hälsa (för drift/övervakning) |

---

## Live-data från Polisen

Appen hämtar **riktiga händelser** från Polisens öppna API (`polisen.se/api/events`)
för Örebro och visar dem som ett live-flöde bredvid kartan, med egna pulsande markörer
och en LIVE-indikator i sidhuvudet. Källfiltret växlar mellan lokala rapporter, Polisen
eller båda.

- `PolisenClient` pollar API:et **var 15:e minut** och serverar från cache, för att hålla
  sig inom Polisens anropsgränser (min 10 s mellan anrop, max 60/timme, 1440/dygn).
- Externa anrop har **timeouts** och **felar tyst** — ligger nätet nere behålls senaste
  lyckade hämtning och flödet markeras som offline.
- Polisens koordinat är en **mittpunkt för kommunen**, inte exakt brottsplats. Det visas
  tydligt i kartan ("ungefärlig plats") och markörerna sprids deterministiskt så att de
  inte staplas — ärligt om datans precision.

> Kräver internet vid körning. Når appen inte Polisens API visas resten ändå (lokal
> seed-data), och flödet markeras offline.

---

## Köra mot PostgreSQL

```bash
docker compose up --build
```

Startar Postgres + appen med produktionsprofilen (`SPRING_PROFILES_ACTIVE=postgres`).

---

## Deploy

`render.yaml` är en färdig Render-blueprint (gratis webbtjänst + Postgres,
health check mot `/actuator/health`). Anslut repot i Render och välj
**New → Blueprint**.

---

## CI

`.github/workflows/ci.yml` kör `mvn -B verify` (bygg + tester) på varje push och PR.

---

## Tekniska val

- **H2 som standard** så att `mvn spring-boot:run` fungerar utan installation; Postgres
  aktiveras via profil för produktion.
- **Filtrering i tjänstelagret** i stället för komplexa JPA-queries — medvetet enkelt
  för en prototyp med hundratals rader, lätt att läsa och felsöka.
- **Reproducerbar seed** (fast slumpfrö) så demon ser likadan ut varje gång.
- **Leaflet + CARTO dark tiles** i stället för Google Maps — ingen API-nyckel krävs.
