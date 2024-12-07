package alexgr.exchange_rate_app.utils;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import alexgr.exchange_rate_app.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceHelper {
    private final CurrencyRepository repository;

    /**
     * Сохраняет или обновляет информацию о валюте в базе данных.
     *
     * @param code        Код валюты (например, USD, EUR).
     * @param rate        Курс валюты.
     * @param dateTime    Дата и время обновления курса.
     * @param description Описание валюты (например, "US Dollar"). Может быть null.
     */
    public void saveOrUpdateCurrency(String code, BigDecimal rate, LocalDateTime dateTime, String description) {
        log.info("Начало сохранения/обновления информации о валюте: {}", code);
        try {
            // Проверка наличия записи о валюте в базе данных
            CurrencyEntity entity = repository.findByCurrencyCode(code);

            // Если запись отсутствует, создаем новую
            if (entity == null) {
                log.debug("Валюта с кодом {} отсутствует в базе. Создаём новую запись.", code);
                entity = new CurrencyEntity();
                entity.setCurrencyCode(code);
            }

            // Обновляем данные валюты
            entity.setRates(rate);
            entity.setDateTime(dateTime);

            // Обновляем описание, если оно передано
            if (description != null) {
                entity.setCurrencyDescription(description);
            }

            // Сохраняем запись в базе данных
            repository.save(entity);
            log.info("Валюта сохранена/обновлена: {} - Курс: {}, Описание: {}", code, rate, description);
        } catch (Exception e) {
            // Логируем ошибку, если что-то пошло не так
            log.error("Ошибка при сохранении/обновлении валюты {}: {}", code, e.getMessage(), e);
        }
    }
}
