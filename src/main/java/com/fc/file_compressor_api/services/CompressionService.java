package com.fc.file_compressor_api.services;

import com.fc.file_compressor_api.services.factory.CompressionStrategyFactory;
import org.springframework.stereotype.Service;

@Service
public class CompressionService {
    private final CompressionStrategyFactory compressionStrategyFactory;

    public CompressionService(CompressionStrategyFactory compressionStrategyFactory) {
        this.compressionStrategyFactory = compressionStrategyFactory;
    }
}
