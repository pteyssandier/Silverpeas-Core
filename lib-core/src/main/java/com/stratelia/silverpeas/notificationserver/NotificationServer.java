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
/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.silverpeas.notificationserver;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.stratelia.webactiv.util.JNDINames;
import com.stratelia.webactiv.util.exception.SilverpeasException;

public class NotificationServer {
  private String m_JmsFactory = JNDINames.JMS_FACTORY;
  private String m_JmsQueue = JNDINames.JMS_QUEUE;
  private String m_JmsHeaderChannel = JNDINames.JMS_HEADER_CHANNEL;
  private Hashtable m_JmsHeaders;

  /**
   * Constructor declaration
   * @see
   */
  public NotificationServer() {
    m_JmsHeaders = new Hashtable();
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // getNotificationPendingCount

  /**
   * Description of the method. Method name should begin with a lower case.
   * @author Name (Name of the method's creator) facultatif
   * @see ClassName (Link to a related class name) facultatif
   * @see ClassName#member (Link to a related class member) facultatif
   * @see functionName (Link to a related function)facultatif
   * @version Text (Information version) facultatif
   * @param pValue Description (parameter name should be prefixed by "p").
   * @return Description
   * @exception MyException * Description
   */
  public long addNotification(NotificationData pData)
      throws NotificationServerException {
    long notificationid = 0; // a gerer plus tard (necessite une database)
    String notificationAsXML;

    m_JmsHeaders.clear();
    m_JmsHeaders.put(m_JmsHeaderChannel, pData.getTargetChannel());

    pData.setNotificationId(notificationid);

    notificationAsXML = NotificationServerUtil
        .convertNotificationDataToXML(pData);
    try {
      jmsSendToQueue(notificationAsXML, m_JmsFactory, m_JmsQueue, m_JmsHeaders);
    } catch (Exception e) {
      throw new NotificationServerException(
          "NotificationServer.addNotification()", SilverpeasException.ERROR,
          "notificationServer.EX_CANT_SEND_TO_JSM_QUEUE", notificationAsXML, e);
    }
    return notificationid;
  }

  /**
   * Send the NotificationMessage in a JMS Queue
   */
  private static void jmsSendToQueue(String notificationMessage,
      String jmsFactory, String jmsQueue, Hashtable p_JmsHeaders)
      throws JMSException, NamingException, IOException {
    QueueConnectionFactory qconFactory;
    QueueConnection qcon;
    QueueSession qsession;
    QueueSender qsender;
    Queue queue;

    InitialContext ic = new InitialContext();

    qconFactory = (QueueConnectionFactory) ic.lookup(jmsFactory);
    qcon = qconFactory.createQueueConnection();
    qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
    queue = (Queue) ic.lookup(jmsQueue);
    qsender = qsession.createSender(queue);

    TextMessage textMsg;

    textMsg = qsession.createTextMessage();
    qcon.start();

    // Add notificationMessage as message
    textMsg.setText(notificationMessage);
    // Add property
    for (Enumeration e = p_JmsHeaders.keys(); e.hasMoreElements();) {
      Object key = e.nextElement();

      textMsg.setStringProperty((String) key, (String) p_JmsHeaders
          .get((String) key));
    }

    qsender.send(textMsg);

    qsender.close();
    qsession.close();
    qcon.close();
  }

}
