package com.stratelia.webactiv.agenda.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.agenda.control.AgendaException;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasException;

public class CalendarImportSettingsDaoJdbc implements CalendarImportSettingsDao {

	/**
	 * Get synchronisation user settings
	 * 
	 * @param userId
	 *            Id of user whose settings belong to
	 * @return CalendarImportSettings object containing user settings, null if
	 *         no settings found
	 * @see com.stratelia.webactiv.agenda.model.CalendarImportSettings
	 */
	public CalendarImportSettings getUserSettings(String userId) {
		Connection connection = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		CalendarImportSettings settings = null;

		String query = "select * from sb_agenda_import_settings where userId = ?";

		try {
			connection = getConnection();
			st = connection.prepareStatement(query);
			st.setInt(1, Integer.parseInt(userId));
			rs = st.executeQuery();
			if (rs.next()) {
				settings = new CalendarImportSettings();
				settings.setUserId(rs.getInt("userId"));
				settings.setHostName(rs.getString("hostname"));
				settings.setSynchroType(rs.getInt("synchroType"));
				settings.setSynchroDelay(rs.getInt("synchroDelay"));
				settings.setUrlIcalendar(rs.getString("url"));
				settings.setLoginIcalendar(rs.getString("remoteLogin"));
				settings.setPwdIcalendar(rs.getString("remotePwd"));
				settings.setCharset(rs.getString("charset"));
			}
		} catch (Exception e) {
			SilverTrace.error("agenda", "CalendarImportSettingsDaoJdbc",
					"agenda.EX_CANT_GET_USER_SETTINGS", e);
		} finally {
			DBUtil.close(rs, st);
			close(connection);
		}

		return settings;
	}

	/**
	 * Save synchronisation user settings
	 * 
	 * @param settings
	 *            CalendarImportSettings object containing user settings
	 * @see com.stratelia.webactiv.agenda.model.CalendarImportSettings
	 */
	public void saveUserSettings(CalendarImportSettings settings) 
		throws AgendaException {
		Connection connection = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		String insertStatement = "insert into sb_agenda_import_settings (userId, hostName, synchroType, synchroDelay, url, remoteLogin, remotePwd, charset) values (?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			connection = getConnection();
			st = connection.prepareStatement(insertStatement);
			st.setInt(1, settings.getUserId());
			st.setString(2, settings.getHostName());
			st.setInt(3, settings.getSynchroType());
			st.setInt(4, settings.getSynchroDelay());
			st.setString(5, settings.getUrlIcalendar());
			st.setString(6, settings.getLoginIcalendar());
			st.setString(7, settings.getPwdIcalendar());
			st.setString(8, settings.getCharset());
			st.executeUpdate();
		} catch (Exception e) {
			throw new AgendaException("CalendarImportSettingsDaoJdbc.saveUserSettings", SilverpeasException.ERROR, "agenda.EX_CANT_SAVE_USER_SETTINGS", "user id = " + settings.getUserId(), e);
		} finally {
			DBUtil.close(rs, st);
			close(connection);
		}
	}

	/**
	 * Update synchronisation user settings
	 * @param 	settings 	CalendarImportSettings object containing user settings
	 * @see com.stratelia.webactiv.agenda.model.CalendarImportSettings
	 */
	public void updateUserSettings(CalendarImportSettings settings) 
		throws AgendaException {
		Connection connection = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		String updateStatement = "update sb_agenda_import_settings set hostName = ?, synchroType = ?, synchroDelay = ?, url= ?, remoteLogin= ?, remotePwd= ?, charset= ? where userId = ?";

		try {
			connection = getConnection();
			st = connection.prepareStatement(updateStatement);
			st.setString(1, settings.getHostName());
			st.setInt(2, settings.getSynchroType());
			st.setInt(3, settings.getSynchroDelay());
			st.setString(4, settings.getUrlIcalendar());
			st.setString(5, settings.getLoginIcalendar());
			st.setString(6, settings.getPwdIcalendar());
			st.setString(7, settings.getCharset());
			st.setInt(8, settings.getUserId());
			st.executeUpdate();
		} catch (Exception e) {
			throw new AgendaException("CalendarImportSettingsDaoJdbc.updateUserSettings", SilverpeasException.ERROR, "agenda.EX_CANT_UPDATE_USER_SETTINGS", "user id = " + settings.getUserId(), e);
		} finally {
			DBUtil.close(rs, st);
			close(connection);
		}
	}

	// Fermeture des ressources
	protected void close(Connection connection) {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
			}
	}

	// Récupération de la connection
	private Connection getConnection() throws Exception {
		return DBUtil.makeConnection(JNDINames.DATABASE_DATASOURCE);
	}

}
