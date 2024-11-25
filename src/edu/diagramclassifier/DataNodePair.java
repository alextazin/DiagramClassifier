package edu.diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represents a pair of E-graph data nodes.
 * @author Alexey Tazin 
 */
public class DataNodePair {

  // The data node 1
  private DataNode dataNode1;

  // The data node 2
  private DataNode dataNode2;

  public NodePair(DataNode dataNode1, DataNode dataNode2) {
    this.dataNode1 = dataNode1;
    this.dataNode2 = dataNode2;
  }

  public DataNode getDataNode1() {
    return dataNode1;
  }

  public DataNode getDataNode2() {
    return dataNode2;
  }

}