package com.fc.file_compressor_api.services.strategy;

import com.fc.file_compressor_api.services.strategy.helpers.HuffmanHelper;
import com.fc.file_compressor_api.services.strategy.helpers.HuffmanHelper.HuffmanNode;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class HuffmanCompressionStrategy implements CompressionStrategy{

    @Override
    public void compress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {
        // Count frequency of each byte in input stream
        Map<Byte, Integer> freqDict = HuffmanHelper.countFrequency(fileInputStream);

        // Heapify the frequency map
        PriorityQueue<HuffmanNode> minHeap = HuffmanHelper.heapifyFrequencyMap(freqDict);

        // Build Huffman Tree
        HuffmanHelper.buildHuffmanTree(minHeap);
        HuffmanNode root = minHeap.poll();

        // Generate Huffman codes
        Map<Byte, String> huffmanCodes = new HashMap<>();
        if (root != null) {
            huffmanCodes = HuffmanHelper.generateHuffmanCodes(root, "", huffmanCodes);
        }

    }

    @Override
    public void decompress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {

    }
}


