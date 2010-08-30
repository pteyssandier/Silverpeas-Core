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
package com.silverpeas.socialNetwork.newsFeed.servlets;

import com.silverpeas.socialNetwork.model.SocialInformation;
import com.silverpeas.socialNetwork.myProfil.control.SocialNetworkService;

import java.io.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;

import org.json.JSONObject;
import com.silverpeas.socialNetwork.model.SocialInformationType;
import com.silverpeas.socialNetwork.newsFeed.control.NewsFeedService;
import com.silverpeas.socialNetwork.user.model.SNContactUser;
import com.silverpeas.util.EncodeHelper;
import com.silverpeas.util.StringUtil;

import com.stratelia.silverpeas.peasCore.MainSessionController;


import com.stratelia.webactiv.util.GeneralPropertiesManager;
import com.stratelia.webactiv.util.ResourceLocator;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;

public class NewsFeedJSONServlet extends HttpServlet {

  private static final int DEFAULT_OFFSET = 0;
  private static final int DEFAULT_ELEMENT_PER_PAGE = 3;
  private String mContext = GeneralPropertiesManager.getGeneralResourceLocator().getString(
      "ApplicationURL");
  private String iconURL = mContext + "/socialNetwork/jsp/icons/";
  private SocialInformationType type;
  private SocialNetworkService socialNetworkService = new SocialNetworkService();
  private NewsFeedService newsFeedService = new NewsFeedService();
  private ResourceLocator multilang;
  private ResourceLocator settings;
  private Locale locale;

  /**
   * servlet method for returning JSON format
   * @param request
   * @param response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    locale = request.getLocale();
    HttpSession session = request.getSession();
    MainSessionController m_MainSessionCtrl = (MainSessionController) session.getAttribute(
        "SilverSessionController");
    String userId = m_MainSessionCtrl.getUserId();
    //initialze myId for newsFeedService
    newsFeedService.setMyId(Integer.parseInt(userId));
    //initialze myId for socialNetworkService
    socialNetworkService.setMyId(userId);

    //initialze myContactsIds
    List<String> myContactsIds = newsFeedService.getMyContactsIds();
    myContactsIds.add(0, userId);// add my self to the list
    socialNetworkService.setMyContactsIds(myContactsIds);
    multilang = new ResourceLocator(
        "com.silverpeas.socialNetwork.multilang.socialNetworkBundle", locale);
    settings = new ResourceLocator(
        "com.silverpeas.socialNetwork.settings.socialNetworkSettings", locale);

      Map<Date, List<SocialInformation>> map = new LinkedHashMap<Date, List<SocialInformation>>();


      try {
        //recover the type
        type = SocialInformationType.valueOf(request.getParameter("type"));

        //recover the first Element
        int offset = DEFAULT_OFFSET;
        if (StringUtil.isInteger(request.getParameter("offset"))) {
          offset = Integer.parseInt(request.getParameter("offset"));
        }
        //recover the numbre elements per page
        int limit = DEFAULT_ELEMENT_PER_PAGE;
        if (StringUtil.isInteger(settings.getString("profil.elements_per_page"))) {
          limit = Integer.parseInt(
              settings.getString("profil.elements_per_page"));
        }

        map = socialNetworkService.getSocialInformationOfMyContacts(type, limit, offset);

      } catch (Exception ex) {
        Logger.getLogger(NewsFeedJSONServlet.class.getName()).log(Level.SEVERE, null, ex);
      }
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();
      out.println(toJsonS(map));

   

  }

  /**
   * convert the SocialInormation to JSONObject
   * @param event
   * @return JSONObject
   */
  private JSONObject toJson(SocialInformation event) {
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", locale);

    JSONObject valueObj = new JSONObject();
    SNContactUser contactUser1 = new SNContactUser(event.getAuthor());
    if (event.getType().equals(SocialInformationType.RELATIONSHIP.toString())) {

      SNContactUser contactUser2 = new SNContactUser(event.getTitle());
      valueObj.put("type", event.getType());
      valueObj.put("author", sNContactUserToJSON(contactUser1));
      valueObj.put("title", sNContactUserToJSON(contactUser2));
      valueObj.put("hour", formatTime.format(event.getDate()));
      valueObj.put("url", mContext + event.getUrl());
      valueObj.put("icon", mContext + contactUser2.getProfilPhoto());
      return valueObj;
    } else if (event.getType().endsWith(SocialInformationType.EVENT.toString())) {
      return eventSocialToJSON(event, contactUser1);
    }
    valueObj.put("type", event.getType());
    valueObj.put("author", sNContactUserToJSON(contactUser1));
    
      valueObj.put("description", EncodeHelper.javaStringToHtmlParagraphe(event.getDescription()+""));
    
    if (event.getType().equals(SocialInformationType.STATUS.toString())) {
      SNContactUser contactUser = new SNContactUser(event.getTitle());
      valueObj.put("title", contactUser.getDisplayedName());
    } else {
      valueObj.put("title", event.getTitle());
    }
    //if time not identified display string empty
    if ("00:00".equalsIgnoreCase(formatTime.format(event.getDate()))) {
      valueObj.put("hour", "");
    } else {
      valueObj.put("hour", formatTime.format(event.getDate()));
    }
    valueObj.put("url", mContext + event.getUrl());
    valueObj.put("icon",
        getIconUrl(SocialInformationType.valueOf(event.getType())) + event.getIcon());
    valueObj.put("label", multilang.getString("newsFeed." + event.getType().toLowerCase() + ".apdated." + event.
        isUpdeted()));

    return valueObj;
  }

  /**
   * convert the Map of socailInformation to JSONArray
   * @param Map<Date, List<SocialInformation>> map
   * @return JSONArray
   */
  private JSONArray toJsonS(Map<Date, List<SocialInformation>> map) {
    SimpleDateFormat formatDate = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
    JSONArray result = new JSONArray();
    for (Map.Entry<Date, List<SocialInformation>> entry : map.entrySet()) {
      JSONArray jsonArrayDateWithValues = new JSONArray();
      Object key = entry.getKey();

      JSONArray jsonArray = new JSONArray();
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("day", formatDate.format(key));
      List<SocialInformation> events = entry.getValue();
      for (SocialInformation event : events) {
        System.out.println("tset"+event.getDescription());
        jsonArray.put(toJson(event));
      }
      jsonArrayDateWithValues.put(jsonObject);
      jsonArrayDateWithValues.put(jsonArray);
      result.put(jsonArrayDateWithValues);
    }
    return result;
  }

  /**
   * convert socailInformationEvent to JSONObject
   * @param event
   * @return JSONObject
   */
  private JSONObject eventSocialToJSON(SocialInformation event, SNContactUser contactUser) {
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", locale);
    JSONObject valueObj = new JSONObject();
    valueObj.put("type", event.getType());
    valueObj.put("author", sNContactUserToJSON(contactUser));
    valueObj.put("hour", formatTime.format(event.getDate()));
    valueObj.put("url", mContext + event.getUrl());
    valueObj.put("icon",
        getIconUrl(SocialInformationType.valueOf(event.getType())) + event.getIcon());
    if (!event.isUpdeted() && event.getIcon().startsWith(event.getType() + "_private")) {
      valueObj.put("title", multilang.getString("profil.icon.private.event"));
      valueObj.put("description", "");
    } else {
      valueObj.put("title", event.getTitle());
      valueObj.put("description", event.getDescription());
    }

    return valueObj;
  }

  /**
   * convert the Map of SNContactUser to JSONArray
   * @param Map<Date, List<SocialInformation>> map
   * @return JSONArray
   */
  private JSONObject sNContactUserToJSON(SNContactUser user) {
    JSONObject userJSON = new JSONObject();
    userJSON.put("id", user.getUserId());
    userJSON.put("displayedName", user.getDisplayedName());
    userJSON.put("profilPhoto", mContext + user.getProfilPhoto());
    return userJSON;
  }

  /**
   * return the url of icon
   * @param SocialInformationType type
   * @return String
   */
  private String getIconUrl(SocialInformationType type) {
    String url = iconURL;
    if (type.equals(SocialInformationType.PHOTO)) {
      url = mContext;
    }
    return url;
  }

  
}
