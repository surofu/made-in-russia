package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.moderation.SetReviewModerateStatusCommand;
import com.surofu.exporteru.core.service.moderation.ModerationService;
import com.surofu.exporteru.core.service.moderation.operation.SetProductApproveStatus;
import com.surofu.exporteru.core.service.moderation.operation.SetProductReviewApproveStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/moderation")
@Tag(
        name = "Moderation",
        description = "Contains operations for moderation: approve & disapprove"
)
public class ModerationRestController {

    private final ModerationService moderationService;

    private final SetProductApproveStatus.Result.Processor<ResponseEntity<?>> setProductApproveStatusProcessor;
    private final SetProductReviewApproveStatus.Result.Processor<ResponseEntity<?>> setProductReviewApproveStatusProcessor;

    @PostMapping("product/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> setProductApproveStatus(
            @PathVariable Long id,
            @RequestBody SetReviewModerateStatusCommand command
    ) {
        SetProductApproveStatus operation = SetProductApproveStatus.of(id, command.status());
        return moderationService.setProductApproveStatus(operation).process(setProductApproveStatusProcessor);
    }

    @PostMapping("product-review/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> setProductReviewApproveStatus(
            @PathVariable Long id,
            @RequestBody SetReviewModerateStatusCommand command
    ) {
        SetProductReviewApproveStatus operation = SetProductReviewApproveStatus.of(id, command.status());
        return moderationService.setProductReviewApproveStatus(operation).process(setProductReviewApproveStatusProcessor);
    }
}