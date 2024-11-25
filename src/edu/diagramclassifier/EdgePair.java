package edu.diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represents a pair of E-graph graph edges.
 * @author Alexey Tazin 
 */
public class EdgePair {

  // The graph edge 1
  private Edge edge1;

  // The graph edge 2
  private Edge edge2;

  public EdgePair(Edge edge1, Edge edge2) {
    this.edge1 = edge1;
    this.edge2 = edge2;
  }

  public Edge getEdge1() {
    return edge1;
  }

  public Edge getEdge2() {
    return edge2;
  }

}