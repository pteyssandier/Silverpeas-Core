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
package com.silverpeas.socialNetwork.invitation;

import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.exception.UtilException;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvitationDao {

  private static final String INSERT_INVITATION =
      "INSERT INTO sb_sn_invitation (id, senderID, receiverId, message, invitationDate) VALUES (?, ?, ?, ?, ?)";
  private static final String DELETE_INVITATION = "DELETE FROM sb_sn_invitation WHERE id = ?";
  private static final String SELECT_INVITATION =
      "SELECT id, senderID, receiverId, message, invitationDate FROM sb_sn_invitation  WHERE senderID = ? and receiverId= ?";
  private static final String SELECT_INVITATION_BY_ID =
      "SELECT id, senderID, receiverId, message, invitationDate FROM sb_sn_invitation  WHERE id = ? ";
  private static final String SELECT_ALL_INVITATIONS_SENT =
      "SELECT id, senderID, receiverId, message, invitationDate FROM sb_sn_invitation  WHERE senderID = ?";
  private static final String SELECT_ALL_INVITATIONS_RECEIVE =
      "SELECT id, senderID, receiverId, message, invitationDate FROM sb_sn_invitation  WHERE receiverId= ?";

  /**
   * Create new invitation
   * @param connection
   * @param invitation
   * @return int the id of invitation
   * @throws UtilException
   * @throws SQLException
   */
  public int createInvitation(Connection connection, Invitation invitation) throws UtilException,
      SQLException {

    int id = DBUtil.getNextId("sb_sn_invitation", "id");
    PreparedStatement pstmt = null;
    try {
      pstmt = connection.prepareStatement(INSERT_INVITATION);

      pstmt.setInt(1, id);
      pstmt.setInt(2, invitation.getSenderId());
      pstmt.setInt(3, invitation.getReceiverId());
      pstmt.setString(4, invitation.getMessage());
      pstmt.setTimestamp(5, new Timestamp(invitation.getInvitationDate().getTime()));
      pstmt.executeUpdate();
    } finally {
      DBUtil.close(pstmt);
    }
    return id;

  }

  /**
   * Delete invitation rturn true whene this invitation was deleting
   * @param connection
   * @param id
   * @return boolean
   * @throws SQLException
   */
  public boolean deleteInvitation(Connection connection, int id) throws SQLException {
    PreparedStatement pstmt = null;
    boolean endAction = false;
    try {
      pstmt = connection.prepareStatement(DELETE_INVITATION);
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
      endAction = true;
    } finally {
      DBUtil.close(pstmt);
    }
    return endAction;
  }

  /**
   * rturn invitation between 2 users
   * @param connection
   * @param senderId
   * @param receiverId
   * @return Invitation
   * @throws SQLException
   */
  public Invitation getInvitation(Connection connection, int senderId, int receiverId) throws
      SQLException {
    Invitation invitation = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      pstmt = connection.prepareStatement(SELECT_INVITATION);
      pstmt.setInt(1, senderId);
      pstmt.setInt(2, receiverId);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        invitation = new Invitation();
        invitation.setId(rs.getInt(1));
        invitation.setSenderId(rs.getInt(2));
        invitation.setReceiverId(rs.getInt(3));
        invitation.setMessage(rs.getString(4));
        invitation.setInvitationDate(new Date(rs.getTimestamp(5).getTime()));
      }

    } finally {
      DBUtil.close(pstmt);
    }
    return invitation;
  }

  /**
   * rturn invitation
   * @param connection
   * @param id
   * @return Invitation
   * @throws SQLException
   */

  public Invitation getInvitation(Connection connection, int id) throws SQLException {
    Invitation invitation = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    try {
      pstmt = connection.prepareStatement(SELECT_INVITATION_BY_ID);
      pstmt.setInt(1, id);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        invitation = new Invitation();
        invitation.setId(rs.getInt(1));
        invitation.setSenderId(rs.getInt(2));
        invitation.setReceiverId(rs.getInt(3));
        invitation.setMessage(rs.getString(4));
        invitation.setInvitationDate(new Date(rs.getTimestamp(5).getTime()));
      }

    } finally {
      DBUtil.close(pstmt);
    }
    return invitation;
  }

  /**
   * return true if this invitation exist between 2 users
   * @param connection
   * @param senderId
   * @param receiverId
   * @return boolean
   * @throws SQLException
   */

  public boolean isExists(Connection connection, int senderId, int receiverId) throws SQLException {
    return (getInvitation(connection, senderId, receiverId) != null);
  }

  /**
   * return All my invitations sented
   * @param connection
   * @param myId
   * @return List<Invitation>
   * @throws SQLException
   */

  public List<Invitation> getAllMyInvitationsSent(Connection connection, int myId) throws
      SQLException {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Invitation> invitation_list = new ArrayList<Invitation>();
    try {
      pstmt = connection.prepareStatement(SELECT_ALL_INVITATIONS_SENT);
      pstmt.setInt(1, myId);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        Invitation invitation = new Invitation();
        invitation.setId(rs.getInt(1));
        invitation.setSenderId(rs.getInt(2));
        invitation.setReceiverId(rs.getInt(3));
        invitation.setMessage(rs.getString(4));
        invitation.setInvitationDate(new Date(rs.getTimestamp(5).getTime()));
        invitation_list.add(invitation);
      }
    } finally {
      DBUtil.close(rs, pstmt);
    }
    return invitation_list;
  }

  /**
   * return All my invitations received
   * @param connection
   * @param myId
   * @return List<Invitation>
   * @throws SQLException
   */
  public List<Invitation> getAllMyInvitationsReceive(Connection connection, int myId) throws
      SQLException {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Invitation> invitation_list = new ArrayList<Invitation>();
    try {
      pstmt = connection.prepareStatement(SELECT_ALL_INVITATIONS_RECEIVE);
      pstmt.setInt(1, myId);
      rs = pstmt.executeQuery();

      while (rs.next()) {
        Invitation invitation = new Invitation();
        invitation.setId(rs.getInt(1));
        invitation.setSenderId(rs.getInt(2));
        invitation.setReceiverId(rs.getInt(3));
        invitation.setMessage(rs.getString(4));
        invitation.setInvitationDate(new Date(rs.getTimestamp(5).getTime()));
        invitation_list.add(invitation);
      }
    } finally {
      DBUtil.close(rs, pstmt);
    }
    return invitation_list;
  }
}
