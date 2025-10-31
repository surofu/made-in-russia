package com.surofu.exporteru.infrastructure.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${app.cloudinary.api-key}")
    private String cloudinaryApiKey;

    @Value("${app.cloudinary.api-secret}")
    private String cloudinarySecretKey;

    @Value("${app.cloudinary.cloud-name}")
    private String cloudinaryCloudName;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinaryCloudName,
                "api_key", cloudinaryApiKey,
                "api_secret", cloudinarySecretKey
        ));
    }
}