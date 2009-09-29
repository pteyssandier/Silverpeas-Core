package com.stratelia.silverpeas.pdc.model;

import com.stratelia.webactiv.util.exception.*;

/**
 * Class declaration
 * 
 * 
 * @author
 */
public class PdcException extends SilverpeasException implements
    java.io.Serializable {

  /**
   * Constructor declaration
   * 
   * 
   * @param callingClass
   * @param errorLevel
   * @param message
   * 
   * @see
   */
  public PdcException(String callingClass, int errorLevel, String message) {
    super(callingClass, errorLevel, message);
  }

  /**
   * Constructor declaration
   * 
   * 
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   * 
   * @see
   */
  public PdcException(String callingClass, int errorLevel, String message,
      String extraParams) {
    super(callingClass, errorLevel, message, extraParams);
  }

  /**
   * Constructor declaration
   * 
   * 
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param nested
   * 
   * @see
   */
  public PdcException(String callingClass, int errorLevel, String message,
      Exception nested) {
    super(callingClass, errorLevel, message, nested);
  }

  /**
   * Constructor declaration
   * 
   * 
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   * @param nested
   * 
   * @see
   */
  public PdcException(String callingClass, int errorLevel, String message,
      String extraParams, Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getModule() {
    return "Pdc";
  }

}
