package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.core.model.category.CategoryCreationDate;
import com.surofu.madeinrussia.core.model.category.CategoryLastModificationDate;
import com.surofu.madeinrussia.core.model.category.CategoryName;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodCreationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodLastModificationDate;
import com.surofu.madeinrussia.core.model.deliveryMethod.DeliveryMethodName;
import com.surofu.madeinrussia.core.model.product.*;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import com.surofu.madeinrussia.core.service.product.operation.GetProductById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    final BigDecimal MIN_PRICE = BigDecimal.ZERO;
    final BigDecimal MAX_PRICE = BigDecimal.TEN.multiply(BigDecimal.valueOf(2));

    final ZonedDateTime TEST_DATE_TIME = ZonedDateTime.now();

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductApplicationService productApplicationService;

    @Test
    void getProducts_ReturnsSuccessResultWithNotEmptyProductDtoList() {
        // given
        int mockProductCount = 20;
        int mockCategoryCount = 5;
        int mockDeliveryMethodCount = 2;

        List<Category> mockCategories = new ArrayList<>();

        IntStream.range(0, mockCategoryCount).forEach(i -> {
            Category mockCategory = new Category();
            mockCategory.setId((long) i);
            mockCategory.setName(CategoryName.of(String.format("Category %s", i)));
            mockCategory.setCreationDate(CategoryCreationDate.of(TEST_DATE_TIME));
            mockCategory.setLastModificationDate(CategoryLastModificationDate.of(TEST_DATE_TIME));

            mockCategories.add(mockCategory);
        });

        List<DeliveryMethod> mockDeliveryMethods = new ArrayList<>();

        IntStream.range(0, mockDeliveryMethodCount).forEach(i -> {
            DeliveryMethod mockDeliveryMethod = new DeliveryMethod();
            mockDeliveryMethod.setId((long) i);
            mockDeliveryMethod.setName(DeliveryMethodName.of(String.format("Delivery Method %s", i)));
            mockDeliveryMethod.setCreationDate(DeliveryMethodCreationDate.of(TEST_DATE_TIME));
            mockDeliveryMethod.setLastModificationDate(DeliveryMethodLastModificationDate.of(TEST_DATE_TIME));

            mockDeliveryMethods.add(mockDeliveryMethod);
        });

        List<Product> mockProducts = new ArrayList<>();
        List<ProductDto> mockProductDtos = new ArrayList<>();

        Random random = new Random();

        IntStream.range(0, mockProductCount).forEach(i -> {
            int randomCategoryIndex = random.nextInt(mockCategoryCount);
            Category randomMockCategory = mockCategories.get(randomCategoryIndex);

            int randomDeliveryMethodIndex = random.nextInt(mockDeliveryMethodCount);
            DeliveryMethod randomMockDeliveryMethod = mockDeliveryMethods.get(randomDeliveryMethodIndex);

            Product mockProduct = getProduct((long) i+1, randomMockCategory, randomMockDeliveryMethod);

            if (random.nextBoolean()) {
                Set<DeliveryMethod> deliveryMethodSet = new HashSet<>(mockDeliveryMethods);
                mockProduct.setDeliveryMethods(deliveryMethodSet);
            }

            mockProduct.setTitle(ProductTitle.of(String.format("Product %s", i)));
            mockProduct.setPrices(Set.of());
            mockProduct.setPreviewImageUrl(ProductPreviewImageUrl.of(String.format("Product %s image url", i)));
            mockProduct.setCreationDate(ProductCreationDate.of(TEST_DATE_TIME));
            mockProduct.setLastModificationDate(ProductLastModificationDate.of(TEST_DATE_TIME));

            mockProducts.add(mockProduct);
            mockProductDtos.add(ProductDto.of(mockProduct));
        });

        Pageable productPageable = PageRequest.of(0, 10);
        ArgumentCaptor<Pageable> productPageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        Page<Product> productPage = new PageImpl<>(mockProducts, productPageable, mockProductCount);

        ArgumentCaptor<Specification<Product>> specificationArgumentCaptor = ArgumentCaptor.forClass(Specification.class);

        doReturn(productPage)
                .when(productRepository).getProductPage(
                        specificationArgumentCaptor.capture(),
                        productPageableArgumentCaptor.capture()
                );

        // when
        GetProductPage operation = GetProductPage.of(
                0,
                10,
                "",
                List.of(),
                List.of()
        );

        GetProductPage.Result getProductsResult = productApplicationService.getProductPage(operation);

        // then
        assertNotNull(getProductsResult);
        assertInstanceOf(GetProductPage.Result.class, getProductsResult);
        assertInstanceOf(GetProductPage.Result.Success.class, getProductsResult);

        GetProductPage.Result.Success getProductsSuccessResult = (GetProductPage.Result.Success) getProductsResult;
        Page<ProductDto> resultProductDtoPage = getProductsSuccessResult.getProductDtoPage();

        assertNotNull(resultProductDtoPage);
        assertNotNull(resultProductDtoPage.getContent());
        assertFalse(resultProductDtoPage.getContent().isEmpty());
        assertEquals(mockProductCount, resultProductDtoPage.getTotalElements());
        assertEquals(mockProductCount / operation.getSize(), resultProductDtoPage.getTotalPages());

        IntStream.range(0, mockProductCount).forEach(i -> {
            ProductDto resultProductDto = resultProductDtoPage.getContent().get(i);
            assertNotNull(resultProductDto);
            assertEquals(mockProductDtos.get(i).getId(), resultProductDto.getId());
            assertEquals(mockProductDtos.get(i).getCategory().getId(), resultProductDto.getCategory().getId());
            assertEquals(mockProductDtos.get(i).getDeliveryMethods().size(), resultProductDto.getDeliveryMethods().size());
            assertEquals(mockProductDtos.get(i).getTitle(), resultProductDto.getTitle());
            assertEquals(mockProductDtos.get(i).getPreviewImageUrl(), resultProductDto.getPreviewImageUrl());
            assertEquals(mockProductDtos.get(i).getCreationDate(), resultProductDto.getCreationDate());
            assertEquals(mockProductDtos.get(i).getLastModificationDate(), resultProductDto.getLastModificationDate());
        });

        verify(productRepository, times(1)).getProductPage(
                specificationArgumentCaptor.capture(),
                productPageableArgumentCaptor.capture()
        );
    }

    @Test
    void getProductById_WhenProductExistsByValidId_ReturnsSuccessResultWithValidProductDto() {
        // given
        long mockCategoryId = 1L;
        long mockDeliveryMethodId = 1L;
        long mockProductId = 1L;

        Category category = new Category();
        category.setId(mockCategoryId);
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
        mockProduct.setDescription(ProductDescription.of("Главное описание", "Вторичное описание", "Кратное описание", "Важное описание"));
        mockProduct.setMedia(Set.of());
        mockProduct.setReviews(Set.of());
        mockProduct.setCharacteristics(Set.of());
        mockProduct.setCategory(category);
        mockProduct.setDeliveryMethods(Set.of(deliveryMethod));
        mockProduct.setTitle(ProductTitle.of(String.format("Product %s", mockProductId)));
        mockProduct.setPreviewImageUrl(ProductPreviewImageUrl.of(String.format("Product %s image url", mockProductId)));
        mockProduct.setPrices(Set.of());
        mockProduct.setCreationDate(ProductCreationDate.of(TEST_DATE_TIME));
        mockProduct.setLastModificationDate(ProductLastModificationDate.of(TEST_DATE_TIME));
        return mockProduct;
    }
}