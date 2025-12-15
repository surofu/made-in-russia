package com.surofu.exporteru.application.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

@Slf4j
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
                    VendorDto vendorDto = mapper.readValue(dbData, VendorDto.class);
                    processAddressTranslations(vendorDetails, vendorDto);
                    if (root.has("loginTranslations")) {
                        JsonNode loginTranslations = root.get("loginTranslations");
                        String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
                        if (loginTranslations.has(currentLanguage)) {
                            vendorDto.setLogin(loginTranslations.get(currentLanguage).asText());
                        }
                    }
                    return vendorDto;
                }
            }

            UserDto userDto = mapper.readValue(dbData, UserDto.class);
            if (root.has("loginTranslations")) {
                JsonNode loginTranslations = root.get("loginTranslations");
                String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
                if (loginTranslations.has(currentLanguage)) {
                    userDto.setLogin(loginTranslations.get(currentLanguage).asText());
                }
            }
            return userDto;
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to AbstractAccountDto", e);
        }
    }

    private void processAddressTranslations(JsonNode vendorDetails, VendorDto vendorDto) {
        if (vendorDetails.has("addressTranslations") && !vendorDetails.get("addressTranslations").isNull()) {
            JsonNode translationsNode = vendorDetails.get("addressTranslations");

            String currentLanguage = LocaleContextHolder.getLocale().getLanguage();
            String translatedAddress = extractAddressTranslation(translationsNode, currentLanguage);

            if (translatedAddress != null && vendorDto.getVendorDetails() != null) {
                vendorDto.getVendorDetails().setAddress(translatedAddress);
            }
        }
    }

    private String extractAddressTranslation(JsonNode translationsNode, String lang) {
        try {
            String address;
            if (translationsNode.isTextual()) {
                address = translationsNode.asText();
            } else {
                address = translationsNode.toString();
            }

            try {
                Map<String, String> json = (Map<String, String>) mapper.readValue(address, Map.class);
                return json.getOrDefault(lang, "");
            } catch (Exception e) {
                log.warn("Error parsing translated address: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error extracting address translation: {}", e.getMessage(), e);
        }
        return null;
    }
}