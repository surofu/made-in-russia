package com.surofu.exporteru.application.service;

import com.surofu.exporteru.core.model.currency.Currency;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.repository.CurrencyRepository;
import com.surofu.exporteru.core.service.currency.CurrencyConverterService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RussianCentralBankCurrencyConverterService implements CurrencyConverterService {
  private final CurrencyRepository currencyRepository;
  @Value("${app.redis.currency-ttl-duration}")
  private Duration ttl;
  private Cache cache;

  public RussianCentralBankCurrencyConverterService(CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
  }

  @PostConstruct
  public void init() {
    this.cache = new Cache(ttl);
  }

  @Override
  public BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount)
      throws IOException, InterruptedException {
    if (from.equals(to)) {
      return amount;
    }

    BigDecimal multiplier = Objects.requireNonNullElse(getMultiplier(from, to), BigDecimal.ONE);
    return amount.multiply(multiplier).setScale(0, RoundingMode.DOWN);
  }

  private BigDecimal getMultiplier(CurrencyCode from, CurrencyCode to)
      throws IOException, InterruptedException {
    BigDecimal multiplierFromCache = cache.get(from, to);

    if (multiplierFromCache != null) {
      return multiplierFromCache;
    }

    Map<CurrencyCode, Currency> currencies = currencyRepository.getCurrencies();
    currencies.put(CurrencyCode.RUB, new Currency("0", CurrencyCode.RUB, 1.0, "Ruble", 1.0));

    for (Map.Entry<CurrencyCode, Currency> fromEntry : currencies.entrySet()) {
      for (Map.Entry<CurrencyCode, Currency> toEntry : currencies.entrySet()) {
        if (fromEntry.getKey().equals(toEntry.getKey())) {
          cache.put(fromEntry.getKey(), toEntry.getKey(), BigDecimal.ONE);
          cache.put(toEntry.getKey(), fromEntry.getKey(), BigDecimal.ONE);
        } else {
          var currencyFrom = fromEntry.getValue();
          var currencyTo = toEntry.getValue();

          Double normalizedFromRate = currencyFrom.rate() / currencyFrom.unit();
          Double normalizedToRate = currencyTo.rate() / currencyTo.unit();
          BigDecimal multiplier = BigDecimal.valueOf(normalizedFromRate / normalizedToRate);
          BigDecimal reverseMultiplier = BigDecimal.valueOf(normalizedToRate / normalizedFromRate);

          cache.put(fromEntry.getKey(), toEntry.getKey(), multiplier);
          cache.put(toEntry.getKey(), fromEntry.getKey(), reverseMultiplier);
        }
      }
    }

    return cache.get(from, to);
  }

  private static class Cache {
    private final Map<CurrencyCode, Map<CurrencyCode, ValueWithSetDateTime>> map;
    private final Duration ttl;

    public Cache(Duration ttl) {
      this.map = new HashMap<>();
      this.ttl = ttl;
    }

    public BigDecimal get(CurrencyCode from, CurrencyCode to) {
      var valueWithSetDateTime = map.getOrDefault(from, new HashMap<>()).get(to);

      if (valueWithSetDateTime == null) {
        return null;
      }

      if (valueWithSetDateTime.setDateTime().plus(ttl).isBefore(ZonedDateTime.now())) {
        map.get(from).remove(to);
        return null;
      }

      return valueWithSetDateTime.value();
    }

    public void put(CurrencyCode from, CurrencyCode to, BigDecimal value) {
      var valueWithSetDateTime = new ValueWithSetDateTime(value, ZonedDateTime.now());
      var toMap = map.get(from);

      if (toMap == null) {
        toMap = new HashMap<>();
        toMap.put(to, valueWithSetDateTime);
        map.put(from, toMap);
      } else {
        toMap.put(to, valueWithSetDateTime);
      }
    }

    private record ValueWithSetDateTime(BigDecimal value, ZonedDateTime setDateTime) {
    }
  }
}
