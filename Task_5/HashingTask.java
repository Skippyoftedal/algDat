import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class HashingTask {

  public static void main(String[] args) {

    part1(args);
    System.out.println("\nPART 2");
    compareCollisions();
    compareTimeSpent();

  }

  private static void compareTimeSpent() {
    int hashSize = 10000019;
    double[] percentages = new double[]{0.5, 0.8, 0.9, 0.99, 1};

    System.out.println("Size of table: " + hashSize + " (Prime " + hashSize + " ~ 10 million)");
    System.out.println("Amount of time in ms:");
    System.out.println(
        "-------------------------------------------------------------------------------------");
    System.out.printf("| %-25s | %-25s | %25s | %n", "Percentage", "Linear Probing",
        "Double Hashing");

    for (double percentage : percentages) {
      Integer[] data = TestData.createRandomIntegerArray((int) (percentage * hashSize));

      System.out.printf("| %-25s | %-25s | %25s | %n", 100 * percentage + "%",
          takeTimeLinearProbeInsert(data, hashSize), takeTimeDoubleHashInsert(data, hashSize));
    }
    System.out.println(
        "-------------------------------------------------------------------------------------");
  }


  private static void compareCollisions() {
    int hashSize = 10000019;
    double[] percentages = new double[]{0.5, 0.8, 0.9, 0.99, 1};

    System.out.println("Size of table: " + hashSize + " (Prime " + hashSize + " ~ 10 million)");
    System.out.println("Amount of collisions:");
    System.out.println(
        "-------------------------------------------------------------------------------------");
    System.out.printf("| %-25s | %-25s | %25s | %n", "Percentage", "Linear Probing",
        "Double Hashing");

    for (double percentage : percentages) {
      Integer[] data = TestData.createRandomIntegerArray((int) (percentage * hashSize));

      System.out.printf("| %-25s | %-25s | %25s | %n", 100 * percentage + "%",
          getCollisionsLinear(data, hashSize), getCollisionsDoubleHash(data, hashSize));
    }
    System.out.println(
        "-------------------------------------------------------------------------------------");
  }


  private static void part1(String[] args) {
    //PART 1
    int tableSize = 167;
    HashTableLinked hashTableLinked = new HashTableLinked(tableSize);

    String file = "https://www.idi.ntnu.no/emner/idatt2101/hash/navn.txt";

    if (args.length > 0) {
      file = args[0];
      readFromArg(hashTableLinked, file);
    } else {
      readFromWeb(hashTableLinked, file);
    }

    System.out.println("____________________________________");
    System.out.println("PART 1");
    System.out.println(
        "Finds Tobias Skipevåg Oftedal: " + (hashTableLinked.get("Tobias Skipevåg Oftedal")
            != null));
    System.out.println("Finds Ola Nordmann: " + (hashTableLinked.get("Ola Nordmann") != null));
    System.out.println("TableSize: " + tableSize);
    System.out.println("Total collisions: " + hashTableLinked.getCollisions());
    System.out.println("Collisions per person: " + hashTableLinked.getCollisionsPerPerson());
    System.out.println("Load: " + hashTableLinked.getLoad());
    System.out.println("Load-factor: " + hashTableLinked.getLoadFactor());
    System.out.println("____________________________________");
  }

  private static long takeTimeLinearProbeInsert(Integer[] testData, int size) {
    HashTableLinear hashTableLinear = new HashTableLinear(size);

    long start = System.currentTimeMillis();

    for (Integer data : testData) {

      hashTableLinear.put(data);
    }

    long end = System.currentTimeMillis();
    return end - start;
  }

  private static long takeTimeDoubleHashInsert(Integer[] testData, int size) {
    HashTableDoubleHash hashDouble = new HashTableDoubleHash(size);

    long start = System.currentTimeMillis();
    for (Integer data : testData) {

      hashDouble.put(data);
    }

    long end = System.currentTimeMillis();
    return end - start;
  }

  private static long getCollisionsLinear(Integer[] testData, int size) {
    HashTableLinear hashTableLinear = new HashTableLinear(size);

    for (Integer data : testData) {
      hashTableLinear.put(data);
    }

    return hashTableLinear.getCollisions();

  }

  private static long getCollisionsDoubleHash(Integer[] testData, int size) {
    HashTableDoubleHash hashTableDoubleHash = new HashTableDoubleHash(size);
    for (Integer data : testData) {
      hashTableDoubleHash.put(data);
    }

    return hashTableDoubleHash.getCollisions();
  }


  private static void readFromWeb(HashTableLinked table, String webURL) {
    try {

      URL url = new URL(webURL);
      URLConnection connection = url.openConnection();

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        table.put(new Node(line));
      }
      //135 students
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private static void readFromArg(HashTableAbstract<Node, String> table, String arg) {

    Scanner scanner;

    try {

      scanner = new Scanner(new FileReader(arg));

    } catch (FileNotFoundException e) {

      System.out.println("Something went wrong while reading the given file");

      return;

    }
    int i = 0;
    while (scanner.hasNextLine()) {
      table.put(new Node(scanner.nextLine()));
      i++;
    }
    System.out.println("size " + i);
    scanner.close();

  }

}






abstract class HashTableAbstract<T, E> {

  protected final int tableSize;
  protected final T[] table;
  protected long load;
  protected long collisions;

  public HashTableAbstract(Class<T> elementType, int tableSize) {
    this.tableSize = tableSize;
    this.table = (T[]) Array.newInstance(elementType, tableSize);
    this.load = 0;

  }

  public abstract void put(T s);

  public abstract int hash(T object);

  public abstract T get(E element);

  public boolean contains(E element) {
    return get(element) != null;
  }

  public long getLoad() {
    return load;
  }

  public double getLoadFactor() {
    return (double) load / tableSize;
  }

  public long getCollisions() {
    return collisions;
  }

  public void setCollisions(long collisions) {
    this.collisions = collisions;
  }

  public double getCollisionsPerPerson() {
    return (double) collisions / (double) load;
  }

  @Override
  public String toString() {
    return "HashTable{" + "table=" + Arrays.toString(table) + '}';
  }
}



class HashTableDoubleHash extends HashTableAbstract<Integer, Integer> {

  public HashTableDoubleHash(int size) {
    super(Integer.class, size);
  }

  @Override
  public void put(Integer integer) {
    if (collisions < 0){
      throw new RuntimeException("collisions less than 0 at " + integer);
    }
    if (load >= tableSize) {
      throw new RuntimeException("Full");
    }

    int divHash = hash(integer);
    int hash2 = hash2(integer);
    int probePos = probe(divHash, hash2, 0);

    int i = 1;
    while(table[probePos] != null){
      if (i > tableSize){
        throw new RuntimeException("CANNOT INSERT");
      }
      probePos = probe(divHash, hash2, i);
      collisions++;
      i++;
    }

    table[probePos] = integer;
    load++;
  }

  private int probe(int hashed1, int hashed2, int i) {

    return (hashed1 + hashed2 * i) % tableSize;
  }

  @Override
  public int hash(Integer object) {
    return object % tableSize;
  }

  public int hash2(Integer object) {
    return 7 - (object % 7);
  }

  @Override
  public Integer get(Integer element) {

    for (int i = 0; i < tableSize; i++) {
      int j = probe(hash(element), hash2(element), i);
      if (table[j] == null) {
        return null;
      }

      if (Objects.equals(table[j], element)) {
        return table[j];
      }

    }
    return null;
  }
}



class HashTableLinear extends HashTableAbstract<Integer, Integer> {


  public HashTableLinear(int size) {
    super(Integer.class, size);

  }

  @Override
  public void put(Integer integer) {
    if (tableSize <= load) {
      throw new RuntimeException("Full");
    }

    int pos = hash(integer);

    while (table[pos] != null) {

      pos = (pos + 1) % tableSize;
      collisions++;

    }
    table[pos] = integer;
    load++;
  }


  @Override
  public int hash(Integer integer) {
    return integer % tableSize;
  }

  @Override
  public Integer get(Integer integer) {
    int pos = hash(integer);

    for (int i = 0; i < tableSize; i++) {
      if (Objects.equals(table[pos], integer)){
        return table[pos];
      }
      pos++;
      pos %=tableSize;
    }
    return null;

  }

}



class HashTableLinked extends HashTableAbstract<Node, String> {

  public HashTableLinked(int size) {
    super(Node.class, size);
  }


  @Override
  public Node get(String s) {
    Node goal = new Node(s);
    Node current = table[hash(goal)];

    while (current != null) {
      if (current.getValue().equals(goal.getValue())) {
        return current;
      }

      current = current.getNext();
    }

    return null;
  }

  @Override
  public void put(Node newNode) {

    int pos = hash(newNode);
    if (table[pos] != null) {
      System.out.printf("Collision between %s and %s at index %d\n", newNode, table[pos], pos);
      collisions++;
      Node old = table[pos];
      newNode.setNext(old);
    }
    table[pos] = newNode;
    load++;
  }

  @Override
  public int hash(Node object) {
    int hash = 0;

    for (char c : object.getValue().toCharArray()) {
      hash = (hash * 7 + c) % tableSize;
    }
    return hash;
  }


}


class Node {

  private String value;
  private Node next;

  @Override
  public String toString() {
    return "Node:" + value;
  }

  public Node(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public Node getNext() {
    return next;
  }

  public void setValue(String value) {
    this.value = value;
  }


  public void setNext(Node next) {
    this.next = next;
  }
}

class TestData {

  public static Integer[] createRandomIntegerArray(int length) {
    int bound = 130;
    if (length > Integer.MAX_VALUE / bound){
      throw new RuntimeException("Integer array to long");
    }
    Integer[] randomArray = new Integer[length];

    int a = 1;
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      a += random.nextInt(bound);
      randomArray[i] = a;
    }
    Collections.shuffle(Arrays.asList(randomArray));
    return randomArray;
  }

}






