package com.fc.file_compressor_api.services.strategy.helpers.compressor;

import lombok.Getter;
import lombok.Setter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanCompressorHelper {

    @Getter
    @Setter
    public static class HuffmanNode implements Comparable<HuffmanNode>{
        public int freq;
        public byte data;
        public HuffmanNode left;
        public HuffmanNode right;

        public HuffmanNode(int freq, byte data) {
            this.freq = freq;
            this.data = data;
        }

        public HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            return this.freq - o.freq;
        }
    }
    public static Map<Byte, Integer> countFrequency(InputStream inputStream) throws IOException {
        Map<Byte, Integer> result = new HashMap<>();
        int byteData;
        while ((byteData = inputStream.read()) != -1) {
            byte b = (byte) byteData;
            result.put(b, result.getOrDefault(b, 0) + 1);
        }
        return result;
    }

    public static PriorityQueue<HuffmanNode> heapifyFrequencyMap(Map<Byte, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> minHeap = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
            minHeap.offer(new HuffmanNode(entry.getValue(), entry.getKey()));
        }
        return minHeap;
    }

    public static void buildHuffmanTree(PriorityQueue<HuffmanNode> minHeap) {
        while (minHeap.size() > 1) {
            HuffmanNode left = minHeap.poll();
            HuffmanNode right = minHeap.poll();
            HuffmanNode parent = new HuffmanNode(left.freq + left.freq, left, right);
            minHeap.offer(parent);
        }
    }

    public static Map<Byte, String> generateHuffmanCodes(HuffmanNode node, String code, Map<Byte, String> huffmanCodes) {
        if (node == null) return huffmanCodes;
        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.data, code);
            return huffmanCodes;
        }
        generateHuffmanCodes(node.left, code + "0", huffmanCodes);
        generateHuffmanCodes(node.right, code + "1", huffmanCodes);
        return huffmanCodes;
    }

    public static void persistFrequencyMap(DataOutputStream dataOutputStream, Map<Byte, Integer> freqDict) throws IOException {
        dataOutputStream.writeInt(freqDict.size());

        for (Map.Entry<Byte, Integer> entry: freqDict.entrySet()) {
            dataOutputStream.writeByte(entry.getKey());
            dataOutputStream.writeInt(entry.getValue());
        }
    }

    public static void convertHuffmanCodeToBits(InputStream fileInputStream, DataOutputStream dataOutputStream, Map<Byte, String> huffmanCodes) throws IOException {
        int byteData;
        int currentByte = 0;
        int bitCount = 0;
        while ((byteData = fileInputStream.read()) != -1) {
            byte b = (byte) byteData;
            String code = huffmanCodes.get(b);
            for (char bitChar : code.toCharArray()) {
                // Shift the current byte to make space for the new bit
                currentByte <<= 1;
                //  without the if block and the |= 1 operation,
                //  we would not be able to represent the '1's from the Huffman code in compressed data.
                //  The compressed output would only contain sequences of 0 bits,
                if (bitChar == '1') {
                    currentByte |= 1;
                }
                bitCount ++;

                // 1 byte only has 8 bits
                if (bitCount == 8) {
                    dataOutputStream.write((byte) currentByte); // store the full 8-bit byte
                    currentByte = 0;
                    bitCount = 0;
                }
            }
        }

        // What if the total bit length is not a multiple of 8?
        // Zero-padding on the right
        int paddingBits = 0;
        if (bitCount > 0) {
            paddingBits = 8 - bitCount;
            currentByte <<= paddingBits;
            dataOutputStream.write((byte) currentByte);
        }

        // Store the number of padding bits
        // We encode full-length files or store size info
        // so the decoder knows when to stop reading bits
        // otherwise it might read padding as real data.
        dataOutputStream.writeByte((byte) paddingBits);

        dataOutputStream.flush();
    }


}
