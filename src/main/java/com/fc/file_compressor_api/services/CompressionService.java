package com.fc.file_compressor_api.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface CompressionService {
    Resource compressFile(MultipartFile file, String strategyName);
    void decompressFile(MultipartFile compressedFile, String strategyName, String outputFilePath);
}
