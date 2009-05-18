package com.stratelia.silverpeas.notificationManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.UserDetail;

public class NotificationMetaData implements java.io.Serializable {
  private int messageType;
  private Date date;
  private String sender;
  private String source;
  private String link;
  private String sessionId;
  private Collection userRecipients;
  private Collection groupRecipients;
  private Connection connection; // usefull to send notification from ejb part
  private String componentId;
  private boolean isAnswerAllowed = false;

  private Hashtable titles = new Hashtable();
  private Hashtable contents = new Hashtable();

  /**
   * Default Constructor
   */
  public NotificationMetaData() {
    reset();
  }

  /**
   * Most common used constructor
   *
   * @param messageType
   *          message type (NORMAL, URGENT, ...)
   * @param title
   *          message title (=subject)
   * @param content
   *          message content (=body)
   */
  public NotificationMetaData(int messageType, String title, String content) {
    reset();
    this.messageType = messageType;

    addLanguage("fr", title, content);
  }

  /**
   * reset all attributes
   */
  private void reset() {
    messageType = NotificationParameters.NORMAL;
    date = new Date();
    sender = "";
    source = "";
    link = "";
    sessionId = "";
    userRecipients = new ArrayList();
    groupRecipients = new ArrayList();
    connection = null;
    componentId = "";
    isAnswerAllowed = false;
  }

  public void addLanguage(String language, String title, String content) {
    titles.put(language, title);
    contents.put(language, content);
  }

  public Set getLanguages() {
    return titles.keySet();
  }

  /**
   * Set message type
   *
   * @param messageType
   *          the message type to be set
   */
  public void setMessageType(int messageType) {
    this.messageType = messageType;
  }

  /**
   * Get message type
   *
   * @return the message type
   */
  public int getMessageType() {
    return messageType;
  }

  /**
   * Set message date
   *
   * @param date
   *          the message date to be set
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Get message date
   *
   * @return the message date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Set message title
   *
   * @param title
   *          the title to be set
   */
  public void setTitle(String title) {
    // this.title = title;
    titles.put("fr", title);
  }

  public void setTitle(String title, String language) {
    titles.put(language, title);
  }

  /**
   * Get message title
   *
   * @return the message title
   */
  public String getTitle() {
    // return title;
    return (String) titles.get("fr");
  }

  public String getTitle(String language) {
    return (String) titles.get(language);
  }

  /**
   * Set message content
   *
   * @param content
   *          the content to be set
   */
  public void setContent(String content) {
    // this.content = content;
    contents.put("fr", content);
  }

  public void setContent(String content, String language) {
    SilverTrace.info("notificationManager",
        "NotificationMetaData.setContent()", "root.MSG_GEN_ENTER_METHOD",
        "language = " + language + ", content = " + content);
    contents.put(language, content);
  }

  /**
   * Get message content
   *
   * @return the message content
   */
  public String getContent() {
    // return content;
    return (String) getContent("fr");
  }

  public String getContent(String language) {
    SilverTrace.info("notificationManager",
        "NotificationMetaData.getContent()", "root.MSG_GEN_ENTER_METHOD",
        "language = " + language);
    String result = (String) contents.get(language);
    SilverTrace.info("notificationManager",
        "NotificationMetaData.getContent()", "root.MSG_GEN_EXIT_METHOD",
        "result = " + result);
    return result;
  }

  /**
   * Set message source
   *
   * @param source
   *          the source to be set
   */
  public void setSource(String source) {
    this.source = source;
  }

  /**
   * Get message source
   *
   * @return the message source
   */
  public String getSource() {
    return source;
  }

  /**
   * Set message sender
   *
   * @param sender
   *          the sender to be set
   */
  public void setSender(String sender) {
    this.sender = sender;
  }

  /**
   * Set answer allowed
   *
   * @param if
   *          answer allowed
   */
  public void setAnswerAllowed(boolean answerAllowed) {
    this.isAnswerAllowed = answerAllowed;
  }

  /**
   * Get message sender
   *
   * @return the message sender
   */
  public String getSender() {
    return sender;
  }

  /**
   * Get answer allowed
   *
   * @return if answer is allowed
   */
  public boolean isAnswerAllowed() {
    return isAnswerAllowed;
  }

  /**
   * Set message link
   *
   * @param link
   *          the link to be set
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Get message link
   *
   * @return the message link
   */
  public String getLink() {
    return link;
  }

  /**
   * Set message session Id
   *
   * @param sessionId
   *          the session Id to be set
   */
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  /**
   * Get message session Id
   *
   * @return the message session Id
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Set message user recipients
   *
   * @param userRecipients
   *          the user ids that must receive this message
   */
  public void setUserRecipients(Collection userRecipients) {
    this.userRecipients = userRecipients;
  }

  /**
   * Get message user recipients
   *
   * @return the message user recipients
   */
  public Collection getUserRecipients() {
    return userRecipients;
  }

  /**
   * Add a user recipient to user recipients
   *
   * @param userId
   *          id of user that must be added
   */
  public void addUserRecipient(String userId) {
    userRecipients.add(userId);
  }

  /**
   * Add a user recipient to user recipients
   *
   * @param userIds
   *          user ids to be added as an array of String
   */
  public void addUserRecipients(String[] userIds) {
	  if (userIds != null)
		  userRecipients.addAll(Arrays.asList(userIds));
  }

  /**
   * Add a user recipient to user recipients
   *
   * @param users
   *          users to be added as an array of UserDetail objects
   */
  public void addUserRecipients(UserDetail[] users) {
    for (int i = 0; users != null && i < users.length; i++) {
      if (users[i] != null)
        userRecipients.add(users[i].getId());
    }
  }

  /**
   * Set message group recipients
   *
   * @param groupRecipients
   *          the group ids that must receive this message
   */
  public void setGroupRecipients(Collection groupRecipients) {
    this.groupRecipients = groupRecipients;
  }

  /**
   * Get message group recipients
   *
   * @return the message group recipients
   */
  public Collection getGroupRecipients() {
    return groupRecipients;
  }

  /**
   * Add a group recipient to group recipients
   *
   * @param groupId
   *          id of group that must be added
   */
  public void addGroupRecipient(String groupId) {
    groupRecipients.add(groupId);
  }

  /**
   * Add group recipients to group recipients
   *
   * @param groupIds
   *          group ids to be added as an array of String
   */
  public void addGroupRecipients(String[] groupIds) {
	  if (groupIds != null)
		  groupRecipients.addAll(Arrays.asList(groupIds));
  }

  public void setConnection(Connection con) {
    this.connection = con;
  }

  public Connection getConnection() {
    return this.connection;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }
}