package alexgr.exchange_rate_app.controller;

import alexgr.exchange_rate_app.entity.CurrencyEntity;
import alexgr.exchange_rate_app.repository.CurrencyRepository;
import alexgr.exchange_rate_app.service.UpdateCurrencyByDateService;
import alexgr.exchange_rate_app.service.CurrencyUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class MainController {

    private final CurrencyRepository repository;
    private final CurrencyUpdateService service;
    private final UpdateCurrencyByDateService updateCurrencyByDateService;
    /**
     * Эндпоинт для отображения всех валют.
     * @param model объект Model для передачи данных в представление.
     * @return имя Thymeleaf-шаблона для отображения списка валют.
     */
    @GetMapping
    public String showCurrencies(Model model) {
        // Получение всех записей валют из базы данных.
        List<CurrencyEntity> currencies = repository.findAll();
        // Добавление списка валют в модель.
        model.addAttribute("currencies", currencies);
        // Возвращение имени Thymeleaf-шаблона "currencies".
        return "currencies";
    }

    /**
     * Эндпоинт для обновления текущих курсов валют.
     * @return перенаправление на страницу со списком валют.
     * @throws IOException если возникает ошибка при обновлении данных.
     */
    @GetMapping("/update")
    public String updateInfo() throws IOException {
        // Обновление курсов валют с базовой валютой "rub".
        service.updateCurrencyRates("rub");
        // Перенаправление на домашнюю страницу "/api/currencies".
        return "redirect:/api/currencies";
    }

    /**
     * Эндпоинт для обновления курсов валют на указанную дату.
     * @param date дата для обновления в формате "yyyy-MM-dd".
     * @param base базовая валюта.
     * @param model объект Model для передачи данных в представление.
     * @return имя Thymeleaf-шаблона для отображения результата.
     */
    @GetMapping("/update-by-date")
    public String updateCurrenciesByDate(
            @RequestParam("date") String date, // Параметр запроса: дата.
            @RequestParam("base") String base, // Параметр запроса: базовая валюта.
            Model model) {
        try {
            // Сохранение курсов валют на указанную дату.
            updateCurrencyByDateService.saveChanges(date, base);
            // Добавление сообщения об успешном обновлении.
            model.addAttribute("successMessage", "Данные успешно обновлены на дату: " + date + " для валюты: " + base);
        } catch (IllegalArgumentException e) {
            // Добавление сообщения об ошибке валидации.
            model.addAttribute("errorMessage", e.getMessage());
        } catch (IOException e) {
            // Добавление сообщения об ошибке API.
            model.addAttribute("errorMessage", "Ошибка обновления данных. Попробуйте позже.");
        }

        // Повторное получение списка валют из базы.
        List<CurrencyEntity> currencies = repository.findAll();
        // Добавление списка валют в модель.
        model.addAttribute("currencies", currencies);
        // Возвращение имени Thymeleaf-шаблона "currencies".
        return "currencies";
    }
}
