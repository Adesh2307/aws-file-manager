package com.filemanager.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class FileResponse {
    private String fileName;
    private String fileUrl;
    private Long size;
    private Instant lastModified;
    private Map<String, String> metadata;
    private String contentType;
    private String status;
}
