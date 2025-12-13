package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.currency.Currency;
import com.surofu.exporteru.core.model.currency.CurrencyCode;

import java.io.IOException;
import java.util.Map;

public interface CurrencyRepository {
    Map<CurrencyCode, Currency> getCurrencies() throws IOException, InterruptedException;
}
