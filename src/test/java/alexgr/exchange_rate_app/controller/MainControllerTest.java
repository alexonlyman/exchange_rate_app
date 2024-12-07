package alexgr.exchange_rate_app.controller;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import alexgr.exchange_rate_app.repository.CurrencyRepository;
import alexgr.exchange_rate_app.service.CurrencyUpdateService;
import alexgr.exchange_rate_app.service.UpdateCurrencyByDateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {
    @Mock
    private CurrencyRepository repository;

    @Mock
    private CurrencyUpdateService service;

    @Mock
    private UpdateCurrencyByDateService updateCurrencyByDateService;

    @InjectMocks
    private MainController mainController;

    private MockMvc mockMvc;

    @Test
    void testShowCurrenciesWithoutView() {

        CurrencyEntity usd = new CurrencyEntity();
        usd.setCurrencyCode("USD");
        usd.setCurrencyDescription("US Dollar");
        usd.setRates(BigDecimal.valueOf(1.0));

        CurrencyEntity eur = new CurrencyEntity();
        eur.setCurrencyCode("EUR");
        eur.setCurrencyDescription("Euro");
        eur.setRates(BigDecimal.valueOf(0.85));

        List<CurrencyEntity> mockCurrencies = List.of(usd, eur);

        when(repository.findAll()).thenReturn(mockCurrencies);

        Model model = mock(Model.class);

        String viewName = mainController.showCurrencies(model);

        assertEquals("currencies", viewName);

        verify(model).addAttribute("currencies", mockCurrencies);


    }

    @Test
    void testUpdateInfo() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();

        mockMvc.perform(get("/api/currencies/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/currencies"));

        verify(service, times(1)).updateCurrencyRates("rub");
    }

    @Test
    void testUpdateCurrenciesByDate_Success() throws IOException {

        String date = "2023-12-01";
        String base = "USD";

        CurrencyEntity usd = new CurrencyEntity();
        usd.setCurrencyCode("USD");
        usd.setCurrencyDescription("US Dollar");
        usd.setRates(BigDecimal.valueOf(1.0));

        List<CurrencyEntity> currencies = List.of(usd);

        doNothing().when(updateCurrencyByDateService).saveChanges(date, base);
        when(repository.findAll()).thenReturn(currencies);

        Model model = mock(Model.class);

        String viewName = mainController.updateCurrenciesByDate(date, base, model);

        assertEquals("currencies", viewName);

        verify(model).addAttribute("successMessage", "Данные успешно обновлены на дату: " + date + " для валюты: " + base);
        verify(model).addAttribute("currencies", currencies);
    }
}