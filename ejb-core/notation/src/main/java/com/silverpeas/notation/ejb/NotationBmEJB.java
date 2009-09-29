package com.silverpeas.notation.ejb;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.silverpeas.notation.model.Notation;
import com.silverpeas.notation.model.NotationDAO;
import com.silverpeas.notation.model.NotationDetail;
import com.silverpeas.notation.model.NotationPK;
import com.silverpeas.notation.model.comparator.NotationDetailComparator;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasRuntimeException;

public class NotationBmEJB implements SessionBean {

  public void updateNotation(NotationPK pk, int note) throws RemoteException {
    Connection con = openConnection();
    try {
      if (hasUserNotation(pk)) {
        NotationDAO.updateNotation(con, pk, note);
      } else {
        NotationDAO.createNotation(con, pk, note);
      }
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.updateNotation()",
          SilverpeasRuntimeException.ERROR,
          "notation.CREATING_NOTATION_FAILED", e);
    } finally {
      closeConnection(con);
    }
  }

  public void deleteNotation(NotationPK pk) throws RemoteException {
    Connection con = openConnection();
    try {
      NotationDAO.deleteNotation(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.deleteNotation()",
          SilverpeasRuntimeException.ERROR, "notation.DELETE_NOTATION_FAILED",
          e);
    } finally {
      closeConnection(con);
    }
  }

  public NotationDetail getNotation(NotationPK pk) throws RemoteException {
    NotationDetail notationDetail = new NotationDetail(pk);
    Collection notations = null;
    Connection con = openConnection();
    try {
      notations = NotationDAO.getNotations(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.getNotation()",
          SilverpeasRuntimeException.ERROR, "notation.GET_NOTE_FAILED", e);
    } finally {
      closeConnection(con);
    }

    String userId = pk.getUserId();
    int notesCount = 0;
    float globalNote = 0;
    int userNote = 0;
    if (notations != null && !notations.isEmpty()) {
      notesCount = notations.size();
      Iterator iter = notations.iterator();
      Notation notation;
      float sum = 0;
      while (iter.hasNext()) {
        notation = (Notation) iter.next();
        if (userId != null && userId.equals(notation.getAuthor())) {
          userNote = notation.getNote();
        }
        sum += notation.getNote();
      }
      globalNote = sum / notesCount;
    }
    notationDetail.setNotesCount(notesCount);
    notationDetail.setGlobalNote(globalNote);
    notationDetail.setUserNote(userNote);
    return notationDetail;
  }

  public int countNotations(NotationPK pk) throws RemoteException {
    Connection con = openConnection();
    try {
      return NotationDAO.countNotations(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.countNotations()",
          SilverpeasRuntimeException.ERROR, "notation.COUNT_NOTATIONS_FAILED",
          e);
    } finally {
      closeConnection(con);
    }
  }

  public boolean hasUserNotation(NotationPK pk) throws RemoteException {
    Connection con = openConnection();
    try {
      return NotationDAO.hasUserNotation(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.hasUserNotation()",
          SilverpeasRuntimeException.ERROR,
          "notation.HAS_USER_NOTATION_FAILED", e);
    } finally {
      closeConnection(con);
    }
  }

  public Collection getBestNotations(NotationPK pk, int notationsCount)
      throws RemoteException {
    Connection con = openConnection();
    Collection notationPKs = null;
    try {
      notationPKs = NotationDAO.getNotationPKs(con, pk);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.hasUserNotation()",
          SilverpeasRuntimeException.ERROR,
          "notation.HAS_USER_NOTATION_FAILED", e);
    } finally {
      closeConnection(con);
    }
    return getBestNotations(notationPKs, notationsCount);
  }

  public Collection getBestNotations(Collection pks, int notationsCount)
      throws RemoteException {
    ArrayList notations = new ArrayList();
    if (pks != null && !pks.isEmpty()) {
      Iterator iter = pks.iterator();
      while (iter.hasNext()) {
        notations.add(getNotation((NotationPK) iter.next()));
      }
      Collections.sort(notations, new NotationDetailComparator());
      if (notations.size() > notationsCount) {
        return notations.subList(0, notationsCount);
      }
    }
    return notations;
  }

  private Connection openConnection() {
    try {
      return DBUtil.makeConnection(JNDINames.NODE_DATASOURCE);
    } catch (Exception e) {
      throw new NotationRuntimeException("NotationBmEJB.getConnection()",
          SilverpeasRuntimeException.ERROR, "root.EX_CONNECTION_OPEN_FAILED", e);
    }
  }

  private void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
      } catch (Exception e) {
        SilverTrace.error("notation", "NotationBmEJB.closeConnection()",
            "root.EX_CONNECTION_CLOSE_FAILED", "", e);
      }
    }
  }

  public void ejbCreate() throws CreateException {
  }

  public void ejbActivate() {
  }

  public void ejbPassivate() {
  }

  public void ejbRemove() {
  }

  public void setSessionContext(SessionContext sc) {
  }

}