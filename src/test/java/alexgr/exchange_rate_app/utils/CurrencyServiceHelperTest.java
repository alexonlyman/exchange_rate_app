package alexgr.exchange_rate_app.utils;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import alexgr.exchange_rate_app.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceHelperTest {

    @Mock
    private CurrencyRepository repository;

    @InjectMocks
    private CurrencyServiceHelper helper;

    @Test
    void testSaveOrUpdateCurrency_NewCurrency() {

        String code = "USD";
        BigDecimal rate = BigDecimal.valueOf(74.32);
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "US Dollar";

        Mockito.when(repository.findByCurrencyCode(code)).thenReturn(null);

        helper.saveOrUpdateCurrency(code, rate, dateTime, description);

        ArgumentCaptor<CurrencyEntity> captor = ArgumentCaptor.forClass(CurrencyEntity.class);
        Mockito.verify(repository).save(captor.capture());

        CurrencyEntity savedEntity = captor.getValue();
        assertEquals(code, savedEntity.getCurrencyCode());
        assertEquals(rate, savedEntity.getRates());
        assertEquals(dateTime, savedEntity.getDateTime());
        assertEquals(description, savedEntity.getCurrencyDescription());
    }


}