package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.core.model.category.*;
import com.surofu.madeinrussia.core.repository.CategoryRepository;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryApplicationServiceTest {

    final ZonedDateTime TEST_DATE_TIME = ZonedDateTime.now();

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryApplicationService categoryApplicationService;

    @Test
    void getCategories_ReturnsSuccessResultWithNotEmptyCategoryDtoList() {
        // given
        List<Category> mockCategories = new ArrayList<>();
        List<CategoryDto> mockCategoryDtos = new ArrayList<>();

        int categoryCount = 10;
        IntStream.range(0, categoryCount).forEach(i -> {
            Category mockCategory = new Category();
            mockCategory.setId((long) i);
            mockCategory.setSlug(CategorySlug.of("l1_slug_" + i));
            mockCategory.setChildren(Set.of());
            mockCategory.setChildrenCount(0L);
            mockCategory.setName(CategoryName.of(String.format("Cat %s", i)));
            mockCategory.setCreationDate(CategoryCreationDate.of(TEST_DATE_TIME));
            mockCategory.setLastModificationDate(CategoryLastModificationDate.of(TEST_DATE_TIME));

            mockCategories.add(mockCategory);
            mockCategoryDtos.add(CategoryDto.of(mockCategory));
        });

        doReturn(mockCategories).when(categoryRepository).getCategories();

        // when
        GetCategories.Result getCategoriesResult = categoryApplicationService.getCategories(GetCategories.of(Locale.getDefault()));

        // then
        assertNotNull(getCategoriesResult);
        assertInstanceOf(GetCategories.Result.class, getCategoriesResult);
        assertInstanceOf(GetCategories.Result.Success.class, getCategoriesResult);

        GetCategories.Result.Success getCategoriesResultSuccess = (GetCategories.Result.Success) getCategoriesResult;
        assertFalse(getCategoriesResultSuccess.getCategoryDtos().isEmpty());

        List<CategoryDto> resultCategoryDtos = getCategoriesResultSuccess.getCategoryDtos();
        assertNotNull(resultCategoryDtos);
        assertEquals(mockCategoryDtos, resultCategoryDtos);
        assertEquals(mockCategories.size(), resultCategoryDtos.size());

        IntStream.range(0, categoryCount).forEach(i -> {
            assertNotNull(resultCategoryDtos.get(i));
            assertEquals(mockCategoryDtos.get(i).getId(), resultCategoryDtos.get(i).getId());
            assertEquals(mockCategoryDtos.get(i).getName(), resultCategoryDtos.get(i).getName());
            assertEquals(mockCategoryDtos.get(i).getCreationDate(), resultCategoryDtos.get(i).getCreationDate());
            assertEquals(mockCategoryDtos.get(i).getLastModificationDate(), resultCategoryDtos.get(i).getLastModificationDate());
        });

        verify(categoryRepository, times(1)).getCategories();
    }

    @Test
    void getCategoryById_WhenCategoryExistsByValidId_ReturnsSuccessResultWithValidCategoryDto() {
        // given
        long mockCategoryId = 1L;

        Category mockCategory = new Category(
                mockCategoryId,
                null,
                Set.of(),
                0L,
                CategorySlug.of("l1_slug"),
                CategoryName.of("Cat 1"),
                CategoryImageUrl.of("https://example.com"),
                CategoryCreationDate.of(TEST_DATE_TIME),
                CategoryLastModificationDate.of(TEST_DATE_TIME)
        );
        CategoryDto mockCategoryDto = CategoryDto.of(mockCategory);

        doReturn(Optional.of(mockCategory)).when(categoryRepository).getCategoryById(mockCategoryId);

        // when
        GetCategoryById getCategoryByIdOperation = GetCategoryById.of(mockCategoryId, Locale.getDefault());
        GetCategoryById.Result getCategoryByIdResult = categoryApplicationService.getCategoryById(getCategoryByIdOperation);

        // then
        assertNotNull(getCategoryByIdResult);
        assertInstanceOf(GetCategoryById.Result.class, getCategoryByIdResult);
        assertInstanceOf(GetCategoryById.Result.Success.class, getCategoryByIdResult);

        GetCategoryById.Result.Success getCategoryByIdResultSuccess = (GetCategoryById.Result.Success) getCategoryByIdResult;
        assertNotNull(getCategoryByIdResultSuccess.getCategoryDto());

        CategoryDto resultCategoryDto = getCategoryByIdResultSuccess.getCategoryDto();
        assertEquals(mockCategoryDto.getId(), resultCategoryDto.getId());
        assertEquals(mockCategoryDto.getSlug(), resultCategoryDto.getSlug());
        assertEquals(mockCategoryDto.getName(), resultCategoryDto.getName());
        assertEquals(mockCategoryDto.getCreationDate(), resultCategoryDto.getCreationDate());
        assertEquals(mockCategoryDto.getLastModificationDate(), resultCategoryDto.getLastModificationDate());

        verify(categoryRepository, times(1)).getCategoryById(mockCategoryId);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExistsById_ReturnsNotFoundResult() {
        // given
        doReturn(Optional.empty()).when(categoryRepository).getCategoryById(anyLong());

        // when
        long categoryId = 1L;
        GetCategoryById getCategoryByIdOperation = GetCategoryById.of(categoryId, Locale.getDefault());
        GetCategoryById.Result getCategoryByIdResult = categoryApplicationService.getCategoryById(getCategoryByIdOperation);

        assertNotNull(getCategoryByIdResult);
        assertInstanceOf(GetCategoryById.Result.class, getCategoryByIdResult);
        assertInstanceOf(GetCategoryById.Result.NotFound.class, getCategoryByIdResult);

        GetCategoryById.Result.NotFound getCategoryByIdResultNotFound = (GetCategoryById.Result.NotFound) getCategoryByIdResult;

        assertNotNull(getCategoryByIdResultNotFound.getCategoryId());
        assertEquals(categoryId, getCategoryByIdResultNotFound.getCategoryId());

        verify(categoryRepository, times(1)).getCategoryById(categoryId);
    }
}