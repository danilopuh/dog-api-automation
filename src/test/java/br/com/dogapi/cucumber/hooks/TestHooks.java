package br.com.dogapi.cucumber.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

public class TestHooks {
    
    @Before
    public void setUp(Scenario scenario) {
        // Configurar RestAssured
        RestAssured.baseURI = "https://dog.ceo/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Adicionar filtro do Allure
        RestAssured.filters(new AllureRestAssured());
        
        // Log do início do cenário
        System.out.println("Iniciando cenário: " + scenario.getName());
    }
    
    @After
    public void tearDown(Scenario scenario) {
        // Log do resultado do cenário
        if (scenario.isFailed()) {
            System.out.println("Cenário falhou: " + scenario.getName());
        } else {
            System.out.println("Cenário passou: " + scenario.getName());
        }
        
        // Limpar filtros do RestAssured
        RestAssured.reset();
    }
}