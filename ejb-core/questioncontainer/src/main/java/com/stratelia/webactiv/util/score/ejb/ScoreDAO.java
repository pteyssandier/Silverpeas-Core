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

// Source file: d:\\webactiv\\util\\com\\stratelia\\webactiv\\util\\score\\ejb\\ScoreDAO.java
package com.stratelia.webactiv.util.score.ejb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;
import com.stratelia.webactiv.util.exception.UtilException;
import com.stratelia.webactiv.util.score.model.ScoreDetail;
import com.stratelia.webactiv.util.score.model.ScorePK;
import com.stratelia.webactiv.util.score.model.ScoreRuntimeException;

/*
 * CVS Informations
 * 
 * $Id: ScoreDAO.java,v 1.3 2003/11/25 08:34:49 cbonin Exp $
 * 
 * $Log: ScoreDAO.java,v $
 * Revision 1.3  2003/11/25 08:34:49  cbonin
 * no message
 *
 * Revision 1.2  2003/11/24 11:10:31  cbonin
 * no message
 *
 * Revision 1.1.1.1  2002/08/06 14:47:53  nchaix
 * no message
 *
 * Revision 1.15  2001/12/21 13:51:41  scotte
 * no message
 *
 */

/**
 * Class declaration
 * @author
 */
public class ScoreDAO {

  public static final String SCORECOLUMNNAMES =
      "scoreId, qcId, userId, scoreParticipationId, scoreScore, scoreElapsedTime,scoreParticipationDate,scoreSuggestion";

  /**
   * Method declaration
   * @param rs
   * @param scorePK
   * @return
   * @throws SQLException
   * @see
   */
  private static ScoreDetail getScoreFromResultSet(ResultSet rs, ScorePK scorePK)
      throws SQLException {
    String id = new Integer(rs.getInt(1)).toString();
    String fatherId = new Integer(rs.getInt(2)).toString();
    String userId = rs.getString(3);
    int participationId = rs.getInt(4);
    int score = rs.getInt(5);
    int elapsedTime = rs.getInt(6);
    String participationDate = rs.getString(7);
    String suggestion = rs.getString(8);

    ScoreDetail result = new ScoreDetail(new ScorePK(id, scorePK), fatherId,
        userId, participationId, participationDate, score, elapsedTime,
        suggestion);

    return result;
  }

  /**
   * Method declaration
   * @param con
   * @param scoreDetail
   * @throws SQLException
   * @see
   */
  public static void addScore(Connection con, ScoreDetail scoreDetail)
      throws SQLException {
    int newId = 0;
    String insertStatement = "insert into "
        + scoreDetail.getScorePK().getTableName()
        + " values(?, ?, ?, ?, ?, ?, ?, ?) ";

    try {
      /* Recherche de la nouvelle PK de la table */
      newId = DBUtil.getNextId(scoreDetail.getScorePK().getTableName(),
          new String("scoreId"));
    } catch (UtilException ue) {
      throw new ScoreRuntimeException("ScoreDAO.addScore()",
          SilverpeasRuntimeException.ERROR, "score.EX_RECORD_GETNEXTID_FAILED",
          "id = " + scoreDetail.getScorePK().getId().toString());
    }

    ScorePK scorePK = scoreDetail.getScorePK();

    scorePK.setId(new Integer(newId).toString());

    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(insertStatement);
      prepStmt.setInt(1, newId);
      prepStmt.setInt(2, new Integer(scoreDetail.getFatherId()).intValue());
      prepStmt.setString(3, scoreDetail.getUserId());
      prepStmt.setInt(4, scoreDetail.getParticipationId());
      prepStmt.setInt(5, scoreDetail.getScore());
      prepStmt.setInt(6, scoreDetail.getElapsedTime());
      prepStmt.setString(7, scoreDetail.getParticipationDate());
      prepStmt.setString(8, scoreDetail.getSuggestion());

      prepStmt.executeUpdate();
    } finally {
      DBUtil.close(prepStmt);
    }

  }

  /**
   * Method declaration
   * @param con
   * @param scoreDetail
   * @throws SQLException
   * @see
   */
  public static void updateScore(Connection con, ScoreDetail scoreDetail)
      throws SQLException {
    String insertStatement = "update "
        + scoreDetail.getScorePK().getTableName()
        + " set scoreSuggestion = ? where scoreId = ?";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(insertStatement);
      prepStmt.setString(1, scoreDetail.getSuggestion());
      prepStmt.setInt(2, new Integer(scoreDetail.getScorePK().getId())
          .intValue());
      prepStmt.executeUpdate();
    } finally {
      DBUtil.close(prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3C7500C0
   */
  public static void deleteScore(Connection con, ScorePK scorePK)
      throws SQLException {
    String deleteStatement = "delete from " + scorePK.getTableName()
        + " where scoreId = ? ";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(deleteStatement);
      prepStmt.setInt(1, new Integer(scorePK.getId()).intValue());
      prepStmt.executeUpdate();
      prepStmt.close();
    } finally {
      DBUtil.close(prepStmt);
    }
  }

  // Begin Modif Une Table par Instance by hguig

  /**
   * Method declaration
   * @param con
   * @param scorePK
   * @param fatherId
   * @throws SQLException
   * @see
   */
  public static void deleteScoreByFatherPK(Connection con, ScorePK scorePK,
      String fatherId) throws SQLException {
    String deleteStatement = "delete from " + scorePK.getTableName()
        + " where qcId = ? ";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(deleteStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      // prepStmt.setString(2,questionPK.getComponentName());
      prepStmt.executeUpdate();
      prepStmt.close();
    } finally {
      DBUtil.close(prepStmt);
    }
  }

  // End Modif Une Table par Instance

  /**
   * @roseuid 3ACC3C8F0370
   */
  public static Collection getAllScores(Connection con, ScorePK scorePK)
      throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName() + " order by scoreParticipationDate desc";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();

      while (rs.next()) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3CAE0248
   */
  public static Collection getUserScores(Connection con, ScorePK scorePK,
      String userId) throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName()
        + " where userId = ? order by scoreParticipationDate desc";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(userId).intValue());
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();
      while (rs.next()) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3CC50025
   */
  public static Collection getUserScoresByFatherId(Connection con,
      ScorePK scorePK, String fatherId, String userId) throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName()
        + " where qcId = ? and userId=? order by scoreParticipationDate desc";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      prepStmt.setString(2, userId);
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();
      while (rs.next()) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * Method declaration
   * @param con
   * @param scorePK
   * @param fatherId
   * @return
   * @throws SQLException
   * @see
   */
  public static Collection getScoresByFatherId(Connection con, ScorePK scorePK,
      String fatherId) throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName() + " where qcId = ? order by scoreScore desc";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();
      while (rs.next()) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3D7D01F6
   */
  public static Collection getBestScoresByFatherId(Connection con,
      ScorePK scorePK, int nbBestScores, String fatherId) throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    int nbRecord = 0;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName() + " where qcId = ? order by scoreScore desc";

    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();
      while ((rs.next()) && (nbRecord < nbBestScores)) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
        nbRecord++;
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3DBB02B3
   */
  public static Collection getWorstScoresByFatherId(Connection con,
      ScorePK scorePK, int nbWorstScores, String fatherId) throws SQLException {
    ResultSet rs = null;
    ScoreDetail scoreDetail = null;
    int nbRecord = 0;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName() + " where qcId = ? order by scoreScore";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      rs = prepStmt.executeQuery();
      ArrayList result = new ArrayList();
      while ((rs.next()) && (nbRecord < nbWorstScores)) {
        scoreDetail = getScoreFromResultSet(rs, scorePK);
        result.add(scoreDetail);
        nbRecord++;
      }
      return result;
    } finally {
      DBUtil.close(rs, prepStmt);
    }
  }

  /**
   * @roseuid 3ACC3DEA0397
   */
  public static int getNbVotersByFatherId(Connection con, ScorePK scorePK,
      String fatherId) throws SQLException {
    ResultSet rs = null;
    String selectStatement = "select count(*) from " + scorePK.getTableName()
        + " where qcId = ?";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      rs = prepStmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } finally {
      DBUtil.close(rs, prepStmt);
    }
    return 0;
  }

  /**
   * @roseuid 3ACC3E1B00F9
   */
  public static float getAverageScoreByFatherId(Connection con,
      ScorePK scorePK, String fatherId) throws SQLException {
    int nbVoters = getNbVotersByFatherId(con, scorePK, fatherId);
    float average = 0;
    int sumPoints = 0;

    if (nbVoters > 0) {
      ResultSet rs = null;
      String selectStatement = "select sum(scoreScore) from "
          + scorePK.getTableName() + " where qcId = ?";
      PreparedStatement prepStmt = null;

      try {
        prepStmt = con.prepareStatement(selectStatement);
        prepStmt.setInt(1, new Integer(fatherId).intValue());
        rs = prepStmt.executeQuery();
        if (rs.next()) {
          sumPoints = rs.getInt(1);
          average = Math
              .round(((new Float(sumPoints).floatValue()) / (new Float(nbVoters)
              .floatValue())) * 10)
              / (new Float(10).floatValue());
        }
      } finally {
        DBUtil.close(rs, prepStmt);
      }

    }
    return average;
  }

  /**
   * @roseuid 3ACC3E8F01F0
   */
  public static ScoreDetail getUserScoreByFatherIdAndParticipationId(
      Connection con, ScorePK scorePK, String fatherId, String userId,
      int participationId) throws SQLException {
    ResultSet rs = null;
    String selectStatement = "select " + SCORECOLUMNNAMES + " from "
        + scorePK.getTableName()
        + " where qcId = ? and userId = ? and scoreParticipationId= ?";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      prepStmt.setString(2, userId);
      prepStmt.setInt(3, participationId);
      rs = prepStmt.executeQuery();
      if (rs.next()) {
        return getScoreFromResultSet(rs, scorePK);
      }
    } finally {
      DBUtil.close(rs, prepStmt);
    }
    return null;
  }

  /**
   * @roseuid 3ACC3EB70003
   */
  public static int getUserNbParticipationsByFatherId(Connection con,
      ScorePK scorePK, String fatherId, String userId) throws SQLException {
    ResultSet rs = null;
    String selectStatement = "select count(*) from " + scorePK.getTableName()
        + " where qcId = ? and userId = ?";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, new Integer(fatherId).intValue());
      prepStmt.setString(2, userId);
      rs = prepStmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } finally {
      DBUtil.close(rs, prepStmt);
    }
    return 0;
  }

  /**
   * @roseuid 3ACC3F0302E7
   */
  public static int getUserPositionByFatherIdAndParticipationId(Connection con,
      ScorePK scorePK, String fatherId, String userId, int participationId)
      throws SQLException {
    Collection scoreDetails = getScoresByFatherId(con, scorePK, fatherId);
    Iterator it = scoreDetails.iterator();
    int position = 0;
    int nbPosition = 0;
    String previousScore = null;

    while (it.hasNext()) {
      ScoreDetail scoreDetail = (ScoreDetail) it.next();

      if ((previousScore != null)
          && (scoreDetail.getScore() == new Integer(previousScore).intValue())) {
        nbPosition++;
      } else {
        position += nbPosition + 1;
        nbPosition = 0;
      }

      if ((scoreDetail.getUserId().equals(userId))
          && (scoreDetail.getParticipationId() == participationId)) {
        return position;
      }
      previousScore = new Integer(scoreDetail.getScore()).toString();

    }
    return 0;
  }

}
