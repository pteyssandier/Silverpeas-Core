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

package com.stratelia.silverpeas.silverstatistics.control;

import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.stratelia.silverpeas.silverstatistics.model.SilverStatisticsConfigException;
import com.stratelia.silverpeas.silverstatistics.model.StatisticsConfig;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;

/**
 * Class declaration
 * @author
 */
public class SilverStatisticsMessageDriven implements MessageDrivenBean, MessageListener {
  private StatisticsConfig myStatsConfig;
  private SilverStatistics silverStatistics = null;
  private MessageDrivenContext ctx;

  /**
   * Method declaration
   * @see
   */
  public void ejbCreate() {
  }

  /**
   * Method declaration
   * @see
   */
  public void ejbRemove() {
  }

  /**
   * Method declaration
   * @param sc
   * @see
   */
  public void setMessageDrivenContext(MessageDrivenContext sc) {
    SilverTrace.info("silverstatistics",
        "SilverStatisticsMessageDriven.setMessageDrivenContext",
        "root.MSG_GEN_PARAM_VALUE", "MessageDrivenContext=" + sc);
    ctx = sc;
    myStatsConfig = new StatisticsConfig();
    try {
      if (myStatsConfig.init() != 0) {
        SilverTrace.error("silverstatistics",
            "SilverStatisticsMessageDriven.setSessionContext",
            "silverstatistics.MSG_CONFIG_FILE");
      }
    } catch (SilverStatisticsConfigException e) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsMessageDriven.setSessionContext",
          "silverstatistics.MSG_CONFIG_FILE", e);
    }
  }

  /**
   * Method declaration
   * @param m
   */
  public void onMessage(Message m) {

    TextMessage tm = (TextMessage) m;
    String msg = "";
    String typeOfStats = "";
    String stat = "";

    try {
      msg = tm.getText();
      SilverTrace.debug("silverstatistics",
          "SilverStatisticsMessageDriven.onMessage",
          "root.MSG_GEN_PARAM_VALUE", "msg=" + msg);

      StringTokenizer stData = new StringTokenizer(msg,
          SilverStatisticsConstants.SEPARATOR);

      if (stData.hasMoreTokens()) {
        typeOfStats = stData.nextToken();
        if (typeOfStats.length() + SilverStatisticsConstants.SEPARATOR.length() < msg
            .length()) {
          stat = msg.substring(typeOfStats.length()
              + SilverStatisticsConstants.SEPARATOR.length(), msg.length());

          try {
            SilverTrace.info("silverstatistics",
                "SilverStatisticsMessageDriven.onMessage",
                "root.MSG_GEN_PARAM_VALUE", "before putStats stat=" + stat);
            getSilverStatistics().putStats(typeOfStats, stat);
            SilverTrace.debug("silverstatistics",
                "SilverStatisticsMessageDriven.onMessage", "after putStats");
          } catch (RemoteException ex) {
            SilverTrace.error("silverstatistics",
                "SilverStatisticsMessageDriven.onMessage",
                "impossible de trouver " + JNDINames.SILVERSTATISTICS_EJBHOME,
                ex);
          }
        } else {
          SilverTrace
              .error("silverstatistics",
              "SilverStatisticsMessageDriven.onMessage", "Mauvais message",
              msg);
        }
      }
    } catch (Exception e) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsMessageDriven.onMessage", "Probleme jms ", e);
    }
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  private SilverStatistics getSilverStatistics() {
    if (silverStatistics == null) {
      try {
        SilverStatisticsHome silverStatisticsHome = (SilverStatisticsHome) EJBUtilitaire
            .getEJBObjectRef(JNDINames.SILVERSTATISTICS_EJBHOME,
            SilverStatisticsHome.class);

        silverStatistics = silverStatisticsHome.create();
      } catch (Exception e) {
        throw new EJBException(e.getMessage());
      }
    }
    return silverStatistics;
  }

}
