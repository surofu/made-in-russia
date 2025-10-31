package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.support.SendSupportMailCommand;
import com.surofu.exporteru.core.service.support.SupportService;
import com.surofu.exporteru.core.service.support.operation.SendSupportMail;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/support")
@Tag(
        name = "Support",
        description = "API for accessing support operations"
)
public class SupportRestController {

    private final SupportService service;

    private final SendSupportMail.Result.Processor<ResponseEntity<?>> sendSupportMailProcessor;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> sendSupportMail(
            @RequestPart("data") SendSupportMailCommand command,
            @RequestPart(value = "media", required = false) List<MultipartFile> media
    ) {
        SendSupportMail operation = SendSupportMail.of(
                command.username(),
                command.email(),
                command.subject(),
                command.body(),
                Objects.requireNonNullElse(media, new ArrayList<>())
        );
        return service.sendSupportMail(operation).process(sendSupportMailProcessor);
    }
}
