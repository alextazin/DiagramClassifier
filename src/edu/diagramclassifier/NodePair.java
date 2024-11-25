package edu.diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an pair of E-graph graph nodes.
 * @author Alexey Tazin 
 */
public class NodePair {

  // The graph node 1
  private Node node1;

  // The graph node 2
  private Node node2;

  public NodePair(Node node1, Node node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  public Node getNode1() {
    return node1;
  }

  public Node getNode2() {
    return node2;
  }

}