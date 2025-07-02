package com.surofu.madeinrussia.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class ProductHintDto implements Serializable {
    Long id;
    String title;
    String image;
}
