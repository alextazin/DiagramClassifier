package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an E-graph attribute edge.
 * @author Alexey Tazin 
 */
public class AttributeEdge {

  // The source graph node
  private Node source;

  // The target data node
  private DataNode target;

  // The attribute node type
  private String type;

  // The attribute node name
  private String name;

  public AttributeEdge(String name, String type, Node source, DataNode target) {
    this.name = name;
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public Node getSource() {
    return source;
  }

  public DataNode getTarget() {
    return target;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

}