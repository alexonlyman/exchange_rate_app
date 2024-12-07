package alexgr.exchange_rate_app.utils;

import alexgr.exchange_rate_app.service.CurrencyUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppStartUpRunnerUtil implements CommandLineRunner {
    private final CurrencyUpdateService service;

    @Override
    public void run(String... args) {
        log.info("Starting application initialization...");
        try {
            service.initializeCurrencyDescriptions();
            String defaultCurrency = "USD";
            service.updateCurrencyRates(defaultCurrency);
            log.info("Application initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during application initialization: {}", e.getMessage(), e);
        }
    }
}
