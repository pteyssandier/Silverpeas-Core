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

/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.silverpeas.genericPanel;

import java.util.Hashtable;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.util.GeneralPropertiesManager;
import com.stratelia.webactiv.util.ResourceLocator;

public class AllGroupPanel extends PanelProvider {
  protected static final int FILTER_NAME = 0;

  protected static final int COL_NAME = 0;

  protected OrganizationController m_oc = new OrganizationController();

  protected Hashtable<String, Group> m_AllGroup = new Hashtable<String, Group>();

  public AllGroupPanel(String language, String title) {
    initAll(language, title);
  }

  public void initAll(String language, String title) {
    String[] filters = new String[1];

    // Set the language
    m_Language = language;

    ResourceLocator message = GeneralPropertiesManager
        .getGeneralMultilang(m_Language);

    // Set the resource locator for columns header
    m_rs = new ResourceLocator(
        "com.stratelia.silverpeas.genericPanel.multilang.genericPanelBundle",
        m_Language);

    // Set the Page name
    m_PageName = title;
    m_PageSubTitle = m_rs.getString("genericPanel.usersList");

    // Set column headers
    m_ColumnsHeader = new String[1];
    m_ColumnsHeader[COL_NAME] = message.getString("GML.name");

    // Build search tokens
    m_SearchTokens = new PanelSearchToken[1];

    m_SearchTokens[FILTER_NAME] = new PanelSearchEdit(0, message
        .getString("GML.name"), "");

    // Set filters and get Ids
    filters[FILTER_NAME] = "";
    refresh(filters);
  }

  public void refresh(String[] filters) {
    Group modelGroup = new Group();
    Group[] result = null;

    if ((filters[FILTER_NAME] != null) && (filters[FILTER_NAME].length() > 0)) {
      modelGroup.setName(filters[FILTER_NAME] + "%");
    } else {
      modelGroup.setName("");
    }

    result = m_oc.searchGroups(modelGroup, true);
    m_AllGroup.clear();
    m_Ids = new String[result.length];
    for (int i = 0; i < result.length; i++) {
      m_Ids[i] = result[i].getId();
      m_AllGroup.put(m_Ids[i], result[i]);
    }

    // Set search tokens values
    ((PanelSearchEdit) m_SearchTokens[FILTER_NAME]).m_Text = getSureString(filters[FILTER_NAME]);
    verifIndexes();
  }

  public PanelLine getElementInfos(String id) {
    Group theGroup = (Group) m_AllGroup.get(id);
    String[] theValues;
    PanelLine valret = null;

    SilverTrace.info("genericPanel", "AllGroupPanel.getElementInfos()",
        "root.GEN_MSG_PARAM_VALUE", "id=" + id);
    theValues = new String[1];
    theValues[COL_NAME] = theGroup.getName();
    valret = new PanelLine(theGroup.getId(), theValues, false);
    return valret;
  }
}
