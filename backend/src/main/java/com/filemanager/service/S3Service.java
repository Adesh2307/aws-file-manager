package com.filemanager.service;

import com.filemanager.model.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public FileResponse uploadFile(MultipartFile file, Map<String, String> metadata) throws IOException {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .contentType(file.getContentType());

        if (metadata != null && !metadata.isEmpty()) {
            builder.metadata(metadata);
        }

        PutObjectResponse response = s3Client.putObject(
                builder.build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return FileResponse.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .status(response.sdkHttpResponse().isSuccessful() ? "Uploaded successfully" : "Upload failed")
                .metadata(metadata)
                .build();
    }

    public List<FileResponse> listFiles() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        return s3Client.listObjectsV2(request).contents().stream()
                .map(s3Object -> {
                    HeadObjectResponse objectMetadata = s3Client.headObject(
                            HeadObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(s3Object.key())
                                    .build()
                    );

                    String presignedUrl = generatePresignedUrl(s3Object.key());

                    return FileResponse.builder()
                            .fileName(s3Object.key())
                            .fileUrl(presignedUrl)
                            .size(s3Object.size())
                            .lastModified(s3Object.lastModified())
                            .metadata(objectMetadata.metadata())
                            .contentType(objectMetadata.contentType())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void deleteFile(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }

    private String generatePresignedUrl(String objectKey) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public Map<String, String> getFileMetadata(String fileName) {
        try {
            HeadObjectResponse response = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build()
            );
            return new HashMap<>(response.metadata());
        } catch (S3Exception e) {
            return new HashMap<>();
        }
    }
}
