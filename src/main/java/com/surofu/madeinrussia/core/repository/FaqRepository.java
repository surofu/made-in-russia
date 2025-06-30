package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.faq.Faq;

import java.util.List;
import java.util.Optional;

public interface FaqRepository {
    List<Faq> getAllFaq();

    Optional<Faq> getFaqById(Long id);

    void save(Faq faq);

    void deleteFaqById(Long id);
}
