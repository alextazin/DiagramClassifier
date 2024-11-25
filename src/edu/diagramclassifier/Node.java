package edu.diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an E-graph graph node.
 * @author Alexey Tazin 
 */
public class Node {

  // The outgoing graph edge list
  private ArrayList<Edge> outgoingEdgeList = new ArrayList<Edge>();

  // The incoming graph edge list
  private ArrayList<Edge> incomingEdgeList = new ArrayList<Edge>();

  // The attribute edge list
  private ArrayList<AttributeEdge> attributeEdgeList = new ArrayList<AttributeEdge>();

  // The graph node type
  private String type;

  // The graph node name
  private String name;

  public Node(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public ArrayList<Edge> getOutgoingEdgeList() {
    return outgoingEdgeList;
  }

  public ArrayList<Edge> getIncomingEdgeList() {
    return incomingEdgeList;
  }

  public ArrayList<AttributeEdge> getAttributeEdgeList() {
    return attributeEdgeList;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

}