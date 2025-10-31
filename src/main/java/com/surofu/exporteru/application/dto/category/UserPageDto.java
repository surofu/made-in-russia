package com.surofu.exporteru.application.dto.category;

import com.surofu.exporteru.application.dto.AbstractAccountDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Schema(
        name = "UserPageDto",
        description = "Represents a page of users"
)
public final class UserPageDto extends PageImpl<AbstractAccountDto> implements Serializable {
    public UserPageDto(List<AbstractAccountDto> content, Pageable pageable, Long total) {
        super(content, pageable, total);
    }
    public UserPageDto(List<AbstractAccountDto> content) {
        super(content);
    }
}