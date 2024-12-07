package alexgr.exchange_rate_app.service;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import alexgr.exchange_rate_app.repository.CurrencyRepository;
import alexgr.exchange_rate_app.utils.ApiConnectionUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateCurrencyByDateService {
    private final CurrencyRepository repository;
    private final ApiConnectionUtil client;
    private final ObjectMapper objectMapper;

    /**
     * Сохраняет или обновляет курсы валют в базе данных на указанную дату.
     *
     * @param date дата в формате "yyyy-MM-dd".
     * @param base базовая валюта (например, "USD").
     * @throws IOException если произошла ошибка при запросе к API или парсинге данных.
     */
    public void saveChanges(String date, String base) throws IOException {
        log.info("Запуск обновления курсов валют на дату {} с базовой валютой {}.", date, base);

        // Получение данных от API
        String ratesResponse = client.getRatesForDate(date, base);
        log.debug("Получен ответ от API: {}", ratesResponse);

        // Парсинг JSON-ответа
        JsonNode rootNode = objectMapper.readTree(ratesResponse);
        log.debug("JSON успешно распарсен: {}", rootNode);

        // Извлечение даты и базовой валюты из ответа
        String responseDate = rootNode.get("date").asText();
        String responseBase = rootNode.get("base").asText();
        JsonNode rateNode = rootNode.get("rates");

        LocalDateTime responseDateTime = LocalDate.parse(responseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay();

        log.info("Начало обновления курсов валют для даты {} и базовой валюты {}.", responseDate, responseBase);

        // Обработка каждой валюты и обновление базы данных
        rateNode.fields().forEachRemaining(field -> {
            String currencyCode = field.getKey();
            BigDecimal rate = field.getValue().decimalValue();

            log.debug("Обработка валюты {} с курсом {}.", currencyCode, rate);

            // Поиск существующей записи в базе данных
            CurrencyEntity entity = repository.findByCurrencyCode(currencyCode);

            if (entity == null) {
                log.debug("Валюта {} не найдена в базе данных. Создаем новую запись.", currencyCode);
                entity = new CurrencyEntity();
                entity.setCurrencyCode(currencyCode);
            }

            // Обновление данных в сущности
            entity.setRates(rate);
            entity.setDateTime(responseDateTime);

            // Сохранение сущности в базе данных
            repository.save(entity);
            log.debug("Курс валюты {} обновлен/сохранен в базе данных.", currencyCode);
        });

        log.info("Курсы валют успешно обновлены в базе данных на дату {} с базовой валютой {}.", responseDate, responseBase);
    }
}
