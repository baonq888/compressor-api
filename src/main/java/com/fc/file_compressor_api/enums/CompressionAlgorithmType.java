package com.fc.file_compressor_api.enums;

public enum CompressionAlgorithmType {
    HUFFMAN("huffman"),
    ;

    private final String algorithmType;

    CompressionAlgorithmType(String type) {
        this.algorithmType = type;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public static CompressionAlgorithmType fromType(String algoType) {
        for (CompressionAlgorithmType type : values()) {
            if (type.algorithmType.equalsIgnoreCase(algoType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown algorithm type: " + algoType);
    }
}
