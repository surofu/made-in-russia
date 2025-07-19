package com.surofu.madeinrussia.core.service.company;

import com.surofu.madeinrussia.core.service.company.operation.GetCompaniesByCategorySlug;

public interface CompanyService {

    GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation);
}
