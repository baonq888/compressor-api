package com.fc.file_compressor_api.services.strategy;

import com.fc.file_compressor_api.services.strategy.helpers.HuffmanHelper;
import com.fc.file_compressor_api.services.strategy.helpers.HuffmanHelper.HuffmanNode;


import java.io.DataOutputStream;
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

        // Generate frequency map to pass for Huffman decompressor to rebuild tree
        // Compression happens in one instance of the program
        // Whereas Decompression will happen in a separate instance of the program
        // Therefore, create an in-memory temporary frequency map data, persisting the frequency map
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        dataOutputStream.writeInt(freqDict.size());

        for (Map.Entry<Byte, Integer> entry: freqDict.entrySet()) {
            dataOutputStream.writeByte(entry.getKey());
            dataOutputStream.writeInt(entry.getValue());
        }

        fileInputStream.reset();

        // Compress the data
        int byteData;
        StringBuilder compressedData = new StringBuilder();
        while ((byteData = fileInputStream.read()) != -1) {
            byte b = (byte) byteData;
            compressedData.append(huffmanCodes.get(b));
        }

        // Writing the compressed data as a string of "0"s and "1"s is very inefficient
        // because each '0' or '1' character takes up a full byte of storage
        // Therefore, handle writing the compressed bit stream efficiently using bit manipulation

    }

    @Override
    public void decompress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {

    }
}


