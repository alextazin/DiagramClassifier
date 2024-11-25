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
   * patterns.
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
     
      ArrayList<DataNode> diagramGraphDataNodeList = diagramGraph.getDataNodeList();
      ArrayList<Edge> diagramGraphEdgeList = diagramGraph.getEdgeList();
      ArrayList<AttributeEdge> diagramGraphAttrEdgeList = diagramGraph.getAttributeEdgeList();
      ArrayList<Node> diagramGraphNodeList = diagramGraph.getNodeList();

      ArrayList<DiagramGraph> basicPatternGraphList = new ArrayList<DiagramGraph>();

      ArrayList<ArrayList<NodePair>> diagramCoverageNodeMappingList = new ArrayList<ArrayList<NodePair>>();
      ArrayList<ArrayList<EdgePair>> diagramCoverageEdgeMappingList = new ArrayList<ArrayList<EdgePair>>();
      ArrayList<ArrayList<DataNodePair>> diagramCoverageDataNodeMappingList = new ArrayList<ArrayList<DataNodePair>>();
      ArrayList<ArrayList<AttributeEdgePair>> diagramCoverageAttrEdgeMappingList = new ArrayList<ArrayList<AttributeEdgePair>>();

      ArrayList<Node> diagramGraphGenNodeList = new ArrayList<Node>();
      ArrayList<Node> diagramGraphAssocNodeList = new ArrayList<Node>();
      ArrayList<Node> diagramGraphPropNodeList = new ArrayList<Node>();
      ArrayList<Node> classNodeList = new ArrayList<Node>();
      ArrayList<Node> dataTypeNodeList = new ArrayList<Node>();

      for (Node node : diagramGraphNodeList)
        switch (node.getType()) {
          case "Generalization":
            diagramGraphGenNodeList.add(node);
            break;
          case "Association":
            diagramGraphAssocNodeList.add(node);
            break;
          case "Property":
            diagramGraphPropNodeList.add(node);
            break;
          case "Class":
            diagramGraphClassNodeList.add(node);
            break;
          case "DataType":
            diagramGraphDataTypeNodeList.add(node);
            break;
          default:
            break;
        }

      int propNodeCountStart = 0;
      int propNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph1();

        Node basicPatternPropNode =  basicPatternGraph.getNode("Property");
        Edge basicPatternTypeEdge = basicPatternPropNode.getOutgoingEdge("type");
        Node basicPatternDataTypeNode = basicPatternTypeEdge.getTarget();
        Edge basicPatternClassEdge = basicPatternPropNode.getOutgoingEdge("class");
        Node basicPatternClassNode = basicPatternClassEdge.getTarget();
        Edge basicPatternOwnedAttrEdge = Node.getEdge(basicPatternClassNode, basicPatternPropNode, "ownedAttribute");  
        AttributeEdge basicPatternLowerAttrEdge = basicPatternPropNode.getAttributeEdge("lower");
        DataNode basicPatternDataNode1 = basicPatternLowerAttrEdge.getTarget();
        AttributeEdge basicPatternUpperAttrEdge = basicPatternPropNode.getAttributeEdge("upper");
        DataNode basicPatternDataNode2 = basicPatternUpperAttrEdge.getTarget();

        for (propNodeCount = propNodeCountStart; propNodeCount < diagramGraphPropNodeList.size(); propNodeCount++) {

          Node diagramGraphPropNode = diagramGraphPropNodeList.get(propNodeCount);
          Edge diagramGraphTypeEdge = diagramGraphPropNode.getOutgoingEdge("type");
          
          if (diagramGraphTypeEdge.getTarget().getType().equals("DataType") {

            Node diagramGraphDataTypeNode = diagramGraphTypeEdge.getTarget();
            Edge diagramGraphClassEdge = diagramGraphPropNode.getOutgoingEdge("class");
            Node diagramGraphClassNode = diagramGraphClassEdge.getTarget();
            Edge diagramGraphOwnedAttrEdge = Node.getEdge(diagramGraphClassNode, diagramGraphPropNode, "ownedAttribute");  
            AttributeEdge diagramGraphLowerAttrEdge = diagramGraphPropNode.getAttributeEdge("lower");
            DataNode diagramGraphDataNode1 = diagramGraphLowerAttrEdge.getTarget();
            AttributeEdge diagramGraphUpperAttrEdge = diagramGraphPropNode.getAttributeEdge("upper");
            DataNode diagramGraphDataNode2 = diagramGraphUpperAttrEdge.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode, diagramGraphClassNode);
            NodePair nodePair2 = new NodePair(basicPatternDataTypeNode, diagramGraphDataTypeNode);
            NodePair nodePair3 = new NodePair(basicPatternPropNode, diagramGraphPropNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge, diagramGraphClassEdge);
            EdgePair edgePair2 = new EdgePair(basicPatternTypeEdge, diagramGraphTypeEdge);
            EdgePair edgePair3 = new EdgePair(basicPatternOwnedAttrEdge, diagramGraphOwnedAttrEdge);
            edgeMapping.add(edgePair1);
            edgeMapping.add(edgePair2);
            edgeMapping.add(edgePair3);
            diagramCoverageEdgeMappingList.add(edgeMapping);
            ArrayList<DataNodePair> dataNodeMapping = new ArrayList<DataNodePair>();
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramGraphDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramGraphDataNode2);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge, diagramGraphLowerAttrEdge);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge, diagramGraphUpperAttrEdge);
            attrEdgeMapping.add(edgePair1);
            attrEdgeMapping.add(edgePair2);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            propNodeCountStart = propNodeCount + 1;

            break;

          }

        }

        if (propNodeCount == diagramGraphPropNodeList.size())
          break;

      }

      for (int i = 0; i < diagramGraphGenNodeList.size(); i++) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph2();

        Node basicPatternGenNode = basicPatternGraph.getNode("Generalization");
        Edge basicPatternGeneralEdge = basicPatternGenNode.getOutgoingEdge("general");
        Edge basicPatternSpecifciEdge = basicPatternGenNode.getOutgoingEdge("specific");
        Node basicPatternClassNode1 = basicPatternGeneralEdge.getTarget();
        Node basicPatternClassNode2 = basicPatternSpecifciEdge.getTarget();

        Node diagramGraphGenNode = diagramGraphGenNodeList.get(i);
        Edge diagramGraphGeneralEdge = diagramGraphGenNode.getOutgoingEdge("general");
        Edge diagramGraphSpecifciEdge = diagramGraphGenNode.getOutgoingEdge("specific");
        Node diagramGraphClassNode1 = diagramGraphGeneralEdge.getTarget();
        Node diagramGraphClassNode2 = diagramGraphSpecifciEdge.getTarget();
        
        ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
        NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramGraphClassNode2);
        NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramGraphClassNode1);
        NodePair nodePair3 = new NodePair(basicPatternGenNode, diagramGraphGenNode);
        nodeMapping.add(nodePair1);
        nodeMapping.add(nodePair2);
        nodeMapping.add(nodePair3);
        diagramCoverageNodeMappingList.add(nodeMapping);
        ArrayList<EdgePair> edgeMapping = new ArrayList<NodePair>();
        EdgePair edgePair1 = new NodePair(basicPatternGeneralEdge, diagramGraphGeneralEdge);
        EdgePair edgePair2 = new NodePair(basicPatternSpecifciEdge, diagramGraphSpecifciEdge);
        edgeMapping.add(edgePair1);
        edgeMapping.add(edgePair2);
        diagramCoverageEdgeMappingList.add(edgeMapping);

        basicPatternGraphList.add(basicPatternGraph);

      }

      int assocNodeCountStart = 0;
      int assocNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph3();

        Node basicPatternAssocNode =  basicPatternGraph.getNode("Association");
        ArrayList<Edge> basicPatternMemberEndEdgeList = basicPatternAssocNode.getOutgoingEdgeList("memberEnd");
        Node basicPatternPropNode1 = basicPatternMemberEndEdgeList.get(0).getTarget();
        Edge basicPatternClassEdge1 = basicPatternPropNode1.getOutgoingEdge("class");
        Node basicPatternClassNode1 = basicPatternClassEdge1.getTarget();
        Node basicPatternPropNode2 = basicPatternMemberEndEdgeList.get(1).getTarget();
        Edge basicPatternClassEdge2 = basicPatternPropNode2.getOutgoingEdge("class");
        Node basicPatternClassNode2 = basicPatternClassEdge2.getTarget();
        Edge basicPatternTypeEdge1 = basicPatternPropNode1.getOutgointEdge("type");
        Edge basicPatternTypeEdge2 = basicPatternPropNode2.getOutgointEdge("type");
        Edge basicPatternOwnedAttrEdge1 = Node.getEdge(basicPatternClassNode1, basicPatternPropNode1, "ownedAttribute"); 
        Edge basicPatternOwnedAttrEdge2 = Node.getEdge(basicPatternClassNode2, basicPatternPropNode2, "ownedAttribute");
        AttributeEdge basicPatternLowerAttrEdge1 = basicPatternPropNode1.getAttributeEdge("lower");
        DataNode basicPatternDataNode1 = basicPatternLowerAttrEdge1.getTarget();
        AttributeEdge basicPatternUpperAttrEdge1 = basicPatternPropNode1.getAttributeEdge("upper");
        DataNode basicPatternDataNode2 = basicPatternUpperAttrEdge1.getTarget(); 
        AttributeEdge basicPatternLowerAttrEdge2 = basicPatternPropNode2.getAttributeEdge("lower");
        DataNode basicPatternDataNode3 = basicPatternLowerAttrEdge2.getTarget();
        AttributeEdge basicPatternUpperAttrEdge2 = basicPatternPropNode2.getAttributeEdge("upper");
        DataNode basicPatternDataNode4 = basicPatternUpperAttrEdge2.getTarget(); 

        for (assocNodeCount = assocNodeCountStart; assocNodeCount < diagramGraphAssocNodeList.size(); assocNodeCount++) {

          Node diagramGraphAssocNode = diagramGraphAssocNodeList.get(assocNodeCount);
          ArrayList<Edge> diagramGraphOwnedEndEdgeList = diagramGraphAssocNode.getOutgoingEdgeList("ownedEnd");

          if (diagramGraphOwnedEndEdgeList.size() == 0) {

            ArrayList<Edge> diagramGraphMemberEndEdgeList = diagramGraphAssocNode.getOutgoingEdgeList("memberEnd");
            Node diagramGraphPropNode1 = diagramGraphMemberEndEdgeList.get(0).getTarget();        
            Edge diagramGraphClassEdge1 = diagramGraphPropNode1.getOutgoingEdge("class");
            Node diagramGraphClassNode1 = diagramGraphClassEdge1.getTarget();
            Node diagramGraphPropNode2 = diagramGraphMemberEndEdgeList.get(1).getTarget();        
            Edge diagramGraphClassEdge2 = diagramGraphPropNode1.getOutgoingEdge("class");
            Node diagramGraphClassNode2 = diagramGraphClassEdge2.getTarget();
            Edge diagramGraphTypeEdge1 = diagramGraphPropNode1.getOutgointEdge("type");
            Edge diagramGraphTypeEdge2 = diagramGraphPropNode2.getOutgointEdge("type");
            Edge diagramGraphOwnedAttrEdge1 = Node.getEdge(diagramGraphClassNode1, diagramGraphPropNode1, "ownedAttribute");       
            Edge diagramGraphOwnedAttrEdge2 = Node.getEdge(diagramGraphClassNode2, diagramGraphPropNode2, "ownedAttribute"); 
            AttributeEdge diagramGraphLowerAttrEdge1 = diagramGraphPropNode1.getAttributeEdge("lower");
            DataNode diagramGraphDataNode1 = diagramGraphLowerAttrEdge1.getTarget();
            AttributeEdge diagramGraphUpperAttrEdge1 = diagramGraphPropNode1.getAttributeEdge("upper");
            DataNode diagramGraphDataNode2 = diagramGraphUpperAttrEdge1.getTarget();
            AttributeEdge diagramGraphLowerAttrEdge2 = diagramGraphPropNode2.getAttributeEdge("lower");
            DataNode diagramGraphDataNode3 = diagramGraphLowerAttrEdge2.getTarget();
            AttributeEdge diagramGraphUpperAttrEdge2 = diagramGraphPropNode2.getAttributeEdge("upper");
            DataNode diagramGraphDataNode4 = diagramGraphUpperAttrEdge2.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramGraphClassNode1);
            NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramGraphClassNode2);
            NodePair nodePair3 = new NodePair(basicPatternPropNode1, diagramGraphPropNode1);
            NodePair nodePair4 = new NodePair(basicPatternPropNode2, diagramGraphPropNode2);
            NodePair nodePair5 = new NodePair(basicPatternAssocNode, diagramGraphAssocNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            nodeMapping.add(nodePair4);
            nodeMapping.add(nodePair5);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge1, diagramGraphClassEdge1);
            EdgePair edgePair2 = new EdgePair(basicPatternClassEdge2, diagramGraphClassEdge1);
            EdgePair edgePair3 = new EdgePair(basicPatternTypeEdge1, diagramGraphTypeEdge1);
            EdgePair edgePair4 = new EdgePair(basicPatternTypeEdge2, diagramGraphTypeEdge2);
            EdgePair edgePair5 = new EdgePair(basicPatternOwnedAttrEdge1, diagramGraphOwnedAttrEdge1);
            EdgePair edgePair6 = new EdgePair(basicPatternOwnedAttrEdge2, diagramGraphOwnedAttrEdge2);
            EdgePair edgePair7 = new EdgePair(basicPatternMemberEndEdgeList.get(0), diagramGraphMemberEndEdgeList.get(0));
            EdgePair edgePair8 = new EdgePair(basicPatternMemberEndEdgeList.get(1), diagramGraphMemberEndEdgeList.get(1));
            edgeMapping.add(edgePair1);
            edgeMapping.add(edgePair2);
            edgeMapping.add(edgePair3);
            edgeMapping.add(edgePair4);
            edgeMapping.add(edgePair5);
            edgeMapping.add(edgePair6);
            edgeMapping.add(edgePair7);
            edgeMapping.add(edgePair8);
            diagramCoverageEdgeMappingList.add(edgeMapping);
            ArrayList<DataNodePair> dataNodeMapping = new ArrayList<DataNodePair>();
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramGraphDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramGraphDataNode2);
            DataNodePair dataNodePair3 = new DataNodePair(basicPatternDataNode3, diagramGraphDataNode3);
            DataNodePair dataNodePair4 = new DataNodePair(basicPatternDataNode4, diagramGraphDataNode4);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            dataNodeMapping.add(dataNodePair3);
            dataNodeMapping.add(dataNodePair4);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge1, diagramGraphLowerAttrEdge1);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge1, diagramGraphUpperAttrEdge1);
            AttributeEdgePair attrEdgePair3 = new AttributeEdgePair(basicPatternLowerAttrEdge2, diagramGraphLowerAttrEdge2);
            AttributeEdgePair attrEdgePair4 = new AttributeEdgePair(basicPatternUpperAttrEdge2, diagramGraphUpperAttrEdge2);
            attrEdgeMapping.add(edgePair1);
            attrEdgeMapping.add(edgePair2);
            attrEdgeMapping.add(edgePair3);
            attrEdgeMapping.add(edgePair4);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            assocNodeCountStart = assocNodeCount + 1;

            break;

          }

        }

        if (assocNodeCount == diagramGraphAssocNodeList.size())
          break;

      }

      assocNodeCountStart = 0;
      assocNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph4();

        Node basicPatternAssocNode =  basicPatternGraph.getNode("Association");
        ArrayList<Edge> basicPatternMemberEndEdgeList = basicPatternAssocNode.getOutgoingEdgeList("memberEnd");
        Edge basicPatternOnwnedEndEdge = basicPatternAssocNode.getOutgoingEdge("ownedEnd");
        Node basicPatternPropNode2 = basicPatternOnwnedEndEdge.getTarget();
        Edge basicPatternTypeEdge2 = basicPatternPropNode2.getOutgointEdge("type");
        Node basicPatternClassNode2 = basicPatternTypeEdge2.getTarget();
        Node basicPatternMemberEndEdge1 = null;
        Node basicPatternMemberEndEdge2 = null;
        if (basicPatternMemberEndEdgeList.get(0).getTarget() == basicPatternPropNode2) {
          basicPatternMemberEndEdge1 = basicPatternMemberEndEdgeList.get(1);
          basicPatternMemberEndEdge2 = basicPatternMemberEndEdgeList.get(0);
        } else {
          basicPatternMemberEndEdge1 = basicPatternMemberEndEdgeList.get(0);
          basicPatternMemberEndEdge2 = basicPatternMemberEndEdgeList.get(1);
        }
        Node basicPatternPropNode1 = basicPatternMemberEndEdge1.getTarget(); 
        Edge basicPatternClassEdge1 = basicPatternPropNode1.getOutgoingEdge("class");
        Node basicPatternClassNode1 = basicPatternClassEdge1.getTarget();
        Edge basicPatternTypeEdge1 = basicPatternPropNode1.getOutgointEdge("type");
        Edge basicPatternOwnedAttrEdge1 = Node.getEdge(basicPatternClassNode1, basicPatternPropNode1, "ownedAttribute"); 
        AttributeEdge basicPatternLowerAttrEdge1 = basicPatternPropNode1.getAttributeEdge("lower");
        DataNode basicPatternDataNode1 = basicPatternLowerAttrEdge1.getTarget();
        AttributeEdge basicPatternUpperAttrEdge1 = basicPatternPropNode1.getAttributeEdge("upper");
        DataNode basicPatternDataNode2 = basicPatternUpperAttrEdge1.getTarget(); 
        AttributeEdge basicPatternLowerAttrEdge2 = basicPatternPropNode2.getAttributeEdge("lower");
        DataNode basicPatternDataNode3 = basicPatternLowerAttrEdge2.getTarget();
        AttributeEdge basicPatternUpperAttrEdge2 = basicPatternPropNode2.getAttributeEdge("upper");
        DataNode basicPatternDataNode4 = basicPatternUpperAttrEdge2.getTarget(); 

        for (assocNodeCount = assocNodeCountStart; assocNodeCount < diagramGraphAssocNodeList.size(); assocNodeCount++) {

          Node diagramGraphAssocNode = diagramGraphAssocNodeList.get(assocNodeCount);
          ArrayList<Edge> diagramGraphOwnedEndEdgeList = diagramGraphAssocNode.getOutgoingEdgeList("ownedEnd");

          if (diagramGraphOwnedEndEdgeList.size() == 1) {

            ArrayList<Edge> diagramGraphMemberEndEdgeList = diagramGraphAssocNode.getOutgoingEdgeList("memberEnd");
            Edge diagramGraphOnwnedEndEdge = diagramGraphAssocNode.getOutgoingEdge("ownedEnd");
            Node diagramGraphPropNode2 = diagramGraphOnwnedEndEdge.getTarget();        
            Edge diagramGraphClassEdge2 = diagramGraphPropNode1.getOutgoingEdge("class");
            Node diagramGraphClassNode2 = diagramGraphClassEdge2.getTarget();
            Edge diagramGraphTypeEdge2 = diagramGraphPropNode2.getOutgointEdge("type");
            Node memberEndNode1 = null;
            Node memberEndNode2 = null;
            if (basicPatternMemberEndEdgeList.get(0).getTarget() == diagramGraphPropNode2) {
              memberEndNode1 = diagramGraphMemberEndEdgeList.get(1);
              memberEndNode2 = diagramGraphMemberEndEdgeList.get(0);
            } else {
              memberEndNode1 = diagramGraphMemberEndEdgeList.get(0);
              memberEndNode2 = diagramGraphMemberEndEdgeList.get(1);
            }
            Node diagramGraphPropNode1 = memberEndNode1.getTarget();       
            Edge diagramGraphClassEdge1 = diagramGraphPropNode1.getOutgoingEdge("class");
            Node diagramGraphClassNode1 = diagramGraphClassEdge1.getTarget();
            Edge diagramGraphTypeEdge1 = diagramGraphPropNode1.getOutgointEdge("type");
            Edge diagramGraphOwnedAttrEdge1 = Node.getEdge(diagramGraphClassNode1, diagramGraphPropNode1, "ownedAttribute"); 
            AttributeEdge diagramGraphLowerAttrEdge1 = diagramGraphPropNode1.getAttributeEdge("lower");
            DataNode diagramGraphDataNode1 = diagramGraphLowerAttrEdge1.getTarget();
            AttributeEdge diagramGraphUpperAttrEdge1 = diagramGraphPropNode1.getAttributeEdge("upper");
            DataNode diagramGraphDataNode2 = diagramGraphUpperAttrEdge1.getTarget();
            AttributeEdge diagramGraphLowerAttrEdge2 = diagramGraphPropNode2.getAttributeEdge("lower");
            DataNode diagramGraphDataNode3 = diagramGraphLowerAttrEdge2.getTarget();
            AttributeEdge diagramGraphUpperAttrEdge2 = diagramGraphPropNode2.getAttributeEdge("upper");
            DataNode diagramGraphDataNode4 = diagramGraphUpperAttrEdge2.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramGraphClassNode1);
            NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramGraphClassNode2);
            NodePair nodePair3 = new NodePair(basicPatternPropNode1, diagramGraphPropNode1);
            NodePair nodePair4 = new NodePair(basicPatternPropNode2, diagramGraphPropNode2);
            NodePair nodePair5 = new NodePair(basicPatternAssocNode, diagramGraphAssocNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            nodeMapping.add(nodePair4);
            nodeMapping.add(nodePair5);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<NodePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge1, diagramGraphClassEdge1);
            EdgePair edgePair2 = new EdgePair(basicPatternTypeEdge1, diagramGraphTypeEdge1);
            EdgePair edgePair3 = new EdgePair(basicPatternTypeEdge2, diagramGraphTypeEdge2);
            EdgePair edgePair4 = new EdgePair(basicPatternOwnedAttrEdge1, diagramGraphOwnedAttrEdge1);
            EdgePair edgePair5 = new EdgePair(basicPatternMemberEndEdge1, diagramGraphMemberEndEdge1);
            EdgePair edgePair6 = new EdgePair(basicPatternMemberEndNode2, diagramGraphMemberEndEdge2);
            EdgePair edgePair7 = new EdgePair(basicPatternOwnedEndEdge, ownedEndEdge);
            edgeMapping.add(edgePair1);
            edgeMapping.add(edgePair2);
            edgeMapping.add(edgePair3);
            edgeMapping.add(edgePair4);
            edgeMapping.add(edgePair5);
            edgeMapping.add(edgePair6);
            edgeMapping.add(edgePair7);
            diagramCoverageEdgeMappingList.add(edgeMapping);
            ArrayList<DataNodePair> dataNodeMapping = new ArrayList<DataNodePair>();
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramGraphDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramGraphDataNode2);
            DataNodePair dataNodePair3 = new DataNodePair(basicPatternDataNode3, diagramGraphDataNode3);
            DataNodePair dataNodePair4 = new DataNodePair(basicPatternDataNode4, diagramGraphDataNode4);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            dataNodeMapping.add(dataNodePair3);
            dataNodeMapping.add(dataNodePair4);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge1, diagramGraphLowerAttrEdge1);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge1, diagramGraphUpperAttrEdge1);
            AttributeEdgePair attrEdgePair3 = new AttributeEdgePair(basicPatternLowerAttrEdge2, diagramGraphLowerAttrEdge2);
            AttributeEdgePair attrEdgePair4 = new AttributeEdgePair(basicPatternUpperAttrEdge2, diagramGraphUpperAttrEdge2);
            attrEdgeMapping.add(edgePair1);
            attrEdgeMapping.add(edgePair2);
            attrEdgeMapping.add(edgePair3);
            attrEdgeMapping.add(edgePair4);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            assocNodeCountStart = assocNodeCount + 1;

            break;

          }

        }

        if (assocNodeCount == diagramGraphAssocNodeList.size())
          break;

      }

      basicPatternCoverage = true;
      for (Node node : diagramGraphNodeList)
        if (!hasNodeMapping(diagramCoverageNodeMappingList, node)) {
          basicPatternCoverage = false;
          return null;
        }
      for (Edge edge : diagramGraphEdgeList)
        if (!hasEdgeMapping(diagramCoverageEdgeMappingList, edge)) {
          basicPatternCoverage = false;
          return null;
        }
      for (DataNode dataNode : diagramGraphDataNodeList)
        if (!hasDataNodeMapping(diagramCoverageDataNodeMappingList, dataNode)) {
          basicPatternCoverage = false;
          return null;
        }
      for (AttributeEdge attrEdge : diagramGraphAttrNodeList)
        if (!hasAttributeEdgeMapping(diagramCoverageAttrEdgeMappingList, attrEdge)) {
          basicPatternCoverage = false;
          return null;
        }

      int classNodeCount = 1;
      for (Node classNode: diagramGraphClassNodeList) {
        ArrayList<Node> preImageNodeList = getPreImageList(diagramCoverageNodeMappingList, classNode);
        for (Node preImageNode: preImageNodeList) {
          preImageNode.setName("c" + classNodeCount);
        }
        classNodeCount++;
      }
      int dataTypeNodeCount = 1;
      for (Node dataTypeNode: diagramGraphDataTypeNodeList) {
        ArrayList<Node> preImageNodeList = getPreImageList(diagramCoverageNodeMappingList, dataTypeNode);
        for (Node preImageNode: preImageNodeList) {
          preImageNode.setName("dt" + dataTypeNodeCount);
        }
        dataTypeNodeCount++;
      }
      int genNodeCount = 1;
      int assocNodeCount = 1;
      int propNodeCount = 1;
      for (DiagramGraph basicPatternGraph: basicPatternGraphList) {
        ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
        for (Node node: nodeList)
          switch (node.getType()) {
            case "Generalization":
              node.setName("g" + genNodeCount);
              break;
            case "Association":
              node.setName("a" + assocNodeCount);
              break;
            case "Property":
              node.setName("p" + propNodeCount);
              break;
            default:
              break;
          }
      }
      for (DiagramGraph basicPatternGraph: basicPatternGraphList) {
        ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
        for (Node node: nodeList) {
          ArrayList<AttributeEdge> attrEdgeList = node.attributeEdgeList();
          for (AttributeEdge attrEdge: attrEdgeList) {
            DataNode dataNode = attrEdge.getTarget();
            dataNode.setName(dataNode.getValue() + "_" + node.getName() + "_" + attrEdge.getType());
          }
        }
      }

      DiagramGraph complexPatternGraph = createComplexPattern(basicPatternGraphList);

      ArrayList<ColorGraphNode> colorGraphNodeList1 = new ArrayList<ColorGraphNode>();
      ArrayList<ColorGraphEdge> colorGraphEdgeList1 = new ArrayList<ColorGraphEdge>();
      createColorGraph(diagramGraphNodeList, diagramGraphEdgeList, colorGraphNodeList1, colorGraphEdgeList1);

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
   * Generates an E-graph of the basic patter 1. 
   * @return the E-graph of the basic patter 1
   */
  public static DiagramGraph createBasicPatternGraph1() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<Edge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
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
    return diagramGraph
  }

  /**
   * Generates an E-graph of the basic patter 2. 
   * @return the E-graph of the basic patter 2
   */
  public static DiagramGraph createBasicPatternGraph2() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    Node classNode1 = new Node("c1", "Class");
    nodeList.add(classNode1);
    Node classNode2 = new Node("c2", "Class");
    nodeList.add(classNode2);
    Node GenNode = new Node("g1", "Generalization");
    nodeList.add(GenNode);
    Edge generalEdge = new Edge(null, "general", GenNode, classNode2);
    Edge specificEdge = new Edge(null, "specific", GenNode, classNode1);
    edgeList.add(generalEdge);
    edgeList.add(specificEdge);
    outgoingEdgeList = GenNode.getOutgoingEdgeList();
    outgoingEdgeList.add(generalEdge);
    outgoingEdgeList.add(specificEdge);
    ArrayList<Edge> incomingEdgeList = classNode1.getIncomingEdgeList();
    incomingEdgeList.add(specificEdge);
    incomingEdgeList = classNode2.getIncomingEdgeList();
    incomingEdgeList.add(generalEdge);
    return diagramGraph;
  }

  /**
   * Generates an E-graph of the basic patter 3. 
   * @return the E-graph of the basic patter 3
   */
  public static DiagramGraph createBasicPatternGraph3() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<Edge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
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
    return diagramGraph;
  }

  /**
   * Generates an E-graph of the basic patter 4. 
   * @return the E-graph of the basic patter 4
   */
  public static DiagramGraph createBasicPatternGraph4() {
    DiagramGraph basicPatternGraph = new DiagramGraph();
    ArrayList<Node> nodeList = basicPatternGraph.getNodeList();
    ArrayList<Edge> edgeList = basicPatternGraph.getEdgeList();
    ArrayList<DataNode> dataNodeList = basicPatternGraph.getDataNodeList();
    ArrayList<Edge> attrEdgeList = basicPatternGraph.getAttributeEdgeList();
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
    return diagramGraph;
  }

  /**
   * Returns a list of preimage graph nodes from the given list of graph node 
   * mappings that are equal to the given graph node. 
   * @param nodeMappingList - the list of graph node mappings
   *        node - the graph node
   * @return the list of preimage graph nodes from the given list of graph node 
   *         mappings that are equal to the given graph node
   */
  public static ArrayList<Node> getPreImageList(ArrayList<ArrayList<NodePair>> nodeMappingList, Node node) {
    ArrayList<Node> nodePreImageList = new ArrayList<Node>();
    for (ArrayList<NodePair> nodeMapping: nodeMappingList) {
      for (NodePair nodePair: nodeMapping)
        if (nodePair.getNode2() == node)
           nodePreImageList.add(nodePair.getNode1());
     return nodePreImageList;
  }


  /**
   * Creates a complex pattern E-graph from the given list of basic pattern E-graphs.
   * @param basicPatternGraphList - the list of basic pattern E-graphs
   * @return the complex pattern E-graph
   */
  public static DaigramGraph createComplexPatternGraph(ArrayList<DaigramGraph> basicPatternGraphList) {
     DiagramGraph complexPatternGraph2 = null;
     for (int i = 1; i < basicPatternGraphList.size(); i++) {
        DiagramGraph complexPatternGraph1 = null;
        if (complexPatternGraph2 == null) 
          complexPatternGraph1 = basicPatternGraphList.get(0);
        else
          complexPatternGraph1 = complexPatternGraph2;
        DiagramGraph basicPatternGraph = basicPatternGraphList.get(i);
        DiagramGraph complexPatternGraph2 = extendComplexPatternGraph(complexPatternGraph1, basicPatternGraph);
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
        return nodePair
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
        return nodePair
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
  public static AttributeEdge getEquivalentAttributeEdge(ArrayList<Edge> attrEdgeList, AttributeEdge attrEdge) {
    for (AttributeEdge element: attrEdgeList)
      if (element.getType().equals(attrEdge.getType()) &&
          element.getSource().getName().equals(attrEdge.getSource().getName()) &&
          element.getTarget().getName().equals(attrEdge.getTarget().getName()))
        return element;
    return null;
  }

  /**
   * Extends the given complex pattern E-graph with the basic pattern E-graph.
   * @param complexPatternGraph - the complex pattern E-graph
   *        basicPatternGraph - the basic pattern E-graph
   * @return the extended complex pattern E-graph
   */
  public static DiagramGraph extendComplexPatternGraph(DiagramGraph complexPatternGraph, DiagramGraph basicPatternGraph) {

     ArrayList<Node> complexPatternGraphNodeList = complexPatternGraph.getNodeList();
     ArrayList<Node> basicPatternGraphNodeList = basicPatternGraph.getNodeList();
     ArrayList<Edge> complexPatternGraphEdgeList = complexPatternGraph.getEdgeList();
     ArrayList<Edge> basicPatternGraphEdgeList = basicPatternGraph.getEdgeList();
     ArrayList<Node> complexPatternGraphDataNodeList = complexPatternGraph.getDataNodeList();
     ArrayList<Node> basicPatternGraphDataNodeList = basicPatternGraph.getDataNodeList();
     ArrayList<Edge> complexPatternGraphAttrEdgeList = complexPatternGraph.getAttributeEdgeList();
     ArrayList<Edge> basicPatternGraphAttrEdgeList = basicPatternGraph.getAttributeEdgeList();
     DiagramGraph resultComplexPatternGraph = new DiagramGraph();
     ArrayList<Node> resultComplexPatternGraphNodeList = resultComplexPatternGraph.getNodeList();
     ArrayList<Edge> resultComplexPatternGraphEdgeList = resultComplexPatternGraph.getEdgeList();
     ArrayList<Node> resultComplexPatternGraphDataNodeList = resultComplexPatternGraph.getDataNodeList();
     ArrayList<Edge> resultComplexPatternGraphAttrEdgeList = resultComplexPatternGraph.getAttributeEdgeList();
     HashMap<String, Node> resultComplexPatternGraphNodeMap = new HashMap<>();
     DiagramGraph qGraph = new DiagramGraph();
     ArrayList<Node> qGraphNodeList = qGraph.getNodeList();
     ArrayList<Edge> qGraphEdgeList = qGraph.getEdgeList();
     ArrayList<Node> qGraphDataNodeList = qGraph.getDataNodeList();
     ArrayList<Edge> qGraphAttrEdgeList = qGraph.getAttributeEdgeList();
     HashMap<String, Node> qGraphNodeMap = new HashMap<>();
     HashMap<String, DataNode> qGraphDataNodeMap = new HashMap<>();
     ArrayList<NodePair> h1NodeMapping = ArrayList<NodePair>();
     ArrayList<EdgePair> h1EdgeMapping = ArrayList<EdgePair>();
     ArrayList<NodePair> h1DataNodeMapping = ArrayList<DataNodePair>();
     ArrayList<EdgePair> h1AttrEdgeMapping = ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> h2NodeMapping = ArrayList<NodePair>();
     ArrayList<EdgePair> h2EdgeMapping = ArrayList<EdgePair>();
     ArrayList<NodePair> h2DataNodeMapping = ArrayList<DataNodePair>();
     ArrayList<EdgePair> h2AttrEdgeMapping = ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> g1NodeMapping = ArrayList<NodePair>();
     ArrayList<EdgePair> g1EdgeMapping = ArrayList<EdgePair>();
     ArrayList<NodePair> g1DataNodeMapping = ArrayList<DataNodePair>();
     ArrayList<EdgePair> g1AttrEdgeMapping = ArrayList<AttributeEdgePair>();
     ArrayList<NodePair> g2NodeMapping = ArrayList<NodePair>();
     ArrayList<EdgePair> g2EdgeMapping = ArrayList<EdgePair>();
     ArrayList<NodePair> g2DataNodeMapping = ArrayList<DataNodePair>();
     ArrayList<EdgePair> g2AttrEdgeMapping = ArrayList<AttributeEdgePair>();

     for (Node node1: complexPatternGraphNodeList) {
       Node node2 = new Node(node1.getName(), node1.getType());
       resultComplexPatternGraphNodeList.add(node2);
       NodePair nodePair = new NodePair(node1, node2);
       h1NodeMapping.add(nodePair);
     }
     for (Node node1: basicPatternGraphNodeList) {
       Node node2 = getEquivalentNode(complexPatternGraphNodeList, node1);
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
     for (Edge edge1: complexPatternGraphEdgeList) { 
       Node sourceNode  = resultComplexPatternGraphNodeMap.get(edge1.getSource().getName());
       Node targetNode  = resultComplexPatternGraphNodeMap.get(edge1.Target().getName());
       Edge edge2 = Edge(null, edge1.getType(), sourceNode, targetNode);
       resultComplexPatternGraphEdgeList.add(edge2);
       ArrayList<Edge> outgoingEdgeList = sourceNode.getOutgoingEdgeList();
       outgoingEdgeList.add(edge2);
       ArrayList<Edge> incomingEdgeList = targetNode.getIncomingEdgeList();
       incomingEdgeList.add(edge2);
       EdgePair edgePair = new EdgePair(edge1, edge2);
       h1EdgeMapping.add(edgePair);
     }
     for (Edge edge1: basicPatternGraphEdgeList)
       Edge edge2 = getEquivalentEdge(complexPatternGraphEdgeList, edge1);
       if (edge2 == null) {
         Node sourceNode = resultComplexPatternGraphNodeMap.get(edge1.getSource().getName());
         Node targetNode = resultComplexPatternGraphNodeMap.get(edge1.Target().getName());
         Edge edge3 = Edge(null, edge1.getType(), sourceNode, targetNode);
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

     for (Node node1: resultComplexPatternGraphNodeList) {
        NodePair nodePair1 = getNodePairByImage(h1NodeMapping, node1);
        NodePair nodePair2 = getNodePairByImage(h2NodeMapping, node1);
        if (nodePair1 != null && nodePair2 != null) {
           Node node2 = Node(nodePair1.getNode1().getName(), null);
           qGraphNodeList.add(node2);
           NodePair nodePair3 = new NondePair(node2, nodePair1.getNode1());
           g1NodeMapping.add(nodePair);
           NodePair nodePair4 = new NondePair(node2, nodePair2.getNode1());
           g2NodeMapping.add(nodePair);
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
           Edge edge2 = Edge(null, null, sourceNode, targetDataNode);
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

     for (DataNode dataNode1: complexPatternGraphDataNodeList) {
       DataNode dataNode2 = new DataNode(dataNode1.getName(), dataNode1.getType());
       resultComplexPatternGraphDataNodeList.add(dataNode2);
       DataNodePair dataNodePair = new DataNodePair(dataNode1, dataNode2);
       h1DataNodeMapping.add(dataNodePair);
     }
     for (DataNode dataNode1: basicPatternGraphDataNodeList) {
       DataNode dataNode2 = getEquivalentDataNode(complexPatternGraphDataNodeList, dataNode1);
       if (dataNode2 == null) {
         DataNode dataNode3 = new DataNode(dataNode1.getName(), dataNode1.getType());
         resultComplexPatternGraphDataNodeList.add(dataNode3);
         DataNodePair dataNodePair = new DataNodePair(dataNode1, dataNode3);
         h2DataNodeMapping.add(dataNodePair);
       } else {
         DataNodePair dataNodePair1 = getDataNodePairByPreImage(h1DataNodeMapping, dataNataNode2);
         DataNodePair dataNodePair2 = new DataNodePair(dataNode1, dataNodePair1.getDataNode2());
         h2DataNodeMapping.add(dataNodePair2);         
       }
     }
     for (DataNode dataNode: resultComplexPatternGraphDataNodeList)
       resultComplexPatternGraphDataNodeMap.put(dataNode.getName(), dataNode);
     for (AttributeEdge attrEdge1: complexPatternGraphAttrEdgeList) { 
       Node sourceNode  = resultComplexPatternGraphNodeMap.get(attrEdge1.getSource().getName());
       DataNode targetDataNode  = resultComplexPatternGraphDataNodeMap.get(attrEdge1.Target().getName());
       AttributeEdge attrEdge2 = AttributeEdge(null, attrEdge1.getType(), sourceNode, targetDataNode);
       resultComplexPatternGraphAttrEdgeList.add(attrEdge2);
       ArrayList<AttributeEdge> attrEdgeList = sourceNode.getAttributeEdgeList();
       attrEdgeList.add(attrEdge2);
       AttributeEdgePair attrEdgePair = new AttributeEdgePair(attrEdge1, attrEdge2);
       h1AttrEdgeMapping.add(attrEdgePair);
     }
     for (AttributeEdge attrEdge1: basicPatternGraphAttrEdgeList)
       AttributeEdge attrEdge2 = getEquivalentAttributeEdge(complexPatternGraphAttrEdgeList, attrEdge1);
       if (attrEdge2 == null) {
         Node sourceNode = resultComplexPatternGraphNodeMap.get(attrEdge1.getSource().getName());
         DataNode targetDataNode = resultComplexPatternGraphDataNodeMap.get(attrEdge1.Target().getName());
         AttributeEdge attrEdge3 = AttributeEdge(null, attrEdge1.getType(), sourceNode, targetNode);
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

     for (DataNode dataNode1: resultComplexPatternGraphDataNodeList) {
        DataNodePair dataNodePair1 = getDataNodePairByImage(h1DataNodeMapping, dataNode1);
        DataNodePair dataNodePair2 = getDataNodePairByImage(h2DataNodeMapping, dataNode1);
        if (dataNodePair1 != null && dataNodePair2 != null) {
           DataNode dataNode2 = DataNode(dataNodePair1.getDataNode1().getName(), null);
           qGraphDataNodeList.add(dataNode2);
           DataNodePair dataNodePair3 = new NondePair(dataNode2, dataNodePair1.getDataNode1());
           g1DataNodeMapping.add(dataNodePair);
           DataNodePair dataNodePair4 = new NondePair(dataNode2, dataNodePair2.getDataNode1());
           g2DataNodeMapping.add(dataNodePair);
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
           AttributeEdge attrEdge2 = AttributeEdge(null, null, sourceNode, targetDataNode);
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
         NodePair nodePair3 = getNodePairByPreImage(g1NodeMapping, edge.geTarget());
         NodePair nodePair4 = getNodePairByPreImage(h1NodeMapping, nodePair3.getNode2());
         if (nodePair2.getNode2() != edgePair2.getEdge2().getSource() ||
             nodePair4.getNode2() != edgePair2.getEdge2().getTarget())
           pushOutCommuteExists = false;
         EdgePair edgePair3 = getEdgePairByPreImage(g2EdgeMapping, edge);
         EdgePair edgePair4 = getEdgePairByPreImage(h2EdgeMapping, edgePair3.getEdge2());
         NodePair nodePair5 = getNodePairByPreImage(g2NodeMapping, edge.getSource());
         NodePair nodePair6 = getNodePairByPreImage(h2NodeMapping, nodePair5.getNode2());
         NodePair nodePair7 = getNodePairByPreImage(g2NodeMapping, edge.geTarget());
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
       for (AttributeEdge attrEdge: qGraphEdgeList) {
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
         DataNodePair dataNodePair3 = getDataNodePairByPreImage(g2DataNodeMapping, attrEdge.geTarget());
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

  /**
   * Returns true if the given graph node appears in the list of graph node 
   * mappings.
   * @param nodeMappingList - the list of graph node mappings
   *        node - the graph node
   * @return true if the given graph node appears in the list of graph node 
   *         mappings; false otherwise
   */
  public static boolean hasNodeMapping(ArrayList<NodeMapping> nodeMappingList, Node node) {
    for (NodeMapping nodeMapping : nodeMappingList)
      for (NodePair nodePair: nodeMapping)
        if (nodePair.getNode2() == node)
          return true;
    return false;
  }

  /**
   * Returns true if the given graph edge appears in the list of graph edge 
   * mappings.
   * @param edgeMappingList - the list of graph edge mappings
   *        edge - the graph edge
   * @return true if the given graph edge appears in the list of graph edge 
   *         mappings; false otherwise
   */
  public static boolean hasEdgeMapping(ArrayList<EdgeMapping> edgeMappingList, Edge edge) {
    for (EdgeMapping edgeMapping : edgeMappingList)
      for (EdgePair edgePair: edgeMapping)
        if (edgeMapping.getEdge2() == edge)
          return true;
    return false;
  }

  /**
   * Returns true if the given data node appears in the list of data node 
   * mappings.
   * @param dataNodeMappingList - the list of data node mappings
   *        dataNode - the data node
   * @return true if the given data node appears in the list of data node 
   *         mappings; false otherwise
   */
  public static boolean hasDataNodeMapping(ArrayList<DataNodeMapping> dataNodeMappingList, DataNode node) {
    for (DataNodeMapping dataNodeMapping : dataNodeMappingList)
      for (DataNodePair dataNodePair: dataNodeMapping)
        if (dataNodeMapping.getDataNode2() == dataNode)
          return true;
    return false;
  }

  /**
   * Returns true if the given attribute edge appears in the list of attribute 
   * edge mappings.
   * @param attrEdgeMappingList - the list of attribute edge mappings
   *        attrEdge - the attribute edge
   * @return true if the given attribute edge appears in the list of attribute 
   *         edge mappings; false otherwise
   */
  public static boolean hasAttributeEdgeMapping(ArrayList<AttributeEdgeMapping> attrEdgeMappingList, AttributeEdge attrEdge) {
    for (AttributeEdgeMapping attrEdgeMapping : attrEdgeMappingList)
      for (AttributeEdgePair attrEdgePair: attrEdgeMapping)
        if (attrEdgeMapping.getAttributeEdge2() == attrEdge)
          return true;
    return false;
  }

}