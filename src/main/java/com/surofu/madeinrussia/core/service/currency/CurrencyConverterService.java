package com.surofu.madeinrussia.core.service.currency;

import com.surofu.madeinrussia.core.model.currency.CurrencyCode;

import java.io.IOException;
import java.math.BigDecimal;

public interface CurrencyConverterService {
    BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) throws IOException, InterruptedException;
}
