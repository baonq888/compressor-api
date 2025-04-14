package com.fc.file_compressor_api.services;

import com.fc.file_compressor_api.services.factory.CompressionStrategyFactory;
import com.fc.file_compressor_api.services.strategy.CompressionStrategy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CompressionServiceImpl implements CompressionService {
    private static final String DEFAULT_COMPRESSION_STRATEGY = "huffman";
    private final CompressionStrategyFactory compressionStrategyFactory;

    public CompressionServiceImpl(CompressionStrategyFactory compressionStrategyFactory) {
        this.compressionStrategyFactory = compressionStrategyFactory;
    }

    @Override
    public Resource compressFile(MultipartFile file, String strategyName) {
        String originalFilename = file.getOriginalFilename();
        String strategyToUse = (strategyName != null && !strategyName.isEmpty()) ? strategyName : DEFAULT_COMPRESSION_STRATEGY;
        CompressionStrategy strategy = compressionStrategyFactory.getStrategy(strategyToUse);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported compression strategy: " + strategyToUse);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            InputStream inputStream = file.getInputStream();
            // Extract filename without extension
            String filenameWithoutExtension = originalFilename;
            int lastDotIndex = 0;
            if (originalFilename != null) {
                lastDotIndex = originalFilename.lastIndexOf('.');
            }
            if (lastDotIndex > 0) {
                filenameWithoutExtension = originalFilename.substring(0, lastDotIndex);
            }

            assert filenameWithoutExtension != null;
            ZipEntry zipEntry = new ZipEntry(filenameWithoutExtension);
            zipOutputStream.putNextEntry(zipEntry);

            InputStream strategyInputStream = inputStream;
            if (strategyToUse.equalsIgnoreCase("huffman")) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                strategyInputStream = new ByteArrayInputStream(buffer.toByteArray());
            }
            strategy.compress(strategyInputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] compressedBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayResource(compressedBytes);
    }

    @Override
    public Resource decompressFile(MultipartFile compressedFile, String strategyName) {
        String strategyToUse = (strategyName != null && !strategyName.isEmpty()) ? strategyName : DEFAULT_COMPRESSION_STRATEGY;
        CompressionStrategy strategy = compressionStrategyFactory.getStrategy(strategyToUse);

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported compression strategy: " + strategyToUse);
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            InputStream inputStream = compressedFile.getInputStream();
            strategy.decompress(inputStream, byteOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] decompressedBytes = byteOut.toByteArray();
        return new ByteArrayResource(decompressedBytes);
    }
}
