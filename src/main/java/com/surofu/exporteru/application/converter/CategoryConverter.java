package com.surofu.exporteru.application.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CategoryConverter implements AttributeConverter<CategoryDto, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(CategoryDto attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert CategoryDto to JSON", e);
        }
    }

    @Override
    public CategoryDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, CategoryDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert JSON to CategoryDto", e);
        }
    }
}