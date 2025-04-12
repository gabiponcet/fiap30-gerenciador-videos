package com.fiap.tech.challenge.domain.video;

import com.fiap.tech.challenge.domain.Identifier;
import com.fiap.tech.challenge.domain.utils.IDUtils;

import java.util.Objects;

public class ClientID extends Identifier {

    protected final String value;

    private ClientID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }
    public static ClientID unique() {
        return ClientID.from(IDUtils.uuid());
    }

    public static ClientID from(final String anId) {
        return new ClientID(anId);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ClientID videoID = (ClientID) o;

        return value.equals(videoID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
