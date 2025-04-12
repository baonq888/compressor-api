package com.fc.file_compressor_api.services.strategy;

import com.fc.file_compressor_api.services.strategy.helpers.compressor.HuffmanCompressorHelper;
import com.fc.file_compressor_api.services.strategy.helpers.compressor.HuffmanCompressorHelper.HuffmanNode;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class HuffmanCompressionStrategy implements CompressionStrategy{

    @Override
    public void compress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {
        // Count frequency of each byte in input stream

        Map<Byte, Integer> freqDict = HuffmanCompressorHelper.countFrequency(fileInputStream);

        // Heapify the frequency map
        PriorityQueue<HuffmanNode> minHeap = HuffmanCompressorHelper.heapifyFrequencyMap(freqDict);

        // Build Huffman Tree
        HuffmanCompressorHelper.buildHuffmanTree(minHeap);
        HuffmanNode root = minHeap.poll();

        // Generate Huffman codes
        Map<Byte, String> huffmanCodes = new HashMap<>();
        if (root != null) {
            huffmanCodes = HuffmanCompressorHelper.generateHuffmanCodes(root, "", huffmanCodes);
        }

        // Generate frequency map to pass for Huffman decompressor to rebuild tree
        // Compression happens in one instance of the program
        // Whereas Decompression will happen in a separate instance of the program
        // Therefore, create an in-memory temporary frequency map data, persisting the frequency map
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        HuffmanCompressorHelper.persistFrequencyMap(dataOutputStream, freqDict);

        fileInputStream.reset();

        // Compress the data
        // Writing the compressed data as a string of "0"s and "1"s is very inefficient
        // because each '0' or '1' character takes up a full byte of storage
        // Therefore, use bit manipulation to save space
        // Convert huffman codes from string to bit
        // Example, '0101' as a string → 4 bytes total
        // So '0101' packed → b'\x05' → 1 byte only
        HuffmanCompressorHelper.convertHuffmanCodeToBits(fileInputStream,dataOutputStream,huffmanCodes);
    }

    @Override
    public void decompress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);

        // Read the frequency map from input

    }
}


