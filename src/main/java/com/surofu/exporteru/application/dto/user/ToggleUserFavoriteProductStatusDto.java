package com.surofu.exporteru.application.dto.user;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;

@Data
@Value(staticConstructor = "of")
public class ToggleUserFavoriteProductStatusDto implements Serializable {
    boolean status;
}
