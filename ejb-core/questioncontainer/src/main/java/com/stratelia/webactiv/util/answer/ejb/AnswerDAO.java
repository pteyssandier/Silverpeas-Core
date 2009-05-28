//TODO : reporter dans CVS (done)
package com.stratelia.webactiv.util.answer.ejb;

import java.sql.*;
import java.util.*;

import com.silverpeas.util.ForeignPK;
import com.stratelia.webactiv.util.*;
import com.stratelia.webactiv.util.exception.*;
import com.stratelia.webactiv.util.answer.model.*;

import com.stratelia.silverpeas.silvertrace.*;

/**
 * This class is made to access database only (table SB_Question_Answer)
 * 
 * @author neysseri
 */
public class AnswerDAO
{

	public static final String ANSWERCOLUMNNAMES =
		"answerId, questionId, answerLabel, answerNbPoints, answerIsSolution, answerComment, answerNbVoters, answerIsOpened, answerImage, answerQuestionLink";

	/**
	 * Build an Answer objet with data containing in the Resulset
	 *
	 * @param rs		the Resultset which contains data from database
	 * @param answerPK	an AnswerPK to know context (spaceId, componentId)
	 *
	 * @return an Answer
	 *
	 * @throws SQLException
	 *
	 */
	private static Answer getAnswerFromResultSet(ResultSet rs, AnswerPK answerPK) throws SQLException
	{
		String id = rs.getString(1);
		String questionId = rs.getString(2);
		String label = rs.getString(3);
		int nbPoints = rs.getInt(4);
		boolean isSolution = false;

		if (rs.getInt(5) != 0)
			isSolution = true;

		String comment = rs.getString(6);
		int nbVoters = rs.getInt(7);
		boolean isOpened = false;

		if (rs.getInt(8) != 0)
			isOpened = true;

		String image = rs.getString(9);
		String questionLink = rs.getString(10);
		Answer result =
			new Answer(
				new AnswerPK(id, answerPK),
				new ForeignPK(questionId, answerPK),
				label,
				nbPoints,
				isSolution,
				comment,
				nbVoters,
				isOpened,
				image,
				questionLink);

		return result;
	}

	/**
	 * Get answers which composed the question
	 *
	 * @param con			the Connection
	 * @param questionPK	the QuestionPK (question id)
	 *
	 * @return a Collection of Answer
	 *
	 * @throws SQLException
	 *
	 */
	public static Collection getAnswersByQuestionPK(Connection con, ForeignPK questionPK) throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.getAnswersByQuestionPK()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK =" + questionPK);
		ResultSet rs = null;
		Answer answer = null;
		AnswerPK answerPK = new AnswerPK(null, questionPK);

		String selectStatement =
			"select " + ANSWERCOLUMNNAMES + " from " + answerPK.getTableName() + " where questionId = ? order by answerId ";

		ArrayList result = new ArrayList();
		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setInt(1, new Integer(questionPK.getId()).intValue());
			rs = prepStmt.executeQuery();

			while (rs.next())
			{
				answer = getAnswerFromResultSet(rs, answerPK);
				result.add(answer);
			}
		}
		finally
		{
			DBUtil.close(rs, prepStmt);
		}

		return result;
	}

	/**
	 * Record that the answer (answerPK) has been chosen to the question (questionPK)
	 *
	 * @param con			the Connection
	 * @param questionPK	the QuestionPK (question id)
	 * @param answerPK		the AnswerPK (answer id)
	 *
	 * @throws SQLException
	 *
	 */
	public static void recordThisAnswerAsVote(Connection con, ForeignPK questionPK, AnswerPK answerPK)
		throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.recordThisAnswerAsVote()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK =" + questionPK + ", answerPK =" + answerPK);
		String updateStatement =
			"update "
				+ answerPK.getTableName()
				+ " set answerNbVoters = answerNbVoters + 1"
				+ " where answerId = ? "
				+ " and questionId = ?";

		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(updateStatement);
			prepStmt.setInt(1, new Integer(answerPK.getId()).intValue());
			prepStmt.setInt(2, new Integer(questionPK.getId()).intValue());
			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

	/**
	 * Add some answers to a question
	 *
	 * @param con			the Connection
	 * @param answers		a Collection of Answer
	 * @param questionPK	the QuestionPK (question id)
	 *
	 * @throws SQLException
	 *
	 */
	public static void addAnswersToAQuestion(Connection con, Collection answers, ForeignPK questionPK)
		throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.addAnswersToAQuestion()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK =" + questionPK);
		if (answers != null)
		{
			Iterator it = answers.iterator();

			while (it.hasNext())
			{
				Answer answer = (Answer) it.next();
				addAnswerToAQuestion(con, answer, questionPK);
			}
		}
	}

	/**
	 * Add an answer to a question
	 *
	 * @param con			the Connection
	 * @param answer		the Answer
	 * @param questionPK	the QuestionPK (question id)
	 *
	 * @throws SQLException
	 *
	 */
	public static void addAnswerToAQuestion(Connection con, Answer answer, ForeignPK questionPK) throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.addAnswerToAQuestion()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK =" + questionPK);
		int newId = 0;

		AnswerPK answerPK = new AnswerPK("unknown", questionPK);

		String updateStatement = "insert into " + answerPK.getTableName() + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try
		{
			/* Recherche de la nouvelle PK de la table */
			newId = DBUtil.getNextId(answerPK.getTableName(), new String("answerId"));
		}
		catch (Exception e)
		{
			throw new AnswerRuntimeException(
				"AnswerDAO.addAnswerToAQuestion()",
				SilverpeasRuntimeException.ERROR,
				"root.EX_GET_NEXTID_FAILED",
				e);
		}

		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(updateStatement);
			prepStmt.setInt(1, newId);
			prepStmt.setInt(2, new Integer(questionPK.getId()).intValue());
			prepStmt.setString(3, answer.getLabel());
			prepStmt.setInt(4, answer.getNbPoints());
			if (answer.isSolution())
				prepStmt.setInt(5, 1);
			else
				prepStmt.setInt(5, 0);
			prepStmt.setString(6, answer.getComment());
			prepStmt.setInt(7, 0);
			if (answer.isOpened())
				prepStmt.setInt(8, 1);
			else
				prepStmt.setInt(8, 0);
			prepStmt.setString(9, answer.getImage());
			prepStmt.setString(10, answer.getQuestionLink());
			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

	/**
	 * Delete all answers to a given question
	 *
	 * @param con			the Connection
	 * @param questionPK	the QuestionPK (question id)
	 *
	 * @throws SQLException
	 *
	 */
	public static void deleteAnswersToAQuestion(Connection con, ForeignPK questionPK) throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.deleteAnswersToAQuestion()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK =" + questionPK);
		AnswerPK answerPK = new AnswerPK("unknown", questionPK);

		String deleteStatement = "delete from " + answerPK.getTableName() + " where questionId = ? ";

		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(deleteStatement);
			prepStmt.setInt(1, new Integer(questionPK.getId()).intValue());
			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

	/**
	 * Delete an answer to a question
	 *
	 * @param con			the Connection
	 * @param questionPK	the QuestionPK (question id)
	 * @param answerId		the answer id
	 *
	 * @throws SQLException
	 *
	 */
	public static void deleteAnswerToAQuestion(Connection con, ForeignPK questionPK, String answerId)
		throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.deleteAnswerToAQuestion()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK = " + questionPK + ", answerId = " + answerId);
		AnswerPK answerPK = new AnswerPK("unknown", questionPK);

		String deleteStatement = "delete from " + answerPK.getTableName() + " where questionId = ? and answerId = ? ";

		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(deleteStatement);
			prepStmt.setInt(1, new Integer(questionPK.getId()).intValue());
			prepStmt.setInt(2, new Integer(answerId).intValue());
			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

	/**
	 * Update an answer to a question
	 *
	 * @param con			the Connection
	 * @param questionPK	the QuestionPK (question id)
	 * @param answer		the Answer
	 *
	 * @throws SQLException
	 *
	 */
	public static void updateAnswerToAQuestion(Connection con, ForeignPK questionPK, Answer answer)
		throws SQLException
	{
		SilverTrace.info(
			"answer",
			"AnswerDAO.updateAnswerToAQuestion()",
			"root.MSG_GEN_ENTER_METHOD",
			"questionPK = " + questionPK + ", answer = " + answer.toString());
		AnswerPK answerPK = answer.getPK();

		String updateStatement =
			"update "
				+ answerPK.getTableName()
				+ " set questionId = ?,"
				+ " set answerLabel = ?,"
				+ " set answerNbPoints = ?,"
				+ " set answerIsSolution = ?,"
				+ " set answerComment = ?,"
				+ " set answerNbVoters = ?,"
				+ " set answerIsOpened = ?,"
				+ " set answerImage = ?,"
				+ " set answerQuestionLink = ?,"
				+ " where answerId = ? ";

		PreparedStatement prepStmt = null;

		try
		{
			prepStmt = con.prepareStatement(updateStatement);
			prepStmt.setInt(1, new Integer(questionPK.getId()).intValue());
			prepStmt.setString(2, answer.getLabel());
			prepStmt.setInt(3, answer.getNbPoints());

			if (answer.isSolution())
				prepStmt.setInt(4, 1);
			else
				prepStmt.setInt(4, 0);

			prepStmt.setString(5, answer.getComment());
			prepStmt.setInt(6, answer.getNbVoters());

			if (answer.isOpened())
				prepStmt.setInt(7, 1);
			else
				prepStmt.setInt(7, 0);

			prepStmt.setString(8, answer.getImage());
			prepStmt.setString(9, answer.getQuestionLink());
			prepStmt.setString(10, answer.getPK().getId());

			prepStmt.executeUpdate();
		}
		finally
		{
			DBUtil.close(prepStmt);
		}
	}

}