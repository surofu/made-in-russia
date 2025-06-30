package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.faq.CreateFaqCommand;
import com.surofu.madeinrussia.application.command.faq.UpdateFaqCommand;
import com.surofu.madeinrussia.core.model.faq.FaqAnswer;
import com.surofu.madeinrussia.core.model.faq.FaqQuestion;
import com.surofu.madeinrussia.core.service.faq.FaqService;
import com.surofu.madeinrussia.core.service.faq.operation.CreateFaq;
import com.surofu.madeinrussia.core.service.faq.operation.DeleteFaqById;
import com.surofu.madeinrussia.core.service.faq.operation.GetAllFaq;
import com.surofu.madeinrussia.core.service.faq.operation.UpdateFaqById;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/faq")
public class FaqRestController {

    private final FaqService service;

    private final GetAllFaq.Result.Processor<ResponseEntity<?>> getAllFaqProcessor;
    private final CreateFaq.Result.Processor<ResponseEntity<?>> createFaqProcessor;
    private final UpdateFaqById.Result.Processor<ResponseEntity<?>> updateFaqProcessor;
    private final DeleteFaqById.Result.Processor<ResponseEntity<?>> deleteFaqProcessor;

    @GetMapping
    public ResponseEntity<?> getAllFaq() {
        GetAllFaq operation = GetAllFaq.of();
        return service.getAllFaq(operation).process(getAllFaqProcessor);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> createFaq(@RequestBody @Valid CreateFaqCommand createFaqCommand) {
        CreateFaq operation = CreateFaq.of(
                FaqQuestion.of(createFaqCommand.question()),
                FaqAnswer.of(createFaqCommand.answer())
        );
        return service.createFaq(operation).process(createFaqProcessor);
    }

    @PutMapping("{faqId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateFaqById(
            @PathVariable Long faqId,
            @RequestBody @Valid UpdateFaqCommand updateFaqCommand
    ) {
        UpdateFaqById operation = UpdateFaqById.of(
                faqId,
                FaqQuestion.of(updateFaqCommand.question()),
                FaqAnswer.of(updateFaqCommand.answer())
        );
        return service.updateFaqById(operation).process(updateFaqProcessor);
    }

    @DeleteMapping("{faqId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteFaqById(@PathVariable Long faqId) {
        DeleteFaqById operation = DeleteFaqById.of(faqId);
        return service.deleteFaqById(operation).process(deleteFaqProcessor);
    }
}
