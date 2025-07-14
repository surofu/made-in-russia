package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.infrastructure.persistence.translation.TranslationResponse;

import java.io.IOException;

public interface TranslationRepository {
    TranslationResponse translateToEn(String ...texts) throws IOException, InterruptedException;

    TranslationResponse translateToRu(String ...texts) throws IOException, InterruptedException;

    TranslationResponse translateToZh(String ...texts) throws IOException, InterruptedException;
}
