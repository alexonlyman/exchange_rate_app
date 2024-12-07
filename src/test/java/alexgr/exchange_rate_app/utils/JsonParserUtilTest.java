package alexgr.exchange_rate_app.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserUtilTest {

    private JsonParserUtil jsonParserUtil;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonParserUtil = new JsonParserUtil(objectMapper);
    }

    @Test
    void parseJson_ShouldReturnCorrectNode_WhenFieldExists() throws Exception {

        String jsonResponse = "{ \"field1\": \"value1\", \"field2\": { \"nestedField\": 123 } }";
        String fieldName = "field2";

        JsonNode result = jsonParserUtil.parseJson(jsonResponse, fieldName);

        assertNotNull(result);
        assertTrue(result.has("nestedField"));
        assertEquals(123, result.get("nestedField").asInt());
    }

    @Test
    void parseJson_ShouldThrowException_WhenFieldDoesNotExist() {

        String jsonResponse = "{ \"field1\": \"value1\" }";
        String fieldName = "nonexistentField";


        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                jsonParserUtil.parseJson(jsonResponse, fieldName)
        );
        assertEquals("JSON не содержит поле 'nonexistentField'", exception.getMessage());
    }

    @Test
    void parseJson_ShouldThrowException_WhenJsonIsInvalid() {

        String invalidJsonResponse = "{ invalid json }";
        String fieldName = "field";


        Exception exception = assertThrows(IOException.class, () ->
                jsonParserUtil.parseJson(invalidJsonResponse, fieldName)
        );
        assertTrue(exception.getMessage().contains("Unexpected character"));
    }

}