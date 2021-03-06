/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stratelia.silverpeas.notificationserver.channel.silvermail;

import com.stratelia.webactiv.util.exception.SilverpeasException;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * @author eDurand
 * @version 1.0
 */
public class SILVERMAILException extends SilverpeasException {

  private static final long serialVersionUID = 1266360603211222081L;

  /**
   * 
   * @param callingClass
   * @param errorLevel
   * @param message 
   */
  public SILVERMAILException(String callingClass, int errorLevel, String message) {
    super(callingClass, errorLevel, message);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   * @see
   */
  public SILVERMAILException(String callingClass, int errorLevel, String message, 
      String extraParams) {
    super(callingClass, errorLevel, message, extraParams);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param nested
   * @see
   */
  public SILVERMAILException(String callingClass, int errorLevel, String message, 
      Exception nested) {
    super(callingClass, errorLevel, message, nested);
  }

  /**
   * Constructor declaration
   * @param callingClass
   * @param errorLevel
   * @param message
   * @param extraParams
   * @param nested
   * @see
   */
  public SILVERMAILException(String callingClass, int errorLevel, String message, String extraParams,
      Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }

  /**
   * -------------------------------------------------------------------------- getModule getModule
   */
  @Override
  public String getModule() {
    return "silvermail";
  }
}
