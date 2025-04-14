package com.fc.file_compressor_api.services.strategy.helpers.algorithms.decompressor;
import com.fc.file_compressor_api.services.strategy.helpers.datastructures.HuffmanNode;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HuffmanDecompressorHelper {
    public static Map<Byte, Integer>  readFrequencyMap(DataInputStream dataInputStream) throws IOException {
        int frequencyMapSize = dataInputStream.readInt();
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (int i = 0; i < frequencyMapSize; i++) {
            byte b = dataInputStream.readByte();
            int frequency = dataInputStream.readInt();
            frequencyMap.put(b, frequency);
        }
        return frequencyMap;
    }

    public static void handleSingleUniqueCharacter(HuffmanNode root,
                                                   Map<Byte, Integer> frequencyMap,
                                                   OutputStream fileOutputStream) throws IOException {
        int originalSize = frequencyMap.values().iterator().next();
        for (int i = 0; i < originalSize; i++) {
            fileOutputStream.write(root.data);
        }
    }

    public static void traverseHuffmanTreeAndDecode(HuffmanNode root,
                                                    DataInputStream dataInputStream,
                                                    InputStream fileInputStream,
                                                    int paddingBits,
                                                    OutputStream fileOutputStream) throws IOException {
        HuffmanNode current = root;
        int byteData = dataInputStream.read();
        while (byteData != -1) {
            int bitsToProcess = 8;
            if (fileInputStream.available() == 0) {
                bitsToProcess = 8 - paddingBits;
            }
            // Iterate through bits from left to right
            for (int i = 7; i >= 8 - bitsToProcess; i--) {
                int bit = (byteData >> i) & 1;

                if (current != null) {
                    if (bit == 0) {
                        current = current.left;
                    } else {
                        current = current.right;
                    }

                    if (current != null && current.left == null && current.right == null) {
                        fileOutputStream.write(current.data);
                        current = root;
                    }
                } else {
                    throw new IOException("Invalid compressed data encountered: Prefix code not found in Huffman tree.");
                }
            }
            byteData = dataInputStream.read();
        }
    }
}
