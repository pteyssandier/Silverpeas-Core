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

package com.stratelia.webactiv.util.publication.info.model;

import java.io.Serializable;

import com.stratelia.webactiv.util.WAPrimaryKey;

public class InfoImagePK extends WAPrimaryKey implements Serializable {

  private static final long serialVersionUID = -3671112039695795408L;

  public InfoImagePK(String id) {
    super(id);
  }

  public InfoImagePK(String id, String space, String componentName) {
    super(id, space, componentName);
  }

  public InfoImagePK(String id, WAPrimaryKey pk) {
    super(id, pk);
  }

  public String getRootTableName() {
    return "InfoImage";
  }

  public String getTableName() {
    return "SB_Publication_InfoImage";
  }

  public boolean equals(Object other) {
    if (!(other instanceof InfoImagePK))
      return false;
    return (id.equals(((InfoImagePK) other).getId()))
        && (space.equals(((InfoImagePK) other).getSpace()))
        && (componentName.equals(((InfoImagePK) other).getComponentName()));
  }

  public String toString() {
    return "InfoImagePK(id = " + getId() + ", space = " + getSpace()
        + ", componentName = " + getComponentName() + ")";
  }

  /**
   * Returns a hash code for the key
   * @return A hash code for this object
   */
  public int hashCode() {
    return toString().hashCode();
  }

}