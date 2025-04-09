package com.fc.file_compressor_api.services.strategy.helpers;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanHelper {

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
}
