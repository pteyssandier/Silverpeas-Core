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

package com.stratelia.silverpeas.domains.silverpeasdriver;

import com.stratelia.webactiv.organization.AdminPersistenceException;
import com.stratelia.webactiv.util.Schema;
import com.stratelia.webactiv.util.SchemaPool;
import com.stratelia.webactiv.util.exception.SilverpeasException;
import com.stratelia.webactiv.util.exception.UtilException;

/**
 * The DomainSPSchemaPool class manages a pool of DomainSPSchema shared by all the client (admin
 * classes). All the public methods are static and the calls are deferred to a singleton.
 */
public class DomainSPSchemaPool extends SchemaPool {
  /**
   * The unique OrganizationSchemaPool built to serve all the requests.
   */
  static private DomainSPSchemaPool singleton = new DomainSPSchemaPool();

  /**
   * The constructor is private, so we can ensure that only one pool will be created in the JVM.
   */
  private DomainSPSchemaPool() {
  }

  /**
   * Returns an DomainSPSchemaPool. The returned schema must be released after use.
   */
  static public DomainSPSchema getDomainSPSchema()
      throws AdminPersistenceException {
    try {
      return (DomainSPSchema) singleton.getInstance();
    } catch (UtilException ue) {
      throw new AdminPersistenceException("DomainSPSchema.getSchema",
          SilverpeasException.ERROR, "root.EX_DATASOURCE_INVALID", ue);
    }
  }

  /**
   * Release an DomainSPSchema previously returned by the pool.
   */
  static public void releaseDomainSPSchema(DomainSPSchema s) {
    singleton.release(s);
  }

  static public void releaseConnections() {
    singleton.releaseSchemas();
  }

  protected Schema newSchema(int connectionLot) throws UtilException {
    return (Schema) (new DomainSPSchema(connectionLot));
  }
}
