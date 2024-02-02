package com.skipevaag;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import eu.jacquet80.minigeo.MapWindow;
import eu.jacquet80.minigeo.Point;
import eu.jacquet80.minigeo.Segment;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Main {


  public static void main(String[] args) {
    ArrayList<Node> nodes = PreProcessor.getNodeListFromFile("src/main/resources/noder.txt");
    int[] pointsOfInterest = new int[]{2531818, 1940019, 6798510};

    PreProcessor.writeLandmarkToNodes("src/main/resources/landemerker_til_noder.txt",
    pointsOfInterest, nodes);
    PreProcessor.writeNodesToLandMark("src/main/resources/noder_til_landemerker.txt",
    pointsOfInterest, nodes);

    HashMap<Integer, LocationOfInterest> nodesWithLocation =
        PreProcessor.getNodesWithPointsOfInterest(
        "src/main/resources/interressepunkter.txt");
    Graph graph = new Graph();

    nodes.forEach(node -> node.setEdge(null));
    PreProcessor.addEdgesToNodes("src/main/resources/kanter.txt", nodes, false);
    graph.setNodes(nodes);

    int[][] landmarksToNodes = PreProcessor.getPrecalculatedDistances(
        "src/main/resources/landemerker_til_noder.txt");

    int[][] nodesToLandmarks = PreProcessor.getPrecalculatedDistances(
        "src/main/resources/noder_til_landemerker.txt");

    runAltSearch(nodes, graph, 7826348, 2155867, landmarksToNodes, nodesToLandmarks);

    runSearchesFromTask(nodes, graph, landmarksToNodes, nodesToLandmarks);
    runLocationSearchesFromTask(nodes, nodesWithLocation, graph);

  }

  private static void runSearchesFromTask(ArrayList<Node> nodes, Graph graph,
                                          int[][] landmarksToNodes, int[][] nodesToLandmarks) {
    //spesifikke
    //runAltSearch(nodes, graph, 2266026, 7826348, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 5009309, 999080, landmarksToNodes, nodesToLandmarks);

    //Examples
    runAltSearch(nodes, graph, 2800567, 7705656, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 7705656, 2800567, landmarksToNodes, nodesToLandmarks);

    runAltSearch(nodes, graph, 647826, 136530, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 136530, 647826, landmarksToNodes, nodesToLandmarks);

    runAltSearch(nodes, graph, 7826348, 2948202, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 2948202, 7826348, landmarksToNodes, nodesToLandmarks);

    runAltSearch(nodes, graph, 339910, 1853145, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 1853145, 339910, landmarksToNodes, nodesToLandmarks);

    runAltSearch(nodes, graph, 2503331, 2866570, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 2866570, 2503331, landmarksToNodes, nodesToLandmarks);

    runAltSearch(nodes, graph, 6441311, 3168086, landmarksToNodes, nodesToLandmarks);
    runAltSearch(nodes, graph, 3168086, 6441311, landmarksToNodes, nodesToLandmarks);
  }

  private static void runAltSearch(ArrayList<Node> nodes, Graph graph, int start, int end,
                                   int[][] landmarksToNodes, int[][] nodesToLandmarks) {

    graph.setLandmarksToNodes(landmarksToNodes);
    graph.setNodesToLandmarks(nodesToLandmarks);

    long startTimer = System.currentTimeMillis();
    int b = graph.dikstra(start, end);
    ArrayList<Node> pathDik = graph.reconstructPath(end);

    long endTimer = System.currentTimeMillis();

    System.out.printf("%d->%d , nodes in path: %d l:" + "\u001B[32m" + getTimeAsString(b)
            + " calculation time: %d" + "\u001B[0m" + '\n', start, end, pathDik.size(),
        endTimer - startTimer);

    startTimer = System.currentTimeMillis();
    int a = graph.alt(start, end);
    ArrayList<Node> path = graph.reconstructPath(end);
    endTimer = System.currentTimeMillis();

    System.out.printf(
        "%d->%d , nodes in path: %d l:" + getTimeAsString(a) + " calculation time: %d" + '\n',
        start, end, path.size(), endTimer - startTimer);

    MapVisualiser mapVisualiser = new MapVisualiser();

    ArrayList<Node> tenPercent = new ArrayList<>();

    for (int i = 0; i < nodes.size(); i++) {
      if (i % 100 == 1) {
        tenPercent.add(nodes.get(i));
      }
    }
    mapVisualiser.addNodes(tenPercent, Color.BLACK);
    mapVisualiser.addNodesContinuous(pathDik, Color.GREEN);
    mapVisualiser.addNodesContinuous(path, Color.RED);

    mapVisualiser.show();
  }

  private static void runLocationSearchesFromTask(ArrayList<Node> nodes,
                                                  HashMap<Integer, LocationOfInterest> nodesWithLocation,
                                                  Graph graph) {
    //orkanger 5 nærmeste ladestasjoner
    runDijkstraLocationTypeSearch(2266026, LocationType.CHARGING_STATION, 5, nodes, graph,
        nodesWithLocation);
    //5 drikkested som er nærmest trondheim camping
    runDijkstraLocationTypeSearch(3005466, LocationType.DRINKING_SPOT, 5, nodes, graph,
        nodesWithLocation);

    //5 spisesteder nær Hotell Östersund
    runDijkstraLocationTypeSearch(3240367, LocationType.EATING_SPOT, 5, nodes, graph,
        nodesWithLocation);

    runDijkstraLocationTypeSearch(3240367, LocationType.EATING_SPOT, 5, nodes, graph,
        nodesWithLocation);
  }

  private static void runDijkstraLocationTypeSearch(int start, LocationType desired,
                                                    int desiredAmount, ArrayList<Node> nodes,
                                                    Graph djikstra,
                                                    HashMap<Integer, LocationOfInterest> nodesWithLocation) {
    long startTime = System.currentTimeMillis();

    ArrayList<Integer> answer = djikstra.getClosestLocations(start, desired, desiredAmount,
        nodesWithLocation);

    long end = System.currentTimeMillis();

    System.out.println(
        "\nLooking for " + desiredAmount + " locations of type: " + desired + " close to " + start);
    System.out.println("Time spent " + (end - startTime) + " ms");

    for (Integer locationIndex : answer) {
      System.out.println(nodesWithLocation.get(locationIndex).getName());
      Node node = nodes.get(locationIndex);
      System.out.println(
          "Coordinate long:" + node.getCoordinate().getLongitude() + "lat:" + node.getCoordinate()
              .getLatitude());
      System.out.println(node.id + " " + getTimeAsString(node.weight));
    }

  }

  private static void runDijkstraSearch(ArrayList<Node> nodes, Graph graph) {

    int nodeAmount = nodes.size();

    //DJIKSTRA implementation
    int start = 2948202;
    int end = 7826348;

    long startTime = System.currentTimeMillis();

    int l = graph.dikstra(start, end);
    List<Node> path = graph.reconstructPath(end);

    long endTime = System.currentTimeMillis();

//    MapVisualiser mapVisualiser = new MapVisualiser();
//
//    ArrayList<Node> tenPercent = new ArrayList<>();
//
//    for (int i = 0; i < nodes.size(); i++) {
//      if (i % 100 == 1) {
//        tenPercent.add(nodes.get(i));
//      }
//    }
//    mapVisualiser.addNodes(tenPercent, Color.BLACK);
//
//    mapVisualiser.addNodesContinuous(path, Color.RED);
//    mapVisualiser.show();

    System.out.println("Runnin' Djikstra normal");
    System.out.printf("Going from %d to %d\n", start, end);
    System.out.println("Time spent calculating " + (endTime - startTime) + " ms");

    System.out.println(
        "Driving time: \u001B[1m" + getTimeAsString(l) + " nodes: " + path.size() + "\u001B[0m");

    assert path.size() == 334;
  }

  public static String getTimeAsString(int centiSeconds) {
    StringBuilder sb = new StringBuilder();
    int totalSeconds = centiSeconds / 100;
    int hours = totalSeconds / 3600;
    int minutes = (totalSeconds % 3600) / 60;
    int seconds = (totalSeconds % 3600) % 60;
    return String.format("(h: %d, m: %d, s: %d)", hours, minutes, seconds);
  }


}


class Coordinate {

  private double latitude;
  private double longitude;

  public Coordinate(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public static double getDistance(Coordinate first, Coordinate second) {
    //radius of the earth, 6371km
    int r = 6371;
    return 2 * r * asin(sqrt(
        (pow(sin((first.latitude - second.latitude) / 2), 2)) + cos(first.latitude) * cos(
            second.latitude) * pow(sin((first.longitude - second.longitude) / 2), 2)

    ));
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}


class WeightedEdge {

  WeightedEdge nextEdge;
  int nodePointer;
  int weight;

  public WeightedEdge(WeightedEdge next, int weight, int nextNode) {
    this.nextEdge = next;
    this.weight = weight;
    this.nodePointer = nextNode;
  }
}


class PreProcessor {

  static String[] felt = new String[10]; //Max 10 felt i en linje

  public static ArrayList<Node> getNodeListFromFile(String filePath) {
    ArrayList<Node> nodes = new ArrayList<>();

    try (Stream<String> lines = Files.lines(Path.of(filePath))) {
      lines.skip(1).forEach(splitString -> {
        hsplit(splitString, 3);
        int nodeNumber = Integer.parseInt(felt[0]);
        double latitude = Double.parseDouble(felt[1]);
        double longitude = Double.parseDouble(felt[2].replaceAll("\\s", ""));
        Node node = new Node(nodeNumber, new Coordinate(latitude, longitude));
        nodes.add(node);
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return nodes;
  }

  public static HashMap<Integer, LocationOfInterest> getNodesWithPointsOfInterest(String filePath) {
    HashMap<Integer, LocationOfInterest> map = new HashMap<>();

    String line = "";
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {

      bufferedReader.readLine();

      while ((line = bufferedReader.readLine()) != null) {

        String[] splitString = line.split("\t");
        int nodeNumber = Integer.parseInt(splitString[0]);
        int locationType = Integer.parseInt(splitString[1]);
        map.put(nodeNumber, new LocationOfInterest(splitString[2], locationType));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return map;
  }

  public static void addEdgesToNodes(String path, ArrayList<Node> nodes, boolean reverse) {

    File file = new File(path);
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      int edgeAmount = Integer.parseInt(bufferedReader.readLine().replaceAll("\\s+", ""));

      String line;
      while ((line = bufferedReader.readLine()) != null) {

        hsplit(line, 3);

        int fromNode = Integer.parseInt(felt[0]);
        int toNode = Integer.parseInt(felt[1]);

        if (reverse) {
          int temp = fromNode;
          fromNode = toNode;
          toNode = temp;
        }

        //We care about this one, it is given in 100th's of a second
        int drivingTime = Integer.parseInt(felt[2]);

        nodes.get(fromNode).addEdge(drivingTime, toNode);
      }

      int actualNodeAmount = nodes.stream().mapToInt(node -> node.getEdges().size()).sum();
      System.out.println("Expected: " + edgeAmount + " actual " + actualNodeAmount);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void writeLandmarkToNodes(String filePath, int[] landmarkIndexes,
                                          ArrayList<Node> nodes) {

    Graph graph = new Graph();
    nodes.forEach(node -> node.setEdge(null));
    for (Node n : nodes) {
      if (n.edge != null) {
        System.out.println("er ikke null");
      }
    }
    addEdgesToNodes("src/main/resources/kanter.txt", nodes, false);
    graph.setNodes(nodes);

    try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(filePath))) {

      //skriver antall landemerker og antall noder
      writer.writeInt(landmarkIndexes.length);
      writer.writeInt(nodes.size());
      System.out.println(landmarkIndexes.length);
      System.out.println(nodes.size());
      for (int landmarkIndex : landmarkIndexes) {

        graph.dikstra(landmarkIndex, -1);
        for (Node node : nodes) {
          writer.writeInt(node.weight);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeNodesToLandMark(String filePath, int[] landmarkIndexes,
                                          ArrayList<Node> nodes) {

    Graph reverseGraph = new Graph();
    nodes.forEach(node -> node.setEdge(null));
    for (Node n : nodes) {
      if (n.edge != null) {
        System.out.println("er ikke null");
      }
    }
    addEdgesToNodes("src/main/resources/kanter.txt", nodes, true);
    reverseGraph.setNodes(nodes);

    try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(filePath))) {

      //skriver antall landemerker og antall noder
      writer.writeInt(landmarkIndexes.length);
      writer.writeInt(nodes.size());
      for (int landmarkIndex : landmarkIndexes) {
        reverseGraph.dikstra(landmarkIndex, -1);
        for (Node node : nodes) {
          writer.writeInt(node.weight);
        }
      }


    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  //can be used for both directions
  public static int[][] getPrecalculatedDistances(String filePath) {
    int[][] intArray = null;
    try (FileInputStream fileInputStream = new FileInputStream(
        filePath); DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {

      int landmarks = dataInputStream.readInt();
      int nodes = dataInputStream.readInt();
      IntBuffer intBuffer = ByteBuffer.wrap(fileInputStream.readAllBytes())
          .order(ByteOrder.BIG_ENDIAN).asIntBuffer();
      int[] readIntegers = new int[intBuffer.remaining()];
      intBuffer.get(readIntegers);

      intArray = new int[landmarks][nodes];
      for (int i = 0; i < landmarks; i++) {
        System.arraycopy(readIntegers, i * nodes, intArray[i], 0, nodes);
      }

      return intArray;


    } catch (EOFException e) {
      return intArray;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private static void downloadFile(String fileUrl, String destPath) {
    try (BufferedInputStream in = new BufferedInputStream(
        new URL(fileUrl).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(
        destPath)) {
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void hsplit(String linje, int antall) {
    int j = 0;
    int lengde = linje.length();
    for (int i = 0; i < antall; ++i) {
      while (linje.charAt(j) <= ' ') {
        ++j;
      }

      int ordstart = j;
      while (j < lengde && linje.charAt(j) > ' ') {
        ++j;
      }
      felt[i] = linje.substring(ordstart, j);
    }
  }

}

class Node {

  int id;
  WeightedEdge edge;
  Coordinate coordinate;
  Integer previous;
  boolean visited;
  int weight;

  int distanceFromGoalEstimate;


  public Node(int id, Coordinate coordinate) {
    this.id = id;
    this.edge = null;
    this.coordinate = coordinate;
    this.weight = 0;
    previous = null;
    visited = false;
  }

  public void addEdge(int weight, int to) {
    if (edge == null) {
      this.edge = new WeightedEdge(null, weight, to);
    } else {
      this.edge = new WeightedEdge(this.edge, weight, to);
    }
  }

  public int getHeuristics() {
    return weight + distanceFromGoalEstimate;
  }

  public List<WeightedEdge> getEdges() {
    ArrayList<WeightedEdge> weightedEdges = new ArrayList<>();
    WeightedEdge current = this.edge;
    while (current != null) {
      weightedEdges.add(current);
      current = current.nextEdge;
    }
    return weightedEdges;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public WeightedEdge getEdge() {
    return edge;
  }

  public void setEdge(WeightedEdge edge) {
    this.edge = edge;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public Integer getPrevious() {
    return previous;
  }

  public void setPrevious(Integer previous) {
    this.previous = previous;
  }

  public boolean isVisited() {
    return visited;
  }

  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getDistanceFromGoalEstimate() {
    return distanceFromGoalEstimate;
  }

  public void setDistanceFromGoalEstimate(int distanceFromGoalEstimate) {
    this.distanceFromGoalEstimate = distanceFromGoalEstimate;
  }
}

class MapVisualiser {

  MapWindow mapWindow;
  List<Segment> segments;

  public MapVisualiser() {
    this.mapWindow = new MapWindow();
    segments = new ArrayList<>();
  }

  public void addNodes(List<Node> nodes, Color color) {

    for (Node current : nodes) {
      segments.add(new Segment(

          new Point(current.getCoordinate().getLatitude(), current.getCoordinate().getLongitude()),
          new Point(current.getCoordinate().getLatitude(), current.getCoordinate().getLongitude()),
          color));


    }
    mapWindow.addSegments(segments);

    mapWindow.add(new JLabel(new ImageIcon("src/main/resources/Norge_kart.jpg")));
    mapWindow.setVisible(true);

  }


  public void addNodesContinuous(List<Node> nodes, Color color) {

    if (nodes.size() == 0) {
      return;
    }
    Node previous = nodes.get(0);
    for (int i = 1; i < nodes.size(); i++) {
      Node current = nodes.get(i);
      segments.add(new Segment(

          new Point(current.getCoordinate().getLatitude(), current.getCoordinate().getLongitude()),
          new Point(previous.getCoordinate().getLatitude(),
              previous.getCoordinate().getLongitude()), color));

      previous = current;
    }
    mapWindow.addSegments(segments);

    mapWindow.add(new JLabel(new ImageIcon("src/main/resources/Norge_kart.jpg")));

    mapWindow.setVisible(true);

  }

  public void show() {
    mapWindow.setVisible(true);
  }


}



/**
 * Bit Betyr Eksempler
 * 1 Stedsnavn Trondheim, Moholt, …
 * 2 Bensinstasjon Shell Herlev
 * 4 Ladestasjon Ionity Klett
 * 8 Spisested Restauranter, kafeer, puber
 * 16 Drikkested Barer, puber, nattklubber
 * 32 Overnattingssted Hoteller, moteller, gjestehus
 */
enum LocationType {
  NAMED_LOCATION(1),
  GAS_STATION(2),
  CHARGING_STATION(4),
  EATING_SPOT(8),
  DRINKING_SPOT(16),
  SLEEPING_SPOT(32);


  private final int bit;

  LocationType(int bit){
    this.bit = bit;
  }

  public static boolean isOfType(LocationType desiredType, int givenInteger){
    return ((givenInteger & desiredType.bit) == desiredType.bit);
  }

  @Override
  public String toString() {
    return name();
  }
}

class LocationOfInterest {
  private String name;
  private int locationType;

  public LocationOfInterest(String name, int locationType) {
    this.name = name;
    this.locationType = locationType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getLocationType() {
    return locationType;
  }

  public void setLocationType(int locationType) {
    this.locationType = locationType;
  }
}




class Graph {

  ArrayList<Node> nodes;

  int[][] landmarksToNodes;
  int[][] nodesToLandmarks;

  private final Comparator<Node> dikstraComparator = Comparator.comparingInt(Node::getWeight);

  private final Comparator<Node> altComparator = Comparator.comparingInt(Node::getHeuristics);

  public Graph() {
  }

  public void setLandmarksToNodes(int[][] landmarksToNodes) {
    this.landmarksToNodes = landmarksToNodes;
  }

  public void setNodesToLandmarks(int[][] nodesToLandmarks) {
    this.nodesToLandmarks = nodesToLandmarks;
  }

  public int dikstra(int start, int end) {
    if (end > nodes.size()) {
      throw new IllegalArgumentException("Invalid node searched for");
    }

    for (Node n : nodes) {
      n.setVisited(false);
      n.setWeight(Integer.MAX_VALUE);
      n.setDistanceFromGoalEstimate(0);
    }

    PriorityQueue<Node> pq = new PriorityQueue<>(dikstraComparator);
    Node startNode = nodes.get(start);
    startNode.setWeight(0);
    startNode.setPrevious(-1);
    startNode.setVisited(true);


    pq.offer(startNode);

    int col = 0;
    while (!pq.isEmpty()) {
      col++;
      Node currentNode = pq.poll();

      if (currentNode.getId() == end) {
        System.out.printf("Nodes checked DJI: " + col + " ");
        return currentNode.getWeight();
      }

      int currentNodeId = currentNode.getId();
      currentNode.setVisited(true);

      List<WeightedEdge> weightedEdges = currentNode.getEdges();

      for (WeightedEdge edge : weightedEdges) {
        Node destination = nodes.get(edge.nodePointer);
        if (destination.isVisited()) {
          continue;
        }

        int newDistance = currentNode.getWeight() + edge.weight;

        if (newDistance < destination.getWeight()) {
          destination.setPrevious(currentNodeId);

          destination.setWeight(newDistance);
          pq.offer(destination);
        }
      }
    }
    return Integer.MAX_VALUE;
  }


  public ArrayList<Integer> getClosestLocations(int start, LocationType desiredType, int amount,
                                                HashMap<Integer, LocationOfInterest> locations) {

    for (Node n : nodes) {
      n.setVisited(false);
      n.setWeight(Integer.MAX_VALUE);
    }

    PriorityQueue<Node> pq = new PriorityQueue<>(dikstraComparator);
    nodes.get(start).weight = 0;
    nodes.get(start).setPrevious(-1);

    pq.offer(nodes.get(start));

    ArrayList<Integer> locationOfInterests = new ArrayList<>(amount);

    while (!pq.isEmpty()) {

      Node currentNode = pq.poll();


      int currentNodeId = currentNode.id;

      if (locations.containsKey(currentNodeId)) {
        LocationOfInterest l = locations.get(currentNodeId);
        if (LocationType.isOfType(desiredType, l.getLocationType())) {
          locationOfInterests.add(currentNodeId);
          if (locationOfInterests.size() == amount) {
            return locationOfInterests;
          }
        }
      }
      currentNode.setVisited(true);

      List<WeightedEdge> weightedEdges = currentNode.getEdges();

      for (WeightedEdge edge : weightedEdges) {
        Node destination = nodes.get(edge.nodePointer);
        if (destination.isVisited()) {
          continue;
        }

        int newDistance = currentNode.weight + edge.weight;

        if (newDistance < destination.weight) {
          destination.setPrevious(currentNodeId);

          destination.setWeight(newDistance);
          pq.offer(destination);
        }

      }


    }
    System.out.println("not enough locations");
    return locationOfInterests;
  }

  public ArrayList<Node> reconstructPath(int end) {
    ArrayList<Node> path = new ArrayList<>();
    Node current = nodes.get(end);

    while (true){
      path.add(current);
      if (current.previous == -1){
        break;
      }
      current = nodes.get(current.previous);

    }
    Collections.reverse(path);
    return path;
  }

  //ALT

  public int alt(int start, int end) {
    if (end > nodes.size()) {
      throw new IllegalArgumentException("Invalid node searched for");
    }
    Node startNode = nodes.get(start);
    Node endNode = nodes.get(end);

    for (Node n : nodes) {
      n.setVisited(false);
      n.setWeight(Integer.MAX_VALUE);

    }



    startNode.setWeight(0);
    startNode.setPrevious(-1);
    startNode.setVisited(true);

    int col = 0;
    PriorityQueue<Node> pq = new PriorityQueue<>(altComparator);

    Node currentNode = null;

    pq.offer(startNode);
    while (!pq.isEmpty()) {
      col++;
      currentNode = pq.remove();


      int currentNodeId = currentNode.getId();
      currentNode.setVisited(true);

      if (currentNode.getId() == end) {
        System.out.printf("Nodes checked ALT: " + col + " ");
        return currentNode.getWeight();
      }



      List<WeightedEdge> weightedEdges = currentNode.getEdges();

      for (WeightedEdge edge : weightedEdges) {
        Node destination = nodes.get(edge.nodePointer);
        if (destination.isVisited()) {
          continue;
        }

        int newDistance = currentNode.weight + edge.weight;

        if (newDistance < destination.weight) {
          destination.setPrevious(currentNodeId);
          destination.setWeight(newDistance);

          int currentMax = 0;
          int n = destination.id;

          for (int i = 0; i < landmarksToNodes.length; i++) {
            //destination er nå current node
            int estimate1 = Math.max(landmarksToNodes[i][end] - landmarksToNodes[i][n], 0);
            int estimate2 = nodesToLandmarks[i][n] - nodesToLandmarks[i][end];
            int tempMax = Math.max(estimate1, estimate2);

            if (tempMax > currentMax){
              currentMax = tempMax;
            }
          }

          destination.setDistanceFromGoalEstimate(currentMax);

          while(pq.contains(destination)){
            pq.remove(destination);
          }

          pq.offer(destination);
        }
      }
    }

    return endNode.weight;
  }


  public void setNodes(ArrayList<Node> nodes) {
    nodes.forEach(node -> node.setWeight(Integer.MAX_VALUE));
    this.nodes = nodes;
  }


}






