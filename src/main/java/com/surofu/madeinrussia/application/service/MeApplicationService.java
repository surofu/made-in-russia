package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.product.ProductReviewDto;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.dto.session.SessionDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorCountryDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorFaqDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.madeinrussia.application.enums.FileStorageFolders;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserAvatar;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    private final VendorProductCategoryRepository vendorProductCategoryRepository;
    private final VendorFaqRepository vendorFaqRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final JwtUtils jwtUtils;
    private final FileStorageRepository fileStorageRepository;

    private final AsyncSessionApplicationService asyncSessionApplicationService;

    @Value("${app.session.secret}")
    private String sessionSecret;

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

        UserView userView = userRepository.getViewById(operation.getSecurityUser().getUser().getId()).orElseThrow();

        if (userView.getRole().equals(UserRole.ROLE_VENDOR)) {
            VendorDto vendorDto = VendorDto.of(userView);

            List<VendorCountryView> vendorCountryViewList = vendorCountryRepository.getAllViewsByVendorDetailsIdAndLang(
                    operation.getSecurityUser().getUser().getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );

            List<VendorProductCategoryView> vendorProductCategoryViewList = vendorProductCategoryRepository.getAllViewsByVendorDetailsIdAndLang(
                    operation.getSecurityUser().getUser().getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );
            List<VendorFaqView> vendorFaqViewList = vendorFaqRepository.getAllViewsByVendorDetailsIdAndLang(
                    operation.getSecurityUser().getUser().getVendorDetails().getId(),
                    operation.getLocale().getLanguage()
            );

            List<VendorCountryDto> vendorCountryDtoList = vendorCountryViewList.stream().map(VendorCountryDto::of).toList();
            List<VendorProductCategoryDto> vendorProductCategoryDtoList = vendorProductCategoryViewList.stream().map(VendorProductCategoryDto::of).toList();
            List<VendorFaqDto> vendorFaqDtoList = vendorFaqViewList.stream().map(VendorFaqDto::of).toList();

            vendorDto.getVendorDetails().setCountries(vendorCountryDtoList);
            vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtoList);
            vendorDto.getVendorDetails().setFaq(vendorFaqDtoList);

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
                .map(p -> ProductSummaryViewDto.of(operation.getLocale().getLanguage(), p));

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

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser);

        return RefreshMeCurrentSession.Result.success(tokenDto);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userById", key = "#operation.securityUser.user.id"),
            @CacheEvict(value = "userByLogin", key = "#operation.securityUser.user.login.value"),
            @CacheEvict(value = "userByEmail", key = "#operation.securityUser.user.email.value"),
            @CacheEvict(value = "userByUsername", key = "#operation.securityUser.user.email.value")
    })
    public UpdateMe.Result updateMe(UpdateMe operation) {
        User user = operation.getSecurityUser().getUser();

        if (operation.getUserPhoneNumber() != null) {
            user.setPhoneNumber(operation.getUserPhoneNumber());
        }

        if (operation.getUserRegion() != null && !user.getRole().equals(UserRole.ROLE_VENDOR)) {
            user.setRegion(operation.getUserRegion());
        }

        if (user.getRole().equals(UserRole.ROLE_VENDOR)) {
            VendorDetails vendorDetails = user.getVendorDetails();

            if (operation.getInn() != null) {
                vendorDetails.setInn(operation.getInn());
            }

            if (operation.getCountryNames() != null && !operation.getCountryNames().isEmpty()) {
                Set<VendorCountry> vendorCountries = new HashSet<>();

                for (VendorCountryName countryName : operation.getCountryNames()) {
                    List<VendorCountry> existingCountries = vendorDetails.getVendorCountries().stream()
                            .filter(c -> c.getName().toString().equals(countryName.toString()))
                            .toList();

                    if (existingCountries.isEmpty()
                    ) {
                        VendorCountry vendorCountry = new VendorCountry();
                        vendorCountry.setVendorDetails(vendorDetails);
                        vendorCountry.setName(countryName);
                        vendorCountries.add(vendorCountry);
                    } else {
                        vendorCountries.addAll(existingCountries);
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

            user.setVendorDetails(vendorDetails);
        }

        User newUser = userRepository.save(user);

        if (user.getRole().equals(UserRole.ROLE_VENDOR)) {
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
        if (operation.getFile() != null && !operation.getFile().isEmpty()) {
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
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return SaveMeAvatar.Result.saveError(e);
            }
        }

        return SaveMeAvatar.Result.success();
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

            try {
                operation.getSecurityUser().getUser().setAvatar(null);
                userRepository.save(operation.getSecurityUser().getUser());
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DeleteMeAvatar.Result.deleteError(e);
            }
        }

        return DeleteMeAvatar.Result.success();
    }

    /* ========== PRIVATE ========== */

    private Optional<Session> getSessionBySecurityUser(SecurityUser securityUser) {
        Long userId = securityUser.getUser().getId();
        SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
        return sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);
    }

    private Page<ProductReviewDto> getProductReviewsBy(Specification<ProductReview> specification, Pageable pageable) {
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
