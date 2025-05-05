import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AuthIntegrationTest {
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4003"; //link to api-gateway
    }

    //1. Arrange - Setup
    //2. Act - Conduct Test
    //3. Assert - Compare

    @Test
    public void shouldReturnOKWithValidJWTToken() {
        //Arrange
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
            """;
        //Act & Assert
        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                //Assert
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidEmail() {
        //Arrange
        String loginPayload = """
                {
                    "email": "invalidUser@test.com",
                    "password": "password123"
                }
            """;
        //Act & Assert
        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                //Assert
                .then()
                .statusCode(401);
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidPassword() {
        //Arrange
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "invalidPassword"
                }
            """;
        //Act & Assert
        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                //Assert
                .then()
                .statusCode(401);
    }

    //@Test
    //public void shouldReturnValidWithValidJWTToken() {}

    //@Test
    //public void shouldReturnUnauthorizedWithInvalidJWTToken() {}

}
