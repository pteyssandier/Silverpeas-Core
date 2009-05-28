/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

//
// -- Java Code Generation Process -

package com.stratelia.webactiv.util.score.control;

// Import Statements
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.SessionContext;

import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;
import com.stratelia.webactiv.util.score.ejb.ScoreDAO;
import com.stratelia.webactiv.util.score.model.ScoreDetail;
import com.stratelia.webactiv.util.score.model.ScorePK;
import com.stratelia.webactiv.util.score.model.ScoreRuntimeException;

/*
 * CVS Informations
 *
 * $Id: ScoreBmEJB.java,v 1.2 2008/05/28 08:35:59 ehugonnet Exp $
 *
 * $Log: ScoreBmEJB.java,v $
 * Revision 1.2  2008/05/28 08:35:59  ehugonnet
 * Import inutile
 *
 * Revision 1.1.1.1  2002/08/06 14:47:53  nchaix
 * no message
 *
 * Revision 1.13  2001/12/21 13:51:04  scotte
 * no message
 *
 */

/**
 * Class declaration
 *
 *
 * @author
 */
public class ScoreBmEJB implements javax.ejb.SessionBean, ScoreBmSkeleton
{

    private String dbName = JNDINames.SCORE_DATASOURCE;

    /*
     * Method: Default Constructor
     */

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public ScoreBmEJB() {}

    /*
     * Method:  ejbCreate
     */

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbCreate() {}

    /*
     * Method:  ejbRemove
     */

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbRemove() {}

    /*
     * Method:  ejbActivate
     */

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbActivate() {}

    /*
     * Method:  ejbPassivate
     */

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void ejbPassivate() {}

    /*
     * Method:  setSessionContext
     */

    /**
     * Method declaration
     *
     *
     * @param sc
     *
     * @see
     */
    public void setSessionContext(SessionContext sc) {}

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    private Connection getConnection()
    {
        try
        {
            Connection con = DBUtil.makeConnection(dbName);

            return con;
        }
        catch (Exception re)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getConnection()", SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_OPEN_FAILED", re);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param con
     *
     * @see
     */
    private void freeConnection(Connection con)
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (SQLException re)
            {
                throw new ScoreRuntimeException("ScoreBmEJB.closeConnection()", SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_CLOSE_FAILED", re);
            }
        }
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     * @param userId
     *
     * @return
     *
     * @see
     */
    public int getUserNbParticipationsByFatherId(ScorePK scorePK, String fatherId, String userId)
    {
        Connection con = null;
        int        userNbParticipations = 0;

        try
        {
            con = getConnection();
            userNbParticipations = ScoreDAO.getUserNbParticipationsByFatherId(con, scorePK, fatherId, userId);
        }
        catch (Exception re)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getUserNbParticipationsByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_USER_NB_PARTICIPATION_FAILED", re);
        }
        finally
        {
            freeConnection(con);
        }
        return userNbParticipations;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     * @param userId
     * @param participationId
     *
     * @return
     *
     * @see
     */
    private int getUserPositionByFatherIdAndParticipationId(ScorePK scorePK, String fatherId, String userId, int participationId)
    {
        Connection con = null;
        int        userPosition = 0;

        try
        {
            con = getConnection();
            userPosition = ScoreDAO.getUserPositionByFatherIdAndParticipationId(con, scorePK, fatherId, userId, participationId);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getUserPositionByFatherIdAndParticipationId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_USER_POSITION_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return userPosition;
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetails
     *
     * @see
     */
    private void setPositions(Collection scoreDetails)
    {
        Iterator it = scoreDetails.iterator();

        while (it.hasNext())
        {
            ScoreDetail scoreDetail = (ScoreDetail) it.next();

            scoreDetail.setPosition(getUserPositionByFatherIdAndParticipationId(scoreDetail.getScorePK(), scoreDetail.getFatherId(), scoreDetail.getUserId(), scoreDetail.getParticipationId()));
        }
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetails
     *
     * @see
     */
    private void setParticipations(Collection scoreDetails)
    {
        Iterator it = scoreDetails.iterator();

        while (it.hasNext())
        {
            ScoreDetail scoreDetail = (ScoreDetail) it.next();

            scoreDetail.setNbParticipations(getUserNbParticipationsByFatherId(scoreDetail.getScorePK(), scoreDetail.getFatherId(), scoreDetail.getUserId()));
        }
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetail
     *
     * @see
     */
    private void setPosition(ScoreDetail scoreDetail)
    {
        scoreDetail.setPosition(getUserPositionByFatherIdAndParticipationId(scoreDetail.getScorePK(), scoreDetail.getFatherId(), scoreDetail.getUserId(), scoreDetail.getParticipationId()));
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetail
     *
     * @see
     */
    private void setNbParticipation(ScoreDetail scoreDetail)
    {
        scoreDetail.setNbParticipations(getUserNbParticipationsByFatherId(scoreDetail.getScorePK(), scoreDetail.getFatherId(), scoreDetail.getUserId()));
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetail
     *
     * @see
     */
    public void addScore(ScoreDetail scoreDetail)
    {
        Connection con = null;

        try
        {
            con = getConnection();
            ScoreDAO.addScore(con, scoreDetail);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.addScore()", SilverpeasRuntimeException.ERROR, "score.EX_CREATE_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param scoreDetail
     *
     * @see
     */
    public void updateScore(ScoreDetail scoreDetail)
    {
        Connection con = null;

        try
        {
            con = getConnection();
            ScoreDAO.updateScore(con, scoreDetail);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.updateScore()", SilverpeasRuntimeException.ERROR, "score.EX_UPDATE_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     *
     * @see
     */
    public void deleteScore(ScorePK scorePK)
    {
        Connection con = null;

        try
        {
            con = getConnection();
            ScoreDAO.deleteScore(con, scorePK);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.deleteScore()", SilverpeasRuntimeException.ERROR, "score.EX_DELETE_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    // Begin Modif une table par instance by hguig

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     *
     * @see
     */
    public void deleteScoreByFatherPK(ScorePK scorePK, String fatherId)
    {
        Connection con = null;

        try
        {
            con = getConnection();
            ScoreDAO.deleteScoreByFatherPK(con, scorePK, fatherId);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.deleteScoreByFatherPK()", SilverpeasRuntimeException.ERROR, "score.EX_DELETE_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
    }

    // End Modif

    /**
     * Method declaration
     *
     *
     * @param scorePK
     *
     * @return
     *
     * @see
     */
    public Collection getAllScores(ScorePK scorePK)
    {
        Connection con = null;
        Collection allScores = null;

        try
        {
            con = getConnection();
            allScores = ScoreDAO.getAllScores(con, scorePK);
            setParticipations(allScores);
            setPositions(allScores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getAllScores()", SilverpeasRuntimeException.ERROR, "score.EX_GET_ALL_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return allScores;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param userId
     *
     * @return
     *
     * @see
     */
    public Collection getUserScores(ScorePK scorePK, String userId)
    {
        Connection con = null;
        Collection userScores = null;

        try
        {
            con = getConnection();
            userScores = ScoreDAO.getUserScores(con, scorePK, userId);
            setParticipations(userScores);
            setPositions(userScores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getUserScores()", SilverpeasRuntimeException.ERROR, "score.EX_GET_USER_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return userScores;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     * @param userId
     *
     * @return
     *
     * @see
     */
    public Collection getUserScoresByFatherId(ScorePK scorePK, String fatherId, String userId)
    {
        Connection con = null;
        Collection userScores = null;

        try
        {
            con = getConnection();
            userScores = ScoreDAO.getUserScoresByFatherId(con, scorePK, fatherId, userId);
            setParticipations(userScores);
            setPositions(userScores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getUserScoresByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_PARTICIPATION_USER_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return userScores;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param nbBestScores
     * @param fatherId
     *
     * @return
     *
     * @see
     */
    public Collection getBestScoresByFatherId(ScorePK scorePK, int nbBestScores, String fatherId)
    {
        Connection con = null;
        Collection bestScores = null;

        try
        {
            con = getConnection();
            bestScores = ScoreDAO.getBestScoresByFatherId(con, scorePK, nbBestScores, fatherId);
            setParticipations(bestScores);
            setPositions(bestScores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getBestScoresByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_BEST_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return bestScores;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param nbWorstScores
     * @param fatherId
     *
     * @return
     *
     * @see
     */
    public Collection getWorstScoresByFatherId(ScorePK scorePK, int nbWorstScores, String fatherId)
    {
        Connection con = null;
        Collection worstScores = null;

        try
        {
            con = getConnection();
            worstScores = ScoreDAO.getWorstScoresByFatherId(con, scorePK, nbWorstScores, fatherId);
            setParticipations(worstScores);
            setPositions(worstScores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getWorstScoresByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_WORST_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return worstScores;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     *
     * @return
     *
     * @see
     */
    public int getNbVotersByFatherId(ScorePK scorePK, String fatherId)
    {
        Connection con = null;
        int        nbVoters = 0;

        try
        {
            con = getConnection();
            nbVoters = ScoreDAO.getNbVotersByFatherId(con, scorePK, fatherId);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getNbVotersByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_NB_PLAYERS_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return nbVoters;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     *
     * @return
     *
     * @see
     */
    public float getAverageScoreByFatherId(ScorePK scorePK, String fatherId)
    {
        Connection con = null;
        float      averageScore = 0;

        try
        {
            con = getConnection();
            averageScore = ScoreDAO.getAverageScoreByFatherId(con, scorePK, fatherId);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getAverageScoreByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_AVERAGE_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return averageScore;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     * @param userId
     * @param participationId
     *
     * @return
     *
     * @see
     */
    public ScoreDetail getUserScoreByFatherIdAndParticipationId(ScorePK scorePK, String fatherId, String userId, int participationId)
    {
        Connection  con = null;
        ScoreDetail scoreDetail = null;

        try
        {
            con = getConnection();
            scoreDetail = ScoreDAO.getUserScoreByFatherIdAndParticipationId(con, scorePK, fatherId, userId, participationId);
            setNbParticipation(scoreDetail);
            setPosition(scoreDetail);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getUserScoreByFatherIdAndParticipationId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_USER_SCORE_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return scoreDetail;
    }

    /**
     * Method declaration
     *
     *
     * @param scorePK
     * @param fatherId
     *
     * @return
     *
     * @see
     */
    public Collection getScoresByFatherId(ScorePK scorePK, String fatherId)
    {
        Connection con = null;
        Collection scores = null;

        try
        {
            con = getConnection();
            scores = ScoreDAO.getScoresByFatherId(con, scorePK, fatherId);
            setParticipations(scores);
            setPositions(scores);
        }
        catch (Exception e)
        {
            throw new ScoreRuntimeException("ScoreBmEJB.getScoresByFatherId()", SilverpeasRuntimeException.ERROR, "score.EX_GET_SCORES_FAILED", e);
        }
        finally
        {
            freeConnection(con);
        }
        return scores;
    }

}
