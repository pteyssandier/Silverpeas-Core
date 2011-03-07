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
package com.silverpeas.thumbnail.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.exception.UtilException;

public class ThumbnailDAO {

  private static String thumbnailTableName = "SB_Thumbnail_Thumbnail";
  private static String thumbnailColumnNames =
      " instanceid, objectid, objecttype, originalattachmentname, modifiedattachmentname, "
          + "mimetype, xstart, ystart, xlength, ylength ";

  public ThumbnailDAO() {
  }

  public static ThumbnailDetail insertThumbnail(Connection con,
      ThumbnailDetail thumbnailDetail) throws SQLException, UtilException {
    String insertQuery = "insert into "
        + thumbnailTableName + " (" + thumbnailColumnNames + ")"
        + " values ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)";
    PreparedStatement prepStmt = null;
    try {
      prepStmt = con.prepareStatement(insertQuery);
      prepStmt.setString(1, thumbnailDetail.getInstanceId());
      prepStmt.setInt(2, thumbnailDetail.getObjectId());
      prepStmt.setInt(3, thumbnailDetail.getObjectType());
      prepStmt.setString(4, thumbnailDetail.getOriginalFileName());
      if (thumbnailDetail.getCropFileName() != null) {
        prepStmt.setString(5, thumbnailDetail.getCropFileName());
      } else {
        prepStmt.setNull(5, Types.VARCHAR);
      }
      if (thumbnailDetail.getMimeType() != null) {
        prepStmt.setString(6, thumbnailDetail.getMimeType());
      } else {
        prepStmt.setNull(6, Types.VARCHAR);
      }
      if (thumbnailDetail.getXStart() != -1) {
        prepStmt.setInt(7, thumbnailDetail.getXStart());
      } else {
        prepStmt.setNull(7, Types.INTEGER);
      }
      if (thumbnailDetail.getYStart() != -1) {
        prepStmt.setInt(8, thumbnailDetail.getYStart());
      } else {
        prepStmt.setNull(8, Types.INTEGER);
      }
      if (thumbnailDetail.getXLength() != -1) {
        prepStmt.setInt(9, thumbnailDetail.getXLength());
      } else {
        prepStmt.setNull(9, Types.INTEGER);
      }
      if (thumbnailDetail.getYLength() != -1) {
        prepStmt.setInt(10, thumbnailDetail.getYLength());
      } else {
        prepStmt.setNull(10, Types.INTEGER);
      }
      prepStmt.executeUpdate();
    } finally {
      DBUtil.close(prepStmt);
    }

    return thumbnailDetail;
  }

  public static void updateThumbnail(Connection con, ThumbnailDetail thumbToUpdate)
      throws SQLException {
    String updateQuery =
        "update " + thumbnailTableName +
            " set xstart = ?, ystart = ?, xlength = ?, ylength = ?, "
            + " originalattachmentname = ?, modifiedattachmentname = ? "
            + " where objectId = ? and objectType = ? and instanceId = ? ";
    PreparedStatement prepStmt = null;
    try {
      prepStmt = con.prepareStatement(updateQuery);
      prepStmt.setInt(1, thumbToUpdate.getXStart());
      prepStmt.setInt(2, thumbToUpdate.getYStart());
      prepStmt.setInt(3, thumbToUpdate.getXLength());
      prepStmt.setInt(4, thumbToUpdate.getYLength());
      prepStmt.setString(5, thumbToUpdate.getOriginalFileName());
      prepStmt.setString(6, thumbToUpdate.getCropFileName());
      prepStmt.setInt(7, thumbToUpdate.getObjectId());
      prepStmt.setInt(8, thumbToUpdate.getObjectType());
      prepStmt.setString(9, thumbToUpdate.getInstanceId());

      prepStmt.executeUpdate();
    } finally {
      DBUtil.close(prepStmt);
    }
  }

  public static void deleteThumbnail(Connection con, int objectId, int objectType, String instanceId)
      throws SQLException {
    PreparedStatement prepStmt = null;

    try {
      String deleteQuery = "delete from " + thumbnailTableName
          + " where objectId = ? and objectType = ? and instanceId = ? ";

      prepStmt = con.prepareStatement(deleteQuery);
      prepStmt.setInt(1, objectId);
      prepStmt.setInt(2, objectType);
      prepStmt.setString(3, instanceId);
      prepStmt.executeUpdate();

    } finally {
      DBUtil.close(prepStmt);
    }
  }

  public static void deleteAllThumbnails(Connection con, String instanceId)
      throws SQLException {
    PreparedStatement prepStmt = null;

    try {
      String deleteQuery = "delete from " + thumbnailTableName
          + " where instanceId = ?";

      prepStmt = con.prepareStatement(deleteQuery);
      prepStmt.setString(1, instanceId);
      prepStmt.executeUpdate();

    } finally {
      DBUtil.close(prepStmt);
    }
  }

  public static ThumbnailDetail selectByKey(Connection con,
        String instanceId, int objectId, int objectType) throws SQLException {
    SilverTrace.info("publication", "ThumbnailDAO.selectByPubId()",
            "root.MSG_GEN_ENTER_METHOD", "objectId = " + objectId + "objectType" + objectType +
                "instanceId" + instanceId);
    ResultSet rs = null;
    ThumbnailDetail thumbnailDetail = null;
    String selectStatement =
        "select " +
            thumbnailColumnNames +
                     "from sb_thumbnail_thumbnail where objectId = ? and objectType = ? and instanceId = ?";
    PreparedStatement prepStmt = null;

    try {
      prepStmt = con.prepareStatement(selectStatement);
      prepStmt.setInt(1, objectId);
      prepStmt.setInt(2, objectType);
      prepStmt.setString(3, instanceId);

      rs = prepStmt.executeQuery();
      if (rs.next()) {
        thumbnailDetail = resultSet2ThumbDetail(rs);
      }
    } finally {
      DBUtil.close(rs, prepStmt);
    }

    return thumbnailDetail;
  }

  private static ThumbnailDetail resultSet2ThumbDetail(ResultSet rs) throws SQLException {
    ThumbnailDetail thumbnailDetail =
        new ThumbnailDetail(rs.getString("instanceid"), rs.getInt("objectid"), rs
            .getInt("objecttype"));
    thumbnailDetail.setOriginalFileName(rs.getString("originalattachmentname"));
    thumbnailDetail.setCropFileName(rs.getString("modifiedattachmentname"));
    thumbnailDetail.setMimeType(rs.getString("mimetype"));
    thumbnailDetail.setXStart(rs.getInt("xstart"));
    thumbnailDetail.setYStart(rs.getInt("ystart"));
    thumbnailDetail.setXLength(rs.getInt("xlength"));
    thumbnailDetail.setYLength(rs.getInt("ylength"));
    return thumbnailDetail;
  }
}