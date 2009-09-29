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

package com.stratelia.silverpeas.notificationserver.channel.silvermail;

import java.util.Date;

/**
 * Titre : Description : Copyright : Copyright (c) 2001 Soci�t� :
 * 
 * @author eDurand
 * @version 1.0
 */

public class SILVERMAILMessage {

  /**
   * Constructor declaration
   * 
   * 
   * @see
   */
  public SILVERMAILMessage() {
  }

  private long m_Id;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setId(long value) {
    m_Id = value;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public long getId() {
    return m_Id;
  }

  private long userId = -1;

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public long getUserId() {
    return userId;
  }

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setUserId(long value) {
    userId = value;
  }

  private String m_UserLogin;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setUserLogin(String value) {
    m_UserLogin = value;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getUserLogin() {
    return m_UserLogin;
  }

  private String m_SenderName;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setSenderName(String value) {
    m_SenderName = value;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getSenderName() {
    return m_SenderName;
  }

  private String m_Subject;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setSubject(String value) {
    m_Subject = value;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getSubject() {
    return m_Subject;
  }

  private String m_Body;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setBody(String value) {
    m_Body = value;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getBody() {
    return m_Body;
  }

  private String m_Url;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setUrl(String url) {
    m_Url = url;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getUrl() {
    return m_Url;
  }

  private String m_Source;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setSource(String source) {
    m_Source = source;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public String getSource() {
    return m_Source;
  }

  private Date m_Date;

  /**
   * Method declaration
   * 
   * 
   * @param value
   * 
   * @see
   */
  public void setDate(Date date) {
    m_Date = date;
  }

  /**
   * Method declaration
   * 
   * 
   * @return
   * 
   * @see
   */
  public Date getDate() {
    return m_Date;
  }

  private int m_readen = 0;

  public void setReaden(int readen) {
    m_readen = readen;
  }

  public int getReaden() {
    return m_readen;
  }

}