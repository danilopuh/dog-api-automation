package br.com.dogapi.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matcher;
import java.io.InputStream;
import static org.hamcrest.MatcherAssert.assertThat;

public class SchemaValidator {
    public static void assertMatches(String schemaResourceOnClasspath, String json) {
        InputStream schemaStream = SchemaValidator.class.getClassLoader().getResourceAsStream(schemaResourceOnClasspath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema não encontrado no classpath: " + schemaResourceOnClasspath);
        }
        
        // Usando explicitamente o tipo correto para o matcher
        Matcher<? super String> validator = JsonSchemaValidator.matchesJsonSchema(schemaStream);
        assertThat("JSON não corresponde ao schema: " + schemaResourceOnClasspath, json, validator);
    }
}