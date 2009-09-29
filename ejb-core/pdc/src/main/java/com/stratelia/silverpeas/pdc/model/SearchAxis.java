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
package com.stratelia.silverpeas.pdc.model;

//import java.util.ArrayList;
import java.util.List;

import com.stratelia.silverpeas.classifyEngine.PertinentAxis;

/**
 * @author Nicolas EYSSERIC
 */
public class SearchAxis extends PertinentAxis implements java.io.Serializable {
  // private String axisName = null;
  private int axisRootId = -1;

  private AxisHeader axis = null;

  private List values = null;

  public SearchAxis(int axisId, int nbObjects) {
    super();
    setAxisId(axisId);
    setNbObjects(nbObjects);
  }

  public String getAxisName() {
    return axis.getName();
  }

  public String getAxisName(String lang) {
    return axis.getName(lang);
  }

  /*
   * public void setAxisName(String axisName) { this.axisName = axisName; }
   */

  public void setAxis(AxisHeader axis) {
    this.axis = axis;
  }

  public int getAxisRootId() {
    return axisRootId;
  }

  public void setAxisRootId(int rootId) {
    axisRootId = rootId;
  }

  /**
   * @return a List of SearchValue
   */
  public List getValues() {
    return values;
  }

  /**
   * @param list
   */
  public void setValues(List list) {
    values = list;
  }

  /*
   * public void addValue(SearchValue value) { if (values == null) values = new
   * ArrayList();
   * 
   * values.add(value); }
   */

}