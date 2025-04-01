package com.fiap.tech.challenge.application.video.retrieve.list;

import com.fiap.tech.challenge.application.UseCase;
import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;

public abstract class ListVideoUseCase extends UseCase<SearchQuery, Pagination<VideoListOutput>> {
}
