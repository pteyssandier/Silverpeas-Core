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
/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

/*
 * GroupNavigationStock.java
 */

package com.silverpeas.jobDomainPeas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.silverpeas.util.StringUtil;
import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.beans.admin.UserDetail;

/**
 * This class manage the informations needed for groups navigation and browse PRE-REQUIRED : the
 * Group passed in the constructor MUST BE A VALID GROUP (with Id, etc...)
 * @t.leroi
 */
public class GroupNavigationStock extends NavigationStock {
  Group m_NavGroup = null;
  String m_GroupId = null;
  List<String> manageableGroupIds = null;

  public GroupNavigationStock(String navGroup, AdminController adc,
      List<String> manageableGroupIds) {
    super(adc);
    m_GroupId = navGroup;
    this.manageableGroupIds = manageableGroupIds;
    refresh();
  }

  public void refresh() {
    String[] subUsersIds = null;
    String[] subGroupsIds = null;
    int i;

    m_NavGroup = m_adc.getGroupById(m_GroupId);
    subUsersIds = m_NavGroup.getUserIds();
    if (subUsersIds == null) {
      m_SubUsers = new UserDetail[0];
    } else {
      m_SubUsers = m_adc.getUserDetails(subUsersIds);
    }
    JobDomainSettings.sortUsers(m_SubUsers);

    subGroupsIds = m_adc.getAllSubGroupIds(m_NavGroup.getId());
    if (subGroupsIds == null) {
      m_SubGroups = new Group[0];
    } else {
      if (manageableGroupIds != null)
        subGroupsIds = filterGroupsToGroupManager(subGroupsIds);

      m_SubGroups = new Group[subGroupsIds.length];
      for (i = 0; i < subGroupsIds.length; i++) {
        m_SubGroups[i] = m_adc.getGroupById(subGroupsIds[i]);
      }
    }
    JobDomainSettings.sortGroups(m_SubGroups);
    verifIndexes();
  }

  private String[] filterGroupsToGroupManager(String[] groupIds) {
    // get all manageable groups by current user
    Iterator<String> itManageableGroupsIds = null;

    List<String> temp = new ArrayList<String>();

    // filter groups
    String groupId = null;
    for (int g = 0; g < groupIds.length; g++) {
      groupId = groupIds[g];

      if (manageableGroupIds.contains(groupId))
        temp.add(groupId);
      else {
        // get all subGroups of group
        List<String> subGroupIds = Arrays.asList(m_adc
            .getAllSubGroupIdsRecursively(groupId));

        // check if at least one manageable group is part of subGroupIds
        itManageableGroupsIds = manageableGroupIds.iterator();

        String manageableGroupId = null;
        boolean find = false;
        while (!find && itManageableGroupsIds.hasNext()) {
          manageableGroupId = itManageableGroupsIds.next();
          if (subGroupIds.contains(manageableGroupId))
            find = true;
        }

        if (find)
          temp.add(groupId);
      }
    }

    return (String[]) temp.toArray(new String[0]);
  }

  public boolean isThisGroup(String grId) {
    if (StringUtil.isDefined(grId)) {
      return (grId.equals(m_NavGroup.getId()));
    } else {
      return (isGroupValid(m_NavGroup) == false);
    }
  }

  public Group getThisGroup() {
    return m_NavGroup;
  }

  static public boolean isGroupValid(Group gr) {
    if (gr != null && StringUtil.isDefined(gr.getId())) {
      return true;
    }
    return false;
  }
}
