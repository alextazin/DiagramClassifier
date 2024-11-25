package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an E-graph data node.
 * @author Alexey Tazin 
 */
public class DataNode {

  // The data node type
  private String type;

  // The data node vlue
  private String value;

  // The data node name
  pritate String name;

  public DataNode(String value, String type, String name) {
    this.value = value;
    this.type = type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

}