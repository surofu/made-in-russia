package com.surofu.exporteru.infrastructure.persistence.currency;

import com.surofu.exporteru.core.model.currency.Currency;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RussianCentralBankCurrencyRepository implements CurrencyRepository {

    private final HttpClient http;

    public RussianCentralBankCurrencyRepository() {
        http = HttpClient.newHttpClient();
    }

    @Override
    public Map<CurrencyCode, Currency> getCurrencies() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.cbr.ru/eng/currency_base/daily/"))
                .header("Content-Type", "text/html")
                .build();
        var response = http.send(request, HttpResponse.BodyHandlers.ofString());
        var html = response.body();
        var htmlC = html.replaceAll("\\s+|\\t+|\\r+|\\n+", "");
        var rows = htmlC.split("((</td></tr>)?<tr><td>)|</td></tr></tbody>");
        var rowsC = Arrays.copyOfRange(rows, 1, rows.length);
        var rowsC2 = Arrays.stream(rowsC).map(r -> r.split("</td><td>")).toArray(String[][]::new);

        Map<CurrencyCode, Currency> currencies = new HashMap<>();

        for (var row : rowsC2) {
            try {
                String numCode = row[0].trim();
                CurrencyCode currencyCode = CurrencyCode.valueOf(row[1].trim());
                Double unit = Double.valueOf(row[2].trim());
                String currencyName = row[3].trim();
                Double rate = Double.valueOf(row[4].trim());
                var currency = new Currency(numCode, currencyCode, unit, currencyName, rate);
                currencies.put(currencyCode, currency);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            } catch (Exception ignored) {
            }
        }

        return currencies;
    }
}
