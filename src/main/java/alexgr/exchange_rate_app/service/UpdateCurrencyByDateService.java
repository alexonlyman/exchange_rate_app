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
public class CurService {
    private final CurrencyRepository repository;
    private final ApiConnectionUtil client;
    private final ObjectMapper objectMapper;


    public void saveChanges(String date, String base) throws IOException {
        // Получение данных от утилиты
        String ratesResponse = client.getRatesForDate(date, base);

        // Парсинг ответа
        JsonNode rootNode = objectMapper.readTree(ratesResponse);

        // Извлечение информации
        String responseDate = rootNode.get("date").asText(); // Дата из ответа
        String responseBase = rootNode.get("base").asText(); // Базовая валюта
        JsonNode rateNode = rootNode.get("rates");           // Узел с курсами валют

        LocalDateTime responseDateTime = LocalDate.parse(responseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atStartOfDay(); // Устанавливаем время в 00:00

        // Обновление записей в базе данных
        rateNode.fields().forEachRemaining(field -> {
            String currencyCode = field.getKey(); // Код валюты
            BigDecimal rate = field.getValue().decimalValue(); // Курс валюты

            // Поиск существующей записи по коду валюты
            CurrencyEntity entity = repository.findByCurrencyCode(currencyCode);

            if (entity == null) {
                // Если запись не найдена, создаем новую
                entity = new CurrencyEntity();
                entity.setCurrencyCode(currencyCode);
            }

            // Обновляем данные
            entity.setRates(rate);
            entity.setDateTime(responseDateTime);

            // Сохраняем запись в базе
            repository.save(entity);
        });

        log.info("Курсы валют успешно обновлены в базе данных на дату {} с базовой валютой {}.", responseDate, responseBase);
    }
}
