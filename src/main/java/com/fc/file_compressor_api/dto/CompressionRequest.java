package com.fc.file_compressor_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
public class CompressionRequest {
    private InputStream fileInputStream;
    private String fileName;
}
