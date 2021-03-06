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

package com.stratelia.silverpeas.silvertrace;

import com.silverpeas.util.FileUtil;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.net.SMTPAppender;

/**
 * SilverLog is the trace tool used in silverpeas to log the users' connexions. This is a 'fully'
 * static class. All functions could be called directly and is thread-safe. The log function is :
 * log.
 * @author Marc Guillemin
 */
public class SilverLog {

  /**
   * Used in setTraceLevel to reset a level trace
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_UNKNOWN = 0x00000000;

  /**
   * Debug-level traces
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_DEBUG = 0x00000001;

  /**
   * Info-level traces
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_INFO = 0x00000002;

  /**
   * Warning-level traces
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_WARN = 0x00000003;

  /**
   * Error-level traces
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_ERROR = 0x00000004;

  /**
   * Fatal-level traces
   * @see #setTraceLevel
   * @see #getTraceLevel
   */
  public static final int TRACE_LEVEL_FATAL = 0x00000005;

  /**
   * Appender sending informations on console
   * @see #addAppenderConsole
   * @see #removeAppender
   */
  public static final int APPENDER_CONSOLE = 0x00000001;

  /**
   * Appender sending informations on file
   * @see #addAppenderFile
   * @see #removeAppender
   */
  public static final int APPENDER_FILE = 0x00000002;

  /**
   * Appender sending informations on rolling file
   * @see #addAppenderRollingFile
   * @see #removeAppender
   * @see #ROLLING_MODE_MOUNTH
   * @see #ROLLING_MODE_WEEK
   * @see #ROLLING_MODE_DAILY
   * @see #ROLLING_MODE_HOUR
   */
  public static final int APPENDER_ROLLING_FILE = 0x00000004;

  /**
   * Appender sending informations mail
   * @see #addAppenderMail
   * @see #removeAppender
   */
  public static final int APPENDER_MAIL = 0x00000008;

  /**
   * Used to remove all appenders attached to this module
   * @see #removeAppender
   */
  public static final int APPENDER_ALL = 0xFFFFFFFF;

  /**
   * HTML layout : Display "Time / Thread / Priority / Category / Message" into a TABLE
   * @see #addAppenderConsole
   * @see #addAppenderFile
   * @see #addAppenderRollingFile
   * @see #addAppenderMail
   */
  public static String LAYOUT_HTML = "LAYOUT_HTML";

  /**
   * Short layout : Display "Time / Priority / Message"
   * @see #addAppenderConsole
   * @see #addAppenderFile
   * @see #addAppenderRollingFile
   * @see #addAppenderMail
   */
  public static String LAYOUT_SHORT = "LAYOUT_SHORT";

  /**
   * Detailed layout : Display "Time / Priority / Calling Class and module / Message"
   * @see #addAppenderConsole
   * @see #addAppenderFile
   * @see #addAppenderRollingFile
   * @see #addAppenderMail
   */
  public static String LAYOUT_DETAILED = "LAYOUT_DETAILED";

  /**
   * Fully detailed layout : Display
   * "Tic count / Time / Priority / Thread / Calling Class and module / Message"
   * @see #addAppenderConsole
   * @see #addAppenderFile
   * @see #addAppenderRollingFile
   * @see #addAppenderMail
   */
  public static String LAYOUT_FULL_DEBUG = "LAYOUT_FULL_DEBUG";

  /**
   * The trace file will be copied every 1st day of a mounth with the name :
   * FileName.ext.year-mounth A new file named FileName.ext is the created and will contains the
   * next mounth's traces Example : MyFile.txt.2001-07
   * @see #addAppenderRollingFile
   */
  public static String ROLLING_MODE_MOUNTH = "'.'yyyy-MM";

  /**
   * The trace file will be copied every 1st day of a week with the name : FileName.ext.year-week A
   * new file named FileName.ext is the created and will contains the next week's traces Example :
   * MyFile.txt.2001-34
   * @see #addAppenderRollingFile
   */
  public static String ROLLING_MODE_WEEK = "'.'yyyy-ww";

  /**
   * The trace file will be copied every day at midnight with the name :
   * FileName.ext.year-mounth-day A new file named FileName.ext is the created and will contains the
   * next day's traces Example : MyFile.txt.2001-07-23
   * @see #addAppenderRollingFile
   */
  public static String ROLLING_MODE_DAILY = "'.'yyyy-MM-dd";

  /**
   * The trace file will be copied every hour with the name : FileName.ext.year-mounth-day-hour A
   * new file named FileName.ext is the created and will contains the next hour's traces Example :
   * MyFile.txt.2001-07-23-18
   * @see #addAppenderRollingFile
   */
  public static String ROLLING_MODE_HOUR = "'.'yyyy-MM-dd-HH";

  protected static Category logCategory = null;
  // Directory to the log files
  protected static String logDir = null;
  protected static String logModule = null;

  // Layouts
  protected static String layoutShort = "%-5p : %m%n";
  // protected static String layoutDetailed =
  // "%d{dd/MM/yy-HH:mm:SSS} - %-5p : %m%n";
  protected static String layoutDetailed = "%d{dd/MM/yy-HH:mm:SSS},%m%n";
  protected static String layoutFullDebug =
      "%-15.15r [%-26.26t] - %d{dd/MM/yy-HH:mm:SSS} - %-5p : %m%n";

  // Initialisation
  static {
    initAll();
    SilverTrace.info("silvertrace", "SilverLog.static",
        "silvertrace.MSG_END_OF_INIT");
  }

  /**
   * Log connexion informations.
   * @param message login/logout
   * @param adresseIP of the connected client
   * @param userLogin of the connected client
   */
  static public void logConnexion(String message, String adresseIP,
      String userLogin) {
    try {
      if (logCategory != null) {
        if (logCategory.isEnabledFor(Priority.DEBUG)) {
          logCategory.debug(formatTraceMessage(message, adresseIP, userLogin));
        }
      }
    } catch (RuntimeException e) {
      SilverTrace.error("silvertrace", "SilverLog.logConnexion()",
          "silvertrace.ERR_RUNTIME_ERROR_OCCUR", e);
    }
  }

  /**
   * Init all appenders
   */
  static protected void initAll() {
    ResourceBundle resource = null;

    try {
      resource = FileUtil.loadBundle("com.stratelia.silverpeas.silvertrace.settings.silverLog",
          new Locale("", ""));
      logDir = resource.getString("LogDir");

      if (logCategory != null) {
        logCategory.getRoot().getLoggerRepository().resetConfiguration();
        // logCategory.getRoot().getHierarchy().resetConfiguration();
      }

      logCategory = Category.getInstance(resource.getString("module.path"));
      logModule = resource.getString("module.name");

      initFromBundle(resource);

    } catch (Exception e) {
      SilverTrace.error("silvertrace", "SilverLog.resetAll()",
          "silvertrace.ERR_INIT_TRACE", e);
    }
  }

  /**
   * Loads the configuration from the resource given in argument.
   * @param fileProperties the properties to merge with the current configuration
   */
  static public void initFromBundle(ResourceBundle resource) {

    int i;
    String appenderTypeStr;
    int appenderTypeInt;
    boolean appenderEnabled;

    try {
      // read the appenders
      // --------------------------
      i = 0;
      appenderTypeStr = resource.getString("appender" + Integer.toString(i)
          + ".type");
      // for each appenders
      while (appenderTypeStr != null) {
        // Translate the appender type from the string to the internal constant
        if (appenderTypeStr.equalsIgnoreCase("APPENDER_CONSOLE")) {
          appenderTypeInt = APPENDER_CONSOLE;
        } else if (appenderTypeStr.equalsIgnoreCase("APPENDER_FILE")) {
          appenderTypeInt = APPENDER_FILE;
        } else if (appenderTypeStr.equalsIgnoreCase("APPENDER_ROLLING_FILE")) {
          appenderTypeInt = APPENDER_ROLLING_FILE;
        } else if (appenderTypeStr.equalsIgnoreCase("APPENDER_MAIL")) {
          appenderTypeInt = APPENDER_MAIL;
        } else {
          appenderTypeInt = APPENDER_ALL;
        }
        appenderEnabled = MsgTrace.getBooleanProperty(resource, "appender"
            + Integer.toString(i) + ".enabled", true);
        if ((appenderTypeInt != APPENDER_ALL) && appenderEnabled) {
          // Create the appender and attach it to his module
          addAppenderFromBundle(resource, i, appenderTypeInt);
        }
        i++;

        try {
          appenderTypeStr = resource.getString("appender" + Integer.toString(i)
              + ".type");
        } catch (MissingResourceException ex) {
          appenderTypeStr = null;
        }
      }

    } catch (Exception e) {
      SilverTrace.error("silvertrace", "SilverLog.resetAll()",
          "silvertrace.ERR_INIT_APPENDER_FROM_PROP", e);
    }
  }

  /**
   * Read appender information from a property file and attach it to it's module
   * @param fileProperties
   * @param appenderNumber
   * @param appenderType
   */
  static protected void addAppenderFromBundle(ResourceBundle resource,
      int appenderNumber, int appenderType) {
    String layout;
    String fileName;
    String consoleName;
    boolean append;
    String rollingModeName;
    String rollingMode;
    String mailHost;
    String mailFrom;
    String mailTo;
    String mailSubject;

    // Retrieve the properties of the current appender and call the function
    // that will create and attach it
    //

    layout = resource.getString("appender" + Integer.toString(appenderNumber)
        + ".layout");

    switch (appenderType) {
      case APPENDER_CONSOLE:
        consoleName = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".consoleName");
        addAppenderConsole(layout, consoleName);
        break;
      case APPENDER_FILE:
        fileName = translateFileName(resource.getString("appender"
            + Integer.toString(appenderNumber) + ".fileName"));
        append = MsgTrace.getBooleanProperty(resource, "appender"
            + Integer.toString(appenderNumber) + ".append", true);
        addAppenderFile(layout, fileName, append);
        break;
      case APPENDER_ROLLING_FILE:
        fileName = translateFileName(resource.getString("appender"
            + Integer.toString(appenderNumber) + ".fileName"));
        rollingModeName = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".rollingMode");
        if (rollingModeName == null) {
          rollingMode = ROLLING_MODE_DAILY;
        } else if (rollingModeName.equalsIgnoreCase("ROLLING_MODE_MOUNTH")) {
          rollingMode = ROLLING_MODE_MOUNTH;
        } else if (rollingModeName.equalsIgnoreCase("ROLLING_MODE_WEEK")) {
          rollingMode = ROLLING_MODE_WEEK;
        } else if (rollingModeName.equalsIgnoreCase("ROLLING_MODE_DAILY")) {
          rollingMode = ROLLING_MODE_DAILY;
        } else if (rollingModeName.equalsIgnoreCase("ROLLING_MODE_HOUR")) {
          rollingMode = ROLLING_MODE_HOUR;
        } else if (rollingModeName.length() == 0) {
          rollingMode = ROLLING_MODE_DAILY;
        } else {
          rollingMode = rollingModeName;
        }
        addAppenderRollingFile(layout, fileName, rollingMode);
        break;
      case APPENDER_MAIL:
        mailHost = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".mailHost");
        mailFrom = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".mailFrom");
        mailTo = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".mailTo");
        mailSubject = resource.getString("appender"
            + Integer.toString(appenderNumber) + ".mailSubject");
        addAppenderMail(layout, mailHost, mailFrom, mailTo, mailSubject);
        break;
    }
  }

  /**
   * Add a new console appender. If an appender with the same type have been previously set, delete
   * it and replace it with the new created one.
   * @param patternLayout the things displayed in this appender, could be one of the LAYOUT_...
   * constants
   * @param consoleName Name of the console output. If null or "", "system.out" is used
   */
  static protected void addAppenderConsole(String patternLayout,
      String consoleName) {
    ConsoleAppender a1 = new ConsoleAppender();

    if (logCategory != null) {
      try {
        logCategory.removeAppender(getAppenderName(APPENDER_CONSOLE));
        a1.setName(getAppenderName(APPENDER_CONSOLE));
        a1.setLayout(getLayout(patternLayout));
        if (MsgTrace.stringValid(consoleName)) {
          a1.setTarget(consoleName);
        }
        a1.activateOptions();
        logCategory.addAppender(a1);
      } catch (Exception e) {
        SilverTrace.error("silvertrace", "SilverLog.addAppenderConsole()",
            "silvertrace.ERR_CANT_ADD_APPENDER", "Console " + ","
            + patternLayout + "," + consoleName, e);
      }
    }
  }

  /**
   * Add a new file appender. If an appender with the same type have been previously set, delete it
   * and replace it with the new created one.
   * @param patternLayout the things displayed in this appender, could be one of the LAYOUT_...
   * constants
   * @param fileName full-path name of the file where the trace are written
   * @param appendOnFile true to append at the end of the existing file (if ther is one), false to
   * remove old file before writting
   */
  static protected void addAppenderFile(String patternLayout, String fileName,
      boolean appendOnFile) {
    FileAppender a1 = new FileAppender();

    if (logCategory != null) {
      try {
        logCategory.removeAppender(getAppenderName(APPENDER_FILE));
        a1.setName(getAppenderName(APPENDER_FILE));
        a1.setLayout(getLayout(patternLayout));
        a1.setAppend(appendOnFile);
        a1.setFile(fileName);
        a1.activateOptions();
        logCategory.addAppender(a1);
      } catch (Exception e) {
        SilverTrace.error("silvertrace", "SilverLog.addAppenderFile()",
            "silvertrace.ERR_CANT_ADD_APPENDER", "File " + patternLayout + ","
            + fileName, e);
      }
    }
  }

  /**
   * Add a new rolling file appender. If an appender with the same type have been previously set,
   * delete it and replace it with the new created one.
   * @param patternLayout the things displayed in this appender, could be one of the LAYOUT_...
   * constants
   * @param fileName full-path name of the file where the trace are written
   * @param rollingMode frequency of the rolling file, could be one of the ROLLING_MODE_...
   * constants
   */
  static protected void addAppenderRollingFile(String patternLayout,
      String fileName, String rollingMode) {
    if (logCategory != null) {
      try {
        DailyRollingFileAppender a1 = new DailyRollingFileAppender(
            getLayout(patternLayout), fileName, rollingMode);

        logCategory.removeAppender(getAppenderName(APPENDER_ROLLING_FILE));
        a1.setName(getAppenderName(APPENDER_ROLLING_FILE));
        logCategory.addAppender(a1);
      } catch (Exception e) {
        SilverTrace.error("silvertrace", "SilverLog.addAppenderRollingFile()",
            "silvertrace.ERR_CANT_ADD_APPENDER", "RollingFile " + ","
            + patternLayout + "," + fileName, e);
      }
    }
  }

  /**
   * Add a new mail appender. If an appender with the same type have been previously set, delete it
   * and replace it with the new created one. How it works : mails are only sent when an ERROR or
   * FATAL occur. The mail contains the error and the 512 last traces taken into account (ie, higher
   * than the trace level).
   * @param patternLayout the things displayed in this appender, could be one of the LAYOUT_...
   * constants
   * @param mailHost host name
   * @param mailFrom email of the sender
   * @param mailTo target email, could be multiple targets separeted with comas
   * @param mailSubject subject of the mail
   */
  static protected void addAppenderMail(String patternLayout, String mailHost,
      String mailFrom, String mailTo, String mailSubject) {
    SMTPAppender a1 = new SMTPAppender();

    if (logCategory != null) {
      try {
        logCategory.removeAppender(getAppenderName(APPENDER_MAIL));
        a1.setName(getAppenderName(APPENDER_MAIL));
        a1.setLayout(getLayout(patternLayout));
        a1.setSMTPHost(mailHost);
        a1.setFrom(mailFrom);
        a1.setTo(mailTo);
        a1.setSubject(mailSubject);
        a1.activateOptions();
        logCategory.addAppender(a1);
      } catch (Exception e) {
        SilverTrace.error("silvertrace", "SilverLog.addAppenderMail()",
            "silvertrace.ERR_CANT_ADD_APPENDER", "SMTP " + "," + patternLayout
            + "," + mailHost, e);
      }
    }
  }

  /**
   * Remove appender(s). typeOfAppender could be one value or a mask of multiple appender types
   * @param typeOfAppender could be a mask of APPENDER_... values or APPENDER_ALL to remove all
   * appenders attached to the module
   */
  static protected void removeAppender(int typeOfAppender) {
    if (logCategory != null) {
      if ((typeOfAppender & APPENDER_CONSOLE) == APPENDER_CONSOLE) {
        logCategory.removeAppender(getAppenderName(APPENDER_CONSOLE));
      }
      if ((typeOfAppender & APPENDER_FILE) == APPENDER_FILE) {
        logCategory.removeAppender(getAppenderName(APPENDER_FILE));
      }
      if ((typeOfAppender & APPENDER_ROLLING_FILE) == APPENDER_ROLLING_FILE) {
        logCategory.removeAppender(getAppenderName(APPENDER_ROLLING_FILE));
      }
      if ((typeOfAppender & APPENDER_MAIL) == APPENDER_MAIL) {
        logCategory.removeAppender(getAppenderName(APPENDER_MAIL));
      }
    }
  }

  /**
   * Format the trace message to send to log4j
   * @return the built message
   */
  static protected String formatTraceMessage(String message, String adresseIP,
      String userLogin) {
    StringBuffer valret = new StringBuffer("");

    if (MsgTrace.stringValid(message)) {
      valret.append(message + ",");
    }
    if (MsgTrace.stringValid(adresseIP)) {
      valret.append(adresseIP + ",");
    }
    if (MsgTrace.stringValid(userLogin)) {
      valret.append(userLogin);
    }

    return valret.toString();
  }

  /**
   * Translate the @ErrorDir@ into the real value
   * @param fileName
   */
  static protected String translateFileName(String fileName) {
    String valret = fileName;
    int index;

    if (MsgTrace.stringValid(fileName)) {
      index = fileName.indexOf("@ErrorDir@");
      if (index == 0) {
        valret = logDir + fileName.substring(index + 10, fileName.length());
      } else if (index > 0) {
        valret = fileName.substring(0, index) + logDir
            + fileName.substring(index + 10, fileName.length());
      }
    }
    return valret;
  }

  /**
   * Return the layout object depending on it's name
   * @param patternLayout
   * @return
   */
  static protected Layout getLayout(String patternLayout) {
    if (patternLayout.equalsIgnoreCase(LAYOUT_HTML)) {
      return new HTMLLayout();
    } else if (patternLayout.equalsIgnoreCase(LAYOUT_SHORT)) {
      return new PatternLayout(layoutShort);
    } else if (patternLayout.equalsIgnoreCase(LAYOUT_DETAILED)) {
      return new PatternLayout(layoutDetailed);
    } else if (patternLayout.equalsIgnoreCase(LAYOUT_FULL_DEBUG)) {
      return new PatternLayout(layoutFullDebug);
    } else // Custom pattern layout
    {
      return new PatternLayout(patternLayout);
    }
  }

  /**
   * Return the name of the appender depending on it's attached module and type
   * @param typeOfAppender
   * @return
   */
  static protected String getAppenderName(int typeOfAppender) {
    if ((typeOfAppender & APPENDER_CONSOLE) == APPENDER_CONSOLE) {
      return (logModule + ".ConsoleAppender");
    }
    if ((typeOfAppender & APPENDER_FILE) == APPENDER_FILE) {
      return (logModule + ".FileAppender");
    }
    if ((typeOfAppender & APPENDER_ROLLING_FILE) == APPENDER_ROLLING_FILE) {
      return (logModule + ".DailyRollingFileAppender");
    }
    if ((typeOfAppender & APPENDER_MAIL) == APPENDER_MAIL) {
      return (logModule + ".SMTPAppender");
    }
    return null;
  }

}
