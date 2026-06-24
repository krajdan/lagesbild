package se.lagesbild.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.lagesbild.config.AreaCatalog;
import se.lagesbild.incident.Actor;
import se.lagesbild.incident.Incident;
import se.lagesbild.incident.IncidentRepository;
import se.lagesbild.incident.IncidentType;
import se.lagesbild.intervention.Intervention;
import se.lagesbild.intervention.InterventionRepository;
import se.lagesbild.intervention.InterventionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fyller databasen med en realistisk lagesbild for Orebro vid forsta uppstart:
 * fler handelser i utsatta omraden, tydlig kvallspuls och helghoppning. Detta
 * gor att kartan, hot-spots och veckopulsen ser levande ut direkt.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final int INCIDENT_COUNT = 680;
    private static final int DAYS_BACK = 120;

    private final IncidentRepository incidents;
    private final InterventionRepository interventions;
    private final Random random = new Random(42); // fast fro = reproducerbar demo

    public DataSeeder(IncidentRepository incidents, InterventionRepository interventions) {
        this.incidents = incidents;
        this.interventions = interventions;
    }

    @Override
    public void run(String... args) {
        if (incidents.count() > 0) {
            return;
        }
        seedIncidents();
        seedInterventions();
    }

    private void seedIncidents() {
        // Relativ "hetta" per omrade (samma ordning som AreaCatalog.AREAS)
        double[] areaWeight = {0.16, 0.18, 0.14, 0.08, 0.10, 0.09, 0.06, 0.04, 0.05, 0.03};
        // Timvikt: lag pa natten, topp pa kvallen (17-23)
        double[] hourWeight = {
                0.4, 0.3, 0.2, 0.2, 0.2, 0.3, 0.5, 0.7, 0.8, 0.7, 0.6, 0.7,
                0.8, 0.8, 0.9, 1.0, 1.2, 1.6, 1.9, 2.0, 1.8, 1.5, 1.1, 0.7
        };
        // Veckodagsvikt (mandag..sondag), helg tyngre
        double[] dowWeight = {0.8, 0.8, 0.9, 1.0, 1.5, 1.8, 1.2};

        IncidentType[] types = IncidentType.values();
        Actor[] reporters = Actor.values();
        double[] reporterWeight = {0.34, 0.20, 0.30, 0.06, 0.10}; // kommun, polis, bostadsbolag, vakt, invanare
        double[] severityWeight = {0.55, 0.32, 0.13}; // grad 1,2,3

        List<Incident> batch = new ArrayList<>(INCIDENT_COUNT);
        for (int n = 0; n < INCIDENT_COUNT; n++) {
            int ai = weightedIndex(areaWeight);
            AreaCatalog.Area area = AreaCatalog.AREAS.get(ai);

            double lat = area.lat() + (random.nextDouble() - 0.5) * 0.008;
            double lng = area.lng() + (random.nextDouble() - 0.5) * 0.014;

            IncidentType type = types[random.nextInt(types.length)];
            int severity = weightedIndex(severityWeight) + 1;
            Actor reporter = reporters[weightedIndex(reporterWeight)];

            LocalDateTime occurredAt = randomOccurrence(hourWeight, dowWeight);

            batch.add(new Incident(type, area.name(), round(lat), round(lng),
                    severity, occurredAt, descriptionFor(type), reporter));
        }
        incidents.saveAll(batch);
    }

    private LocalDateTime randomOccurrence(double[] hourWeight, double[] dowWeight) {
        // Hitta en dag (med helgvikt) inom intervallet, samt en timme med kvallsvikt.
        LocalDate day;
        while (true) {
            int offset = random.nextInt(DAYS_BACK);
            day = LocalDate.now().minusDays(offset);
            double w = dowWeight[day.getDayOfWeek().getValue() - 1] / 1.8;
            if (random.nextDouble() <= w) {
                break;
            }
        }
        int hour = weightedIndex(hourWeight);
        int minute = random.nextInt(60);
        return day.atTime(hour, minute);
    }

    private void seedInterventions() {
        interventions.saveAll(List.of(
                new Intervention(
                        "Forbattrad belysning i Vivalla centrum",
                        "Vivalla",
                        "Kommunen byter armaturer och beskar buskage langs gangstrak efter aterkommande rapporter om upplevd otrygghet kvallstid.",
                        InterventionStatus.PAGAR,
                        LocalDate.now().minusWeeks(5),
                        Actor.KOMMUN),
                new Intervention(
                        "Trygghetsvardar vid Varberga torg",
                        "Varberga",
                        "Bostadsbolaget och kommunen samordnar narvaro av trygghetsvardar fredag-lordag kvall.",
                        InterventionStatus.PAGAR,
                        LocalDate.now().minusWeeks(3),
                        Actor.BOSTADSBOLAG),
                new Intervention(
                        "Klottersanering och kameror, Brickebacken",
                        "Brickebacken",
                        "Snabb sanering inom 24h samt utokad kamerabevakning vid garageinfarter.",
                        InterventionStatus.AVSLUTAD,
                        LocalDate.now().minusWeeks(9),
                        Actor.KOMMUN),
                new Intervention(
                        "Riktad polisnarvaro Oxhagen",
                        "Oxhagen",
                        "Planerad insats kopplad till hot-spots for narkotikarelaterade handelser.",
                        InterventionStatus.PLANERAD,
                        LocalDate.now().plusWeeks(1),
                        Actor.POLIS)
        ));
    }

    private String descriptionFor(IncidentType type) {
        // Halften av handelserna saknar fritext, precis som i verkligheten.
        if (random.nextBoolean()) {
            return null;
        }
        return switch (type.getKind()) {
            case BROTT -> "Rapporterad handelse: " + type.getLabel().toLowerCase() + ".";
            case OTRYGGHET -> "Platsen upplevs otrygg: " + type.getLabel().toLowerCase() + ".";
        };
    }

    private int weightedIndex(double[] weights) {
        double sum = 0;
        for (double w : weights) {
            sum += w;
        }
        double r = random.nextDouble() * sum;
        double acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += weights[i];
            if (r <= acc) {
                return i;
            }
        }
        return weights.length - 1;
    }

    private double round(double v) {
        return Math.round(v * 1_000_000d) / 1_000_000d;
    }
}
