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
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSite;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.repository.specification.ProductReviewSpecifications;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.*;
import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final VendorPhoneNumberRepository vendorPhoneNumberRepository;
    private final VendorEmailRepository vendorEmailRepository;
    private final VendorSiteRepository vendorSiteRepository;
    private final VendorCountryRepository vendorCountryRepository;
    private final VendorProductCategoryRepository vendorProductCategoryRepository;
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
        if (userRepository.existsVendorOrAdminById(operation.getVendorId())) {
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
        User user = operation.getSecurityUser().getUser();

        if (!vendorFaqRepository.existsByIdAndVendorId(operation.getFaqId(), user.getId())) {
            return DeleteVendorFaqById.Result.notFound(operation.getFaqId());
        }

        VendorDetails vendorDetails = user.getVendorDetails();

        if (vendorDetails != null) {
            vendorDetails.getFaq().removeIf(faq -> faq.getId().equals(operation.getFaqId()));

            try {
                userRepository.save(user);
            } catch (Exception e) {
                return DeleteVendorFaqById.Result.saveError(e);
            }
        }

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

        Optional<User> userOptional = userRepository.getUserById(operation.getId());

        if (userOptional.isEmpty()) {
            return ForceUpdateVendorById.Result.notFound(operation.getId());
        }

        User user = userOptional.get();
        VendorDetails vendorDetails = user.getVendorDetails();

        if (vendorDetails == null) {
            VendorDetails newVendorDetails = new VendorDetails();
            newVendorDetails.setUser(user);
            user.setVendorDetails(newVendorDetails);

            vendorDetails = vendorDetailsRepository.save(newVendorDetails);
        }

        if (!user.getEmail().equals(operation.getEmail()) && userRepository.existsUserByEmail(operation.getEmail())) {
            return ForceUpdateVendorById.Result.emailAlreadyExists(operation.getEmail());
        }

        if (!user.getLogin().equals(operation.getLogin()) && userRepository.existsUserByLogin(operation.getLogin())) {
            return ForceUpdateVendorById.Result.loginAlreadyExists(operation.getLogin());
        }

        if (!user.getPhoneNumber().equals(operation.getPhoneNumber()) && userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
            return ForceUpdateVendorById.Result.phoneNumberAlreadyExists(operation.getPhoneNumber());
        }

        VendorDetailsInn inn = Objects.requireNonNullElse(vendorDetails.getInn(), operation.getInn());
        if (!inn.equals(operation.getInn())) {
            if (vendorDetailsRepository.existsByInnAndNotVendorDetailsId(operation.getInn(), vendorDetails.getId())) {
                return ForceUpdateVendorById.Result.innAlreadyExists(operation.getInn());
            }
        }

        // Setting
        user.setEmail(operation.getEmail());
        user.setLogin(operation.getLogin());
        user.setPhoneNumber(operation.getPhoneNumber());

        vendorDetails.setUser(user);
        user.setVendorDetails(vendorDetails);

        vendorDetails.setInn(operation.getInn());
        vendorDetails.setDescription(operation.getDescription());

        if (StringUtils.trimToNull(operation.getDescription().toString()) != null) {
            HstoreTranslationDto descriptionTranslation;

            try {
                descriptionTranslation = translationRepository.expand(operation.getDescription().toString());
            } catch (Exception e) {
                return ForceUpdateVendorById.Result.translationError(operation.getId(), e);
            }

            vendorDetails.getDescription().setTranslations(descriptionTranslation);
        }

        Set<VendorPhoneNumber> vendorPhoneNumberSet = new HashSet<>();

        for (VendorPhoneNumberPhoneNumber number : operation.getPhoneNumbers()) {
            VendorPhoneNumber phoneNumber = new VendorPhoneNumber();
            phoneNumber.setVendorDetails(vendorDetails);
            phoneNumber.setPhoneNumber(number);
            vendorPhoneNumberSet.add(phoneNumber);
        }

        List<VendorPhoneNumber> oldPhoneNumbers = vendorPhoneNumberRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
            vendorPhoneNumberRepository.deleteAll(oldPhoneNumbers);
            vendorPhoneNumberRepository.saveAll(vendorPhoneNumberSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
        }

        Set<VendorEmail> vendorEmailSet = new HashSet<>();

        for (VendorEmailEmail email : operation.getEmails()) {
            VendorEmail vendorEmail = new VendorEmail();
            vendorEmail.setVendorDetails(vendorDetails);
            vendorEmail.setEmail(email);
            vendorEmailSet.add(vendorEmail);
        }

        List<VendorEmail> oldVendorEmails = vendorEmailRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
            vendorEmailRepository.deleteAll(oldVendorEmails);
            vendorEmailRepository.saveAll(vendorEmailSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
        }

        Set<VendorSite> vendorSiteSet = new HashSet<>();

        for (VendorSiteUrl url : operation.getSites()) {
            VendorSite vendorSite = new VendorSite();
            vendorSite.setVendorDetails(vendorDetails);
            vendorSite.setUrl(url);
            vendorSiteSet.add(vendorSite);
        }

        List<VendorSite> oldVendorSites = vendorSiteRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
            vendorSiteRepository.deleteAll(oldVendorSites);
            vendorSiteRepository.saveAll(vendorSiteSet);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
        }

        List<VendorCountry> vendorCountryList = new ArrayList<>(operation.getVendorCountries().size());

        for (VendorCountryName name : operation.getVendorCountries()) {
            VendorCountry vendorCountry = new VendorCountry();
            vendorCountry.setVendorDetails(vendorDetails);
            vendorCountry.setName(name);
            vendorCountryList.add(vendorCountry);
        }

        List<VendorProductCategory> vendorProductCategoryList = new ArrayList<>(operation.getVendorProductCategories().size());

        for (VendorProductCategoryName name : operation.getVendorProductCategories()) {
            VendorProductCategory vendorProductCategory = new VendorProductCategory();
            vendorProductCategory.setVendorDetails(vendorDetails);
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

        List<VendorCountry> oldVendorCountries = vendorCountryRepository.getAllByVendorDetailsId(vendorDetails.getId());
        List<VendorProductCategory> oldVendorProductCategoryList = vendorProductCategoryRepository.getAllByVendorDetailsId(vendorDetails.getId());

        try {
            vendorCountryRepository.deleteAll(oldVendorCountries);
            vendorProductCategoryRepository.deleteAll(oldVendorProductCategoryList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.translationError(operation.getId(), e);
        }

        try {
            vendorCountryRepository.saveAll(vendorCountryList);
            vendorProductCategoryRepository.saveAll(vendorProductCategoryList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.translationError(operation.getId(), e);
        }

        try {
            userRepository.save(user);
            return ForceUpdateVendorById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
        }
    }
}
