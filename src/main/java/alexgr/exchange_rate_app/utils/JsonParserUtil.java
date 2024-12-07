package alexgr.exchange_rate_app.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JsonParserUtil {
    private final ObjectMapper objectMapper;

    public JsonParserUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Метод для извлечения указанного поля из JSON-ответа.
     *
     * @param response  строка в формате JSON
     * @param fieldName имя поля, которое нужно извлечь
     * @return JsonNode - узел с данными из указанного поля
     * @throws IOException если возникла ошибка при обработке JSON
     * @throws IllegalArgumentException если поле отсутствует в JSON
     */
    public JsonNode parseJson(String response, String fieldName) throws IOException {
        // Логируем начало обработки JSON-строки
        log.debug("Начинаем парсинг JSON-строки. Ищем поле: {}", fieldName);

        // Парсим JSON-строку в дерево объектов
        JsonNode rootNode = objectMapper.readTree(response);
        log.debug("JSON успешно разобран в дерево объектов.");

        // Извлекаем узел с указанным именем поля
        JsonNode targetNode = rootNode.get(fieldName);

        // Если поле отсутствует, логируем предупреждение и выбрасываем исключение
        if (targetNode == null) {
            log.warn("Поле '{}' отсутствует в переданном JSON.", fieldName);
            throw new IllegalArgumentException("JSON не содержит поле '" + fieldName + "'");
        }

        // Логируем успешное извлечение поля
        log.debug("Поле '{}' успешно извлечено из JSON.", fieldName);

        // Возвращаем извлеченный узел
        return targetNode;
    }
}
