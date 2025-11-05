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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    return vendorDto;
                }
            }

            return mapper.readValue(dbData, UserDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to AbstractAccountDto", e);
        }
    }

    private void processAddressTranslations(JsonNode vendorDetails, VendorDto vendorDto) {
        if (vendorDetails.has("addressTranslations") && !vendorDetails.get("addressTranslations").isNull()) {
            JsonNode translationsNode = vendorDetails.get("addressTranslations");

            String currentLanguage = getCurrentLanguage();
            String translatedAddress = extractAddressTranslation(translationsNode, currentLanguage);

            if (translatedAddress != null && vendorDto.getVendorDetails() != null) {
                vendorDto.getVendorDetails().setAddress(translatedAddress);
            }
        }
    }

    private String extractAddressTranslation(JsonNode translationsNode, String lang) {
        try {
            String hstoreString;
            if (translationsNode.isTextual()) {
                hstoreString = translationsNode.asText();
            } else {
                hstoreString = translationsNode.toString();
            }

            try {
                TranslationJson json = mapper.readValue(hstoreString, TranslationJson.class);

                return switch (lang) {
                    case "en" -> json.en();
                    case "ru" -> json.ru();
                    case "zh" -> json.zh();
                    default -> json.en();
                };
            } catch (Exception e) {
                log.warn("Error parsing translated address: {}", e.getMessage());
            }

            if (hstoreString != null && lang != null) {
                Pattern pattern = Pattern.compile("\"" + lang + "\"=>\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(hstoreString);
                return matcher.find() ? matcher.group(1) : null;
            }
        } catch (Exception e) {
            log.error("Error extracting address translation: {}", e.getMessage(), e);
        }
        return null;
    }

    private String getCurrentLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    private record TranslationJson(String en, String ru, String zh) implements Serializable {}
}