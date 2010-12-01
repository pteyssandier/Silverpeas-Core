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

package com.silverpeas.lookV5;

import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.silverpeas.external.filesharing.model.FileSharingInterface;
import com.silverpeas.external.filesharing.model.FileSharingInterfaceImpl;
import com.silverpeas.external.webConnections.dao.WebConnectionsImpl;
import com.silverpeas.external.webConnections.model.WebConnectionsInterface;
import com.silverpeas.look.LookHelper;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.notificationserver.channel.silvermail.SILVERMAILPersistence;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.URLManager;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminException;
import com.stratelia.webactiv.beans.admin.ComponentInst;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.beans.admin.PersonalSpaceController;
import com.stratelia.webactiv.beans.admin.SpaceInst;
import com.stratelia.webactiv.beans.admin.instance.control.WAComponent;
import com.stratelia.webactiv.util.ResourceLocator;

public class PersonalSpaceJSONServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    doPost(req, res);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    SilverTrace.info("lookSilverpeasV5", "JSONServlet.doPost", "root.MSG_GEN_ENTER_METHOD");

    HttpSession session = req.getSession(true);
    MainSessionController m_MainSessionCtrl =
        (MainSessionController) session.getAttribute("SilverSessionController");
    LookHelper helper = (LookHelper) session.getAttribute("Silverpeas_LookHelper");
    OrganizationController orgaController = m_MainSessionCtrl.getOrganizationController();
    String userId = m_MainSessionCtrl.getUserId();

    res.setContentType("application/json");

    String action = req.getParameter("Action");

    Writer writer = res.getWriter();
    PersonalSpaceController psc = new PersonalSpaceController();
    if ("GetAvailableComponents".equals(action)) {
      Collection<WAComponent> components = psc.getVisibleComponents(orgaController);
      SpaceInst space = psc.getPersonalSpace(userId);
      if (space != null) {
        writer.write(getWAComponentsAsJSONArray(getNotUsedComponents(components, space), helper));
      } else {
        writer.write(getWAComponentsAsJSONArray(components, helper));
      }
    } else if ("GetComponents".equals(action)) {
      SpaceInst space = psc.getPersonalSpace(userId);
      if (space != null) {
        writer.write(getComponentsAsJSONArray(space.getAllComponentsInst(), helper));
      }
    } else if ("AddComponent".equals(action)) {
      String componentName = req.getParameter("ComponentName");
      try {
        String componentId =
            psc.addComponent(helper.getUserId(), componentName, getComponentLabel(componentName,
                helper));
        writer.write(getResult(componentName, componentId, null, helper).toString());
      } catch (AdminException e) {
        writer.write(getResult(componentName, null, e, helper).toString());
        SilverTrace.error("admin", "PersonalSpaceJSONServlet.doPost.AddComponent",
              "root.EX_NO_MESSAGE", e);
      }
    } else if ("RemoveComponent".equals(action)) {
      String componentId = req.getParameter("ComponentId");
      try {
        String componentName = psc.removeComponent(userId, componentId);
        writer.write(getResult(componentName, componentId, null, helper).toString());
      } catch (AdminException e) {
        writer.write(getResult(null, componentId, e, helper).toString());
        SilverTrace.error("admin", "PersonalSpaceJSONServlet.doPost.RemoveComponent",
              "root.EX_NO_MESSAGE", e);
      }
    } else if ("GetTools".equals(action)) {
      writer.write(getToolsAsJSONArray(helper));
    }
  }

  private Collection<WAComponent> getNotUsedComponents(Collection<WAComponent> components,
      SpaceInst space) {
    Collection<WAComponent> availables = new ArrayList<WAComponent>();
    Collection<ComponentInst> used = space.getAllComponentsInst();
    for (WAComponent component : components) {
      if (!isComponentUsed(component, used)) {
        availables.add(component);
      }
    }
    return availables;
  }

  private boolean isComponentUsed(WAComponent component, Collection<ComponentInst> componentsUsed) {
    for (ComponentInst componentUsed : componentsUsed) {
      if (componentUsed.getName().equalsIgnoreCase(component.getName())) {
        return true;
      }
    }
    return false;
  }

  private String getWAComponentsAsJSONArray(Collection<WAComponent> components, LookHelper helper) {
    JSONArray jsonArray = new JSONArray();
    for (WAComponent component : components) {
      JSONObject jsonObject = getWAComponentAsJSONObject(component, helper);
      jsonArray.put(jsonObject);
    }
    return jsonArray.toString();
  }

  private String getComponentsAsJSONArray(Collection<ComponentInst> components, LookHelper helper) {
    JSONArray jsonArray = new JSONArray();
    for (ComponentInst component : components) {
      JSONObject jsonObject = getComponentAsJSONObject(component, helper);
      jsonArray.put(jsonObject);
    }
    return jsonArray.toString();
  }

  private JSONObject getWAComponentAsJSONObject(WAComponent component, LookHelper helper) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", component.getName());
    jsonObject.put("description", component.getDescription());
    jsonObject.put("label", getComponentLabel(component.getName(), helper));

    return jsonObject;
  }

  private JSONObject getComponentAsJSONObject(ComponentInst component, LookHelper helper) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", component.getName());
    jsonObject.put("description", component.getDescription());
    jsonObject.put("label", getComponentLabel(component.getName(), helper));
    jsonObject.put("id", component.getId());

    return jsonObject;
  }

  private JSONObject getResult(String componentName, String componentId, Exception e,
      LookHelper helper) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", componentName);
    jsonObject.put("label", getComponentLabel(componentName, helper));
    jsonObject.put("successfull", e == null);
    if (componentId != null) {
      jsonObject.put("id", componentId);
      jsonObject.put("url", URLManager.getURL(componentName, "useless", componentId) + "Main");
    }
    if (e != null) {
      jsonObject.put("exception", e.toString());
    }
    return jsonObject;
  }

  private String getComponentLabel(String componentName, LookHelper helper) {
    String label = helper.getString("lookSilverpeasV5.personalSpace." + componentName);
    if (!StringUtil.isDefined(label)) {
      label = componentName;
    }
    return label;
  }

  private JSONObject getToolAsJSONObject(String id, String label, String url) {
    return getToolAsJSONObject(id, label, url, 0);
  }

  private JSONObject getToolAsJSONObject(String id, String label, String url, int nb) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", "");
    jsonObject.put("description", "");
    jsonObject.put("label", label);
    jsonObject.put("id", id);
    jsonObject.put("url", url);
    jsonObject.put("nb", nb);

    return jsonObject;
  }

  private String getToolsAsJSONArray(LookHelper helper) {
    boolean isAnonymousAccess = helper.isAnonymousAccess();
    ResourceLocator message =
        new ResourceLocator("com.stratelia.webactiv.homePage.multilang.homePageBundle", helper
            .getLanguage());
    JSONArray jsonArray = new JSONArray();
    if (!isAnonymousAccess && helper.getSettings("personnalSpaceVisible", true)) {
      if (helper.getSettings("agendaVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("agenda", message.getString("Diary"), URLManager
                .getURL(URLManager.CMP_AGENDA) +
                "Main");
        jsonArray.put(tool);
      }
      if (helper.getSettings("todoVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("todo", message.getString("ToDo"), URLManager
                .getURL(URLManager.CMP_TODO) +
                "todo.jsp");
        jsonArray.put(tool);
      }
      if (helper.getSettings("notificationVisible", true)) {
        // get number of notifications
        int nbNotifications = 0;
        try {
          Collection notifications =
              SILVERMAILPersistence.getMessageOfFolder(Integer.parseInt(helper.getUserId()),
                  "INBOX");
          if (notifications != null) {
            nbNotifications = notifications.size();
          }
        } catch (Exception e) {
          SilverTrace.error("admin", "PersonalSpaceJSONServlet.getToolsAsJSONArray",
              "root.CANT_GET_NOTIFICATIONS", e);
        }

        JSONObject tool =
            getToolAsJSONObject("notification", message.getString("Mail"), URLManager
                .getURL(URLManager.CMP_SILVERMAIL) +
                "Main", nbNotifications);
        jsonArray.put(tool);
      }
      if (helper.getSettings("interestVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("subscriptions", message.getString("MyInterestCenters"), URLManager
                .getURL(URLManager.CMP_PDCSUBSCRIPTION) +
                "subscriptionList.jsp");
        jsonArray.put(tool);
      }
      if (helper.getSettings("favRequestVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("requests", message.getString("FavRequests"), URLManager
                .getURL(URLManager.CMP_INTERESTCENTERPEAS) +
                "iCenterList.jsp");
        jsonArray.put(tool);
      }
      if (helper.getSettings("linksVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("links", message.getString("FavLinks"), URLManager
                .getURL(URLManager.CMP_MYLINKSPEAS) +
                "Main");
        jsonArray.put(tool);
      }
      if (helper.getSettings("fileSharingVisible", true)) {
        FileSharingInterface fileSharing = new FileSharingInterfaceImpl();
        try {
          if (!fileSharing.getTicketsByUser(helper.getUserId()).isEmpty()) {
            JSONObject tool =
                getToolAsJSONObject("fileSharing", message.getString("FileSharing"), URLManager
                    .getURL(URLManager.CMP_FILESHARING) +
                    "Main");
            jsonArray.put(tool);
          }
        } catch (RemoteException e) {
          SilverTrace.error("admin", "PersonalSpaceJSONServlet.getToolsAsJSONArray",
              "root.CANT_GET_TICKETS", e);
        }
      }
      // mes connexions
      if (helper.getSettings("webconnectionsVisible", true)) {
        WebConnectionsInterface webConnections = new WebConnectionsImpl();
        try {
          if (webConnections.getConnectionsByUser(helper.getUserId()).size() > 0) {
            JSONObject tool =
                getToolAsJSONObject("webConnections", message.getString("WebConnections"),
                    URLManager.getURL(URLManager.CMP_WEBCONNECTIONS) + "Main");
            jsonArray.put(tool);
          }
        } catch (RemoteException e) {
          SilverTrace.error("admin", "PersonalSpaceJSONServlet.getToolsAsJSONArray",
              "root.CANT_GET_CONNECTIONS", e);
        }
      }

      if (helper.getSettings("customVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("personalize", message.getString("Personalization"), URLManager
                .getURL(URLManager.CMP_PERSONALIZATION) +
                "Main.jsp");
        jsonArray.put(tool);
      }
      if (helper.getSettings("mailVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("notifAdmins", message.getString("Feedback"),
                "javascript:notifyAdministrators()");
        jsonArray.put(tool);
      }
      if (helper.getSettings("clipboardVisible", true)) {
        JSONObject tool =
            getToolAsJSONObject("clipboard", message.getString("Clipboard"),
                "javascript:openClipboard()");
        jsonArray.put(tool);
      }
    }
    return jsonArray.toString();
  }

}
