package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represents a color graph edge.
 * @author Alexey Tazin 
 */
public class ColorGraphEdge {

  // The node 1
  private ColorGraphNode node1;

  // The node 2
  private ColorGraphNode node2;

  public ColorGraphEdge(ColorGraphNode node1, ColorGraphNode node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  public ColorGraphNode getNode1() {
    return node1;
  }

  public ColorGraphNode getNode2() {
    return node2;
  }

}