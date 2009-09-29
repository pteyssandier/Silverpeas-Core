package com.stratelia.silverpeas.domains.sqldriver;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminException;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.exception.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * A GroupTable object manages the DomainSQL_Group table.
 */
public class SQLGroupTable {
  SQLSettings drvSettings = new SQLSettings();

  public SQLGroupTable(SQLSettings ds) {
    drvSettings = ds;
  }

  protected String getColumns() {
    return drvSettings.getGroupSpecificIdColumnName() + ", "
        + drvSettings.getGroupParentIdColumnName() + ", "
        + drvSettings.getGroupNameColumnName() + ", "
        + drvSettings.getGroupDescriptionColumnName();
  }

  /**
   * Fetch the current group row from a resultSet.
   */
  protected Group fetchGroup(ResultSet rs) throws SQLException {
    Group g = new Group();

    g.setSpecificId(Integer.toString(rs.getInt(1)));
    g.setSuperGroupId(Integer.toString(rs.getInt(2)));
    if (rs.wasNull())
      g.setSuperGroupId(null);
    g.setName(rs.getString(3));
    g.setDescription(rs.getString(4));
    return g;
  }

  /**
   * Inserts in the database a new Group row.
   */
  public int createGroup(Connection c, Group group) throws AdminException {
    PreparedStatement statement = null;
    int nextId = 0;
    String theQuery = "insert into " + drvSettings.getGroupTableName() + "("
        + getColumns() + ") values (?,?,?,?)";

    try {
      SilverTrace.debug("admin", "SQLGroupTable.createGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      nextId = DBUtil.getNextId(drvSettings.getGroupTableName(), drvSettings
          .getGroupSpecificIdColumnName());
      statement.setInt(1, nextId);
      String gid = group.getSuperGroupId();
      if ((gid == null) || (gid.length() <= 0) || (gid.equals("-1")))
        statement.setNull(2, Types.INTEGER);
      else
        statement.setInt(2, Integer.parseInt(gid));
      statement.setString(3, drvSettings.trunc(group.getName(), 100));
      statement.setString(4, drvSettings.trunc(group.getDescription(), 400));
      statement.executeUpdate();
    } catch (Exception e) {
      throw new AdminException("SQLGroupTable.createGroup",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(statement);
    }
    return nextId;
  }

  public void deleteGroup(Connection c, int groupId) throws AdminException {
    PreparedStatement statement = null;
    String theQuery = "delete from " + drvSettings.getGroupTableName()
        + " where " + drvSettings.getGroupSpecificIdColumnName() + " = ?";

    try {
      SilverTrace.debug("admin", "SQLGroupTable.deleteGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      statement.setInt(1, groupId);
      statement.executeUpdate();
    } catch (Exception e) {
      throw new AdminException("SQLGroupTable.deleteGroup",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(statement);
    }
  }

  public void updateGroup(Connection c, Group g) throws AdminException {
    PreparedStatement statement = null;
    String theQuery = "update " + drvSettings.getGroupTableName() + " set "
        + drvSettings.getGroupNameColumnName() + " = ?,"
        + drvSettings.getGroupDescriptionColumnName() + " = ?" + " where "
        + drvSettings.getGroupSpecificIdColumnName() + " = ?";

    try {
      SilverTrace.debug("admin", "SQLGroupTable.updateGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      statement.setString(1, drvSettings.trunc(g.getName(), 100));
      statement.setString(2, drvSettings.trunc(g.getDescription(), 400));
      statement.setInt(3, Integer.parseInt(g.getSpecificId()));
      statement.executeUpdate();
    } catch (Exception e) {
      throw new AdminException("SQLGroupTable.updateGroup",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(statement);
    }
  }

  /**
   * Returns the Group whith the given id.
   */
  public Group getGroup(Connection c, int groupId) throws AdminException {
    ResultSet rs = null;
    PreparedStatement statement = null;
    String theQuery = "select " + getColumns() + " from "
        + drvSettings.getGroupTableName() + " where id = ?";

    try {
      SilverTrace.debug("admin", "SQLGroupTable.getGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      statement.setInt(1, groupId);
      rs = statement.executeQuery();
      if (rs.next()) {
        return fetchGroup(rs);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new AdminException("SQLGroupTable.getGroup",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(rs, statement);
    }
  }

  /**
   * Returns the Group whith the given name.
   */
  public Group getGroupByName(Connection c, String groupName)
      throws AdminException {
    ResultSet rs = null;
    PreparedStatement statement = null;
    String theQuery = "select " + getColumns() + " from "
        + drvSettings.getGroupTableName() + " where name = ?";
    try {
      SilverTrace.debug("admin", "SQLGroupTable.getGroupByName",
          "root.MSG_QUERY", theQuery);
      statement = c.prepareStatement(theQuery);
      statement.setString(1, groupName);
      rs = statement.executeQuery();
      if (rs.next()) {
        return fetchGroup(rs);
      } else {
        return null;
      }
    } catch (SQLException e) {
      throw new AdminException("SQLGroupTable.getGroupByName",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(rs, statement);
    }
  }

  /**
   * Returns the User whith the given id.
   */
  public ArrayList getAllGroups(Connection c) throws AdminException {
    ResultSet rs = null;
    PreparedStatement statement = null;
    ArrayList theResult = new ArrayList();
    String theQuery = "select " + getColumns() + " from "
        + drvSettings.getGroupTableName();

    try {
      SilverTrace.debug("admin", "SQLGroupTable.getGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      rs = statement.executeQuery();
      while (rs.next()) {
        theResult.add(fetchGroup(rs));
      }
      return theResult;
    } catch (SQLException e) {
      throw new AdminException("SQLGroupTable.getAllGroups",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(rs, statement);
    }
  }

  /**
   * Returns the User whith the given id.
   */
  public ArrayList getDirectSubGroups(Connection c, int groupId)
      throws AdminException {
    ResultSet rs = null;
    PreparedStatement statement = null;
    ArrayList theResult = new ArrayList();
    String theQuery = "select " + getColumns() + " from "
        + drvSettings.getGroupTableName() + " where "
        + drvSettings.getGroupParentIdColumnName();

    try {
      if (groupId == -1)
        theQuery = theQuery + " is null";
      else
        theQuery = theQuery + " = ?";
      SilverTrace.debug("admin", "SQLGroupTable.getGroup", "root.MSG_QUERY",
          theQuery);
      statement = c.prepareStatement(theQuery);
      if (groupId != -1)
        statement.setInt(1, groupId);
      rs = statement.executeQuery();
      while (rs.next()) {
        theResult.add(fetchGroup(rs));
      }
      return theResult;
    } catch (SQLException e) {
      throw new AdminException("SQLGroupTable.getAllGroups",
          SilverpeasException.ERROR, "root.EX_SQL_QUERY_FAILED", "Query = "
              + theQuery, e);
    } finally {
      DBUtil.close(rs, statement);
    }
  }
}
