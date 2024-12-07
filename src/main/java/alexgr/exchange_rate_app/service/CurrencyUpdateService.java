package alexgr.exchange_rate_app.service;

import alexgr.exchange_rate_app.utils.ApiConnectionUtil;
import alexgr.exchange_rate_app.utils.CurrencyServiceHelper;
import alexgr.exchange_rate_app.utils.JsonParserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyUpdateService {
    private final ApiConnectionUtil client;
    private final CurrencyServiceHelper helper;
    private final JsonParserUtil jsonParserUtil;

    /**
     * Инициализация описаний валют.
     *
     * Метод извлекает данные о валютах (код и описание) с внешнего API
     * и сохраняет их в базу данных. Значения курса и даты не обновляются.
     *
     * @throws IOException если произошла ошибка при запросе к API или парсинге данных.
     */
    public void initializeCurrencyDescriptions() throws IOException {
        log.info("Инициализация описаний валют...");

        // Получение и парсинг JSON-данных с описаниями валют
        JsonNode symbolsNode = jsonParserUtil.parseJson(
                client.getConnectionToInitCurrencyDescription(), "symbols");

        log.debug("Получены описания валют: {}", symbolsNode);

        // Сохранение описаний валют в базу данных
        symbolsNode.fields().forEachRemaining(field -> {
            String currencyCode = field.getKey();
            String description = field.getValue().asText();

            log.debug("Сохранение валюты: Код = {}, Описание = {}", currencyCode, description);

            helper.saveOrUpdateCurrency(currencyCode, null, null, description);
        });

        log.info("Инициализация описаний валют завершена.");
    }

    /**
     * Обновление курсов валют.
     *
     * <p>Метод извлекает данные о текущих курсах валют относительно заданной базовой валюты
     * с внешнего API и сохраняет их в базу данных. Поле описания валют не обновляется.
     *
     * @param baseCurrency базовая валюта (например, "USD").
     * @throws IOException если произошла ошибка при запросе к API или парсинге данных.
     */
    public void updateCurrencyRates(String baseCurrency) throws IOException {
        log.info("Обновление курсов валют для базовой валюты {}...", baseCurrency);

        // Получение и парсинг JSON-данных с курсами валют
        JsonNode ratesNode = jsonParserUtil.parseJson(
                client.getConnectionToExchangeRate(baseCurrency), "rates");

        log.debug("Получены курсы валют: {}", ratesNode);

        // Сохранение курсов валют в базу данных
        ratesNode.fields().forEachRemaining(field -> {
            String currencyCode = field.getKey();
            BigDecimal rate = field.getValue().decimalValue();

            log.debug("Сохранение курса валюты: Код = {}, Курс = {}", currencyCode, rate);

            helper.saveOrUpdateCurrency(currencyCode, rate, LocalDateTime.now(), null);
        });

        log.info("Обновление курсов валют завершено.");
    }
}
