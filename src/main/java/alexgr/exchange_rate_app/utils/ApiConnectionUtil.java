package alexgr.exchange_rate_app.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ConnectionToClientApiUtil {

    private static final String API_KEY = "aoT7U52QCJ6qmJ4I6xBwk5qr80DHevZa";
    private static final String BASE_URL = "https://api.apilayer.com/fixer";

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();


    /**
     * Получение данных с любого эндпоинта API.
     *
     * @param endpoint    конечная точка API (например, "/latest" или "/symbols").
     * @param queryParams строка параметров запроса (например, "base=USD&symbols=EUR").
     * @return JSON-ответ в виде строки.
     * @throws IOException если запрос завершился неудачей.
     */
    public String getExchangeRateData(String endpoint, String queryParams) throws IOException {
        String url = BASE_URL + endpoint + (queryParams != null ? "?" + queryParams : "");
        log.info("Отправка запроса на URL: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .get()
                .build();

        return executeRequest(request);
    }

    /**
     * Получение данных о символах валют.
     *
     * @return JSON-ответ с описанием валют.
     * @throws IOException если запрос завершился неудачей.
     */
    public String getConnectionToInitCurrencyDescription() throws IOException {
        return getExchangeRateData("/symbols", null);
    }

    /**
     * Получение текущих курсов валют для указанной базовой валюты.
     *
     * @param base базовая валюта (например, "USD").
     * @return JSON-ответ с текущими курсами.
     * @throws IOException если запрос завершился неудачей.
     */
    public String getConnectionToExchangeRate(String base) throws IOException {
        return getExchangeRateData("/latest", "symbols=&base=" + base);
    }

    /**
     * Получение исторических курсов валют на указанную дату.
     *
     * @param date дата в формате "yyyy-MM-dd".
     * @param base базовая валюта (например, "USD").
     * @return JSON-ответ с курсами валют на указанную дату.
     * @throws IOException если запрос завершился неудачей.
     */
    public String getRatesForDate(String date, String base) throws IOException {
        log.info("Получение курсов валют на дату {} с базовой валютой {}", date, base);

        validateDate(date);

        return getExchangeRateData("/" + date, "symbols=&base=" + base);
    }

    /**
     * Проверка допустимости даты.
     *
     * @param date дата в формате "yyyy-MM-dd".
     */
    private void validateDate(String date) {
        log.debug("Проверка введенной даты: {}", date);

        LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate currentDate = LocalDate.now();

        if (inputDate.isBefore(currentDate.minusDays(5)) || inputDate.isAfter(currentDate)) {
            log.error("Дата {} не входит в допустимый диапазон (максимум 5 дней назад).", date);
            throw new IllegalArgumentException("Дата должна быть в пределах последних 5 дней.");
        }

        log.debug("Дата {} прошла валидацию.", date);
    }

    /**
     * Выполнение запроса и обработка ответа.
     *
     * @param request запрос OkHttp.
     * @return JSON-ответ в виде строки.
     * @throws IOException если запрос завершился неудачей.
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            log.info("Получен ответ от API с кодом: {}", response.code());

            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Ошибка API: код ответа " + response.code());
            }
        }
    }
}
