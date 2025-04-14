package com.fc.file_compressor_api.controllers;

import com.fc.file_compressor_api.services.CompressionService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class CompressionController {
    private final CompressionService compressionService;

    public CompressionController(CompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @PostMapping("/compress")
    public ResponseEntity<Resource> compressFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Resource resource = compressionService.compressFile(file, null);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + ".zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));

            return  ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
