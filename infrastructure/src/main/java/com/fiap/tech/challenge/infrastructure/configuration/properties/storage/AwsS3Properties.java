package com.fiap.tech.challenge.infrastructure.configuration.properties.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class AwsS3Properties implements InitializingBean {

    public static final Logger log = LoggerFactory.getLogger(AwsS3Properties.class);
    private String bucket;
    private String region;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public AwsS3Properties() {
    }

    public String getBucket() {
        return bucket;
    }

    public AwsS3Properties setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public AwsS3Properties setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public AwsS3Properties setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public AwsS3Properties setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public AwsS3Properties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    @Override
    public String toString() {
        return "AwsS3Properties{" +
                "bucket='" + bucket + '\'' +
                ", region='" + region + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }

    @Override
    public void afterPropertiesSet() {
        log.debug(toString());
    }
}
