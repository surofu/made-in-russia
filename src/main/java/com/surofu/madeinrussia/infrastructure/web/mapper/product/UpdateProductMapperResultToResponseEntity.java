package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.UpdateProduct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductMapperResultToResponseEntity
        implements UpdateProduct.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(UpdateProduct.Result.Success result) {
        String message = "Продукт успешно изменен";
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processProductNotFound(UpdateProduct.Result.ProductNotFound result) {
        String message = String.format("Товар с ID '%s' не найден", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processInvalidOwner(UpdateProduct.Result.InvalidOwner result) {
        String message = String.format("Операция недоступна. Вы('%s') не являетесь автором товара с ID '%s'", result.getUserLogin().toString(), result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processErrorSavingFiles(UpdateProduct.Result.ErrorSavingFiles result) {
        String message = "Ошибка сохранения файлов";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processErrorSavingProduct(UpdateProduct.Result.ErrorSavingProduct result) {
        String message = "Ошибка сохранения данных о товаре";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processErrorDeletingFiles(UpdateProduct.Result.ErrorDeletingFiles result) {
        String message = "Ошибка удаления файлов";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processCategoryNotFound(UpdateProduct.Result.CategoryNotFound result) {
        String message = String.format("Категория с ID '%s' не найдена", result.getCategoryId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processDeliveryMethodNotFound(UpdateProduct.Result.DeliveryMethodNotFound result) {
        String message = String.format("Способ доставки с ID '%s' не найден", result.getDeliveryMethodId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processEmptyFile(UpdateProduct.Result.EmptyFile result) {
        String message = "Обнаружен пустой файл";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processInvalidMediaType(UpdateProduct.Result.InvalidMediaType result) {
        String message = "Неизвестный формат файла";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processSimilarProductNotFound(UpdateProduct.Result.SimilarProductNotFound result) {
        String message = String.format("Похожий товар с ID '%s' не найден", result.getProductId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processOldProductMediaNotFound(UpdateProduct.Result.OldProductMediaNotFound result) {
        String message = String.format("Медиа файл товара с ID '%s' не найден", result.getProductMediaId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processOldVendorDetailsMediaNotFound(UpdateProduct.Result.OldVendorDetailsMediaNotFound result) {
        String message = String.format("Медиа файл о продавце с ID '%s' не найден", result.getVendorDetailsMediaId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }
}
