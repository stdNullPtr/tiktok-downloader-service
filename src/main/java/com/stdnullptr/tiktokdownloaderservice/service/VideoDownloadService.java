package com.stdnullptr.tiktokdownloaderservice.service;

import com.stdnullptr.tiktokdownloaderservice.model.DownloadData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoDownloadService {

    private static final String DUMMY_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    private final RestTemplate restTemplate;

    public DownloadData download(final String tiktokVideoUrl) {
        final TiktokResponse tiktokResponse = getTiktokVideoPageData(tiktokVideoUrl);
        final VideoData videoData = extractVideoData(tiktokResponse);
        final byte[] video = downloadVideo(videoData, tiktokResponse);

        return new DownloadData(video, videoData.userId, videoData.videoId);
    }

    private byte[] downloadVideo(final VideoData videoData, final TiktokResponse tiktokResponse) {
        final List<String> cookies = tiktokResponse.responseHeaders().get(SET_COOKIE);
        if (CollectionUtils.isEmpty(cookies)) {
            throw new IllegalStateException("Cookie header from tiktok video page response is empty.");
        }

        final var headersForTiktokVideoDownload = new HttpHeaders();
        headersForTiktokVideoDownload.add(USER_AGENT, DUMMY_USER_AGENT);
        headersForTiktokVideoDownload.add(COOKIE, String.join("; ", cookies));

        final var videoResponse = restTemplate.exchange(videoData.downloadUrl, GET, new HttpEntity<>(headersForTiktokVideoDownload), byte[].class);

        if (videoResponse.getBody() == null) {
            throw new IllegalStateException("Response from video download contains no body.");
        }

        return videoResponse.getBody();
    }

    private VideoData extractVideoData(final TiktokResponse tiktokResponse) {
        final var matcherVideoIdUserId = Pattern.compile("&item_id=(.*?)&.*?\"uniqueId\":\"(.*?)\"").matcher(tiktokResponse.pageContent());
        if (!matcherVideoIdUserId.find() || matcherVideoIdUserId.groupCount() != 2) {
            throw new IllegalStateException("Failed to find video ID and user ID from TikTok response.");
        }

        final var videoId = matcherVideoIdUserId.group(1);
        final var userId = matcherVideoIdUserId.group(2);

        final var matcherVideoUrl = Pattern.compile("\"UrlList\":\\[\"(http.*?)\".*%s".formatted(videoId)).matcher(tiktokResponse.pageContent());
        if (!matcherVideoUrl.find()) {
            throw new IllegalStateException("Failed to find video URL from TikTok response.");
        }

        final var downloadUrlEscaped = matcherVideoUrl.group(1);
        final var downloadUrl = downloadUrlEscaped.replace("\\u002F", "/");

        return new VideoData(videoId, userId, downloadUrl);
    }

    private TiktokResponse getTiktokVideoPageData(final String url) {
        final var headersForTiktokRequest = new HttpHeaders();
        headersForTiktokRequest.add(USER_AGENT, DUMMY_USER_AGENT);
        final var tiktokHtmlResponse = restTemplate.exchange(url, GET, new HttpEntity<>(headersForTiktokRequest), String.class);

        if (tiktokHtmlResponse.getBody() == null) {
            throw new IllegalStateException("Response from tiktok contains no body.");
        }

        return new TiktokResponse(tiktokHtmlResponse.getBody(), tiktokHtmlResponse.getHeaders());
    }

    private record TiktokResponse(String pageContent, HttpHeaders responseHeaders) {
    }

    private record VideoData(String videoId, String userId, String downloadUrl) {
    }

}
