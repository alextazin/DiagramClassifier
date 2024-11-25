# Class Diagram Classifier 1.0

This application allows to classifiy ArgoUML class diagrams represented in 
XMI 1.2 format. The current version supports four basic pattern diagrams. The images 
of these diagrams can be found in the doc directory for reference. The basic pattern 
diagrams cannot be specified as input. Rather the E-graphs 
representing these diagrams are generated in the code. The basic patterns 
support the following class diagram concepts: class, generalization, binary 
association, association end, association end multiplicity, association end 
navigable property, data type (for representing primitive data types), 
attribute, and attribute type.

## Software Requirements

- Linux
- Java 1.8 or higher
- ArgoUML 0.35.1
- Jbliss 0.5 beta 2

## Installation

1. Install all the requirements
2. Build the application by running build.sh

## ArgoUML

1. Install ArgoUML on Windows
2. Create an ArgoUML home directory on Linux
3. Copy the following files to the ArgoUML home directory
   - antlr-2.7.7.jar
   - argouml-euml.jar
   - argouml-mdr.jar
   - argouml-model.jar
   - argouml.jar
   - batik-awt-util-1.7.jar
   - batik-dom-1.7.jar
   - batik-ext-1.7.jar
   - batik-svggen-1.7.jar
   - batik-util-1.7.jar
   - batik-xml-1.7.jar
   - commons-logging-1.0.2.jar
   - gef-0.13.8.jar
   - java-interfaces.jar
   - jmi.jar
   - jmiutils.jar
   - log4j-1.2.6.jar
   - mdrapi.jar
   - mof.jar
   - nbmdr.jar
   - ocl-argo-1.1.jar
   - openide-util.jar
   - swidgets-0.1.4.jar
   - toolbar-1.4.1-20071227.jar
4. Add argouml-mdr.jar and argouml-model.jar to Java classpath

## Jbliss

1. Download Jbliss from http://www.tcs.hut.fi/Software/bliss/
2. Build Jbliss by running the Makefile
3. Add jbliss.jar to Java classpath

## Running the Application

Execute the following command.

java -jar DiagramClassifier.jar N

N is the number of diagrams to classify.

## Usage

1. Create ArgoUML diagrams and export them to XMI 1.2 format.
2. Place the .xmi files in the diagrams directory. The file names should have the 
diagramN.xmi format where N is an integer from 1 to the to total number of diagrams 
to classify.

