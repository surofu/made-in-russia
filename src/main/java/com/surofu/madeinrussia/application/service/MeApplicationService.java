package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.service.async.AsyncMeApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.ProductSummaryViewRepository;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.repository.specification.ProductSummarySpecifications;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.*;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.view.ProductSummaryView;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final SessionRepository sessionRepository;
    private final ProductSummaryViewRepository productSummaryViewRepository;
    private final ProductReviewRepository productReviewRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final JwtUtils jwtUtils;

    private final AsyncSessionApplicationService asyncSessionApplicationService;
    private final AsyncMeApplicationService asyncMeApplicationService;

    @Value("${app.session.secret}")
    private String sessionSecret;

    @Override
    @Transactional(readOnly = true)
    public GetMe.Result getMeByJwt(GetMe operation) {
        SecurityUser securityUser = operation.getSecurityUser();
        Optional<Session> existingSession = getSessionBySecurityUser(securityUser);

        if (existingSession.isEmpty()) {
            return GetMe.Result.sessionWithUserIdAndDeviceIdNotFound(
                    securityUser.getUser().getId(),
                    securityUser.getSessionInfo().getDeviceId()
            );
        }

        Optional<Session> sessionWithUser = sessionRepository.getSessionById(existingSession.get().getId());

        if (sessionWithUser.isEmpty()) {
            return GetMe.Result.sessionWithIdNotFound(existingSession.get().getId());
        }

        User user = sessionWithUser.get().getUser();

        if (user.getRole().equals(UserRole.ROLE_VENDOR)) {
            VendorDto vendorDto = VendorDto.of(user);
            return GetMe.Result.success(vendorDto);
        }

        UserDto userDto = UserDto.of(user);

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
        Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

        List<Category> allChildCategories = categoryRepository.getCategoriesByIds(operation.getCategoryIds());
        List<Long> categoryIdsWithChildren = new ArrayList<>();

        if (operation.getCategoryIds() != null) {
            categoryIdsWithChildren.addAll(operation.getCategoryIds());
        }

        categoryIdsWithChildren.addAll(allChildCategories.stream().map(Category::getId).toList());

        Specification<ProductSummaryView> specification = Specification
                .where(ProductSummarySpecifications.byUserId(operation.getSecurityUser().getUser().getId()))
                .and(ProductSummarySpecifications.byTitle(operation.getTitle()))
                .and(ProductSummarySpecifications.hasCategories(categoryIdsWithChildren))
                .and(ProductSummarySpecifications.hasDeliveryMethods(operation.getDeliveryMethodIds()))
                .and(ProductSummarySpecifications.priceBetween(operation.getMinPrice(), operation.getMaxPrice()));

        Page<ProductSummaryView> productSummaryViewPage = productSummaryViewRepository.getProductSummaryViewPage(specification, pageable);
        Page<ProductSummaryViewDto> productSummaryViewDtoPage = productSummaryViewPage.map(ProductSummaryViewDto::of);

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

        if (operation.getUserRegion() != null && !user.getRole().equals(UserRole.ROLE_VENDOR)) {
            user.setRegion(operation.getUserRegion());
        }

        asyncMeApplicationService.updateUser(user);

        return UpdateMe.Result.success(UserDto.of(user));
    }

    private Optional<Session> getSessionBySecurityUser(SecurityUser securityUser) {
        Long userId = securityUser.getUser().getId();
        SessionDeviceId sessionDeviceId = securityUser.getSessionInfo().getDeviceId();
        return sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);
    }

    private Page<ProductReviewDto> getProductReviewsBy(Specification<ProductReview> specification, Pageable pageable) {
        Page<ProductReview> productReviewPageWithoutMedia = productReviewRepository.findAll(specification, pageable);

        if (!productReviewPageWithoutMedia.isEmpty()) {
            List<Long> productReviewIds = productReviewPageWithoutMedia.map(ProductReview::getId).toList();

            List<ProductReview> productReviewPageWithMedia = productReviewRepository.findByIdInWithMedia(productReviewIds);

            Map<Long, ProductReview> productReviewMap = productReviewPageWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            return productReviewPageWithoutMedia.map(r -> {
                r.setMedia(productReviewMap.get(r.getId()).getMedia());
                return ProductReviewDto.of(r);
            });
        }

        return Page.empty();
    }
}
