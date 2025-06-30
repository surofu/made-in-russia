package com.surofu.madeinrussia.infrastructure.persistence.faq;

import com.surofu.madeinrussia.core.model.faq.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFaqRepository extends JpaRepository<Faq, Long> {
}
