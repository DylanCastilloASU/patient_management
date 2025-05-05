import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:4003";
    }

    @Test
    public void shouldReturnPatientsWithValidToken() {
        //Arrange
        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
            """;
        //Act & Assert
        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                //Assert
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("token");

        //Act & Assert
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .statusCode(200)
                .body("patients", notNullValue());
    }
}
