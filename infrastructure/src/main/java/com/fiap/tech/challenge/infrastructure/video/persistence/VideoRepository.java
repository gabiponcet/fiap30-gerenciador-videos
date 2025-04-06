package com.fiap.tech.challenge.infrastructure.video.persistence;

import com.fiap.tech.challenge.domain.video.VideoPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query("""
            select distinct new com.fiap.tech.challenge.domain.video.VideoPreview(
                v.id as id,
                v.title as title,
                v.description as description
            )
            from Video v
            where
                (:terms is null or UPPER(v.title) like :terms)
            """)
    Page<VideoPreview> findAll(
            @Param("terms") String terms,
            Pageable page
    );
}