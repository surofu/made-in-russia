package com.surofu.exporteru.infrastructure.persistence.advertisement;

import com.surofu.exporteru.core.model.advertisement.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataAdvertisementRepository extends JpaRepository<Advertisement, Long>,
    JpaSpecificationExecutor<Advertisement> {
}
