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
 * FLOSS exception.  You should have recieved a copy of the text describing
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
package com.sun.portal.portletcontainer.driver.admin;

/**
 * A <code>PortletAdminDataException</code> is thrown when there are errors
 * related to the portlet admin console like deployment of portlet, creation of
 * portlet window.
 */
public class PortletAdminDataException extends Exception {

  /**
   * Constructs a new exception with the specified message
   * 
   * @param msg
   *          The descriptive message.
   */
  public PortletAdminDataException(String msg) {
    super(msg);
  }

  /**
   * Constructs a new exception with the specified message, and the original
   * <code>exception</code> or <code>error</code>
   * 
   * @param msg
   *          The descriptive message.
   * @param cause
   *          The original <code>exception</code> or <code>error</code>.
   */
  public PortletAdminDataException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
