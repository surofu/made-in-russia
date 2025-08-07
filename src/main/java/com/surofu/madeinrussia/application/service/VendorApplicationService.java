package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorDto;
import com.surofu.madeinrussia.application.dto.vendor.VendorReviewPageDto;
import com.surofu.madeinrussia.application.service.async.AsyncVendorViewApplicationService;
import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorApplicationService implements VendorService {

    private final UserRepository userRepository;
    private final VendorDetailsRepository vendorDetailsRepository;
    private final VendorViewRepository vendorViewRepository;
    private final ProductReviewRepository productReviewRepository;
    private final VendorFaqRepository vendorFaqRepository;
    private final TranslationRepository translationRepository;

    private final AsyncVendorViewApplicationService asyncVendorViewApplicationService;

    @Override
    @Transactional
    public GetVendorById.Result getVendorById(GetVendorById operation) {
        Optional<User> user = userRepository.getVendorById(operation.getVendorId());

        if (user.isEmpty()) {
            return GetVendorById.Result.notFound(operation.getVendorId());
        }

        AbstractAccountDto dto = UserDto.of(user.get());
        VendorDetails vendorDetails = user.get().getVendorDetails();

        if (vendorDetails != null) {
            dto = VendorDto.of(user.get());
            Long viewsCount = vendorViewRepository.getCountByVendorDetailsId(vendorDetails.getId());
            user.get().getVendorDetails().setVendorViewsCount(viewsCount);

            if (operation.getSecurityUser() != null) {
                User currentUser = operation.getSecurityUser().getUser();

                if (!currentUser.getId().equals(user.get().getId())) {
                    VendorView vendorView = new VendorView();
                    vendorView.setVendorDetails(vendorDetails);
                    vendorView.setUser(currentUser);
                    asyncVendorViewApplicationService.saveVendorViewInDatabase(vendorView);
                }
            }
        }

        return GetVendorById.Result.success(dto);
    }

    @Override
    @Transactional
    public GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation) {
        if (userRepository.existsVendorById(operation.getVendorId())) {
            Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

            Specification<ProductReview> specification = Specification
                    .where(ProductReviewSpecifications.byProductUserId(operation.getVendorId()))
                    .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(), operation.getMaxRating()));

            Page<ProductReview> productReviewPage = productReviewRepository.getPage(specification, pageable);

            if (productReviewPage.getContent().isEmpty()) {
                VendorReviewPageDto vendorReviewPageDto = VendorReviewPageDto.of(productReviewPage, 0);

                return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
            }

            List<Long> reviewIds = productReviewPage.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();

            List<ProductReview> reviewsWithMedia = productReviewRepository.getByIdInWithMedia(reviewIds);

            Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            Page<ProductReview> productReviewPageWithMedia = productReviewPage.map(p -> {
                p.setMedia(reviewMap.get(p.getId()).getMedia());
                return p;
            });

            Double ratingAverage = productReviewRepository.findAverageRatingByVendorId(operation.getVendorId());

            VendorReviewPageDto vendorReviewPageDto = VendorReviewPageDto.of(productReviewPageWithMedia, ratingAverage);

            return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
        }

        return GetVendorReviewPageById.Result.vendorNotFound(operation.getVendorId());
    }

    @Override
    @Transactional
    public CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation) {
        VendorDetails vendorDetails = operation.getSecurityUser().getUser().getVendorDetails();

        VendorFaq faq = new VendorFaq();
        faq.setVendorDetails(vendorDetails);
        faq.setQuestion(operation.getQuestion());
        faq.setAnswer(operation.getAnswer());

        vendorFaqRepository.save(faq);

        return CreateVendorFaq.Result.success();
    }

    @Override
    @Transactional
    public DeleteVendorFaqById.Result deleteVendorFaqById(DeleteVendorFaqById operation) {
        if (!vendorFaqRepository.existsByIdAndVendorId(operation.getFaqId(), operation.getSecurityUser().getUser().getId())) {
            return DeleteVendorFaqById.Result.notFound(operation.getFaqId());
        }

        VendorDetails newVendorDetails = operation.getSecurityUser().getUser().getVendorDetails();
        newVendorDetails.getFaq().removeIf(faq -> faq.getId().equals(operation.getFaqId()));
        operation.getSecurityUser().getUser().setVendorDetails(newVendorDetails);

        userRepository.save(operation.getSecurityUser().getUser());

        return DeleteVendorFaqById.Result.success(operation.getFaqId());
    }

    @Override
    @Transactional
    public ForceUpdateVendorById.Result forceUpdateVendorById(ForceUpdateVendorById operation) {
        // Validation
        if (operation.getVendorCountries().isEmpty()) {
            return ForceUpdateVendorById.Result.emptyVendorCountries();
        }

        if (operation.getVendorProductCategories().isEmpty()) {
            return ForceUpdateVendorById.Result.emptyVendorProductCategories();
        }

        Optional<User> user = userRepository.getUserById(operation.getId());

        if (user.isEmpty()) {
            return ForceUpdateVendorById.Result.notFound(operation.getId());
        }

        VendorDetails vendorDetails = Objects.requireNonNullElse(user.get().getVendorDetails(), new VendorDetails());

        if (!user.get().getEmail().equals(operation.getEmail()) && userRepository.existsUserByEmail(operation.getEmail())) {
            return ForceUpdateVendorById.Result.emailAlreadyExists(operation.getEmail());
        }

        if (!user.get().getLogin().equals(operation.getLogin()) && userRepository.existsUserByLogin(operation.getLogin())) {
            return ForceUpdateVendorById.Result.loginAlreadyExists(operation.getLogin());
        }

        if (!user.get().getPhoneNumber().equals(operation.getPhoneNumber()) && userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
            return ForceUpdateVendorById.Result.phoneNumberAlreadyExists(operation.getPhoneNumber());
        }

        VendorDetailsInn inn = Objects.requireNonNullElse(vendorDetails.getInn(), operation.getInn());
        if (!inn.equals(operation.getInn()) && vendorDetailsRepository.existsByInn(operation.getInn())) {
            return ForceUpdateVendorById.Result.innAlreadyExists(operation.getInn());
        }

        // Setting
        user.get().setEmail(operation.getEmail());
        user.get().setLogin(operation.getLogin());
        user.get().setPhoneNumber(operation.getPhoneNumber());

        vendorDetails.setUser(user.get());
        user.get().setVendorDetails(vendorDetails);

        user.get().getVendorDetails().setInn(operation.getInn());

        List<VendorCountry> vendorCountryList = new ArrayList<>(operation.getVendorCountries().size());

        for (VendorCountryName name : operation.getVendorCountries()) {
            VendorCountry vendorCountry = new VendorCountry();
            vendorCountry.setVendorDetails(user.get().getVendorDetails());
            vendorCountry.setName(name);
            vendorCountryList.add(vendorCountry);
        }

        List<VendorProductCategory> vendorProductCategoryList = new ArrayList<>(operation.getVendorProductCategories().size());

        for (VendorProductCategoryName name : operation.getVendorProductCategories()) {
            VendorProductCategory vendorProductCategory = new VendorProductCategory();
            vendorProductCategory.setVendorDetails(user.get().getVendorDetails());
            vendorProductCategory.setName(name);
            vendorProductCategoryList.add(vendorProductCategory);
        }

        List<String> countryNames = operation.getVendorCountries().stream().map(VendorCountryName::toString).toList();
        List<String> productCategoryNames = operation.getVendorProductCategories().stream().map(VendorProductCategoryName::toString).toList();

        List<String> allStringList = new ArrayList<>(countryNames.size() + productCategoryNames.size());
        allStringList.addAll(countryNames);
        allStringList.addAll(productCategoryNames);

        String[] flatStrings = allStringList.toArray(new String[0]);

        TranslationResponse translationEn, translationRu, translationZh;

        try {
            translationEn = translationRepository.translateToEn(flatStrings);
            translationRu = translationRepository.translateToRu(flatStrings);
            translationZh = translationRepository.translateToZh(flatStrings);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.translationError(operation.getId(), e);
        }

        for (int i = 0; i < allStringList.size(); i++) {
            if (countryNames.size() > i) {
                vendorCountryList.get(i).getName().setTranslations(new HstoreTranslationDto(
                        translationEn.getTranslations()[i].getText(),
                        translationRu.getTranslations()[i].getText(),
                        translationZh.getTranslations()[i].getText()
                ));
            }

            if (countryNames.size() <= i) {
                vendorProductCategoryList.get(i - countryNames.size()).getName().setTranslations(new HstoreTranslationDto(
                        translationEn.getTranslations()[i].getText(),
                        translationRu.getTranslations()[i].getText(),
                        translationZh.getTranslations()[i].getText()
                ));
            }
        }

        user.get().getVendorDetails().getVendorCountries().clear();
        user.get().getVendorDetails().getVendorCountries().addAll(vendorCountryList);

        user.get().getVendorDetails().getVendorProductCategories().clear();
        user.get().getVendorDetails().getVendorProductCategories().addAll(vendorProductCategoryList);

        try {
            userRepository.save(user.get());
            return ForceUpdateVendorById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
        }
    }
}
