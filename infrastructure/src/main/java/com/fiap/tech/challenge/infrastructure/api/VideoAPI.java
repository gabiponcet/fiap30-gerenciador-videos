package com.fiap.tech.challenge.infrastructure.api;

import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.infrastructure.video.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RequestMapping(value = "videos")
@Tag(name = "Video")
public interface VideoAPI {

//    @PreAuthorize("#id == authentication.principal.subject")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new video with medias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    ResponseEntity<?> createFull(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "duration", required = false) Double duration,
            @RequestParam(name = "video_file", required = false) MultipartFile videoFile
    );

//    @PreAuthorize("#id == authentication.principal.subject")
    @GetMapping(
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Get a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    VideoResponse getById(@PathVariable(name = "id") String id);

    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete a Video by it's identifier")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Video was deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    void deleteById(@PathVariable(name = "id") String id);

//    @PreAuthorize("#id == authentication.principal.subject")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "List all videos paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully"),
            @ApiResponse(responseCode = "422", description = "A query param was invalid"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    Pagination<VideoListResponse> list(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") String dir
    );

    @GetMapping(
            value = "{id}/medias/{type}"
    )
    @Operation(summary = "Get a video media by it's type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Media was not found"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    ResponseEntity<byte[]> getMediaByType(@PathVariable(name = "id") String id, @PathVariable(name = "type") String type);

//    @PreAuthorize("#id == authentication.principal.subject")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            value = "{id}/medias/{type}"
    )
    @Operation(summary = "Upload a media by it's type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media created successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal error was thrown"),
    })
    ResponseEntity<UploadMediaResponse> uploadMediaByType(
            @PathVariable(name = "id") String id,
            @PathVariable(name = "type") String type,
            @RequestParam(name = "media_file") MultipartFile mediaFile
    );
}
