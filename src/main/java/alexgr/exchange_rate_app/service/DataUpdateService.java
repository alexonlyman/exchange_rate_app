package alexgr.exchange_rate_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataUpdateService {

    private final CurrencyUpdateService updateService;

    /**
     * Задача для периодического обновления курсов валют.
     * Выполняется каждые 30 минут.
     */
    @Scheduled(fixedRate = 1800000)
    public void updateData() {
        log.info("Запущена задача по обновлению курсов валют...");
        try {
            // Обновляем курсы валют с базовой валютой USD
            updateService.updateCurrencyRates("USD");
            log.info("Курсы валют успешно обновлены.");
        } catch (Exception e) {

            log.error("Ошибка при обновлении курсов валют: {}", e.getMessage(), e);
        }
    }
}
