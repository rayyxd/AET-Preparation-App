package ru.rayyxd.aetpreparation.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.rayyxd.aetpreparation.services.S3Service;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Download a file from S3
    @GetMapping("/download/{fileName}")
    public String downloadFile(@PathVariable String fileName) {
        System.out.println("asdads");
        return s3Service.downloadFile(fileName).getKey();
    }
}
