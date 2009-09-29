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
package com.silverpeas.peasUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.URLManager;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.ComponentInstLight;
import com.stratelia.webactiv.beans.admin.Domain;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.beans.admin.UserFull;
import com.stratelia.webactiv.util.GeneralPropertiesManager;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.exporters.RSS_2_0_Exporter;
import de.nava.informa.impl.basic.Channel;
import de.nava.informa.impl.basic.Item;

public abstract class RssServlet extends HttpServlet {
  HttpSession session;
  PrintWriter out;

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doPost(req, res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    SilverTrace.info("peasUtil", "RssServlet.doPost",
        "root.MSG_GEN_ENTER_METHOD");
    String instanceId = getObjectId(req);
    String userId = getUserId(req);
    String login = getLogin(req);
    String password = getPassword(req);

    // rechercher si le composant a bien le flux RSS autoris�
    if (isComponentRss(instanceId)) {
      try {
        SilverTrace.info("peasUtil", "RssServlet.doPost",
            "root.MSG_GEN_PARAM_VALUE", "InstanceId = " + instanceId);

        // V�rification que le login - password correspond bien � un utilisateur
        // dans Silverpeas
        // V�rification que le user a droit d'acc�s au composant
        AdminController adminController = new AdminController(null);
        UserFull user = adminController.getUserFull(userId);
        if (user != null && login.equals(user.getLogin())
            && password.equals(user.getPassword())
            && isComponentAvailable(adminController, instanceId, userId)) {

          String serverURL = getServerURL(adminController, user.getDomainId());
          ChannelIF channel = new Channel();

          // r�cup�ration de la liste des N �l�ments � remonter dans le flux
          int nbReturnedElements = getNbReturnedElements();
          Collection<Object> listElements = getListElements(instanceId,
              nbReturnedElements);

          // cr�ation d'une liste de ItemIF en fonction de la liste des �l�ments
          Object element;
          String title;
          URL link;
          String description;
          String creatorId;
          Date dateElement;
          ItemIF item;
          Iterator<Object> it = (Iterator) listElements.iterator();
          while (it.hasNext()) {
            element = it.next();
            title = getElementTitle(element, userId);
            link = new URL(serverURL + getElementLink(element, userId));
            description = getElementDescription(element, userId);
            dateElement = getElementDate(element);
            creatorId = getElementCreatorId(element);

            item = new Item();
            item.setTitle(title);
            item.setLink(link);
            item.setDescription(description);
            item.setDate(dateElement);

            if (StringUtil.isDefined(creatorId)) {
              UserDetail creator = adminController.getUserDetail(creatorId);
              if (creator != null)
                item.setCreator(creator.getDisplayedName());
            } else if (StringUtil.isDefined(getExternalCreatorId(element))) {
              item.setCreator(getExternalCreatorId(element));
            }

            channel.addItem(item);
          }

          // construction de l'objet Channel
          channel.setTitle(getChannelTitle(instanceId));
          
          URL componentUrl = new URL(serverURL + URLManager.getApplicationURL()
              + URLManager.getURL("useless", instanceId));
          channel.setLocation(componentUrl);

          // exportation du channel
          String encoding = "ISO-8859-1";
          res.setContentType("application/rss+xml");
          res.setHeader("Content-Disposition", "inline; filename=feeds.rss" );
          Writer writer = res.getWriter();
          RSS_2_0_Exporter rssExporter = new RSS_2_0_Exporter(writer, encoding);
          rssExporter.write(channel);

          if (rssExporter == null) {
            objectNotFound(req, res);
          }
        } else {
          objectNotFound(req, res);
        }
      } catch (Exception e) {
        objectNotFound(req, res);
      }
    }
  }

  public String getChannelTitle(String instanceId) {
    OrganizationController orga = new OrganizationController();
    ComponentInstLight instance = orga.getComponentInstLight(instanceId);
    if (instance != null)
      return instance.getLabel();

    return "";
  }

  public String getServerURL(AdminController admin, String domainId) {
    Domain defaultDomain = admin.getDomain(domainId);
    return defaultDomain.getSilverpeasServerURL();
  }

  public boolean isComponentRss(String instanceId) {
    OrganizationController orga = new OrganizationController();
    String paramRssValue = orga.getComponentParameterValue(instanceId, "rss");

    // rechercher si le composant a bien le flux RSS autoris�
    if ("yes".equalsIgnoreCase(paramRssValue)) {
      return true;
    }
    return false;
  }

  public boolean isComponentAvailable(AdminController admin, String instanceId,
      String userId) {
    return admin.isComponentAvailable(instanceId, userId);
  }

  public int getNbReturnedElements() {
    return 15;
  }

  public abstract Collection getListElements(String instanceId, int nbReturned)
      throws RemoteException;

  public abstract String getElementTitle(Object element, String userId);

  public abstract String getElementLink(Object element, String userId);

  public abstract String getElementDescription(Object element, String userId);

  public abstract Date getElementDate(Object element);

  public abstract String getElementCreatorId(Object element);

  public String getExternalCreatorId(Object element) {
    return "";
  }

  private String getObjectId(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo != null)
      return pathInfo.substring(1);
    return null;
  }

  private String getUserId(HttpServletRequest request) {
    return request.getParameter("userId");
  }

  private String getLogin(HttpServletRequest request) {
    return request.getParameter("login");
  }

  private String getPassword(HttpServletRequest request) {
    return request.getParameter("password");
  }

  private MainSessionController getMainSessionController(HttpServletRequest req) {
    HttpSession session = req.getSession(true);
    MainSessionController mainSessionCtrl = (MainSessionController) session
        .getAttribute("SilverSessionController");
    return mainSessionCtrl;
  }

  private boolean isUserLogin(HttpServletRequest req) {
    return (getMainSessionController(req) != null);
  }

  private void objectNotFound(HttpServletRequest req, HttpServletResponse res)
      throws IOException {
    boolean isLoggedIn = isUserLogin(req);
    if (!isLoggedIn)
      res.sendRedirect("/weblib/notFound.html");
    else
      res.sendRedirect(GeneralPropertiesManager.getGeneralResourceLocator()
          .getString("ApplicationURL")
          + "/admin/jsp/documentNotFound.jsp");
  }

}