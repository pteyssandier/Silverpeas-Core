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

package com.silverpeas.socialNetwork.provider;

import com.stratelia.webactiv.util.exception.SilverpeasException;
import java.util.List;

/**
 *
 * @author Bensalem Nabil
 */
public interface SocialRelationShipsInterface {

   /**
   * get list of socialInformation
   * according to  number of Item and the first Index
   * @param userId
   * @param numberOfElement
   * @param firstIndex
   * @return
   * @throws SilverpeasException
   */

  public List getSocialInformationsList(String userId, int numberOfElement, int firstIndex) throws
      SilverpeasException;
  /**
   * get list of socialInformation of my contacts
   * according to ids of my contacts , number of Item and the first Index
   * @param myId
   * @param myContactsIds
   * @param numberOfElement
   * @param firstIndex
   * @return
   * @throws SilverpeasException
   */

  public List getSocialInformationsListOfMyContacts(String myId , List<String> myContactsIds , int numberOfElement, int firstIndex) throws
      SilverpeasException;
}
