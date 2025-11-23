package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.cache.DeleteAccountCache;
import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.auth.TokenDto;
import com.surofu.exporteru.application.dto.product.ProductReviewDto;
import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import com.surofu.exporteru.application.dto.session.SessionDto;
import com.surofu.exporteru.application.dto.user.ToggleUserFavoriteProductStatusDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorCountryDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.dto.vendor.VendorMediaDto;
import com.surofu.exporteru.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.utils.AuthUtils;
import com.surofu.exporteru.application.utils.JwtUtils;
import com.surofu.exporteru.application.utils.LocalizationManager;
import com.surofu.exporteru.core.model.media.MediaType;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.ProductTitle;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.product.review.ProductReviewContent;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.session.SessionDeviceId;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserAvatar;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaMimeType;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaPosition;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMediaUrl;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.repository.CategoryRepository;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.ProductReviewRepository;
import com.surofu.exporteru.core.repository.ProductSummaryViewRepository;
import com.surofu.exporteru.core.repository.SessionRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.VendorCountryRepository;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
import com.surofu.exporteru.core.repository.VendorEmailRepository;
import com.surofu.exporteru.core.repository.VendorFaqRepository;
import com.surofu.exporteru.core.repository.VendorMediaRepository;
import com.surofu.exporteru.core.repository.VendorPhoneNumberRepository;
import com.surofu.exporteru.core.repository.VendorProductCategoryRepository;
import com.surofu.exporteru.core.repository.VendorSiteRepository;
import com.surofu.exporteru.core.repository.specification.ProductReviewSpecifications;
import com.surofu.exporteru.core.repository.specification.ProductSummarySpecifications;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.me.MeService;
import com.surofu.exporteru.core.service.me.operation.DeleteMe;
import com.surofu.exporteru.core.service.me.operation.DeleteMeAvatar;
import com.surofu.exporteru.core.service.me.operation.DeleteMeReviewById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeSessionById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeVendorMediaById;
import com.surofu.exporteru.core.service.me.operation.DeleteMeVendorMediaByIdList;
import com.surofu.exporteru.core.service.me.operation.GetMe;
import com.surofu.exporteru.core.service.me.operation.GetMeCurrentSession;
import com.surofu.exporteru.core.service.me.operation.GetMeFavoriteProducts;
import com.surofu.exporteru.core.service.me.operation.GetMeProductSummaryViewPage;
import com.surofu.exporteru.core.service.me.operation.GetMeReviewPage;
import com.surofu.exporteru.core.service.me.operation.GetMeSessions;
import com.surofu.exporteru.core.service.me.operation.GetMeVendorProductReviewPage;
import com.surofu.exporteru.core.service.me.operation.RefreshMeCurrentSession;
import com.surofu.exporteru.core.service.me.operation.SaveMeAvatar;
import com.surofu.exporteru.core.service.me.operation.ToggleMeFavoriteProductById;
import com.surofu.exporteru.core.service.me.operation.UpdateMe;
import com.surofu.exporteru.core.service.me.operation.UploadMeVendorMedia;
import com.surofu.exporteru.core.service.me.operation.VerifyDeleteMe;
import com.surofu.exporteru.core.service.user.UserService;
import com.surofu.exporteru.core.view.ProductSummaryView;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.exporteru.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import com.surofu.exporteru.infrastructure.persistence.vendorView.JpaVendorViewRepository;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final ProductSummaryViewRepository productSummaryViewRepository;
  private final ProductReviewRepository productReviewRepository;
  private final VendorDetailsRepository vendorDetailsRepository;
  private final VendorCountryRepository vendorCountryRepository;
  private final VendorPhoneNumberRepository vendorPhoneNumberRepository;
  private final VendorEmailRepository vendorEmailRepository;
  private final VendorSiteRepository vendorSiteRepository;
  private final VendorProductCategoryRepository vendorProductCategoryRepository;
  private final VendorFaqRepository vendorFaqRepository;
  private final VendorMediaRepository vendorMediaRepository;
  private final UserService userService;
  private final CategoryRepository categoryRepository;
  private final JwtUtils jwtUtils;
  private final FileStorageRepository fileStorageRepository;
  private final TranslationRepository translationRepository;
  private final ProductRepository productRepository;

  private final MailService mailService;
  private final DeleteAccountCache deleteAccountCache;
  private final JpaVendorViewRepository vendorViewRepository;
  private final LocalizationManager localizationManager;

  @Value("${app.session.secret}")
  private String sessionSecret;

  @Value("${app.redis.verification-ttl-duration}")
  private Duration verificationTtlDuration;

  @Override
  @Transactional(readOnly = true)
  public GetMe.Result getMeByJwt(GetMe operation) {
    SecurityUser securityUser = operation.getSecurityUser();
    Optional<Session> existingSession = getSessionBySecurityUser(securityUser);

    if (existingSession.isEmpty() &&
        !sessionSecret.equals(securityUser.getSessionInfo().getSessionKey())) {
      return GetMe.Result.sessionWithUserIdAndDeviceIdNotFound(
          securityUser.getUser().getId(),
          securityUser.getSessionInfo().getDeviceId()
      );
    }

    UserView userView = userRepository.getViewById(securityUser.getUser().getId()).orElseThrow();

    if (userView.getVendorDetails() == null) {
      UserDto userDto = UserDto.of(userView, operation.getLocale());
      return GetMe.Result.success(userDto);
    }

    VendorDto vendorDto = VendorDto.of(userView, operation.getLocale());

    if (!operation.getLocale().getLanguage().equals("ru")) {
      String login = vendorDto.getLogin();
      String address = vendorDto.getVendorDetails().getAddress();

      String translitLogin = TransliterationManager.transliterate(login);
      String translitAddress = TransliterationManager.transliterate(address);

      vendorDto.setLogin(translitLogin);
      vendorDto.getVendorDetails().setAddress(translitAddress);
    }

    Long viewsCount =
        vendorViewRepository.getCountByVendorDetailsId(userView.getVendorDetails().getId());
    vendorDto.getVendorDetails().setViewsCount(viewsCount);

    List<VendorCountryView> vendorCountryViewList =
        vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
            userView.getVendorDetails().getId(),
            operation.getLocale().getLanguage()
        );
    List<VendorProductCategoryView> vendorProductCategoryViewList =
        vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
            userView.getVendorDetails().getId(),
            operation.getLocale().getLanguage()
        );
    List<VendorFaqView> vendorFaqViewList = vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
        userView.getVendorDetails().getId(),
        operation.getLocale().getLanguage()
    );
    List<VendorPhoneNumber> vendorPhoneNumberList =
        vendorPhoneNumberRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());
    List<VendorEmail> vendorEmailList =
        vendorEmailRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());
    List<VendorSite> vendorSiteList =
        vendorSiteRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());
    List<VendorMedia> vendorMediaList =
        vendorMediaRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());

    List<VendorCountryDto> vendorCountryDtoList =
        vendorCountryViewList.stream().map(VendorCountryDto::of).toList();
    List<VendorProductCategoryDto> vendorProductCategoryDtoList =
        vendorProductCategoryViewList.stream().map(VendorProductCategoryDto::of).toList();
    List<VendorFaqDto> vendorFaqDtoList = vendorFaqViewList.stream().map(VendorFaqDto::of).toList();
    List<String> phoneNumbers = vendorPhoneNumberList.stream()
        .map(VendorPhoneNumber::getPhoneNumber)
        .map(VendorPhoneNumberPhoneNumber::toString)
        .toList();
    List<String> emails = vendorEmailList.stream()
        .map(VendorEmail::getEmail)
        .map(VendorEmailEmail::toString)
        .toList();
    List<String> sites = vendorSiteList.stream()
        .map(VendorSite::getUrl)
        .map(VendorSiteUrl::toString)
        .toList();
    List<VendorMediaDto> vendorMediaDtoList =
        vendorMediaList.stream().map(VendorMediaDto::of).toList();

    vendorDto.getVendorDetails().setCountries(vendorCountryDtoList);
    vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtoList);
    vendorDto.getVendorDetails().setFaq(vendorFaqDtoList);
    vendorDto.getVendorDetails().setPhoneNumbers(phoneNumbers);
    vendorDto.getVendorDetails().setEmails(emails);
    vendorDto.getVendorDetails().setSites(sites);
    vendorDto.getVendorDetails().setMedia(vendorMediaDtoList);

    return GetMe.Result.success(vendorDto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeSessions.Result getMeSessions(GetMeSessions operation) {
    List<SessionDto> sessionDtos = sessionRepository
        .getSessionsByUserId(operation.getSecurityUser().getUser().getId())
        .stream()
        .map(SessionDto::of)
        .toList();

    return GetMeSessions.Result.success(sessionDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeCurrentSession.Result getMeCurrentSession(GetMeCurrentSession operation) {
    SecurityUser securityUser = operation.getSecurityUser();
    Long userId = securityUser.getUser().getId();
    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();

    Optional<SessionDto> sessionDto = sessionRepository
        .getSessionByUserIdAndDeviceId(userId, sessionDeviceId)
        .map(SessionDto::of);

    if (sessionDto.isEmpty()) {
      return GetMeCurrentSession.Result.sessionNotFound(userId, sessionDeviceId);
    }

    return GetMeCurrentSession.Result.success(sessionDto.get());
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeProductSummaryViewPage.Result getMeProductSummaryViewPage(
      GetMeProductSummaryViewPage operation) {
    String[] sortStrings = operation.getSort().split(",");
    Sort sort = Sort.by(Sort.Direction.fromString(operation.getDirection()), sortStrings);

    Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), sort);

    List<Long> allChildCategoriesIds =
        categoryRepository.getCategoriesIdsByIds(operation.getCategoryIds());
    List<Long> categoryIdsWithChildren = new ArrayList<>();

    if (operation.getCategoryIds() != null) {
      categoryIdsWithChildren.addAll(operation.getCategoryIds());
    }

    categoryIdsWithChildren.addAll(allChildCategoriesIds);

    Specification<ProductSummaryView> specification = Specification
        .where(ProductSummarySpecifications.byUserId(operation.getSecurityUser().getUser().getId()))
        .and(ProductSummarySpecifications.byTitle(operation.getTitle()))
        .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
        .and(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
        .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(),
            operation.getMaxPrice()))
        .and(ProductSummarySpecifications.approveStatusIn(operation.getApproveStatuses()));

    Page<ProductSummaryView> productSummaryViewPage =
        productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
    Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage
        .map(p -> ProductSummaryViewDto.of(
            localizationManager.localizePrice(p, operation.getLocale()),
            operation.getLocale().getLanguage()
        ));

    return GetMeProductSummaryViewPage.Result.success(productSummaryViewDtoPage);
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeReviewPage.Result getMeReviewPage(GetMeReviewPage operation) {
    Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

    Specification<ProductReview> specification = Specification
        .where(ProductReviewSpecifications.byUserId(operation.getSecurityUser().getUser().getId()))
        .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(),
            operation.getMaxRating()));

    Page<ProductReviewDto> productReviewDtoPage =
        getProductReviewsBy(specification, pageable, operation.getLocale());

    return GetMeReviewPage.Result.success(productReviewDtoPage);
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeVendorProductReviewPage.Result getMeVendorProductReviewPage(
      GetMeVendorProductReviewPage operation) {
    Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

    Specification<ProductReview> specification = Specification
        .where(ProductReviewSpecifications.byProductUserId(
            operation.getSecurityUser().getUser().getId()))
        .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(),
            operation.getMaxRating()));

    Page<ProductReviewDto> productReviewDtoPage =
        getProductReviewsBy(specification, pageable, operation.getLocale());

    return GetMeVendorProductReviewPage.Result.success(productReviewDtoPage);
  }

  private ProductReview translateProductReview(ProductReview productReview, Locale locale) {
    String translatedProductTitle =
        productReview.getProduct().getTitle().getLocalizedValue(locale);

    if (StringUtils.trimToNull(translatedProductTitle) != null) {
      productReview.getProduct().setTitle(ProductTitle.of(translatedProductTitle));
    }

    if (productReview.getUser().getLogin().getTransliteration() != null) {
      String translatedUserLogin =
          productReview.getUser().getLogin().getLocalizedValue(locale);
      productReview.getUser().setLogin(UserLogin.of(translatedUserLogin));
    }

    String translatedText = productReview.getContent().getLocalizedValue(locale);

    if (StringUtils.trimToNull(translatedText) != null) {
      productReview.setContent(ProductReviewContent.of(translatedText));
    }
    return productReview;
  }

  @Override
  @Transactional
  public RefreshMeCurrentSession.Result refreshMeCurrentSession(RefreshMeCurrentSession operation) {
    UserEmail userEmail;

    try {
      userEmail = jwtUtils.extractUserEmailFromRefreshToken(operation.getRefreshToken());
    } catch (JwtException | IllegalArgumentException ex) {
      return RefreshMeCurrentSession.Result.invalidRefreshToken(operation.getRefreshToken(), ex);
    }

    SecurityUser securityUser;

    try {
      securityUser = (SecurityUser) userService.loadUserByUsername(userEmail.toString());
    } catch (UsernameNotFoundException ex) {
      return RefreshMeCurrentSession.Result.userNotFound(userEmail);
    }

    Long userId = securityUser.getUser().getId();

    Optional<Session> session = sessionRepository.getSessionByUserIdAndDeviceId(userId,
        operation.getSessionInfo().getDeviceId());

    if (session.isEmpty() && !sessionSecret.equals(operation.getSessionInfo().getSessionKey())) {
      return RefreshMeCurrentSession.Result.sessionNotFound(
          operation.getSessionInfo().getDeviceId());
    }

    String accessToken = jwtUtils.generateAccessToken(securityUser);
    TokenDto tokenDto = TokenDto.of(accessToken);

    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
    Session oldSession = sessionRepository
        .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
        .orElse(new Session());
    Session newSession =
        Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

    try {
      sessionRepository.save(newSession);
    } catch (Exception e) {
      return RefreshMeCurrentSession.Result.saveSessionError(userEmail, e);
    }

    return RefreshMeCurrentSession.Result.success(tokenDto);
  }

  @Override
  @Transactional
  public UpdateMe.Result updateMe(UpdateMe operation) {
    User user = operation.getSecurityUser().getUser();
    VendorDetails vendorDetails = user.getVendorDetails();

    if (operation.getUserPhoneNumber() != null) {
      if (userRepository.existsUserByPhoneNumberAndNotUserId(operation.getUserPhoneNumber(),
          user.getId())) {
        return UpdateMe.Result.phoneNumberAlreadyExists(operation.getUserPhoneNumber());
      }

      user.setPhoneNumber(operation.getUserPhoneNumber());
    }

    if (operation.getUserRegion() != null && !user.getRole().equals(UserRole.ROLE_VENDOR)) {
      user.setRegion(operation.getUserRegion());
    }

    if (vendorDetails != null) {
      if (operation.getInn() != null) {
        if (vendorDetailsRepository.existsByInnAndNotVendorDetailsId(operation.getInn(),
            vendorDetails.getId())) {
          return UpdateMe.Result.innAlreadyExists(operation.getInn());
        }

        vendorDetails.setInn(operation.getInn());
      }

      if (operation.getAddress() != null) {
        vendorDetails.setAddress(operation.getAddress());

        try {
          vendorDetails.getAddress().setTranslations(translationRepository.expand(operation.getAddress().toString()));
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.translationError(e);
        }
      }

      if (operation.getDescription() != null) {
        vendorDetails.setDescription(operation.getDescription());


        try {
          vendorDetails.getDescription().setTranslations(translationRepository.expand(operation.getDescription().toString()));
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.translationError(e);
        }
      }

      if (operation.getCountryNames() != null && !operation.getCountryNames().isEmpty()) {
        Set<VendorCountry> vendorCountries = new HashSet<>();

        for (VendorCountryName countryName : operation.getCountryNames()) {
          VendorCountry vendorCountry = new VendorCountry();
          vendorCountry.setVendorDetails(vendorDetails);
          vendorCountry.setName(countryName);

          try {
            countryName.setTranslations(translationRepository.expand(countryName.toString()));
          } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateMe.Result.translationError(e);
          }

          vendorCountries.add(vendorCountry);
        }

        List<VendorCountry> oldCountries =
            vendorCountryRepository.getByVendorId(vendorDetails.getId());

        try {
          vendorCountryRepository.deleteAll(oldCountries);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }

        try {
          vendorCountryRepository.saveAll(vendorCountries);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }
      }

      if (operation.getCategoryNames() != null && !operation.getCategoryNames().isEmpty()) {
        Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

        for (VendorProductCategoryName categoryName : operation.getCategoryNames()) {
          VendorProductCategory vendorProductCategory = new VendorProductCategory();
          vendorProductCategory.setVendorDetails(vendorDetails);
          vendorProductCategory.setName(categoryName);

          try {
            vendorProductCategory.getName().setTranslations(translationRepository.expand(categoryName.toString()));

          } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateMe.Result.translationError(e);
          }

          vendorProductCategories.add(vendorProductCategory);
        }

        List<VendorProductCategory> oldProductCategories =
            vendorProductCategoryRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
          vendorProductCategoryRepository.deleteAll(oldProductCategories);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }

        try {
          vendorProductCategoryRepository.saveAll(vendorProductCategories);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }
      }

      if (operation.getPhoneNumbers() != null) {
        Set<VendorPhoneNumber> phoneNumberSet = new HashSet<>();

        for (VendorPhoneNumberPhoneNumber number : operation.getPhoneNumbers()) {
          VendorPhoneNumber phoneNumber = new VendorPhoneNumber();
          phoneNumber.setVendorDetails(vendorDetails);
          phoneNumber.setPhoneNumber(number);
          phoneNumberSet.add(phoneNumber);
        }

        List<VendorPhoneNumber> oldPhoneNumbers =
            vendorPhoneNumberRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
          vendorPhoneNumberRepository.deleteAll(oldPhoneNumbers);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }

        try {
          vendorPhoneNumberRepository.saveAll(phoneNumberSet);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }
      }

      if (operation.getEmails() != null) {
        Set<VendorEmail> emailSet = new HashSet<>();

        for (VendorEmailEmail email : operation.getEmails()) {
          VendorEmail vendorEmail = new VendorEmail();
          vendorEmail.setVendorDetails(vendorDetails);
          vendorEmail.setEmail(email);
          emailSet.add(vendorEmail);
        }

        List<VendorEmail> oldVendorEmails =
            vendorEmailRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
          vendorEmailRepository.deleteAll(oldVendorEmails);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }

        try {
          vendorEmailRepository.saveAll(emailSet);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }
      }

      if (operation.getSites() != null) {
        Set<VendorSite> siteSet = new HashSet<>();

        for (VendorSiteUrl url : operation.getSites()) {
          VendorSite site = new VendorSite();
          site.setVendorDetails(vendorDetails);
          site.setUrl(url);
          siteSet.add(site);
        }

        List<VendorSite> oldVendorSites =
            vendorSiteRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
          vendorSiteRepository.deleteAll(oldVendorSites);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }

        try {
          vendorSiteRepository.saveAll(siteSet);
        } catch (Exception e) {
          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
          return UpdateMe.Result.saveError(e);
        }
      }
    }

    VendorDetails savedVendorDetails = null;

    if (vendorDetails != null) {
      savedVendorDetails = vendorDetailsRepository.save(vendorDetails);
    }

    if (operation.getUserLogin() != null) {
      user.setLogin(operation.getUserLogin());
    }

    User savedUser = userRepository.save(user);

    if (savedVendorDetails != null) {
      List<VendorCountryView> vendorCountries =
          vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(savedVendorDetails.getId(),
              operation.getLocale().getLanguage());
      List<VendorProductCategoryView> vendorProductCategoryViews =
          vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
              savedVendorDetails.getId(), operation.getLocale().getLanguage());

      List<VendorCountryDto> vendorCountryDtos =
          vendorCountries.stream().map(VendorCountryDto::of).toList();
      List<VendorProductCategoryDto> vendorProductCategoryDtos =
          vendorProductCategoryViews.stream().map(VendorProductCategoryDto::of).toList();

      VendorDto vendorDto = VendorDto.of(savedUser, operation.getLocale());

      vendorDto.getVendorDetails().setCountries(vendorCountryDtos);
      vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtos);

      return UpdateMe.Result.success(vendorDto);
    }

    return UpdateMe.Result.success(UserDto.of(savedUser, operation.getLocale()));
  }

  @Override
  @Transactional
  public DeleteMeSessionById.Result deleteMeSessionById(DeleteMeSessionById operation) {
    Optional<Session> session = sessionRepository.getSessionById(operation.getSessionId());

    if (session.isPresent()) {
      try {
        sessionRepository.deleteSessionById(session.get().getId());
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DeleteMeSessionById.Result.deleteError(e);
      }

      return DeleteMeSessionById.Result.success(operation.getSessionId());
    }

    return DeleteMeSessionById.Result.notFound(operation.getSessionId());
  }

  @Override
  @Transactional
  public SaveMeAvatar.Result saveMeAvatar(SaveMeAvatar operation) {
    if (operation.getFile() == null || operation.getFile().isEmpty()) {
      return SaveMeAvatar.Result.emptyFile();
    }

    String url;

    try {
      if (operation.getFile().getContentType() == null) {
        return SaveMeAvatar.Result.invalidContentType("null");
      }

      if (operation.getFile().getContentType().contains("image")) {
        url = fileStorageRepository.uploadImageToFolder(operation.getFile(),
            FileStorageFolders.USERS_AVATARS.getValue());
      } else if (operation.getFile().getContentType().contains("video")) {
        url = fileStorageRepository.uploadVideoToFolder(operation.getFile(),
            FileStorageFolders.USERS_AVATARS.getValue());
      } else {
        return SaveMeAvatar.Result.invalidContentType(operation.getFile().getContentType());
      }

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return SaveMeAvatar.Result.saveError(e);
    }

    User user = operation.getSecurityUser().getUser();

    if (user.getAvatar() != null && StringUtils.trimToNull(user.getAvatar().toString()) != null) {
      try {
        fileStorageRepository.deleteMediaByLink(user.getAvatar().toString());
      } catch (Exception e) {
        log.warn("Error deleting avatar from storage", e);
      }
    }

    user.setAvatar(UserAvatar.of(url));

    try {
      userRepository.save(operation.getSecurityUser().getUser());
      return SaveMeAvatar.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return SaveMeAvatar.Result.saveError(e);
    }
  }

  @Override
  @Transactional
  public DeleteMeAvatar.Result deleteMeAvatar(DeleteMeAvatar operation) {
    User user = operation.getSecurityUser().getUser();

    if (user.getAvatar() != null) {
      try {
        fileStorageRepository.deleteMediaByLink(user.getAvatar().getUrl());
      } catch (Exception e) {
        log.warn("Error deleting avatar from storage", e);
      }

      user.setAvatar(null);

      try {
        userRepository.save(user);
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DeleteMeAvatar.Result.deleteError(e);
      }
    }

    return DeleteMeAvatar.Result.success();
  }

  @Override
  public DeleteMe.Result deleteMe(DeleteMe operation) {
    UserEmail email = operation.getSecurityUser().getUser().getEmail();
    String code = AuthUtils.generateVerificationCode();
    deleteAccountCache.put(email.toString(), code);
    ZonedDateTime expirationDate = ZonedDateTime.now().plus(verificationTtlDuration);

    CompletableFuture.runAsync(() -> {
      try {
        mailService.sendConfirmDeleteAccountMail(email.toString(), code, expirationDate,
            operation.getLocale());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });

    return DeleteMe.Result.success(email);
  }

  @Override
  @Transactional
  public VerifyDeleteMe.Result verifyDeleteMe(VerifyDeleteMe operation) {
    UserEmail email = operation.getSecurityUser().getUser().getEmail();
    String code = deleteAccountCache.get(email.toString());

    if (operation.getCode() == null || StringUtils.trimToNull(operation.getCode()) == null) {
      return VerifyDeleteMe.Result.invalidConfirmationCode(email);
    }

    if (code == null) {
      return VerifyDeleteMe.Result.confirmationNotFound(email);
    }

    if (!code.equals(operation.getCode())) {
      return VerifyDeleteMe.Result.invalidConfirmationCode(email);
    }

    var user = operation.getSecurityUser().getUser();

    try {
      productRepository.deleteByUserId(user.getId());
      sessionRepository.deleteByUserId(user.getId());
      userRepository.delete(user);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return VerifyDeleteMe.Result.deleteError(email, e);
    }

    deleteAccountCache.remove(email.toString());

    try {
      mailService.sendDeleteAccountMail(email.toString(), operation.getLocale());
    } catch (Exception e) {
      log.warn("Error while sending delete account mail", e);
    }

    return VerifyDeleteMe.Result.success(email);
  }

  @Override
  @Transactional
  public UploadMeVendorMedia.Result uploadMeVendorMedia(UploadMeVendorMedia operation) {
    if (operation.getMedia().size() != operation.getNewMediaPositions().size()) {
      log.warn("Неверные позиции: новые медиа файлы({}), позиции: {} ({})",
          operation.getMedia().size(), operation.getNewMediaPositions(),
          operation.getNewMediaPositions().size());
      return UploadMeVendorMedia.Result.invalidPosition(
          new RuntimeException("Invalid new media positions"));
    }

    List<MultipartFile> images = new ArrayList<>();
    List<MultipartFile> videos = new ArrayList<>();

    User user = operation.getSecurityUser().getUser();
    VendorDetails vendorDetails = user.getVendorDetails();

    if (vendorDetails == null) {
      vendorDetails = new VendorDetails();
      user.setVendorDetails(vendorDetails);
    }

    List<VendorMedia> vendorMediaImageList = new ArrayList<>();
    List<VendorMedia> vendorMediaVideoList = new ArrayList<>();

    for (int i = 0; i < operation.getMedia().size(); i++) {
      MultipartFile file = operation.getMedia().get(i);

      if (file.isEmpty()) {
        return UploadMeVendorMedia.Result.emptyFile();
      }

      String contentType = Objects.requireNonNullElse(file.getContentType(), "");

      if (contentType.contains("image")) {
        images.add(file);
        vendorMediaImageList.add(createVendorMedia(vendorDetails, file, MediaType.IMAGE));
      } else if (contentType.contains("video")) {
        videos.add(file);
        vendorMediaVideoList.add(createVendorMedia(vendorDetails, file, MediaType.VIDEO));
      } else {
        return UploadMeVendorMedia.Result.unknownContentType(contentType);
      }
    }

    List<String> imageLinks, videoLinks;

    List<VendorMedia> media = vendorMediaRepository.getAllByVendorDetailsId(vendorDetails.getId());

    if (media.size() != operation.getOldMediaIds().size()) {
      log.warn("Неверные позиции: старые медиа из бд {} ({}), старые медиа из запроса: {} ({})",
          media.stream().map(VendorMedia::getId).toList(), media.size(), operation.getOldMediaIds(),
          operation.getOldMediaIds().size());
      return UploadMeVendorMedia.Result.invalidPosition(
          new RuntimeException("Invalid old media positions"));
    }

    try {
      imageLinks = fileStorageRepository.uploadManyImagesToFolder(
          FileStorageFolders.VENDOR_IMAGES.getValue(), images.toArray(MultipartFile[]::new));
      videoLinks = fileStorageRepository.uploadManyVideosToFolder(
          FileStorageFolders.VENDOR_VIDEOS.getValue(), videos.toArray(MultipartFile[]::new));
    } catch (Exception e) {
      return UploadMeVendorMedia.Result.uploadError(user.getId(), e);
    }

    try {
      for (int i = 0; i < imageLinks.size(); i++) {
        String link = imageLinks.get(i);
        vendorMediaImageList.get(i).setUrl(VendorMediaUrl.of(link));
        vendorMediaImageList.get(i)
            .setPosition(VendorMediaPosition.of(operation.getNewMediaPositions().get(i)));
      }

      for (int i = 0; i < videoLinks.size(); i++) {
        String link = videoLinks.get(i);
        vendorMediaVideoList.get(i).setUrl(VendorMediaUrl.of(link));
        vendorMediaVideoList.get(i).setPosition(
            VendorMediaPosition.of(operation.getNewMediaPositions().get(imageLinks.size() + i)));
      }
    } catch (IndexOutOfBoundsException e) {
      log.warn("Позиция нового медиа вышла за границы нового массива: {} ({})",
          operation.getMedia().size(), operation.getNewMediaPositions());
      return UploadMeVendorMedia.Result.invalidPosition(e);
    }

    Map<Long, Integer> oldMediaPositions = new HashMap<>();

    int newOldMediaIndex = 0;
    for (int i = 0; i < operation.getOldMediaIds().size(); i++) {
      if (operation.getNewMediaPositions().contains(i)) {
        newOldMediaIndex += 1;
      }

      oldMediaPositions.put(operation.getOldMediaIds().get(i), newOldMediaIndex);
      newOldMediaIndex++;
    }

    for (VendorMedia oldMedia : media) {
      Integer oldMediaPosition = oldMediaPositions.get(oldMedia.getId());

      if (oldMediaPosition == null) {
        return UploadMeVendorMedia.Result.invalidPosition(
            new RuntimeException("Old media position not found"));
      }

      oldMedia.setPosition(VendorMediaPosition.of(oldMediaPosition));
    }

    media.addAll(vendorMediaImageList);
    media.addAll(vendorMediaVideoList);

    vendorDetails.setMedia(new HashSet<>(media));

    try {
      vendorMediaRepository.saveAll(vendorMediaImageList);
      vendorMediaRepository.saveAll(vendorMediaVideoList);
      User savedUser = userRepository.save(user);
      VendorDto vendorDto = Objects.requireNonNull(VendorDto.of(savedUser, operation.getLocale()),
          "Saved user is null");
      List<VendorMedia> savedMediaList =
          vendorMediaRepository.getAllByVendorDetailsId(vendorDetails.getId());
      List<VendorMediaDto> savedMediaDtoList =
          savedMediaList.stream().map(VendorMediaDto::of).toList();
      vendorDto.getVendorDetails().setMedia(savedMediaDtoList);
      return UploadMeVendorMedia.Result.success(vendorDto,
          vendorMediaImageList.size() + vendorMediaVideoList.size());
    } catch (Exception e) {
      return UploadMeVendorMedia.Result.saveError(user.getId(), e);
    }
  }

  @Override
  @Transactional
  public DeleteMeVendorMediaById.Result deleteMeVendorMediaById(DeleteMeVendorMediaById operation) {
    User user = operation.getSecurityUser().getUser();
    VendorDetails vendorDetails = user.getVendorDetails();

    if (vendorDetails == null) {
      return DeleteMeVendorMediaById.Result.notFound(operation.getId());
    }

    Optional<VendorMedia> vendorMediaOptional = vendorMediaRepository.getById(operation.getId());

    if (vendorMediaOptional.isEmpty()) {
      return DeleteMeVendorMediaById.Result.notFound(operation.getId());
    }

    List<VendorMedia> vendorMediaList =
        vendorMediaRepository.getAllByVendorDetailsId(vendorDetails.getId());
    List<VendorMedia> resultMediaList = new ArrayList<>();

    int index = 0;
    for (VendorMedia vendorMedia : vendorMediaList) {
      if (vendorMedia.getId().equals(operation.getId())) {
        try {
          vendorMediaRepository.delete(vendorMedia);
          fileStorageRepository.deleteMediaByLink(vendorMedia.getUrl().toString());
          continue;
        } catch (Exception e) {
          return DeleteMeVendorMediaById.Result.deleteMediaError(e, operation.getId());
        }
      }

      vendorMedia.setPosition(VendorMediaPosition.of(index));
      resultMediaList.add(vendorMedia);
      index++;
    }

    try {
      vendorMediaRepository.saveAll(resultMediaList);
      vendorMediaRepository.flush();
      User newUser = userRepository.getUserById(user.getId()).orElseThrow();
      VendorDto vendorDto = Objects.requireNonNull(VendorDto.of(newUser, operation.getLocale()),
          "Saved user is null");
      List<VendorMedia> savedMediaList =
          vendorMediaRepository.getAllByVendorDetailsId(vendorDetails.getId());
      List<VendorMediaDto> savedMediaDtoList =
          savedMediaList.stream().map(VendorMediaDto::of).toList();
      vendorDto.getVendorDetails().setMedia(savedMediaDtoList);
      return DeleteMeVendorMediaById.Result.success(vendorDto, operation.getId());
    } catch (Exception e) {
      return DeleteMeVendorMediaById.Result.saveError(e, operation.getId());
    }
  }

  @Override
  @Transactional
  public DeleteMeVendorMediaByIdList.Result deleteMeVendorMediaByIdList(
      DeleteMeVendorMediaByIdList operation) {
    User user = operation.getSecurityUser().getUser();
    VendorDetails vendorDetails = user.getVendorDetails();

    Set<String> links = new HashSet<>();
    List<VendorMedia> mediaToDelete = new ArrayList<>();

    if (vendorDetails != null) {
      List<VendorMedia> vendorMediaList =
          vendorMediaRepository.getAllByVendorDetailsId(vendorDetails.getId());

      vendorMediaList.removeIf(m -> {
        if (operation.getIds().contains(m.getId())) {
          mediaToDelete.add(m);
          links.add(m.getUrl().toString());
          return true;
        }
        return false;
      });
    }

    try {
      vendorMediaRepository.deleteAll(mediaToDelete);
      vendorMediaRepository.flush();
    } catch (Exception e) {
      return DeleteMeVendorMediaByIdList.Result.deleteMediaError(e);
    }

    try {
      fileStorageRepository.deleteMediaByLink(links.toArray(String[]::new));
    } catch (Exception e) {
      return DeleteMeVendorMediaByIdList.Result.deleteMediaError(e);
    }

    var newUser = userRepository.getUserById(user.getId()).orElseThrow();
    var dto = vendorDetails != null ? VendorDto.of(newUser, operation.getLocale()) :
        UserDto.of(newUser, operation.getLocale());
    return DeleteMeVendorMediaByIdList.Result.success(dto);
  }

  @Override
  @Transactional
  public DeleteMeReviewById.Result deleteMeReviewById(DeleteMeReviewById operation) {
    User user = operation.getSecurityUser().getUser();

    Optional<ProductReview> productReviewOptional =
        productReviewRepository.getByIdAndUserId(operation.getId(), user.getId());

    if (productReviewOptional.isEmpty()) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteMeReviewById.Result.notFound(operation.getId(), user);
    }

    try {
      productReviewRepository.delete(productReviewOptional.get());
      return DeleteMeReviewById.Result.success(operation.getId(), user);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteMeReviewById.Result.deleteError(operation.getId(), user, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public GetMeFavoriteProducts.Result getMeFavoriteProducts(GetMeFavoriteProducts operation) {
    Long userId = operation.getSecurityUser().getUser().getId();
    List<ProductSummaryView> products =
        productSummaryViewRepository.getProductSummaryViewByInUserFavoritesWithUserId(userId);
    List<ProductSummaryViewDto> dtos = products.stream()
        .map(p -> ProductSummaryViewDto.of(
            localizationManager.localizePrice(p, operation.getLocale()),
            operation.getLocale().getLanguage())
        )
        .toList();
    return GetMeFavoriteProducts.Result.success(dtos);
  }

  @Override
  @Transactional
  public ToggleMeFavoriteProductById.Result toggleFavoriteProductById(
      ToggleMeFavoriteProductById operation) {
    Optional<Product> productOptional = productRepository.getProductById(operation.getProductId());

    if (productOptional.isEmpty()) {
      return ToggleMeFavoriteProductById.Result.productNotFound(operation.getProductId());
    }

    Product product = productOptional.get();
    User user =
        userRepository.getUserById(operation.getSecurityUser().getUser().getId()).orElseThrow();

    boolean status = productRepository.existsInFavorite(user.getId(), product.getId());

    if (status) {
      user.getFavoriteProducts().remove(product);
    } else {
      user.getFavoriteProducts().add(product);
    }

    try {
      userRepository.save(user);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ToggleMeFavoriteProductById.Result.saveError(e, user.getEmail(),
          operation.getProductId());
    }

    return ToggleMeFavoriteProductById.Result.success(
        ToggleUserFavoriteProductStatusDto.of(!status));
  }

  /* ========== PRIVATE ========== */

  @Transactional(readOnly = true)
  protected Optional<Session> getSessionBySecurityUser(SecurityUser securityUser) {
    Long userId = securityUser.getUser().getId();
    SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
    return sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);
  }

  @Transactional(readOnly = true)
  protected Page<ProductReviewDto> getProductReviewsBy(Specification<ProductReview> specification,
                                                       Pageable pageable, Locale locale) {
    Page<ProductReview> productReviewPageWithoutMedia =
        productReviewRepository.getPage(specification, pageable);

    if (!productReviewPageWithoutMedia.isEmpty()) {
      List<Long> productReviewIds =
          productReviewPageWithoutMedia.map(ProductReview::getId).toList();

      List<ProductReview> productReviewPageWithMedia =
          productReviewRepository.getByIdInWithMedia(productReviewIds);

      Map<Long, ProductReview> productReviewMap = productReviewPageWithMedia.stream()
          .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

      return productReviewPageWithoutMedia
          .map(r -> {
            if (productReviewMap.containsKey(r.getId())) {
              r.setMedia(productReviewMap.get(r.getId()).getMedia());
            }

            return r;
          })
          .map(r -> translateProductReview(r, locale))
          .map(r -> ProductReviewDto.of(r, locale));
    }

    return Page.empty(pageable);
  }

  private VendorMedia createVendorMedia(VendorDetails vendorDetails, MultipartFile file,
                                        MediaType mediaType) {
    VendorMedia vendorMedia = new VendorMedia();
    vendorMedia.setVendorDetails(vendorDetails);
    vendorMedia.setMediaType(mediaType);
    vendorMedia.setMimeType(VendorMediaMimeType.of(file.getContentType()));
    return vendorMedia;
  }
}
