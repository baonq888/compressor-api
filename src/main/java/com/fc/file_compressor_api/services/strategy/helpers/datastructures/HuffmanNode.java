package com.fc.file_compressor_api.services.strategy.helpers.datastructures;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HuffmanNode implements Comparable<HuffmanNode>{
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
        return Integer.compare(this.freq, o.freq);
    }
}
