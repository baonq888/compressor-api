package com.fc.file_compressor_api.services.strategy;

import com.fc.file_compressor_api.services.strategy.helpers.algorithms.compressor.HuffmanCompressorHelper;
import com.fc.file_compressor_api.services.strategy.helpers.algorithms.decompressor.HuffmanDecompressorHelper;
import com.fc.file_compressor_api.services.strategy.helpers.datastructures.HuffmanNode;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class HuffmanCompressionStrategy implements CompressionStrategy{

    @Override
    public void compress(InputStream fileInputStream, OutputStream fileOutputStream) throws IOException {
        // Count frequency of each byte (a unit of data in a file) in input stream

        Map<Byte, Integer> frequencyMap = HuffmanCompressorHelper.countFrequency(fileInputStream);

        // Heapify the frequency map
        PriorityQueue<HuffmanNode> minHeap = HuffmanCompressorHelper.heapifyFrequencyMap(frequencyMap);

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
        HuffmanCompressorHelper.persistFrequencyMap(dataOutputStream, frequencyMap);

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
        // Previously, we defined DataOutputStream as [ENTRY_COUNT][BYTE_1][FREQ_1][BYTE_2][FREQ_2]...
        int frequencyMapSize = dataInputStream.readInt();
        Map<Byte, Integer> frequencyMap = HuffmanDecompressorHelper.readFrequencyMap(dataInputStream);

        // Read the number of padding bits
        byte paddingBitsByte = dataInputStream.readByte();
        int paddingBits = paddingBitsByte & 0xFF; // Ensure we treat it as an unsigned byte

        // Rebuild the Huffman Tree
        PriorityQueue<HuffmanNode> priorityQueue = HuffmanCompressorHelper.heapifyFrequencyMap(frequencyMap);

        HuffmanNode root = null;
        if (priorityQueue.size() != 1) {
            HuffmanCompressorHelper.buildHuffmanTree(priorityQueue);
        }
        root = priorityQueue.poll();

        // Handle the first case where the original file had only one unique character
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            HuffmanDecompressorHelper.decompressFileWithOneCharacter(root, frequencyMap, fileOutputStream);
            return;
        }

        // Read the compressed bit stream and traverse the Huffman Tree
        HuffmanNode current = root;
        int byteData;

    }
}


