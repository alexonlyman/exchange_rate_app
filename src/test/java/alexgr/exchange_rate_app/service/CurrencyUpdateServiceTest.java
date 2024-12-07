package alexgr.exchange_rate_app.service;

import alexgr.exchange_rate_app.utils.ApiConnectionUtil;
import alexgr.exchange_rate_app.utils.CurrencyServiceHelper;
import alexgr.exchange_rate_app.utils.JsonParserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyUpdateServiceTest {

    @Mock
    private ApiConnectionUtil apiConnectionUtil;

    @Mock
    private CurrencyServiceHelper currencyServiceHelper;

    @Mock
    private JsonParserUtil jsonParserUtil;

    private CurrencyUpdateService currencyUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyUpdateService = new CurrencyUpdateService(apiConnectionUtil, currencyServiceHelper, jsonParserUtil);
    }

    @Test
    void testInitializeCurrencyDescriptions() throws IOException {
        // Подготовка тестовых данных
        String mockResponse = "{\"symbols\": {\"USD\": \"United States Dollar\", \"EUR\": \"Euro\"}}";
        JsonNode mockSymbolsNode = new ObjectMapper().readTree(mockResponse).get("symbols");

        when(apiConnectionUtil.getConnectionToInitCurrencyDescription()).thenReturn(mockResponse);
        when(jsonParserUtil.parseJson(mockResponse, "symbols")).thenReturn(mockSymbolsNode);

        // Вызов метода
        currencyUpdateService.initializeCurrencyDescriptions();

        // Проверка, что helper вызван для каждой валюты
        verify(currencyServiceHelper).saveOrUpdateCurrency("USD", null, null, "United States Dollar");
        verify(currencyServiceHelper).saveOrUpdateCurrency("EUR", null, null, "Euro");
        verifyNoMoreInteractions(currencyServiceHelper);
    }

    @Test
    void testUpdateCurrencyRates() throws IOException {
        // Подготовка тестовых данных
        String mockResponse = "{\"rates\": {\"USD\": 1.0, \"EUR\": 0.85}}";
        JsonNode mockRatesNode = new ObjectMapper().readTree(mockResponse).get("rates");

        when(apiConnectionUtil.getConnectionToExchangeRate("USD")).thenReturn(mockResponse);
        when(jsonParserUtil.parseJson(mockResponse, "rates")).thenReturn(mockRatesNode);

        // Вызов метода
        currencyUpdateService.updateCurrencyRates("USD");

        // Проверка, что helper вызван для каждой валюты
        verify(currencyServiceHelper).saveOrUpdateCurrency(eq("USD"), eq(BigDecimal.valueOf(1.0)), any(LocalDateTime.class), isNull());
        verify(currencyServiceHelper).saveOrUpdateCurrency(eq("EUR"), eq(BigDecimal.valueOf(0.85)), any(LocalDateTime.class), isNull());
        verifyNoMoreInteractions(currencyServiceHelper);
    }

}