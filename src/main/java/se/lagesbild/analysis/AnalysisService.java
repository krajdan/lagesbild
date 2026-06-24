package se.lagesbild.analysis;

import org.springframework.stereotype.Service;
import se.lagesbild.incident.Incident;
import se.lagesbild.incident.IncidentKind;
import se.lagesbild.incident.IncidentRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {

    private static final int TREND_WEEKS = 12;

    private final IncidentRepository repository;

    public AnalysisService(IncidentRepository repository) {
        this.repository = repository;
    }

    public AnalysisDtos.Overview overview() {
        List<Incident> all = repository.findAll();
        return new AnalysisDtos.Overview(
                summary(all),
                hotTimes(all),
                byArea(all),
                trend(all)
        );
    }

    private AnalysisDtos.Summary summary(List<Incident> all) {
        long total = all.size();
        long brott = all.stream().filter(i -> i.getKind() == IncidentKind.BROTT).count();
        long otrygghet = total - brott;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime d30 = now.minusDays(30);
        LocalDateTime d60 = now.minusDays(60);

        long last30 = all.stream().filter(i -> i.getOccurredAt().isAfter(d30)).count();
        long prev30 = all.stream()
                .filter(i -> i.getOccurredAt().isAfter(d60) && !i.getOccurredAt().isAfter(d30))
                .count();

        double changePct = prev30 == 0 ? 0.0
                : Math.round(((double) (last30 - prev30) / prev30) * 1000.0) / 10.0;

        String topArea = byArea(all).stream()
                .findFirst()
                .map(AnalysisDtos.AreaCount::area)
                .orElse("-");

        return new AnalysisDtos.Summary(total, brott, otrygghet, last30, prev30, changePct, topArea);
    }

    /** Bygger 7x24-matrisen (veckopulsen). Alltid full matris sa frontend slipper luckor. */
    private List<AnalysisDtos.HotTimeCell> hotTimes(List<Incident> all) {
        long[][] grid = new long[8][24]; // index 1-7 for veckodag
        for (Incident i : all) {
            int weekday = i.getOccurredAt().getDayOfWeek().getValue(); // 1=mandag .. 7=sondag
            int hour = i.getOccurredAt().getHour();
            grid[weekday][hour]++;
        }
        List<AnalysisDtos.HotTimeCell> cells = new ArrayList<>();
        for (int wd = 1; wd <= 7; wd++) {
            for (int h = 0; h < 24; h++) {
                cells.add(new AnalysisDtos.HotTimeCell(wd, h, grid[wd][h]));
            }
        }
        return cells;
    }

    private List<AnalysisDtos.AreaCount> byArea(List<Incident> all) {
        Map<String, long[]> acc = new LinkedHashMap<>(); // [total, brott, otrygghet]
        for (Incident i : all) {
            long[] c = acc.computeIfAbsent(i.getArea(), k -> new long[3]);
            c[0]++;
            if (i.getKind() == IncidentKind.BROTT) {
                c[1]++;
            } else {
                c[2]++;
            }
        }
        return acc.entrySet().stream()
                .map(e -> new AnalysisDtos.AreaCount(e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2]))
                .sorted(Comparator.comparingLong(AnalysisDtos.AreaCount::total).reversed())
                .toList();
    }

    private List<AnalysisDtos.TrendPoint> trend(List<Incident> all) {
        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<AnalysisDtos.TrendPoint> points = new ArrayList<>();
        for (int k = TREND_WEEKS - 1; k >= 0; k--) {
            LocalDate weekStart = thisMonday.minusWeeks(k);
            LocalDate weekEnd = weekStart.plusWeeks(1);
            long count = all.stream()
                    .filter(i -> {
                        LocalDate d = i.getOccurredAt().toLocalDate();
                        return !d.isBefore(weekStart) && d.isBefore(weekEnd);
                    })
                    .count();
            int isoWeek = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            points.add(new AnalysisDtos.TrendPoint("v." + isoWeek, isoWeek, count));
        }
        return points;
    }
}
