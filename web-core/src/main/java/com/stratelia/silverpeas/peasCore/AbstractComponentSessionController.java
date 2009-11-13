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
package com.stratelia.silverpeas.peasCore;

import com.silverpeas.util.clipboard.ClipboardSelection;
import java.net.URLEncoder;
import java.util.List;

import com.stratelia.silverpeas.alertUser.AlertUser;
import com.stratelia.silverpeas.genericPanel.GenericPanel;
import com.stratelia.silverpeas.selection.Selection;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.SilverpeasRole;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.personalization.control.ejb.PersonalizationBm;
import com.stratelia.webactiv.util.ResourceLocator;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Base class for all component session controller.
 */
public class AbstractComponentSessionController implements
    ComponentSessionController {
  private MainSessionController controller = null;
  private ComponentContext context = null;
  private String rootName = null;
  private ResourceLocator message = null;
  private ResourceLocator icon = null;
  private String messageLanguage = null;
  private String messageFile = null;
  private String iconFile = null;
  private ResourceLocator settings = null;
  private String settingsFile = null;

  /**
   * Constructor declaration
   * 
   * 
   * @param controller
   * @param spaceId
   * @param componentId
   * 
   * @see
   */
  public AbstractComponentSessionController(MainSessionController controller,
      String spaceId, String componentId) {
    this.controller = controller;
    // this.messageLanguage = controller.getFavoriteLanguage();
    this.context = controller.createComponentContext(spaceId, componentId);
    setComponentRootName(URLManager.getComponentNameFromComponentId(componentId));
  }

  /**
   * Constructor declaration
   * 
   * 
   * @param controller
   * @param context
   * 
   * @see
   */
  public AbstractComponentSessionController(MainSessionController controller,
      ComponentContext context) {
    this(controller, context, null);
  }

  /**
   * Constructor declaration
   * 
   * 
   * @param controller
   * @param context
   * @param resourceFileName
   * 
   * @see
   */
  public AbstractComponentSessionController(MainSessionController controller,
      ComponentContext context, String resourceFileName) {
    this.controller = controller;
    // this.messageLanguage = controller.getFavoriteLanguage();
    this.context = context;
    setComponentRootName(URLManager.getComponentNameFromComponentId(
        getComponentId()));
    setResourceFileName(resourceFileName);
  }

  public AbstractComponentSessionController(MainSessionController controller,
      ComponentContext context, String multilangFileName, String iconFileName) {
    this.controller = controller;
    // this.messageLanguage = controller.getFavoriteLanguage();
    this.context = context;
    setComponentRootName(URLManager.getComponentNameFromComponentId(
        getComponentId()));
    setMultilangFileName(multilangFileName);
    setIconFileName(iconFileName);
  }

  public AbstractComponentSessionController(MainSessionController controller,
      ComponentContext context, String multilangFileName, String iconFileName,
      String settingsFileName) {
    this.controller = controller;
    this.context = context;
    setComponentRootName(URLManager.getComponentNameFromComponentId(
        getComponentId()));
    setMultilangFileName(multilangFileName);
    setIconFileName(iconFileName);
    this.settingsFile = settingsFileName;
  }

  public ResourceLocator getMultilang() {
    SilverTrace.info("peasCore",
        "AbstractComponentSessionController.getMultilang()",
        "root.MSG_GEN_ENTER_METHOD", "Current Language="
        + controller.getFavoriteLanguage());
    if (message != null
        && !message.getLanguage().equals(controller.getFavoriteLanguage())) {
      // the resourcelocator language doesn't match with the current language
      setMultilangFileName(messageFile);
    }
    return message;
  }

  public ResourceLocator getIcon() {
    if (icon != null
        && !icon.getLanguage().equals(controller.getFavoriteLanguage())) {
      // the resourcelocator language doesn't match with the current language
      setIconFileName(iconFile);
    }
    return icon;
  }

  public ResourceLocator getSettings() {
    if (settings == null && settingsFile != null) {
      settings = new ResourceLocator(settingsFile, "fr");
    }
    return settings;
  }

  /**
   * Method declaration
   * 
   * 
   * @param resourceFileName
   * 
   * @see
   */
  public void setMultilangFileName(String multilangFileName) {
    messageFile = multilangFileName;
    if (messageFile != null) {
      try {
        messageLanguage = getLanguage();
        message = new ResourceLocator(messageFile, messageLanguage);
        // messageLanguage = getLanguage();
        SilverTrace.info("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.MSG_GEN_EXIT_METHOD", "Language=" + messageLanguage);
      } catch (Exception e) {
        SilverTrace.error("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.EX_CANT_GET_LANGUAGE_RESOURCE", "File=" + messageFile
            + "|Language=" + getLanguage(), e);
        message = new ResourceLocator(messageFile, "fr");
        // Une erreur s'est produite : on se rabat sur la langue par defaut.
        // Cependant, messageLanguage doit rester a la bonne valeur et ne pas
        // passer a fr, sinon, getString re-appellera cette fonction sans cesses
        messageLanguage = getLanguage();
      }
    } else {
      message = null;
    }
  }

  /**
   * Method declaration
   * 
   * 
   * @param resourceFileName
   * 
   * @see
   */
  public void setIconFileName(String iconFileName) {
    iconFile = iconFileName;
    if (iconFile != null) {
      try {
        messageLanguage = getLanguage();
        icon = new ResourceLocator(iconFile, messageLanguage);
        // messageLanguage = getLanguage();
        SilverTrace.info("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.MSG_GEN_EXIT_METHOD", "Language=" + messageLanguage);
      } catch (Exception e) {
        SilverTrace.error("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.EX_CANT_GET_LANGUAGE_RESOURCE", "File=" + messageFile
            + "|Language=" + getLanguage(), e);
        icon = new ResourceLocator(iconFile, "fr");
        // Une erreur s'est produite : on se rabat sur la langue par defaut.
        // Cependant, messageLanguage doit rester a la bonne valeur et ne pas
        // passer a fr, sinon, getString re-appellera cette fonction sans cesses
        messageLanguage = getLanguage();
      }
    } else {
      icon = null;
    }
  }

  /**
   * Method declaration
   * 
   * 
   * @param resourceFileName
   * 
   * @see
   */
  public void setResourceFileName(String resourceFileName) {
    SilverTrace.info("peasCore",
        "AbstractComponentSessionController.setResourceFileName()",
        "root.MSG_GEN_PARAM_VALUE", "File=" + resourceFileName);
    messageFile = resourceFileName;
    if (messageFile != null) {
      try {
        messageLanguage = getLanguage();
        message = new ResourceLocator(messageFile, messageLanguage);
        // messageLanguage = getLanguage();
        SilverTrace.info("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.MSG_GEN_EXIT_METHOD", "Language=" + messageLanguage);
      } catch (Exception e) {
        SilverTrace.error("peasCore",
            "AbstractComponentSessionController.setResourceFileName()",
            "root.EX_CANT_GET_LANGUAGE_RESOURCE", "File=" + messageFile
            + "|Language=" + getLanguage(), e);
        message = new ResourceLocator(messageFile, "fr");
        // Une erreur s'est produite : on se rabat sur la langue par defaut.
        // Cependant, messageLanguage doit rester a la bonne valeur et ne pas
        // passer a fr, sinon, getString re-appellera cette fonction sans cesses
        messageLanguage = getLanguage();
      }
    } else {
      message = null;
    }
  }

  /**
   * Method declaration
   * 
   * 
   * @param resName
   * 
   * @return
   * 
   * @see
   */
  public String getString(String resName) {
    String theLanguage = getLanguage();

    // If the language changed, Re-open a new ResourceLocator
    if ((theLanguage != null) || (message == null)) {
      if ((message == null) || (messageLanguage == null)
          || (messageLanguage.equals(theLanguage) == false)) {
        setResourceFileName(messageFile);
      }
    }

    if (message == null) {
      return resName;
    } else {
      return message.getString(resName);
    }
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  private MainSessionController getMainSessionController() {
    return controller;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  @Override
  public OrganizationController getOrganizationController() {
    return getMainSessionController().getOrganizationController();
  }

  /**
   * Return the user language
   */
  @Override
  public String getLanguage() {
    return getMainSessionController().getFavoriteLanguage();
  }

  public String getFavoriteSpace() {
    return getMainSessionController().getFavoriteSpace();
  }

  /**
   * The utilization of this method is allowed only for
   * PersonalizationSessionController
   */
  public void setLanguageToMainSessionController(String newLanguage) {
    // change the language into the mainSessionController
    getMainSessionController().setFavoriteLanguage(newLanguage);
  }

  /**
   * The utilization of this method is allowed only for
   * PersonalizationSessionController
   */
  public void setFavoriteSpaceToMainSessionController(String newSpace) {
    getMainSessionController().setFavoriteSpace(newSpace);
  }

  /**
   * Return the user language
   */
  public String getLook() {
    return getMainSessionController().getFavoriteLook();
  }

  /**
   * Return the UserDetail of the current user
   */
  @Override
  public UserDetail getUserDetail() {
    return getMainSessionController().getCurrentUserDetail();
  }

  public UserDetail getUserDetail(String userId) {
    return getOrganizationController().getUserDetail(userId);
  }

  /**
   * Return the UserId of the current user
   */
  @Override
  public String getUserId() {
    return getUserDetail().getId();
  }

  /**
   * Return the space label (as known by the user)
   */
  @Override
  public String getSpaceLabel() {
    return context.getCurrentSpaceName();
  }

  /**
   * Return the space id
   */
  @Override
  public String getSpaceId() {
    return context.getCurrentSpaceId();
  }

  public String getComponentName() {
    return context.getCurrentComponentName();
  }

  /**
   * Return the component label (as known by the user)
   */
  public String getComponentLabel() {
    return context.getCurrentComponentLabel();
  }

  /**
   * Return the component id
   */
  public String getComponentId() {
    return context.getCurrentComponentId();
  }

  /**
   * return the component Url : For Old Components' use ONLY ! (use it in the
   * jsp:forward lines)
   */
  public String getComponentUrl() {
    return URLManager.getURL(rootName, getSpaceId(), getComponentId());
  }

  /**
   * return the component Root name : i.e. 'agenda', 'todo', 'kmelia', .... (the
   * name that appears in the URL's root (the 'R' prefix is added later when
   * needed))
   */
  public String getComponentRootName() {
    return rootName;
  }

  /**
   * set the component Root name : i.e. 'agenda', 'todo', 'kmelia', .... (the
   * name that appears in the URL's root (the 'R' prefix is added later when
   * needed)) this function is called by the class of non-instanciable
   * components the inherits from this class
   */
  protected void setComponentRootName(String newRootName) {
    rootName = newRootName;
  }

  /**
   * Return the parameters for this component instance
   */
  @Override
  public List getComponentParameters() {
    return getMainSessionController().getComponentParameters(getComponentId());
  }

  /**
   * Return the parameter value for this component instance and the given
   * parameter name
   */
  @Override
  public String getComponentParameterValue(String parameterName) {
    return getMainSessionController().getComponentParameterValue(
        getComponentId(), parameterName);
  }

  /**
   * Return the user's available components Ids list
   */
  @Override
  public String[] getUserAvailComponentIds() {
    return getMainSessionController().getUserAvailComponentIds();
  }

  /**
   * Return the user's available space Ids list
   */
  @Override
  public String[] getUserAvailSpaceIds() {
    return getMainSessionController().getUserAvailSpaceIds();
  }

  public String[] getUserManageableSpaceIds() {
    return getMainSessionController().getUserManageableSpaceIds();
  }

  public List getUserManageableGroupIds() {
    return getMainSessionController().getUserManageableGroupIds();
  }

  public boolean isGroupManager() {
    return getUserManageableGroupIds().size() > 0;
  }

  /**
   * Return the name of the user's roles
   */
  @Override
  public String[] getUserRoles() {
    return context.getCurrentProfile();
  }

  /**
   * Return the highest user's role (admin, publisher or user)
   */
  @Override
  public String getUserRoleLevel() {
    String[] profiles = getUserRoles();
    String flag = SilverpeasRole.user.toString();

    for (int i = 0; i < profiles.length; i++) {
      // if admin, return it, we won't find a better profile
      if (SilverpeasRole.admin == SilverpeasRole.valueOf(profiles[i])) {
        return profiles[i];
      }
      if (SilverpeasRole.publisher == SilverpeasRole.valueOf(profiles[i])) {
        flag = profiles[i];
      }
    }
    return flag;
  }


  @Override
  public synchronized PersonalizationBm getPersonalization() {
    return getMainSessionController().getPersonalization();
  }

  public void initPersonalization() {
    getMainSessionController().initPersonalization();
  }

  public String getUserAccessLevel() {
    return getMainSessionController().getUserAccessLevel();
  }

  public void setGenericPanel(String panelKey, GenericPanel panel) {
    getMainSessionController().setGenericPanel(panelKey, panel);
  }

  public GenericPanel getGenericPanel(String panelKey) {
    return getMainSessionController().getGenericPanel(panelKey);
  }

  public Selection getSelection() {
    return getMainSessionController().getSelection();
  }

  public AlertUser getAlertUser() {
    return getMainSessionController().getAlertUser();
  }

  // Maintenance Mode
  @Override
  public boolean isAppInMaintenance() {
    return getMainSessionController().isAppInMaintenance();
  }

  @Override
  public void setAppModeMaintenance(boolean mode) {
    getMainSessionController().setAppModeMaintenance(mode);
  }

  @Override
  public boolean isSpaceInMaintenance(String spaceId) {
    return getMainSessionController().isSpaceInMaintenance(spaceId);
  }

  @Override
  public void setSpaceModeMaintenance(String spaceId, boolean mode) {
    getMainSessionController().setSpaceModeMaintenance(spaceId, mode);
  }

  public String getServerNameAndPort() {
    return getMainSessionController().getServerNameAndPort();
  }

  public List getLastResults() {
    return getMainSessionController().getLastResults();
  }

  public void setLastResults(List results) {
    getMainSessionController().setLastResults(results);
  }

  public void close() {
  }

  public boolean isPasswordChangeAllowed() {
    return controller.isAllowPasswordChange();
  }

  public String getRSSUrl() {
    return "/rss"
        + getComponentRootName()
        + "/"
        + getComponentId()
        + "?userId="
        + getUserId()
        + "&login="
        + URLEncoder.encode(getUserDetail().getLogin())
        + "&password="
        + URLEncoder.encode(controller.getOrganizationController().getUserFull(
        getUserId()).getPassword());
  }

  @Override
  public Collection getClipboardSelectedObjects() throws RemoteException {
    return controller.getSelectedObjects();
  }

  @Override
  public String getClipboardErrorMessage() throws RemoteException {
    return controller.getMessageError();
  }

  @Override
  public Exception getClipboardExceptionError() throws RemoteException {
    return controller.getExceptionError();
  }

  @Override
  public Collection getClipboardObjects() throws RemoteException {
    return controller.getObjects();
  }

  @Override
  public void addClipboardSelection(ClipboardSelection selection) throws RemoteException {
    controller.add(selection);
  }

  @Override
  public String getClipboardName() throws RemoteException {
    return controller.getName();
  }

  @Override
  public Integer getClipboardCount() throws RemoteException {
    return controller.getCount();
  }

  @Override
  public void clipboardPasteDone() throws RemoteException {
    controller.PasteDone();
  }

  @Override
  public void setClipboardSelectedElement(int index, boolean selected) throws RemoteException {
    controller.setSelected(index, selected);
  }

  @Override
   public int getClipboardSize() throws RemoteException {
    return controller.size();
  }

  @Override
   public void removeClipboardElement(int index) throws RemoteException {
    controller.remove(index);
  }

  @Override
   public void setClipboardError(String messageId, Exception ex) throws RemoteException {
    controller.setMessageError(messageId, ex);
  }
}
