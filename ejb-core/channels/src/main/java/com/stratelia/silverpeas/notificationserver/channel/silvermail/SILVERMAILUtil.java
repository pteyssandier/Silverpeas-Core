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

/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.silverpeas.notificationserver.channel.silvermail;

import java.util.Collection;

import com.stratelia.webactiv.util.ResourceLocator;

/**
 * Class declaration
 * @author
 * @version %I%, %G%
 */
public class SILVERMAILUtil {
  // private String language = "";
  private String userId = "";
  private ResourceLocator message = null;

  /**
   * Constructor declaration
   * @see
   */
  public SILVERMAILUtil(String userId, String language) {
    // this.language = language;
    this.userId = userId;
  }

  /**
   * Method declaration
   * @param folderName
   * @return
   * @see
   */
  public Collection getFolderMessageList(String folderName)
      throws SILVERMAILException {
    return SILVERMAILPersistence.getMessageOfFolder(Integer.parseInt(userId),
        folderName);
  }

  /**
   * Method declaration
   * @param resName
   * @return
   * @see
   */
  public String getString(String resName) {
    if (message == null) {
      return resName;
    } else {
      return message.getString(resName);
    }
  }

}