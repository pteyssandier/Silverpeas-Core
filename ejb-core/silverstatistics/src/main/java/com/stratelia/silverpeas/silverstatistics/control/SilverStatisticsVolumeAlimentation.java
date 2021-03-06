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

package com.stratelia.silverpeas.silverstatistics.control;

import com.silverpeas.util.FileUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminController;
import com.stratelia.webactiv.beans.admin.ComponentInst;
import com.stratelia.webactiv.beans.admin.SpaceInst;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the alimentation for the statistics on volume. It gets the number of elements from each
 * components from each space. All components must implements the ComponentStatisticsInterface.
 * @author sleroux
 */
public class SilverStatisticsVolumeAlimentation {
  private static ResourceBundle resources = null;

  static {
      try {
        resources = FileUtil.loadBundle(
            "com.stratelia.silverpeas.silverstatistics.SilverStatistics", Locale.getDefault());
      } catch (Exception ex) {
        SilverTrace.error("silverstatistics",
            "SilverStatisticsVolumeAlimentation",
            "root.EX_CLASS_NOT_INITIALIZED", ex);
      }
  }

  /**
   * Method declaration
   * @see
   */
  public static void makeVolumeAlimentationForAllComponents() {
    java.util.Date now = new java.util.Date();
    // get all spaces
    List<String> listAllSpacesId = getAllSpacesAndAllSubSpacesId();
    String currentSpaceId  ;
    String currentComponentsId  ;

    if (listAllSpacesId != null && !listAllSpacesId.isEmpty()) {

      for (String aListAllSpacesId : listAllSpacesId) {
        ArrayList listAllComponentsInst;

        // get all components from a space
        currentSpaceId = aListAllSpacesId;
        listAllComponentsInst = getAllComponentsInst(currentSpaceId);

        for (Object aListAllComponentsInst : listAllComponentsInst) {
          Collection collectionUserIdCountVolume;

          ComponentInst ci = (ComponentInst) aListAllComponentsInst;

          currentComponentsId = ci.getId();

          // get all elements from a component
          collectionUserIdCountVolume = getCollectionUserIdCountVolume(
              currentSpaceId, ci);

          if (collectionUserIdCountVolume != null) {

            for (Object aCollectionUserIdCountVolume : collectionUserIdCountVolume) {
              UserIdCountVolumeCouple currentUserIdCountVolume =
                  (UserIdCountVolumeCouple) aCollectionUserIdCountVolume;
              SilverTrace
                  .debug(
                      "silverstatistics",
                      "SilverStatisticsVolumeAlimentation.makeVolumeAlimentationForAllComponents",
                      "userId= " + currentUserIdCountVolume.getUserId()
                          + " countVolume=  "
                          + currentUserIdCountVolume.getCountVolume()
                          + " name= " + ci.getName() + " spaceId= "
                          + currentSpaceId + " compoId= " + currentComponentsId);

              // notify statistics
              SilverStatisticsManager.getInstance().addStatVolume(
                  currentUserIdCountVolume.getUserId(),
                  currentUserIdCountVolume.getCountVolume(), now, ci.getName(),
                  currentSpaceId, currentComponentsId);

            }
          }
        }
      }
    }
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  private static List<String> getAllSpacesAndAllSubSpacesId() {
    List<String> resultList = new ArrayList<String>();
    AdminController myAdminController = new AdminController("");
    String[] spaceIds = null;

    try {
      spaceIds = myAdminController.getAllSpaceIds();
    } catch (Exception e) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsVolumeAlimentation.getAllSpacesAndAllSubSpacesId()",
          "admin.MSG_ERR_GET_ALL_SPACE_IDS", e);
    }

    if (spaceIds != null) {
      for (String spaceId : spaceIds) {
        resultList.add(spaceId);
      }
    }
    return resultList;
  }

  /**
   * Method declaration
   * @param spaceId
   * @return
   * @see
   */
  private static ArrayList getAllComponentsInst(String spaceId) {
    AdminController myAdminController = new AdminController("");
    SpaceInst mySpaceInst = myAdminController.getSpaceInstById(spaceId);

    return mySpaceInst.getAllComponentsInst();
  }

  /**
   * Method declaration
   * @param spaceId
   * @param componentId
   * @return
   * @see
   */
  private static Collection getCollectionUserIdCountVolume(String spaceId,
      ComponentInst ci) {
    ComponentStatisticsInterface myCompo  ;
    Collection c = null;

    try {
      SilverTrace
          .info(
          "silverstatistics",
          "SilverStatisticsVolumeAlimentation.getCollectionUserIdCountVolume()",
          "root.MSG_GEN_PARAM_VALUE", "spaceId=" + spaceId);
      SilverTrace
          .info(
          "silverstatistics",
          "SilverStatisticsVolumeAlimentation.getCollectionUserIdCountVolume()",
          "root.MSG_GEN_PARAM_VALUE", "componentId=" + ci.getId());

      String className = getComponentStatisticsClassName(ci.getName());
      if (className != null) {
        myCompo = (ComponentStatisticsInterface) Class.forName(className)
            .newInstance();
        Collection v = myCompo.getVolume(spaceId, ci.getId());

        c = agregateUser(v);
      }
    } catch (ClassNotFoundException ce) {
      SilverTrace
          .info(
          "silverstatistics",
          "SilverStatisticsVolumeAlimentation.getCollectionUserIdCountVolume()",
          "silverstatistics.EX_SUPPLY_VOLUME_COMPONENT_NOT_FOUND",
          "component = " + ci.getName(), ce);
    } catch (Exception e) {
      SilverTrace
          .error(
          "silverstatistics",
          "SilverStatisticsVolumeAlimentation.getCollectionUserIdCountVolume()",
          "silverstatistics.EX_SUPPLY_VOLUME_COMPONENT_FAILED",
          "component = " + ci.getName(), e);
    }
    return c;
  }

  private static String getComponentStatisticsClassName(String componentName) {
    String componentStatisticsClassName  ;

    try {
      componentStatisticsClassName = resources.getString(componentName);
    } catch (MissingResourceException e) {
      // in order to trigger the ClassNotFoundException of the
      // getCollectionUserIdCountVolume method
      componentStatisticsClassName = null;
      SilverTrace
          .error(
          "silverstatistics",
          "SilverStatisticsVolumeAlimentation.getCollectionUserIdCountVolume()",
          "silverstatistics.EX_SUPPLY_VOLUME_COMPONENT_FAILED",
          "No statistic implementation class for component '"
          + componentName + "'");
    }

    return componentStatisticsClassName;
  }

  private static Collection agregateUser(Collection in) {

    if (in == null)
      return null;

    ArrayList myArrayList = new ArrayList();

    // parcours collection initiale
    for (Object anIn : in) {
      // lecture d'un userId
      // s'il n'existe pas dans la collection finale alors on l'ajoute
      // sinon on modifie le countVolume et on passe au suivant

      UserIdCountVolumeCouple eltIn = (UserIdCountVolumeCouple) anIn;
      UserIdCountVolumeCouple eltOut = getCouple(myArrayList, eltIn);

      SilverTrace.debug("silverstatistics",
          "SilverStatisticsVolumeAlimentation.agregateUser)",
          "eltIn.getUserId() = " + eltIn.getUserId()
              + "eltIn.getCountVolume() = " + eltIn.getCountVolume());

      if (eltOut == null) {
        myArrayList.add(eltIn);
        SilverTrace.debug("silverstatistics",
            "SilverStatisticsVolumeAlimentation.agregateUser)", "add eltIn");
      } else {
        eltOut.setCountVolume(eltIn.getCountVolume() + eltOut.getCountVolume());
        SilverTrace.debug("silverstatistics",
            "SilverStatisticsVolumeAlimentation.agregateUser)",
            "eltOut.getUserId() = " + eltOut.getUserId()
                + "eltOut.getCountVolume() = " + eltOut.getCountVolume());
      }

    }

    return myArrayList;
  }

  private static UserIdCountVolumeCouple getCouple(Collection in,
      UserIdCountVolumeCouple eltIn) {
    for (Object anIn : in) {
      UserIdCountVolumeCouple elt = (UserIdCountVolumeCouple) anIn;
      if (elt.getUserId().equals(eltIn.getUserId())) {
        return elt;
      }
    }
    return null;
  }

}
