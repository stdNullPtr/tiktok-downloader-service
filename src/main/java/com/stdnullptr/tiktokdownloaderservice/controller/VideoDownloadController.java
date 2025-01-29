package com.stdnullptr.tiktokdownloaderservice.controller;

import com.stdnullptr.tiktokdownloaderservice.model.DownloadData;
import com.stdnullptr.tiktokdownloaderservice.model.DownloadRequest;
import com.stdnullptr.tiktokdownloaderservice.service.VideoDownloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${service.api.url}/download")
@CrossOrigin(origins = "${service.api.allowed-origins}", exposedHeaders = CONTENT_DISPOSITION)
public class VideoDownloadController {

    private final VideoDownloadService videoDownloadService;

    @PostMapping
    ResponseEntity<byte[]> download(@Valid @RequestBody final DownloadRequest request) {
        final DownloadData downloadData = videoDownloadService.download(request.url());

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .header(CONTENT_DISPOSITION, "attachment; filename=\"%s_%s.mp4\"".formatted(downloadData.userId(), downloadData.videoId()))
                .body(downloadData.video());

    }
}
