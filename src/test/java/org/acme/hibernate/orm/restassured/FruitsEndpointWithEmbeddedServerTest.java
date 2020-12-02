package org.acme.hibernate.orm.restassured;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class FruitsEndpointWithEmbeddedServerTest {
    
    @Test
    public void testListAllFruits() {
        //List all, should have all 3 fruits the database has initially:
        given()
                .when().basePath("resources").get("/fruits")
                .then()
                .body(containsString("Cherry"),
                        containsString("Apple"),
                        containsString("Banana"))
                .statusCode(200).log();

    }

    @Test
    void deleteFruit() {
        //Delete the Cherry:
        given().pathParam("id", "1")
                .when().basePath("resources").delete("/fruits/{id}")
                .then()
                .statusCode(204);

        //List all, cherry should be missing now:
        given()
                .when().basePath("resources").get("/fruits")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("Cherry")),
                        containsString("Apple"),
                        containsString("Banana"));

        //Create the Cherry:
        given().with()
                .body("{\"name\" : \"Cherry\"}")
                .contentType("application/json")
                .when()
                .basePath("resources")
                .post("/fruits")
                .then()
                .statusCode(201);

    }

    @Test
    void createFruit() {

        //Create the Pear:
        given().with()
                .body("{\"name\" : \"Pear\"}")
                .contentType("application/json")
                .when()
                .basePath("resources")
                .post("/fruits")
                .then()
                .statusCode(201);

        //List all, cherry should be missing now:
        given()
                .when().basePath("resources").get("/fruits")
                .then()
                .statusCode(200)
                .body(containsString("Cherry"),
                        containsString("Apple"),
                        containsString("Banana"),
                        containsString("Pear"));
    }
}
