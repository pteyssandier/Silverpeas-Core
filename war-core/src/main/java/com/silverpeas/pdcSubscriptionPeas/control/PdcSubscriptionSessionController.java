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

package com.silverpeas.pdcSubscriptionPeas.control;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.RemoveException;

import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;
import com.stratelia.webactiv.util.node.control.NodeBm;
import com.stratelia.webactiv.util.node.control.NodeBmHome;
import com.stratelia.webactiv.util.node.model.NodePK;
import com.stratelia.webactiv.util.subscribe.control.SubscribeBm;
import com.stratelia.webactiv.util.subscribe.control.SubscribeBmHome;

import com.stratelia.silverpeas.peasCore.*;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.silverpeas.pdc.control.PdcBm;
import com.stratelia.silverpeas.pdc.control.PdcBmImpl;
import com.stratelia.silverpeas.pdc.model.AxisHeader;
import com.stratelia.silverpeas.pdc.model.PdcException;
import com.stratelia.silverpeas.classifyEngine.Criteria;
import com.silverpeas.pdcSubscription.ejb.PdcSubscriptionBm;
import com.silverpeas.pdcSubscription.ejb.PdcSubscriptionBmHome;
import com.silverpeas.pdcSubscription.PdcSubscriptionRuntimeException;
import com.silverpeas.pdcSubscription.model.PDCSubscription;

public class PdcSubscriptionSessionController extends AbstractComponentSessionController {

  private PdcSubscriptionBm scBm = null;
  private PdcBm pdcBm = null;

  /**
   * Constructor Creates new PdcSubscription Session Controller
   */
  public PdcSubscriptionSessionController(
      MainSessionController mainSessionCtrl, ComponentContext componentContext) {
    super(mainSessionCtrl, componentContext,
        "com.silverpeas.pdcSubscriptionPeas.multilang.pdcSubscriptionBundle",
        "com.silverpeas.pdcSubscriptionPeas.settings.pdcSubscriptionPeasIcons");
  }

  /**
   * Method initEJB initializes EJB
   */
  private void initEJB() {
    if (scBm == null) {
      try {
        PdcSubscriptionBmHome icEjbHome = (PdcSubscriptionBmHome) EJBUtilitaire
            .getEJBObjectRef(JNDINames.PDC_SUBSCRIPTION_EJBHOME,
            PdcSubscriptionBmHome.class);
        scBm = icEjbHome.create();
      } catch (Exception e) {
        throw new PdcSubscriptionRuntimeException(
            "PdcSubscriptionSessionController.initEJB()",
            SilverTrace.TRACE_LEVEL_ERROR, "root.EX_CANT_GET_REMOTE_OBJECT", e);
      }
    }
  }

  private PdcBm getPdcBm() {
    if (pdcBm == null) {
      pdcBm = new PdcBmImpl();
    }
    return pdcBm;
  }

  private SubscribeBm getSubscribeBm() {
    SubscribeBm subscribeBm = null;
    try {
      SubscribeBmHome subscribeBmHome = (SubscribeBmHome) EJBUtilitaire
          .getEJBObjectRef(JNDINames.SUBSCRIBEBM_EJBHOME, SubscribeBmHome.class);
      subscribeBm = subscribeBmHome.create();
    } catch (Exception e) {
      throw new PdcSubscriptionRuntimeException(
          "PdcSubscriptionSessionController.getSubscribeBm()",
          SilverTrace.TRACE_LEVEL_ERROR, "root.EX_CANT_GET_REMOTE_OBJECT", e);
    }
    return subscribeBm;
  }

  public NodeBm getNodeBm() {
    NodeBm nodeBm = null;
    try {
      NodeBmHome nodeBmHome = (NodeBmHome) EJBUtilitaire.getEJBObjectRef(
          JNDINames.NODEBM_EJBHOME, NodeBmHome.class);
      nodeBm = nodeBmHome.create();
    } catch (Exception e) {
      throw new PdcSubscriptionRuntimeException(
          "PdcSubscriptionSessionController.getNodeBm()",
          SilverpeasRuntimeException.ERROR, "root.EX_CANT_GET_REMOTE_OBJECT", e);
    }
    return nodeBm;
  }

  public Collection getUserSubscribe(String userId) {
    Collection subscribe = new ArrayList();
    if (userId == null || "".equals(userId) || ("null".equals(userId)))
      userId = getUserId();
    try {
      Collection list = getSubscribeBm().getUserSubscribePKs(userId);
      Iterator i = list.iterator();
      while (i.hasNext()) {
        NodePK pk = (NodePK) i.next();

        try {
          Collection path = getNodeBm().getPath(pk);
          subscribe.add(path);
        } catch (RemoteException e) {
          // User is subscribe to a non existing component or topic
          // Do nothing. Process next subscription.
        }
      }
    } catch (RemoteException e) {
      throw new PdcSubscriptionRuntimeException(
          "PdcSubscriptionSessionController.getUserSubscribe()",
          SilverTrace.TRACE_LEVEL_ERROR, "root.EX_CANT_GET_REMOTE_OBJECT", e);
    }
    return subscribe;
  }

  public void deleteThemes(String[] themes) {
    try {
      for (int i = 0; i < themes.length; i++) {
        // convertir la chaine en NodePK
        String nodeId = themes[i].substring(0, themes[i].lastIndexOf("-"));
        String instanceId = themes[i].substring(themes[i].lastIndexOf("-") + 1,
            themes[i].length());
        NodePK node = new NodePK(nodeId, instanceId);
        getSubscribeBm().removeSubscribe(getUserId(), node);
      }
    } catch (RemoteException e) {
      throw new PdcSubscriptionRuntimeException(
          "PdcSubscriptionSessionController.getUserSubscribe()",
          SilverTrace.TRACE_LEVEL_ERROR, "root.EX_CANT_GET_REMOTE_OBJECT", e);
    }
  }

  public ArrayList getUserPDCSubscription() throws RemoteException {
    initEJB();
    return scBm.getPDCSubscriptionByUserId(Integer.parseInt(getUserId()));
  }

  public ArrayList getUserPDCSubscription(int userId) throws RemoteException {
    initEJB();
    return scBm.getPDCSubscriptionByUserId(userId);
  }

  public PDCSubscription getPDCSubsriptionById(int id) throws RemoteException {
    initEJB();
    return scBm.getPDCSubsriptionById(id);
  }

  public void createPDCSubscription(PDCSubscription subscription)
      throws RemoteException {
    initEJB();
    subscription.setId(scBm.createPDCSubscription(subscription));
  }

  public void updateIC(PDCSubscription subscription) throws RemoteException {
    initEJB();
    scBm.updatePDCSubscription(subscription);
  }

  public void removePDCSubscriptionById(int id) throws RemoteException {
    initEJB();
    scBm.removePDCSubscriptionById(id);
  }

  public void removeICByPK(int[] ids) throws RemoteException {
    initEJB();
    scBm.removePDCSubscriptionById(ids);
  }

  public AxisHeader getAxisHeader(String axisId) throws PdcException {
    AxisHeader axisHeader = getPdcBm().getAxisHeader(axisId);
    return axisHeader;
  }

  public List getFullPath(String valueId, String treeId) throws PdcException {
    return getPdcBm().getFullPath(valueId, treeId);
  }

  private String getLastValueOf(String path) {

    String newValueId = path;
    int len = path.length();
    path = path.substring(0, len - 1); // on retire le slash

    if (path.equals("/")) {
      newValueId = newValueId.substring(1); // on retire le slash
    } else {
      int lastIdx = path.lastIndexOf("/");
      newValueId = path.substring(lastIdx + 1);
    }
    return newValueId;
  }

  public List getPathCriterias(List searchCriterias) throws Exception {
    ArrayList pathCriteria = new ArrayList();

    if (searchCriterias.size() > 0) {
      for (int i = 0; i < searchCriterias.size(); i++) {
        Criteria sc = (Criteria) searchCriterias.get(i);

        int searchAxisId = sc.getAxisId();
        String searchValue = getLastValueOf(sc.getValue());
        AxisHeader axis = getAxisHeader(new Integer(searchAxisId).toString());

        String treeId = null;
        if (axis != null) {
          treeId = new Integer(axis.getRootId()).toString();
        }

        List fullPath = new ArrayList();
        if (searchValue != null && treeId != null) {
          fullPath = getFullPath(searchValue, treeId);
        }

        pathCriteria.add(fullPath);
      }
    }
    return pathCriteria;
  }

  public void close() {
    try {
      if (scBm != null)
        scBm.remove();
    } catch (RemoteException e) {
      SilverTrace.error("pdcSubscription",
          "PdcSubscriptionSessionController.close", "", e);
    } catch (RemoveException e) {
      SilverTrace.error("pdcSubscription",
          "PdcSubscriptionSessionController.close", "", e);
    }
  }

}
