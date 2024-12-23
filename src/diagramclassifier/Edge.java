package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an E-graph graph edge.
 * @author Alexey Tazin 
 */
public class Edge {

  // The source graph node
  private Node source;

  // The target graph node
  private Node target;

  // The graph edge type
  private String type;

  // The graph edge name
  private String name;

  private boolean matched = false;

  private boolean mapped = false;

  public Edge(String name, String type, Node source, Node target) {
    this.name = name;
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public Node getSource() {
    return source;
  }

  public Node getTarget() {
    return target;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public void setMatched(boolean matched) {
    this.matched = matched;
  }

  public boolean isMatched() {
    return matched;
  }

  public void setMapped(boolean mapped) {
    this.mapped = mapped;
  }

  public boolean isMapped() {
    return mapped;
  }

}