package com.fc.file_compressor_api.services.strategy.helpers.algorithms.decompressor;
import com.fc.file_compressor_api.services.strategy.helpers.datastructures.HuffmanNode;

import java.io.DataInputStream;
import java.io.IOException;
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

    public static void decompressFileWithOneCharacter(HuffmanNode root, Map<Byte, Integer> frequencyMap, OutputStream fileOutputStream) throws IOException {
        int originalSize = frequencyMap.values().iterator().next();
        for (int i = 0; i < originalSize; i++) {
            fileOutputStream.write(root.data);
        }
    }
}
