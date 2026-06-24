package se.lagesbild;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LagesbildApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    @Test
    void contextLoads() {
    }

    @Test
    void incidentsAreSeededAndServed() {
        Object[] body = rest.getForObject("http://localhost:" + port + "/api/incidents", Object[].class);
        assertThat(body).isNotNull();
        assertThat(body.length).isGreaterThan(100);
    }

    @Test
    void analysisOverviewIsAvailable() {
        String json = rest.getForObject("http://localhost:" + port + "/api/analysis/overview", String.class);
        assertThat(json).contains("summary").contains("hotTimes").contains("byArea").contains("trend");
    }
}
