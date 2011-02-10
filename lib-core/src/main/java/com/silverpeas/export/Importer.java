/*
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
 * "http://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.export;

import java.io.Serializable;
import java.util.List;

/**
 * This interface defines the features an immporter of serializable resources in Silverpeas have to
 * satisfy. All importer in Silverpeas should implement this interface.
 *
 * An importer in Silverpeas is defined for a specific type of serializable resources and it has the
 * responsability to know how to import them from a specific or a specified format.
 * @param <T> The type of the serializable resources to import.
 */
public interface Importer<T extends Serializable> {

  /**
   * Imports the serialized resources from the reader and the import parameters carried by the specified
   * descriptor.
   * The resources are deserialized each of them in an instance of T and they are returned in a list.
   * @param descriptor the import descriptor in which information about the import process is
   * indicated.
   * @throws ImportException when an unexpected error occurs while importing
   * the resources.
   * @return a list of instances of T corresponfding to the imported resources.
   */
  List<T> importFrom(final ImportDescriptor descriptor) throws ImportException;
  
}
