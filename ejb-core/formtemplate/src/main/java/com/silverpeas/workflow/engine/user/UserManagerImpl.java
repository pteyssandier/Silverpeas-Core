package com.silverpeas.workflow.engine.user;

import java.util.Hashtable;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;

import com.silverpeas.workflow.api.UserManager;
import com.silverpeas.workflow.api.WorkflowException;
import com.silverpeas.workflow.api.user.User;
import com.silverpeas.workflow.api.user.UserInfo;
import com.silverpeas.workflow.api.user.UserSettings;
import com.silverpeas.workflow.engine.exception.UnknownUserException;
import com.silverpeas.workflow.engine.jdo.WorkflowJDOManager;
import com.stratelia.webactiv.beans.admin.Admin;
import com.stratelia.webactiv.beans.admin.AdminException;
import com.stratelia.webactiv.beans.admin.ComponentInst;
import com.stratelia.webactiv.beans.admin.OrganizationController;
import com.stratelia.webactiv.beans.admin.UserDetail;

/**
 * A UserManager implementation built upon the silverpeas user management
 * system.
 */
public class UserManagerImpl implements UserManager {
  /**
   * The UserManagerImpl shares a silverpeas OrganisationController
   */
  static private OrganizationController organizationController = null;

  /**
   * The UserManagerImpl shares a silverpeas Admin object
   */
  static private Admin admin = null;

  static private Hashtable userSettings = null;

  /**
   * The constructor builds and set the shared OrganisationController.
   */
  public UserManagerImpl() {
    synchronized (UserManagerImpl.class) {
      if (organizationController == null) {
        organizationController = new OrganizationController();
      }
      if (admin == null) {
        admin = new Admin();
      }
      if (userSettings == null) {
        userSettings = new Hashtable();
      }

    }
  }

  /**
   * Returns the user with the given userId
   * 
   * @return the user with the given userId.
   * @throw WorkflowException if the userId is unknown.
   */
  public User getUser(String userId) throws WorkflowException {
    return new UserImpl(getUserDetail(userId), admin);
  }

  /**
   * Make a User[] from a userIds' String[].
   * 
   * @throw WorkflowException if a userId is unknown.
   */
  public User[] getUsers(String[] userIds) throws WorkflowException {
    User[] users = new User[userIds.length];
    for (int i = 0; i < userIds.length; i++) {
      users[i] = getUser(userIds[i]);
    }
    return users;
  }

  /**
   * Returns all the roles of a given user relative to a processModel.
   */
  public String[] getRoleNames(User user, String modelId)
      throws WorkflowException {
    String roleNames[] = null;
    try {
      // the modelId is the peasId.
      ComponentInst peas = admin.getComponentInst(modelId);
      roleNames = admin.getCurrentProfiles(user.getUserId(), peas);
    } catch (AdminException e) {
      throw new WorkflowException("UserManagerImpl.getRoleNames",
          "workflowEngine.EXP_UNKNOWN_ROLE", e);
    }

    if (roleNames == null)
      roleNames = new String[0];
    return roleNames;
  }

  /**
   * Returns all the users having a given role relative to a processModel.
   */
  public User[] getUsersInRole(String roleName, String modelId)
      throws WorkflowException {
    UserDetail[] userDetails = null;
    try {
      // the modelId is the peasId.
      ComponentInst peas = admin.getComponentInst(modelId);
      userDetails = organizationController.getUsers(peas.getDomainFatherId(),
          modelId, roleName);
    } catch (AdminException e) {
      throw new WorkflowException("UserManagerImpl.getUserInRole",
          "workflowEngine.EXP_UNKNOWN_ROLE", e);
    }

    if (userDetails == null)
      userDetails = new UserDetail[0];
    return getUsers(userDetails);
  }

  /**
   * returns all the known info for an user; Each returned value can be used as
   * a parameter to the User method getInfo().
   */
  public String[] getUserInfoNames() {
    return UserImpl.getUserInfoNames();
  }

  /**
   * returns the userDetail of a userId.
   */
  private UserDetail getUserDetail(String userId) throws WorkflowException {
    UserDetail userDetail = organizationController.getUserDetail(userId);
    if (userDetail == null) {
      throw new UnknownUserException("UserManagerImpl.getUserDetail", userId);
    }
    return userDetail;
  }

  /**
   * Make a User[] from a UserDetail[].
   */
  private User[] getUsers(UserDetail[] userDetails) {
    User[] users = new User[userDetails.length];
    for (int i = 0; i < userDetails.length; i++) {
      users[i] = new UserImpl(userDetails[i], admin);
    }
    return users;
  }

  /**
   * Get a user from a given user and relation
   * 
   * @param user
   *          reference user
   * @param relation
   *          relation between given user and searched user
   * @param peasId
   *          the id of workflow peas associated to that information
   * @return the user that has the given relation with given user
   */
  public User getRelatedUser(User user, String relation, String peasId)
      throws WorkflowException {
    UserSettings settings = this.getUserSettings(user.getUserId(), peasId);
    if (settings == null)
      throw new WorkflowException("UserManagerImpl.getRelatedUser",
          "workflowEngine.EXP_NO_USER_SETTING", "user id : " + user.getUserId());

    UserInfo info = settings.getUserInfo(relation);
    if (info == null)
      throw new WorkflowException("UserManagerImpl.getRelatedUser",
          "workflowEngine.EXP_USERINFO_NOT_FOUND", "user id : "
              + user.getUserId() + ", info name : " + relation);

    return getUser(info.getValue());
  }

  /**
   * Get the user settings in database The full list of information is described
   * in the process model
   * 
   * @param userId
   *          the user Id
   * @param peasId
   *          the id of workflow peas associated to that information
   * @return UserSettings
   * @see ProcessModel
   */
  public UserSettings getUserSettings(String userId, String peasId)
      throws WorkflowException {
    Database db = null;
    OQLQuery query = null;
    QueryResults results;
    UserSettings settings = null;

    settings = (UserSettings) userSettings.get(userId + "_" + peasId);
    if (settings == null) {
      try {
        // Constructs the query
        db = WorkflowJDOManager.getDatabase(true);
        db.begin();

        query = db
            .getOQLQuery("SELECT settings FROM com.silverpeas.workflow.engine.user.UserSettingsImpl settings WHERE userId = $1 AND peasId = $2");

        // Execute the query
        query.bind(userId);
        query.bind(peasId);
        results = query.execute();

        if (results.hasMore()) {
          settings = (UserSettings) results.next();
        }

        db.commit();

        if (settings == null) {
          settings = getEmptyUserSettings(userId, peasId);
        }

        userSettings.put(userId + "_" + peasId, settings);
      } catch (PersistenceException pe) {
        throw new WorkflowException("UserManagerImpl.getUserSettings",
            "EX_ERR_CASTOR_GET_USER_SETTINGS", pe);
      } finally {
        WorkflowJDOManager.closeDatabase(db);
      }
    }
    return settings;
  }

  public void resetUserSettings(String userId, String peasId)
      throws WorkflowException {
    userSettings.remove(userId + "_" + peasId);
  }

  /**
   * Get an empty user settings in database The full list of information is
   * described in the process model
   * 
   * @param userId
   *          the user Id
   * @param peasId
   *          the id of workflow peas associated to that information
   * @return UserSettings
   * @see ProcessModel
   */
  public UserSettings getEmptyUserSettings(String userId, String peasId) {
    return new UserSettingsImpl(userId, peasId);
  }

}