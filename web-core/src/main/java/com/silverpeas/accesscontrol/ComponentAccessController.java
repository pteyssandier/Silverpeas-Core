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

import com.silverpeas.util.ComponentHelper;
import com.silverpeas.util.StringUtil;
import com.stratelia.webactiv.beans.admin.OrganizationController;

/**
 * Check the access to a component for a user.
 * @author ehugonnet
 */
public class ComponentAccessController implements AccessController<String> {

  private final OrganizationController controller;

  public ComponentAccessController() {
    this(new OrganizationController());
  }

  /**
   * For tests only.
   * @param controller 
   */  
  ComponentAccessController(OrganizationController controller) {
    this.controller = controller;
  }

  /**
   * Indicates that the rights are set on node as well as the component.
   * @param userId 
   * @param componentId
   * @return 
   */
  public boolean isRightOnTopicsEnabled(String userId, String componentId) {
    return isThemeTracker(componentId) && StringUtil.getBooleanValue(controller.
        getComponentParameterValue(componentId, "rightsOnTopics"));
  }

  private boolean isThemeTracker(String componentId) {
    return ComponentHelper.getInstance().isThemeTracker(componentId);
  }

  @Override
  public boolean isUserAuthorized(String userId, String componentId) throws Exception {
    if (componentId == null) { // Personal space
      return true;
    }
    if (StringUtil.getBooleanValue(controller.getComponentParameterValue(componentId, "publicFiles"))) {
      return true;
    }
    return controller.isComponentAvailable(componentId, userId);
  }
}
