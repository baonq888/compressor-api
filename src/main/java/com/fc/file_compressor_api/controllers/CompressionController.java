package com.fc.file_compressor_api.controllers;

import com.fc.file_compressor_api.services.CompressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Compression", description = "Endpoints for file compression and decompression")
public class CompressionController {
    private final CompressionService compressionService;

    public CompressionController(CompressionService compressionService) {
        this.compressionService = compressionService;
    }

    @Operation(
            summary = "Compress a file",
            description = "Upload a file to compress. Returns the compressed file as a downloadable ZIP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully compressed file", content = @Content(
                    mediaType = "application/zip",
                    schema = @Schema(type = "string", format = "binary")
            )),
            @ApiResponse(responseCode = "400", description = "Bad request (e.g., no file uploaded)"),
            @ApiResponse(responseCode = "500", description = "Error compressing file")
    })
    @PostMapping(value = "/compress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> compressFile(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Resource resource = compressionService.compressFile(file, null);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + ".zip\"");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));

            return  ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Decompress a file",
            description = "Upload a compressed file to decompress it. Returns the decompressed file as a downloadable resource."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully decompressed file", content = @Content(
                    mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")
            )),
            @ApiResponse(responseCode = "400", description = "Bad request (e.g., no file uploaded)"),
            @ApiResponse(responseCode = "500", description = "Error decompressing file")
    })
    @PostMapping(value = "/decompress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> decompressFile(
            @RequestParam("file") MultipartFile compressedFile) {
        if (compressedFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            String compressedFileName = compressedFile.getOriginalFilename();
            String originalFileName = compressedFileName;

            int fileNameDotIndex = compressedFileName != null ? compressedFileName.lastIndexOf('.') : 0;
            if (fileNameDotIndex > 0) {
                originalFileName = compressedFileName.substring(0, fileNameDotIndex);
            }

            Resource resource = compressionService.decompressFile(compressedFile, null);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"");
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
