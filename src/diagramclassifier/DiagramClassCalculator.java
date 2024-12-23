package diagramclassifier;

import java.io.*;
import java.util.*;
import java.lang.*;
import org.argouml.model.*;
import org.xml.sax.InputSource;
import fi.tkk.ics.jbliss.Graph;
import fi.tkk.ics.jbliss.DefaultReporter;
import fi.tkk.ics.jbliss.Utils;

/**
 * This allows to generate a graph of the complex pattern diagram representing
 * the class of class diagrams with the given class diagram as a member. The 
 * generated graph has cannonical labeling.
 * @author Alexey Tazin 
 */
public class DiagramClassCalculator {

  // The types of elements in an E-graph that represents a class diagram
  private final String[] graphElementTypes = {"Class", 
                                              "Property", 
                                              "Generalization",
                                              "Property",
                                              "Association",
                                              "DataType",
                                              "class",
                                              "ownedAttribute",
                                              "type",
                                              "memberEnd",
                                              "ownedEnd",
                                              "association",
                                              "general",
                                              "specific"};

  /**
   * True if the given class diagram is covered by copies for the given basic 
   * templates.
   */
  private boolean basicPatternCoverage = false;

  public boolean getBasicPatternCoverage() {
    return basicPatternCoverage;
  }

  /**
   * Generates a graph of the complex pattern diagram representing the class 
   * of class diagrams with the given class diagram as a member. The 
   * grenerated graph has cannonical labeling.
   * @param diagramGraph - the class diagram E-graph
   * @return the graph of the complex pattern diagram representing the class 
   *         of class diagrams with the given class diagram as a member
   */
  public Graph<Integer> calculateDiagramClass(DiagramGraph diagramGraph) {

    Graph<Integer> graphClass = null;

    try {

      BasicPatternGraphGenerator basicPatternGraphGenerator = new BasicPatternGraphGenerator();
      basicPatternGraphGenerator.createInstancesOfBasicPatternGraph(diagramGraph, BasicPatternGraphGenerator::createBasicPatternGraph2);
      basicPatternGraphGenerator.createInstancesOfBasicPatternGraph(diagramGraph, BasicPatternGraphGenerator::createBasicPatternGraph3);
      basicPatternGraphGenerator.createInstancesOfBasicPatternGraph(diagramGraph, BasicPatternGraphGenerator::createBasicPatternGraph4);
      basicPatternGraphGenerator.createInstancesOfBasicPatternGraph(diagramGraph, BasicPatternGraphGenerator::createBasicPatternGraph1);

      ArrayList<Edge> diagramEdgeList = diagramGraph.getEdgeList();
      ArrayList<Node> diagramNodeList = diagramGraph.getNodeList();

      ArrayList<DiagramGraph> basicPatternGraphList = basicPatternGraphGenerator.getBasicPatternList();

      ArrayList<Stack<NodePair>> diagramCoverageNodeMappingList = basicPatternGraphGenerator.getDiagramCoverageNodeMappingList();
      ArrayList<Stack<NodePair>> diagramCoverageEdgeMappingList = basicPatternGraphGenerator.getDiagramCoverageEdgeMappingList();

      basicPatternCoverage = true;
      for (Node node : diagramNodeList)
        if (!isMappedToBasicPatternNode(diagramCoverageNodeMappingList, node)) {
          basicPatternCoverage = false;
          return null;
        }
      for (Edge edge : diagramEdgeList)
        if (!isMappedToBasicPatternEdge(diagramCoverageEdgeMappingList, edge)) {
          basicPatternCoverage = false;
          return null;
        }

      renameBasicPattenGraphElements(diagramGraph, diagramCoverageNodeMappingList, basicPatternGraphList);
 
      DiagramGraph complexPatternGraph = createComplexPatternGraph(basicPatternGraphList);

      ArrayList<ColorGraphNode> colorGraphNodeList1 = new ArrayList<ColorGraphNode>();
      ArrayList<ColorGraphEdge> colorGraphEdgeList1 = new ArrayList<ColorGraphEdge>();
      createColorGraph(diagramNodeList, diagramEdgeList, colorGraphNodeList1, colorGraphEdgeList1);

      ArrayList<ColorGraphNode> colorGraphNodeList2 = new ArrayList<ColorGraphNode>();
      ArrayList<ColorGraphEdge> colorGraphEdgeList2 = new ArrayList<ColorGraphEdge>();
      createColorGraph(complexPatternGraph.getNodeList(), complexPatternGraph.getEdgeList(), colorGraphNodeList2, colorGraphEdgeList2);

      Graph<String> g1 = new Graph<String>();
      for (ColorGraphNode colorGraphNode : colorGraphNodeList1) 
	g1.add_vertex(colorGraphNode.getName(), colorGraphNode.getColor());
      for (ColorGraphEdge colorGraphEdge : colorGraphEdgeList1)
	g1.add_edge(colorGraphEdge.getNode1().getName(), colorGraphEdge.getNode2().getName());
      g1.find_automorphisms(null, null);
      Map<String,Integer> canlab1 = g1.canonical_labeling();
      Graph<Integer> g_canform1 = g1.relabel(canlab1);

      Graph<String> g2 = new Graph<String>();
      for (ColorGraphNode colorGraphNode : colorGraphNodeList2) 
        g2.add_vertex(colorGraphNode.getName(), colorGraphNode.getColor());
      for (ColorGraphEdge colorGraphEdge : colorGraphEdgeList2)
        g2.add_edge(colorGraphEdge.getNode1().getName(), colorGraphEdge.getNode2().getName());
      g2.find_automorphisms(null, null);
      Map<String,Integer> canlab2 = g2.canonical_labeling();
      graphClass = g2.relabel(canlab2);

      if (g_canform1.compareTo(graphClass) != 0)
        return null;

    } catch (Exception e) {

      e.printStackTrace();
      return null;

    }

    return graphClass;

  }

  /**
   * Generates a color graph where nodes represent graph nodes and graph edges of the given E-graph. 
   * @param nodeList - the E-graph graph nodes
   *        edgeList - the E-graph graph edges
   *        colorGraphNodeList - the color graph nodes
   *        colorGraphEdgeList - the color graph edges
   */
  public static void createColorGraph(ArrayList<Node> nodeList,
                                      ArrayList<Edge> edgeList,
                                      ArrayList<ColorGraphNode> colorGraphNodeList, 
                                      ArrayList<ColorGraphEdge> colorGraphEdgeList) {
    int colorGraphNodeCount = 1;
    for (Node node: nodeList) { 
      ColorGraphNode colorGraphNode = new ColorGraphNode("v" + colorGraphNodeCount, getColor(node.getType())); 
      colorGraphNodeCount++; 
      colorGraphNodeList.add(colorGraphNode);
    }
    for (Edge edge: edgeList) {   
      Node node1 = edge.getSource();
      Node node2 = edge.getTarget();
      ColorGraphNode colorGraphNode1 = getColorGraphNode(nodeList, colorGraphNodeList, node1);
      ColorGraphNode colorGraphNode2 = getColorGraphNode(nodeList, colorGraphNodeList, node2); 
      ColorGraphNode colorGraphNode3 = new ColorGraphNode("v" + colorGraphNodeCount, getColor(edge.getType())); 
      colorGraphNodeList.add(colorGraphNode3);  
      colorGraphNodeCount++; 
      ColorGraphEdge colorGraphEdge1 = new ColorGraphEdge(colorGraphNode3, colorGraphNode2);
      ColorGraphEdge colorGraphEdge2 = new ColorGraphEdge(colorGraphNode1, colorGraphNode3); 
      colorGraphEdgeList.add(colorGraphEdge1);
      colorGraphEdgeList.add(colorGraphEdge2);
    }   
  }

  /**
   * Finds a color graph node in the list of generated color graph nodes 
   * represeting E-graph graph nodes that corresponds to the given E-graph 
   * graph node. The color graph nodes representing the E-graph graph nodes 
   * have the same order as the E-graph graph nodes.
   * @param nodeList - the E-graph graph nodes
   *        colorGraphNodeList - the generated color graph nodes represeting 
   *        E-graph graph nodes
   *        node - the E-graph graph node
   * @return the color graph node form the list of generated color graph nodes 
   *         represeting E-graph graph nodes that corresponds to the given 
   *         E-graph graph node; null if there is no such color graph node
   */
  public static ColorGraphNode getColorGraphNode(ArrayList<Node> nodeList, ArrayList<ColorGraphNode> colorGraphNodeList, Node node) {
    for (int i = 0; i < nodeList.size(); i++)
      if (nodeList.get(i) == node)
        return colorGraphNodeList.get(i);
    return null;
  }

  /**
   * Returns the color (represented as an integer value) assigned to the given
   * type of element in an E-graph representing a class diagram.
   * @param graphElementType - the type of element of an E-graph 
   *        representing a class diagram
   * @return the color (represented as an integer value) assigned to the given
   *         type of element in an E-graph representing a class diagram; -1 if
   *         the given type is invalid
   */
  public static int getColor(String graphElementType) {
    for (int i = 0; i < graphElementTypes.length; i++)
      if (graphElementTypes[i].equals(graphElementType))
        return i;
    return -1;
  } 

  /**
   * Returns a list of preimage graph nodes from the given list of graph node 
   * mappings that are equal to the given graph node. 
   * @param nodeMappingList - the list of graph node mappings
   *        node - the graph node
   * @return the list of preimage graph nodes from the given list of graph node 
   *         mappings that are equal to the given graph node
   */
  public static ArrayList<Node> getPreImageList(ArrayList<Stack<NodePair>> nodeMappingList, Node node) {
    ArrayList<Node> nodePreImageList = new ArrayList<Node>();
    for (Stack<NodePair> nodeMapping: nodeMappingList) 
      for (NodePair nodePair: nodeMapping)
        if (nodePair.getNode2() == node)
           nodePreImageList.add(nodePair.getNode1());
     return nodePreImageList;
  }

  /**
   * Creates a complex template E-graph from the given list of basic template E-graphs.
   * @param basicPatternGraphList - the list of basic template E-graphs
   * @return the complex template E-graph
   */
  public static DiagramGraph createComplexPatternGraph(ArrayList<DiagramGraph> basicPatternGraphList) {
     DiagramGraph complexPatternGraph2 = null;
     for (int i = 1; i < basicPatternGraphList.size(); i++) {
        DiagramGraph complexPatternGraph1 = null;
        if (complexPatternGraph2 == null) 
          complexPatternGraph1 = basicPatternGraphList.get(0);
        else
          complexPatternGraph1 = complexPatternGraph2;
        DiagramGraph basicPatternGraph = basicPatternGraphList.get(i);
        complexPatternGraph2 = extendComplexPatternGraph(complexPatternGraph1, basicPatternGraph);
        if (complexPatternGraph2 == null)
          return null;
     }
     return complexPatternGraph2;
  }

  /**
   * Finds a graph node in the list of graph nodes that is equivalent to the 
   * given graph node. The equivalence is determined by name.
   * @param nodeList - the list of graph nodes
   *        node - the graph node
   * @return the graph node from the list of graph nodes that is equivalent 
   *         to the given graph node; null if the equivalent graph node is
   *         not found
   */
  public static Node getEquivalentNode(ArrayList<Node> nodeList, Node node) {
    for (Node element: nodeList)
      if (element.getName().equals(node.getName()))
        return element;
    return null;
  }

  /**
   * Finds a graph edge in the list of graph edge that is equivalent to the 
   * given graph edge. The equivalence is determined by edge type and names 
   * of source and target.
   * @param edgeList - the list of graph edges
   *        edge - the graph edge
   * @return the graph edge from the list of graph edges that is equivalent 
   *         to the given graph edge; null if the equivalent graph edge 
   *         is not found
   */
  public static Edge getEquivalentEdge(ArrayList<Edge> edgeList, Edge edge) {
    for (Edge element: edgeList)
      if (element.getType().equals(edge.getType()) &&
          element.getSource().getName().equals(edge.getSource().getName()) &&
          element.getTarget().getName().equals(edge.getTarget().getName()))
        return element;
    return null;
  }

  /**
   * Finds a graph node pair in the given graph node mapping where the
   * image graph node equals to the given graph node. 
   * @param nodeMapping - the graph node mapping
   *        node - the graph node
   * @return the graph node pair from the given graph node mapping where the
   *         image graph node equals to the given graph node; null if no such 
   *         graph node pair is found
   */
  public static NodePair getNodePairByImage(ArrayList<NodePair> nodeMapping, Node node) {
    ArrayList<Node> nodeImageList = new ArrayList<Node>();
    for (NodePair nodePair: nodeMapping)
      if (nodePair.getNode2() == node)
        return nodePair;
     return null;
  }

  public static EdgePair getEdgePairByImage(ArrayList<EdgePair> edgeMapping, Edge edge) {
    ArrayList<Edge> edgeImageList = new ArrayList<Edge>();
    for (EdgePair edgePair: edgeMapping)
      if (edgePair.getEdge2() == edge)
        return edgePair;
     return null;
  }

  public static DataNodePair getDataNodePairByImage(ArrayList<DataNodePair> dataNodeMapping, DataNode dataNode) {
    ArrayList<DataNode> dataNodeImageList = new ArrayList<DataNode>();
    for (DataNodePair dataNodePair: dataNodeMapping)
      if (dataNodePair.getDataNode2() == dataNode)
        return dataNodePair;
     return null;
  }

  public static AttributeEdgePair getAttributeEdgePairByImage(ArrayList<AttributeEdgePair> attrEdgeMapping, AttributeEdge attrEdge) {
    ArrayList<AttributeEdge> attrEdgeImageList = new ArrayList<AttributeEdge>();
    for (AttributeEdgePair attrEdgePair: attrEdgeMapping)
      if (attrEdgePair.getAttributeEdge2() == attrEdge)
        return attrEdgePair;
     return null;
  }

  /**
   * Finds a graph node pair in the given graph node mapping where the
   * preimage graph node equals to the given graph node. 
   * @param nodeMapping - the graph node mapping
   *        node - the graph node
   * @return the graph node pair from the given graph node mapping where the
   *         preimage graph node equals to the given graph node; null if no such 
   *         graph node pair is found
   */
  public static NodePair getNodePairByPreImage(ArrayList<NodePair> nodeMapping, Node node) {
    ArrayList<Node> nodeImageList = new ArrayList<Node>();
    for (NodePair nodePair: nodeMapping)
      if (nodePair.getNode1() == node)
        return nodePair;
     return null;
  }


  public static EdgePair getEdgePairByPreImage(ArrayList<EdgePair> edgeMapping, Edge edge) {
    ArrayList<Edge> edgeImageList = new ArrayList<Edge>();
    for (EdgePair edgePair: edgeMapping)
      if (edgePair.getEdge1() == edge)
        return edgePair;
     return null;
  }

  public static DataNodePair getDataNodePairByPreImage(ArrayList<DataNodePair> dataNodeMapping, DataNode dataNode) {
    ArrayList<DataNode> dataNodeImageList = new ArrayList<DataNode>();
    for (DataNodePair dataNodePair: dataNodeMapping)
      if (dataNodePair.getDataNode1() == dataNode)
        return dataNodePair;
     return null;
  }


  public static AttributeEdgePair getAttributeEdgePairByPreImage(ArrayList<AttributeEdgePair> attrEdgeMapping, AttributeEdge attrEdge) {
    ArrayList<AttributeEdge> attrEdgeImageList = new ArrayList<AttributeEdge>();
    for (AttributeEdgePair attrEdgePair: attrEdgeMapping)
      if (attrEdgePair.getAttributeEdge1() == attrEdge)
        return attrEdgePair;
     return null;
  }

  /**
   * Finds a data node in the list of data nodes that is equivalent to the 
   * given data node. The equivalence is determined by name.
   * @param dataNodeList - the list of data nodes
   *        dataNode - the graph node
   * @return the data node from the list of data nodes that is equivalent 
   *         to the given data node; null if the equivalent data node is
   *         not found
   */
  public static DataNode getEquivalentDataNode(ArrayList<DataNode> dataNodeList, DataNode dataNode) {
    for (DataNode element: dataNodeList)
      if (element.getName().equals(dataNode.getName()))
        return element;
    return null;
  }

  /**
   * Finds an attribute edge in the list of attribute edges that is equivalent 
   * to the given attribute edge. The equivalence is determined by attribute 
   * edge type and names of source and target.
   * @param attrAdgeList - the list of attribute edges
   *        attrEdge - the attribute edge
   * @return the attribute edge from the list of attribute edges that is 
   *         equivalent to the given attribute edge; null if the equivalent 
   *         attribute edge is not found
   */
  public static AttributeEdge getEquivalentAttributeEdge(ArrayList<AttributeEdge> attrEdgeList, AttributeEdge attrEdge) {
    for (AttributeEdge element: attrEdgeList)
      if (element.getType().equals(attrEdge.getType()) &&
          element.getSource().getName().equals(attrEdge.getSource().getName()) &&
          element.getTarget().getName().equals(attrEdge.getTarget().getName()))
        return element;
    return null;
  }

  /**
   * Extends the given complex template E-graph with the basic template E-graph.
   * @param complexPatternGraph - the complex template E-graph
   *        basicPatternGraph - the basic template E-graph
   * @return the extended complex pattern E-graph
   */
  public static DiagramGraph extendComplexPatternGraph(DiagramGraph complexPatternGraph, DiagramGraph basicPatternGraph) {

     ArrayList<Node> complexPatternNodeList = complexPatternGraph.getNodeList();
     ArrayList<Node> basicPatternNodeList = basicPatternGraph.getNodeList();
     ArrayList<Edge> complexPatternEdgeList = complexPatternGraph.getEdgeList();
     ArrayList<Edge> basicPatternEdgeList = basicPatternGraph.getEdgeList();
     ArrayList<DataNode> complexPatternDataNodeList = complexPatternGraph.getDataNodeList();
     ArrayList<DataNode> basicPatternDataNodeList = basicPatternGraph.getDataNodeList();
     ArrayList<AttributeEdge> complexPatternAttrEdgeList = complexPatternGraph.getAttributeEdgeList();
     ArrayList<AttributeEdge> basicPatternAttrEdgeList = basicPatternGraph.getAttributeEdgeList();
     DiagramGraph resultComplexPatternGraph = new DiagramGraph();
     ArrayList<Node> resultComplexPatternGraphNodeList = resultComplexPatternGraph.getNodeList();
     ArrayList<Edge> resultComplexPatternGraphEdgeList = resultComplexPatternGraph.getEdgeList();
     ArrayList<DataNode> resultComplexPatternGraphDataNodeList = resultComplexPatternGraph.getDataNodeList();
     ArrayList<AttributeEdge> resultComplexPatternGraphAttrEdgeList = resultComplexPatternGraph.getAttributeEdgeList();
     HashMap<String, Node> resultComplexPatternGraphNodeMap = new HashMap<>();
     HashMap<String, DataNode> resultComplexPatternGraphDataNodeMap = new HashMap<>();
     DiagramGraph qGraph = new DiagramGraph();
     ArrayList<Node> qGraphNodeList = qGraph.getNodeList();
     ArrayList<Edge> qGraphEdgeList = qGraph.getEdgeList();
     ArrayList<DataNode> qGraphDataNodeList = qGraph.getDataNodeList();
     ArrayList<AttributeEdge> qGraphAttrEdgeList = qGraph.getAttributeEdgeList();
     HashMap<String, Node> qGraphNodeMap = new HashMap<>();
     HashMap<String, DataNode> qGraphDataNodeMap = new HashMap<>();
     ArrayList<NodePair> h1NodeMapping = new ArrayList<NodePair>();
     ArrayList<EdgePair> h1EdgeMapping = new ArrayList<EdgePair>();
     ArrayList<DataNodePair> h1DataNodeMapping = new ArrayList<DataNodePair>();
     ArrayList<AttributeEdgePair> h1AttrEdgeMapping = new ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> h2NodeMapping = new ArrayList<NodePair>();
     ArrayList<EdgePair> h2EdgeMapping = new ArrayList<EdgePair>();
     ArrayList<DataNodePair> h2DataNodeMapping = new ArrayList<DataNodePair>();
     ArrayList<AttributeEdgePair> h2AttrEdgeMapping = new ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> g1NodeMapping = new ArrayList<NodePair>();
     ArrayList<EdgePair> g1EdgeMapping = new ArrayList<EdgePair>();
     ArrayList<DataNodePair> g1DataNodeMapping = new ArrayList<DataNodePair>();
     ArrayList<AttributeEdgePair> g1AttrEdgeMapping = new ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> g2NodeMapping = new ArrayList<NodePair>();
     ArrayList<EdgePair> g2EdgeMapping = new ArrayList<EdgePair>();
     ArrayList<DataNodePair> g2DataNodeMapping = new ArrayList<DataNodePair>();
     ArrayList<AttributeEdgePair> g2AttrEdgeMapping = new ArrayList<AttributeEdgePair>();

     for (Node node1: complexPatternNodeList) {
       Node node2 = new Node(node1.getName(), node1.getType());
       resultComplexPatternGraphNodeList.add(node2);
       NodePair nodePair = new NodePair(node1, node2);
       h1NodeMapping.add(nodePair);
     }
     for (Node node1: basicPatternNodeList) {
       Node node2 = getEquivalentNode(complexPatternNodeList, node1);
       if (node2 == null) {
         Node node3 = new Node(node1.getName(), node1.getType());
         resultComplexPatternGraphNodeList.add(node3);
         NodePair nodePair = new NodePair(node1, node3);
         h2NodeMapping.add(nodePair);
       } else {
         NodePair nodePair1 = getNodePairByPreImage(h1NodeMapping, node2);
         NodePair nodePair2 = new NodePair(node1, nodePair1.getNode2());
         h2NodeMapping.add(nodePair2);         
       }
     }
     for (Node node: resultComplexPatternGraphNodeList)
       resultComplexPatternGraphNodeMap.put(node.getName(), node);
     for (Edge edge1: complexPatternEdgeList) { 
       Node sourceNode  = resultComplexPatternGraphNodeMap.get(edge1.getSource().getName());
       Node targetNode  = resultComplexPatternGraphNodeMap.get(edge1.getTarget().getName());
       Edge edge2 = new Edge(null, edge1.getType(), sourceNode, targetNode);
       resultComplexPatternGraphEdgeList.add(edge2);
       ArrayList<Edge> outgoingEdgeList = sourceNode.getOutgoingEdgeList();
       outgoingEdgeList.add(edge2);
       ArrayList<Edge> incomingEdgeList = targetNode.getIncomingEdgeList();
       incomingEdgeList.add(edge2);
       EdgePair edgePair = new EdgePair(edge1, edge2);
       h1EdgeMapping.add(edgePair);
     }
     for (Edge edge1: basicPatternEdgeList) {
       Edge edge2 = getEquivalentEdge(complexPatternEdgeList, edge1);
       if (edge2 == null) {
         Node sourceNode = resultComplexPatternGraphNodeMap.get(edge1.getSource().getName());
         Node targetNode = resultComplexPatternGraphNodeMap.get(edge1.getTarget().getName());
         Edge edge3 = new Edge(null, edge1.getType(), sourceNode, targetNode);
         resultComplexPatternGraphEdgeList.add(edge3);
         ArrayList<Edge> outgoingEdgeList = sourceNode.getOutgoingEdgeList();
         outgoingEdgeList.add(edge3);
         ArrayList<Edge> incomingEdgeList = targetNode.getIncomingEdgeList();
         incomingEdgeList.add(edge3);
         EdgePair edgePair = new EdgePair(edge1, edge3);
         h2EdgeMapping.add(edgePair);
       } else {
         EdgePair edgePair1 = getEdgePairByPreImage(h1EdgeMapping, edge2);
         EdgePair edgePair2 = new EdgePair(edge1, edgePair1.getEdge2());
         h2EdgeMapping.add(edgePair2);         
       }
     }

     for (Node node1: resultComplexPatternGraphNodeList) {
        NodePair nodePair1 = getNodePairByImage(h1NodeMapping, node1);
        NodePair nodePair2 = getNodePairByImage(h2NodeMapping, node1);
        if (nodePair1 != null && nodePair2 != null) {
           Node node2 = new Node(nodePair1.getNode1().getName(), null);
           qGraphNodeList.add(node2);
           NodePair nodePair3 = new NodePair(node2, nodePair1.getNode1());
           g1NodeMapping.add(nodePair3);
           NodePair nodePair4 = new NodePair(node2, nodePair2.getNode1());
           g2NodeMapping.add(nodePair4);
        }
     }
     for (Node node: qGraphNodeList)
       qGraphNodeMap.put(node.getName(), node);
     for (Edge edge1: resultComplexPatternGraphEdgeList) {
        EdgePair edgePair1 = getEdgePairByImage(h1EdgeMapping, edge1);
        EdgePair edgePair2 = getEdgePairByImage(h2EdgeMapping, edge1);
        if (edgePair1 != null && edgePair2 != null) {
           Node sourceNode  = qGraphNodeMap.get(edgePair1.getEdge1().getSource().getName());
           Node targetNode  = qGraphNodeMap.get(edgePair1.getEdge1().getTarget().getName());
           Edge edge2 = new Edge(null, null, sourceNode, targetNode);
           qGraphEdgeList.add(edge2);
           ArrayList<Edge> outgoingEdgeList = sourceNode.getOutgoingEdgeList();
           outgoingEdgeList.add(edge2);
           ArrayList<Edge> incomingEdgeList = targetNode.getIncomingEdgeList();
           incomingEdgeList.add(edge2);
           EdgePair edgePair3 = new EdgePair(edge2, edgePair1.getEdge1());
           g1EdgeMapping.add(edgePair3);
           EdgePair edgePair4 = new EdgePair(edge2, edgePair2.getEdge1());
           g2EdgeMapping.add(edgePair4);
        }
     }

     for (DataNode dataNode1: complexPatternDataNodeList) {
       DataNode dataNode2 = new DataNode(dataNode1.getValue(), dataNode1.getType(), dataNode1.getName());
       resultComplexPatternGraphDataNodeList.add(dataNode2);
       DataNodePair dataNodePair = new DataNodePair(dataNode1, dataNode2);
       h1DataNodeMapping.add(dataNodePair);
     }
     for (DataNode dataNode1: basicPatternDataNodeList) {
       DataNode dataNode2 = getEquivalentDataNode(complexPatternDataNodeList, dataNode1);
       if (dataNode2 == null) {
         DataNode dataNode3 = new DataNode(dataNode1.getValue(), dataNode1.getType(), dataNode1.getName());
         resultComplexPatternGraphDataNodeList.add(dataNode3);
         DataNodePair dataNodePair = new DataNodePair(dataNode1, dataNode3);
         h2DataNodeMapping.add(dataNodePair);
       } else {
         DataNodePair dataNodePair1 = getDataNodePairByPreImage(h1DataNodeMapping, dataNode2);
         DataNodePair dataNodePair2 = new DataNodePair(dataNode1, dataNodePair1.getDataNode2());
         h2DataNodeMapping.add(dataNodePair2);         
       }
     }
     for (DataNode dataNode: resultComplexPatternGraphDataNodeList) {
       resultComplexPatternGraphDataNodeMap.put(dataNode.getName(), dataNode);
     }
     for (AttributeEdge attrEdge1: complexPatternAttrEdgeList) { 
       Node sourceNode  = resultComplexPatternGraphNodeMap.get(attrEdge1.getSource().getName());
       DataNode targetDataNode  = resultComplexPatternGraphDataNodeMap.get(attrEdge1.getTarget().getName());
       AttributeEdge attrEdge2 = new AttributeEdge(null, attrEdge1.getType(), sourceNode, targetDataNode);
       resultComplexPatternGraphAttrEdgeList.add(attrEdge2);
       ArrayList<AttributeEdge> attrEdgeList = sourceNode.getAttributeEdgeList();
       attrEdgeList.add(attrEdge2);
       AttributeEdgePair attrEdgePair = new AttributeEdgePair(attrEdge1, attrEdge2);
       h1AttrEdgeMapping.add(attrEdgePair);
     }
     for (AttributeEdge attrEdge1: basicPatternAttrEdgeList) {
       AttributeEdge attrEdge2 = getEquivalentAttributeEdge(complexPatternAttrEdgeList, attrEdge1);
       if (attrEdge2 == null) {
         Node sourceNode = resultComplexPatternGraphNodeMap.get(attrEdge1.getSource().getName());
         DataNode targetDataNode = resultComplexPatternGraphDataNodeMap.get(attrEdge1.getTarget().getName());
         AttributeEdge attrEdge3 = new AttributeEdge(null, attrEdge1.getType(), sourceNode, targetDataNode);
         resultComplexPatternGraphAttrEdgeList.add(attrEdge3);
         ArrayList<AttributeEdge> attrEdgeList = sourceNode.getAttributeEdgeList();
         attrEdgeList.add(attrEdge3);
         AttributeEdgePair attrEdgePair = new AttributeEdgePair(attrEdge1, attrEdge3);
         h2AttrEdgeMapping.add(attrEdgePair);
       } else {
         AttributeEdgePair attrEdgePair1 = getAttributeEdgePairByPreImage(h1AttrEdgeMapping, attrEdge2);
         AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(attrEdge1, attrEdgePair1.getAttributeEdge2());
         h2AttrEdgeMapping.add(attrEdgePair2);         
       }
     }

     for (DataNode dataNode1: resultComplexPatternGraphDataNodeList) {
        DataNodePair dataNodePair1 = getDataNodePairByImage(h1DataNodeMapping, dataNode1);
        DataNodePair dataNodePair2 = getDataNodePairByImage(h2DataNodeMapping, dataNode1);
        if (dataNodePair1 != null && dataNodePair2 != null) {
           DataNode dataNode2 = new DataNode(dataNodePair1.getDataNode1().getValue(), null, dataNodePair1.getDataNode1().getName());
           qGraphDataNodeList.add(dataNode2);
           DataNodePair dataNodePair3 = new DataNodePair(dataNode2, dataNodePair1.getDataNode1());
           g1DataNodeMapping.add(dataNodePair3);
           DataNodePair dataNodePair4 = new DataNodePair(dataNode2, dataNodePair2.getDataNode1());
           g2DataNodeMapping.add(dataNodePair4);
        }
     }
     for (DataNode dataNode: qGraphDataNodeList)
       qGraphDataNodeMap.put(dataNode.getName(), dataNode);
     for (AttributeEdge attrEdge1: resultComplexPatternGraphAttrEdgeList) {
        AttributeEdgePair attrEdgePair1 = getAttributeEdgePairByImage(h1AttrEdgeMapping, attrEdge1);
        AttributeEdgePair attrEdgePair2 = getAttributeEdgePairByImage(h2AttrEdgeMapping, attrEdge1);
        if (attrEdgePair1 != null && attrEdgePair2 != null) {
           Node sourceNode  = qGraphNodeMap.get(attrEdgePair1.getAttributeEdge1().getSource().getName());
           DataNode targetDataNode  = qGraphDataNodeMap.get(attrEdgePair1.getAttributeEdge1().getTarget().getName());
           AttributeEdge attrEdge2 = new AttributeEdge(null, null, sourceNode, targetDataNode);
           qGraphAttrEdgeList.add(attrEdge2);
           ArrayList<AttributeEdge> attrEdgeList = sourceNode.getAttributeEdgeList();
           attrEdgeList.add(attrEdge2);
           AttributeEdgePair attrEdgePair3 = new AttributeEdgePair(attrEdge2, attrEdgePair1.getAttributeEdge1());
           g1AttrEdgeMapping.add(attrEdgePair3);
           AttributeEdgePair attrEdgePair4 = new AttributeEdgePair(attrEdge2, attrEdgePair2.getAttributeEdge1());
           g2AttrEdgeMapping.add(attrEdgePair4);
        }
     }

     if (qGraphNodeList.size() != 0) {

       boolean pushOutCommuteExists = true;

       for (Node node: qGraphNodeList) {
         NodePair nodePair1 = getNodePairByPreImage(g1NodeMapping, node);
         NodePair nodePair2 = getNodePairByPreImage(h1NodeMapping, nodePair1.getNode2());
         NodePair nodePair3 = getNodePairByPreImage(g2NodeMapping, node);
         NodePair nodePair4 = getNodePairByPreImage(h2NodeMapping, nodePair3.getNode2());
         if (nodePair2.getNode2() != nodePair4.getNode2())
           pushOutCommuteExists = false;
       }
       for (Edge edge: qGraphEdgeList) {
         EdgePair edgePair1 = getEdgePairByPreImage(g1EdgeMapping, edge);
         EdgePair edgePair2 = getEdgePairByPreImage(h1EdgeMapping, edgePair1.getEdge2());
         EdgePair edgePair3 = getEdgePairByPreImage(g2EdgeMapping, edge);
         EdgePair edgePair4 = getEdgePairByPreImage(h2EdgeMapping, edgePair3.getEdge2());
         if (edgePair2.getEdge2() != edgePair4.getEdge2())
           pushOutCommuteExists = false;
       }
       for (Edge edge: qGraphEdgeList) {
         EdgePair edgePair1 = getEdgePairByPreImage(g1EdgeMapping, edge);
         EdgePair edgePair2 = getEdgePairByPreImage(h1EdgeMapping, edgePair1.getEdge2());
         NodePair nodePair1 = getNodePairByPreImage(g1NodeMapping, edge.getSource());
         NodePair nodePair2 = getNodePairByPreImage(h1NodeMapping, nodePair1.getNode2());
         NodePair nodePair3 = getNodePairByPreImage(g1NodeMapping, edge.getTarget());
         NodePair nodePair4 = getNodePairByPreImage(h1NodeMapping, nodePair3.getNode2());
         if (nodePair2.getNode2() != edgePair2.getEdge2().getSource() ||
             nodePair4.getNode2() != edgePair2.getEdge2().getTarget())
           pushOutCommuteExists = false;
         EdgePair edgePair3 = getEdgePairByPreImage(g2EdgeMapping, edge);
         EdgePair edgePair4 = getEdgePairByPreImage(h2EdgeMapping, edgePair3.getEdge2());
         NodePair nodePair5 = getNodePairByPreImage(g2NodeMapping, edge.getSource());
         NodePair nodePair6 = getNodePairByPreImage(h2NodeMapping, nodePair5.getNode2());
         NodePair nodePair7 = getNodePairByPreImage(g2NodeMapping, edge.getTarget());
         NodePair nodePair8 = getNodePairByPreImage(h2NodeMapping, nodePair7.getNode2());
         if (nodePair6.getNode2() != edgePair4.getEdge2().getSource() ||
             nodePair8.getNode2() != edgePair4.getEdge2().getTarget())
           pushOutCommuteExists = false;
       }

       for (DataNode dataNode: qGraphDataNodeList) {
         DataNodePair dataNodePair1 = getDataNodePairByPreImage(g1DataNodeMapping, dataNode);
         DataNodePair dataNodePair2 = getDataNodePairByPreImage(h1DataNodeMapping, dataNodePair1.getDataNode2());
         DataNodePair dataNodePair3 = getDataNodePairByPreImage(g2DataNodeMapping, dataNode);
         DataNodePair dataNodePair4 = getDataNodePairByPreImage(h2DataNodeMapping, dataNodePair3.getDataNode2());
         if (dataNodePair2.getDataNode2() != dataNodePair4.getDataNode2())
           pushOutCommuteExists = false;
       }
       for (AttributeEdge attrEdge: qGraphAttrEdgeList) {
         AttributeEdgePair attrEdgePair1 = getAttributeEdgePairByPreImage(g1AttrEdgeMapping, attrEdge);
         AttributeEdgePair attrEdgePair2 = getAttributeEdgePairByPreImage(h1AttrEdgeMapping, attrEdgePair1.getAttributeEdge2());
         AttributeEdgePair attrEdgePair3 = getAttributeEdgePairByPreImage(g2AttrEdgeMapping, attrEdge);
         AttributeEdgePair attrEdgePair4 = getAttributeEdgePairByPreImage(h2AttrEdgeMapping, attrEdgePair3.getAttributeEdge2());
         if (attrEdgePair2.getAttributeEdge2() != attrEdgePair4.getAttributeEdge2())
           pushOutCommuteExists = false;
       }
       for (AttributeEdge attrEdge: qGraphAttrEdgeList) {
         AttributeEdgePair attrEdgePair1 = getAttributeEdgePairByPreImage(g1AttrEdgeMapping, attrEdge);
         AttributeEdgePair attrEdgePair2 = getAttributeEdgePairByPreImage(h1AttrEdgeMapping, attrEdgePair1.getAttributeEdge2());
         NodePair nodePair1 = getNodePairByPreImage(g1NodeMapping, attrEdge.getSource());
         NodePair nodePair2 = getNodePairByPreImage(h1NodeMapping, nodePair1.getNode2());
         DataNodePair dataNodePair1 = getDataNodePairByPreImage(g1DataNodeMapping, attrEdge.getTarget());
         DataNodePair dataNodePair2 = getDataNodePairByPreImage(h1DataNodeMapping, dataNodePair1.getDataNode2());
         if (nodePair2.getNode2() != attrEdgePair2.getAttributeEdge2().getSource() ||
             dataNodePair2.getDataNode2() != attrEdgePair2.getAttributeEdge2().getTarget())
           pushOutCommuteExists = false;
         AttributeEdgePair attrEdgePair3 = getAttributeEdgePairByPreImage(g2AttrEdgeMapping, attrEdge);
         AttributeEdgePair attrEdgePair4 = getAttributeEdgePairByPreImage(h2AttrEdgeMapping, attrEdgePair3.getAttributeEdge2());
         NodePair nodePair3 = getNodePairByPreImage(g2NodeMapping, attrEdge.getSource());
         NodePair nodePair4 = getNodePairByPreImage(h2NodeMapping, nodePair3.getNode2());
         DataNodePair dataNodePair3 = getDataNodePairByPreImage(g2DataNodeMapping, attrEdge.getTarget());
         DataNodePair dataNodePair4 = getDataNodePairByPreImage(h2DataNodeMapping, dataNodePair3.getDataNode2());
         if (nodePair4.getNode2() != attrEdgePair4.getAttributeEdge2().getSource() ||
             dataNodePair4.getDataNode2() != attrEdgePair4.getAttributeEdge2().getTarget())
           pushOutCommuteExists = false;
       }

       if (!pushOutCommuteExists)
         return null;

     }

     return resultComplexPatternGraph;

  }

  public static void renameBasicPattenGraphElements(
           DiagramGraph diagramGraph, 
           ArrayList<Stack<NodePair>> diagramCoverageNodeMappingList, 
           ArrayList<DiagramGraph> basicPatternGraphList) {

    int classNodeCount = 1;
    int dataTypeNodeCount = 1;
    ArrayList<Node> diagramClassNodeList = diagramGraph.getNodeList("Class");
    ArrayList<Node> diagramDataTypeNodeList = diagramGraph.getNodeList("DataType");   
    for (Node classNode: diagramClassNodeList) {
      ArrayList<Node> preImageNodeList = getPreImageList(diagramCoverageNodeMappingList, classNode);
      for (Node preImageNode: preImageNodeList) 
        preImageNode.setName("c" + classNodeCount);
      classNodeCount++;
    }
    for (Node dataTypeNode: diagramDataTypeNodeList) {
      ArrayList<Node> preImageNodeList = getPreImageList(diagramCoverageNodeMappingList, dataTypeNode);
      for (Node preImageNode: preImageNodeList)
        preImageNode.setName("dt" + dataTypeNodeCount);
      dataTypeNodeCount++;
    }

    int genNodeCount = 1;
    int assocNodeCount = 1;
    int propNodeCount = 1;
    for (DiagramGraph basicPatternGraph: basicPatternGraphList) {
      ArrayList<Node> basicPatternGenNodeList = basicPatternGraph.getNodeList("Generalization");
      for (Node genNode: basicPatternGenNodeList) {
        genNode.setName("g" + genNodeCount);
        genNodeCount++;
      }
      ArrayList<Node> basicPatternAssocNodeList = basicPatternGraph.getNodeList("Association");
      for (Node assocNode: basicPatternAssocNodeList) {
        assocNode.setName("a" + assocNodeCount);
        assocNodeCount++;
      }
      ArrayList<Node> basicPatternPropNodeList = basicPatternGraph.getNodeList("Property");
      for (Node propNode: basicPatternPropNodeList) {
        propNode.setName("p" + propNodeCount);
        propNodeCount++;
      }
    }

    for (DiagramGraph basicPatternGraph: basicPatternGraphList) {
      ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
      for (Node node: nodeList) {
        ArrayList<AttributeEdge> attrEdgeList = node.getAttributeEdgeList();
        for (AttributeEdge attrEdge: attrEdgeList) {
          DataNode dataNode = attrEdge.getTarget();
          dataNode.setName(dataNode.getValue() + "_" + node.getName() + "_" + attrEdge.getType());
        }
      }
    }

  }

  public static boolean isMappedToBasicPatternNode(ArrayList<Stack<NodePair>> diagramCoverageNodeMappingList, Node node) {
    for (Stack<NodePair> nodeMapping : diagramCoverageNodeMappingList)
      for (NodePair nodePair: nodeMapping)
        if (nodePair.getNode2() == node)
          return true;
    return false;
  }

  public static boolean isMappedToBasicPatternEdge(ArrayList<Stack<EdgePair>> diagramCoverageEdgeMappingList, Edge edge) {
    for (Stack<EdgePair> edgeMapping : diagramCoverageEdgeMappingList)
      for (EdgePair edgePair: edgeMapping)
        if (edgePair.getEdge2() == edge)
          return true;
    return false;
  }

}