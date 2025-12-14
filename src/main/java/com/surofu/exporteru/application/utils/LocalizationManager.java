package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.product.ProductDto;
import com.surofu.exporteru.application.dto.product.ProductPackageOptionDto;
import com.surofu.exporteru.application.dto.product.ProductPriceDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.core.model.currency.CurrencyCode;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOptionPrice;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceCurrency;
import com.surofu.exporteru.core.model.product.price.ProductPriceDiscountedPrice;
import com.surofu.exporteru.core.model.product.price.ProductPriceOriginalPrice;
import com.surofu.exporteru.core.service.currency.CurrencyConverterService;
import com.surofu.exporteru.core.view.ProductSummaryView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalizationManager {

  private final MessageSource messageSource;
  private final CurrencyConverterService currencyConverterService;

  @Nullable
  private static Long getDaysBeforeDiscountExpires(@Nullable ZonedDateTime discountExpirationDate) {
    if (discountExpirationDate == null) {
      return null;
    }

    ZonedDateTime now = ZonedDateTime.now();

    // Если дата истекла или равна текущей - возвращаем null
    if (discountExpirationDate.isBefore(now) || discountExpirationDate.isEqual(now)) {
      return null;
    }

    // Корректный расчет с учетом часовых поясов
    return now.until(discountExpirationDate, ChronoUnit.DAYS);
  }

  public String localize(String messageCode, Locale locale, Object... args) {
    String messageTemplate = messageSource.getMessage(messageCode, null, locale);
    return String.format(messageTemplate, args);
  }

  public String localize(String messageCode, Object... args) {
    Locale locale = LocaleContextHolder.getLocale();
    return localize(messageCode, locale, args);
  }

  public ProductSummaryView localizePrice(ProductSummaryView view, Locale locale) {
    CurrencyCode from = view.getPriceCurrencyCode();
    CurrencyCode to = switch (locale.getLanguage()) {
      case ("en") -> CurrencyCode.USD;
      case ("ru") -> CurrencyCode.RUB;
      case ("zh") -> CurrencyCode.CNY;
      case ("hi") -> CurrencyCode.INR;
      default -> CurrencyCode.USD;
    };

    if (from == null) {
      return view;
    }

    try {
      BigDecimal localizedOriginalPrice =
          currencyConverterService.convert(from, to, view.getOriginPrice());
      BigDecimal localizedDiscountedPrice =
          currencyConverterService.convert(from, to, view.getDiscountedPrice());

      view.setOriginPrice(localizedOriginalPrice.setScale(0, RoundingMode.DOWN));

      if (Math.abs(localizedDiscountedPrice.compareTo(BigDecimal.ZERO)) < 1) {
        view.setDiscountedPrice(BigDecimal.ONE);

        if (localizedOriginalPrice.compareTo(BigDecimal.ZERO) < 1) {
          view.setOriginPrice(BigDecimal.valueOf(2));
        }
      } else {
        view.setDiscountedPrice(localizedDiscountedPrice.setScale(0, RoundingMode.DOWN));
      }

      view.setPriceCurrencyCode(to);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (!locale.getLanguage().equals("ru")) {
      if (view.getUser() instanceof VendorDto vendorDto) {
        String login = vendorDto.getLogin();
        String address = vendorDto.getVendorDetails().getAddress();

        String translitLogin = TransliterationManager.transliterate(login);
        String translitAddress = TransliterationManager.transliterate(address);

        vendorDto.setLogin(translitLogin);
        vendorDto.getVendorDetails().setAddress(translitAddress);
      }
    }

    return view;
  }

  public ProductDto localizePrice(ProductDto dto, Locale locale) {
    CurrencyCode from = CurrencyCode.USD;
    CurrencyCode to = switch (locale.getLanguage()) {
      case ("en") -> CurrencyCode.USD;
      case ("ru") -> CurrencyCode.RUB;
      case ("zh") -> CurrencyCode.CNY;
      case ("hi") -> CurrencyCode.INR;
      default -> CurrencyCode.USD;
    };

    if (!dto.getPrices().isEmpty()) {
      from = CurrencyCode.valueOf(dto.getPrices().get(0).getCurrency());
    }

    for (ProductPackageOptionDto packagingOption : dto.getPackagingOptions()) {
      try {
        BigDecimal convertedPrice =
            currencyConverterService.convert(from, to, packagingOption.getPrice());
        packagingOption.setPrice(convertedPrice);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    for (ProductPriceDto price : dto.getPrices()) {
      try {
        BigDecimal localizedOriginalPrice =
            currencyConverterService.convert(from, to, price.getOriginalPrice());
        BigDecimal localizedDiscountedPrice =
            currencyConverterService.convert(from, to, price.getDiscountedPrice());

        price.setOriginalPrice(localizedOriginalPrice.setScale(0, RoundingMode.DOWN));

        if (dto.getDaysBeforeDiscountExpires() == null) {
          price.setDiscountedPrice(localizedOriginalPrice);
        } else {
          if (Math.abs(localizedDiscountedPrice.compareTo(BigDecimal.ZERO)) < 1) {
            price.setDiscountedPrice(BigDecimal.ONE);

            if (localizedOriginalPrice.compareTo(BigDecimal.ZERO) < 1) {
              price.setOriginalPrice(BigDecimal.valueOf(2));
            }
          } else {
            price.setDiscountedPrice(
                localizedDiscountedPrice.setScale(0, RoundingMode.DOWN));
          }
        }

        price.setCurrency(to.name());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    if (!locale.getLanguage().equals("ru")) {
      VendorDto vendorDto = dto.getUser();
      String login = vendorDto.getLogin();
      String address = vendorDto.getVendorDetails().getAddress();

      String translitLogin = TransliterationManager.transliterate(login);
      String translitAddress = TransliterationManager.transliterate(address);

      vendorDto.setLogin(translitLogin);
      vendorDto.getVendorDetails().setAddress(translitAddress);
    }

    return dto;
  }

  public Product localizePrice(Product product, Locale locale) {
    CurrencyCode from = CurrencyCode.USD;
    CurrencyCode to = switch (locale.getLanguage()) {
      case ("en") -> CurrencyCode.USD;
      case ("ru") -> CurrencyCode.RUB;
      case ("zh") -> CurrencyCode.CNY;
      case ("hi") -> CurrencyCode.INR;
      default -> CurrencyCode.USD;
    };

    if (!product.getPrices().isEmpty()) {
      from = CurrencyCode.valueOf(
          product.getPrices().iterator().next().getCurrency().getValue().name());
    }

    for (ProductPackageOption packagingOption : product.getPackageOptions()) {
      try {
        BigDecimal convertedPrice = currencyConverterService.convert(from, to,
            packagingOption.getPrice().getValue());
        packagingOption.setPrice(new ProductPackageOptionPrice(convertedPrice));
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    for (ProductPrice price : product.getPrices()) {
      try {
        BigDecimal localizedOriginalPrice =
            currencyConverterService.convert(from, to, price.getOriginalPrice().getValue());
        BigDecimal localizedDiscountedPrice = currencyConverterService.convert(from, to,
            price.getDiscountedPrice().getValue());

        price.setOriginalPrice(new ProductPriceOriginalPrice(
            localizedOriginalPrice.setScale(0, RoundingMode.DOWN)));

        if (getDaysBeforeDiscountExpires(product.getDiscountExpirationDate().getValue()) == null) {
          price.setDiscountedPrice(new ProductPriceDiscountedPrice(localizedOriginalPrice));
        } else {
          if (Math.abs(localizedDiscountedPrice.compareTo(BigDecimal.ZERO)) < 1) {
            price.setDiscountedPrice(new ProductPriceDiscountedPrice(BigDecimal.ONE));

            if (localizedOriginalPrice.compareTo(BigDecimal.ZERO) < 1) {
              price.setOriginalPrice(new ProductPriceOriginalPrice(BigDecimal.valueOf(2)));
            }
          } else {
            price.setDiscountedPrice(new ProductPriceDiscountedPrice(
                localizedDiscountedPrice.setScale(0, RoundingMode.DOWN)));
          }
        }

        price.setCurrency(new ProductPriceCurrency(to.name()));
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    return product;
  }
}
