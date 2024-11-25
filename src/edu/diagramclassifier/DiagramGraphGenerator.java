package edu.diagramclassifier;

import java.io.*;
import java.util.*;
import java.lang.*;
import org.argouml.model.*;
import org.xml.sax.InputSource;

/**
 * This class allows to generate an E-graph representing the given ArgoUML class diagram.
 * @author Alexey Tazin 
 */
public class DiagramGraphGenerator {


  /**
   * Generates an E-graph representing the given class diagram.
   * @param modelImpl - the ArgoUML model implemetation
   *        model - the ArgoUML model
   *        diagramGraph - the class diagram graph
   * @return the E-graph representing the given class diagram
   */
  public static void generateDiagramGraph(ModelImplementation modelImpl, Object model, DiagramGraph diagramGraph) {
    try {

      ArrayList<DataNode> dataNodeList = diagramGraph.getDataNodeList();
      ArrayList<Edge> edgeList = diagramGraph.getEdgeList();
      ArrayList<AttributeEdge> attrEdgeList = diagramGraph.getAttributeEdgeList();
      ArrayList<Node> nodeList = diagramGraph.getNodeList();

      ArrayList<Node> classNodeList = new ArrayList<Node>();
      ArrayList<Node> dataTypeNodeList = new ArrayList<Node>();

      Collection classList1 = Model.getCoreHelper().getAllClasses(model);
      Collection dataTypeList1 = Model.getCoreHelper().getAllDataTypes(model);

      ArrayList<Object> visitedAssocList = new ArrayList<Object>();

      for (Object c1 : classList1) {
        for (Object c2 : classList1) {
          Node classNode1 = getClassNode(classNodeList, Model.getFacade().getName(c1));
          if (classNode1 == null) {
            classNode1 = new Node(Model.getFacade().getName(c1), "Class");
            classNodeList.add(classNode1);
            nodeList.add(classNode1);
          }
          Node classNode2 = getClassNode(classNodeList, Model.getFacade().getName(c2));
          if (classNode2 == null) {
            classNode2 = new Node(Model.getFacade().getName(c2), "Class");
            classNodeList.add(classNode2);
            nodeList.add(classNode2);
          }
          Object gen1 = modelImpl.getCoreHelper().getGeneralization(c1, c2);
          if (gen1 != null) {
             Node genNode = new Node(Model.getFacade().getName(gen1), "Generalization");
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
          }
          ArrayList<Object> assocList = getAssociations(modelImpl, c1, c2);
          for (Object assoc : assocList)
            if (!visitedAssocList.contains(assoc)) {
              Object assocEnd1 = Model.getCoreHelper().getAssociationEnd(c1, assoc);
              Object assocEnd2 = Model.getCoreHelper().getAssociationEnd(c2, assoc);
              boolean assocEnd1Navigable = Model.getFacade().isNavigable(assocEnd1);
              boolean assocEnd2Navigable = Model.getFacade().isNavigable(assocEnd2);
              if (assocEnd1Navigable && assocEnd2Navigable) {
                Node assocNode = new Node(Model.getFacade().getName(assoc), "Association");
                nodeList.add(assocNode);
                Node propNode1 = new Node(Model.getFacade().getName(assocEnd2), "Property");
                nodeList.add(propNode1);
                Node propNode2 = new Node(Model.getFacade().getName(assocEnd1), "Property");
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
                int lowerBound1 = Model.getFacade().getLower(assocEnd2);
                int upperBound1 = Model.getFacade().getUpper(assocEnd2);
                DataNode dataNode = new DataNode(String.valueOf(lowerBound1), "Integer", null);
                dataNodeList.add(dataNode);
                AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound1), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                int lowerBound2 = Model.getFacade().getLower(assocEnd1);
                int upperBound2 = Model.getFacade().getUpper(assocEnd1);
                dataNode = new DataNode(String.valueOf(lowerBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "lower", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                visitedAssocList.add(assoc);
              } else if (!assocEnd1Navigable && assocEnd2Navigable) {
                Node assocNode = new Node(Model.getFacade().getName(assoc), "Association");
                nodeList.add(assocNode);
                Node propNode1 = new Node(Model.getFacade().getName(assocEnd2), "Property");
                nodeList.add(propNode1);
                Node propNode2 = new Node(Model.getFacade().getName(assocEnd1), "Property");
                nodeList.add(propNode2);
                Edge memberEndEdge1 = new Edge(null, "memberEnd", assocNode, propNode1);
                Edge memberEndEdge2 = new Edge(null, "memberEnd", assocNode, propNode2);
                Edge typeEdge1 = new Edge(null, "type", propNode1, classNode2);
                Edge typeEdge2 = new Edge(null, "type", propNode2, classNode1);
                Edge ownedAttributeEdge = new Edge(null, "ownedAttribute", classNode1, propNode1);
                Edge ownedEndEdge = new Edge(null, "ownedEnd", assocNode, propNode2);
                Edge assocEdge1 = new Edge(null, "association", propNode1, assocNode);
                Edge assocEdge2 = new Edge(null, "association", propNode2, assocNode);
                Edge classEdge = new Edge(null, "class", propNode1, classNode1);
                edgeList.add(memberEndEdge1);
                edgeList.add(memberEndEdge2);
                edgeList.add(typeEdge1);
                edgeList.add(typeEdge2);
                edgeList.add(ownedAttributeEdge);
                edgeList.add(ownedEndEdge);
                edgeList.add(assocEdge1);
                edgeList.add(assocEdge2);
                edgeList.add(classEdge);
                ArrayList<Edge> outgoingEdgeList = assocNode.getOutgoingEdgeList();
                outgoingEdgeList.add(memberEndEdge1);
                outgoingEdgeList.add(memberEndEdge2);
                outgoingEdgeList.add(ownedEndEdge);
                outgoingEdgeList = propNode1.getOutgoingEdgeList();
                outgoingEdgeList.add(typeEdge1);
                outgoingEdgeList.add(classEdge);
                outgoingEdgeList.add(assocEdge1);
                outgoingEdgeList = propNode2.getOutgoingEdgeList();
                outgoingEdgeList.add(typeEdge2);
                outgoingEdgeList.add(assocEdge2);
                outgoingEdgeList = classNode1.getOutgoingEdgeList();
                outgoingEdgeList.add(ownedAttributeEdge);
                ArrayList<Edge> incomingEdgeList = assocNode.getIncomingEdgeList();
                incomingEdgeList.add(assocEdge1);
                incomingEdgeList.add(assocEdge2);
                incomingEdgeList = propNode1.getIncomingEdgeList();
                incomingEdgeList.add(ownedAttributeEdge);
                incomingEdgeList.add(memberEndEdge1);
                incomingEdgeList = propNode2.getIncomingEdgeList();
                incomingEdgeList.add(ownedEndEdge);
                incomingEdgeList.add(memberEndEdge2);
                incomingEdgeList = classNode1.getIncomingEdgeList();
                incomingEdgeList.add(typeEdge2);
                incomingEdgeList.add(classEdge);
                incomingEdgeList = classNode2.getIncomingEdgeList();
                incomingEdgeList.add(typeEdge1);
                int lowerBound1 = Model.getFacade().getLower(assocEnd2);
                int upperBound1 = Model.getFacade().getUpper(assocEnd2);
                DataNode dataNode = new DataNode(String.valueOf(lowerBound1), "Integer", null);
                dataNodeList.add(dataNode);
                AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound1), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                int lowerBound2 = Model.getFacade().getLower(assocEnd1);
                int upperBound2 = Model.getFacade().getUpper(assocEnd1);
                dataNode = new DataNode(String.valueOf(lowerBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "lower", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                visitedAssocList.add(assoc);
              } else if (assocEnd1Navigable && !assocEnd2Navigable) {
                Node assocNode = new Node(Model.getFacade().getName(assoc), "Association");
                nodeList.add(assocNode);
                Node propNode1 = new Node(Model.getFacade().getName(assocEnd2), "Property");
                nodeList.add(propNode1);
                Node propNode2 = new Node(Model.getFacade().getName(assocEnd1), "Property");
                nodeList.add(propNode2);
                Edge memberEndEdge1 = new Edge(null, "memberEnd", assocNode, propNode1);
                Edge memberEndEdge2 = new Edge(null, "memberEnd", assocNode, propNode2);
                Edge typeEdge1 = new Edge(null, "type", propNode1, classNode2);
                Edge typeEdge2 = new Edge(null, "type", propNode2, classNode1);
                Edge ownedAttributeEdge = new Edge(null, "ownedAttribute", classNode2, propNode2);
                Edge ownedEndEdge = new Edge(null, "ownedEnd", assocNode, propNode1);
                Edge assocEdge1 = new Edge(null, "association", propNode1, assocNode);
                Edge assocEdge2 = new Edge(null, "association", propNode2, assocNode);
                Edge classEdge = new Edge(null, "class", propNode2, classNode2);
                edgeList.add(memberEndEdge1);
                edgeList.add(memberEndEdge2);
                edgeList.add(typeEdge1);
                edgeList.add(typeEdge2);
                edgeList.add(ownedAttributeEdge);
                edgeList.add(ownedEndEdge);
                edgeList.add(assocEdge1);
                edgeList.add(assocEdge2);
                edgeList.add(classEdge);
                ArrayList<Edge> outgoingEdgeList = assocNode.getOutgoingEdgeList();
                outgoingEdgeList.add(memberEndEdge1);
                outgoingEdgeList.add(memberEndEdge2);
                outgoingEdgeList.add(ownedEndEdge);
                outgoingEdgeList = propNode1.getOutgoingEdgeList();
                outgoingEdgeList.add(typeEdge1);
                outgoingEdgeList.add(assocEdge1);
                outgoingEdgeList = propNode2.getOutgoingEdgeList();
                outgoingEdgeList.add(typeEdge2);
                outgoingEdgeList.add(classEdge);
                outgoingEdgeList.add(assocEdge2);
                outgoingEdgeList = classNode2.getOutgoingEdgeList();
                outgoingEdgeList.add(ownedAttributeEdge);
                ArrayList<Edge> incomingEdgeList = assocNode.getIncomingEdgeList();
                incomingEdgeList.add(assocEdge1);
                incomingEdgeList.add(assocEdge2);
                incomingEdgeList = propNode1.getIncomingEdgeList();
                incomingEdgeList.add(ownedEndEdge);
                incomingEdgeList.add(memberEndEdge1);
                incomingEdgeList = propNode2.getIncomingEdgeList();
                incomingEdgeList.add(ownedAttributeEdge);
                incomingEdgeList.add(memberEndEdge2);
                incomingEdgeList = classNode1.getIncomingEdgeList();
                incomingEdgeList.add(typeEdge2);
                incomingEdgeList = classNode2.getIncomingEdgeList();
                incomingEdgeList.add(typeEdge1);
                incomingEdgeList.add(classEdge);
                int lowerBound1 = Model.getFacade().getLower(assocEnd2);
                int upperBound1 = Model.getFacade().getUpper(assocEnd2);
                DataNode dataNode = new DataNode(String.valueOf(lowerBound1), "Integer", null);
                dataNodeList.add(dataNode);
                AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode1.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound1), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode1, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                int lowerBound2 = Model.getFacade().getLower(assocEnd1);
                int upperBound2 = Model.getFacade().getUpper(assocEnd1);
                dataNode = new DataNode(String.valueOf(lowerBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "lower", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList = propNode2.getAttributeEdgeList();
                outgoingAttrEdgeList.add(attrEdge);
                dataNode = new DataNode(String.valueOf(upperBound2), "Integer", null);
                dataNodeList.add(dataNode);
                attrEdge = new AttributeEdge(null, "upper", propNode2, dataNode);
                attrEdgeList.add(attrEdge);
                outgoingAttrEdgeList.add(attrEdge);
                visitedAssocList.add(assoc);
              }
            }
        }
      }

      for (Object c : classList1) {
        for (Object dt : dataTypeList1) {
          Node classNode = getClassNode(classNodeList, Model.getFacade().getName(c));
          Node dataTypeNode = getDataTypeNode(dataTypeNodeList, Model.getFacade().getName(dt));
          if (dataTypeNode == null) {
            dataTypeNode = new Node(Model.getFacade().getName(dt), "DataType");
            dataTypeNodeList.add(dataTypeNode);
            nodeList.add(dataTypeNode);
          }  
          for (Object attr : modelImpl.getFacade().getAttributes(c)) {
            Object type = modelImpl.getFacade().getType(attr);
            if (type == dt) {
              Node propNode = new Node(Model.getFacade().getName(attr), "Property");
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
              ArrayList<Edge> incomingEdgeList = classNode.getIncomingEdgeList();
              incomingEdgeList.add(classEdge);
              incomingEdgeList = dataTypeNode.getIncomingEdgeList();
              incomingEdgeList.add(typeEdge);
              incomingEdgeList = propNode.getIncomingEdgeList();
              incomingEdgeList.add(ownedAttrEdge);
              DataNode dataNode = new DataNode("1", "Integer", null);
              dataNodeList.add(dataNode);
              AttributeEdge attrEdge = new AttributeEdge(null, "lower", propNode, dataNode);
              attrEdgeList.add(attrEdge);
              ArrayList<AttributeEdge> outgoingAttrEdgeList = propNode.getAttributeEdgeList();
              outgoingAttrEdgeList.add(attrEdge);
              attrEdge = new AttributeEdge(null, "upper", propNode, dataNode);
              attrEdgeList.add(attrEdge);
              outgoingAttrEdgeList.add(attrEdge);
            }            
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Returns a list of graph edges of memberEnd type for the given graph node 
   * of Association type.
   * @param propNode - the graph node of Association type
   * @return the list of graph edges of memberEnd type for the given graph node 
   * of Association type
   */
  public static ArrayList<Edge> getMemberEndEdgeList(Node assocNode) {
    ArrayList<Edge> outgoingEdgeList = assocNode.getOutgoingEdgeList();
    ArrayList<Edge> memberEndEdgeList = new ArrayList<Edge>();
    for (Edge edge: outgoingEdgeList)
      if (edge.getType().equals("memberEnd"))
        memberEndEdgeList.add(edge);
    return memberEndEdgeList;    
  }

  /**
   * Returns true if the given graph node of Property type has a graph edge.
   * of class type.
   * @param propNode - the graph node of Property type
   * @return true if the given graph node of Property type has a graph edge; 
   *         fasle otherwise
   */
  public static boolean hasClassEdge(Node propNode) {
    ArrayList<Edge> outgoingEdgeList = propNode.getOutgoingEdgeList();
    for (Edge edge: outgoingEdgeList) 
      if (edge.getType().equals("class"))
        return true;
    return false;
  }

  /**
   * Finds a class for the given name in the list of generated classes.
   * @param builtClassList - the list of generated classes
   *        className - the class name
   * @return the class for the given name from the list of generated classes; 
   *         null if no such class found
   */
  public static Object getBuiltClass(ArrayList<Object> builtClassList, String className) {
    for (Object element : builtClassList)
      if (Model.getFacade().getName(element).equals(className))
        return element; 
    return null;
  }

  /**
   * Returns a graph node of Association type for the given graph node of Property 
   * type.
   * of class type.
   * @param propNode - the graph node of Property type
   * @return the graph node of Association for the given graph node of Property 
   *         type
   */
  public static Node getAssociationNode(Node propNode) {
    ArrayList<Edge> outgoingEdgeList = propNode.getOutgoingEdgeList();
    for (Edge edge: outgoingEdgeList)
      if (edge.getType().equals("association"))
        return edge.getTarget();
    return null;
  }

  /**
   * Returns a list of associations between two classes.
   * @param modelImpl - the ArgoUML model implemetation
   *        c1 - the class 1
   *        c2 - the class 2
   * @return the list of associations between two classes
   */
  public static ArrayList<Object> getAssociations(ModelImplementation modelImpl, Object c1, Object c2) {
    ArrayList<Object> results = new ArrayList<Object>();
    for (Object end : modelImpl.getFacade().getAssociationEnds(c1)) {
      Object oppositeEnd = modelImpl.getFacade().getNextEnd(end);
      Object oppositeEndClass = modelImpl.getFacade().getClassifier(oppositeEnd);
      if (oppositeEndClass == c2) {
        Object assoc = modelImpl.getFacade().getAssociation(oppositeEnd);
        results.add(assoc);
      }
    }
    return results;
  }

  /**
   * Finds a graph node of Class type with the given class name from the list of 
   * graph nodes of Class type.
   * @param classNodeList - the list of graph nodes of Class type
   *        className - the class name
   * @return the graph node of Class type with the given class name from the
   *         list of graph nodes of Class type; null if no such graph node of 
   *         Class type found
   */
  public static Node getClassNode(ArrayList<Node> classNodeList, String className) {
    for (Node classNode : classNodeList)
      if (classNode.getName().equals(className))
        return classNode; 
    return null;
  }

  /**
   * Finds a graph node of DataType type with the given datatype name from the list of 
   * graph nodes of DataType type.
   * @param dataTypeNodeList - the list of graph nodes of DataType type
   *        dataTypeName - the datatype name
   * @return the graph node of DataType type with the given datatype name from the
   *         list of graph nodes of DataType type; null if no such graph node of 
   *         DataType type found
   */
  public static Node getDataTypeNode(ArrayList<Node> dataTypeNodeList, String dataTypeName) {
    for (Node dataTypeNode : dataTypeNodeList)
      if (dataTypeNode.getName().equals(dataTypeName))
        return dataTypeNode; 
    return null;
  }

}