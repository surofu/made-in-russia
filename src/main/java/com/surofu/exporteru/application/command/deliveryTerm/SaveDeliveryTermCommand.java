package com.surofu.exporteru.application.command.deliveryTerm;

import java.io.Serializable;

public record SaveDeliveryTermCommand(
    Long id,
    String code,
    String name,
    String description
) implements Serializable {
}
