package diagramclassifier;

import java.util.*;
import java.io.*;

/**
 * This class represents a color graph node.
 * @author Alexey Tazin 
 */
public class ColorGraphNode {

  // The node name
  private String name;

  // The node color represented as integer value
  private int color;

  public ColorGraphNode(String name, int color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public int getColor() {
    return color;
  }

}