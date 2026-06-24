package se.lagesbild.config;

import java.util.List;

/**
 * Omraden i Orebro med ungefarliga centrumkoordinater. Anvands bade for
 * seed-data och for omradesvalet i granssnittet.
 */
public final class AreaCatalog {

    public record Area(String name, double lat, double lng) {
    }

    public static final List<Area> AREAS = List.of(
            new Area("Centrum", 59.2741, 15.2066),
            new Area("Vivalla", 59.3056, 15.1542),
            new Area("Varberga", 59.2939, 15.1685),
            new Area("Oxhagen", 59.2986, 15.1789),
            new Area("Baronbackarna", 59.2884, 15.1817),
            new Area("Brickebacken", 59.2360, 15.2401),
            new Area("Markbacken", 59.2829, 15.1875),
            new Area("Mellringe", 59.2700, 15.1620),
            new Area("Adolfsberg", 59.2362, 15.2120),
            new Area("Hovsta", 59.3500, 15.2300)
    );

    private AreaCatalog() {
    }

    public static List<String> names() {
        return AREAS.stream().map(Area::name).toList();
    }
}
