package com.surofu.exporteru.application.command.importproduct;

import com.surofu.exporteru.core.model.currency.CurrencyCode;
import java.math.BigDecimal;
import java.util.List;

public record ImportProductCommand(
    String title,
    String mainDescription,
    String furtherDescription,
    Price price,
    List<Characteristic> characteristics,
    List<String> images
) {

  public record Price(
      BigDecimal value,
      CurrencyCode currency,
      String unit
  ) {
  }

  public record Characteristic(
      String name,
      String value
  ) {
  }
}