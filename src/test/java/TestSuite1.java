import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestSuite1 {

    static final String SERVICE_ACCESS_KEY = "f5c75d2ff5c75d2ff5c75d2f07f5a9ff2eff5c7f5c75d2fa83fc83894978f71f3d858c3";
    static final String query = "https://api.vk.com/method/users.get?" +
            "user_ids=1,210700286" +
            "&access_token=" + SERVICE_ACCESS_KEY +
            "&v=5.103";

    // Обращение к API VK средствами стандартной библиотеки и при помощи Rest Assured
    public void compareStandardLibraryAndRestAssured() {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(query).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(500);
            connection.setReadTimeout(500);

            connection.connect();

            StringBuilder builder = new StringBuilder();

            if(HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                for(String line = ""; (line = in.readLine()) != null; ) {
                    builder.append(line);
                    builder.append("\n");
                }
                System.out.println(query);
                System.out.println(builder);
            }
        } catch (Throwable cause) {
            System.out.println("Всё плохо: " + cause.getMessage());
            cause.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        baseURI = "https://api.vk.com/method/users.get";
        Response response =
                given()
                        .param("user_ids", "1,210700286")
                        .param("access_token", SERVICE_ACCESS_KEY)
                        .param("v", "5.103")
                .when()
                        .get();

        System.out.println("Response Body is =>  " + response.asString());
    }

    @Test
    @Description("Проверяем, есть ли связь с сервером")
    public void checkServerStatusTest() {
        baseURI = "https://api.vk.com/method/users.get";
        int statusCode = given().
                param("user_ids", "1,210700286").
                param("access_token", SERVICE_ACCESS_KEY).
                param("v", "5.103").
        when().
                get().
                statusCode();

        Assertions.assertEquals(200, statusCode);
    }

    @Test
    @Description("Проверяем, соответствует ли ответ JSON-схеме")
    public void isValidJsonResponseTest() {
        baseURI = "https://api.vk.com/method/users.get";
        File jsonSchema = new File("/Users/user/IdeaProjects/rest-assured/src/test/java/jsonSchema");
        given().
                param("user_ids", "1,210700286").
                param("access_token", SERVICE_ACCESS_KEY).
                param("v", "5.103").
        when().
                get().
        then().
                assertThat().
                body(matchesJsonSchema(jsonSchema));
    }

    @Test
    @Description("Проверяем, о тех ли пользователях получили информацию")
    public void checkIdsFromUsersGetTest() {
        baseURI = "https://api.vk.com/method/users.get";
        given().
                param("user_ids", "1,210700286").
                param("access_token", SERVICE_ACCESS_KEY).
                param("v", "5.103").
                when().
                get().
                then().
                body("response.id", hasItems(1, 210700286));
    }

}
