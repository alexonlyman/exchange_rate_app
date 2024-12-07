package alexgr.exchange_rate_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppStartUpRunnerService implements CommandLineRunner {
    private final CurrencyUpdateService service;


    /**
     * Метод выполняется при запуске приложения.
     * Инициализирует описания валют и обновляет курсы валют с базовой валютой по умолчанию.
     *
     * @param args аргументы командной строки (не используются).
     */
    @Override
    public void run(String... args) {
        log.info("Начало инициализации приложения...");
        try {
            // Инициализация описаний валют
            service.initializeCurrencyDescriptions();

            // Установка базовой валюты по умолчанию
            String defaultCurrency = "USD";

            // Обновление курсов валют
            service.updateCurrencyRates(defaultCurrency);

            log.info("Инициализация приложения успешно завершена.");
        } catch (Exception e) {
            // Логируем ошибки, если инициализация не удалась
            log.error("Ошибка при инициализации приложения: {}", e.getMessage(), e);
        }
    }
}
