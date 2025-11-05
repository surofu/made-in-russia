package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import com.surofu.exporteru.application.dto.vendor.*;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.repository.*;
import com.surofu.exporteru.core.repository.specification.ProductReviewSpecifications;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.vendor.VendorService;
import com.surofu.exporteru.core.service.vendor.operation.*;
import com.surofu.exporteru.infrastructure.persistence.translation.TranslationResponse;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import com.surofu.exporteru.infrastructure.persistence.vendor.VendorDetailsView;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.exporteru.infrastructure.persistence.vendor.media.JpaVendorMediaRepository;
import com.surofu.exporteru.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
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
    private final MailService mailService;
    private final JpaVendorMediaRepository vendorMediaRepository;

    @Override
    @Transactional
    public GetVendorById.Result getVendorById(GetVendorById operation) {
        Optional<UserView> userOptional = userRepository.getViewById(operation.getVendorId());

        if (userOptional.isEmpty()) {
            return GetVendorById.Result.notFound(operation.getVendorId());
        }

        UserView userView = userOptional.get();
        UserDto userDto = UserDto.of(userView, operation.getLocale());
        VendorDetailsView vendorDetailsView = userView.getVendorDetails();

        if (vendorDetailsView == null) {
            return GetVendorById.Result.success(userDto);
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

        Long viewsCount = vendorViewRepository.getCountByVendorDetailsId(vendorDetailsView.getId());
        vendorDto.getVendorDetails().setViewsCount(viewsCount);

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
        List<VendorMedia> vendorMediaList = vendorMediaRepository.getAllByVendorDetailsId(userView.getVendorDetails().getId());

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
        List<VendorMediaDto> vendorMediaDtoList = vendorMediaList.stream().map(VendorMediaDto::of).toList();

        vendorDto.getVendorDetails().setCountries(vendorCountryDtoList);
        vendorDto.getVendorDetails().setProductCategories(vendorProductCategoryDtoList);
        vendorDto.getVendorDetails().setFaq(vendorFaqDtoList);
        vendorDto.getVendorDetails().setPhoneNumbers(phoneNumbers);
        vendorDto.getVendorDetails().setEmails(emails);
        vendorDto.getVendorDetails().setSites(sites);
        vendorDto.getVendorDetails().setMedia(vendorMediaDtoList);

        return GetVendorById.Result.success(vendorDto);
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
                VendorReviewPageDto vendorReviewPageDto = VendorReviewPageDto.of(productReviewPage, 0.0);

                return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
            }

            List<Long> reviewIds = productReviewPage.getContent().stream()
                    .map(ProductReview::getId)
                    .toList();

            List<ProductReview> reviewsWithMedia = productReviewRepository.getByIdInWithMedia(reviewIds);

            Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
                    .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

            Page<ProductReview> productReviewPageWithMedia = productReviewPage.map(p -> {
                if (reviewMap.containsKey(p.getId())) {
                    p.setMedia(reviewMap.get(p.getId()).getMedia());
                }
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

        HstoreTranslationDto questionTranslationResult, answerTranslationResult;

        try {
            questionTranslationResult = translationRepository.expand(operation.getQuestion().toString());
            answerTranslationResult = translationRepository.expand(operation.getAnswer().toString());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CreateVendorFaq.Result.translationError(e);
        }

        VendorFaq faq = new VendorFaq();
        faq.setVendorDetails(vendorDetails);
        faq.setQuestion(operation.getQuestion());
        faq.getQuestion().setTranslations(questionTranslationResult);
        faq.setAnswer(operation.getAnswer());
        faq.getAnswer().setTranslations(answerTranslationResult);

        VendorFaq savedFaq;

        try {
            savedFaq = vendorFaqRepository.save(faq);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus();
            return CreateVendorFaq.Result.saveError(e);
        }

        return CreateVendorFaq.Result.success(VendorFaqDto.of(savedFaq));
    }

    @Override
    @Transactional
    public DeleteVendorFaqById.Result deleteVendorFaqById(DeleteVendorFaqById operation) {
        Optional<VendorFaq> vendorFaqOptional = vendorFaqRepository.findById(operation.getFaqId());

        if (vendorFaqOptional.isEmpty()) {
            return DeleteVendorFaqById.Result.notFound(operation.getFaqId());
        }

        try {
            vendorFaqRepository.delete(vendorFaqOptional.get());
        } catch (Exception e) {
            return DeleteVendorFaqById.Result.deleteError(e);
        }

        return DeleteVendorFaqById.Result.success(operation.getFaqId());
    }

    @Override
    @Transactional
    public UpdateVendorFaq.Result updateVendorFaq(UpdateVendorFaq operation) {
        VendorDetails vendorDetails = operation.getSecurityUser().getUser().getVendorDetails();

        if (vendorDetails == null) {
            return UpdateVendorFaq.Result.notFound(operation.getId());
        }

        Optional<VendorFaq> faqOptional = vendorFaqRepository.getByIdAndVendorDetailsId(operation.getId(), vendorDetails.getId());

        if (faqOptional.isEmpty()) {
            return UpdateVendorFaq.Result.notFound(operation.getId());
        }

        HstoreTranslationDto questionTranslationResult, answerTranslationResult;

        try {
            questionTranslationResult = translationRepository.expand(operation.getQuestion().toString());
            answerTranslationResult = translationRepository.expand(operation.getAnswer().toString());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateVendorFaq.Result.translationError(operation.getId(), e);
        }

        VendorFaq faq = faqOptional.get();
        faq.setQuestion(operation.getQuestion());
        faq.getQuestion().setTranslations(questionTranslationResult);
        faq.setAnswer(operation.getAnswer());
        faq.getAnswer().setTranslations(answerTranslationResult);

        VendorFaq savedFaq;

        try {
            savedFaq = vendorFaqRepository.save(faq);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UpdateVendorFaq.Result.saveError(operation.getId(), e);
        }

        return UpdateVendorFaq.Result.success(VendorFaqDto.of(savedFaq));
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

    @Override
    @Transactional(readOnly = true)
    public SendCallRequestMail.Result sendCallRequestMail(SendCallRequestMail operation) {
        Optional<User> vendorOptional = userRepository.getVendorById(operation.getVendorId());

        if (vendorOptional.isEmpty()) {
            return SendCallRequestMail.Result.notFound(operation.getVendorId());
        }

        User vendor = vendorOptional.get();
        String email = vendor.getEmail().toString();

        User sender = operation.getSecurityUser().getUser();
        String senderFirstName = sender.getLogin().toString();
        String senderEmail = sender.getEmail().toString();
        String senderPhoneNumber = sender.getPhoneNumber() != null ? sender.getPhoneNumber().toString() : "-";

        try {
            mailService.sendPhoneRequestMail(email, senderFirstName, senderEmail, senderPhoneNumber);
        } catch (IOException e) {
            return SendCallRequestMail.Result.sendMailError(e);
        }

        return SendCallRequestMail.Result.success();
    }
}
