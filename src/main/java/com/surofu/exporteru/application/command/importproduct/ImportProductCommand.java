package com.surofu.exporteru.application.command.importproduct;

import java.util.List;

public record ImportProductCommand(
    String title,
    String mainDescription,
    String furtherDescription,
    List<Characteristic> characteristics,
    List<String> images
) {

  public record Characteristic(
      String name,
      String value
  ) {
  }
}