import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class Main {

  public static void main(String[] args) throws IOException {

    BufferedReader bufferedReader = null;

    String[] graphUrl = new String[] {
            "https://www.idi.ntnu.no/emner/idatt2101/v-graf/flytgraf1",
            "https://www.idi.ntnu.no/emner/idatt2101/v-graf/flytgraf2",
            "https://www.idi.ntnu.no/emner/idatt2101/v-graf/flytgraf3",
            "https://www.idi.ntnu.no/emner/idatt2101/v-graf/flytgraf4",
            "https://www.idi.ntnu.no/emner/idatt2101/v-graf/flytgraf5"
    };


    //Reads every file from directory and creates graph.
    for (int i = 0; i < graphUrl.length; i++) {

      URL url = new URL(graphUrl[i]);
      URLConnection connection = url.openConnection();

      try {
        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } catch (IOException e) {
        System.out.println("File not found.");
      }

      System.out.println("Graph: " + (i + 1));
      Graph graph = new Graph(bufferedReader, i);
      System.out.println("Max Flow: " + graph.edmondKarp());
      System.out.println("");

    }
  }
}


class Graph {
  int nodes;
  int edges;
  int source;
  int sink;
  int[] presumedSink = new int[]{7, 1, 1, 7, 7}; //End nodes for the various graphs that are read.
  Node[] graph;

  public Graph(BufferedReader br, int sinkIndex) throws IOException {

    StringTokenizer st = new StringTokenizer(br.readLine());
    nodes = Integer.parseInt(st.nextToken());
    edges = Integer.parseInt(st.nextToken());
    source = 0;
    sink = presumedSink[sinkIndex];
    graph = new Node[nodes];

    for (int i = 0; i < nodes; i++) {
      graph[i] = new Node();
    }

    for (int i = 0; i < edges; i++) {

      st = new StringTokenizer(br.readLine());

      int originNode = Integer.parseInt(st.nextToken());
      int endNode = Integer.parseInt(st.nextToken());
      int capacity = Integer.parseInt(st.nextToken());

      //Edge creation
      Edge originalEdge = new Edge(originNode, endNode, 0, capacity);
      Edge reverseEdge = new Edge(endNode, originNode, 0, 0);

      //Sets each other reverse edge.
      originalEdge.setReverseEdge(reverseEdge);
      reverseEdge.setReverseEdge(originalEdge);

      //Adds to graph array
      graph[originNode].edges.add(originalEdge);
      graph[endNode].edges.add(reverseEdge);
    }
  }

  public int edmondKarp() {

    System.out.println("Maximum flow from " + source + " to " + sink + " using edmonds Karps method.");

    int maxFlow = 0;

    while (true) {


      // Parent array used for storing edges to path.
      Edge[] parent = new Edge[nodes];

      ArrayList<Node> q = new ArrayList<>();
      q.add(graph[source]);

      // BFS finding the shortest augmenting path
      while (!q.isEmpty()) {
        Node curr = q.remove(0);

        // Checks that edge has not yet been visited, and it doesn't
        // point to the source, and it is possible to send flow through it.
        for (Edge e : curr.edges) {
          if (parent[e.endNode] == null && e.endNode != source && e.capacity > e.flow) {
            parent[e.endNode] = e;
            q.add(graph[e.endNode]);
          }
        }
      }

      // If sink was NOT reached, no augmenting path was found.
      // Algorithm terminates and prints out max flow.
      if (parent[sink] == null) {
        break;
      }


      // If sink WAS reached, we will push more flow through the path
      int pushFlow = Integer.MAX_VALUE;

      //List of nodes which are a part of our path we found. Used to give a nice print.
      ArrayList<Integer> nodeList = new ArrayList<>();

      // Finds maximum flow that can be pushed through given path.
      // Adds nodes within the given path into the node list.
      nodeList.add(parent[sink].endNode);
      for (Edge e = parent[sink]; e != null; e = parent[e.originNode]) {
        pushFlow = Math.min(pushFlow, e.capacity - e.flow);
        nodeList.add(e.originNode);
      }

      // Adds to flow values and subtracts from reverse flow values in path.
      for (Edge e = parent[sink]; e != null; e = parent[e.originNode]) {
        e.flow += pushFlow;
        e.reverseEdge.flow -= pushFlow;
      }
      maxFlow += pushFlow;

      // Reverses node list.
      Collections.reverse(nodeList);



      // Dumb print statement.
      for (int i = 0; i < nodeList.size(); i++) {
        System.out.print(nodeList.get(i) + " ");
      }
      System.out.print(" Flow Increase: " + pushFlow);
      System.out.print("\n");
    }
    return maxFlow;
  }
}


/**
 * Simple node class.
 */
class Node {
  ArrayList<Edge> edges = new ArrayList<>();
}

/**
 * Edge class.
 */
class Edge {
  int originNode;
  int endNode;
  int flow;
  int capacity;
  Edge reverseEdge;

  /**
   * Constructor which is responsible for creation of edge.
   *
   * @param originNode is the originNode to end edge.
   * @param endNode    is the end node to the edge.
   * @param flow       is the current flow in which the edge has.
   * @param capacity   is the capacity of the edge.
   */
  public Edge(int originNode, int endNode, int flow, int capacity) {
    this.originNode = originNode;
    this.endNode = endNode;
    this.flow = flow;
    this.capacity = capacity;
  }

  /**
   * Sets the reverse edge to the given edge.
   *
   * @param givenEdge given edge we want to set the reverse edge on.
   */
  public void setReverseEdge(Edge givenEdge) {
    reverseEdge = givenEdge;
  }
}