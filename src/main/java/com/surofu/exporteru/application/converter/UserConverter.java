package com.surofu.exporteru.application.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserConverter implements AttributeConverter<AbstractAccountDto, String> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(AbstractAccountDto attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting AbstractAccountDto to JSON", e);
        }
    }

    @Override
    public AbstractAccountDto convertToEntityAttribute(String dbData) {
        try {
            JsonNode root = mapper.readTree(dbData);

            if (root.has("vendorDetails")) {
                JsonNode vendorDetails = root.get("vendorDetails");

                if (!vendorDetails.isNull()) {
                    return mapper.readValue(dbData, VendorDto.class);
                }
            }

            return mapper.readValue(dbData, UserDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to AbstractAccountDto", e);
        }
    }
}