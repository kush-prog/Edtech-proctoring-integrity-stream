package com.edtech.audit.service;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplayService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${app.topic.exam-events}")
    private String examEventsTopic;

    public void replaySession(String sessionId, String bucketName) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            // Assuming events are stored as JSON files named {sessionId}.json
            // In a real system, you'd list objects with prefix sessionId
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(sessionId + ".json")
                            .build());

            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            log.info("Replaying content for session {}: {}", sessionId, content);

            // Simplified: Assuming content is a single event or array.
            // For this demo, we just send it as a string to the topic.
            kafkaTemplate.send(examEventsTopic, sessionId, content);

            stream.close();
        } catch (Exception e) {
            log.error("Error replaying session {}", sessionId, e);
            throw new RuntimeException("Replay failed", e);
        }
    }
}
