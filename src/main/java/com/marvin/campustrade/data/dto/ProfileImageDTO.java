package com.marvin.campustrade.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProfileImageDTO {

    @Getter
    @Setter
    public static class PresignRequest {
        @NotBlank
        private String contentType;
    }

    @AllArgsConstructor
    @Getter
    public static class PresignResponse {
        private String key;
        private String uploadUrl;
    }

    @Getter @Setter
    public static class SaveRequest {
        @NotBlank
        private String key;
    }

    @AllArgsConstructor
    @Getter
    public static class ViewResponse {
        private String url;
    }
}

