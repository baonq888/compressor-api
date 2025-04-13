package com.fc.file_compressor_api.services.factory;

import com.fc.file_compressor_api.services.strategy.CompressionStrategy;
import com.fc.file_compressor_api.services.strategy.HuffmanCompressionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultCompressionStrategyFactory implements CompressionStrategyFactory {
    private final Map<String, CompressionStrategy> strategies = new HashMap<>();

    @Autowired
    public DefaultCompressionStrategyFactory(HuffmanCompressionStrategy huffmanCompressionStrategy) {
        strategies.put("huffman", huffmanCompressionStrategy);
    }

    @Override
    public CompressionStrategy getStrategy(String strategy) {
        return strategies.get(strategy);
    }
}
