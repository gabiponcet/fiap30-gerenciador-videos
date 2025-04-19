package com.fiap.tech.challenge.domain;


import com.fiap.tech.challenge.domain.resource.Resource;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.AudioVideoMedia;
import com.fiap.tech.challenge.domain.video.ClientID;
import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoMediaType;
import com.github.javafaker.Faker;


public final class Fixture {

    private static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static Integer year() {
        return FAKER.random().nextInt(2020, 2030);
    }

    public static Double duration() {
        return FAKER.options().option(
                20.20, 30.10, 120.30, 9.30
        );
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static String title() {
        return FAKER.options().option(
                "System Design no Mercado Livre na prática",
                "Não cometa esses erros ao trabalhar com Microsserviços",
                "Testes de Mutação. Você não testa seu software corretamente"
        );
    }

    public static Video video() {
        return Video.newVideo(
                Fixture.title(),
                Videos.description(),
                duration(),
                Videos.clientId()

        );
    }

    public static final class Videos {

        private static final Video SYSTEM_DESIGN = Video.newVideo(
                "System Design no Mercado Livre na prática",
                description(),
                duration(),
                clientId()


        );

        public static Video systemDesign() {
            return Video.with(SYSTEM_DESIGN);
        }

        public static String description() {
            return FAKER.lorem().paragraph();
        }

        public static ClientID clientId() {
            return FAKER.options().option(ClientID.unique());
        }


        public static VideoMediaType mediaType() {
            return FAKER.options().option(
                    VideoMediaType.values()
            );
        }

        public static Resource resource(final VideoMediaType type) {
            final String contentType = "video/mp4";
            final var checksum = IDUtils.uuid();
            final byte[] content = FAKER.lorem().characters().getBytes();
            return Resource.with(checksum, content, contentType, type.name().toLowerCase());
        }

        public static AudioVideoMedia audioVideo(final VideoMediaType type) {
            final var checksum = IDUtils.uuid();
            return AudioVideoMedia.with(
                    checksum,
                    type.name().toLowerCase(),
                    "/videos/" + checksum
            );
        }

    }
}
