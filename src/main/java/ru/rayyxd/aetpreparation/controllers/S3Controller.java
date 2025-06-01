package ru.rayyxd.aetpreparation.controllers;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayyxd.aetpreparation.services.S3Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Получить картинку из S3 по имени файла и вернуть как байты с корректным Content-Type.
     * Пример запроса: GET /api/v1/s3/image/example.png
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            // Скачиваем объект из S3
            S3Object s3Object = s3Service.downloadFile(fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // Читаем входящий поток в byte[]
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] tmp = new byte[4096];
            int len;
            while ((len = inputStream.read(tmp)) != -1) {
                buffer.write(tmp, 0, len);
            }
            byte[] imageBytes = buffer.toByteArray();
            inputStream.close();

            // Определяем Content-Type из метаданных S3 (если оно указано при загрузке)
            String contentType = s3Object.getObjectMetadata().getContentType();
            MediaType mediaType;
            if (contentType != null && !contentType.isBlank()) {
                mediaType = MediaType.parseMediaType(contentType);
            } else {
                // Если в метаданных не было Content-Type, пробуем угадать по расширению
                if (fileName.toLowerCase().endsWith(".png")) {
                    mediaType = MediaType.IMAGE_PNG;
                } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                    mediaType = MediaType.IMAGE_JPEG;
                } else if (fileName.toLowerCase().endsWith(".gif")) {
                    mediaType = MediaType.IMAGE_GIF;
                } else {
                    // По умолчанию «octet-stream»
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            // Не сохранять в кеш? (опционально)
            headers.setCacheControl("no-cache, no-store, must-revalidate");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            // Если не удалось прочитать поток
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        } catch (Exception e) {
            // Например, если файл не найден в бакете
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
