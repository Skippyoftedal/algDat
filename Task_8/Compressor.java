import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Compressor {

  public static void main(String[] args) {
    runProgram(args);
  }

  public static void runProgram(String[] args) {
    if (args.length < 3) {
      throw new IllegalArgumentException("Not enough arguments, expected 3");
    }

    String fileHandleIn = args[1];
    String fileHandleOut = args[2];
    File input = new File(fileHandleIn);
    File output = new File(fileHandleOut);

    boolean compress = args[0].equalsIgnoreCase("compress");
    boolean decompress = args[0].equalsIgnoreCase("decompress");

    if (compress) {
      Huffman.compress(input, output);
    } else if (decompress) {
      Huffman.decompress(input, output);
    } else {
      throw new IllegalArgumentException("The first argument should be \"compress\" or \"decompress\"");
    }

    System.out.println("\nInput file size: " + input.length() + " bytes");
    System.out.println("Output file size: " + output.length() + " bytes");
  }
}

class Huffman {

  private static final int BYTE_LENGTH = 8;
  private static final int CHAR_MAX = 256;

  private static Node createHuffmanTree(int[] occurrences) {

    PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new NodeComparator());

    for (int i = 0; i < occurrences.length; i++) {
      if (occurrences[i] == 0) {
        continue;
      }

      Node node = new Node();
      node.setCharacter(i);
      node.setValue(occurrences[i]);
      priorityQueue.add(node);
    }

    while ((priorityQueue.size() > 1)) {
      Node first = priorityQueue.poll();
      Node second = priorityQueue.poll();

      Node newNode = new Node();
      newNode.setLeft(first);
      newNode.setRight(second);
      assert second != null;
      newNode.setValue(first.getValue() + second.getValue());
      priorityQueue.add(newNode);
    }
    return priorityQueue.poll();
  }

  public static void compress(File input, File output) {
    String[] bitCodes = findBitCodes(input, output);
    writeCompressToFile(input, output, bitCodes);
  }

  private static String[] findBitCodes(File input, File output) {
    int[] occurrences = new int[Huffman.CHAR_MAX];
    writeOccurrencesToFile(input, output, occurrences);

    Node root = createHuffmanTree(occurrences);
    String[] bitCodes = new String[Huffman.CHAR_MAX];
    String start = "";
    findBitCodesRecursive(start, root, bitCodes);

    return bitCodes;
  }

  private static void writeCompressToFile(File input, File output, String[] bitCodes) {
    try (FileInputStream fileInputStream = new FileInputStream(input);
         DataInputStream dataInputStream = new DataInputStream(fileInputStream);
         FileOutputStream fileOutputStream = new FileOutputStream(output, true)) {

      BitSet bitSet = new BitSet();
      int bitSetIndex = 0;

      int data;
      while ((data = dataInputStream.read()) != -1) {

        String byteCode = bitCodes[data];

        for (int i = 0; i < byteCode.length(); i++) {
          if (byteCode.charAt(i) == '1') {
            bitSet.set(bitSetIndex);
          }
          bitSetIndex++;
        }
      }

      byte[] bytesToWrite = bitSet.toByteArray();
      fileOutputStream.write(bytesToWrite);
      bitSet.clear();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void decompress(File input, File output) {

    try (FileInputStream fileInputStream = new FileInputStream(input);
         DataInputStream dataInputStream = new DataInputStream(fileInputStream);
         DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(output))) {

      int[] occurrences = new int[Huffman.CHAR_MAX];

      for (int i = 0; i < Huffman.CHAR_MAX; i++) {
        int occurrence = dataInputStream.readInt();
        occurrences[i] = occurrence;
      }

      int bitSetIndex = 0;
      BitSet bitSet = new BitSet();

      int readByte;

      while ((readByte = dataInputStream.read()) != -1) {
        for (int j = 0; j < BYTE_LENGTH; j++) {
          int bit = (readByte >> j) & 1;
          if (bit == 1) {
            bitSet.set(bitSetIndex);
          }
          bitSetIndex++;
        }
      }

      Node root = createHuffmanTree(occurrences);
      Node current = root;

      int i = 0;
      while (i < bitSet.length()) {
        while (current.getRight() != null || current.getLeft() != null) {
          boolean bit = bitSet.get(i);
          if (bit) {
            current = current.getRight();
          } else {
            current = current.getLeft();
          }
          i++;
        }
        dataOutputStream.write((char) current.getCharacter());
        current = root;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void findBitCodesRecursive(String current, Node node, String[] bitCodes) {
    if (node == null) {
      return;
    }
    if (node.getLeft() == null && node.getRight() == null) {
      bitCodes[node.getCharacter()] = current;
      return;
    }
    findBitCodesRecursive(current + "0", node.getLeft(), bitCodes);
    findBitCodesRecursive(current + "1", node.getRight(), bitCodes);
  }

  private static void writeOccurrencesToFile(File input, File output, int[] occurrences) {
    try (DataInputStream fileInput = new DataInputStream(new FileInputStream(input));
         DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(output))) {

      int character;
      while ((character = fileInput.read()) != -1) {
        occurrences[character]++;
      }

      for (int occurrence : occurrences) {
        dataOutputStream.writeInt(occurrence);
      }
      dataOutputStream.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class NodeComparator implements Comparator<Node> {

  @Override
  public int compare(Node node1, Node node2) {
    return node1.getValue() - node2.getValue();
  }
}

class Node {

  private Node left;
  private Node right;
  private int character;
  private int value;

  public int getCharacter() {
    return character;
  }

  public void setCharacter(int character) {
    this.character = character;
  }

  public Node getLeft() {
    return left;
  }

  public void setLeft(Node left) {
    this.left = left;
  }

  public Node getRight() {
    return right;
  }

  public void setRight(Node right) {
    this.right = right;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}

