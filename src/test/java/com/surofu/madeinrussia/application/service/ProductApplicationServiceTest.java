package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.core.model.category.*;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodCreationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodLastModificationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodName;
import com.surofu.madeinrussia.core.model.product.*;
import com.surofu.madeinrussia.core.repository.*;
import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    final ZonedDateTime TEST_DATE_TIME = ZonedDateTime.now();

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMediaRepository productMediaRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    DeliveryMethodRepository deliveryMethodRepository;

    @Mock
    FileStorageRepository fileStorageRepository;

    @InjectMocks
    ProductApplicationService productApplicationService;

    @BeforeEach
    public void setup() {
    }

    @Test
    void getProductById_WhenProductExistsByValidId_ReturnsSuccessResultWithValidProductDto() {
        // given
        long mockCategoryId = 1L;
        long mockDeliveryMethodId = 1L;
        long mockProductId = 1L;

        Category category = new Category();
        category.setId(mockCategoryId);
        category.setSlug(CategorySlug.of("l1_slug"));
        category.setChildren(Set.of());
        category.setChildrenCount(0L);
        category.setName(CategoryName.of("Test Category"));
        category.setCreationDate(CategoryCreationDate.of(TEST_DATE_TIME));
        category.setLastModificationDate(CategoryLastModificationDate.of(TEST_DATE_TIME));

        DeliveryMethod deliveryMethod = new DeliveryMethod();
        deliveryMethod.setId(mockDeliveryMethodId);
        deliveryMethod.setName(DeliveryMethodName.of("Test DeliveryMethod"));
        deliveryMethod.setCreationDate(DeliveryMethodCreationDate.of(TEST_DATE_TIME));
        deliveryMethod.setLastModificationDate(DeliveryMethodLastModificationDate.of(TEST_DATE_TIME));

        Product mockProduct = getProduct(mockProductId, category, deliveryMethod);

        ProductDto mockProductDto = ProductDto.of(mockProduct);

        doReturn(Optional.of(mockProduct))
                .when(productRepository)
                .getProductById(mockProductId);

        doReturn(List.of())
                .when(productMediaRepository)
                .findAllByProductId(anyLong());

        // when
        GetProductById getProductByIdOperation = GetProductById.of(mockCategoryId);
        GetProductById.Result getProductByIdResult = productApplicationService.getProductById(getProductByIdOperation);

        // then
        assertNotNull(getProductByIdResult);
        assertInstanceOf(GetProductById.Result.class, getProductByIdResult);
        assertInstanceOf(GetProductById.Result.Success.class, getProductByIdResult);

        GetProductById.Result.Success getProductByIdSuccessResult = (GetProductById.Result.Success) getProductByIdResult;
        ProductDto resultProductDto = getProductByIdSuccessResult.getProductDto();

        assertNotNull(resultProductDto);
        assertEquals(mockProductDto.getId(), resultProductDto.getId());
        assertEquals(mockProductDto.getCategory().getId(), resultProductDto.getCategory().getId());
        assertEquals(mockProductDto.getDeliveryMethods().size(), resultProductDto.getDeliveryMethods().size());
        assertEquals(mockProductDto.getTitle(), resultProductDto.getTitle());
        assertEquals(mockProductDto.getCreationDate(), resultProductDto.getCreationDate());
        assertEquals(mockProductDto.getLastModificationDate(), resultProductDto.getLastModificationDate());

        verify(productRepository, times(1)).getProductById(mockCategoryId);
    }

    @Test
    void getProductById_WhenProductDoesNotExistById_ReturnsNotFoundResult() {
        // given
        doReturn(Optional.empty()).when(productRepository).getProductById(anyLong());

        // when
        long productId = 1L;
        GetProductById getProductByIdOperation = GetProductById.of(productId);
        GetProductById.Result getProductByIdResult = productApplicationService.getProductById(getProductByIdOperation);

        // then
        assertNotNull(getProductByIdResult);
        assertInstanceOf(GetProductById.Result.class, getProductByIdResult);
        assertInstanceOf(GetProductById.Result.NotFound.class, getProductByIdResult);

        GetProductById.Result.NotFound getProductByIdNotFoundResult = (GetProductById.Result.NotFound) getProductByIdResult;
        Long resultProductId = getProductByIdNotFoundResult.getProductId();

        assertNotNull(resultProductId);
        assertEquals(productId, resultProductId);

        verify(productRepository, times(1)).getProductById(productId);
    }

    private Product getProduct(long mockProductId, Category category, DeliveryMethod deliveryMethod) {
        Product mockProduct = new Product();
        mockProduct.setId(mockProductId);
        mockProduct.setArticleCode(ProductArticleCode.of("AbCd-1234"));
        mockProduct.setDescription(ProductDescription.of("Главное описание", "Вторичное описание"));
        mockProduct.setMedia(Set.of());
        mockProduct.setReviews(Set.of());
        mockProduct.setCharacteristics(Set.of());
        mockProduct.setCategory(category);
        mockProduct.setDeliveryMethods(Set.of(deliveryMethod));
        mockProduct.setTitle(ProductTitle.of(String.format("Product %s", mockProductId)));
        mockProduct.setPreviewImageUrl(ProductPreviewImageUrl.of(String.format("Product %s image url", mockProductId)));
        mockProduct.setPrices(Set.of());
        mockProduct.setDiscountExpirationDate(ProductDiscountExpirationDate.of(ZonedDateTime.now().plusDays(30)));
        mockProduct.setMinimumOrderQuantity(ProductMinimumOrderQuantity.of(5));
        mockProduct.setCreationDate(ProductCreationDate.of(TEST_DATE_TIME));
        mockProduct.setLastModificationDate(ProductLastModificationDate.of(TEST_DATE_TIME));
        return mockProduct;
    }
}