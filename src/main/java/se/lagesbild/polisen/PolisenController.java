package se.lagesbild.polisen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/polisen")
public class PolisenController {

    private final PolisenClient client;

    public PolisenController(PolisenClient client) {
        this.client = client;
    }

    @GetMapping("/events")
    public List<PolisenEvent> events() {
        return client.events();
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> body = new HashMap<>();
        body.put("live", client.isLive());
        body.put("count", client.events().size());
        Instant updated = client.lastUpdated();
        body.put("lastUpdated", updated != null ? updated.toString() : null);
        return body;
    }
}
