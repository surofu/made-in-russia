package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMedia;
import com.surofu.madeinrussia.core.model.product.productMedia.ProductMediaUrl;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMediaImage;
import com.surofu.madeinrussia.core.repository.FileStorageRepository;
import com.surofu.madeinrussia.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncProductApplicationService {

    private final ProductRepository productRepository;
    private final FileStorageRepository fileStorageRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> deleteProduct(Product product) {
        try {
            productRepository.delete(product);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<Void> deleteProductMediaFiles(Product product) {
        List<String> mediaLinks = product.getMedia().stream()
                .map(ProductMedia::getUrl)
                .map(ProductMediaUrl::toString)
                .toList();

        List<String> vendorMediaLinks = product.getProductVendorDetails().getMedia().stream()
                .map(ProductVendorDetailsMedia::getImage)
                .map(ProductVendorDetailsMediaImage::getUrl)
                .toList();

        List<String> allLinks = new ArrayList<>(mediaLinks.size() + vendorMediaLinks.size());
        allLinks.addAll(mediaLinks);
        allLinks.addAll(vendorMediaLinks);

        try {
            fileStorageRepository.deleteAllMediaByLink(allLinks);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
