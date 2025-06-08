package ru.rayyxd.aetpreparation.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // Получить метаданные (размер, content-type)
    public ObjectMetadata getMetadata(String fileName) {
        return amazonS3.getObjectMetadata(bucketName, fileName);
    }

    // Скачивает кусок файла по GetObjectRequest (с поддержкой Range)
    public S3Object downloadFile(GetObjectRequest request) {
        return amazonS3.getObject(request);
    }

    public String getBucket() {
        return bucketName;
    }
}
