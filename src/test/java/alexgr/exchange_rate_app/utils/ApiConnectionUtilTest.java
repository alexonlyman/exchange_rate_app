package alexgr.exchange_rate_app.utils;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class ApiConnectionUtilTest {

    private MockWebServer mockWebServer;
    private ApiConnectionUtil apiConnectionUtil;


    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String BASE_URL = mockWebServer.url("http://localhost/").toString(); // Подменяем URL на локальный сервер
        apiConnectionUtil = new ApiConnectionUtil();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    @Test
    void testGetConnectionToInitCurrencyDescription_Success() throws IOException {
        // Настраиваем ответ MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"symbols\": {\"USD\": \"US Dollar\", \"EUR\": \"Euro\"}}"));

        // Вызываем метод
        String response = apiConnectionUtil.getConnectionToInitCurrencyDescription();

        // Проверяем результат
        assertNotNull(response);
        assertTrue(response.contains("USD"));
    }
    @Test
    void testGetConnectionToExchangeRate_Success() throws IOException {
        // Настраиваем ответ MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"base\": \"USD\", \"rates\": {\"EUR\": 0.85, \"GBP\": 0.75}}"));

        // Вызываем метод
        String response = apiConnectionUtil.getConnectionToExchangeRate("USD");

        // Проверяем результат
        assertNotNull(response);
        assertTrue(response.contains("EUR"));
    }
    @Test
    void testGetRatesForDate_InvalidDate() {
        // Проверяем выброс исключения на некорректную дату
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            apiConnectionUtil.getRatesForDate("2023-01-01", "USD");
        });

        assertEquals("Дата должна быть в пределах последних 5 дней.", exception.getMessage());
    }

    @Test
    void testGetRatesForDate_Success() throws IOException {
        // Настраиваем ответ MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"base\": \"USD\", \"date\": \"2024-12-05\", \"rates\": {\"EUR\": 0.85}}"));

        // Вызываем метод
        String response = apiConnectionUtil.getRatesForDate("2024-12-05", "USD");

        // Проверяем результат
        assertNotNull(response);
        assertTrue(response.contains("EUR"));
    }


}