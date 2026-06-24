package se.lagesbild.analysis;

import java.util.List;

/**
 * Samlade svarsobjekt for analysvyn (lagesbild over tid, hot-spots, hot-times).
 */
public final class AnalysisDtos {

    private AnalysisDtos() {
    }

    public record Summary(
            long total,
            long brott,
            long otrygghet,
            long last30,
            long prev30,
            double changePct,
            String topArea
    ) {
    }

    /** En cell i veckopulsen: veckodag 1-7 (mandag-sondag) x timme 0-23. */
    public record HotTimeCell(int weekday, int hour, long count) {
    }

    public record AreaCount(String area, long total, long brott, long otrygghet) {
    }

    public record TrendPoint(String label, int isoWeek, long count) {
    }

    public record Overview(
            Summary summary,
            List<HotTimeCell> hotTimes,
            List<AreaCount> byArea,
            List<TrendPoint> trend
    ) {
    }
}
