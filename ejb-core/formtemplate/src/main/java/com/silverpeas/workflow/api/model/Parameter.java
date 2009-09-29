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
package com.silverpeas.workflow.api.model;

/**
 * Interface describing a representation of the &lt;parameter&gt; element of a
 * Process Model.
 */
public interface Parameter {
  /**
   * Get the name of the Parameter
   * 
   * @return parameter's name
   */
  public String getName();

  /**
   * Set the name of the Parameter
   * 
   * @param parameter
   *          's name
   */
  public void setName(String name);

  /**
   * Get the value of the Parameter
   * 
   * @return parameter's value
   */
  public String getValue();

  /**
   * Set the value of the Parameter
   * 
   * @param parameter
   *          's value
   */
  public void setValue(String value);
}
