package juja.microservices.acceptance;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import juja.microservices.keepers.Keepers;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.Reader;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder.mongoDb;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Danil Kuznetsov
 * @author Dmitriy Lyashenko
 * @author Vadim Dyachenko
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {Keepers.class})
@DirtiesContext
public class BaseAcceptanceTest {

    @LocalServerPort
    int localPort;

    @Rule
    public MongoDbRule mongoDbRule = new MongoDbRule(
            mongoDb()
                    .databaseName("keepers")
                    .host("127.0.0.1")
                    .port(27017)
                    .build()
    );

    @Before
    public void setup() {
        RestAssured.port = localPort;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    String convertToString(Reader reader) throws IOException {
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
            buffer.append(arr, 0, numCharsRead);
        }
        return buffer.toString();
    }

    void printConsoleReport(String url, String expectedResponse, ResponseBody actualResponse) throws IOException {

        System.out.println("\n\n URL  - " + url);

        System.out.println("\n Actual Response :\n");
        actualResponse.prettyPrint();

        System.out.println("\nExpected Response :");
        System.out.println("\n" + expectedResponse + "\n\n");
    }

    Response getResponse(String url, String jsonContentRequest, HttpMethod method) {
        Response response = getComonResponse(url, jsonContentRequest, method);
        return response
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    Response getRealResponse(String url, String jsonContentRequest, HttpMethod method) {
        Response response = getComonResponse(url, jsonContentRequest, method);
        return response
                .then()
                .extract()
                .response();
    }

    private Response getComonResponse(String url, String jsonContentRequest, HttpMethod method) {
        RequestSpecification specification = given()
                .contentType("application/json")
                .body(jsonContentRequest)
                .when();
        Response response;
        if (HttpMethod.POST == method) {
            response = specification.post(url);
        } else if (HttpMethod.GET == method) {
            response = specification.get(url);
        } else if (HttpMethod.PUT == method) {
            response = specification.put(url);
        } else {
            throw new RuntimeException("Unsupported HttpMethod in getResponse()");
        }
        return response;
    }
}
