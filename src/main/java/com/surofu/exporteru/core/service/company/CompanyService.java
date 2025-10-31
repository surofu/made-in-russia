package com.surofu.exporteru.core.service.company;

import com.surofu.exporteru.core.service.company.operation.GetCompaniesByCategorySlug;

public interface CompanyService {

    GetCompaniesByCategorySlug.Result getByCategorySlug(GetCompaniesByCategorySlug operation);
}
