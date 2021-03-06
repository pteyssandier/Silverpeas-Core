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

/*
 * @author Norbert CHAIX
 * @version 1.0
 */

package com.stratelia.webactiv.beans.admin;

import com.stratelia.webactiv.util.exception.SilverpeasException;

public class AdminException extends SilverpeasException {

  private static final long serialVersionUID = 1L;
  private boolean m_bAlreadyPrinted = false;

  public AdminException(boolean bAlreadyPrinted) {
    super("NoClass", SilverpeasException.ERROR, "");
    m_bAlreadyPrinted = bAlreadyPrinted;
  }

  public AdminException(String sMessage, boolean bAlreadyPrinted) {
    super("NoClass", SilverpeasException.ERROR, sMessage);
    m_bAlreadyPrinted = bAlreadyPrinted;
  }

  public AdminException(Exception e, boolean bAlreadyPrinted) {
    super("NoClass", SilverpeasException.ERROR, "", e);
    m_bAlreadyPrinted = bAlreadyPrinted;
  }

  public boolean isAlreadyPrinted() {
    return m_bAlreadyPrinted;
  }

  /**
   * constructor
   */
  public AdminException(String callingClass, int errorLevel, String message) {
    super(callingClass, errorLevel, message);
  }

  public AdminException(String callingClass, int errorLevel, String message,
      String extraParams) {
    super(callingClass, errorLevel, message, extraParams);
  }

  public AdminException(String callingClass, int errorLevel, String message,
      Exception nested) {
    super(callingClass, errorLevel, message, nested);
  }

  public AdminException(String callingClass, int errorLevel, String message,
      String extraParams, Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }

  /**
   * getModule
   */
  public String getModule() {
    return "admin";
  }
}
