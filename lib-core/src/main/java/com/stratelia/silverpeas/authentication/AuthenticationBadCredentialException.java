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
/*
 * AuthenticationBadCredentialException.java
 *
 * Created on 6 aout 2001
 */

package com.stratelia.silverpeas.authentication;

/**
 * @author tleroi
 * @version
 */
public class AuthenticationBadCredentialException extends AuthenticationException {
  /**
   * -------------------------------------------------------------------------- constructor
   * constructor
   */
  public AuthenticationBadCredentialException(String callingClass,
      int errorLevel, String message) {
    super(callingClass, errorLevel, message);
  }

  public AuthenticationBadCredentialException(String callingClass,
      int errorLevel, String message, String extraParams) {
    super(callingClass, errorLevel, message, extraParams);
  }

  public AuthenticationBadCredentialException(String callingClass,
      int errorLevel, String message, Exception nested) {
    super(callingClass, errorLevel, message, nested);
  }

  public AuthenticationBadCredentialException(String callingClass,
      int errorLevel, String message, String extraParams, Exception nested) {
    super(callingClass, errorLevel, message, extraParams, nested);
  }
}
