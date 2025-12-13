package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.category.SaveCategoryCommand;
import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.core.model.category.CategoryDescription;
import com.surofu.exporteru.core.model.category.CategoryLabel;
import com.surofu.exporteru.core.model.category.CategoryMetaDescription;
import com.surofu.exporteru.core.model.category.CategoryName;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.model.category.CategoryTitle;
import com.surofu.exporteru.core.service.category.CategoryService;
import com.surofu.exporteru.core.service.category.operation.CreateCategory;
import com.surofu.exporteru.core.service.category.operation.DeleteCategoryById;
import com.surofu.exporteru.core.service.category.operation.GetCategories;
import com.surofu.exporteru.core.service.category.operation.GetCategoryById;
import com.surofu.exporteru.core.service.category.operation.GetCategoryBySlug;
import com.surofu.exporteru.core.service.category.operation.UpdateCategoryById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories")
@Tag(name = "Categories", description = "API for managing product categories")
public class CategoryRestController {
  private final CategoryService service;

  private final GetCategories.Result.Processor<ResponseEntity<?>> getCategoriesProcessor;
  private final GetCategoryById.Result.Processor<ResponseEntity<?>> getCategoryByIdProcessor;
  private final GetCategoryBySlug.Result.Processor<ResponseEntity<?>> getCategoryBySlugProcessor;
  private final CreateCategory.Result.Processor<ResponseEntity<?>> getCreateCategoryProcessor;
  private final UpdateCategoryById.Result.Processor<ResponseEntity<?>> getUpdateCategoryProcessor;
  private final DeleteCategoryById.Result.Processor<ResponseEntity<?>> getDeleteCategoryProcessor;

  @GetMapping
  @Operation(
      summary = "Get all categories",
      description = "Retrieves a list of all available product categories",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Successfully retrieved categories",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
              )
          )
      }
  )
  public ResponseEntity<?> getCategories() {
    Locale locale = LocaleContextHolder.getLocale();
    GetCategories operation = GetCategories.of(locale);
    return service.getCategories(operation).process(getCategoriesProcessor);
  }

  @GetMapping("/{categoryIdOrSlug}")
  @Operation(
      summary = "Get category by ID or slug",
      description = "Retrieves a single category by its unique identifier or unique slug",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Category found and returned",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CategoryDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Category not found",
              content = @Content
          )
      }
  )
  public ResponseEntity<?> getCategoryByIdOrSlug(
      @Parameter(
          name = "categoryIdOrSlug",
          description = "ID or slug of the category to retrieve",
          required = true,
          examples = {
              @ExampleObject(
                  name = "categoryId",
                  description = "ID of the category to retrieve",
                  value = "1"
              ),
              @ExampleObject(
                  name = "categorySlug",
                  description = "Slug of the category to retrieve",
                  value = "l1_rastenievodstvo-i-zhivotnovodstvo"
              )
          }
      )
      @PathVariable
      String categoryIdOrSlug
  ) {
    Locale locale = LocaleContextHolder.getLocale();
    try {
      long categoryId = Long.parseLong(categoryIdOrSlug);
      GetCategoryById operation = GetCategoryById.of(categoryId, locale);
      return service.getCategoryById(operation).process(getCategoryByIdProcessor);
    } catch (NumberFormatException e) {
      GetCategoryBySlug operation =
          GetCategoryBySlug.of(new CategorySlug(categoryIdOrSlug), locale);
      return service.getCategoryBySlug(operation).process(getCategoryBySlugProcessor);
    }
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @Operation(
      summary = "Create a new category (Admin only)",
      description = "Creates a new product category with optional image upload. Requires ADMIN role.",
      security = @SecurityRequirement(name = "Bearer Authentication"),
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Category created successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseMessageDto.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "Forbidden - insufficient permissions",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "409",
              description = "Conflict - category with this slug already exists",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          )
      }
  )
  public ResponseEntity<?> createCategory(
      @Parameter(description = "Category data in JSON format", required = true)
      @RequestPart("data") SaveCategoryCommand command,

      @Parameter(description = "Optional category image file")
      @RequestPart(value = "image", required = false) MultipartFile imageFile,

      @Parameter(description = "Optional category icon file")
      @RequestPart(value = "icon", required = false) MultipartFile iconFile
  ) {
    CategoryName categoryName = new CategoryName(command.name(), command.nameTranslations());
    CategoryTitle categoryTitle = new CategoryTitle(
        Objects.requireNonNullElse(command.title(), command.name()),
        Objects.requireNonNullElse(command.titleTranslations(), command.nameTranslations())
    );
    CategoryLabel categoryLabel = new CategoryLabel(
        Objects.requireNonNullElse(command.label(), command.name()),
        Objects.requireNonNullElse(command.labelTranslations(), command.nameTranslations())
    );
    CategoryDescription categoryDescription =
        new CategoryDescription(command.description(), command.descriptionTranslations());
    CategoryMetaDescription categoryMetaDescription =
        new CategoryMetaDescription(command.metaDescription(), command.metaDescriptionTranslations());
    CreateCategory operation = CreateCategory.of(
        categoryName,
        categoryTitle,
        categoryLabel,
        categoryDescription,
        categoryMetaDescription,
        new CategorySlug(command.slug()),
        command.parentId(),
        Objects.requireNonNullElse(command.okvedCategories(), new ArrayList<>()),
        imageFile,
        iconFile
    );
    return service.createCategory(operation).process(getCreateCategoryProcessor);
  }

  @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @Operation(
      summary = "Update existing category (Admin only)",
      description = "Updates an existing product category with optional image update. Requires ADMIN role.",
      security = @SecurityRequirement(name = "Bearer Authentication"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Category updated successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseMessageDto.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid input data",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Category not found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "Forbidden - insufficient permissions",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          )
      }
  )
  public ResponseEntity<?> updateCategoryById(
      @Parameter(description = "ID of the category to update", required = true)
      @PathVariable Long id,

      @Parameter(description = "Whether to save/update the image (default: true)")
      @RequestParam(value = "saveImage", defaultValue = "true", required = false) Boolean saveImage,

      @Parameter(description = "Whether to save/update the icon (default: true)")
      @RequestParam(value = "saveIcon", defaultValue = "true", required = false) Boolean saveIcon,

      @Parameter(description = "Updated category data in JSON format", required = true)
      @RequestPart("data") SaveCategoryCommand command,

      @Parameter(description = "New category image file (optional)")
      @RequestPart(value = "image", required = false) MultipartFile imageFile,

      @Parameter(description = "New category icon file (optional)")
      @RequestPart(value = "icon", required = false) MultipartFile iconFile
  ) {
    CategoryName categoryName = new CategoryName(command.name(), command.nameTranslations());
    CategoryTitle categoryTitle = new CategoryTitle(
        Objects.requireNonNullElse(command.title(), command.name()),
        Objects.requireNonNullElse(command.titleTranslations(), command.nameTranslations())
    );
    CategoryLabel categoryLabel = new CategoryLabel(
        Objects.requireNonNullElse(command.label(), command.name()),
        Objects.requireNonNullElse(command.labelTranslations(), command.nameTranslations())
    );
    CategoryDescription categoryDescription =
        new CategoryDescription(command.description(), command.descriptionTranslations());
    CategoryMetaDescription categoryMetaDescription =
        new CategoryMetaDescription(command.metaDescription(), command.metaDescriptionTranslations());
    UpdateCategoryById operation = UpdateCategoryById.of(
        id,
        categoryName,
        categoryTitle,
        categoryLabel,
        categoryDescription,
        categoryMetaDescription,
        new CategorySlug(command.slug()),
        command.parentId(),
        Objects.requireNonNullElse(command.okvedCategories(), new ArrayList<>()),
        imageFile,
        iconFile,
        saveImage,
        saveIcon
    );
    return service.updateCategoryById(operation).process(getUpdateCategoryProcessor);
  }

  @DeleteMapping("{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @Operation(
      summary = "Delete category (Admin only)",
      description = "Deletes a product category by ID. Requires ADMIN role.",
      security = @SecurityRequirement(name = "Bearer Authentication"),
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Category deleted successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Category not found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "401",
              description = "Unauthorized",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "Forbidden - insufficient permissions",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "Internal server error during deletion",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SimpleResponseErrorDto.class))
          )
      }
  )
  public ResponseEntity<?> deleteCategoryById(
      @Parameter(description = "ID of the category to delete", required = true)
      @PathVariable Long id
  ) {
    DeleteCategoryById operation = DeleteCategoryById.of(id);
    return service.deleteCategoryById(operation).process(getDeleteCategoryProcessor);
  }
}
