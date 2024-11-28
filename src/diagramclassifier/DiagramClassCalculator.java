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

      ArrayList<Edge> diagramEdgeList = diagramGraph.getEdgeList();
      ArrayList<Node> diagramNodeList = diagramGraph.getNodeList();

      ArrayList<DiagramGraph> basicPatternGraphList = new ArrayList<DiagramGraph>();

      ArrayList<ArrayList<NodePair>> diagramCoverageNodeMappingList = new ArrayList<ArrayList<NodePair>>();
      ArrayList<ArrayList<EdgePair>> diagramCoverageEdgeMappingList = new ArrayList<ArrayList<EdgePair>>();
      ArrayList<ArrayList<DataNodePair>> diagramCoverageDataNodeMappingList = new ArrayList<ArrayList<DataNodePair>>();
      ArrayList<ArrayList<AttributeEdgePair>> diagramCoverageAttrEdgeMappingList = new ArrayList<ArrayList<AttributeEdgePair>>();

      ArrayList<Node> diagramGenNodeList = diagramGraph.getNodeList("Generalization");
      ArrayList<Node> diagramAssocNodeList = diagramGraph.getNodeList("Association");
      ArrayList<Node> diagramPropNodeList = diagramGraph.getNodeList("Property");
      ArrayList<Node> diagramClassNodeList = diagramGraph.getNodeList("Class");
      ArrayList<Node> diagramDataTypeNodeList = diagramGraph.getNodeList("DataType");

      int propNodeCountStart = 0;
      int propNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph1();

        Node basicPatternPropNode =  basicPatternGraph.getNodeList("Property").get(0);
        Edge basicPatternTypeEdge = basicPatternPropNode.getOutgoingEdge("type");
        Node basicPatternDataTypeNode = basicPatternTypeEdge.getTarget();
        Edge basicPatternClassEdge = basicPatternPropNode.getOutgoingEdge("class");
        Node basicPatternClassNode = basicPatternClassEdge.getTarget();
        Edge basicPatternOwnedAttrEdge = Node.getEdge(basicPatternClassNode, basicPatternPropNode, "ownedAttribute");  
        AttributeEdge basicPatternLowerAttrEdge = basicPatternPropNode.getAttributeEdge("lower");
        DataNode basicPatternDataNode1 = basicPatternLowerAttrEdge.getTarget();
        AttributeEdge basicPatternUpperAttrEdge = basicPatternPropNode.getAttributeEdge("upper");
        DataNode basicPatternDataNode2 = basicPatternUpperAttrEdge.getTarget();

        for (propNodeCount = propNodeCountStart; propNodeCount < diagramPropNodeList.size(); propNodeCount++) {

          Node diagramPropNode = diagramPropNodeList.get(propNodeCount);
          Edge diagramTypeEdge = diagramPropNode.getOutgoingEdge("type");
          
          if (diagramTypeEdge.getTarget().getType().equals("DataType")) {

            Node diagramDataTypeNode = diagramTypeEdge.getTarget();
            Edge diagramClassEdge = diagramPropNode.getOutgoingEdge("class");
            Node diagramClassNode = diagramClassEdge.getTarget();
            Edge diagramOwnedAttrEdge = Node.getEdge(diagramClassNode, diagramPropNode, "ownedAttribute");  
            AttributeEdge diagramLowerAttrEdge = diagramPropNode.getAttributeEdge("lower");
            DataNode diagramDataNode1 = diagramLowerAttrEdge.getTarget();
            AttributeEdge diagramUpperAttrEdge = diagramPropNode.getAttributeEdge("upper");
            DataNode diagramDataNode2 = diagramUpperAttrEdge.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode, diagramClassNode);
            NodePair nodePair2 = new NodePair(basicPatternDataTypeNode, diagramDataTypeNode);
            NodePair nodePair3 = new NodePair(basicPatternPropNode, diagramPropNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge, diagramClassEdge);
            EdgePair edgePair2 = new EdgePair(basicPatternTypeEdge, diagramTypeEdge);
            EdgePair edgePair3 = new EdgePair(basicPatternOwnedAttrEdge, diagramOwnedAttrEdge);
            edgeMapping.add(edgePair1);
            edgeMapping.add(edgePair2);
            edgeMapping.add(edgePair3);
            diagramCoverageEdgeMappingList.add(edgeMapping);
            ArrayList<DataNodePair> dataNodeMapping = new ArrayList<DataNodePair>();
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramDataNode2);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge, diagramLowerAttrEdge);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge, diagramUpperAttrEdge);
            attrEdgeMapping.add(attrEdgePair1);
            attrEdgeMapping.add(attrEdgePair2);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            propNodeCountStart = propNodeCount + 1;

            break;

          }

        }

        if (propNodeCount == diagramPropNodeList.size())
          break;

      }

      for (int i = 0; i < diagramGenNodeList.size(); i++) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph2();

        Node basicPatternGenNode = basicPatternGraph.getNodeList("Generalization").get(0);
        Edge basicPatternGeneralEdge = basicPatternGenNode.getOutgoingEdge("general");
        Edge basicPatternSpecifciEdge = basicPatternGenNode.getOutgoingEdge("specific");
        Node basicPatternClassNode1 = basicPatternGeneralEdge.getTarget();
        Node basicPatternClassNode2 = basicPatternSpecifciEdge.getTarget();

        Node diagramGenNode = diagramGenNodeList.get(i);
        Edge diagramGeneralEdge = diagramGenNode.getOutgoingEdge("general");
        Edge diagramSpecifciEdge = diagramGenNode.getOutgoingEdge("specific");
        Node diagramClassNode1 = diagramGeneralEdge.getTarget();
        Node diagramClassNode2 = diagramSpecifciEdge.getTarget();
        
        ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
        NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramClassNode2);
        NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramClassNode1);
        NodePair nodePair3 = new NodePair(basicPatternGenNode, diagramGenNode);
        nodeMapping.add(nodePair1);
        nodeMapping.add(nodePair2);
        nodeMapping.add(nodePair3);
        diagramCoverageNodeMappingList.add(nodeMapping);
        ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
        EdgePair edgePair1 = new EdgePair(basicPatternGeneralEdge, diagramGeneralEdge);
        EdgePair edgePair2 = new EdgePair(basicPatternSpecifciEdge, diagramSpecifciEdge);
        edgeMapping.add(edgePair1);
        edgeMapping.add(edgePair2);
        diagramCoverageEdgeMappingList.add(edgeMapping);

        basicPatternGraphList.add(basicPatternGraph);

      }

      int assocNodeCountStart = 0;
      int assocNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph3();

        Node basicPatternAssocNode =  basicPatternGraph.getNodeList("Association").get(0);
        ArrayList<Edge> basicPatternMemberEndEdgeList = basicPatternAssocNode.getOutgoingEdgeList("memberEnd");
        Node basicPatternPropNode1 = basicPatternMemberEndEdgeList.get(0).getTarget();
        Edge basicPatternClassEdge1 = basicPatternPropNode1.getOutgoingEdge("class");
        Node basicPatternClassNode1 = basicPatternClassEdge1.getTarget();
        Node basicPatternPropNode2 = basicPatternMemberEndEdgeList.get(1).getTarget();
        Edge basicPatternClassEdge2 = basicPatternPropNode2.getOutgoingEdge("class");
        Node basicPatternClassNode2 = basicPatternClassEdge2.getTarget();
        Edge basicPatternTypeEdge1 = basicPatternPropNode1.getOutgoingEdge("type");
        Edge basicPatternTypeEdge2 = basicPatternPropNode2.getOutgoingEdge("type");
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

        for (assocNodeCount = assocNodeCountStart; assocNodeCount < diagramAssocNodeList.size(); assocNodeCount++) {

          Node diagramAssocNode = diagramAssocNodeList.get(assocNodeCount);
          ArrayList<Edge> diagramOwnedEndEdgeList = diagramAssocNode.getOutgoingEdgeList("ownedEnd");

          if (diagramOwnedEndEdgeList.size() == 0) {

            ArrayList<Edge> diagramMemberEndEdgeList = diagramAssocNode.getOutgoingEdgeList("memberEnd");
            Node diagramPropNode1 = diagramMemberEndEdgeList.get(0).getTarget();        
            Edge diagramClassEdge1 = diagramPropNode1.getOutgoingEdge("class");
            Node diagramClassNode1 = diagramClassEdge1.getTarget();
            Node diagramPropNode2 = diagramMemberEndEdgeList.get(1).getTarget();        
            Edge diagramClassEdge2 = diagramPropNode2.getOutgoingEdge("class");
            Node diagramClassNode2 = diagramClassEdge2.getTarget();
            Edge diagramTypeEdge1 = diagramPropNode1.getOutgoingEdge("type");
            Edge diagramTypeEdge2 = diagramPropNode2.getOutgoingEdge("type");
            Edge diagramOwnedAttrEdge1 = Node.getEdge(diagramClassNode1, diagramPropNode1, "ownedAttribute");       
            Edge diagramOwnedAttrEdge2 = Node.getEdge(diagramClassNode2, diagramPropNode2, "ownedAttribute");
            AttributeEdge diagramLowerAttrEdge1 = diagramPropNode1.getAttributeEdge("lower");
            DataNode diagramDataNode1 = diagramLowerAttrEdge1.getTarget();
            AttributeEdge diagramUpperAttrEdge1 = diagramPropNode1.getAttributeEdge("upper");
            DataNode diagramDataNode2 = diagramUpperAttrEdge1.getTarget();
            AttributeEdge diagramLowerAttrEdge2 = diagramPropNode2.getAttributeEdge("lower");
            DataNode diagramDataNode3 = diagramLowerAttrEdge2.getTarget();
            AttributeEdge diagramUpperAttrEdge2 = diagramPropNode2.getAttributeEdge("upper");
            DataNode diagramDataNode4 = diagramUpperAttrEdge2.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramClassNode1);
            NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramClassNode2);
            NodePair nodePair3 = new NodePair(basicPatternPropNode1, diagramPropNode1);
            NodePair nodePair4 = new NodePair(basicPatternPropNode2, diagramPropNode2);
            NodePair nodePair5 = new NodePair(basicPatternAssocNode, diagramAssocNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            nodeMapping.add(nodePair4);
            nodeMapping.add(nodePair5);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge1, diagramClassEdge1);
            EdgePair edgePair2 = new EdgePair(basicPatternClassEdge2, diagramClassEdge1);
            EdgePair edgePair3 = new EdgePair(basicPatternTypeEdge1, diagramTypeEdge1);
            EdgePair edgePair4 = new EdgePair(basicPatternTypeEdge2, diagramTypeEdge2);
            EdgePair edgePair5 = new EdgePair(basicPatternOwnedAttrEdge1, diagramOwnedAttrEdge1);
            EdgePair edgePair6 = new EdgePair(basicPatternOwnedAttrEdge2, diagramOwnedAttrEdge2);
            EdgePair edgePair7 = new EdgePair(basicPatternMemberEndEdgeList.get(0), diagramMemberEndEdgeList.get(0));
            EdgePair edgePair8 = new EdgePair(basicPatternMemberEndEdgeList.get(1), diagramMemberEndEdgeList.get(1));
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
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramDataNode2);
            DataNodePair dataNodePair3 = new DataNodePair(basicPatternDataNode3, diagramDataNode3);
            DataNodePair dataNodePair4 = new DataNodePair(basicPatternDataNode4, diagramDataNode4);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            dataNodeMapping.add(dataNodePair3);
            dataNodeMapping.add(dataNodePair4);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge1, diagramLowerAttrEdge1);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge1, diagramUpperAttrEdge1);
            AttributeEdgePair attrEdgePair3 = new AttributeEdgePair(basicPatternLowerAttrEdge2, diagramLowerAttrEdge2);
            AttributeEdgePair attrEdgePair4 = new AttributeEdgePair(basicPatternUpperAttrEdge2, diagramUpperAttrEdge2);
            attrEdgeMapping.add(attrEdgePair1);
            attrEdgeMapping.add(attrEdgePair2);
            attrEdgeMapping.add(attrEdgePair3);
            attrEdgeMapping.add(attrEdgePair4);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            assocNodeCountStart = assocNodeCount + 1;

            break;

          }

        }

        if (assocNodeCount == diagramAssocNodeList.size())
          break;

      }

      assocNodeCountStart = 0;
      assocNodeCount = 0;

      while (true) {

        DiagramGraph basicPatternGraph = createBasicPatternGraph4();

        Node basicPatternAssocNode =  basicPatternGraph.getNodeList("Association").get(0);
        ArrayList<Edge> basicPatternMemberEndEdgeList = basicPatternAssocNode.getOutgoingEdgeList("memberEnd");
        Edge basicPatternOwnedEndEdge = basicPatternAssocNode.getOutgoingEdge("ownedEnd");
        Node basicPatternPropNode2 = basicPatternOwnedEndEdge.getTarget();
        Edge basicPatternTypeEdge2 = basicPatternPropNode2.getOutgoingEdge("type");
        Edge basicPatternMemberEndEdge1 = null;
        Edge basicPatternMemberEndEdge2 = null;
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
        Edge basicPatternTypeEdge1 = basicPatternPropNode1.getOutgoingEdge("type");
        Edge basicPatternOwnedAttrEdge1 = Node.getEdge(basicPatternClassNode1, basicPatternPropNode1, "ownedAttribute"); 
        Node basicPatternClassNode2 = basicPatternTypeEdge1.getTarget();
        AttributeEdge basicPatternLowerAttrEdge1 = basicPatternPropNode1.getAttributeEdge("lower");
        DataNode basicPatternDataNode1 = basicPatternLowerAttrEdge1.getTarget();
        AttributeEdge basicPatternUpperAttrEdge1 = basicPatternPropNode1.getAttributeEdge("upper");
        DataNode basicPatternDataNode2 = basicPatternUpperAttrEdge1.getTarget(); 
        AttributeEdge basicPatternLowerAttrEdge2 = basicPatternPropNode2.getAttributeEdge("lower");
        DataNode basicPatternDataNode3 = basicPatternLowerAttrEdge2.getTarget();
        AttributeEdge basicPatternUpperAttrEdge2 = basicPatternPropNode2.getAttributeEdge("upper");
        DataNode basicPatternDataNode4 = basicPatternUpperAttrEdge2.getTarget(); 

        for (assocNodeCount = assocNodeCountStart; assocNodeCount < diagramAssocNodeList.size(); assocNodeCount++) {

          Node diagramAssocNode = diagramAssocNodeList.get(assocNodeCount);
          ArrayList<Edge> diagramOwnedEndEdgeList = diagramAssocNode.getOutgoingEdgeList("ownedEnd");

          if (diagramOwnedEndEdgeList.size() == 1) {
            ArrayList<Edge> diagramMemberEndEdgeList = diagramAssocNode.getOutgoingEdgeList("memberEnd");
            Edge diagramOwnedEndEdge = diagramAssocNode.getOutgoingEdge("ownedEnd");
            Node diagramPropNode2 = diagramOwnedEndEdge.getTarget();  
            Edge diagramTypeEdge2 = diagramPropNode2.getOutgoingEdge("type");
            Edge diagramMemberEndEdge1 = null;
            Edge diagramMemberEndEdge2 = null;
            if (diagramMemberEndEdgeList.get(0).getTarget() == diagramPropNode2) {
              diagramMemberEndEdge1 = diagramMemberEndEdgeList.get(1);
              diagramMemberEndEdge2 = diagramMemberEndEdgeList.get(0);
            } else {
              diagramMemberEndEdge1 = diagramMemberEndEdgeList.get(0);
              diagramMemberEndEdge2 = diagramMemberEndEdgeList.get(1);
            }
            Node diagramPropNode1 = diagramMemberEndEdge1.getTarget();   
            Edge diagramClassEdge1 = diagramPropNode1.getOutgoingEdge("class");
            Node diagramClassNode1 = diagramClassEdge1.getTarget();
            Edge diagramTypeEdge1 = diagramPropNode1.getOutgoingEdge("type");
            Edge diagramOwnedAttrEdge1 = Node.getEdge(diagramClassNode1, diagramPropNode1, "ownedAttribute"); 
            Node diagramClassNode2 = diagramTypeEdge1.getTarget();
            AttributeEdge diagramLowerAttrEdge1 = diagramPropNode1.getAttributeEdge("lower");
            DataNode diagramDataNode1 = diagramLowerAttrEdge1.getTarget();
            AttributeEdge diagramUpperAttrEdge1 = diagramPropNode1.getAttributeEdge("upper");
            DataNode diagramDataNode2 = diagramUpperAttrEdge1.getTarget();
            AttributeEdge diagramLowerAttrEdge2 = diagramPropNode2.getAttributeEdge("lower");
            DataNode diagramDataNode3 = diagramLowerAttrEdge2.getTarget();
            AttributeEdge diagramUpperAttrEdge2 = diagramPropNode2.getAttributeEdge("upper");
            DataNode diagramDataNode4 = diagramUpperAttrEdge2.getTarget();

            ArrayList<NodePair> nodeMapping = new ArrayList<NodePair>();
            NodePair nodePair1 = new NodePair(basicPatternClassNode1, diagramClassNode1);
            NodePair nodePair2 = new NodePair(basicPatternClassNode2, diagramClassNode2);
            NodePair nodePair3 = new NodePair(basicPatternPropNode1, diagramPropNode1);
            NodePair nodePair4 = new NodePair(basicPatternPropNode2, diagramPropNode2);
            NodePair nodePair5 = new NodePair(basicPatternAssocNode, diagramAssocNode);
            nodeMapping.add(nodePair1);
            nodeMapping.add(nodePair2);
            nodeMapping.add(nodePair3);
            nodeMapping.add(nodePair4);
            nodeMapping.add(nodePair5);
            diagramCoverageNodeMappingList.add(nodeMapping);
            ArrayList<EdgePair> edgeMapping = new ArrayList<EdgePair>();
            EdgePair edgePair1 = new EdgePair(basicPatternClassEdge1, diagramClassEdge1);
            EdgePair edgePair2 = new EdgePair(basicPatternTypeEdge1, diagramTypeEdge1);
            EdgePair edgePair3 = new EdgePair(basicPatternTypeEdge2, diagramTypeEdge2);
            EdgePair edgePair4 = new EdgePair(basicPatternOwnedAttrEdge1, diagramOwnedAttrEdge1);
            EdgePair edgePair5 = new EdgePair(basicPatternMemberEndEdge1, diagramMemberEndEdge1);
            EdgePair edgePair6 = new EdgePair(basicPatternMemberEndEdge2, diagramMemberEndEdge2);
            EdgePair edgePair7 = new EdgePair(basicPatternOwnedEndEdge, diagramOwnedEndEdge);
            edgeMapping.add(edgePair1);
            edgeMapping.add(edgePair2);
            edgeMapping.add(edgePair3);
            edgeMapping.add(edgePair4);
            edgeMapping.add(edgePair5);
            edgeMapping.add(edgePair6);
            edgeMapping.add(edgePair7);
            diagramCoverageEdgeMappingList.add(edgeMapping);
            ArrayList<DataNodePair> dataNodeMapping = new ArrayList<DataNodePair>();
            DataNodePair dataNodePair1 = new DataNodePair(basicPatternDataNode1, diagramDataNode1);
            DataNodePair dataNodePair2 = new DataNodePair(basicPatternDataNode2, diagramDataNode2);
            DataNodePair dataNodePair3 = new DataNodePair(basicPatternDataNode3, diagramDataNode3);
            DataNodePair dataNodePair4 = new DataNodePair(basicPatternDataNode4, diagramDataNode4);
            dataNodeMapping.add(dataNodePair1);
            dataNodeMapping.add(dataNodePair2);
            dataNodeMapping.add(dataNodePair3);
            dataNodeMapping.add(dataNodePair4);
            diagramCoverageDataNodeMappingList.add(dataNodeMapping);
            ArrayList<AttributeEdgePair> attrEdgeMapping = new ArrayList<AttributeEdgePair>();
            AttributeEdgePair attrEdgePair1 = new AttributeEdgePair(basicPatternLowerAttrEdge1, diagramLowerAttrEdge1);
            AttributeEdgePair attrEdgePair2 = new AttributeEdgePair(basicPatternUpperAttrEdge1, diagramUpperAttrEdge1);
            AttributeEdgePair attrEdgePair3 = new AttributeEdgePair(basicPatternLowerAttrEdge2, diagramLowerAttrEdge2);
            AttributeEdgePair attrEdgePair4 = new AttributeEdgePair(basicPatternUpperAttrEdge2, diagramUpperAttrEdge2);
            attrEdgeMapping.add(attrEdgePair1);
            attrEdgeMapping.add(attrEdgePair2);
            attrEdgeMapping.add(attrEdgePair3);
            attrEdgeMapping.add(attrEdgePair4);
            diagramCoverageAttrEdgeMappingList.add(attrEdgeMapping);

            basicPatternGraphList.add(basicPatternGraph);

            assocNodeCountStart = assocNodeCount + 1;

            break;

          }

        }

        if (assocNodeCount == diagramAssocNodeList.size())
          break;

      }

      ArrayList<Node> nodeList = diagramGraph.getNodeList();
      ArrayList<Edge> edgeList = diagramGraph.getEdgeList();
      basicPatternCoverage = true;
      for (Node node : nodeList)
        if (!isMappedToBasicPatternNode(diagramCoverageNodeMappingList, node)) {
          basicPatternCoverage = false;
          return null;
        }
      for (Edge edge : edgeList)
        if (!isMappedToBasicPatternEdge(diagramCoverageEdgeMappingList, edge)) {
          basicPatternCoverage = false;
          return null;
        }

      renameBasicPattenGraphElements(diagramGraph, diagramCoverageNodeMappingList, basicPatternGraphList);
 
      DiagramGraph complexPatternGraph = createComplexPatternGraph(basicPatternGraphList);

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

  /**
   * Generates an E-graph of the basic patter 2. 
   * @return the E-graph of the basic patter 2
   */
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

  /**
   * Generates an E-graph of the basic patter 3. 
   * @return the E-graph of the basic patter 3
   */
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

  /**
   * Generates an E-graph of the basic patter 4. 
   * @return the E-graph of the basic patter 4
   */
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
    for (ArrayList<NodePair> nodeMapping: nodeMappingList) 
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
   * Extends the given complex pattern E-graph with the basic pattern E-graph.
   * @param complexPatternGraph - the complex pattern E-graph
   *        basicPatternGraph - the basic pattern E-graph
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

  /**
   * Returns true if the given graph node appears in the list of graph node 
   * mappings.
   * @param nodeMappingList - the list of graph node mappings
   *        node - the graph node
   * @return true if the given graph node appears in the list of graph node 
   *         mappings; false otherwise
   */
  public static boolean hasNodeMapping(ArrayList<ArrayList<NodePair>> nodeMappingList, Node node) {
    for (ArrayList<NodePair> nodeMapping : nodeMappingList)
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
  public static boolean hasEdgeMapping(ArrayList<ArrayList<EdgePair>> edgeMappingList, Edge edge) {
    for (ArrayList<EdgePair> edgeMapping : edgeMappingList)
      for (EdgePair edgePair: edgeMapping)
        if (edgePair.getEdge2() == edge)
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
  public static boolean hasDataNodeMapping(ArrayList<ArrayList<DataNodePair>> dataNodeMappingList, DataNode dataNode) {
    for (ArrayList<DataNodePair> dataNodeMapping : dataNodeMappingList)
      for (DataNodePair dataNodePair: dataNodeMapping)
        if (dataNodePair.getDataNode2() == dataNode)
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
  public static boolean hasAttributeEdgeMapping(ArrayList<ArrayList<AttributeEdgePair>> attrEdgeMappingList, AttributeEdge attrEdge) {
    for (ArrayList<AttributeEdgePair> attrEdgeMapping : attrEdgeMappingList)
      for (AttributeEdgePair attrEdgePair: attrEdgeMapping)
        if (attrEdgePair.getAttributeEdge2() == attrEdge)
          return true;
    return false;
  }

  public static void renameBasicPattenGraphElements(
           DiagramGraph diagramGraph, 
           ArrayList<ArrayList<NodePair>> diagramCoverageNodeMappingList, 
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

 
  public static boolean isMappedToBasicPatternNode(ArrayList<ArrayList<NodePair>> diagramCoverageNodeMappingList, Node node) {
    for (ArrayList<NodePair> nodeMapping : diagramCoverageNodeMappingList)
      for (NodePair nodePair: nodeMapping)
      if (nodePair.getNode2() == node)
         return true;
    return false;
  }

  public static boolean isMappedToBasicPatternEdge(ArrayList<ArrayList<EdgePair>> diagramCoverageEdgeMappingList, Edge edge) {
    for (ArrayList<EdgePair> edgeMapping : diagramCoverageEdgeMappingList)
      for (EdgePair edgePair: edgeMapping)
      if (edgePair.getEdge2() == edge)
         return true;
    return false;
  }

}