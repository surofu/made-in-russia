package com.surofu.exporteru.application.service.product.create.consumer;

import com.surofu.exporteru.application.command.product.create.CreateProductFaqCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.faq.ProductFaqAnswer;
import com.surofu.exporteru.core.model.product.faq.ProductFaqQuestion;
import com.surofu.exporteru.core.repository.ProductFaqRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.CreateProduct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqProductCreatingConsumer implements ProductCreatingConsumer {
  private final ProductFaqRepository faqRepository;
  private final TranslationRepository translationRepository;

  @Override
  @Transactional
  public void accept(Product product, CreateProduct operation) {
    try {
      List<ProductFaq> faqs = new ArrayList<>();
      List<Map<String, String>> translatedTexts = translateTexts(operation);

      List<CreateProductFaqCommand> createProductFaqCommands =
          operation.getCreateProductFaqCommands();
      for (int i = 0; i < createProductFaqCommands.size(); i++) {
        CreateProductFaqCommand command = createProductFaqCommands.get(i);
        ProductFaq faq = new ProductFaq();
        faq.setProduct(product);
        faq.setQuestion(new ProductFaqQuestion(command.question(), translatedTexts.get(i)));
        faq.setAnswer(new ProductFaqAnswer(command.answer(),
            translatedTexts.get(i + createProductFaqCommands.size())));
        faqs.add(faq);
      }

      faqRepository.saveAll(faqs);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }

  private List<Map<String, String>> translateTexts(CreateProduct operation) {
    List<String> questionsToTranslate =
        operation.getCreateProductFaqCommands().stream()
            .map(CreateProductFaqCommand::question).toList();
    List<String> answersToTranslate =
        operation.getCreateProductFaqCommands().stream()
            .map(CreateProductFaqCommand::answer).toList();
    List<String> textToTranslate = new ArrayList<>();
    textToTranslate.addAll(questionsToTranslate);
    textToTranslate.addAll(answersToTranslate);
    return translationRepository.expand(textToTranslate);
  }
}
