package com.surofu.madeinrussia.core.service.me.operation;

import com.surofu.madeinrussia.application.dto.AbstractAccountDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Value(staticConstructor = "of")
public class UploadMeVendorMedia {
    SecurityUser securityUser;
    List<MultipartFile> media;
    List<Long> oldMediaIds;
    List<Integer> newMediaPositions;

    public interface Result {

        <T> T process(Processor<T> processor);

        static Result success(AbstractAccountDto dto, Integer totalMedia) {
            log.info("Successfully upload media({}) to me vendor with ID '{}'", totalMedia, dto.getId());
            return Success.of(dto);
        }

        static Result emptyFile() {
            log.warn("Empty media found while uploading me vendor media");
            return EmptyFile.INSTANCE;
        }

        static Result unknownContentType(String contentType) {
            log.warn("Unknown content type found while uploading me vendor media: {}", contentType);
            return UnknownContentType.of(contentType);
        }

        static Result uploadError(Long userId, Exception e) {
            log.error("Error while uploading media to me vendor with ID '{}''", userId, e);
            return UploadError.INSTANCE;
        }

        static Result saveError(Long userId, Exception e) {
            log.error("Error while saving user data to me vendor with ID '{}'", userId, e);
            return SaveError.INSTANCE;
        }

        static Result invalidPosition(Exception e) {
            log.warn("Invalid position found while uploading me vendor media", e);
            return InvalidPosition.INSTANCE;
        }

        @Value(staticConstructor = "of")
        class Success implements Result {
            AbstractAccountDto dto;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum EmptyFile implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processEmptyFile(this);
            }
        }

        @Value(staticConstructor = "of")
        class UnknownContentType implements Result {
            String contentType;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUnknownContentType(this);
            }
        }

        enum UploadError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUploadError(this);
            }
        }

        enum SaveError implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSaveError(this);
            }
        }

        enum InvalidPosition implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processInvalidPosition(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processEmptyFile(EmptyFile result);

            T processUnknownContentType(UnknownContentType result);

            T processUploadError(UploadError result);

            T processSaveError(SaveError result);

            T processInvalidPosition(InvalidPosition result);
        }
    }
}
