package com.surofu.exporteru.core.model.currency;

import java.io.Serializable;

public record Currency(
        String numCode,
        CurrencyCode charCode,
        Double unit,
        String currency,
        Double rate
) implements Serializable {
}
