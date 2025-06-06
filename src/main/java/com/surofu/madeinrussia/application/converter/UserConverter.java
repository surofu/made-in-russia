package com.surofu.madeinrussia.application.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surofu.madeinrussia.application.dto.UserDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserConverter implements AttributeConverter<UserDto, String> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(UserDto attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserDto to JSON", e);
        }
    }

    @Override
    public UserDto convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, UserDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to UserDto", e);
        }
    }
}
