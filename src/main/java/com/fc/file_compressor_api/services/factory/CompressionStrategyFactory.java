package com.fc.file_compressor_api.services.factory;

import com.fc.file_compressor_api.services.strategy.CompressionStrategy;

public interface CompressionStrategyFactory {
    CompressionStrategy getStrategy(String strategy);
}
