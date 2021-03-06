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

// TODO : reporter dans CVS (done)
package com.stratelia.silverpeas.silverStatisticsPeas.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;

/**
 * Class declaration Get connections data from database
 * @author
 */
public class SilverStatisticsPeasDAOConnexion {

  public static final int millisPerHour = 60 * 60 * 1000;
  public static final int millisPerMinute = 60 * 1000;

  public static final int INDICE_LIB = 0;
  public static final int INDICE_COUNTCONNEXION = 1;
  public static final int INDICE_DURATION = 2;
  public static final int INDICE_ID = 3;

  private static final String DB_NAME = JNDINames.SILVERSTATISTICS_DATASOURCE;

  /**
   * donne les stats global pour l'enemble de tous les users cad 2 infos, la collection contient
   * donc un seul element
   * @param dateBegin
   * @param dateEnd
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionAllAll(String dateBegin,
      String dateEnd) throws SQLException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionAllAll",
        "root.MSG_GEN_ENTER_METHOD");
    String selectQuery = " SELECT '*', SUM(countConnection), SUM(duration), ''"
        + " FROM SB_Stat_ConnectionCumul" + " WHERE dateStat BETWEEN '"
        + dateBegin + "' AND '" + dateEnd + "'";

    return getStatsConnexionFromQuery(selectQuery);
  }

  /**
   * Method declaration
   * @param rs
   * @return
   * @throws SQLException
   * @throws ParseException
   * @see
   */
  private static Collection[] getCollectionArrayFromResultset(ResultSet rs,
      String dateBegin, String dateEnd) throws SQLException, ParseException {
    Vector<String> dates = new Vector<String>();
    Vector<String> counts = new Vector<String>();
    String date = "";
    long count = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateRef = dateBegin;
    Calendar calDateRef = GregorianCalendar.getInstance();
    calDateRef.setTime(sdf.parse(dateRef));

    while (rs.next()) {
      date = rs.getString(1);
      count = rs.getLong(2);

      while (dateRef.compareTo(date) <= 0) {
        dates.add(dateRef);
        counts.add("0");

        // ajoute un mois
        calDateRef.add(Calendar.MONTH, 1);
        dateRef = sdf.format(calDateRef.getTime());
      }

      dates.add(date);
      counts.add(Long.toString(count));

      // ajoute un mois
      calDateRef.add(Calendar.MONTH, 1);
      dateRef = sdf.format(calDateRef.getTime());
    }

    while (dateRef.compareTo(dateEnd) <= 0) {
      dates.add(dateRef);
      counts.add("0");

      // ajoute un mois
      calDateRef.add(Calendar.MONTH, 1);
      dateRef = sdf.format(calDateRef.getTime());
    }

    return new Collection[] { dates, counts };
  }

  /**
   * Method declaration
   * @param selectQuery
   * @return
   * @throws SQLException
   * @throws ParseException
   * @see
   */
  private static Collection[] getCollectionArrayFromQuery(String selectQuery,
      String dateBegin, String dateEnd) throws SQLException, ParseException {
    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getCollectionArrayFromQuery",
        "selectQuery=" + selectQuery);
    Statement stmt = null;
    ResultSet rs = null;
    Collection[] list = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      rs = stmt.executeQuery(selectQuery);
      list = getCollectionArrayFromResultset(rs, dateBegin, dateEnd);
    } finally {
      DBUtil.close(rs, stmt);
      freeConnection(myCon);
    }

    return list;
  }

  /**
   * donne les stats sur le nombre d'utilisateurs disctincts par mois
   * @return
   * @throws SQLException
   * @throws ParseException
   */
  public static Collection[] getStatsUser(String dateBegin, String dateEnd)
      throws SQLException, ParseException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsUser",
        "root.MSG_GEN_ENTER_METHOD");
    String selectQuery = " SELECT datestat, count(distinct userid)"
        + " FROM SB_Stat_ConnectionCumul" + " WHERE dateStat BETWEEN '"
        + dateBegin + "' AND '" + dateEnd + "'" + " group by dateStat"
        + " order by dateStat";

    return getCollectionArrayFromQuery(selectQuery, dateBegin, dateEnd);
  }

  /**
   * donne les stats global pour l'enemble de tous les users cad 2 infos, la collection contient
   * donc un seul element
   * @return
   * @throws SQLException
   * @throws ParseException
   */
  public static Collection[] getStatsConnexion(String dateBegin, String dateEnd)
      throws SQLException, ParseException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexion",
        "root.MSG_GEN_ENTER_METHOD");
    String selectQuery = " SELECT datestat, SUM(countConnection)"
        + " FROM SB_Stat_ConnectionCumul" + " WHERE dateStat BETWEEN '"
        + dateBegin + "' AND '" + dateEnd + "'" + " group by dateStat"
        + " order by dateStat";

    return getCollectionArrayFromQuery(selectQuery, dateBegin, dateEnd);
  }

  /**
   * donne les stats pour un user seulement cad 2 infos, la collection contient donc un seul element
   * @param dateBegin
   * @param dateEnd
   * @param idUser
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionAllUser(String dateBegin,
      String dateEnd, int idUser) throws SQLException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionAllUser",
        "root.MSG_GEN_ENTER_METHOD");

    String selectQuery = "SELECT B.lastName, SUM(A.countConnection), SUM(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A, ST_User B"
        + " WHERE A.dateStat BETWEEN '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = B.id AND A.userID=?" + " GROUP BY B.lastName, B.id";

    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionAllUser",
        "selectQuery=" + selectQuery);

    return getStatsConnexionFromQuery(selectQuery, idUser);
  }

  /**
   * donne les stats pour un groupe
   * @return
   * @throws SQLException
   * @throws ParseException
   */
  public static Collection[] getStatsUserConnexion(String dateBegin,
      String dateEnd, String idUser) throws SQLException, ParseException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsUserConnexion",
        "root.MSG_GEN_ENTER_METHOD");
    String selectQuery = " SELECT datestat, SUM(countConnection)"
        + " FROM SB_Stat_ConnectionCumul" + " WHERE dateStat BETWEEN '"
        + dateBegin + "' AND '" + dateEnd + "'" + " AND userId =" + idUser
        + " group by dateStat" + " order by dateStat";

    return getCollectionArrayFromQuery(selectQuery, dateBegin, dateEnd);
  }

  /**
   * donne les stats pour un groupe seulement cad 2 info, la collection contient donc un seul
   * element
   * @param dateBegin
   * @param dateEnd
   * @param idGroup
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionAllGroup(String dateBegin,
      String dateEnd, int idGroup) throws SQLException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionAllGroup",
        "root.MSG_GEN_ENTER_METHOD");

    String selectQuery = "SELECT B.name, SUM(A.countConnection), SUM(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A, ST_Group B, ST_Group_User_Rel C"
        + " WHERE A.dateStat BETWEEN '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = C.userId AND C.groupId= B.id AND B.id=?"
        + " GROUP BY B.name, B.id";

    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionAllGroup",
        "selectQuery=" + selectQuery);

    return getStatsConnexionFromQuery(selectQuery, idGroup);
  }

  /**
   * donne les stats pour un groupe
   * @return
   * @throws SQLException
   * @throws ParseException
   */
  public static Collection[] getStatsGroupConnexion(String dateBegin,
      String dateEnd, String idGroup) throws SQLException, ParseException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsGroupConnexion",
        "root.MSG_GEN_ENTER_METHOD");
    String selectQuery = " SELECT A.datestat, SUM(A.countConnection)"
        + " FROM SB_Stat_ConnectionCumul A, ST_Group_User_Rel C"
        + " WHERE A.dateStat BETWEEN '" + dateBegin + "' AND '" + dateEnd + "'"
        + " AND A.userId = C.userId" + " AND C.groupId =" + idGroup
        + " group by dateStat" + " order by dateStat";

    return getCollectionArrayFromQuery(selectQuery, dateBegin, dateEnd);
  }

  /**
   * donne pour chaque groupe ses stats cad 3 infos par groupe, la collection contient autant
   * d'elements que de groupes
   * @param dateBegin
   * @param dateEnd
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionGroupAll(String dateBegin,
      String dateEnd) throws SQLException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionGroupAll",
        "root.MSG_GEN_ENTER_METHOD");

    String selectQuery = "SELECT B.name, SUM(A.countConnection), SUM(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A, ST_Group B, ST_Group_User_Rel C"
        + " WHERE A.dateStat BETWEEN '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = C.userId AND C.groupId= B.id"
        + " GROUP BY B.name, B.id";

    return getStatsConnexionFromQuery(selectQuery);
  }

  /**
   * donne pour un chaque groupe d'un user les stats cad 3 infos par groupe, la collection contient
   * autant d'elements que de groupes dont le user fait parti
   * @param dateBegin
   * @param dateEnd
   * @param idUser
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionGroupUser(String dateBegin,
      String dateEnd, int idUser) throws SQLException {
    String selectQuery = "SELECT B.name, SUM(A.countConnection), SUM(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A, ST_Group B, ST_Group_User_Rel C"
        + " WHERE A.dateStat BETWEEN '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = C.userId AND C.groupId= B.id AND C.groupId=?"
        + " GROUP BY B.name, B.id";

    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionGroupUser",
        "selectQuery=" + selectQuery);

    return getStatsConnexionFromQuery(selectQuery, idUser);
  }

  /**
   * donne pour chaque user ses stats, cad 3 infos, la collection contient autant d'elements que de
   * users
   * @param dateBegin
   * @param dateEnd
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionUserAll(String dateBegin,
      String dateEnd) throws SQLException {
    SilverTrace.info("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionUserAll",
        "root.MSG_GEN_ENTER_METHOD");

    String selectQuery = "SELECT B.lastName, SUM(A.countConnection), SUM(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A,	ST_User B"
        + " WHERE A.dateStat BETWEEN '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = B.id" + " GROUP BY B.lastName, B.id";

    return getStatsConnexionFromQuery(selectQuery);
  }

  /**
   * donne pour chaque user d'un groupe ses stats, cad 3 infos, la collection contient autant
   * d'elements que de users dans le groupe
   * @param dateBegin
   * @param dateEnd
   * @param idGroup
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection<String[]> getStatsConnexionUserUser(String dateBegin,
      String dateEnd, int idUser) throws SQLException {
    String selectQuery = "SELECT B.lastName, sum(A.countConnection), sum(A.duration), B.id"
        + " FROM SB_Stat_ConnectionCumul A,	ST_User B"
        + " WHERE A.dateStat between '"
        + dateBegin
        + "' AND '"
        + dateEnd
        + "'"
        + " AND A.userId = B.Id and B.Id=? " + " GROUP BY B.lastName, B.id";

    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionUserGroup",
        "selectQuery=" + selectQuery);

    return getStatsConnexionFromQuery(selectQuery, idUser);

  }

  public static Collection<String> getYears() throws SQLException {
    // String selectQuery =
    // " SELECT DISTINCT LEFT(dateStat,4) FROM SB_Stat_ConnectionCumul ORDER BY LEFT(dateStat,4)";
    // dle
    String selectQuery = " SELECT DISTINCT dateStat FROM SB_Stat_ConnectionCumul ORDER BY dateStat";

    return getYearsFromQuery(selectQuery);
  }

  /**
   * donne les stats d'accès par KM pour la date donnée
   * @return
   * @throws SQLException
   * @throws ParseException
   */
  public static Collection[] getStatsUserFq(String dateBegin, String dateEnd, int min, int max)
      throws SQLException, ParseException {
    SilverTrace.info("silverStatisticsPeas", "SilverStatisticsPeasDAOConnexion.getStatsUserFq",
        "root.MSG_GEN_ENTER_METHOD");

    String selectQuery = " SELECT dateStat, COUNT(userId)"
        + " FROM SB_Stat_ConnectionCumul" + " WHERE dateStat BETWEEN '"
        + dateBegin + "' AND '" + dateEnd + "'" + " AND countConnection>="
        + min + " AND countConnection<" + max + " GROUP BY dateStat"
        + " order by dateStat";

    return getCollectionArrayFromQuery(selectQuery, dateBegin, dateEnd);
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  private static Connection getConnection() {
    try {
      Connection con = DBUtil.makeConnection(DB_NAME);

      return con;
    } catch (Exception e) {
      throw new SilverStatisticsPeasRuntimeException(
          "SilverStatisticsPeasDAOConnexion.getConnection()",
          SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_OPEN_FAILED",
          "DbName=" + DB_NAME, e);
    }
  }

  /**
   * Method declaration
   * @param con
   * @see
   */
  private static void freeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
      } catch (Exception e) {
        SilverTrace.error("silverStatisticsPeas",
            "SilverStatisticsPeasDAOConnexion.freeConnection()",
            "root.EX_CONNECTION_CLOSE_FAILED", "", e);
      }
    }
  }

  /**
   * Method declaration
   * @param selectQuery
   * @return
   * @throws SQLException
   * @see
   */
  private static Collection<String[]> getStatsConnexionFromQuery(String selectQuery)
      throws SQLException {
    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionFromQuery",
        "selectQuery=" + selectQuery);
    Statement stmt = null;
    ResultSet rs = null;
    Collection<String[]> list = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      rs = stmt.executeQuery(selectQuery);
      list = getStatsConnexion(rs);
    } finally {
      DBUtil.close(rs, stmt);
      freeConnection(myCon);
    }

    return list;
  }

  private static Collection<String> getYearsFromQuery(String selectQuery)
      throws SQLException {
    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getYearsFromQuery", "selectQuery="
            + selectQuery);
    Statement stmt = null;
    ResultSet rs = null;
    Collection<String> c;
    Connection myCon = getConnection();

    try {
      stmt = myCon.createStatement();
      rs = stmt.executeQuery(selectQuery);
      c = getYearsConnexion(rs);
    } finally {
      DBUtil.close(rs, stmt);
      freeConnection(myCon);
    }

    return c;
  }

  /**
   * Method declaration
   * @param selectQuery
   * @param id
   * @return
   * @throws SQLException
   * @see
   */
  private static Collection<String[]> getStatsConnexionFromQuery(String selectQuery,
      int id) throws SQLException {
    SilverTrace.debug("silverStatisticsPeas",
        "SilverStatisticsPeasDAOConnexion.getStatsConnexionFromQuery",
        "selectQuery=" + selectQuery);
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Collection<String[]> list = null;
    Connection myCon = getConnection();

    try {
      stmt = myCon.prepareStatement(selectQuery);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();
      list = getStatsConnexion(rs);
    } finally {
      DBUtil.close(rs, stmt);
      freeConnection(myCon);
    }

    return list;
  }

  private static Collection<String> getYearsConnexion(ResultSet rs) throws SQLException {
    List<String> myList = new ArrayList<String>();
    String year = "";
    while (rs.next()) {
      if (!year.equals(rs.getString(1).substring(0, 4))) {
        year = rs.getString(1).substring(0, 4);
        myList.add(year);
      }
    }
    return myList;
  }

  /**
   * Method declaration
   * @param rs
   * @return
   * @throws SQLException
   * @see
   */
  private static Collection<String[]> getStatsConnexion(ResultSet rs) throws SQLException {
    List<String[]> myList = new ArrayList<String[]>();
    String stat[] = null;
    long duration = 0;
    long count = 0;

    while (rs.next()) {
      stat = new String[4];
      stat[INDICE_LIB] = rs.getString(1);
      count = rs.getLong(2);
      stat[INDICE_COUNTCONNEXION] = Long.toString(count);
      duration = rs.getLong(3);
      if (count != 0) {
        // calcul durée moyenne = durée totale / nb connexions
        duration /= count;
      }
      stat[INDICE_DURATION] = Long.toString(duration);
      stat[INDICE_ID] = rs.getString(4);

      myList.add(stat);
    }
    return myList;
  }
}