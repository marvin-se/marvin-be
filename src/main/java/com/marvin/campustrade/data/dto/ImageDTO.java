package com.marvin.campustrade.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@RequiredArgsConstructor
public class ImageDTO {

    // ------------------------- PRESIGN --------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignRequest {
        @NotEmpty(message = "At least one image must be provided")
        private List<ImageItem> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageItem {

        @NotBlank(message = "File name is required")
        private String fileName;

        @NotBlank(message = "Content type is required")
        private String contentType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignResponse {
        private List<PresignedImage> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PresignedImage {
        private String key;
        private String uploadUrl;
    }


    // ------------------------- SAVE --------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveImagesRequest {
        @NotEmpty(message = "Image keys cannot be empty")
        private List<@NotBlank String> imageKeys;
    }


    // ------------------------- GET --------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageListResponse {
        private List<ImageResponse> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResponse {
        private String key;
        private String url;
    }
}
