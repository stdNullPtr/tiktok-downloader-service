package com.stdnullptr.tiktokdownloaderservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DownloadRequest(
        @NotBlank(message = "Tiktok video link cannot be missing")
        @Pattern(regexp = "^(https?://(www\\.)?tiktok\\.com/\\S*|https?://vm\\.tiktok\\.com/\\S*)$", message = "Invalid tiktok video link")
        String url
) {
}
