/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

/**
 * Titre : Silverpeas<p>
 * Description : This object provides the versioning runtime execption<p>
 * Copyright : Copyright (c) Stratelia<p>
 * Société : Stratelia<p>
 * @author Jean-Claude Groccia
 * @version 1.0
 */
package com.stratelia.silverpeas.versioning.ejb;

import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;

/*
 * CVS Informations
 * 
 * $Id: VersioningRuntimeException.java,v 1.2 2003/01/08 16:10:46 neysseri Exp $
 * 
 * $Log: VersioningRuntimeException.java,v $
 * Revision 1.2  2003/01/08 16:10:46  neysseri
 * no message
 *
 * Revision 1.1.1.1.6.1  2002/10/18 09:50:33  abudnikau
 * no message
 *
 * Revision 1.1.1.1  2002/08/06 14:47:54  nchaix
 * no message
 *
 * Revision 1.2  2002/07/17 16:15:44  nchaix
 * Merge branche EPAM_130602
 *
 * Revision 1.1.2.1  2002/07/02 14:04:08  pbialevich
 * First version
 *
 * Revision 1.5  2002/01/21 18:00:31  mguillem
 * Stabilisation Lot2
 * Réorganisation des Router et SessionController
 * Suppression dans les fichiers *Exception de 'implements FromModule'
 *
 * Revision 1.4  2002/01/21 17:55:19  mguillem
 * Stabilisation Lot2
 * Réorganisation des Router et SessionController
 * Suppression dans les fichiers *Exception de 'implements FromModule'
 *
 * Revision 1.3  2002/01/08 10:28:13  groccia
 * no message
 *
 * Revision 1.2  2001/12/31 15:43:32  groccia
 * stabilisation
 *
 */

/**
 * Class declaration
 * 
 * 
 * @author
 */
public class VersioningRuntimeException extends SilverpeasRuntimeException {

  /**
   * method of interface FromModule
   */
  public String getModule() {
    return "versioning";
  }

  /**
   * constructors
   */

  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message) {
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
  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message, String extraParams) {
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
  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message, Exception nested) {
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
  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message, String extraParams, Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }

  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message, Object extraParams, Exception nested) {
    super(callingClass, errorLevel, message, convert(extraParams), nested);
  }

  public VersioningRuntimeException(String callingClass, int errorLevel,
      String message, Object extraParams) {
    super(callingClass, errorLevel, message, convert(extraParams));
  }

  protected static String convert(Object value) {
    return (value != null) ? value.toString() : "NULL";
  }
}
