package net.dheeru.shortcodes.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This solves the problem of snakes and ladder where you are trying to find the fastest way of winning a snakes and
 * ladder game given a deterministic dice.
 */
public class SnakesAndLadders {
  private final int totalCells;
  private final ArrayList<ArrayList<Edge>> toEdges;
  private final ArrayList<ArrayList<Edge>> fromEdges;

  public SnakesAndLadders(final int totalCells) {
    this.totalCells = totalCells;
    this.toEdges = new ArrayList<ArrayList<Edge>>();
    this.fromEdges = new ArrayList<ArrayList<Edge>>();
    for (int i = 0; i < totalCells; i++) {
      this.toEdges.add(new ArrayList<Edge>());
      this.fromEdges.add(new ArrayList<Edge>());
    }
    this.initializeBoard();
  }

  /**
   * Node represents each cell in the game.
   */
  private static class Node {
    private final int value;
    private static final HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

    private Node(final int value) {
      this.value = value;
    }

    private static Node get(final int value) {
      if (!nodes.containsKey(value)) {
        final Node n = new Node(value);
        nodes.put(value, n);
      }

      return nodes.get(value);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof Node)) {
        return false;
      }
      return ((Node) obj).value == this.value;
    }

    @Override
    public int hashCode() {
      return this.value;
    }

    @Override
    public String toString() {
      return this.value + "";
    }
  }

  /**
   * This represents what dice value takes you from which cell in the game to which other cell.
   */
  private static class Edge {
    private final Node startNode;
    private final Node endNode;
    private final int diceValue;

    private Edge(Node startNode, Node endNode, int diceValue) {
      this.startNode = startNode;
      this.endNode = endNode;
      this.diceValue = diceValue;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof Edge)) {
        return false;
      }
      Edge e = (Edge) obj;
      return e.startNode.equals(this.startNode) && e.endNode.equals(this.endNode) && this.diceValue == e.diceValue;
    }

    @Override
    public int hashCode() {
      return this.diceValue + startNode.hashCode() + endNode.hashCode();
    }

    @Override
    public String toString() {
      return "[" + this.startNode + ", " + this.endNode + ", " + this.diceValue + "]";
    }
  }

  private void initializeBoard() {
    for (int i = 0; i < this.totalCells; i++) {
      for (int j = i + 1; j <= i + 6; j++) {
        if (j < this.totalCells) {
          addEdge(i, j, j - i);
        }
      }
    }
  }

  private void addEdge(final int fromCell, final int toCell, final int edgeValue) {
    final Edge e = new Edge(Node.get(fromCell), Node.get(toCell), edgeValue);
    this.fromEdges.get(fromCell).add(e);
    this.toEdges.get(toCell).add(e);
  }

  private void removeEdge(Edge e) {
    this.fromEdges.get(e.startNode.value).remove(e);
    this.toEdges.get(e.endNode.value).remove(e);
  }

  private void readLadders(Scanner sc) {
    final int numLadders = sc.nextInt();
    for (int i = 0; i < numLadders; i++) {
      final int from = sc.nextInt() - 1;
      final int to = sc.nextInt() - 1;
      addLadder(from, to);
    }
  }

  private void addLadder(int from, int to) {
    ArrayList<Edge> edgesToRemove = new ArrayList<>(this.toEdges.get(from));
    for (Edge e : edgesToRemove) {
      removeEdge(e);
      addEdge(e.startNode.value, to, e.diceValue);
    }
  }

  private void readSnakes(Scanner sc) {
    final int numSnakes = sc.nextInt();
    for (int i = 0; i < numSnakes; i++) {
      final int from = sc.nextInt() - 1;
      final int to = sc.nextInt() - 1;
      addSnake(from, to);
    }
  }

  private void addSnake(int from, int to) {
    ArrayList<Edge> edgesToRemove = new ArrayList<>(this.toEdges.get(from));
    for (Edge e : edgesToRemove) {
      removeEdge(e);
      addEdge(e.startNode.value, to, e.diceValue);
    }
  }

  private Map<Node, Integer> findShortestDistances() {
    final Map<Node, Integer> nodeDistances = new HashMap<Node, Integer>();
    final ArrayList<Node> toVisitNodes = new ArrayList<Node>();
    toVisitNodes.add(Node.get(0));
    nodeDistances.put(Node.get(0), 0);

    while (toVisitNodes.size() > 0) {
      Node n = toVisitNodes.remove(0);
      final ArrayList<Edge> edges = this.fromEdges.get(n.value);
      for (Edge e : edges) {
        if(!nodeDistances.containsKey(e.endNode)) {
          nodeDistances.put(e.endNode, nodeDistances.get(n) + 1);
          toVisitNodes.add(e.endNode);
        }
      }
      //print(nodeDistances);
    }

    return nodeDistances;
  }

  public static void main(String args[]) {
    Scanner sc = new Scanner(System.in);
    final int numTests = sc.nextInt();
    for (int i = 0; i < numTests; i++) {
      final SnakesAndLadders board = new SnakesAndLadders(100);
      board.readLadders(sc);
      board.readSnakes(sc);
      final Map<Node, Integer> shortestDistances = board.findShortestDistances();
      //print(shortestDistances);
      if (shortestDistances.containsKey(Node.get(99))) {
        System.out.println(shortestDistances.get(Node.get(99)));
      } else {
        System.out.println("-1");
      }
    }
  }

  private static void print(Map<Node, Integer> shortestDistances) {
    for (int i = 0; i < 100; i++) {
      System.out.println(i + " " + shortestDistances.get(Node.get(i)));
    }
    System.out.println("=======");
  }
}
