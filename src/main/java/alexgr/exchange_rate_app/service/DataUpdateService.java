package alexgr.exchange_rate_app.service.dataUpdaterService;

import alexgr.exchange_rate_app.service.CurrencyUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataUpdateService {

    private final CurrencyUpdateService updateService;

    @Scheduled(fixedRate = 1800000)
    public void updateData() {
        log.info("Scheduled task started: Updating currency rates...");
        try {
            updateService.updateCurrencyRates("USD");
            log.info("Currency rates updated successfully.");
        } catch (Exception e) {
            log.error("Error during scheduled rate update: {}", e.getMessage(), e);
        }
    }
}
