package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.cache.DeleteAccountCache;
import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.product.ProductReviewDto;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.dto.session.SessionDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorCountryDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorFaqDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.madeinrussia.application.enums.FileStorageFolders;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.AuthUtils;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserAvatar;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSite;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
import com.surofu.madeinrussia.core.service.mail.MailService;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.*;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import io.jsonwebtoken.JwtException;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ProductSummaryViewRepository productSummaryViewRepository;
    private final ProductReviewRepository productReviewRepository;
    private final VendorCountryRepository vendorCountryRepository;
    private final VendorPhoneNumberRepository vendorPhoneNumberRepository;
    private final VendorEmailRepository vendorEmailRepository;
    private final VendorSiteRepository vendorSiteRepository;
    private final VendorProductCategoryRepository vendorProductCategoryRepository;
    private final VendorFaqRepository vendorFaqRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final JwtUtils jwtUtils;
    private final FileStorageRepository fileStorageRepository;
    private final TranslationRepository translationRepository;

    private final AsyncSessionApplicationService asyncSessionApplicationService;
    private final MailService mailService;
    private final DeleteAccountCache deleteAccountCache;

    @Value("${app.session.secret}")
    private String sessionSecret;

    @Value("${app.redis.verification-ttl-duration}")
    private Duration verificationTtlDuration;

    @Override
    @Transactional(readOnly = true)
    public GetMe.Result getMeByJwt(GetMe operation) {
        SecurityUser securityUser = operation.getSecurityUser();
        Optional<Session> existingSession = getSessionBySecurityUser(securityUser);

        if (existingSession.isEmpty() && !sessionSecret.equals(securityUser.getSessionInfo().getSessionKey())) {
            return GetMe.Result.sessionWithUserIdAndDeviceIdNotFound(
                    securityUser.getUser().getId(),
                    securityUser.getSessionInfo().getDeviceId()
            );
        }

        UserView userView = userRepository.getViewById(securityUser.getUser().getId()).orElseThrow();

        if (userView.getVendorDetails() != null) {
            VendorDto vendorDto = VendorDto.of(userView);

            List<VendorCountryView> vendorCountryViewList = vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );
            List<VendorProductCategoryView> vendorProductCategoryViewList = vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );
            List<VendorFaqView> vendorFaqViewList = vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
                    userView.getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );
            List<VendorPhoneNumber> vendorPhoneNumberList = vendorPhoneNumberRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());
            List<VendorEmail> vendorEmailList = vendorEmailRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());
            List<VendorSite> vendorSiteList = vendorSiteRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());

            List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream().map(VendorCountryDto::of).toList();
            List<VendorProductCategoryDto> vendorProductCategoryDtoList = vendorProductCategoryViewList.stream().map(VendorProductCategoryDto::of).toList();
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

            vendorDto.getVendorDetails().setCountries(vendorCountryDtoList);
            vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtoList);
            vendorDto.getVendorDetails().setFaq(vendorFaqDtoList);
            vendorDto.getVendorDetails().setPhoneNumbers(phoneNumbers);
            vendorDto.getVendorDetails().setEmails(emails);
            vendorDto.getVendorDetails().setSites(sites);

            return GetMe.Result.success(vendorDto);
        }

        UserDto userDto = UserDto.of(userView);
        return GetMe.Result.success(userDto);
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
    public GetMeProductSummaryViewPage.Result getMeProductSummaryViewPage(GetMeProductSummaryViewPage operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), Sort.by("creationDate").descending());

        List<Long> allChildCategoriesIds = categoryRepository.getCategoriesIdsByIds(operation.getCategoryIds());
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
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()));


        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage
                .map(p -> ProductSummaryViewDto.of(p, operation.getLocale().getLanguage()));

        return GetMeProductSummaryViewPage.Result.success(productSummaryViewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetMeReviewPage.Result getMeReviewPage(GetMeReviewPage operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        Specification<ProductReview> specification = Specification
                .where(ProductReviewSpecifications.byUserId(operation.getSecurityUser().getUser().getId()))
                .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()));

        Page<ProductReviewDto> productReviewDtoPage = getProductReviewsBy(specification, pageable);

        return GetMeReviewPage.Result.success(productReviewDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public GetMeVendorProductReviewPage.Result getMeVendorProductReviewPage(GetMeVendorProductReviewPage operation) {
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        Specification<ProductReview> specification = Specification
                .where(ProductReviewSpecifications.byProductUserId(operation.getSecurityUser().getUser().getId()))
                .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()));

        Page<ProductReviewDto> productReviewDtoPage = getProductReviewsBy(specification, pageable);

        return GetMeVendorProductReviewPage.Result.success(productReviewDtoPage);
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

        Optional<Session> session = sessionRepository.getSessionByUserIdAndDeviceId(userId, operation.getSessionInfo().getDeviceId());

        if (session.isEmpty() && !sessionSecret.equals(operation.getSessionInfo().getSessionKey())) {
            return RefreshMeCurrentSession.Result.sessionNotFound(operation.getSessionInfo().getDeviceId());
        }

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        TokenDto tokenDto = TokenDto.of(accessToken);

        SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
        Session oldSession = sessionRepository
                .getSessionByUserIdAndDeviceId(securityUser.getUser().getId(), sessionDeviceId)
                .orElse(new Session());
        Session newSession = Session.of(securityUser.getSessionInfo(), securityUser.getUser(), oldSession);

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

        if (operation.getUserPhoneNumber() != null) {
            user.setPhoneNumber(operation.getUserPhoneNumber());
        }

        if (operation.getUserRegion() != null && !user.getRole().equals(UserRole.ROLE_VENDOR)) {
            user.setRegion(operation.getUserRegion());
        }

        if (user.getVendorDetails() != null) {
            VendorDetails vendorDetails = user.getVendorDetails();

            if (operation.getInn() != null) {
                vendorDetails.setInn(operation.getInn());
            }

            if (operation.getDescription() != null) {
                vendorDetails.setDescription(operation.getDescription());
            }

            if (operation.getCountryNames() != null && !operation.getCountryNames().isEmpty()) {
                Set<VendorCountry> vendorCountries = new HashSet<>();

                for (VendorCountryName countryName : operation.getCountryNames()) {
                    Optional<VendorCountry> existingCountry = vendorDetails.getVendorCountries().stream()
                            .filter(c -> c.getName().toString().equals(countryName.toString()))
                            .findFirst();

                    if (existingCountry.isEmpty()) {
                        VendorCountry vendorCountry = new VendorCountry();
                        vendorCountry.setVendorDetails(vendorDetails);

                        HstoreTranslationDto translationDto;

                        try {
                            translationDto = translationRepository.expand(countryName.toString());
                        } catch (Exception e) {
                            return UpdateMe.Result.translationError(e);
                        }

                        countryName.setTranslations(translationDto);
                        vendorCountry.setName(countryName);
                        vendorCountries.add(vendorCountry);
                    } else {
                        vendorCountries.add(existingCountry.get());
                    }
                }

                vendorDetails.setVendorCountries(vendorCountries);
            }

            if (operation.getCategoryNames() != null && !operation.getCategoryNames().isEmpty()) {
                Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

                for (VendorProductCategoryName categoryName : operation.getCategoryNames()) {
                    List<VendorProductCategory> existingCategories = vendorDetails.getVendorProductCategories().stream()
                            .filter(c -> c.getName().toString().equals(categoryName.toString()))
                            .toList();

                    if (existingCategories.isEmpty()) {
                        VendorProductCategory vendorProductCategory = new VendorProductCategory();
                        vendorProductCategory.setVendorDetails(vendorDetails);
                        vendorProductCategory.setName(categoryName);
                        vendorProductCategories.add(vendorProductCategory);
                    } else {
                        vendorProductCategories.addAll(existingCategories);
                    }
                }

                vendorDetails.setVendorProductCategories(vendorProductCategories);
            }

            if (operation.getPhoneNumbers() != null) {
                Set<VendorPhoneNumber> phoneNumberSet = new HashSet<>();

                for (VendorPhoneNumberPhoneNumber number : operation.getPhoneNumbers()) {
                    VendorPhoneNumber phoneNumber = new VendorPhoneNumber();
                    phoneNumber.setVendorDetails(vendorDetails);
                    phoneNumber.setPhoneNumber(number);
                    phoneNumberSet.add(phoneNumber);
                }

                vendorDetails.setPhoneNumbers(phoneNumberSet);
            }

            if (operation.getEmails() != null) {
                Set<VendorEmail> emailSet = new HashSet<>();

                for (VendorEmailEmail email : operation.getEmails()) {
                    VendorEmail vendorEmail = new VendorEmail();
                    vendorEmail.setVendorDetails(vendorDetails);
                    vendorEmail.setEmail(email);
                    emailSet.add(vendorEmail);
                }

                vendorDetails.setEmails(emailSet);
            }

            if (operation.getSites() != null) {
                Set<VendorSite> siteSet = new HashSet<>();

                for (VendorSiteUrl url : operation.getSites()) {
                    VendorSite site = new VendorSite();
                    site.setVendorDetails(vendorDetails);
                    site.setUrl(url);
                    siteSet.add(site);
                }

                vendorDetails.setSites(siteSet);
            }

            user.setVendorDetails(vendorDetails);
        }

        User newUser = userRepository.save(user);

        if (newUser.getVendorDetails() != null) {
            for (VendorCountry country : newUser.getVendorDetails().getVendorCountries()) {
                switch (operation.getLocale().getLanguage()) {
                    case "en": {
                        var name = VendorCountryName.of(country.getName().getTranslations().textEn());
                        name.setTranslations(country.getName().getTranslations());
                        country.setName(name);
                        break;
                    }
                    case "ru": {
                        var name = VendorCountryName.of(country.getName().getTranslations().textRu());
                        name.setTranslations(country.getName().getTranslations());
                        country.setName(name);
                        break;
                    }
                    case "zh": {
                        var name = VendorCountryName.of(country.getName().getTranslations().textZh());
                        name.setTranslations(country.getName().getTranslations());
                        country.setName(name);
                        break;
                    }
                    default:
                        break;
                }
            }


            return UpdateMe.Result.success(VendorDto.of(newUser));
        }

        return UpdateMe.Result.success(UserDto.of(newUser));
    }

    @Override
    @Transactional
    public DeleteMeSessionById.Result deleteMeSessionById(DeleteMeSessionById operation) {
        Optional<Session> session = sessionRepository.getSessionById(operation.getSessionId());

        if (session.isPresent()) {
            asyncSessionApplicationService.removeSessionById(operation.getSessionId());
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
            url = fileStorageRepository.uploadImageToFolder(operation.getFile(), FileStorageFolders.USERS_AVATARS.getValue());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SaveMeAvatar.Result.saveError(e);
        }

        UserAvatar avatar = UserAvatar.of(url);
        operation.getSecurityUser().getUser().setAvatar(avatar);

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
        if (operation.getSecurityUser().getUser().getAvatar() != null) {
            try {
                fileStorageRepository.deleteMediaByLink(operation.getSecurityUser().getUser().getAvatar().getUrl());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DeleteMeAvatar.Result.deleteError(e);
            }

            operation.getSecurityUser().getUser().setAvatar(null);

            try {
                userRepository.save(operation.getSecurityUser().getUser());
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
        LocalDateTime expirationDate = LocalDateTime.now().plus(verificationTtlDuration);

        try {
            mailService.sendConfirmDeleteAccountMail(email.toString(), code, expirationDate, operation.getLocale());
            return DeleteMe.Result.success(email);
        } catch (Exception e) {
            return DeleteMe.Result.sendMailError(e);
        }
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

        try {
            userRepository.delete(operation.getSecurityUser().getUser());
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

    /* ========== PRIVATE ========== */

    @Transactional(readOnly = true)
    protected Optional<Session> getSessionBySecurityUser(SecurityUser securityUser) {
        Long userId = securityUser.getUser().getId();
        SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
        return sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);
    }

    @Transactional(readOnly = true)
    protected Page<ProductReviewDto> getProductReviewsBy(Specification<ProductReview> specification, Pageable pageable) {
        Page<ProductReview> productReviewPageWithoutMedia = productReviewRepository.getPage(specification, pageable);

        if (!productReviewPageWithoutMedia.isEmpty()) {
            List<Long> productReviewIds = productReviewPageWithoutMedia.map(ProductReview::getId).toList();

            List<ProductReview> productReviewPageWithMedia = productReviewRepository.getByIdInWithMedia(productReviewIds);

            Map<Long, ProductReview> productReviewMap = productReviewPageWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            return productReviewPageWithoutMedia.map(r -> {
                r.setMedia(productReviewMap.get(r.getId()).getMedia());
                return ProductReviewDto.of(r);
            });
        }

        return Page.empty(pageable);
    }
}
