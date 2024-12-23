package diagramclassifier;

import java.util.*;
import java.io.*;
import org.argouml.model.*;
import org.xml.sax.InputSource;
import java.util.function.Supplier;

public class BasicPatternGraphGenerator {

  private Stack<NodePair> basicPatternGraphMatchNodeMapping = null;
  private Stack<EdgePair> basicPatternGraphMatchEdgeMapping = null;
  private Stack<DataNodePair> basicPatternGraphMatchDataNodeMapping = null;
  private Stack<AttributeEdgePair> basicPatternGraphMatchAttrEdgeMapping = null;

  ArrayList<Stack<NodePair>> diagramCoverageNodeMappingList = new ArrayList<Stack<NodePair>>();
  ArrayList<Stack<EdgePair>> diagramCoverageEdgeMappingList = new ArrayList<Stack<EdgePair>>();
  ArrayList<Stack<DataNodePair>> diagramCoverageDataNodeMappingList = new ArrayList<Stack<DataNodePair>>();
  ArrayList<Stack<AttributeEdgePair>> diagramCoverageAttrEdgeMappingList = new ArrayList<Stack<AttributeEdgePair>>();

  ArrayList<DiagramGraph> basicPatternGraphList = new ArrayList<DiagramGraph>();

  private int basicPatternNodeMatchCount = 0;

  public ArrayList<DiagramGraph> getBasicPatternList() {
    return basicPatternGraphList;
  }

  public ArrayList<Stack<NodePair>> getDiagramCoverageNodeMappingList() {
    return diagramCoverageNodeMappingList;
  }

  public ArrayList<Stack<EdgePair>> getDiagramCoverageEdgeMappingList() {
    return diagramCoverageEdgeMappingList;
  }

  public void createInstancesOfBasicPatternGraph(DiagramGraph diagramGraph, Supplier<DiagramGraph> createBasicPatternGraphMethodRef) {

    ArrayList<Node> diagramClassNodeList = diagramGraph.getNodeList("Class");

    for (int classNodeCount = 0; classNodeCount < diagramClassNodeList.size(); classNodeCount++) {

      Node classNode = diagramClassNodeList.get(classNodeCount);

      while (true) {
        DiagramGraph basicPatternGraph = createBasicPatternGraphMethodRef.get();
        ArrayList<Node> basicPatternClassNodeList = basicPatternGraph.getNodeList("Class");
        for (Node node: diagramGraph.getNodeList()) 
          node.resetMatchId();
        for (Edge edge: diagramGraph.getEdgeList()) 
          edge.setMatched(false);
        basicPatternGraphMatchNodeMapping = new Stack<NodePair>();
        basicPatternGraphMatchEdgeMapping = new Stack<EdgePair>();
        basicPatternGraphMatchDataNodeMapping = new Stack<DataNodePair>();
        basicPatternGraphMatchAttrEdgeMapping = new Stack<AttributeEdgePair>();
        basicPatternNodeMatchCount = 1;
        classNode.setMatchId(1);
        basicPatternClassNodeList.get(0).setMatchId(1);
        NodePair nodePair = new NodePair(basicPatternClassNodeList.get(0), classNode);
        basicPatternGraphMatchNodeMapping.push(nodePair);
        if (exploreCandidateSubgraphs(classNode, basicPatternClassNodeList.get(0))) {
          diagramCoverageNodeMappingList.add(basicPatternGraphMatchNodeMapping);
          diagramCoverageEdgeMappingList.add(basicPatternGraphMatchEdgeMapping);
          diagramCoverageDataNodeMappingList.add(basicPatternGraphMatchDataNodeMapping);
          diagramCoverageAttrEdgeMappingList.add(basicPatternGraphMatchAttrEdgeMapping);
          basicPatternGraphList.add(basicPatternGraph);
          for (EdgePair edgePair: basicPatternGraphMatchEdgeMapping) {
            Edge edge = edgePair.getEdge2();
            edge.setMapped(true);
          }
        } else 
          break;
      }

    }

  }

  public boolean exploreCandidateSubgraphs(Node diagramNode, Node basicPatternNode) {

    ArrayList<Edge> basicPatternOutgoingEdgeList = basicPatternNode.getOutgoingEdgeList();
    ArrayList<Edge> diagramOutgoingEdgeList = diagramNode.getOutgoingEdgeList();
    if (basicPatternOutgoingEdgeList.size() > diagramOutgoingEdgeList.size())
      return false;
    for (Edge edge1: basicPatternOutgoingEdgeList) {
      boolean nodeMatchFound = false;
      if (edge1.isMatched())
        continue;
      for (Edge edge2: diagramOutgoingEdgeList) {
        if (edge2.isMatched() || edge2.isMapped())
          continue;
        Node node1 = edge1.getTarget();
        Node node2 = edge2.getTarget();
        if (edge1.getType().equals(edge2.getType()) && node1.getType().equals(node2.getType()) && 
            node1.getMatchId() == node2.getMatchId()) {
          if (node1.getMatchId() == 0 && node2.getMatchId() == 0) {
            basicPatternNodeMatchCount++;
            node1.setMatchId(basicPatternNodeMatchCount);
            node2.setMatchId(basicPatternNodeMatchCount);
            edge1.setMatched(true);
            edge2.setMatched(true);
            NodePair nodePair = new NodePair(node1, node2);
            basicPatternGraphMatchNodeMapping.push(nodePair);
            EdgePair edgePair = new EdgePair(edge1, edge2);
            basicPatternGraphMatchEdgeMapping.add(edgePair);
            ArrayList<AttributeEdge> attrEdgeList1 = node1.getAttributeEdgeList();
            ArrayList<AttributeEdge> attrEdgeList2 = node2.getAttributeEdgeList();
            for (AttributeEdge attrEdge1: attrEdgeList1)
              for (AttributeEdge attrEdge2: attrEdgeList2)
                if (attrEdge1.getType().equals(attrEdge2.getType()) && attrEdge1.getTarget().getType().equals(attrEdge2.getTarget().getType())) {
                  DataNodePair dataNodePair = new DataNodePair(attrEdge1.getTarget(), attrEdge2.getTarget());
                  AttributeEdgePair attrEdgePair = new AttributeEdgePair(attrEdge1, attrEdge2);
                  basicPatternGraphMatchDataNodeMapping.push(dataNodePair);
                  basicPatternGraphMatchAttrEdgeMapping.push(attrEdgePair);
                }
            if (exploreCandidateSubgraphs(node2, node1)) {
              nodeMatchFound = true;
              break;
            } else {
              pruneNodeMatchIds(node1);
              pruneEdgeMatchIndicators(edge1);
              pruneDataNodeMapping(node1);
              pruneAttributeEdgeMapping(node1);
              pruneNodeMapping(node1);
              pruneEdgeMapping(edge1);
            }
          } else if (node1.getMatchId() != 0 && node2.getMatchId() != 0) {
            edge1.setMatched(true);
            edge2.setMatched(true);
            EdgePair edgePair = new EdgePair(edge1, edge2);
            basicPatternGraphMatchEdgeMapping.push(edgePair);
            nodeMatchFound = true;
            break;
          }
        }
      }
      if (!nodeMatchFound)
        return false;
    }

    ArrayList<Edge> basicPatternIncomingEdgeList = basicPatternNode.getIncomingEdgeList();
    ArrayList<Edge> diagramIncomingEdgeList = diagramNode.getIncomingEdgeList();
    if (basicPatternIncomingEdgeList.size() > diagramIncomingEdgeList.size()) 
      return false;
    for (Edge edge1: basicPatternIncomingEdgeList) {
      boolean nodeMatchFound = false;
      if (edge1.isMatched())
        continue;
      for (Edge edge2: diagramIncomingEdgeList) {
        if (edge2.isMatched() || edge2.isMapped())
          continue;
        Node node1 = edge1.getSource();
        Node node2 = edge2.getSource();
        if (edge1.getType().equals(edge2.getType()) && node1.getType().equals(node2.getType()) && 
            node1.getMatchId() == node2.getMatchId()) {
          if (node1.getMatchId() == 0 && node2.getMatchId() == 0) {
            basicPatternNodeMatchCount++;
            node1.setMatchId(basicPatternNodeMatchCount);
            node2.setMatchId(basicPatternNodeMatchCount);
            edge1.setMatched(true);
            edge2.setMatched(true);
            NodePair nodePair = new NodePair(node1, node2);
            basicPatternGraphMatchNodeMapping.add(nodePair);
            EdgePair edgePair = new EdgePair(edge1, edge2);
            basicPatternGraphMatchEdgeMapping.add(edgePair);
            ArrayList<AttributeEdge> attrEdgeList1 = node1.getAttributeEdgeList();
            ArrayList<AttributeEdge> attrEdgeList2 = node2.getAttributeEdgeList();
            for (AttributeEdge attrEdge1: attrEdgeList1)
              for (AttributeEdge attrEdge2: attrEdgeList2)
                if (attrEdge1.getType().equals(attrEdge2.getType()) && attrEdge1.getTarget().getType().equals(attrEdge2.getTarget().getType())) {
                  DataNodePair dataNodePair = new DataNodePair(attrEdge1.getTarget(), attrEdge2.getTarget());
                  AttributeEdgePair attrEdgePair = new AttributeEdgePair(attrEdge1, attrEdge2);
                  basicPatternGraphMatchDataNodeMapping.add(dataNodePair);
                  basicPatternGraphMatchAttrEdgeMapping.add(attrEdgePair);
                }
            if (exploreCandidateSubgraphs(node2, node1)) {
              nodeMatchFound = true;
              break;
            } else {
              pruneNodeMatchIds(node1);
              pruneEdgeMatchIndicators(edge1);
              pruneDataNodeMapping(node1);
              pruneAttributeEdgeMapping(node1);
              pruneNodeMapping(node1);
              pruneEdgeMapping(edge1);
            }          
          } else if (node1.getMatchId() != 0 && node2.getMatchId() != 0) {
            edge1.setMatched(true);
            edge2.setMatched(true);
            EdgePair edgePair = new EdgePair(edge1, edge2);
            basicPatternGraphMatchEdgeMapping.add(edgePair);
            nodeMatchFound = true;
            break;
          }
        }
      }
      if (!nodeMatchFound)
        return false;
    }

    return true;

  }

  public void pruneNodeMapping(Node mismatchedBasicPatternNode) {
    while (true) {
      NodePair nodePair = basicPatternGraphMatchNodeMapping.pop();
      if (nodePair.getNode1() == mismatchedBasicPatternNode)
        break;               
    }    
  }
  
  public void pruneEdgeMapping(Edge mismatchedBasicPatternEdge) {
    while (true) {
      EdgePair edgePair = basicPatternGraphMatchEdgeMapping.pop();
      if (edgePair.getEdge1() == mismatchedBasicPatternEdge)
        break;              
    }    
  }

  public void pruneAttributeEdgeMapping(Node mismatchedBasicPatternNode) {
    ArrayList<AttributeEdge> attrEdgeList = mismatchedBasicPatternNode.getAttributeEdgeList();
    for (int i = attrEdgeList.size() - 1; i >= 0; i--)
      while (true) {
        AttributeEdgePair attrEdgePair = basicPatternGraphMatchAttrEdgeMapping.pop();
        if (attrEdgePair.getAttributeEdge1() == attrEdgeList.get(i))
          break;              
      }
  }

  public void pruneDataNodeMapping(Node mismatchedBasicPatternNode) {
    ArrayList<AttributeEdge> attrEdgeList = mismatchedBasicPatternNode.getAttributeEdgeList();
    for (int i = attrEdgeList.size() - 1; i >= 0; i--)
      while (true) {
        DataNodePair dataNodePair = basicPatternGraphMatchDataNodeMapping.pop();
        if (dataNodePair.getDataNode1() == attrEdgeList.get(i).getTarget()) 
          break;
      }
  }

  public void pruneNodeMatchIds(Node mismatchedBasicPatternNode) {
    for (int i = basicPatternGraphMatchNodeMapping.size() - 1; i >= 0; i--) {
      NodePair nodePair = basicPatternGraphMatchNodeMapping.get(i);
      nodePair.getNode1().resetMatchId();
      nodePair.getNode2().resetMatchId();
      if (nodePair.getNode1() == mismatchedBasicPatternNode)
        break;
    }
  }

  public void pruneEdgeMatchIndicators(Edge mismatchedBasicPatternEdge) {
    for (int i = basicPatternGraphMatchEdgeMapping.size() - 1; i >= 0; i--) {
      EdgePair edgePair = basicPatternGraphMatchEdgeMapping.get(i);
      edgePair.getEdge1().setMatched(false);
      edgePair.getEdge2().setMatched(false);
      if (edgePair.getEdge1() == mismatchedBasicPatternEdge)
        break;
    }
  }

  public static DiagramGraph createBasicPatternGraph1() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<AttributeEdge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
    Node classNode = new Node("c1", "Class");
    nodeList.add(classNode);
    Node dataTypeNode = new Node("dt1", "DataType");
    nodeList.add(dataTypeNode);
    Node propNode = new Node("p1", "Property");
    nodeList.add(propNode);
    Edge classEdge = new Edge(null, "class", propNode, classNode);
    Edge ownedAttrEdge = new Edge(null, "ownedAttribute", classNode, propNode);
    Edge typeEdge = new Edge(null, "type", propNode, dataTypeNode);
    edgeList.add(typeEdge);
    edgeList.add(classEdge);
    edgeList.add(ownedAttrEdge);
    ArrayList<Edge> outgoingEdgeList = propNode.getOutgoingEdgeList();
    outgoingEdgeList.add(typeEdge);
    outgoingEdgeList.add(classEdge);
    outgoingEdgeList = classNode.getOutgoingEdgeList();
    outgoingEdgeList.add(ownedAttrEdge);
    ArrayList<Edge> incomingEdgeList = propNode.getIncomingEdgeList();
    incomingEdgeList.add(ownedAttrEdge);
    incomingEdgeList = dataTypeNode.getIncomingEdgeList();
    incomingEdgeList.add(typeEdge);
    incomingEdgeList = classNode.getIncomingEdgeList();
    incomingEdgeList.add(classEdge);
    DataNode dataNode = new DataNode("1", "Integer", null);
    dataNodeList.add(dataNode);
    AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode, dataNode);
    attrEdgeList.add(attrEdge);
    ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("1", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "upper", propNode, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    return basicPatternGraph;
  }

  public static DiagramGraph createBasicPatternGraph2() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<AttributeEdge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
    Node classNode1 = new Node("c1", "Class");
    nodeList.add(classNode1);
    Node classNode2 = new Node("c2", "Class");
    nodeList.add(classNode2);
    Node genNode = new Node("g1", "Generalization");
    nodeList.add(genNode);
    Edge generalEdge = new Edge(null, "general", genNode, classNode2);
    Edge specificEdge = new Edge(null, "specific", genNode, classNode1);
    edgeList.add(generalEdge);
    edgeList.add(specificEdge);
    ArrayList<Edge> outgoingEdgeList = genNode.getOutgoingEdgeList();
    outgoingEdgeList.add(generalEdge);
    outgoingEdgeList.add(specificEdge);
    ArrayList<Edge> incomingEdgeList = classNode1.getIncomingEdgeList();
    incomingEdgeList.add(specificEdge);
    incomingEdgeList = classNode2.getIncomingEdgeList();
    incomingEdgeList.add(generalEdge);
    DataNode dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    AttributeEdge attrEdge = new AttributeEdge(null, "isAbstract", classNode1, dataNode);
    attrEdgeList.add(attrEdge);
    ArrayList<AttributeEdge> outgoingAttrEdgeList = classNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    return basicPatternGraph;
  }

  public static DiagramGraph createBasicPatternGraph3() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<AttributeEdge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
    Node classNode1 = new Node("c1", "Class");
    nodeList.add(classNode1);
    Node classNode2 = new Node("c2", "Class");
    nodeList.add(classNode2);
    Node assocNode = new Node("a1", "Association");
    nodeList.add(assocNode);
    Node propNode1 = new Node("p1", "Property");
    nodeList.add(propNode1);
    Node propNode2 = new Node("p2", "Property");
    nodeList.add(propNode2);
    Edge memberEndEdge1 = new Edge(null, "memberEnd", assocNode, propNode1);
    Edge memberEndEdge2 = new Edge(null, "memberEnd", assocNode, propNode2);
    Edge typeEdge1 = new Edge(null, "type", propNode1, classNode2);
    Edge typeEdge2 = new Edge(null, "type", propNode2, classNode1);
    Edge ownedAttributeEdge1 = new Edge(null, "ownedAttribute", classNode1, propNode1);
    Edge ownedAttributeEdge2 = new Edge(null, "ownedAttribute", classNode2, propNode2);
    Edge assocEdge1 = new Edge(null, "association", propNode1, assocNode);
    Edge assocEdge2 = new Edge(null, "association", propNode2, assocNode);
    Edge classEdge1 = new Edge(null, "class", propNode1, classNode1);
    Edge classEdge2 = new Edge(null, "class", propNode2, classNode2);
    edgeList.add(memberEndEdge1);
    edgeList.add(memberEndEdge2);
    edgeList.add(typeEdge1);
    edgeList.add(typeEdge2);
    edgeList.add(ownedAttributeEdge1);
    edgeList.add(ownedAttributeEdge2);
    edgeList.add(assocEdge1);
    edgeList.add(assocEdge2);
    edgeList.add(classEdge1);
    edgeList.add(classEdge2);
    ArrayList<Edge> outgoingEdgeList = assocNode.getOutgoingEdgeList();
    outgoingEdgeList.add(memberEndEdge1);
    outgoingEdgeList.add(memberEndEdge2);
    outgoingEdgeList = propNode1.getOutgoingEdgeList();
    outgoingEdgeList.add(typeEdge1);
    outgoingEdgeList.add(classEdge1);
    outgoingEdgeList.add(assocEdge1);
    outgoingEdgeList = propNode2.getOutgoingEdgeList();
    outgoingEdgeList.add(typeEdge2);
    outgoingEdgeList.add(classEdge2);
    outgoingEdgeList.add(assocEdge2);
    outgoingEdgeList = classNode1.getOutgoingEdgeList();
    outgoingEdgeList.add(ownedAttributeEdge1);
    outgoingEdgeList = classNode2.getOutgoingEdgeList();
    outgoingEdgeList.add(ownedAttributeEdge2);
    ArrayList<Edge> incomingEdgeList = assocNode.getIncomingEdgeList();
    incomingEdgeList.add(assocEdge1);
    incomingEdgeList.add(assocEdge2);
    incomingEdgeList = propNode1.getIncomingEdgeList();
    incomingEdgeList.add(ownedAttributeEdge1);
    incomingEdgeList.add(memberEndEdge1);
    incomingEdgeList = propNode2.getIncomingEdgeList();
    incomingEdgeList.add(ownedAttributeEdge2);
    incomingEdgeList.add(memberEndEdge2);
    incomingEdgeList = classNode1.getIncomingEdgeList();
    incomingEdgeList.add(typeEdge2);
    incomingEdgeList.add(classEdge1);
    incomingEdgeList = classNode2.getIncomingEdgeList();
    incomingEdgeList.add(typeEdge1);
    incomingEdgeList.add(classEdge2);
    DataNode dataNode = new DataNode("0", "Integer", null);
    dataNodeList.add(dataNode);
    AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode1, dataNode);
    attrEdgeList.add(attrEdge);
    ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("-1", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "upper", propNode1, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("0", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "lower", propNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("-1", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "upper", propNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode1, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    return basicPatternGraph;
  }

  public static DiagramGraph createBasicPatternGraph4() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<AttributeEdge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
    Node classNode1 = new Node("c1", "Class");
    nodeList.add(classNode1); 
    Node classNode2 = new Node("c2", "Class");
    nodeList.add(classNode2);
    Node assocNode = new Node("a1", "Association");
    nodeList.add(assocNode);
    Node propNode1 = new Node("p1", "Property");
    nodeList.add(propNode1);
    Node propNode2 = new Node("p2", "Property");
    nodeList.add(propNode2);
    Edge memberEndEdge1 = new Edge(null, "memberEnd", assocNode, propNode1);
    Edge memberEndEdge2 = new Edge(null, "memberEnd", assocNode, propNode2);
    Edge typeEdge1 = new Edge(null, "type", propNode1, classNode2);
    Edge typeEdge2 = new Edge(null, "type", propNode2, classNode1);
    Edge ownedAttributeEdge1 = new Edge(null, "ownedAttribute", classNode1, propNode1);
    Edge ownedEndEdge = new Edge(null, "ownedEnd", assocNode, propNode2);
    Edge assocEdge1 = new Edge(null, "association", propNode1, assocNode);
    Edge assocEdge2 = new Edge(null, "association", propNode2, assocNode);
    Edge classEdge1 = new Edge(null, "class", propNode1, classNode1);
    edgeList.add(memberEndEdge1);
    edgeList.add(memberEndEdge2);
    edgeList.add(typeEdge1);
    edgeList.add(typeEdge2);
    edgeList.add(ownedAttributeEdge1);
    edgeList.add(ownedEndEdge);
    edgeList.add(assocEdge1);
    edgeList.add(assocEdge2);
    edgeList.add(classEdge1);
    ArrayList<Edge> outgoingEdgeList = assocNode.getOutgoingEdgeList();
    outgoingEdgeList.add(memberEndEdge1);
    outgoingEdgeList.add(memberEndEdge2);
    outgoingEdgeList.add(ownedEndEdge);
    outgoingEdgeList = propNode1.getOutgoingEdgeList();
    outgoingEdgeList.add(typeEdge1);
    outgoingEdgeList.add(classEdge1);
    outgoingEdgeList.add(assocEdge1);
    outgoingEdgeList = propNode2.getOutgoingEdgeList();
    outgoingEdgeList.add(typeEdge2);
    outgoingEdgeList.add(assocEdge2);
    outgoingEdgeList = classNode1.getOutgoingEdgeList();
    outgoingEdgeList.add(ownedAttributeEdge1);
    ArrayList<Edge> incomingEdgeList = assocNode.getIncomingEdgeList();
    incomingEdgeList.add(assocEdge1);
    incomingEdgeList.add(assocEdge2);
    incomingEdgeList = propNode1.getIncomingEdgeList();
    incomingEdgeList.add(ownedAttributeEdge1);
    incomingEdgeList.add(memberEndEdge1);
    incomingEdgeList = propNode2.getIncomingEdgeList();
    incomingEdgeList.add(ownedEndEdge);
    incomingEdgeList.add(memberEndEdge2);
    incomingEdgeList = classNode1.getIncomingEdgeList();
    incomingEdgeList.add(typeEdge2);
    incomingEdgeList.add(classEdge1);
    incomingEdgeList = classNode2.getIncomingEdgeList();
    incomingEdgeList.add(typeEdge1);
    DataNode dataNode = new DataNode("0", "Integer", null);
    dataNodeList.add(dataNode);
    AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode1, dataNode);
    attrEdgeList.add(attrEdge);
    ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("-1", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "upper", propNode1, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("0", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "lower", propNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("-1", "Integer", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "upper", propNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode1, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode1.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    dataNode = new DataNode("false", "Boolean", null);
    dataNodeList.add(dataNode);
    attrEdge = new AttributeEdge(null, "isAbstract", classNode2, dataNode);
    attrEdgeList.add(attrEdge);
    outgoingAttrEdgeList = classNode2.getAttributeEdgeList();
    outgoingAttrEdgeList.add(attrEdge);
    return basicPatternGraph;
  }

}