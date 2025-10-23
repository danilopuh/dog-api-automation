package br.com.dogapi.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Dog API")
@Feature("Validação dos endpoints públicos")
@Owner("Nilo / QA Automation")
public class DogApiTest {

    private static final String BASE_URL = "https://dog.ceo/api";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @DisplayName("GET /breeds/list/all - Deve retornar todas as raças com sucesso")
    @Story("Listagem de raças")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida status code, tempo de resposta, schema e presença de raças na lista.")
    void listarTodasAsRacas() {
        Response resp =
            given()
                .log().ifValidationFails(LogDetail.ALL)
            .when()
                .get("/breeds/list/all")
            .then()
                .statusCode(200)
                .time(lessThan(2000L), TimeUnit.MILLISECONDS)
                .body("status", equalTo("success"))
                .extract().response();

        // Schema
        br.com.dogapi.utils.SchemaValidator.assertMatches(
                "schemas/list_all_schema.json", resp.asString());

        // Conteúdo mínimo esperado
        Map<String, List<String>> breeds = resp.jsonPath().getMap("message");
        assertThat("Lista de raças não deve ser vazia", breeds.keySet(), is(not(empty())));
        assertThat("Raça 'hound' deve existir", breeds.keySet(), hasItem("hound"));
    }

    @Test
    @DisplayName("GET /breed/{breed}/images - Deve retornar imagens válidas para uma raça conhecida")
    @Story("Imagens por raça")
    @Severity(SeverityLevel.NORMAL)
    void imagensPorRaca() {
        String breed = "hound";

        Response resp =
            given()
                .pathParam("breed", breed)
            .when()
                .get("/breed/{breed}/images")
            .then()
                .statusCode(200)
                .time(lessThan(3000L), TimeUnit.MILLISECONDS)
                .body("status", equalTo("success"))
                .extract().response();

        // Schema
        br.com.dogapi.utils.SchemaValidator.assertMatches(
                "schemas/breed_images_schema.json", resp.asString());

        List<String> images = resp.jsonPath().getList("message", String.class);
        assertThat("Deve retornar pelo menos 1 imagem", images.size(), greaterThan(0));
        assertThat("Todas as URLs devem ser http(s) válidas", images, everyItem(startsWith("http")));
    }

    @Test
    @DisplayName("GET /breeds/image/random - Deve retornar uma imagem aleatória válida")
    @Story("Imagem aleatória")
    @Severity(SeverityLevel.NORMAL)
    void imagemAleatoria() {
        Response resp =
            given()
            .when()
                .get("/breeds/image/random")
            .then()
                .statusCode(200)
                .time(lessThan(2000L), TimeUnit.MILLISECONDS)
                .body("status", equalTo("success"))
                .extract().response();

        // Schema
        br.com.dogapi.utils.SchemaValidator.assertMatches(
                "schemas/random_image_schema.json", resp.asString());

        String imageUrl = resp.jsonPath().getString("message");
        assertThat(imageUrl, startsWith("http"));
        assertThat("URL deve ter extensão de imagem", imageUrl, matchesPattern(".+\\.(jpg|jpeg|png)$"));
    }

    @Test
    @DisplayName("GET /breed/{breed}/images - Raça inexistente deve retornar erro coerente")
    @Story("Validação negativa")
    @Severity(SeverityLevel.MINOR)
    void imagensRacaInvalida() {
        String breed = "raca-inexistente-xyz";

        Response resp =
            given()
                .pathParam("breed", breed)
            .when()
                .get("/breed/{breed}/images")
            .then()
                // Algumas versões da API retornam 404, outras 200 com 'status:error'
                .statusCode(anyOf(is(404), is(200)))
                .extract().response();

        JsonPath jp = resp.jsonPath();
        if (resp.statusCode() == 200) {
            String status = jp.getString("status");
            assertThat("Quando 200, deve indicar erro no payload", status, equalTo("error"));
        }
    }

    @Test
    @DisplayName("Contrato e latência: /breeds/list/all em < 2.5s")
    @Severity(SeverityLevel.TRIVIAL)
    void contratoELatenciaBasica() {
        given()
        .when()
            .get("/breeds/list/all")
        .then()
            .statusCode(200)
            .time(lessThan(2500L), TimeUnit.MILLISECONDS)
            .body("status", equalTo("success"))
            .body("message", notNullValue());
    }
}