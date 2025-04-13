package com.fiap.tech.challenge.infrastructure.configuration;

import com.fiap.tech.challenge.infrastructure.configuration.properties.storage.AwsS3Properties;
import com.fiap.tech.challenge.infrastructure.configuration.properties.storage.StorageProperties;
import com.fiap.tech.challenge.infrastructure.services.StorageService;
import com.fiap.tech.challenge.infrastructure.services.impl.S3StorageServiceImpl;
import com.fiap.tech.challenge.infrastructure.services.local.InMemoryStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(value = "storage.videos")
    public StorageProperties storageProperties() {
        return new StorageProperties();
    }

    @Bean
    @ConfigurationProperties(value = "storage.aws")
    public AwsS3Properties awsS3Properties() {
        return new AwsS3Properties();
    }

    @Bean
    @Profile({"development", "test-integration", "test-e2e"})
    public StorageService localStorageAPI() {
        return new InMemoryStorageService();
    }

    @Bean
    public S3Client s3Client(AwsS3Properties props) {
        var credentials = AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey());

        return S3Client.builder()
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(props.getRegion()))
                .forcePathStyle(true)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StorageService s3StorageAPI(
            final AwsS3Properties props
    ) {
        return new S3StorageServiceImpl(props.getBucket(), s3Client(props));
    }

}
