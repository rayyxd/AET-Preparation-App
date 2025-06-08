package ru.rayyxd.aetpreparation.controllers;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.rayyxd.aetpreparation.services.S3Service;

import java.io.InputStream;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Отдаёт только PNG-изображения.
     * Пример: GET /s3/image/example.png
     */
    @GetMapping("/image/{fileName:.+\\.png}")
    public ResponseEntity<StreamingResponseBody> getImage(@PathVariable String fileName) {
        // просто стримим весь файл — PNG обычно небольшой
        S3Object s3Object = s3Service.downloadFile(new GetObjectRequest(s3Service.getBucket(), fileName));
        StreamingResponseBody body = out -> {
            try (InputStream in = s3Object.getObjectContent()) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    /**
     * Отдаёт MP4-видео с поддержкой Range-запросов.
     * Пример: GET /s3/video/example.mp4
     */
    @GetMapping("/video/{fileName:.+\\.mp4}")
    public ResponseEntity<StreamingResponseBody> getVideo(
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            @PathVariable String fileName) {

        // 1) Получаем метаданные для полного размера
        ObjectMetadata meta = s3Service.getMetadata(fileName);
        long fullLength = meta.getContentLength();

        // 2) Разбираем Range (если есть)
        long start = 0, end = fullLength - 1;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] parts = rangeHeader.substring(6).split("-");
            start = Long.parseLong(parts[0]);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
        }
        if (end > fullLength - 1) end = fullLength - 1;
        long contentLength = end - start + 1;

        // 3) Запрос с диапазоном к S3
        GetObjectRequest getReq = new GetObjectRequest(s3Service.getBucket(), fileName)
                .withRange(start, end);
        S3Object s3Object = s3Service.downloadFile(getReq);

        // 4) Content-Type и заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.setContentLength(contentLength);
        if (rangeHeader != null) {
            headers.set(HttpHeaders.CONTENT_RANGE,
                    String.format("bytes %d-%d/%d", start, end, fullLength));
        }

        // 5) Потоковая передача
        StreamingResponseBody body = out -> {
            try (InputStream in = s3Object.getObjectContent()) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }
        };

        HttpStatus status = (rangeHeader != null) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(body, headers, status);
    }
}
