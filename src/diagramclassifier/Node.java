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

  public Edge getOutgoingEdge(String type) {
     for (Edge edge: outgoingEdgeList)
       if (edge.getType().equals(type))
          return edge;
     return null;
  }

  public AttributeEdge getAttributeEdge(String type) {
     for (AttributeEdge attrEdge: attributeEdgeList)
       if (attrEdge.getType().equals(type))
          return attrEdge;
     return null;
  }

  public static Edge getEdge(Node node1, Node node2, String edgeType) {
    ArrayList<Edge> outgointEdgeList = node1.getOutgoingEdgeList();
    for (Edge edge: outgointEdgeList)
      if (edge.getType().equals(edgeType) && edge.getTarget() == node2)
        return edge; 
    return null;
  }

  public ArrayList<Edge> getOutgoingEdgeList(String type) {
     ArrayList<Edge> resultEdgeList = new ArrayList<Edge>();
     for (Edge edge: outgoingEdgeList)
       if (edge.getType().equals(type))
          resultEdgeList.add(edge);
     return resultEdgeList;
  }

}