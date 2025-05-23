package com.fiap.tech.challenge.domain.video;

import com.fiap.tech.challenge.domain.UnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AudioVideoTest extends UnitTest {

    @Test
    public void givenValidParams_whenCallsNewVideo_shouldReturnNewInstance() {
        final var expectedChecksum = "abc";
        final var expectedName = "Trailer.png";
        final var expectedRawLocation = "/images/ac";
        final var expectedEncodedLocation = "/images/ac-encoded";
        final var expectedStatus = MediaStatus.PENDING;

        final var actualVideo = AudioVideoMedia.with(expectedChecksum, expectedName, expectedRawLocation, expectedEncodedLocation, expectedStatus);

        assertNotNull(actualVideo);
        assertEquals(expectedChecksum, actualVideo.checksum());
        assertEquals(expectedName, actualVideo.name());
        assertEquals(expectedEncodedLocation, actualVideo.encodedLocation());
        assertEquals(expectedRawLocation, actualVideo.rawLocation());
        assertEquals(expectedStatus, actualVideo.status());
    }

    @Test
    public void givenTwoImagesWithSameChecksumAndLocation_whenCallsEquals_shouldReturnTrue() {
        final var expectedChecksum = "abc";
        final var expectedRawLocation = "/images/ac";

        final var video1 = AudioVideoMedia.with(expectedChecksum, "Random", expectedRawLocation, "location1", MediaStatus.PENDING);

        final var video2 = AudioVideoMedia.with(expectedChecksum, "Simple", expectedRawLocation, "location2", MediaStatus.COMPLETED);


        final var actualEquals = video1.equals(video2);

        assertTrue(actualEquals);
        assertNotSame(video1, video2);
    }

    @Test
    public void givenInvalidParams_whenCallsWith_shouldReturnError() {
        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with(null, "Random", "rawLo", "encodedLo", MediaStatus.PENDING));

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with("abc", null, "rawLo", "encodedLo", MediaStatus.PENDING));

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", null, "encodedLo", MediaStatus.PENDING));

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", "rawLo", null, MediaStatus.PENDING));

        assertThrows(NullPointerException.class,
                () -> AudioVideoMedia.with("abc", "Random", "rawLo", "encodedLo", null));
    }
}
