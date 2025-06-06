package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.application.service.async.AsyncMeApplicationService;
import com.surofu.madeinrussia.application.service.async.AsyncSessionApplicationService;
import com.surofu.madeinrussia.application.utils.JwtUtils;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserRole;
import com.surofu.madeinrussia.core.repository.ProductReviewRepository;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.*;
import com.surofu.madeinrussia.core.service.user.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductReviewRepository productReviewRepository;
    private final UserService userService;
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
    @Cacheable(
            value = "meProductReviewPages",
            key = """
                    {
                     #operation.securityUser.user.id,
                     #operation.page, #operation.size,
                     #operation.minRating, #operation.maxRating
                    }
                    """,
            unless = "#result.getProductReviewDtoPage().isEmpty()"
    )
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
    @Cacheable(
            value = "meVendorProductReviewPages",
            key = """
                    {
                     #operation.securityUser.user.id,
                     #operation.page, #operation.size,
                     #operation.minRating, #operation.maxRating
                    }
                    """,
            unless = "#result.getVendorProductReviewDtoPage().isEmpty()"
    )
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
        String refreshToken = operation.getCommand().refreshToken();

        UserEmail userEmail;

        try {
            userEmail = jwtUtils.extractUserEmailFromRefreshToken(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            return RefreshMeCurrentSession.Result.invalidRefreshToken(refreshToken, ex);
        }

        SecurityUser securityUser;

        try {
            securityUser = (SecurityUser) userService.loadUserByUsername(userEmail.toString());
        } catch (UsernameNotFoundException ex) {
            return RefreshMeCurrentSession.Result.userNotFound(userEmail);
        }

        Long userId = securityUser.getUser().getId();

        SessionInfo sessionInfo = securityUser.getSessionInfo();
        SessionDeviceId sessionDeviceId = sessionInfo.getDeviceId();

        Optional<Session> session = sessionRepository.getSessionByUserIdAndDeviceId(userId, sessionDeviceId);

        if (session.isEmpty() && !sessionSecret.equals(sessionInfo.getSessionKey())) {
            return RefreshMeCurrentSession.Result.sessionNotFound(sessionDeviceId);
        }

        String accessToken = jwtUtils.generateAccessToken(securityUser);
        TokenDto tokenDto = TokenDto.of(accessToken);

        asyncSessionApplicationService.saveOrUpdateSessionFromHttpRequest(securityUser)
                .exceptionally(ex -> {
                    log.error("Error saving session", ex);
                    return null;
                });

        return RefreshMeCurrentSession.Result.success(tokenDto);
    }

    @Override
    public UpdateMe.Result updateMe(UpdateMe operation) {
        log.info("Before getting user from security user");

        User user = operation.getSecurityUser().getUser();

        log.info("After getting user from security user");

        if (operation.getUserRegion() != null) {
            user.setRegion(operation.getUserRegion());
        }

        log.info("Before updating user");

        asyncMeApplicationService.updateUser(user);

        log.info("After updating user");

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
