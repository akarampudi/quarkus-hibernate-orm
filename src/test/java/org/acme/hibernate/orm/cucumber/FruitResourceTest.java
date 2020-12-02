package org.acme.hibernate.orm.cucumber;

import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;

public class FruitResourceTest {

    private static WireMockServer wireMockServer;

    @Given("I want to stub the mock server")
    public void i_have_following_fruits_in_the_shop() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/resources/fruits"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("[{\"id\": \"1\",\"name\": \"Cherry\"}," +
                                "{\"id\": \"2\",\"name\": \"Apple\"}," +
                                "{\"id\": \"3\",\"name\": \"Banana\"}" +
                                "{\"id\": \"4\",\"name\": \"Pear\"}")));

        wireMockServer.stubFor(post(urlEqualTo("/resources/fruits"))
                .withRequestBody(equalToJson("{\"name\" : \"Pear\"}"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)));

    }

    @When("I want to add a new fruit to the shop")
    public void i_want_to_add_new_fruit_to_the_shop() {
        given().with()
                .body("{\"name\" : \"Pear\"}")
                .contentType("application/json")
                .when()
                .post("http://localhost:8089/resources/fruits")
                .then()
                .statusCode(201);
    }

    @Then("I found following fruits in the store")
    public void i_found_fruits_in_the_store(List<String> expectedFruits) {
        when().get("http://localhost:8089/resources/fruits")
                .then()
                .assertThat()
                .body(containsString(expectedFruits.get(0)),
                        containsString(expectedFruits.get(1)),
                        containsString(expectedFruits.get(2)),
                        containsString(expectedFruits.get(3)))
                .statusCode(200).log();
    }
}
