package com.stdnullptr.tiktokdownloaderservice.model;

import java.util.Objects;

public record DownloadData(byte[] video, String userId, String videoId) {

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DownloadData that = (DownloadData) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(videoId, that.videoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, videoId);
    }

    @Override
    public String toString() {
        return "DownloadData{" +
                "userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }
}
