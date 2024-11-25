package edu.diagramclassifier;

import java.io.*;
import java.util.*;
import java.lang.*;
import org.argouml.model.*;
import org.xml.sax.InputSource;
import fi.tkk.ics.jbliss.Graph;
import fi.tkk.ics.jbliss.DefaultReporter;
import fi.tkk.ics.jbliss.Utils;

/**
 * This allows to classfiy the given list of ArgoUML class diagrams 
 * represented in XMI 1.2 format.
 * @author Alexey Tazin 
 */
public class DiagramClassifier {

  public static void main(String[] args) {
    int diagramsNum = Integer.parseInt(args[0]);
    classifyDiagrams(diagramsNum);

  /**
   * Classifies the given list of class diagrams. The files representing 
   * class diagram are stored in the current directory. The file names have 
   * the diagramN.xmi format where N is an integer from 1 to diagramsNum.
   * @param diagramsNum - the number of class diagrams to classify
   */
  public static void classifyDiagrams(int diagramsNum) {
    try {

        ModelImplementation modelImpl = 
          InitializeModel.initializeModelImplementation("org.argouml.model.mdr.MDRModelImplementation");
        Model.setImplementation(modelImpl);

        ArrayList<Graph<Integer>> graphClassList = new ArrayList<Graph<Integer>>();
        ArrayList<Integer> graphClassMemberFreqList = new ArrayList<Integer>();
        ArrayList<String> graphClassMemberNameList = new ArrayList<String>();

        for (int i = 1; i <= diagramsNum; i++) {
          
          String diagramFileName = "diagram" + i + ".xmi";
          File xmiFile = new File(diagramFileName);
          XmiReader xmiReader = Model.getXmiReader();
          InputSource inputSource = new InputSource(new FileInputStream(xmiFile));
          Collection elements = xmiReader.parse(inputSource, false);
          Object model = elements.iterator().next();

          DiagramGraph diagramGraph = new DiagramGraph();

          DiagramGraphGenerator.generateDiagramGraph(modelImpl, model, diagramGraph);

          DiagramClassCalculator diagramClassCalculator = new DiagramClassCalculator();
          Graph<Integer> graphClass = diagramClassCalculator.calulateDiagramClass(diagraGraph);

          int graphClassIndex = getGraphClassIndex(graphClassList, graphClass);
          if (graphClassIndex != -1)
            graphClassMemberFreqList.set(graphClassIndex, graphClassMemberFreqList.get(graphClassIndex).intValue() + 1);
          else {
            graphClassList.add(graphClass);
            graphClassMemberFreqList.add(1);
            graphClassMemberNameList.add(diagramFileName);
          }

       }

       System.out.println("Class number\tNumber of members\tMember diagram);
       for (int i = 0; i < graphClassMemberFreqList.size(); i++) 
         System.out.println(i + "\t" + graphClassMemberFreqList.get(i).intValue() + "\t" + graphClassMemberList.get(i));
       System.out.println("Number of diagram classes: " + graphClassMemberFreqList.size());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns an index of the graph of the complex pattern diagram 
   * representing the class of class diagrams from the list of graphs of the 
   * complex pattern diagrams representing the classes of class diagrams.
   * @param graphClassList - the list of graphs of the complex pattern 
   *        diagrams representing the classes of class diagrams
   *        graphClass - the graph of the complex pattern diagram 
   *        representing the class of class diagrams
   * @return the index of the graph of the complex pattern diagram 
   *         representing the class of class diagrams; -1 if no such graph 
   *         found
   */
  public static int getGraphClassIndex(ArrayList<Graph<Integer>> graphClassList, 
                                                   Graph<Integer> graphClass) {
    for (int i = 0; i < graphClassList.size(); i++) {
       Graph<Integer> element = graphClassList.get(i);
      if (element.compareTo(graphClass)) 
        return i;
    }
    return -1;
  }

}
