package com.surofu.madeinrussia.infrastructure.persistence.faq;

import com.surofu.madeinrussia.core.model.faq.Faq;
import com.surofu.madeinrussia.core.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaFaqRepository implements FaqRepository {

    private final SpringDataFaqRepository repository;

    @Override
    public List<Faq> getAllFaq() {
        return repository.findAll();
    }

    @Override
    public Optional<Faq> getFaqById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void save(Faq faq) {
        repository.save(faq);
    }

    @Override
    public void deleteFaqById(Long id) {
        repository.deleteById(id);
    }
}
