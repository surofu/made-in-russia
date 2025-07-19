package com.surofu.madeinrussia.infrastructure.persistence.okved;

import com.surofu.madeinrussia.core.model.okved.OkvedCompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OkvedCompanyRepository {

    private final RestClient okvedRestClient;

    public List<OkvedCompany> findByOkvedId(String okvedId) {
        try {
            OkvedCompany[] okvedCompanies = okvedRestClient.get()
                    .uri("/{id}", okvedId)
                    .retrieve()
                    .body(OkvedCompany[].class);
            return Arrays.asList(Objects.requireNonNullElse(okvedCompanies, new OkvedCompany[0]));
        } catch (Exception e) {
            log.warn("Error retrieving company records from Okved ID: {}", okvedId, e);
            return new ArrayList<>();
        }
    }
}
