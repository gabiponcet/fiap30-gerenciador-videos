package com.fiap.tech.challenge.domain.video;

import com.fiap.tech.challenge.domain.validation.Error;
import com.fiap.tech.challenge.domain.validation.ValidationHandler;

import java.util.Objects;

public class VideoValidator extends com.fiap.tech.challenge.domain.validation.Validator {

    private static final int TITLE_MAX_LENGTH = 255;
    private static final int TITLE_MIN_LENGTH = 1;
    private static final String TITLE_SHOULD_NOT_BE_NULL = "'title' should not be null";
    private static final String TITLE_SHOULD_NOT_BE_EMPTY = "'title' should not be empty";
    private static final String TITLE_MUST_BE_BETWEEN_1_AND_255_CHARACTERS = "'title' must be between 1 and 255 characters";

    private static final int DESCRIPTION_MAX_LENGTH = 4_000;
    private static final int DESCRIPTION_MIN_LENGTH = 1;
    private static final String DESCRIPTION_SHOULD_NOT_BE_NULL = "'description' should not be null";
    private static final String DESCRIPTION_SHOULD_NOT_BE_EMPTY = "'description' should not be empty";
    private static final String DESCRIPTION_MUST_BE_BETWEEN_1_AND_4000_CHARACTERS = "'description' must be between 1 and 4000 characters";

    private final Video video;

    protected VideoValidator(final Video video, final ValidationHandler aHandler) {
        super(aHandler);
        this.video = Objects.requireNonNull(video);
    }

    @Override
    public void validate() {
        checkTitleConstraints();
        checkDescriptionConstraints();
    }

    private void checkTitleConstraints() {
        final var title = this.video.getTitle();
        if(title == null) {
            getHandler().append(Error.of(TITLE_SHOULD_NOT_BE_NULL));
            return;
        }
        if(title.isBlank()) {
            getHandler().append(Error.of(TITLE_SHOULD_NOT_BE_EMPTY));
            return;
        }
        final int titleLength = title.trim().length();
        if(titleLength < TITLE_MIN_LENGTH || titleLength > TITLE_MAX_LENGTH) {
            getHandler().append(Error.of(TITLE_MUST_BE_BETWEEN_1_AND_255_CHARACTERS));
        }
    }

    private void checkDescriptionConstraints() {
        final var description = this.video.getDescription();
        if(description == null) {
            getHandler().append(Error.of(DESCRIPTION_SHOULD_NOT_BE_NULL));
            return;
        }
        if(description.isBlank()) {
            getHandler().append(Error.of(DESCRIPTION_SHOULD_NOT_BE_EMPTY));
            return;
        }
        final int descriptionLength = description.trim().length();
        if(descriptionLength < DESCRIPTION_MIN_LENGTH || descriptionLength > DESCRIPTION_MAX_LENGTH) {
            getHandler().append(Error.of(DESCRIPTION_MUST_BE_BETWEEN_1_AND_4000_CHARACTERS));
        }
    }

}
