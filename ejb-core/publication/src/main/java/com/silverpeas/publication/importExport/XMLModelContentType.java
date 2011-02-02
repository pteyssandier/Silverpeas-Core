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
package com.silverpeas.publication.importExport;

import com.silverpeas.form.importExport.XMLField;
import java.util.ArrayList;
import java.util.List;

public class XMLModelContentType {

  private String name = null;
  private List<XMLField> fields = new ArrayList<XMLField>();

  public XMLModelContentType() {
  }

  public XMLModelContentType(String name) {
    this.name = name;
  }

  /**
   * @return the name of the XML Model used
   */
  public String getName() {
    return name;
  }

  /**
   * 
   * @param name 
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the fields.
   */
  public List<XMLField> getFields() {
    return fields;
  }

  /**
   * @param fields the fields to add.
   */
  public void setFields(List<XMLField> fields) {
    this.fields = fields;
  }

  public void addField(XMLField field) {
    fields.add(field);
  }
}