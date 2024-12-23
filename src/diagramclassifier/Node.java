package diagramclassifier;

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

  private int matchId = 0;

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

  public void setName(String name) {
    this.name = name;
  }

  public void resetMatchId() {
    matchId = 0;
  }

  public void setMatchId(int matchId) {
    this.matchId = matchId;
  }

  public int getMatchId() {
    return matchId;
  }

}