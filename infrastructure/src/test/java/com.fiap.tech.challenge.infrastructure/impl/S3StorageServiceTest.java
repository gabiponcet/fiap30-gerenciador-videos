//package com.fiap.tech.challenge.infrastructure.impl;
//
//import com.fiap.tech.challenge.domain.Fixture;
//import com.fiap.tech.challenge.domain.resource.Resource;
//import com.fiap.tech.challenge.domain.utils.IDUtils;
//import com.fiap.tech.challenge.domain.video.VideoMediaType;
//import com.fiap.tech.challenge.infrastructure.services.impl.S3StorageServiceImpl;
//import com.google.api.gax.paging.Page;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//public class S3StorageServiceTest {
//
//    private S3StorageServiceImpl target;
//
//    private S3Client s3Client;
//
//    private String bucket = "bucket";
//
//    @BeforeEach
//    public void setUp() {
//        this.s3Client = mock(S3Client.class);
//        target = new S3StorageServiceImpl(this.bucket, this.s3Client);
//    }
//
//    @Test
//    public void givenValidResource_whenCallsStore_shouldStoreIt() {
//        final var expectedName = IDUtils.uuid();
//        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
//
//        final var blob =  mockBlob(expectedName, expectedResource);
//        doNothing().when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//
//        this.target.store(expectedName, expectedResource);
//
//        final var captor = ArgumentCaptor.forClass(PutObjectRequest.class);
//
//        verify(s3Client, times(1)).putObject(captor.capture(), eq(RequestBody.fromBytes(expectedResource.content())));
//
//        final var actualBlob = captor.getValue();
//
//        assertEquals(this.bucket, actualBlob.getBlobId().getBucket());
//        assertEquals(expectedName, actualBlob.getBlobId().getName());
//        assertEquals(expectedName, actualBlob.getName());
//        assertEquals(expectedResource.checksum(), actualBlob.getCrc32cToHexString());
//        assertEquals(expectedResource.contentType(), actualBlob.getContentType());
//    }
//
//    @Test
//    public void givenValidResource_whenCallsGet_shouldRetrieveIt() {
//        final var expectedName = IDUtils.uuid();
//        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
//
//        final var blob =  mockBlob(expectedName, expectedResource);
//        doReturn(blob).when(s3Client).get(anyString(), anyString());
//
//        final var actualResource = this.target.get(expectedName).get();
//
//
//        verify(s3Client, times(1)).get(eq(this.bucket), eq(expectedName));
//
//        assertEquals(expectedResource, actualResource);
//    }
//
//    @Test
//    public void givenInvalidResource_whenCallsGet_shouldReturnEmpty() {
//        final var expectedName = IDUtils.uuid();
//        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);
//
//        doReturn(null).when(s3Client).get(anyString(), anyString());
//
//        final var actualResource = this.target.get(expectedName);
//
//        verify(s3Client, times(1)).get(eq(this.bucket), eq(expectedName));
//
//        assertTrue(actualResource.isEmpty());
//    }
//
//    @Test
//    public void givenValidPrefix_whenCallsList_shouldRetrieveAll() {
//
//        final var expectedPrefix = "media_";
//        final var expectedNameVideo = expectedPrefix + IDUtils.uuid();
//        final var expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
//
//
//
//        final var expectedResources = List.of(expectedNameVideo, expectedNameBanner);
//
//        final var blobVideo =  mockBlob(expectedNameVideo, expectedVideo);
//
//        final var page = Mockito.mock(Page.class);
//
//        doReturn(List.of(blobVideo, blobBanner)).when(page).iterateAll();
//
//        doReturn(page).when(s3Client).list(anyString(), any());
//
//        final var actualResources = this.target.list(expectedPrefix);
//
//        verify(s3Client, times(1)).list(eq(this.bucket), eq(Storage.BlobListOption.prefix(expectedPrefix)));
//
//        assertTrue(expectedResources.size() == actualResources.size()
//                && expectedResources.containsAll(actualResources));
//    }
//
//    @Test
//    public void givenValidNames_whenCallsDeleteAll_shouldDeleteAll() {
//        final var expectedPrefix = "media_";
//        final var expectedNameVideo = expectedPrefix + IDUtils.uuid();
//
//        final var expectedNameBanner = expectedPrefix + IDUtils.uuid();
//
//        final var expectedResources = List.of(expectedNameVideo, expectedNameBanner);
//
//        this.target.deleteAll(expectedResources);
//
//        final var captor = ArgumentCaptor.forClass(List.class);
//
//        verify(s3Client, times(1)).delete(captor.capture());
//
//        final var actualResources = ((List<BlobId>) captor.getValue()).stream()
//                .map(BlobId::getName)
//                .toList();
//
//        assertTrue(expectedResources.size() == actualResources.size()
//                && expectedResources.containsAll(actualResources));
//    }
//
//    private Blob mockBlob(final String name, final Resource resource) {
//        final var blob = mock(Blob.class);
//        when(blob.getBlobId()).thenReturn(BlobId.of(bucket, name));
//        when(blob.getCrc32cToHexString()).thenReturn(resource.checksum());
//        when(blob.getContent()).thenReturn(resource.content());
//        when(blob.getContentType()).thenReturn(resource.contentType());
//        when(blob.getName()).thenReturn(resource.name());
//        return blob;
//    }
//
//}