package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.currency.Currency;
import com.surofu.madeinrussia.core.model.currency.CurrencyCode;

import java.io.IOException;
import java.util.Map;

public interface CurrencyRepository {
    Map<CurrencyCode, Currency> getCurrencies() throws IOException, InterruptedException;
}
