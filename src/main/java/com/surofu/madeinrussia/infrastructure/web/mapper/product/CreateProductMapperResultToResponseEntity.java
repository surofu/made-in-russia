package com.surofu.madeinrussia.infrastructure.web.mapper.product;

import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.product.operation.CreateProduct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateProductMapperResultToResponseEntity
        implements CreateProduct.Result.Processor<ResponseEntity<?>> {

    @Override
    public ResponseEntity<?> processSuccess(CreateProduct.Result.Success result) {
        String message = "Продукт успешно создан";
        SimpleResponseMessageDto responseMessageDto = SimpleResponseMessageDto.of(message);
        return new ResponseEntity<>(responseMessageDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> processErrorSavingFiles(CreateProduct.Result.ErrorSavingFiles result) {
        String message = "Ошибка сохранения файлов";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processErrorSavingProduct(CreateProduct.Result.ErrorSavingProduct result) {
        String message = "Ошибка сохранения данных о товаре";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> processCategoryNotFound(CreateProduct.Result.CategoryNotFound result) {
        String message = String.format("Категория с ID '%s' не найдена", result.getCategoryId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processDeliveryMethodNotFound(CreateProduct.Result.DeliveryMethodNotFound result) {
        String message = String.format("Способ доставки с ID '%s' не найден", result.getDeliveryMethodId());
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> processEmptyFile(CreateProduct.Result.EmptyFile result) {
        String message = "Обнаружен пустой файл";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> processInvalidMediaType(CreateProduct.Result.InvalidMediaType result) {
        String message = "Неизвестный формат файла";
        SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
