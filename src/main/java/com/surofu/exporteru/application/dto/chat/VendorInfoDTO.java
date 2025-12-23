package com.surofu.exporteru.application.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO с информацией о поставщике для vendor chat
 */
@Getter
@Setter
@Builder
public class VendorInfoDTO {
    private Long id;
    private String name;
    private String avatarUrl;
}