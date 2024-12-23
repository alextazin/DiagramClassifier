package diagramclassifier;

import org.argouml.model.*;

/**
 * Class to initialize the model.
 *
 * @author Alexey Tazin
 */
public final class InitializeModel {
    
  /**
   * The default model implementation to start.
   */
  private static final String DEFAULT_MODEL_IMPLEMENTATION =
    "org.argouml.model.mdr.MDRModelImplementation";


  /**
   * This is never instantiated.
   */
  private InitializeModel() {
  }
    

  /**
   * Initialize the Model subsystem with the MDR ModelImplementation.
   */
  public static void initializeMDR() {
    if (Model.isInitiated())
      return;
    ModelImplementation impl = initializeModelImplementation(
      "org.argouml.model.mdr.MDRModelImplementation");
    Model.setImplementation(impl);
  }

  /**
   * Initialize the Model subsystem with the eUML ModelImplementation.
   */
  public static void initializeEUML() {
    if (Model.isInitiated())
      return;
     ModelImplementation impl = initializeModelImplementation(
       "org.argouml.model.euml.EUMLModelImplementation");
     Model.setImplementation(impl);
  }

  public static ModelImplementation initializeModelImplementation(String name) {
    ModelImplementation impl = null;
    Class implType;
    try {
      implType = Class.forName(name);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    try {
      impl = (ModelImplementation) implType.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return impl;
  }

}