package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represents a pait of E-graph attribute edges.
 * @author Alexey Tazin 
 */
public class AttributeEdgePair {

  // The attribute edge 1
  private AttributeEdge attributeEdge1;

  // The attribute edge 2
  private AttributeEdge attributeEdge2;

  public AttributeEdgePair(AttributeEdge attributeEdge1, AttributeEdge attributeEdge2) {
    this.attributeEdge1 = attributeEdge1;
    this.attributeEdge2 = attributeEdge2;
  }

  public AttributeEdge getAttributeEdge1() {
    return attributeEdge1;
  }

  public AttributeEdge getAttributeEdge2() {
    return attributeEdge2;
  }

}