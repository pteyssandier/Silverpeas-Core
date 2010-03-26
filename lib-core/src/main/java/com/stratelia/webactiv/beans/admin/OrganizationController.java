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
 * @author Norbert CHAIX
 * @version 1.0
 * date 14/09/2000
 */
package com.stratelia.webactiv.beans.admin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.instance.control.WAComponent;

/**
 * This objet is used by all the admin jsp such as SpaceManagement, UserManagement, etc... It
 * provides access functions to query and modify the domains as well as the company organization It
 * should be used only by a client that has the administrator rights
 * @author
 */
public class OrganizationController extends AdminReference implements java.io.Serializable {

  private static final long serialVersionUID = 1869750368600972095L;

  /**
   * Constructor declaration
   */
  public OrganizationController() {
  }

  /**
   * Constructor declaration
   * @param userId
   * @deprecated
   */
  public OrganizationController(String userId) {
  }

  /**
   * Constructor declaration
   * @param auc
   * @deprecated
   */
  public OrganizationController(AdminUserConnections auc) {
  }

  // -------------------------------------------------------------------
  // SPACES QUERIES
  // -------------------------------------------------------------------

  /**
   * Return all the spaces Id available in silverpeas
   */
  public String[] getAllSpaceIds() {
    try {
      return m_Admin.getAllSpaceIds();
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSpaceIds",
          "admin.MSG_ERR_GET_ALL_SPACE_IDS", e);
      return new String[0];
    }
  }

  /**
   * Return all the subSpaces Id available in silverpeas given a space id (driver format)
   */
  public String[] getAllSubSpaceIds(String sSpaceId) {
    try {
      String[] asSubSpaceIds = m_Admin.getAllSubSpaceIds(sSpaceId);

      return asSubSpaceIds;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSubSpaceIds",
          "admin.MSG_ERR_GET_SUBSPACE_IDS", "father space id: '" + sSpaceId
          + "'", e);
      return new String[0];
    }
  }

  /**
   * Return the the spaces name corresponding to the given space ids
   */
  public String[] getSpaceNames(String[] asSpaceIds) {
    try {
      String[] asSpaceNames = m_Admin.getSpaceNames(asSpaceIds);

      return asSpaceNames;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getSpaceNames",
          "admin.MSG_ERR_GET_SPACE_NAMES", e);
      return new String[0];
    }
  }

  /**
   * Return the space light corresponding to the given space id
   */
  public SpaceInstLight getSpaceInstLightById(String spaceId) {
    try {
      SpaceInstLight spaceLight = m_Admin.getSpaceInstLightById(spaceId);

      return spaceLight;
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getSpaceInstLightById",
          "admin.MSG_ERR_GET_SPACE_BY_ID", "spaceId=" + spaceId, e);
      return null;
    }
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public String getGeneralSpaceId() {
    try {
      String sGeneralSpaceId = m_Admin.getGeneralSpaceId();

      return sGeneralSpaceId;
    } catch (Exception e) {
      SilverTrace.fatal("admin", "OrganizationController.getGeneralSpaceId",
          "admin.MSG_FATAL_GET_GENERAL_SPACE_ID", e);
      return "";
    }
  }

  /**
   * Return the space Instance corresponding to the given space id
   */
  public SpaceInst getSpaceInstById(String sSpaceId) {
    try {
      SpaceInst spaceInst = m_Admin.getSpaceInstById(sSpaceId);

      return spaceInst;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getSpaceInstById",
          "admin.MSG_ERR_GET_SPACE", "space Id: '" + sSpaceId + "'", e);
      return null;
    }
  }

  /**
   * Return the component ids available for the cuurent user Id in the given space id
   */
  public String[] getAvailCompoIds(String sClientSpaceId, String sUserId) {
    try {
      String[] asCompoIds = m_Admin.getAvailCompoIds(sClientSpaceId, sUserId);

      return asCompoIds;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAvailCompoIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "space Id: '"
          + sClientSpaceId + "', user Id: '" + sUserId + "'", e);
      return new String[0];
    }
  }

  /**
   * Return the component ids available for the cuurent user Id in the given space id
   */
  public String[] getAvailCompoIdsAtRoot(String sClientSpaceId, String sUserId) {
    try {
      String[] asCompoIds = m_Admin.getAvailCompoIdsAtRoot(sClientSpaceId,
          sUserId);

      return asCompoIds;
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getAvailCompoIdsAtRoot",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "space Id: '"
          + sClientSpaceId + "', user Id: '" + sUserId + "'", e);
      return new String[0];
    }
  }

  /** Return all the components of silverpeas read in the xmlComponent directory */
  public Hashtable<String, WAComponent> getAllComponents() {
    try {
      return m_Admin.getAllComponents();
    } catch (Exception e) {
      if (!(e instanceof AdminException && ((AdminException) e)
          .isAlreadyPrinted()))
        SilverTrace.error("admin",
            "OrganizationController.getAvailDriverCompoIds",
            "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", null, e);
      return new Hashtable<String, WAComponent>();
    }
  }

  /** Return all the components names available in webactiv */
  public Hashtable<String, String> getAllComponentsNames() {
    try {
      return m_Admin.getAllComponentsNames();
    } catch (Exception e) {
      if (!(e instanceof AdminException && ((AdminException) e)
          .isAlreadyPrinted()))
        SilverTrace.error("admin",
            "OrganizationController.getAvailDriverCompoIds",
            "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", null, e);
      return new Hashtable<String, String>();
    }
  }

  /**
   * Return the driver component ids available for the cuurent user Id in the given space id
   */
  public String[] getAvailDriverCompoIds(String sClientSpaceId, String sUserId) {
    try {
      String[] asCompoIds = m_Admin.getAvailDriverCompoIds(sClientSpaceId,
          sUserId);

      return asCompoIds;
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getAvailDriverCompoIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "space Id: '"
          + sClientSpaceId + "', user Id: '" + sUserId + "'", e);
      return new String[0];
    }
  }

  /**
   * Return the tuples (space id, compo id) allowed for the given user and given component name
   */
  public CompoSpace[] getCompoForUser(String sUserId, String sCompoName) {
    try {
      CompoSpace[] aCompoSpace = m_Admin.getCompoForUser(sUserId, sCompoName);

      return aCompoSpace;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getCompoForUser",
          "admin.MSG_ERR_GET_USER_AVAILABLE_INSTANCES_OF_COMPONENT",
          "user Id : '" + sUserId + "', component name: '" + sCompoName + "'",
          e);
      return new CompoSpace[0];
    }
  }

  public String[] getComponentIdsForUser(String sUserId, String sCompoName) {
    try {
      return m_Admin.getComponentIdsByNameAndUserId(sUserId, sCompoName);
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getComponentIdsForUser",
          "admin.MSG_ERR_GET_USER_AVAILABLE_INSTANCES_OF_COMPONENT",
          "user Id : '" + sUserId + "', component name: '" + sCompoName + "'",
          e);
      return new String[0];
    }
  }

  /**
   * Return the compo id for the given component name
   */
  public String[] getCompoId(String sCompoName) {
    try {
      String[] aCompoId = m_Admin.getCompoId(sCompoName);

      return aCompoId;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getCompoId",
          "admin.MSG_ERR_GET_AVAILABLE_INSTANCES_OF_COMPONENT",
          "component name: '" + sCompoName + "'", e);
      return new String[0];
    }
  }

  public String getComponentParameterValue(String sComponentId,
      String parameterName) {
    return m_Admin.getComponentParameterValue(sComponentId, parameterName);
  }

  // -------------------------------------------------------------------
  // COMPONENTS QUERIES
  // -------------------------------------------------------------------

  /**
   * Return the component Instance corresponding to the given component id
   */
  public ComponentInst getComponentInst(String sComponentId) {
    try {
      ComponentInst compoInst = m_Admin.getComponentInst(sComponentId);

      return compoInst;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getComponentInst",
          "admin.MSG_ERR_GET_COMPONENT", "component Id : '" + sComponentId
          + "'", e);
      return null;
    }
  }

  /**
   * @param spaceId - id of the space or subSpace
   * @return a List of SpaceInst ordered from root to subSpace
   * @throws Exception
   */
  public List<SpaceInst> getSpacePath(String spaceId) {
    return getSpacePath(new ArrayList<SpaceInst>(), spaceId);
  }

  public List<SpaceInst> getSpacePathToComponent(String componentId) {
    ComponentInstLight componentInstLight = getComponentInstLight(componentId);
    if (componentInstLight != null) {
      return getSpacePath(componentInstLight.getDomainFatherId());
    }
    return new ArrayList<SpaceInst>();
  }

  private List<SpaceInst> getSpacePath(List<SpaceInst> path, String spaceId) {
    try {
      SpaceInst spaceInst = m_Admin.getSpaceInstById(spaceId);
      path.add(0, spaceInst);
      if (!spaceInst.getDomainFatherId().equals("0")) {
        path = getSpacePath(path, spaceInst.getDomainFatherId());
      }
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getSpacePath",
          "admin.MSG_ERR_GET_SPACE", "spaceId = '" + spaceId + "'", e);
    }
    return path;
  }

  /**
   * Return the component Instance Light corresponding to the given component id
   */
  public ComponentInstLight getComponentInstLight(String sComponentId) {
    try {
      ComponentInstLight compoInst = m_Admin
          .getComponentInstLight(sComponentId);

      return compoInst;
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getComponentInstLight",
          "admin.MSG_ERR_GET_COMPONENT", "component Id : '" + sComponentId
          + "'", e);
      return null;
    }
  }

  // -------------------------------------------------------------------
  // USERS QUERIES
  // -------------------------------------------------------------------

  /**
   * Return the database id of the user with the given ldap Id
   */
  public int getUserDBId(String sUserId) {
    return idAsInt(sUserId);
  }

  /**
   * Return the ldapId of the user with the given db Id
   */
  public String getUserDetailByDBId(int id) {
    return idAsString(id);
  }

  /**
   * Return the UserDetail of the user with the given ldap Id
   */
  public UserFull getUserFull(String sUserId) {
    try {
      return m_Admin.getUserFull(sUserId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUserFull",
          "admin.EX_ERR_GET_USER_DETAIL", "user Id : '" + sUserId + "'", e);
      return null;
    }
  }

  /**
   * Return the UserDetail of the user with the given ldap Id
   */
  public UserDetail getUserDetail(String sUserId) {
    try {
      UserDetail userDetail = m_Admin.getUserDetail(sUserId);

      return userDetail;
    } catch (Exception e) {
      SilverTrace.warn("admin", "OrganizationController.getUserDetail",
          "admin.EX_ERR_GET_USER_DETAIL", "user Id : '" + sUserId + "'", e);
      return null;
    }
  }

  /**
   * Return an array of UserDetail corresponding to the given user Id array
   */
  public UserDetail[] getUserDetails(String[] asUserIds) {
    try {
      UserDetail[] asUserDetails = m_Admin.getUserDetails(asUserIds);

      return asUserDetails;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUserDetails",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return null;
    }
  }

  /**
   * @deprecated use getAllUsers(String componentId) Return all the users allowed to acces the given
   * component of the given space
   */
  public UserDetail[] getAllUsers(String sPrefixTableName, String sComponentName) {
    try {
      UserDetail[] aUserDetail = null;

      if (sComponentName != null) {
        aUserDetail = m_Admin.getUsers(true, null, sPrefixTableName,
            sComponentName);
      }
      return aUserDetail;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllUsers",
          "admin.MSG_ERR_GET_USERS_FOR_PROFILE_AND_COMPONENT", "space Id: '"
          + sPrefixTableName + "' component Id: '" + sComponentName, e);
      return null;
    }
  }

  /**
   * Return all the users allowed to acces the given component
   */
  public UserDetail[] getAllUsers(String componentId) {
    try {
      UserDetail[] aUserDetail = null;

      if (componentId != null) {
        aUserDetail = m_Admin.getUsers(true, null, null, componentId);
      }
      return aUserDetail;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllUsers",
          "admin.MSG_ERR_GET_USERS_FOR_PROFILE_AND_COMPONENT", "componentId: '"
          + componentId, e);
      return null;
    }
  }

  /**
   * For use in userPanel : return the users that are direct child of a given group
   */
  public UserDetail[] getFiltredDirectUsers(String sGroupId,
      String sUserLastNameFilter) {
    try {
      return m_Admin.getFiltredDirectUsers(sGroupId, sUserLastNameFilter);
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getFiltredDirectUsers",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return null;
    }
  }

  /**
   * Return an array of UserDetail corresponding to the founded users
   */
  public UserDetail[] searchUsers(UserDetail modelUser, boolean isAnd) {
    try {
      return m_Admin.searchUsers(modelUser, isAnd);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.searchUsers",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return null;
    }
  }

  /**
   * Return an array of Group corresponding to the founded groups
   */
  public Group[] searchGroups(Group modelGroup, boolean isAnd) {
    try {
      return m_Admin.searchGroups(modelGroup, isAnd);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.searchGroups",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return null;
    }
  }

  /**
   * For use in userPanel : return the total number of users recursivly contained in a group
   */
  public int getAllSubUsersNumber(String sGroupId) {
    try {
      return m_Admin.getAllSubUsersNumber(sGroupId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSubUsersNumber",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return 0;
    }
  }

  /**
   * For use in userPanel : return the direct sub-groups
   */
  public Group[] getAllSubGroups(String parentGroupId) {
    try {
      return m_Admin.getAllSubGroups(parentGroupId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSubGroups",
          "admin.EX_ERR_GET_USER_DETAILS", e);
      return new Group[0];
    }
  }

  /**
   * Return all the users of Silverpeas
   */
  public UserDetail[] getAllUsers() {
    try {
      UserDetail[] aUserDetail = null;
      String[] asUserIds = m_Admin.getAllUsersIds();

      if (asUserIds != null) {
        aUserDetail = m_Admin.getUserDetails(asUserIds);

      }
      return aUserDetail;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllUsers",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  /**
   * Return all the users with the given profile allowed to access the given component of the given
   * space
   */
  public UserDetail[] getUsers(String sPrefixTableName, String sComponentName,
      String sProfile) {
    try {
      UserDetail[] aUserDetail = null;

      if (sPrefixTableName != null && sComponentName != null) {
        aUserDetail = m_Admin.getUsers(false, sProfile, sPrefixTableName,
            sComponentName);

      }
      return aUserDetail;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUsers",
          "admin.MSG_ERR_GET_USERS_FOR_PROFILE_AND_COMPONENT", "profile: '"
          + sProfile + "', space Id: '" + sPrefixTableName
          + "' component Id: '" + sComponentName, e);
      return null;
    }
  }

  public String[] getUserProfiles(String userId, String componentId) {
    try {
      return m_Admin.getCurrentProfiles(userId, getComponentInst(componentId));
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUserProfiles",
          "admin.MSG_ERR_GET_PROFILES_FOR_USER_AND_COMPONENT", "userId: '"
          + userId + "', componentId: '" + componentId + "'", e);
      return null;
    }
  }

  public String[] getUserProfiles(String userId, String componentId,
      int objectId, String objectType) {
    try {
      return m_Admin.getProfilesByObjectAndUserId(objectId, objectType,
          componentId, userId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUserProfiles",
          "admin.MSG_ERR_GET_PROFILES_FOR_USER_AND_COMPONENT", "userId = "
          + userId + ", componentId = " + componentId + ", objectId = "
          + objectId, e);
      return null;
    }
  }

  public Hashtable<String, String> getUsersLanguage(List<String> userIds) {
    Hashtable<String, String> usersLanguage = null;
    try {
      usersLanguage = m_Admin.getUsersLanguage(userIds);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getUsersLanguage",
          "admin.MSG_ERR_GET_LANGUAGES", e);
    }
    return usersLanguage;
  }

  /**
   * Return all administrators ids
   */
  public String[] getAdministratorUserIds(String fromUserId) {
    try {
      return m_Admin.getAdministratorUserIds(fromUserId);
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getAdministratorUserIds",
          "admin.MSG_ERR_GET_ALL_ADMIN_IDS", e);
      return new String[0];
    }
  }

  // -------------------------------------------------------------------
  // GROUPS QUERIES
  // -------------------------------------------------------------------

  /**
   * Return the Group of the group with the given Id
   */
  public Group getGroup(String sGroupId) {
    try {
      Group group = m_Admin.getGroup(sGroupId);

      return group;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getGroup",
          "admin.EX_ERR_GET_GROUP", "group Id : '" + sGroupId + "'", e);
      return null;
    }
  }

  /**
   * Return all groups specified by the groupsIds
   */
  public Group[] getGroups(String[] groupsId) {
    Group[] retour = null;
    try {
      retour = m_Admin.getGroups(groupsId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getGroups",
          "admin.EX_ERR_GET_GROUP", "", e);
    }
    return retour;
  }

  /**
   * Return all the groups of silverpeas
   */
  public Group[] getAllGroups() {
    try {
      Group[] aGroup = null;
      String[] asGroupIds = m_Admin.getAllGroupIds();

      if (asGroupIds != null) {
        aGroup = m_Admin.getGroups(asGroupIds);

      }
      return aGroup;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllGroups",
          "admin.MSG_ERR_GET_ALL_GROUPS", e);
      return null;
    }
  }

  /**
   * Return all root groups of silverpeas
   */
  public Group[] getAllRootGroups() {
    try {
      Group[] aGroup = null;
      String[] asGroupIds = m_Admin.getAllRootGroupIds();
      if (asGroupIds != null) {
        aGroup = m_Admin.getGroups(asGroupIds);
      }
      return aGroup;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllRootGroups",
          "admin.MSG_ERR_GET_ALL_GROUPS", e);
      return null;
    }
  }

  /**
   * Get ALL the users that are in a group or his sub groups
   */
  public UserDetail[] getAllUsersOfGroup(String groupId) {
    SilverTrace.info("admin", "OrganizationController.getAllUsersOfGroup",
        "root.MSG_GEN_ENTER_METHOD", "groupId = " + groupId);
    try {
      return m_Admin.getAllUsersOfGroup(groupId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllUsersOfGroup",
          "admin.MSG_ERR_GET_ALL_DOMAINS", e);
      return new UserDetail[0];
    }
  }

  /**
   * Convert String Id to int Id
   */
  private int idAsInt(String id) {
    if (id == null || id.length() == 0) {
      return -1; // the null id.

    }
    try {
      return Integer.parseInt(id);
    } catch (NumberFormatException e) {
      return -1; // the null id.
    }
  }

  /**
   * Convert int Id to String Id
   */
  static private String idAsString(int id) {
    return Integer.toString(id);
  }

  // -------------------------------------------------------------------
  // RE-INDEXATION
  // -------------------------------------------------------------------
  public String[] getAllSpaceIds(String sUserId) {
    try {
      return m_Admin.getAllSpaceIds(sUserId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSpaceIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACE_IDS", "user Id: '" + sUserId
          + "'", e);
      return new String[0];
    }
  }

  /**
   * Return all the spaces Id manageable by given user in Silverpeas
   */
  public String[] getUserManageableSpaceIds(String sUserId) {
    try {
      String[] asSpaceIds = m_Admin.getUserManageableSpaceIds(sUserId);

      return asSpaceIds;
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getUserManageableSpaceIds",
          "admin.MSG_ERR_GET_USER_MANAGEABLE_SPACE_IDS", "user Id: '" + sUserId
          + "'", e);
      return new String[0];
    }
  }

  /**
   * Return all the root spaceIds
   */
  public String[] getAllRootSpaceIds() {
    try {
      return m_Admin.getAllRootSpaceIds();
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSpaceIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACE_IDS", e);
      return new String[0];
    }
  }

  /**
   * Return all the root spaceIds available for the user sUserId
   */
  public String[] getAllRootSpaceIds(String sUserId) {
    try {
      return m_Admin.getAllRootSpaceIds(sUserId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSpaceIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACE_IDS", "user Id: '" + sUserId
          + "'", e);
      return new String[0];
    }
  }

  /**
   * Return all the subSpaces Id available in webactiv given a space id (driver format)
   */
  public String[] getAllSubSpaceIds(String sSpaceId, String sUserId) {
    try {
      String[] asSubSpaceIds = m_Admin.getAllSubSpaceIds(sSpaceId, sUserId);

      return asSubSpaceIds;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllSubSpaceIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SUBSPACE_IDS", "user Id: '"
          + sUserId + "', father space id: '" + sSpaceId + "'", e);
      return new String[0];
    }
  }

  /**
   * Return all the components Id available in webactiv given a space id (driver format)
   */
  public String[] getAllComponentIds(String sSpaceId) {
    try {
      return m_Admin.getAllComponentIds(sSpaceId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllComponentIds",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "space id="
          + sSpaceId, e);
      return new String[0];
    }
  }

  /**
   * Return all the components Id recursively available in webactiv given a space id (driver format)
   */
  public String[] getAllComponentIdsRecur(String sSpaceId) {
    try {
      return m_Admin.getAllComponentIdsRecur(sSpaceId);
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getAllComponentIdsRecur",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "spaceId = "
          + sSpaceId, e);
      return new String[0];
    }
  }

  /**
   * Return all the components Id recursively in (Space+subspaces, or only subspaces or all
   * silverpeas) available in silverpeas given a userId and a componentNameRoot
   * @author dlesimple
   * @param sSpaceId
   * @param sUserId
   * @param sComponentNameRoot
   * @param inCurrentSpace
   * @param inAllSpaces
   * @return Array of componentsIds
   */
  public String[] getAllComponentIdsRecur(String sSpaceId, String sUserId,
      String sComponentRootName, boolean inCurrentSpace, boolean inAllSpaces) {
    try {
      String[] asComponentsIds = m_Admin.getAllComponentIdsRecur(sSpaceId,
          sUserId, sComponentRootName, inCurrentSpace, inAllSpaces);
      return asComponentsIds;
    } catch (Exception e) {
      return new String[0];
    }
  }

  public boolean isComponentAvailable(String componentId, String userId) {
    try {
      return m_Admin.isComponentAvailable(componentId, userId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.isComponentAvailable",
          "admin.MSG_ERR_GET_USER_AVAILABLE_COMPONENT_IDS", "user Id: '"
          + userId + "', componentId: '" + componentId + "'", e);
      return false;
    }
  }

  public boolean isSpaceAvailable(String spaceId, String userId) {
    try {
      String[] spaceIds = getAllSpaceIds(userId);
      for (int s = 0; s < spaceIds.length; s++) {
        if (spaceIds[s].equals(spaceId))
          return true;
      }
      return false;
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.isSpaceAvailable",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACE_IDS", "user Id: '" + userId
          + "', spaceId: '" + spaceId + "'", e);
      return false;
    }
  }

  public boolean isObjectAvailable(int objectId, String objectType,
      String componentId, String userId) {
    try {
      return m_Admin.isObjectAvailable(componentId, objectId, objectType,
          userId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.isObjectAvailable",
          "admin.MSG_ERR_GET_USER_AVAILABLE_OBJECT", "userId = " + userId
          + ", componentId = " + componentId + ", objectId = " + objectId,
          e);
      return false;
    }
  }

  public List<SpaceInstLight> getSpaceTreeview(String userId) {
    try {
      return m_Admin.getUserSpaceTreeview(userId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getSpaceTreeview",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACES", "user Id = " + userId, e);
      return new ArrayList<SpaceInstLight>();
    }
  }

  public String[] getAllowedSubSpaceIds(String userId, String spaceFatherId) {
    try {
      return m_Admin.getAllowedSubSpaceIds(userId, spaceFatherId);
    } catch (AdminException e) {
      SilverTrace.error("admin", "OrganizationController.getSpaceTreeview",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SUBSPACE_IDS", "user Id = " + userId + ", spaceId = " +
          spaceFatherId, e);
      return new String[0];
    }
  }

  public SpaceInstLight getRootSpace(String spaceId) {
    try {
      return m_Admin.getRootSpace(spaceId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getRootSpace",
          "admin.MSG_ERR_GET_USER_AVAILABLE_SPACE", "spaceId = " + spaceId, e);
      return null;
    }
  }

  // -------------------------------------------------------------------------
  // For SelectionPeas
  // -------------------------------------------------------------------------

  /**
   * Return all the users of Silverpeas
   */
  public String[] getAllUsersIds() {
    try {
      return m_Admin.getAllUsersIds();
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllUsersIds",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  /**
   * Return all the users of Silverpeas
   */
  public String[] searchUsersIds(String groupId, String componentId,
      String[] profileId, UserDetail filterUser) {
    try {
      return m_Admin
          .searchUsersIds(groupId, componentId, profileId, filterUser);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.searchUsersIds",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  /**
   * Return userIds according to a list of profile names
   * @param componentId the instance id
   * @param profileNames the list which contains the profile names
   * @return a string array of user id
   */
  public String[] getUsersIdsByRoleNames(String componentId, List<String> profileNames) {
    try {
      ComponentInst componentInst = getComponentInst(componentId);

      List<ProfileInst> profiles = componentInst.getAllProfilesInst();
      ProfileInst profileInst = null;
      List<String> profileIds = new ArrayList<String>();
      for (int p = 0; p < profiles.size(); p++) {
        profileInst = profiles.get(p);
        if (profileNames.contains(profileInst.getName())) {
          profileIds.add(profileInst.getId());
          SilverTrace.info("admin",
              "OrganizationController.getUsersIdsByRoleNames",
              "root.MSG_GEN_PARAM_VALUE", "profileName = "
              + profileInst.getName() + ", profileId = "
              + profileInst.getId());
        }
      }

      if (!profileIds.isEmpty()) {
        String[] pIds = (String[]) profileIds.toArray(new String[profileIds
            .size()]);
        SilverTrace.info("admin",
            "OrganizationController.getUsersIdsByRoleNames",
            "root.MSG_GEN_PARAM_VALUE", "pIds = " + pIds);

        return m_Admin.searchUsersIds(null, null, pIds, new UserDetail());
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getUsersIdsByRoleNames",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  public String[] getUsersIdsByRoleNames(String componentId, String objectId,
      String objectType, List<String> profileNames) {
    SilverTrace.info("admin", "OrganizationController.getUsersIdsByRoleNames",
        "root.MSG_GEN_ENTER_METHOD", "componentId = " + componentId
        + ", objectId = " + objectId);
    try {
      List<ProfileInst> profiles = m_Admin.getProfilesByObject(objectId, objectType,
          componentId);

      ProfileInst profile = null;
      List<String> profileIds = new ArrayList<String>();
      for (int i = 0; i < profiles.size(); i++) {
        profile = profiles.get(i);
        if (profile != null && profileNames.contains(profile.getName())) {
          profileIds.add(profile.getId());
        }
      }

      SilverTrace.info("admin",
          "OrganizationController.getUsersIdsByRoleNames",
          "root.MSG_GEN_PARAM_VALUE", "profileIds = " + profileIds.toString());

      if (profileIds.isEmpty())
        return new String[0]; // else return all users !!

      return m_Admin.searchUsersIds(null, null, (String[]) profileIds
          .toArray(new String[profileIds.size()]), new UserDetail());
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getUsersIdsByRoleNames",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  public String[] searchGroupsIds(boolean isRootGroup, String componentId,
      String[] profileId, Group modelGroup) {
    try {
      return m_Admin.searchGroupsIds(isRootGroup, componentId, profileId,
          modelGroup);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.searchGroupsIds",
          "admin.MSG_ERR_GET_ALL_USERS", e);
      return null;
    }
  }

  /**
   * Get a domain with given id
   */
  public Domain getDomain(String domainId) {
    try {
      return m_Admin.getDomain(domainId);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getDomain",
          "admin.EX_ERR_GET_DOMAIN", e);
      return null;
    }
  }

  /**
   * Get all domains
   */
  public Domain[] getAllDomains() throws AdminException {
    try {
      return m_Admin.getAllDomains();
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllDomain",
          "admin.EX_ERR_GET_DOMAIN", e);
      e.printStackTrace();
      return null;
    }
  }

  public String[] getDirectGroupIdsOfUser(String userId) {
    try {
      return m_Admin.getDirectGroupsIdsOfUser(userId);
    } catch (Exception e) {
      SilverTrace.error("admin",
          "OrganizationController.getDirectGroupIdsOfUser",
          "admin.MSG_ERR_GET_ALL_GROUPS", e);
      return new String[0];
    }

  }

  private ArrayList<String> recursiveMajListGroupId(String idGroup, ArrayList<String> listRes) {
    Group group = getGroup(idGroup);
    if (group.getSuperGroupId() != null) {
      listRes = recursiveMajListGroupId(group.getSuperGroupId(), listRes);
    }
    listRes.add(idGroup);
    return listRes;
  }

  public String[] getAllGroupIdsOfUser(String userId) {
    try {
      ArrayList<String> listRes = new ArrayList<String>();
      String[] tabGroupIds = m_Admin.getDirectGroupsIdsOfUser(userId);
      String groupId;
      for (int i = 0; i < tabGroupIds.length; i++) {
        groupId = tabGroupIds[i];
        listRes = recursiveMajListGroupId(groupId, listRes);
      }
      return (String[]) listRes.toArray(new String[listRes.size()]);
    } catch (Exception e) {
      SilverTrace.error("admin", "OrganizationController.getAllGroupIdsOfUser",
          "admin.MSG_ERR_GET_ALL_GROUPS", e);
      return new String[0];
    }
  }
}
