import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/**
 * The GraphAlgorithms class contains methods for graph-related algorithms
 * and a main method for demonstration purposes.
 *
 * @author Ramtin Samavat, Tobias Oftedal, and Jeffrey Tabiri.
 * @version 1.0
 * @since Oct 04, 2023.
 */
class GraphAlgorithms {

  private static Graph readGraphFromFile(String webURL) {
    try {
      URL url = new URL(webURL);
      URLConnection connection = url.openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      Graph graph = new Graph();
      graph.newGraph(reader);
      reader.close();

      return graph;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    task1("https://www.idi.ntnu.no/emner/idatt2101/uv-graf/ø6g1", 5); // Execute BFS.
    task2("https://www.idi.ntnu.no/emner/idatt2101/uv-graf/ø6g5"); // Execute topological sort-
  }

  private static void task1(String webURL, int start) {
    // Create graph.
    Graph graph = readGraphFromFile(webURL);

    // Choose a start node for BFS.
    Node startNode = graph.node[start];
    System.out.println("\nStart node: " + startNode + "\n");

    // Perform BFS to calculate distances and predecessors.
    graph.breadthFirstSearch(startNode);

    // Print table headlines.
    System.out.println("Node | Predecessor  | Distance");

    // Print distances and predecessors.
    for (int i = 0; i < graph.getAmountOfNodes(); i++) {
      Node currentNode = graph.node[i];
      Predecessor predecessor = (Predecessor) currentNode.d;

      System.out.printf("%-4d | %-12s | %-8d%n", i,
              predecessor.getPredecessor() != null ? predecessor.getPredecessor().toString() : "-",
              predecessor.getDistance());
    }
  }

  private static void task2(String webURL) {
    // Create graph.
    Graph graph = readGraphFromFile(webURL);

    // Perform topological sorting.
    Node currentNode = graph.topologicalSorting();

    // Print the sorted nodes.
    System.out.println("\nTopological Sorting:");

    while (currentNode != null) {
      System.out.print(currentNode.value + " ");
      currentNode = ((TopologicalSortStructure) currentNode.d).next;
    }
  }
}

class Edge {
  private final Edge next;
  private final Node to;
  public Edge(Node n, Edge nextEdge) {
    this.to = n;
    this.next = nextEdge;
  }

  public Edge getNext() {
    return next;
  }

  public Node getTo() {
    return to;
  }
}

class Node {
  Edge edge1;
  Object d;
  int value;

  public Node(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}

class Graph {
  private int amountOfNodes;
  private int amountOfEdges;
  Node[] node;

  public int getAmountOfNodes() {
    return amountOfNodes;
  }

  public void newGraph(BufferedReader reader) throws IOException {
    StringTokenizer stringTokenizer = new StringTokenizer(reader.readLine());
    amountOfNodes = Integer.parseInt(stringTokenizer.nextToken());
    node = new Node[amountOfNodes];
    for (int i = 0; i < amountOfNodes; ++i) {
      node[i] = new Node(i);
    }
    this.amountOfEdges = Integer.parseInt(stringTokenizer.nextToken());
    for (int i = 0; i < amountOfEdges; ++i) {
      stringTokenizer = new StringTokenizer(reader.readLine());
      int from = Integer.parseInt(stringTokenizer.nextToken());
      int to = Integer.parseInt(stringTokenizer.nextToken());
      Edge edge = new Edge(node[to], node[from].edge1);
      node[from].edge1 = edge;
    }
  }

  public void initializePredecessor(Node n) {
    for (int i = amountOfNodes; i-- > 0;) {
      this.node[i].d = new Predecessor();
    }
    ((Predecessor)n.d).setDistance(0);
  }

  public void breadthFirstSearch(Node startNode) {
    initializePredecessor(startNode);
    CustomQueue queue = new CustomQueue(amountOfNodes - 1);
    queue.addToQueue(startNode);
    while (!queue.empty()) {
      Node node = (Node)queue.nextInQueue();
      for (Edge edge = node.edge1; edge != null; edge = edge.getNext()) {
        Predecessor predecessor = (Predecessor) edge.getTo().d;
        if (predecessor.getDistance() == predecessor.getInfinite()) {
          predecessor.setDistance(((Predecessor)node.d).getDistance() + 1);
          predecessor.setPredecessor(node);
          queue.addToQueue(edge.getTo());
        }
      }
    }
  }

  public Node depthFirstTopological(Node startnode, Node nodeList) {
    TopologicalSortStructure startNodeList = (TopologicalSortStructure)startnode.d;
    if (startNodeList.found) {
      return nodeList;
    }
    startNodeList.found = true;
    for (Edge edge = startnode.edge1; edge != null; edge = edge.getNext()) {
      nodeList = depthFirstTopological(edge.getTo(), nodeList);
    }
    startNodeList.next = nodeList;
    return startnode;
  }

  public Node topologicalSorting() {
    Node list = null;
    for (int i = amountOfNodes; i-- > 0 ;) {
      node[i].d = new TopologicalSortStructure();
    }
    for (int i = amountOfNodes; i-- > 0;) {
      list = depthFirstTopological(node[i], list);
    }
    return list;
  }
}

class TopologicalSortStructure {
  boolean found;
  Node next;
}

class Predecessor {
  private int distance;

  private Node predecessor;

  // Represents an "infinite" distance, which is applied to nodes that cannot be reached from the start node.
  private final int infinite = 1000000000;

  public Predecessor() {
    this.distance = infinite;
  }

  public int getDistance() {
    return distance;
  }

  public Node getPredecessor() {
    return predecessor;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public void setPredecessor(Node predecessor) {
    this.predecessor = predecessor;
  }

  public int getInfinite() {
    return infinite;
  }
}

class CustomQueue {
  private final Object[] table;
  private int start;
  private int end;
  private int amount;

  public CustomQueue(int tableSize) {
    this.table = new Object[tableSize];
    this.start = 0;
    this.end = 0;
    this.amount = 0;
  }

  public boolean empty() {
    return amount == 0;
  }

  public boolean full() {
    return amount == table.length;
  }

  public void addToQueue(Object object) {
    if (full()) {
      return;
    }
    table[end] = object;
    end = (end + 1) % table.length;
    ++amount;
  }

  public Object nextInQueue() {
    if (!empty()) {
      Object object = table[start];
      start = (start + 1) % table.length;
      --amount;
      return object;
    } else {
      return null;
    }
  }

  public Object peek() {
    if (!empty()) {
      return table[start];
    } else {
      return null;
    }
  }
}