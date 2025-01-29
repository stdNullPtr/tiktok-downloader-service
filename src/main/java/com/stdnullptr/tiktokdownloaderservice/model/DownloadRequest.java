package com.stdnullptr.tiktokdownloaderservice.model;

import jakarta.validation.constraints.Pattern;

public record DownloadRequest(
        @Pattern(regexp = "^(https?://(www\\.)?tiktok\\.com/\\S*|https?://vm\\.tiktok\\.com/\\S*)$", message = "Invalid tiktok video link")
        String url
) {
}
