CREATE TABLE videos_video_media (
                                    id CHAR(32) NOT NULL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    checksum VARCHAR(255) NOT NULL,
                                    file_path VARCHAR(500) NOT NULL,
                                    encoded_path VARCHAR(500) NOT NULL,
                                    media_status VARCHAR(50) NOT NULL
);


CREATE TABLE videos (
                        id CHAR(32) NOT NULL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description VARCHAR(1000) NOT NULL,
                        video_id CHAR(32) NULL,
                        CONSTRAINT fk_v_videos_id FOREIGN KEY (video_id) REFERENCES videos_video_media(id) ON DELETE CASCADE
);8