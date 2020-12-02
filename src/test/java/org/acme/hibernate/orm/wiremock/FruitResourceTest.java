package org.acme.hibernate.orm.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

public class FruitResourceTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor("localhost", 8089);
        setupStub();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    private void setupStub() {
        wireMockServer.stubFor(get(urlEqualTo("/resources/fruits"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("[{\"id\": \"1\",\"name\": \"Cherry\"}," +
                                "{\"id\": \"2\",\"name\": \"Apple\"}," +
                                "{\"id\": \"3\",\"name\": \"Banana\"}")));

        wireMockServer.stubFor(delete(urlPathMatching("/resources/fruits/[0-9]+"))
                .willReturn(aResponse()
                        .withStatus(204)));

        // with proxy server
//        wireMockServer.stubFor(get(urlEqualTo("/resources/fruits"))
//                .willReturn(aResponse()
//                        .proxiedFrom("http://localhost:8080/")));
    }

    @Test
    void getAllFruits() throws Exception {
        // Test with Rest-Assured
        when().get("http://localhost:8089/resources/fruits")
                .then()
                .body(containsString("Cherry"),
                        containsString("Apple"),
                        containsString("Banana"),
                        not(containsString("Pear")))
                .statusCode(200).log();

        // Test with HttpClient
        // THEN
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:8089/resources/fruits");
        httpClient.execute(request);

        // VERIFY
        verify(getRequestedFor(urlEqualTo("/resources/fruits")));
    }

    @Test
    void deleteFruit() {
        when().delete("http://localhost:8089/resources/fruits/1")
                .then()
                .statusCode(204).log();
    }
}


