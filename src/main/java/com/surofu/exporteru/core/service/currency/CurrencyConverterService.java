package com.surofu.exporteru.core.service.currency;

import com.surofu.exporteru.core.model.currency.CurrencyCode;

import java.io.IOException;
import java.math.BigDecimal;

public interface CurrencyConverterService {
    BigDecimal convert(CurrencyCode from, CurrencyCode to, BigDecimal amount) throws IOException, InterruptedException;
}
