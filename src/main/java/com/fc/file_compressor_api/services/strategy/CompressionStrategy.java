package com.fc.file_compressor_api.services.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CompressionStrategy {
    void compress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException;
    void decompress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException;
}
