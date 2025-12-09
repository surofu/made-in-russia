package com.surofu.exporteru.application.service.product.update.comsumer;

import com.surofu.exporteru.application.command.product.update.UpdateProductFaqCommand;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.faq.ProductFaqAnswer;
import com.surofu.exporteru.core.model.product.faq.ProductFaqQuestion;
import com.surofu.exporteru.core.repository.ProductFaqRepository;
import com.surofu.exporteru.core.repository.TranslationRepository;
import com.surofu.exporteru.core.service.product.operation.UpdateProduct;
import java.util.ArrayList;
import java.util.HashMap;
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
public class FaqProductUpdatingConsumer
    implements ProductUpdatingConsumer {
  private final ProductFaqRepository faqRepository;
  private final TranslationRepository translationRepository;

  @Override
  @Transactional
  public void accept(Product product, UpdateProduct operation) {
    try {
      List<ProductFaq> newFaq = new ArrayList<>();
      List<ProductFaq> oldFaq = new ArrayList<>();

      for (UpdateProductFaqCommand command : operation.getUpdateProductFaqCommands()) {
        ProductFaq faq = new ProductFaq();
        faq.setProduct(product);
        faq.setQuestion(new ProductFaqQuestion(command.question(), new HashMap<>()));
        faq.setAnswer(new ProductFaqAnswer(command.answer(), new HashMap<>()));

        if (!product.getFaq().contains(faq)) {
          newFaq.add(faq);
        } else {
          oldFaq.add(faq);
        }
      }

      List<String> questionsToTranslate = newFaq.stream()
          .map(ProductFaq::getQuestion)
          .map(ProductFaqQuestion::getValue)
          .toList();
      List<String> answersToTranslate = newFaq.stream()
          .map(ProductFaq::getAnswer)
          .map(ProductFaqAnswer::getValue)
          .toList();
      List<String> textsToTranslate =
          new ArrayList<>(questionsToTranslate.size() + answersToTranslate.size());
      textsToTranslate.addAll(questionsToTranslate);
      textsToTranslate.addAll(answersToTranslate);

      List<Map<String, String>> translatedTexts = translationRepository.expand(textsToTranslate);
      for (int i = 0; i < newFaq.size(); i++) {
        ProductFaq faq = newFaq.get(i);
        faq.setQuestion(new ProductFaqQuestion(faq.getQuestion().getValue(),
            translatedTexts.get(i)));
        faq.setAnswer(new ProductFaqAnswer(faq.getAnswer().getValue(),
            translatedTexts.get(i + newFaq.size())));
      }

      List<ProductFaq> faqToDelete = product.getFaq().stream()
          .filter(d -> !oldFaq.contains(d)).toList();

      faqToDelete.forEach(product.getFaq()::remove);
      newFaq.forEach(product.getFaq()::add);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      log.error(e.getMessage(), e);
    }
  }
}
