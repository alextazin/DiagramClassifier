package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represent an E-graph graph.
 * @author Alexey Tazin 
 */
public class DiagramGraph {

   // The data node list
   ArrayList<DataNode> dataNodeList = new ArrayList<DataNode>();

   // The graph edge list
   ArrayList<Edge> edgeList = new ArrayList<Edge>();

   // The attribute edge list
   ArrayList<AttributeEdge> attributeEdgeList = new ArrayList<AttributeEdge>();

   // The graph node list
   ArrayList<Node> nodeList = new ArrayList<Node>();

   public ArrayList<DataNode> getDataNodeList() {
     return  dataNodeList;
   }

   public ArrayList<Edge> getEdgeList() {
     return edgeList;
   }

   public ArrayList<AttributeEdge> getAttributeEdgeList() {
     return attributeEdgeList;
   }

   public ArrayList<Node> getNodeList() {
     return nodeList;
   }

   public ArrayList<Node> getNodeList(String type) {
     ArrayList<Node> resultNodeList = new ArrayList<Node>();
     for (Node node: nodeList)
       if (node.getType().equals(type))
          resultNodeList.add(node);
     return resultNodeList;
   }

}