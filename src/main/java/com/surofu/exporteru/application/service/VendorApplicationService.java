package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorCountryDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.dto.vendor.VendorFaqDto;
import com.surofu.exporteru.application.dto.vendor.VendorMediaDto;
import com.surofu.exporteru.application.dto.vendor.VendorProductCategoryDto;
import com.surofu.exporteru.application.dto.vendor.VendorReviewPageDto;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;
import com.surofu.exporteru.core.repository.ProductReviewRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.VendorCountryRepository;
import com.surofu.exporteru.core.repository.VendorDetailsRepository;
import com.surofu.exporteru.core.repository.VendorEmailRepository;
import com.surofu.exporteru.core.repository.VendorFaqRepository;
import com.surofu.exporteru.core.repository.VendorPhoneNumberRepository;
import com.surofu.exporteru.core.repository.VendorProductCategoryRepository;
import com.surofu.exporteru.core.repository.VendorSiteRepository;
import com.surofu.exporteru.core.repository.VendorViewRepository;
import com.surofu.exporteru.core.repository.specification.ProductReviewSpecifications;
import com.surofu.exporteru.core.service.mail.MailService;
import com.surofu.exporteru.core.service.vendor.VendorService;
import com.surofu.exporteru.core.service.vendor.operation.CreateVendorFaq;
import com.surofu.exporteru.core.service.vendor.operation.DeleteVendorFaqById;
import com.surofu.exporteru.core.service.vendor.operation.ForceUpdateVendorById;
import com.surofu.exporteru.core.service.vendor.operation.GetVendorById;
import com.surofu.exporteru.core.service.vendor.operation.GetVendorReviewPageById;
import com.surofu.exporteru.core.service.vendor.operation.SendCallRequestMail;
import com.surofu.exporteru.core.service.vendor.operation.UpdateVendorFaq;
import com.surofu.exporteru.infrastructure.persistence.user.UserView;
import com.surofu.exporteru.infrastructure.persistence.vendor.VendorDetailsView;
import com.surofu.exporteru.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.exporteru.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.exporteru.infrastructure.persistence.vendor.media.JpaVendorMediaRepository;
import com.surofu.exporteru.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    if (operation.getSecurityUser() != null) {
      if (!vendorViewRepository.existsByUserIdAndVendorDetailsId(
          operation.getSecurityUser().getUser().getId(), vendorDto.getVendorDetails().getId())) {
        User user =
            userRepository.getUserById(operation.getSecurityUser().getUser().getId()).orElseThrow();
        VendorDetails vendorDetails =
            vendorDetailsRepository.getById(vendorDto.getVendorDetails().getId()).orElseThrow();

        VendorView vendorView = new VendorView();
        vendorView.setUser(user);
        vendorView.setVendorDetails(vendorDetails);

        try {
          vendorViewRepository.save(vendorView);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }

    Long viewsCount = vendorViewRepository.getCountByVendorDetailsId(vendorDetailsView.getId());
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

    return GetVendorById.Result.success(vendorDto);
  }

  @Override
  @Transactional
  public GetVendorReviewPageById.Result getVendorReviewPageById(GetVendorReviewPageById operation) {
    if (userRepository.existsVendorOrAdminById(operation.getVendorId())) {
      return GetVendorReviewPageById.Result.vendorNotFound(operation.getVendorId());
    }

    Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize());

    Specification<ProductReview> specification = Specification
        .where(ProductReviewSpecifications.byProductUserId(operation.getVendorId()))
        .and(ProductReviewSpecifications.ratingBetween(operation.getMinRating(),
            operation.getMaxRating()))
        .and(ProductReviewSpecifications.approveStatusIn(List.of(ApproveStatus.APPROVED)));

    Page<ProductReview> productReviewPage =
        productReviewRepository.getPage(specification, pageable);

    if (productReviewPage.getContent().isEmpty()) {
      VendorReviewPageDto vendorReviewPageDto =
          VendorReviewPageDto.of(productReviewPage, 0.0, operation.getLocale());
      return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
    }

    List<Long> reviewIds = productReviewPage.getContent().stream()
        .map(ProductReview::getId)
        .toList();

    List<ProductReview> reviewsWithMedia = productReviewRepository.getByIdInWithMedia(reviewIds);

    Map<Long, ProductReview> reviewMap = reviewsWithMedia.stream()
        .collect(Collectors.toMap(ProductReview::getId, Function.identity()));

    Page<ProductReview> productReviewPageWithMedia = productReviewPage
        .map(p -> {
          if (reviewMap.containsKey(p.getId())) {
            p.setMedia(reviewMap.get(p.getId()).getMedia());
          }
          return p;
        });

    Double ratingAverage =
        productReviewRepository.findAverageRatingByVendorId(operation.getVendorId());
    VendorReviewPageDto vendorReviewPageDto =
        VendorReviewPageDto.of(productReviewPageWithMedia, ratingAverage, operation.getLocale());
    return GetVendorReviewPageById.Result.success(vendorReviewPageDto);
  }

  @Override
  @Transactional
  public CreateVendorFaq.Result createVendorFaq(CreateVendorFaq operation) {
    VendorDetails vendorDetails = operation.getSecurityUser().getUser().getVendorDetails();

    VendorFaq faq = new VendorFaq();
    faq.setVendorDetails(vendorDetails);
    faq.setQuestion(operation.getQuestion());
    faq.setAnswer(operation.getAnswer());

    try {
      faq.getQuestion().setTranslations(translationRepository.expand(operation.getQuestion().toString()));
      faq.getAnswer().setTranslations(translationRepository.expand(operation.getAnswer().toString()));
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return CreateVendorFaq.Result.translationError(e);
    }

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

    Optional<VendorFaq> faqOptional =
        vendorFaqRepository.getByIdAndVendorDetailsId(operation.getId(), vendorDetails.getId());

    if (faqOptional.isEmpty()) {
      return UpdateVendorFaq.Result.notFound(operation.getId());
    }

    VendorFaq faq = faqOptional.get();
    faq.setQuestion(operation.getQuestion());
    faq.setAnswer(operation.getAnswer());

    try {
      faq.getQuestion().setTranslations(translationRepository.expand(operation.getQuestion().toString()));
      faq.getAnswer().setTranslations(translationRepository.expand(operation.getAnswer().toString()));
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateVendorFaq.Result.translationError(operation.getId(), e);
    }

    VendorFaq savedFaq;

    try {
      savedFaq = vendorFaqRepository.save(faq);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UpdateVendorFaq.Result.saveError(operation.getId(), e);
    }

    return UpdateVendorFaq.Result.success(VendorFaqDto.of(savedFaq));
  }

  // TODO: forceUpdateVendorById. Make dynamic translation
  @Override
  @Transactional
  public ForceUpdateVendorById.Result forceUpdateVendorById(ForceUpdateVendorById operation) {
    try {
      // Validation
      User user = userRepository.getUserById(operation.getId())
          .orElse(null);

      if (user == null) {
        return ForceUpdateVendorById.Result.notFound(operation.getId());
      }

      // Validate unique fields
      ForceUpdateVendorById.Result validationResult = validateUniqueFields(operation, user);
      if (validationResult != null) {
        return validationResult;
      }

      if (user.getVendorDetails() != null &&
          !Objects.equals(user.getVendorDetails().getInn(), operation.getInn())) {
        if (vendorDetailsRepository.existsByInnAndNotVendorDetailsId(
            operation.getInn(),
            user.getVendorDetails().getId()
        )) {
          return ForceUpdateVendorById.Result.innAlreadyExists(operation.getInn());
        }
      }

      // Get or create vendor details
      VendorDetails vendorDetails = getOrCreateVendorDetails(user);

      // Update user and vendor details
      updateUserAndVendorDetails(operation, user, vendorDetails);

      // Update collections
      updateVendorCollections(operation, vendorDetails);

      // Save changes
      vendorDetailsRepository.save(vendorDetails);
      userRepository.save(user);
      return ForceUpdateVendorById.Result.success(operation.getId());

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ForceUpdateVendorById.Result.saveError(operation.getId(), e);
    }
  }

// Вспомогательные методы

  private ForceUpdateVendorById.Result validateUniqueFields(ForceUpdateVendorById operation,
                                                            User user) {
    if (!user.getEmail().equals(operation.getEmail()) &&
        userRepository.existsUserByEmail(operation.getEmail())) {
      return ForceUpdateVendorById.Result.emailAlreadyExists(operation.getEmail());
    }

    if (!user.getLogin().equals(operation.getLogin()) &&
        userRepository.existsUserByLogin(operation.getLogin())) {
      return ForceUpdateVendorById.Result.loginAlreadyExists(operation.getLogin());
    }

    if (!user.getPhoneNumber().equals(operation.getPhoneNumber()) &&
        userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
      return ForceUpdateVendorById.Result.phoneNumberAlreadyExists(operation.getPhoneNumber());
    }

    return null;
  }

  private VendorDetails getOrCreateVendorDetails(User user) {
    VendorDetails vendorDetails = user.getVendorDetails();

    if (vendorDetails == null) {
      vendorDetails = new VendorDetails();
      vendorDetails.setUser(user);
      user.setVendorDetails(vendorDetails);
    }

    return vendorDetails;
  }

  private void updateUserAndVendorDetails(
      ForceUpdateVendorById operation, User user, VendorDetails vendorDetails
  ) {
    user.setEmail(operation.getEmail());
    user.setLogin(operation.getLogin());
    user.setPhoneNumber(operation.getPhoneNumber());
    vendorDetails.setInn(operation.getInn());
    vendorDetails.setDescription(operation.getDescription());
    updateDescriptionTranslations(operation, vendorDetails);
  }

  private void updateDescriptionTranslations(ForceUpdateVendorById operation,
                                             VendorDetails vendorDetails) {
    String descriptionText = StringUtils.trimToNull(operation.getDescription().toString());
    if (descriptionText != null) {
      try {
        vendorDetails.getDescription().setTranslations(translationRepository.expand(descriptionText));
      } catch (Exception e) {
        throw new RuntimeException("Translation error for description", e);
      }
    }
  }

  private void updateVendorCollections(ForceUpdateVendorById operation,
                                       VendorDetails vendorDetails) throws Exception {
    updatePhoneNumbers(operation, vendorDetails);
    updateEmails(operation, vendorDetails);
    updateSites(operation, vendorDetails);
    updateCountriesAndCategories(operation, vendorDetails);
  }

  private void updatePhoneNumbers(ForceUpdateVendorById operation, VendorDetails vendorDetails) {
    updateCollection(
        operation.getPhoneNumbers(),
        vendorPhoneNumberRepository::getAllByVendorDetailsId,
        vendorPhoneNumberRepository::deleteAll,
        (number) -> {
          VendorPhoneNumber phoneNumber = new VendorPhoneNumber();
          phoneNumber.setVendorDetails(vendorDetails);
          phoneNumber.setPhoneNumber(number);
          return phoneNumber;
        },
        vendorPhoneNumberRepository::saveAll,
        vendorDetails
    );
  }

  private void updateEmails(ForceUpdateVendorById operation, VendorDetails vendorDetails) {
    updateCollection(
        operation.getEmails(),
        vendorEmailRepository::getAllByVendorDetailsId,
        vendorEmailRepository::deleteAll,
        (email) -> {
          VendorEmail vendorEmail = new VendorEmail();
          vendorEmail.setVendorDetails(vendorDetails);
          vendorEmail.setEmail(email);
          return vendorEmail;
        },
        vendorEmailRepository::saveAll,
        vendorDetails
    );
  }

  private void updateSites(ForceUpdateVendorById operation, VendorDetails vendorDetails) {
    updateCollection(
        operation.getSites(),
        vendorSiteRepository::getAllByVendorDetailsId,
        vendorSiteRepository::deleteAll,
        (url) -> {
          VendorSite vendorSite = new VendorSite();
          vendorSite.setVendorDetails(vendorDetails);
          vendorSite.setUrl(url);
          return vendorSite;
        },
        vendorSiteRepository::saveAll,
        vendorDetails
    );
  }

  private <T, E> void updateCollection(
      List<T> newItems,
      Function<Long, List<E>> getExistingItems,
      Consumer<List<E>> deleteItems,
      Function<T, E> createEntity,
      Consumer<List<E>> saveItems,
      VendorDetails vendorDetails) {

    if (newItems == null) {
      return;
    }

    List<E> oldItems = getExistingItems.apply(vendorDetails.getId());
    deleteItems.accept(oldItems);

    if (!newItems.isEmpty()) {
      List<E> newEntities = newItems.stream()
          .map(createEntity)
          .collect(Collectors.toList());
      saveItems.accept(newEntities);
    }
  }

  private void updateCountriesAndCategories(ForceUpdateVendorById operation,
                                            VendorDetails vendorDetails) {
    // Prepare new entities
    List<VendorCountry> newCountries = operation.getVendorCountries().stream()
        .map(name -> {
          VendorCountry vendorCountry = new VendorCountry();
          vendorCountry.setVendorDetails(vendorDetails);
          vendorCountry.setName(name);
          vendorCountry.getName().setTranslations(translationRepository.expand(name.getTranslations()));
          return vendorCountry;
        })
        .collect(Collectors.toList());

    List<VendorProductCategory> newCategories = operation.getVendorProductCategories().stream()
        .map(name -> {
          VendorProductCategory vendorProductCategory = new VendorProductCategory();
          vendorProductCategory.setVendorDetails(vendorDetails);
          vendorProductCategory.setName(name);
          vendorProductCategory.getName().setTranslations(translationRepository.expand(name.getTranslations()));
          return vendorProductCategory;
        })
        .collect(Collectors.toList());

    // Update in database
    updateCountriesAndCategoriesInDatabase(vendorDetails, newCountries, newCategories);
  }

  private void updateCountriesAndCategoriesInDatabase(
      VendorDetails vendorDetails,
      List<VendorCountry> newCountries,
      List<VendorProductCategory> newCategories) {

    // Delete old records
    List<VendorCountry> oldCountries =
        vendorCountryRepository.getAllByVendorDetailsId(vendorDetails.getId());
    List<VendorProductCategory> oldCategories =
        vendorProductCategoryRepository.getAllByVendorDetailsId(vendorDetails.getId());

    vendorCountryRepository.deleteAll(oldCountries);
    vendorProductCategoryRepository.deleteAll(oldCategories);

    // Save new records
    if (!newCountries.isEmpty()) {
      vendorCountryRepository.saveAll(newCountries);
    }
    if (!newCategories.isEmpty()) {
      vendorProductCategoryRepository.saveAll(newCategories);
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
    String senderPhoneNumber =
        sender.getPhoneNumber() != null ? sender.getPhoneNumber().toString() : "-";

    try {
      mailService.sendPhoneRequestMail(email, senderFirstName, senderEmail, senderPhoneNumber);
    } catch (IOException e) {
      return SendCallRequestMail.Result.sendMailError(e);
    }

    return SendCallRequestMail.Result.success();
  }
}
