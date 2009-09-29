package com.stratelia.silverpeas.notificationserver.channel.popup;

/*
 * SilverMessageFactory.java
 *
 * Created on 30 august 2001
 *
 * Author D.Blot
 */

import java.util.*;
import com.stratelia.webactiv.persistence.*;
import com.stratelia.webactiv.util.DateUtil;

import com.stratelia.silverpeas.notificationserver.NotificationData;
import com.stratelia.silverpeas.silvertrace.*;
import com.stratelia.silverpeas.util.LongText;

/**
 * 
 * @author dblot
 * @version
 */
public class SilverMessageFactory {

  /**
   * --------------------------------------------------------------------------
   * constructor constructor
   */
  public SilverMessageFactory() {
  }

  /**
   * --------------------------------------------------------------------------
   * pop read
   */
  public static SilverMessage read(String p_UserId) {
    SilverMessage silverMessage = null;
    String msg;
    Collection collectionPopupMessage = null;
    SilverpeasBeanDAO dao;
    IdPK pk = new IdPK();
    String whereClause = null;
    POPUPMessageBean pmb;

    whereClause = " ID IN ( SELECT MIN(ID) FROM ST_PopupMessage WHERE USERID="
        + p_UserId + " )";

    try {
      // find all message to display
      dao = SilverpeasBeanDAOFactory
          .getDAO("com.stratelia.silverpeas.notificationserver.channel.popup.POPUPMessageBean");
      collectionPopupMessage = dao.findByWhereClause(pk, whereClause);
      // if any
      if (collectionPopupMessage.isEmpty() == false) {
        // get the only one
        pmb = (POPUPMessageBean) (collectionPopupMessage.toArray()[0]);
        try {
          // CBO : ADD
          String body = pmb.getBody();
          if (body.startsWith("COMMUNICATION")) {
            body = body.substring(13);
          }
          // CBO : FIN ADD

          // CBO : UPDATE
          // int longTextId = Integer.parseInt(pmb.getBody());
          int longTextId = Integer.parseInt(body);

          msg = LongText.getLongText(longTextId);
        } catch (Exception e) {
          SilverTrace.debug("popup", "SilverMessageFactory.read()",
              "PB converting body id to LongText", "Message Body = "
                  + pmb.getBody());
          msg = pmb.getBody();
        }
        if (msg != null) {
          // CBO : UPDATE
          // silverMessage = new SilverMessage ("ALERT");
          if (pmb.getBody().startsWith("COMMUNICATION")) {
            silverMessage = new SilverMessage("COMMUNICATION");
          } else {
            silverMessage = new SilverMessage("ALERT");
          }
          // CBO : FIN UPDATE

          silverMessage.setContent(msg);
          silverMessage.setID(pmb.getPK().getId());
          silverMessage.setSenderId(pmb.getSenderId());
          silverMessage.setSenderName(pmb.getSenderName());
          silverMessage.setAnswerAllowed(pmb._getAnswerAllowed());
        }
      }
    } catch (PersistenceException e) {
      SilverTrace.error("popup", "SilverMessageFactory.read()",
          "popup.EX_CANT_READ_MSG", "UserId=" + p_UserId, e);
    }

    return silverMessage;
  }

  /**
   * --------------------------------------------------------------------------
   * pop del
   */
  public static void del(String p_MsgId) {
    try {
      SilverpeasBeanDAO dao;
      IdPK pk = new IdPK();

      dao = SilverpeasBeanDAOFactory
          .getDAO("com.stratelia.silverpeas.notificationserver.channel.popup.POPUPMessageBean");
      pk.setId(p_MsgId);
      try {
        POPUPMessageBean toDel = (POPUPMessageBean) dao.findByPrimaryKey(pk);
        int longTextId = -1;

        // CBO : UPDATE
        // longTextId = Integer.parseInt(toDel.getBody());
        if (toDel.getBody().startsWith("COMMUNICATION")) {
          longTextId = Integer.parseInt(toDel.getBody().substring(13));
        } else {
          longTextId = Integer.parseInt(toDel.getBody());
        }
        // CBO : FIN UPDATE
        LongText.removeLongText(longTextId);
      } catch (Exception e) {
        SilverTrace.debug("popup", "SilverMessageFactory.del()",
            "PB converting body id to LongText", "Message Body = " + p_MsgId);
      }
      dao.remove(pk);
    } catch (PersistenceException e) {
      SilverTrace.error("popup", "SilverMessageFactory.del()",
          "popup.EX_CANT_DEL_MSG", "MsgId=" + p_MsgId, e);
    }
  }

  /**
   * --------------------------------------------------------------------------
   * push
   */
  public static void push(String p_UserId, NotificationData p_Message) {
    SilverpeasBeanDAO dao;
    POPUPMessageBean pmb = new POPUPMessageBean();
    try {
      SilverTrace.debug("popup", "SilverMessageFactory.push()", "Message = "
          + p_Message.toString());
      SilverTrace.debug("popup", "SilverMessageFactory.push()",
          "Message.isAnswerAllowed = " + p_Message.isAnswerAllowed());
      dao = SilverpeasBeanDAOFactory
          .getDAO("com.stratelia.silverpeas.notificationserver.channel.popup.POPUPMessageBean");
      pmb.setUserId(Long.parseLong(p_UserId));

      // CBO : UPDATE
      // pmb.setBody(
      // Integer.toString(LongText.addLongText(p_Message.getMessage())) );
      if ("COMMUNICATION".equals(p_Message.getComment())) {
        pmb.setBody(p_Message.getComment()
            + Integer.toString(LongText.addLongText(p_Message.getMessage())));
      } else {
        pmb.setBody(Integer.toString(LongText.addLongText(p_Message
            .getMessage())));
      }
      // CBO : FIN UPDATE

      pmb.setSenderId(p_Message.getSenderId());
      pmb.setSenderName(p_Message.getSenderName());
      pmb.setAnswerAllowed(p_Message.isAnswerAllowed());
      pmb.setMsgDate(DateUtil.date2SQLDate(new Date()));
      pmb.setMsgTime(DateUtil.getFormattedTime(new Date()));
      dao.add(pmb);
    } catch (Exception e) {
      SilverTrace.error("popup", "SilverMessageFactory.push()",
          "popup.EX_CANT_PUSH_MSG", "UserId=" + p_UserId + ";Msg=" + p_Message,
          e);
    }
  }

}