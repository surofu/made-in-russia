package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;
import com.surofu.madeinrussia.core.repository.VendorViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncVendorViewApplicationService {
    private final VendorViewRepository vendorViewRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveVendorViewInDatabase(VendorView vendorView) {
        try {
            if (vendorViewRepository.notExists(vendorView)) {
                vendorViewRepository.saveVendorView(vendorView);
            }
        } catch (Exception e) {
            log.error("Error while saving vendor view: {}", e.getMessage(), e);
        }
    }
}
