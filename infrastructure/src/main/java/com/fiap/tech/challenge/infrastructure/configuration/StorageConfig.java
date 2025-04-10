package com.fiap.tech.challenge.infrastructure.configuration;

import com.fiap.tech.challenge.infrastructure.configuration.properties.storage.StorageProperties;
import com.fiap.tech.challenge.infrastructure.services.StorageService;
import com.fiap.tech.challenge.infrastructure.services.local.InMemoryStorageService;
import com.google.cloud.storage.Storage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(value = "storage.videos")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean
    @Profile({"development", "test-integration", "test-e2e"})
    public StorageService localStorageAPI() {
        return new InMemoryStorageService();
    }

//    @Bean
//    @ConditionalOnMissingBean
//    public StorageService gcStorageAPI(
//            final GoogleStorageProperties props,
//            final Storage storage
//    ) {
//        return new GCStorageService(props.getBucket(), storage);
//    }

}
