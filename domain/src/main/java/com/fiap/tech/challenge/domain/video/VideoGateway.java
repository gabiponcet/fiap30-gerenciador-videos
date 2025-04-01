package com.fiap.tech.challenge.domain.video;


import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;

import java.util.Optional;

public interface VideoGateway {

    Video create(Video aVideo);

    Video update(Video aVideo);

    void deleteById(VideoID anId);

    Optional<Video> findById(VideoID anId);

    Pagination<VideoPreview> findAll(SearchQuery aQuery);
}
