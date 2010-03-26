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
package com.stratelia.silverpeas.portlet.model;

public class PortletStateRow {
  private int id;
  private int state;
  private int userId;
  private int portletRowId;

  public int getId() {
    return id;
  }

  public void setId(int aId) {
    id = aId;
  }

  public int getState() {
    return state;
  }

  public void setState(int aState) {
    state = aState;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int aUserId) {
    userId = aUserId;
  }

  public int getPortletRowId() {
    return portletRowId;
  }

  public void setPortletRowId(int aPortletRowId) {
    portletRowId = aPortletRowId;
  }

  public PortletStateRow(int aId, int aState, int aUserId, int aPortletRowId) {
    id = aId;
    state = aState;
    userId = aUserId;
    portletRowId = aPortletRowId;
  }
}
