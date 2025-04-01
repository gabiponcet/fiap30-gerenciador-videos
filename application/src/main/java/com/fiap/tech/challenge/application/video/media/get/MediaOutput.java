package com.fiap.tech.challenge.application.video.media.get;


import com.fiap.tech.challenge.domain.resource.Resource;

public record MediaOutput(
        byte[] content,
        String contentType,
        String name
) {

    public static MediaOutput with(final Resource aResource) {
        return new MediaOutput(
                aResource.content(),
                aResource.contentType(),
                aResource.name()
        );
    }
}
