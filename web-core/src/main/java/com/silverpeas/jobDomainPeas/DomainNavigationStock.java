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
 * DomainNavigationStock.java
 */

package com.silverpeas.jobDomainPeas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.silverpeas.util.StringUtil;
import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.Domain;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.beans.admin.UserDetail;

/*
 * CVS Informations
 * 
 * $Id: DomainNavigationStock.java,v 1.3 2008/03/12 16:42:46 neysseri Exp $
 * 
 * $Log: DomainNavigationStock.java,v $
 * Revision 1.3  2008/03/12 16:42:46  neysseri
 * no message
 *
 * Revision 1.2.16.1  2007/12/13 15:27:59  neysseri
 * no message
 *
 * Revision 1.2  2004/09/28 12:45:27  neysseri
 * Extension de la longueur du login (de 20 a 50 caracteres) + nettoyage sources
 *
 * Revision 1.1.1.1  2002/08/06 14:47:55  nchaix
 * no message
 *
 * Revision 1.4  2002/04/05 05:22:08  tleroi
 * no message
 *
 * Revision 1.3  2002/04/03 07:40:33  tleroi
 * no message
 *
 * Revision 1.2  2002/04/02 14:34:01  tleroi
 * no message
 *
 *
 */

/**
 * This class manage the informations needed for domains navigation and browse PRE-REQUIRED : the
 * Domain passed in the constructor MUST BE A VALID DOMAIN (with Id, etc...)
 * @t.leroi
 */
public class DomainNavigationStock extends NavigationStock {
  Domain m_NavDomain = null;
  String m_DomainId = null;
  List manageableGroupIds = null;

  public DomainNavigationStock(String navDomain, AdminController adc,
      List manageableGroupIds) {
    super(adc);
    m_DomainId = navDomain;
    this.manageableGroupIds = manageableGroupIds;
    refresh();
  }

  public void refresh() {
    m_NavDomain = m_adc.getDomain(m_DomainId);
    m_SubUsers = m_adc.getUsersOfDomain(m_NavDomain.getId());
    if (m_SubUsers == null) {
      m_SubUsers = new UserDetail[0];
    }
    JobDomainSettings.sortUsers(m_SubUsers);
    m_SubGroups = m_adc.getRootGroupsOfDomain(m_NavDomain.getId());
    if (m_SubGroups == null) {
      m_SubGroups = new Group[0];
    }

    if (manageableGroupIds != null)
      m_SubGroups = filterGroupsToGroupManager(m_SubGroups);

    JobDomainSettings.sortGroups(m_SubGroups);
    verifIndexes();
  }

  private Group[] filterGroupsToGroupManager(Group[] groups) {
    // get all manageable groups by current user
    Iterator itManageableGroupsIds = null;

    List temp = new ArrayList();

    // filter groups
    String groupId = null;
    for (int g = 0; g < groups.length; g++) {
      groupId = groups[g].getId();

      if (manageableGroupIds.contains(groupId))
        temp.add(groups[g]);
      else {
        // get all subGroups of group
        List subGroupIds = Arrays.asList(m_adc
            .getAllSubGroupIdsRecursively(groupId));

        // check if at least one manageable group is part of subGroupIds
        itManageableGroupsIds = manageableGroupIds.iterator();

        String manageableGroupId = null;
        boolean find = false;
        while (!find && itManageableGroupsIds.hasNext()) {
          manageableGroupId = (String) itManageableGroupsIds.next();
          if (subGroupIds.contains(manageableGroupId))
            find = true;
        }

        if (find)
          temp.add(groups[g]);
      }
    }

    return (Group[]) temp.toArray(new Group[0]);
  }

  public boolean isThisDomain(String grId) {
    if (StringUtil.isDefined(grId)) {
      return (grId.equals(m_NavDomain.getId()));
    } else {
      return (isDomainValid(m_NavDomain) == false);
    }
  }

  public Domain getThisDomain() {
    return m_NavDomain;
  }

  static public boolean isDomainValid(Domain dom) {
    return dom != null && StringUtil.isDefined(dom.getId());
  }
}