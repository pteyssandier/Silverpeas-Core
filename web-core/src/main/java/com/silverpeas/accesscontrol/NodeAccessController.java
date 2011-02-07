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
package com.silverpeas.accesscontrol;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.ObjectType;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.node.control.NodeBm;
import com.stratelia.webactiv.util.node.control.NodeBmHome;
import com.stratelia.webactiv.util.node.model.NodeDetail;
import com.stratelia.webactiv.util.node.model.NodePK;

/**
 * Check the access to a node for a user.
 * @author ehugonnet
 */
public class NodeAccessController implements AccessController<NodePK> {

  private final OrganizationController controller;

  public NodeAccessController() {
    this(new OrganizationController());
  }

  /**
   * For tests only.
   * @param controller
   */
  NodeAccessController(OrganizationController controller) {
    this.controller = controller;
  }

  @Override
  public boolean isUserAuthorized(String userId, NodePK nodePK) {
    NodeDetail node;
    try {
      node = getNodeBm().getHeader(nodePK, false);
    } catch (Exception ex) {
      SilverTrace.error("accesscontrol", getClass().getSimpleName() + ".isUserAuthorized()",
          "root.NO_EX_MESSAGE", ex);
      return false;
    }
    if (node != null) {
      if (!node.haveRights()) {
        return true;
      }
      return controller.isObjectAvailable(node.getRightsDependsOn(), ObjectType.NODE, nodePK.
          getInstanceId(), userId);
    }
    return false;
  }

  public NodeBm getNodeBm() throws Exception {
    NodeBmHome nodeBmHome = (NodeBmHome) EJBUtilitaire.getEJBObjectRef(JNDINames.NODEBM_EJBHOME,
        NodeBmHome.class);
    return nodeBmHome.create();
  }
}
