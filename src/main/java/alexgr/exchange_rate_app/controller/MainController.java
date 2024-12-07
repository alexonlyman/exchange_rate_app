package alexgr.exchange_rate_app.controller;

import alexgr.exchange_rate_app.service.currencyService.CurrencyService;
import alexgr.exchange_rate_app.service.dataInitService.DataInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class Controller {

    private final CurrencyService service;
    private final DataInitializerService dataInitializerService;


}
