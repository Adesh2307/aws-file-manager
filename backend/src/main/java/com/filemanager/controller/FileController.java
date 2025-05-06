package com.filemanager.controller;

import com.filemanager.model.FileResponse;
import com.filemanager.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "metadata", required = false) Map<String, String> metadata) throws IOException {
        FileResponse response = s3Service.uploadFile(file, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> listFiles() {
        List<FileResponse> files = s3Service.listFiles();
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        s3Service.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{fileName}/metadata")
    public ResponseEntity<Map<String, String>> getFileMetadata(@PathVariable String fileName) {
        Map<String, String> metadata = s3Service.getFileMetadata(fileName);
        return ResponseEntity.ok(metadata);
    }
}
