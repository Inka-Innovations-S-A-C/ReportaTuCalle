package com.reportatucalle.modules.media.infrastructure.config;

import com.cloudinary.Cloudinary;
import com.reportatucalle.modules.media.domain.portsout.StoragePort;
import com.reportatucalle.modules.media.infrastructure.adapter.CloudinaryStorageAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConditionalOnProperty(name = "storage.provider", havingValue = "cloudinary")
public class CloudinaryConfig {

    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    public StoragePort storagePort(Cloudinary cloudinary) {
        return new CloudinaryStorageAdapter(cloudinary);
    }
}
