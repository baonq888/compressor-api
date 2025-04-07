# File Compressor API

A Java-based API for compressing and decompressing files and folders. Built using Spring Boot, this API applies compression algorithms such as Huffman Encoding, Run-Length Encoding (RLE), and more — with a clean, extensible architecture based on Factory and Strategy design patterns.

---

## Features

- Compress individual files and folders
- Support for multiple compression strategies:
  - Huffman Encoding
  - Run-Length Encoding (RLE)
  - (and more)
- Decompression support
- Extendable with new compression algorithms
- RESTful API built with Spring Boot

---

## Design Patterns Used

| Pattern | Description |
|--------|-------------|
| **Factory** | Dynamically instantiates compression strategy classes based on user input |
| **Strategy** | Allows plugging different compression algorithms (Huffman, RLE, etc.) interchangeably |

---

## Tech Stack

- **Java 23+**
- **Spring Boot 3**
- **Gradle**
- **Lombok**

