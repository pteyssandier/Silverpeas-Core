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

package com.stratelia.webactiv.organization;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.stratelia.silverpeas.domains.ldapdriver.LDAPUtility;
import com.stratelia.silverpeas.silverpeasinitialize.CallBackManager;
import com.stratelia.webactiv.beans.admin.SynchroReport;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.util.exception.SilverpeasException;

/**
 * A UserTable object manages the ST_User table.
 */
public class UserTable extends Table {
  public UserTable(OrganizationSchema schema) {
    super(schema, "ST_User");
    this.organization = schema;
  }

  static final private String USER_COLUMNS =
      "id,specificId,domainId,login,firstName,lastName,loginMail,email,accessLevel,loginQuestion,loginAnswer";

  /**
   * Fetch the current user row from a resultSet.
   */
  protected UserRow fetchUser(ResultSet rs) throws SQLException {
    UserRow u = new UserRow();

    u.id = rs.getInt(1);
    u.specificId = rs.getString(2);
    u.domainId = rs.getInt(3);
    u.login = rs.getString(4);
    u.firstName = rs.getString(5);
    u.lastName = rs.getString(6);
    u.loginMail = rs.getString(7);
    u.eMail = rs.getString(8);
    u.accessLevel = rs.getString(9);
    u.loginQuestion = rs.getString(10);
    u.loginAnswer = rs.getString(11);

    return u;
  }

  public int getUserNumberOfDomain(int domainId)
      throws AdminPersistenceException {
    return getCount("ST_User", "id", "domainId = ? AND accessLevel <> ?",
        domainId, "R");
  }

  /**
   * Returns the User number
   */
  public int getUserNumber() throws AdminPersistenceException {
    return getCount("ST_User", "id", "accessLevel <> ?", "R");
  }

  /**
   * Returns the User whith the given id.
   */
  public UserRow getUser(int id) throws AdminPersistenceException {
    return (UserRow) getUniqueRow(SELECT_USER_BY_ID, id);
  }

  static final private String SELECT_USER_BY_ID = "select " + USER_COLUMNS
      + " from ST_User where id = ?";

  /**
   * Returns the User with the given specificId and login.
   */
  public UserRow getUserBySpecificId(int domainId, String specificId)
      throws AdminPersistenceException {
    UserRow[] users = (UserRow[]) getRows(SELECT_USER_BY_SPECIFICID_AND_LOGIN,
        new int[] { domainId }, new String[] { specificId }).toArray(
        new UserRow[0]);

    if (users.length == 0)
      return null;
    else if (users.length == 1)
      return users[0];
    else {
      throw new AdminPersistenceException("Usertable.getUserBySpecificId",
          SilverpeasException.ERROR, "admin.EX_ERR_LOGIN_FOUND_TWICE",
          "domain id : '" + domainId + "', user specific Id: '" + specificId
          + "'");
    }
  }

  static final private String SELECT_USER_BY_SPECIFICID_AND_LOGIN = "select "
      + USER_COLUMNS + " from ST_User where domainId = ? and specificId = ?";

  /**
   * Returns the User with the given specificId and login.
   */
  public UserRow[] getUsersBySpecificIds(int domainId, List<String> specificIds)
      throws AdminPersistenceException {
    if (specificIds == null || specificIds.size() == 0)
      return null;

    StringBuffer clauseIN = new StringBuffer("(");
    String specificId = null;
    for (int s = 0; s < specificIds.size(); s++) {
      if (s != 0)
        clauseIN.append(", ");
      specificId = specificIds.get(s);
      clauseIN.append("'").append(
          LDAPUtility.dblBackSlashesForDNInFilters(specificId)).append("'");
    }
    clauseIN.append(")");
    String query = SELECT_USERS_BY_SPECIFICIDS + clauseIN;
    return (UserRow[]) getRows(query, domainId).toArray(new UserRow[0]);
  }

  static final private String SELECT_USERS_BY_SPECIFICIDS = "select "
      + USER_COLUMNS + " from ST_User where domainId = ? and specificId IN ";

  /**
   * Returns the User with the given domainId and login.
   */
  public UserRow getUserByLogin(int domainId, String login)
      throws AdminPersistenceException {
    UserRow[] users = (UserRow[]) getRows(SELECT_USER_BY_DOMAINID_AND_LOGIN,
        new int[] { domainId }, new String[] { login }).toArray(new UserRow[0]);

    SynchroReport.debug("UserTable.getUserByLogin()",
        "Vérification que le login" + login + " du domaine no " + domainId
        + " n'est pas présent dans la base, requête : "
        + SELECT_USER_BY_DOMAINID_AND_LOGIN, null);
    if (users.length == 0)
      return null;
    else if (users.length == 1)
      return users[0];
    else {
      throw new AdminPersistenceException("Usertable.getUserByLogin",
          SilverpeasException.ERROR, "admin.EX_ERR_LOGIN_FOUND_TWICE",
          "domain id : '" + domainId + "', user login: '" + login + "'");
    }
  }

  static final private String SELECT_USER_BY_DOMAINID_AND_LOGIN = "select "
      + USER_COLUMNS
      + " from ST_User where domainId = ? and lower(login) = lower(?)";

  /**
   * Returns all the Users.
   */
  public UserRow[] getAllUsers() throws AdminPersistenceException {
    return (UserRow[]) getRows(SELECT_ALL_USERS).toArray(new UserRow[0]);
  }

  static final private String SELECT_ALL_USERS = "select " + USER_COLUMNS
      + " from ST_User where accessLevel <> 'R' order by lastName";

  /**
   * Returns all the User ids.
   */
  public String[] getAllUserIds() throws AdminPersistenceException {
    return (String[]) getIds(SELECT_ALL_USER_IDS).toArray(new String[0]);
  }

  static final private String SELECT_ALL_USER_IDS =
      "select id from ST_User where accessLevel <> 'R' order by lastName";

  /**
   * Returns all the Admin ids.
   */
  public String[] getAllAdminIds(UserDetail fromUser)
      throws AdminPersistenceException {
    if (fromUser.isAccessAdmin() || fromUser.isAccessDomainManager())
      return (String[]) getIds(SELECT_ALL_ADMIN_IDS_TRUE)
          .toArray(new String[0]);
    else {
      String[] valret = (String[]) getIds(SELECT_ALL_ADMIN_IDS_DOMAIN,
          Integer.parseInt(fromUser.getDomainId())).toArray(new String[0]);
      if (valret.length <= 0) {
        valret = (String[]) getIds(SELECT_ALL_ADMIN_IDS_TRUE).toArray(
            new String[0]);
      }
      return valret;
    }
  }

  static final private String SELECT_ALL_ADMIN_IDS_TRUE =
      "select id from ST_User where accessLevel='A' order by lastName";

  static final private String SELECT_ALL_ADMIN_IDS_DOMAIN =
      "select id from ST_User where ((accessLevel='A') or (accessLevel='D')) and (domainId = ?) order by lastName";

  /**
   * Returns all the User ids.
   */
  public String[] getUserIdsByAccessLevel(String accessLevel)
      throws AdminPersistenceException {
    String[] params = new String[1];
    params[0] = accessLevel;

    return (String[]) getIds(SELECT_USER_IDS_BY_ACCESS_LEVEL, new int[0],
        params).toArray(new String[0]);
  }

  static final private String SELECT_USER_IDS_BY_ACCESS_LEVEL =
      "select id from ST_User where accessLevel=? order by lastName";

  /**
   * Returns all the User ids.
   */
  public String[] getUserIdsOfDomainByAccessLevel(int domainId,
      String accessLevel) throws AdminPersistenceException {
    String[] params = new String[1];
    params[0] = accessLevel;

    int[] domainIds = new int[1];
    domainIds[0] = domainId;

    return (String[]) getIds(SELECT_USER_IDS_BY_ACCESS_LEVEL_AND_DOMAIN,
        domainIds, params).toArray(new String[0]);
  }

  static final private String SELECT_USER_IDS_BY_ACCESS_LEVEL_AND_DOMAIN =
      "select id from ST_User where domainId = ? AND accessLevel=? order by lastName";

  /**
   * Returns all the Users which compose a group.
   */
  public UserRow[] getDirectUsersOfGroup(int groupId)
      throws AdminPersistenceException {
    return (UserRow[]) getRows(SELECT_USERS_IN_GROUP, groupId).toArray(
        new UserRow[0]);
  }

  static final private String SELECT_USERS_IN_GROUP = "select " + USER_COLUMNS
      + " from ST_User,ST_Group_User_Rel"
      + " where id = userId and groupId = ? and accessLevel <> 'R'"
      + " order by lastName";

  /**
   * Returns all the User ids which compose a group.
   */
  public String[] getDirectUserIdsOfGroup(int groupId)
      throws AdminPersistenceException {
    SynchroReport.debug("UserTable.getDirectUserIdsOfGroup()",
        "Recherche des utilisateurs inclus directement dans le groupe d'ID "
        + groupId + ", requête : " + SELECT_USER_IDS_IN_GROUP, null);
    return (String[]) getIds(SELECT_USER_IDS_IN_GROUP, groupId).toArray(
        new String[0]);
  }

  static final private String SELECT_USER_IDS_IN_GROUP = "select id from ST_User,ST_Group_User_Rel"
      + " where id = userId and groupId = ? and accessLevel <> 'R'"
      + " order by lastName";

  /**
   * Returns all the Users having directly a given role.
   */
  public UserRow[] getDirectUsersOfUserRole(int userRoleId)
      throws AdminPersistenceException {
    return (UserRow[]) getRows(SELECT_USERS_IN_USERROLE, userRoleId).toArray(
        new UserRow[0]);
  }

  static final private String SELECT_USERS_IN_USERROLE = "select "
      + USER_COLUMNS + " from ST_User,ST_UserRole_User_Rel"
      + " where id = userId and userRoleId = ? and accessLevel <> 'R'"
      + " order by lastName";

  /**
   * Returns all the User ids having directly a given role.
   */
  public String[] getDirectUserIdsOfUserRole(int userRoleId)
      throws AdminPersistenceException {
    return (String[]) getIds(SELECT_USER_IDS_IN_USERROLE, userRoleId).toArray(
        new String[0]);
  }

  static final private String SELECT_USER_IDS_IN_USERROLE =
      "select id from ST_User,ST_UserRole_User_Rel"
      + " where id = userId and userRoleId = ? and accessLevel <> 'R'"
      + " order by lastName";

  /**
   * Returns all the Users having a given domain id.
   */
  public UserRow[] getAllUserOfDomain(int domainId)
      throws AdminPersistenceException {
    SynchroReport.debug("UserTable.getAllUserOfDomain()",
        "Recherche de l'ensemble des utilisateurs du domaine LDAP dans la base (ID "
        + domainId + "), requête : " + SELECT_ALL_USERS_IN_DOMAIN, null);
    return (UserRow[]) getRows(SELECT_ALL_USERS_IN_DOMAIN, domainId).toArray(
        new UserRow[0]);
  }

  static final private String SELECT_ALL_USERS_IN_DOMAIN = "select "
      + USER_COLUMNS + " from ST_User"
      + " where domainId=? and accessLevel <> 'R'" + " order by lastName";

  /**
   * Returns all the User ids having a given domain id.
   */
  public String[] getUserIdsOfDomain(int domainId)
      throws AdminPersistenceException {
    return (String[]) getIds(SELECT_ALL_USER_IDS_IN_DOMAIN, domainId).toArray(
        new String[0]);
  }

  static final private String SELECT_ALL_USER_IDS_IN_DOMAIN =
      "select id from ST_User where domainId=? and accessLevel <> 'R' order by lastName";

  /**
   * Returns all the Users having directly a given space userRole.
   */
  public UserRow[] getDirectUsersOfSpaceUserRole(int spaceUserRoleId)
      throws AdminPersistenceException {
    return (UserRow[]) getRows(SELECT_USERS_IN_SPACEUSERROLE, spaceUserRoleId)
        .toArray(new UserRow[0]);
  }

  static final private String SELECT_USERS_IN_SPACEUSERROLE = "select "
      + USER_COLUMNS + " from ST_User,ST_SpaceUserRole_User_Rel"
      + " where id = userId and spaceUserRoleId = ? and accessLevel <> 'R'";

  /**
   * Returns all the User ids having directly a given space userRole.
   */
  public String[] getDirectUserIdsOfSpaceUserRole(int spaceUserRoleId)
      throws AdminPersistenceException {
    return (String[]) getIds(SELECT_USER_IDS_IN_SPACEUSERROLE, spaceUserRoleId)
        .toArray(new String[0]);
  }

  static final private String SELECT_USER_IDS_IN_SPACEUSERROLE =
      "select id from ST_User,ST_SpaceUserRole_User_Rel"
      + " where id = userId and spaceUserRoleId = ? and accessLevel <> 'R'";

  /**
   * Returns all the Users having directly a given group userRole.
   */
  public UserRow[] getDirectUsersOfGroupUserRole(int groupUserRoleId)
      throws AdminPersistenceException {
    return (UserRow[]) getRows(SELECT_USERS_IN_GROUPUSERROLE, groupUserRoleId)
        .toArray(new UserRow[0]);
  }

  static final private String SELECT_USERS_IN_GROUPUSERROLE = "select "
      + USER_COLUMNS + " from ST_User, ST_GroupUserRole_User_Rel"
      + " where id = userId and groupUserRoleId = ? and accessLevel <> 'R'";

  /**
   * Returns all the User ids having directly a given group userRole.
   */
  public String[] getDirectUserIdsOfGroupUserRole(int groupUserRoleId)
      throws AdminPersistenceException {
    return (String[]) getIds(SELECT_USER_IDS_IN_GROUPUSERROLE, groupUserRoleId)
        .toArray(new String[0]);
  }

  static final private String SELECT_USER_IDS_IN_GROUPUSERROLE =
      "select id from ST_User, ST_GroupUserRole_User_Rel"
      + " where id = userId and groupUserRoleId = ? and accessLevel <> 'R'";

  public String[] searchUsersIds(List<String> userIds, UserRow userModel)
      throws AdminPersistenceException {
    boolean concatAndOr = false;
    String andOr = ") AND (";
    Vector<Integer> ids = new Vector<Integer>();
    Vector<String> params = new Vector<String>();

    // WARNING !!! Ids must all be set before Params !!!!

    StringBuffer theQuery = new StringBuffer(SELECT_SEARCH_USERSID);
    if (userIds != null && userIds.size() > 0) {
      theQuery.append(" WHERE (ST_User.id IN (" + list2String(userIds) + ") ");
      concatAndOr = true;
    }

    concatAndOr = addIdToQuery(ids, theQuery, userModel.id, "ST_User.id",
        concatAndOr, andOr);
    if (userModel.domainId >= 0) {
      // users are not bound to "domaine mixte"
      concatAndOr = addIdToQuery(ids, theQuery, userModel.domainId,
          "ST_User.domainId", concatAndOr, andOr);
    }
    concatAndOr = addParamToQuery(params, theQuery, userModel.specificId,
        "ST_User.specificId", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.login,
        "ST_User.login", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.firstName,
        "ST_User.firstName", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.lastName,
        "ST_User.lastName", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.eMail,
        "ST_User.email", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.accessLevel,
        "ST_User.accessLevel", concatAndOr, andOr);
    concatAndOr =
        addParamToQuery(params, theQuery, userModel.loginQuestion, "ST_User.loginQuestion",
        concatAndOr, andOr);
    concatAndOr =
        addParamToQuery(params, theQuery, userModel.loginAnswer, "ST_User.loginAnswer",
        concatAndOr, andOr);

    if (concatAndOr) {
      theQuery.append(") AND (accessLevel <> 'R')");
    } else {
      theQuery.append(" WHERE (accessLevel <> 'R')");
    }
    theQuery.append(" order by UPPER(ST_User.lastName)");

    int[] idsArray = new int[ids.size()];
    for (int i = 0; i < ids.size(); i++) {
      idsArray[i] = ids.get(i).intValue();
    }

    return getIds(theQuery.toString(), idsArray, params.toArray(new String[0])).toArray(
        new String[0]);
  }

  /*
   * public String[] searchUsersIds(int groupId, int componentId, int[] aRoleId, UserRow userModel)
   * throws AdminPersistenceException { boolean concatAndOr = false; String andOr = ") AND (";
   * StringBuffer theQuery = null; Vector<Integer> ids = new Vector<Integer>(); Vector<String>
   * params = new Vector<String>(); // WARNING !!! Ids must all be set before Params !!!! if
   * (groupId >= 0) { theQuery = new StringBuffer(SELECT_SEARCH_USERSID_IN_GROUP); ids.add(new
   * Integer(groupId)); theQuery.append(
   * " WHERE ((ST_User.id = userId) AND (ST_UserSet_User_Rel.userSetType = 'G') AND (ST_UserSet_User_Rel.userSetId = ?)"
   * ); concatAndOr = true; } else if ((aRoleId != null) && (aRoleId.length > 0)) { theQuery = new
   * StringBuffer(SELECT_SEARCH_USERSID_IN_ROLE); theQuery.append(
   * " WHERE ((ST_User.id = ST_UserSet_User_Rel.userId) AND (ST_UserSet_User_Rel.userSetType = 'R') AND "
   * ); if (aRoleId.length > 1) { theQuery.append("("); } for (int i = 0; i < aRoleId.length; i++) {
   * ids.add(new Integer(aRoleId[i])); if (i > 0) { theQuery.append(" OR "); }
   * theQuery.append("(ST_UserSet_User_Rel.userSetId = ?)"); } if (aRoleId.length > 1) {
   * theQuery.append(")"); } concatAndOr = true; } else if (componentId >= 0) { theQuery = new
   * StringBuffer(SELECT_SEARCH_USERSID_IN_COMPONENT); ids.add(new Integer(componentId)); theQuery
   * .append(
   * " WHERE ((ST_User.id = ST_UserSet_User_Rel.userId) AND (ST_UserSet_User_Rel.userSetType = 'I') AND (ST_UserSet_User_Rel.userSetId = ?)"
   * ); concatAndOr = true; } else { theQuery = new StringBuffer(SELECT_SEARCH_USERSID); }
   * concatAndOr = addIdToQuery(ids, theQuery, userModel.id, "ST_User.id", concatAndOr, andOr); if
   * (userModel.domainId >= 0) { // users are not bound to "domaine mixte" concatAndOr =
   * addIdToQuery(ids, theQuery, userModel.domainId, "ST_User.domainId", concatAndOr, andOr); }
   * concatAndOr = addParamToQuery(params, theQuery, userModel.specificId, "ST_User.specificId",
   * concatAndOr, andOr); concatAndOr = addParamToQuery(params, theQuery, userModel.login,
   * "ST_User.login", concatAndOr, andOr); concatAndOr = addParamToQuery(params, theQuery,
   * userModel.firstName, "ST_User.firstName", concatAndOr, andOr); concatAndOr =
   * addParamToQuery(params, theQuery, userModel.lastName, "ST_User.lastName", concatAndOr, andOr);
   * concatAndOr = addParamToQuery(params, theQuery, userModel.eMail, "ST_User.email", concatAndOr,
   * andOr); concatAndOr = addParamToQuery(params, theQuery, userModel.accessLevel,
   * "ST_User.accessLevel", concatAndOr, andOr); concatAndOr = addParamToQuery(params, theQuery,
   * userModel.loginQuestion, "ST_User.loginQuestion", concatAndOr, andOr); concatAndOr =
   * addParamToQuery(params, theQuery, userModel.loginAnswer, "ST_User.loginAnswer", concatAndOr,
   * andOr); if (concatAndOr) { theQuery.append(") AND (accessLevel <> 'R')"); } else {
   * theQuery.append(" WHERE (accessLevel <> 'R')"); }
   * theQuery.append(" order by UPPER(ST_User.lastName)"); int[] idsArray = new int[ids.size()]; for
   * (int i = 0; i < ids.size(); i++) { idsArray[i] = ids.get(i).intValue(); } return
   * getIds(theQuery.toString(), idsArray, params.toArray(new String[0])).toArray( new String[0]); }
   */

  static final private String SELECT_SEARCH_USERSID =
      "select DISTINCT ST_User.id, UPPER(ST_User.lastName) "
      + "from ST_User";

  /*
   * static final private String SELECT_SEARCH_USERSID_IN_GROUP = "select ST_User.id " +
   * "from ST_User,ST_Group_User_Rel";
   */
  static final private String SELECT_SEARCH_USERSID_IN_GROUP =
      "select DISTINCT ST_User.id, UPPER(ST_User.lastName) "
      + "from ST_User,ST_UserSet_User_Rel";

  static final private String SELECT_SEARCH_USERSID_IN_COMPONENT =
      "select DISTINCT ST_User.id, UPPER(ST_User.lastName) "
      + "from ST_User,ST_UserSet_User_Rel";

  static final private String SELECT_SEARCH_USERSID_IN_ROLE =
      "select DISTINCT ST_User.id, UPPER(ST_User.lastName) "
      + "from ST_User,ST_UserSet_User_Rel";

  /**
   * Returns all the Users satiffying the model
   */
  public UserRow[] searchUsers(UserRow userModel, boolean isAnd)
      throws AdminPersistenceException {
    boolean concatAndOr = false;
    String andOr;
    StringBuffer theQuery = new StringBuffer(SELECT_SEARCH_USERS);
    Vector<Integer> ids = new Vector<Integer>();
    Vector<String> params = new Vector<String>();

    if (isAnd) {
      andOr = ") AND (";
    } else {
      andOr = ") OR (";
    }
    concatAndOr = addIdToQuery(ids, theQuery, userModel.id, "id", concatAndOr,
        andOr);
    concatAndOr = addIdToQuery(ids, theQuery, userModel.domainId, "domainId",
        concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.specificId,
        "specificId", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.login, "login",
        concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.firstName,
        "firstName", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.lastName,
        "lastName", concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.eMail, "email",
        concatAndOr, andOr);
    concatAndOr = addParamToQuery(params, theQuery, userModel.accessLevel,
        "accessLevel", concatAndOr, andOr);
    concatAndOr =
        addParamToQuery(params, theQuery, userModel.loginQuestion, "loginQuestion", concatAndOr,
        andOr);
    concatAndOr =
        addParamToQuery(params, theQuery, userModel.loginAnswer, "loginAnswer", concatAndOr, andOr);
    if (concatAndOr) {
      theQuery.append(") AND (accessLevel <> 'R')");
    } else {
      theQuery.append(" WHERE (accessLevel <> 'R')");
    }
    theQuery.append(" order by UPPER(lastName)");

    int[] idsArray = new int[ids.size()];
    for (int i = 0; i < ids.size(); i++) {
      idsArray[i] = ids.get(i).intValue();
    }

    return getRows(theQuery.toString(), idsArray, params.toArray(new String[0])).toArray(
        new UserRow[0]);
  }

  static final private String SELECT_SEARCH_USERS = "select " + USER_COLUMNS
      + ", UPPER(lastName)" + " from ST_User";

  /**
   * Returns the users whose fields match those of the given sample space fields.
   */
  public UserRow[] getAllMatchingUsers(UserRow sampleUser)
      throws AdminPersistenceException {
    String[] columns = new String[] { "login", "firstName", "lastName", "email" };
    String[] values = new String[] { sampleUser.login, sampleUser.firstName,
        sampleUser.lastName, sampleUser.eMail };

    return (UserRow[]) getMatchingRows(USER_COLUMNS, columns, values).toArray(
        new UserRow[0]);
  }

  /**
   * Inserts in the database a new user row.
   */
  public void createUser(UserRow user) throws AdminPersistenceException {
    SynchroReport.debug("UserTable.createUser()", "Ajout de " + user.login
        + ", requête : " + INSERT_USER, null);
    insertRow(INSERT_USER, user);

    CallBackManager.invoke(CallBackManager.ACTION_AFTER_CREATE_USER, user.id,
        null, null);
  }

  static final private String INSERT_USER = "insert into ST_User ("
      + USER_COLUMNS + ") values (?,?,?,?,?,?,?,?,?,?,?)";

  protected void prepareInsert(String insertQuery, PreparedStatement insert,
      Object row) throws SQLException {
    UserRow u = (UserRow) row;
    if (u.id == -1) {
      u.id = getNextId();
    }

    insert.setInt(1, u.id);
    insert.setString(2, truncate(u.specificId, 500));
    insert.setInt(3, u.domainId);
    insert.setString(4, truncate(u.login, 50));
    insert.setString(5, truncate(u.firstName, 100));
    insert.setString(6, truncate(u.lastName, 100));
    insert.setString(7, truncate(u.loginMail, 100));
    insert.setString(8, truncate(u.eMail, 100));
    insert.setString(9, truncate(u.accessLevel, 1));
    insert.setString(10, truncate(u.loginQuestion, 200));
    insert.setString(11, truncate(u.loginAnswer, 200));
  }

  /**
   * Update a user row.
   */
  public void updateUser(UserRow user) throws AdminPersistenceException {
    SynchroReport.debug("UserTable.updateUser()", "Maj de " + user.login
        + ", Id=" + user.id + ", requête : " + UPDATE_USER, null);
    updateRow(UPDATE_USER, user);
  }

  static final private String UPDATE_USER = "update ST_User set"
      + " specificId = ?,"
      + " domainId = ?,"
      + " login = ?,"
      + " firstName = ?,"
      + " lastName = ?,"
      + " loginMail = ?,"
      + " email = ?,"
      + " accessLevel = ?,"
      + " loginQuestion = ?,"
      + " loginAnswer = ?"
      + " where id = ?";

  protected void prepareUpdate(String updateQuery, PreparedStatement update,
      Object row) throws SQLException {
    UserRow u = (UserRow) row;

    update.setString(1, truncate(u.specificId, 500));
    update.setInt(2, u.domainId);
    update.setString(3, truncate(u.login, 50));
    update.setString(4, truncate(u.firstName, 100));
    update.setString(5, truncate(u.lastName, 100));
    update.setString(6, truncate(u.loginMail, 100));
    update.setString(7, truncate(u.eMail, 100));
    update.setString(8, truncate(u.accessLevel, 1));
    update.setString(9, truncate(u.loginQuestion, 200));
    update.setString(10, truncate(u.loginAnswer, 200));

    update.setInt(11, u.id);
  }

  /**
   * Removes a user row.
   */
  public void removeUser(int id) throws AdminPersistenceException {
    CallBackManager.invoke(CallBackManager.ACTION_BEFORE_REMOVE_USER, id, null,
        null);

    UserRow user = getUser(id);
    if (user == null)
      return;

    SynchroReport.info("UserTable.removeUser()", "Suppression de " + user.login
        + " des groupes dans la base", null);
    GroupRow[] groups = organization.group.getDirectGroupsOfUser(id);
    for (int i = 0; i < groups.length; i++) {
      organization.group.removeUserFromGroup(id, groups[i].id);
    }

    SynchroReport.info("UserTable.removeUser()", "Suppression de " + user.login
        + " des rôles dans la base", null);
    UserRoleRow[] roles = organization.userRole.getDirectUserRolesOfUser(id);
    for (int i = 0; i < roles.length; i++) {
      organization.userRole.removeUserFromUserRole(id, roles[i].id);
    }

    SynchroReport.info("UserTable.removeUser()", "Suppression de " + user.login
        + " en tant que manager d'espace dans la base", null);
    SpaceUserRoleRow[] spaceRoles = organization.spaceUserRole
        .getDirectSpaceUserRolesOfUser(id);
    for (int i = 0; i < spaceRoles.length; i++) {
      organization.spaceUserRole.removeUserFromSpaceUserRole(id,
          spaceRoles[i].id);
    }

    SynchroReport.info("UserTable.removeUser()", "Delete " + user.login
        + " from user favorite space table", null);
    UserFavoriteSpaceDAO ufsDAO = DAOFactory.getUserFavoriteSpaceDAO();
    if (!ufsDAO.removeUserFavoriteSpace(new UserFavoriteSpaceVO(id, -1))) {
      throw new AdminPersistenceException("UserTable.removeUser()",
          SilverpeasException.ERROR, "admin.EX_ERR_DELETE_USER");
    }

    SynchroReport.debug("UserTable.removeUser()", "Suppression de "
        + user.login + " (ID=" + id + "), requête : " + DELETE_USER, null);

    // updateRelation(DELETE_USER, id);
    // Replace the login by a dummy one that must be unique
    user.login = "???REM???" + Integer.toString(id);
    user.accessLevel = "R";
    user.specificId = "???REM???" + Integer.toString(id);
    updateRow(UPDATE_USER, user);
  }

  static final private String DELETE_USER = "delete from ST_User where id = ?";

  private static String list2String(List<String> ids) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < ids.size(); i++) {
      if (i != 0) {
        str.append(",");
      }
      str.append(ids.get(i));
    }
    return str.toString();
  }

  /**
   * Fetch the current user row from a resultSet.
   */
  protected Object fetchRow(ResultSet rs) throws SQLException {
    return fetchUser(rs);
  }

  private OrganizationSchema organization = null;
}