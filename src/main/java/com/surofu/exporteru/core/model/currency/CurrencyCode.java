package com.surofu.exporteru.core.model.currency;

public enum CurrencyCode {
    RUB(Character.toString((char) 8381)),
    USD(Character.toString((char) 36)),
    CNY(Character.toString((char) 165)),
    INR(Character.toString('₹')),
    NO_CURRENCY("notNumberCurrency");

    private final String value;

    CurrencyCode(String value) {
        this.value = value;
    }

    public String getSymbol() {
        return value;
    }
}
