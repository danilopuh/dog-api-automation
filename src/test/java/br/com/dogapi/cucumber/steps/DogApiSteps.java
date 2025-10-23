package br.com.dogapi.cucumber.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.E;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import br.com.dogapi.utils.SchemaValidator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DogApiSteps {
    
    private Response response;
    private String baseUrl = "https://dog.ceo/api";
    
    @Dado("que a Dog API está disponível")
    public void queADogApiEstaDisponivel() {
        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @Dado("que existe a raça {string}")
    public void queExisteARaca(String raca) {
        // Verificação prévia de que a raça existe
        Response racasResponse = given()
            .when()
            .get("/breeds/list/all");
        
        Map<String, List<String>> breeds = racasResponse.jsonPath().getMap("message");
        assertThat("A raça " + raca + " deve existir", breeds.keySet(), hasItem(raca));
    }
    
    @Quando("eu consulto a lista de todas as raças")
    public void euConsultoAListaDeTodasAsRacas() {
        response = given()
            .log().ifValidationFails()
            .when()
            .get("/breeds/list/all");
    }
    
    @Quando("eu consulto as imagens da raça {string}")
    public void euConsultoAsImagensDaRaca(String raca) {
        response = given()
            .pathParam("breed", raca)
            .log().ifValidationFails()
            .when()
            .get("/breed/{breed}/images");
    }
    
    @Quando("eu solicito uma imagem aleatória")
    public void euSolicitoUmaImagemAleatoria() {
        response = given()
            .log().ifValidationFails()
            .when()
            .get("/breeds/image/random");
    }
    
    @Então("a resposta deve ter status {int}")
    public void aRespostaDeveTerStatus(int statusCode) {
        assertThat("Status code deve ser " + statusCode, 
                   response.getStatusCode(), equalTo(statusCode));
    }
    
    @E("o tempo de resposta deve ser menor que {double} segundos")
    public void oTempoDeRespostaDeveSerMenorQueSegundos(double segundos) {
        long tempoLimite = (long) (segundos * 1000);
        assertThat("Tempo de resposta deve ser menor que " + segundos + " segundos",
                   response.getTime(), lessThan(tempoLimite));
    }
    
    @E("a resposta deve conter o status {string}")
    public void aRespostaDeveConterOStatus(String status) {
        assertThat("Status da resposta deve ser " + status,
                   response.jsonPath().getString("status"), equalTo(status));
    }
    
    @E("a lista de raças não deve estar vazia")
    public void aListaDeRacasNaoDeveEstarVazia() {
        Map<String, List<String>> breeds = response.jsonPath().getMap("message");
        assertThat("Lista de raças não deve estar vazia", 
                   breeds.keySet(), is(not(empty())));
    }
    
    @E("a raça {string} deve estar presente na lista")
    public void aRacaDeveEstarPresenteNaLista(String raca) {
        Map<String, List<String>> breeds = response.jsonPath().getMap("message");
        assertThat("Raça '" + raca + "' deve estar presente",
                   breeds.keySet(), hasItem(raca));
    }
    
    @E("a resposta deve seguir o schema JSON de lista de raças")
    public void aRespostaDeveSeguirOSchemaJsonDeListaDeRacas() {
        SchemaValidator.assertMatches("schemas/list_all_schema.json", response.asString());
    }
    
    @E("deve retornar pelo menos {int} imagem")
    public void deveRetornarPeloMenosImagem(int quantidade) {
        List<String> images = response.jsonPath().getList("message", String.class);
        assertThat("Deve retornar pelo menos " + quantidade + " imagem(ns)",
                   images.size(), greaterThanOrEqualTo(quantidade));
    }
    
    @E("todas as URLs devem ser válidas \\(começar com http)")
    public void todasAsUrlsDevemSerValidasComecaarComHttp() {
        List<String> images = response.jsonPath().getList("message", String.class);
        assertThat("Todas as URLs devem começar com http",
                   images, everyItem(startsWith("http")));
    }
    
    @E("a resposta deve seguir o schema JSON de imagens de raça")
    public void aRespostaDeveSeguirOSchemaJsonDeImagensDeRaca() {
        SchemaValidator.assertMatches("schemas/breed_images_schema.json", response.asString());
    }
    
    @E("a URL da imagem deve ser válida")
    public void aUrlDaImagemDeveSerValida() {
        String imageUrl = response.jsonPath().getString("message");
        assertThat("URL da imagem deve começar com http",
                   imageUrl, startsWith("http"));
    }
    
    @E("a URL deve ter uma extensão de imagem válida")
    public void aUrlDeveTerUmaExtensaoDeImagemValida() {
        String imageUrl = response.jsonPath().getString("message");
        assertThat("URL deve ter extensão de imagem válida",
                   imageUrl, matchesPattern(".+\\.(jpg|jpeg|png)$"));
    }
    
    @E("a resposta deve seguir o schema JSON de imagem aleatória")
    public void aRespostaDeveSeguirOSchemaJsonDeImagemAleatoria() {
        SchemaValidator.assertMatches("schemas/random_image_schema.json", response.asString());
    }
    
    @Então("a resposta deve indicar erro para raça inexistente")
    public void aRespostaDeveIndicarErroParaRacaInexistente() {
        // A API pode retornar 404 ou 200 com status "error"
        int statusCode = response.getStatusCode();
        
        if (statusCode == 200) {
            String status = response.jsonPath().getString("status");
            assertThat("Quando status 200, deve indicar erro no payload",
                       status, equalTo("error"));
        } else {
            assertThat("Deve retornar 404 para raça inexistente",
                       statusCode, equalTo(404));
        }
    }
    
    @E("a mensagem não deve ser nula")
    public void aMensagemNaoDeveSerNula() {
        Object message = response.jsonPath().get("message");
        assertThat("Mensagem não deve ser nula", message, notNullValue());
    }
}