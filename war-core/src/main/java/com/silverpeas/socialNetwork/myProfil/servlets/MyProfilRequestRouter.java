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
package com.silverpeas.socialNetwork.myProfil.servlets;

import com.silverpeas.directory.servlets.ImageProfil;
import com.silverpeas.socialNetwork.SocialNetworkException;
import com.silverpeas.socialNetwork.model.SocialInformationType;
import com.silverpeas.socialNetwork.myProfil.control.MyProfilSessionController;
import com.silverpeas.socialNetwork.user.model.SNContactUser;
import com.silverpeas.socialNetwork.user.model.SNFullUser;
import com.silverpeas.util.web.servlet.FileUploadUtil;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.ComponentSessionController;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.servlets.ComponentRequestRouter;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.UserFull;
import com.stratelia.webactiv.util.exception.UtilException;
import java.io.IOException;


import java.util.ArrayList;
import java.util.Enumeration;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author azzedine
 */
public class MyProfilRequestRouter extends ComponentRequestRouter {

  private static final String AVATAR_FOLDER = "avatar";
  private final int NUMBER_CONTACTS_TO_DISPLAY = 3;
  private MyProfilSessionController myProfilSC;
  @Override
  public String getSessionControlBeanName() {
    return "myProfil";
  }

  @Override
  public ComponentSessionController createComponentSessionController(
      MainSessionController mainSessionCtrl, ComponentContext componentContext) {
    return new MyProfilSessionController(mainSessionCtrl, componentContext);
  }
/**
 *
 * @param function
 * @param componentSC
 * @param request
 * @return String
 */
  @Override
  public String getDestination(String function, ComponentSessionController componentSC,
      HttpServletRequest request) {
    String destination = "#";

     myProfilSC = (MyProfilSessionController) componentSC;
    SNFullUser snUserFull = new SNFullUser(myProfilSC.getUserId());
    if (function.equalsIgnoreCase("MyEvents")) {
      try {
        request.setAttribute("type", SocialInformationType.EVENT);
      } catch (Exception ex) {
        Logger.getLogger(MyProfilRequestRouter.class.getName()).log(Level.SEVERE, null, ex);
      }
      destination = "/socialNetwork/jsp/myProfil/myProfilTemplate.jsp";
    } else if (function.equalsIgnoreCase("ALL") || function.equalsIgnoreCase("Main")) {
      request.setAttribute("type", SocialInformationType.ALL);
      destination = "/socialNetwork/jsp/myProfil/myProfilTemplate.jsp";
    } else if (function.equalsIgnoreCase("MyPhotos")) {
      request.setAttribute("type", SocialInformationType.PHOTO);
      destination = "/socialNetwork/jsp/myProfil/myProfilTemplate.jsp";
    } else if (function.equalsIgnoreCase("MyPubs")) {
      request.setAttribute("type", SocialInformationType.PUBLICATION);
      destination = "/socialNetwork/jsp/myProfil/myProfilTemplate.jsp";
    } else if (function.equalsIgnoreCase("validateChangePhoto")) {
      try {
        saveAvatar(request, snUserFull.getUserFull().getLogin());

        return getDestination("MyInfos", componentSC, request);
      } catch (Exception ex) {
        Logger.getLogger(MyProfilRequestRouter.class.getName()).log(Level.SEVERE, null, ex);
      }

    } else if (function.equalsIgnoreCase("MyInfos")) {
      UserFull uf = snUserFull.getUserFull();
      String[] array = uf.getPropertiesNames();
      List<String> propertiesKey = new ArrayList<String>();
      List<String> propertiesValue = new ArrayList<String>();
      List<String> properties = new ArrayList<String>();
      for (int i = 0; i < array.length; i++) {
        if (!array[i].startsWith("password")) {
          properties.add(array[i]);
          propertiesKey.add(uf.getSpecificLabel(myProfilSC.getLanguage(), array[i]));
          propertiesValue.add(uf.getValue(array[i]));
        }
      }
      request.setAttribute("specificLabels", uf.getSpecificLabels(componentSC.getLanguage()));
      request.setAttribute("properties", properties);
      request.setAttribute("propertiesKey", propertiesKey);
      request.setAttribute("propertiesValue", propertiesValue);
      destination = "/socialNetwork/jsp/myProfil/myInfoTemplate.jsp";
    } else if (function.equalsIgnoreCase("updateMyInfos")) {

      // process extra properties
      Hashtable<String, String> properties = new Hashtable<String, String>();
      Enumeration<String> parameters = request.getParameterNames();
      String parameterName = null;
      String property = null;
      while (parameters.hasMoreElements()) {

        parameterName = parameters.nextElement();
        if (parameterName.startsWith("prop_")) {
          property = parameterName.substring(5, parameterName.length()); // remove
          // "prop_"
          properties.put(property, request.getParameter(parameterName));
        }
      }
      try {
        myProfilSC.modifyUser(myProfilSC.getUserId(), properties);
      } catch (SocialNetworkException ex) {
        SilverTrace.error("socialNetwork",
            "MyProfilRequestRouter.getDestination",
            "root.EX_GET_DESTINATION_ERROR", "", ex);
      }

      return getDestination("MyInfos", componentSC, request);
    }
    request.setAttribute("snUserFull", snUserFull);
    List<String> contactIds = myProfilSC.getContactsIdsForUser(myProfilSC.getUserId());
    request.setAttribute("contacts", chooseContactsToDisplay(contactIds));
    request.setAttribute("contactsNumber",contactIds.size());
    return destination;
  }
/**
 * method to change profile Photo
 * @param request
 * @param nameAvatar
 * @return String
 * @throws IOException
 * @throws UtilException
 */
  protected String saveAvatar(HttpServletRequest request, String nameAvatar)
      throws IOException, UtilException {
    List<FileItem> parameters = FileUploadUtil.parseRequest(request);
    FileItem file = FileUploadUtil.getFile(parameters, "WAIMGVAR0");
    String avatar = nameAvatar + ".jpg";
    ImageProfil img = new ImageProfil(avatar, AVATAR_FOLDER);
    img.saveImage(file.getInputStream());
    return avatar;
  }
  /**
   * methode to choose (x) contacts for display it in the page profil
   * x is the number of contacts 
   * the methode use Random rule
   * @param contactIds
   * @return List<SNContactUser>
   */
  private List<SNContactUser> chooseContactsToDisplay(List<String> contactIds) {
    int numberOfContactsTodisplay;
    List<SNContactUser> contacts = new ArrayList<SNContactUser>();
    try {
      numberOfContactsTodisplay = Integer.parseInt(myProfilSC.getSettings().getString(
          "numberOfContactsTodisplay"));
    } catch (NumberFormatException ex) {
      numberOfContactsTodisplay = NUMBER_CONTACTS_TO_DISPLAY;
    }
    if ( contactIds.size()<= numberOfContactsTodisplay) {
      for (int i = 0; i < contactIds.size(); i++) {
        contacts.add(new SNContactUser(contactIds.get(i)));
      }
    } else {
      Random random = new Random();
       int indexContactsChoosed = (random.nextInt(contactIds.size()) );
      for (int i = 0; i < numberOfContactsTodisplay; i++) {
        contacts.add(new SNContactUser(contactIds.get((indexContactsChoosed+i)%numberOfContactsTodisplay)));
      }
    }

    return contacts;
  }
}
