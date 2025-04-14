package com.fc.file_compressor_api.services;

import com.fc.file_compressor_api.services.factory.CompressionStrategyFactory;
import com.fc.file_compressor_api.services.strategy.CompressionStrategy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CompressionServiceImpl implements CompressionService{
    private static final String DEFAULT_COMPRESSION_STRATEGY = "huffman";
    private final CompressionStrategyFactory compressionStrategyFactory;

    public CompressionServiceImpl(CompressionStrategyFactory compressionStrategyFactory) {
        this.compressionStrategyFactory = compressionStrategyFactory;
    }

    @Override
    public Resource compressFile(MultipartFile file, String strategyName) {
        String strategyToUse = (strategyName != null && !strategyName.isEmpty()) ? strategyName : DEFAULT_COMPRESSION_STRATEGY;
        CompressionStrategy strategy = compressionStrategyFactory.getStrategy(strategyToUse);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported compression strategy: " + strategyToUse);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            InputStream inputStream = file.getInputStream();
            ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename() + '.' + strategyToUse);
            zipOutputStream.putNextEntry(zipEntry);
            strategy.compress(inputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayResource(compressedBytes);
    }

    @Override
    public void decompressFile(MultipartFile compressedFile, String strategyName, String outputFilePath) {

    }
}
