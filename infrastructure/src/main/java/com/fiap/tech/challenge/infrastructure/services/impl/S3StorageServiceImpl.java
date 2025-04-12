package com.fiap.tech.challenge.infrastructure.services.impl;

import com.fiap.tech.challenge.domain.resource.Resource;
import com.fiap.tech.challenge.infrastructure.services.StorageService;
import com.fiap.tech.challenge.infrastructure.utils.HashingUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class S3StorageServiceImpl implements StorageService {

    private final String bucket;
    private final S3Client s3;

    public S3StorageServiceImpl(final String bucket, final S3Client s3) {
        this.bucket = bucket;
        this.s3 = Objects.requireNonNull(s3);
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
    }

    @Override
    public void deleteAll(final Collection<String> names) {
        final var toDelete = names.stream()
                .map(name -> ObjectIdentifier.builder().key(name).build())
                .toList();

        s3.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(toDelete).build())
                .build());
    }

    @Override
    public Optional<Resource> get(final String name) {
        try {
            final var response = s3.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(name)
                    .build());

            final var content = response.readAllBytes();
            final var metadata = s3.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(name)
                    .build());

            return Optional.of(Resource.with(
                    HashingUtils.checksum(content),
                    content,
                    metadata.contentType(),
                    name
            ));

        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Failed to fetch resource from S3", e);
        }
    }

    @Override
    public List<String> list(final String prefix) {
        final var response = s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build());

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    @Override
    public void store(final String name, final Resource resource) {
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(name)
                        .contentType(resource.contentType())
                        .build(),
                RequestBody.fromBytes(resource.content()));
    }
}

