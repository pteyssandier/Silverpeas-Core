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

import com.silverpeas.util.FileUtil;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Vector;

import javax.ejb.EJBException;

import com.stratelia.silverpeas.scheduler.SchedulerEvent;
import com.stratelia.silverpeas.scheduler.SchedulerEventHandler;
import com.stratelia.silverpeas.scheduler.SchedulerException;
import com.stratelia.silverpeas.scheduler.SchedulerJob;
import com.stratelia.silverpeas.scheduler.SimpleScheduler;
import com.stratelia.silverpeas.silverstatistics.model.SilverStatisticsConfigException;
import com.stratelia.silverpeas.silverstatistics.model.StatisticsConfig;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.EJBUtilitaire;
import com.stratelia.webactiv.util.JNDINames;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * SilverStatisticsManager is the tool used in silverpeas to compute statistics for connexions,
 * files size and components access. This is a singleton class.
 * @author Marc Guillemin
 */
public class SilverStatisticsManager implements SchedulerEventHandler {
  // Global constants

  // Local constants
  private static final String STAT_SIZE_JOB_NAME = "SilverStatisticsSize";
  private static final String STAT_CUMUL_JOB_NAME = "SilverStatisticsCumul";
  private static final String STAT_VOLUME_JOB_NAME = "SilverStatisticsVolume";
  // Class variables
  // Singleton implementation
  private static SilverStatisticsManager myInstance = null;
  // Object variables
  // List of directory to compute size
  private Vector directoryToScan = null;
  private SilverStatistics silverStatistics = null;
  private StatisticsConfig statsConfig = null;

  /**
   * Prevent the class from being instantiate (private)
   */
  private SilverStatisticsManager() {
  }

  /**
   * Init attributes
   */
  private void initSilverStatisticsManager() {

    // init List
    directoryToScan = new Vector();

    try {
      statsConfig = new StatisticsConfig();

      try {
        statsConfig.init();
        if (!statsConfig.isValidConfigFile()) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.initSilverStatistics",
              "silverstatistics.MSG_CONFIG_FILE");
        }
      } catch (SilverStatisticsConfigException e) {
        SilverTrace.error("silverstatistics",
            "SilverStatisticsManager.initSilverStatistics",
            "silverstatistics.MSG_CONFIG_FILE", e);
      }

      // init userSessionTimeout and scheduledSessionManagementTimeStamp
      ResourceBundle resources = FileUtil.loadBundle(
          "com.stratelia.silverpeas.silverstatistics.SilverStatistics", Locale.getDefault());

      initSchedulerStatistics(resources.getString("scheduledGetStatVolumeTimeStamp"), STAT_VOLUME_JOB_NAME,
          "doGetStatVolume");
      initSchedulerStatistics(resources.getString("scheduledGetStatSizeTimeStamp"), STAT_SIZE_JOB_NAME,
          "doGetStatSize");
      initSchedulerStatistics(resources.getString("scheduledCumulStatTimeStamp"), STAT_CUMUL_JOB_NAME,
          "doCumulStat");

      initDirectoryToScan(resources);

    } catch (Exception ex) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsManager.initSilverStatistics",
          "root.EX_CLASS_NOT_INITIALIZED", ex);
    }
  }

  /**
   * SilverStatisticsManager is a singleton
   */
  public static SilverStatisticsManager getInstance() {
    if (myInstance == null) {
      myInstance = new SilverStatisticsManager();
      // Init ONLY when myIntance is not null
      myInstance.initSilverStatisticsManager();
    }

    return myInstance;
  }

  /**
   * Method declaration
   * @param aCronString
   * @param jobName
   * @param methodeName
   * @throws SchedulerException
   * @see
   */
  public void initSchedulerStatistics(String aCronString, String jobName,
      String methodeName) throws SchedulerException {
    SimpleScheduler.removeJob(myInstance, jobName);
    SilverTrace.info("silverstatistics", "SilverStatisticsManager.initSchedulerStatistics",
        "root.MSG_GEN_PARAM_VALUE", "jobName=" + jobName + ", aCronString=" + aCronString);
    SimpleScheduler.getJob(myInstance, jobName, aCronString, myInstance, methodeName);
  }

  /**
   * Method declaration
   * @param currentDate
   * @see
   */
  public void doGetStatVolume(Date currentDate) {
    SilverTrace.debug("silverstatistics", "SilverStatisticsManager.doGetStatVolume",
        "currentDate=" + currentDate);
    try {
      getSilverStatistics().makeVolumeAlimentationForAllComponents();
    } catch (Exception ex) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsManager.doGetStatVolume", "root.EX_NO_MESSAGE", ex);
    }

  }

  /**
   * Method declaration
   * @param currentDate
   * @see
   */
  public void doGetStatSize(Date currentDate) {
    try {
      // Pour chaque repertoire racine à parcourir
      // Calculer récursivement la taille des fichiers
      // Ajouter la taille du répertoire en base

      for (int i = 0; i < directoryToScan.size(); i++) {
        // Notify statistics
        addStatSize(currentDate, (String) directoryToScan.get(i),
            directorySize((String) directoryToScan.get(i)));
      }
    } catch (Exception ex) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsManager.doGetStatSize", "root.EX_NO_MESSAGE", ex);
    }
  }

  /**
   * Method declaration
   * @param currentDate
   * @see
   */
  public void doCumulStat(Date currentDate) {
    SilverTrace.debug("silverstatistics",
        "SilverStatisticsManager.doCumulStat", "currentDate=" + currentDate);
    try {
      getSilverStatistics().makeStatAllCumul();
    } catch (Exception ex) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsManager.doCumulStat", "root.EX_NO_MESSAGE", ex);
    }
  }

  /**
   * Method declaration
   * @param resources
   * @see
   */
  private void initDirectoryToScan(java.util.ResourceBundle resource) {

    int i;
    String directoryPath;
    File dir;

    try {
      // read the directories
      // --------------------------
      i = 0;
      directoryPath = resource.getString("SilverPeasDataPath"
          + Integer.toString(i));
      // for each directory
      while (directoryPath != null) {

        // Test existence
        dir = new File(directoryPath);
        if (!dir.isDirectory()) {
          throw new Exception("silverstatistics initDirectoryToScan"
              + directoryPath);
        }

        directoryToScan.add(directoryPath);
        i++;

        try {
          directoryPath = resource.getString("SilverPeasDataPath"
              + Integer.toString(i));
        } catch (MissingResourceException ex) {
          directoryPath = null;
        }
      }

    } catch (Exception e) {
      SilverTrace.error("silverstatistics",
          "SilverStatisticsManager.initDirectoryToScan()",
          "silvertrace.ERR_INIT_APPENDER_FROM_PROP", e);
    }
  }

  /**
   * Scheduler Event handler
   */
  public void handleSchedulerEvent(SchedulerEvent aEvent) {

    switch (aEvent.getType()) {
      case SchedulerEvent.EXECUTION_NOT_SUCCESSFULL:
        SilverTrace.error("silverstatistics",
            "SilverStatisticsManager.handleSchedulerEvent", "The job '"
            + aEvent.getJob().getJobName() + "' was not successfull");
        break;
      case SchedulerEvent.EXECUTION_SUCCESSFULL:
        SilverTrace.debug("silverstatistics",
            "SilverStatisticsManager.handleSchedulerEvent", "The job '"
            + aEvent.getJob().getJobName() + "' was successfull");
        break;
      default:
        SilverTrace.error("silverstatistics",
            "SilverStatisticsManager.handleSchedulerEvent",
            "Illegal event type");
        break;
    }
  }

  /*
   * private static String getSoIdinDigitOfComponent(String value) { String myString = value;
   * boolean ok = false; int i = 0; while (!ok && (i!=value.length())) { try {
   * myString=value.substring(i, value.length()); i++; Integer.parseInt(myString); ok = true; }
   * catch (NumberFormatException e) { ok = false; } } return myString; }
   */
  /**
   * Method declaration
   * @param userId
   * @param volume
   * @param dateAccess
   * @param peasType
   * @param spaceId
   * @param componentId
   * @see
   */
  public void addStatVolume(String userId, int volume, Date dateAccess,
      String peasType, String spaceId, String componentId) {
    // should feed Volume (see SilverStatistics.properties)
    if (statsConfig.isRun("Volume")) {
      SilverTrace.debug("silverstatistics", "SilverStatistics.addStatVolume",
          " peasType=" + peasType + " spaceId=" + spaceId + " componentId="
          + componentId);
      // creation du stringbuffer correspondant au type Volume du
      // silverstatistics.properties
      StringBuffer stat = new StringBuffer();

      stat.append(SilverStatisticsConstants.DATE_FORMAT.format(dateAccess));
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(userId); // userId
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(peasType);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(spaceId);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(componentId);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append((new Integer(volume)).toString()); // countVolume

      // asynchrone : utilisation d'une queue avec jms
      if (isAsynchronStats("Volume")) {
        try {
          SilverStatisticsSender mySilverStatisticsSender = new SilverStatisticsSender();
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatVolume",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          mySilverStatisticsSender.send("Volume"
              + SilverStatisticsConstants.SEPARATOR + stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatVolume", "after send");
          mySilverStatisticsSender.close();
        } catch (Exception e) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatVolume",
              "SilverStatisticsSender ", e);
        }
      } // synchrone = appel d'une methode direct de l'ejb SilverStatisticsEJB
      else {
        try {
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatVolume",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          getSilverStatistics().putStats("Volume", stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatVolume", "after putStats");
        } catch (RemoteException ex) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatVolume", "impossible de trouver "
              + JNDINames.SILVERSTATISTICS_EJBHOME, ex);
        }
      }
    }
  }

  /**
   * Method declaration
   * @param userId
   * @param dateAccess
   * @param peasType
   * @param spaceId
   * @param componentId
   * @see
   */
  public void addStatAccess(String userId, Date dateAccess, String peasType,
      String spaceId, String componentId) {
    // should feed Access (see SilverStatistics.properties)
    if (statsConfig.isRun("Access")) {
      SilverTrace.debug("silverstatistics", "SilverStatistics.addStatAccess",
          " peasType=" + peasType + " spaceId=" + spaceId + " componentId="
          + componentId);
      // creation du stringbuffer correspondant au type Acces du
      // silverstatistics.properties
      StringBuffer stat = new StringBuffer();

      stat.append(SilverStatisticsConstants.DATE_FORMAT.format(dateAccess));
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(userId); // userId
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(peasType);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(spaceId);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(componentId);
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append("1"); // countAccess

      // asynchrone : utilisation d'une queue avec jms
      if (isAsynchronStats("Access")) {
        try {
          SilverStatisticsSender mySilverStatisticsSender = new SilverStatisticsSender();
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatAccess",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          mySilverStatisticsSender.send("Access"
              + SilverStatisticsConstants.SEPARATOR + stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatAccess", "after send");
          mySilverStatisticsSender.close();
        } catch (Exception e) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatAccess",
              "SilverStatisticsSender ", e);
        }
      } // synchrone = appel d'une methode direct de l'ejb SilverStatisticsEJB
      else {
        try {
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatAccess",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          getSilverStatistics().putStats("Access", stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatAccess", "after putStats");
        } catch (RemoteException ex) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatAccess", "impossible de trouver "
              + JNDINames.SILVERSTATISTICS_EJBHOME, ex);
        }
      }
    }
  }

  /**
   * Method declaration
   * @param userId
   * @param dateConnection
   * @param count
   * @param duration
   * @see
   */
  public void addStatConnection(String userId, Date dateConnection, int count,
      long duration) {
    // should feed connexion (see SilverStatistics.properties)
    if (statsConfig.isRun("Connexion")) {
      SilverTrace.debug("silverstatistics",
          "SilverStatistics.addStatConnection", " userId=" + userId + " count="
          + count);
      // creation du stringbuffer correspondant au type Acces du
      // silverstatistics.properties

      StringBuffer stat = new StringBuffer();
      stat.append(SilverStatisticsConstants.DATE_FORMAT.format(dateConnection)); // date
      // connexion
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(userId); // userId
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(Long.toString(count)); // countConnection
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(Long.toString(duration)); // duration

      // asynchrone : utilisation d'une queue avec jms
      if (isAsynchronStats("Connexion")) {
        try {
          SilverStatisticsSender mySilverStatisticsSender = new SilverStatisticsSender();
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatConnection",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          mySilverStatisticsSender.send("Connexion"
              + SilverStatisticsConstants.SEPARATOR + stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatConnection", "after send");
          mySilverStatisticsSender.close();
        } catch (Exception e) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatConnection",
              "SilverStatisticsSender ", e);
        }
      } // synchrone = appel d'une methode direct de l'ejb SilverStatisticsEJB
      else {
        try {
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatConnection",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          getSilverStatistics().putStats("Connexion", stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatConnection", "after putStats");
        } catch (RemoteException ex) {
          SilverTrace.error(
              "silverstatistics",
              "SilverStatisticsManager.addStatConnection",
              "impossible de trouver " + JNDINames.SILVERSTATISTICS_EJBHOME,
              ex);
        }
      }
    }
  }

  /**
   * Method declaration
   * @param date
   * @param dirName
   * @param dirSize
   * @see
   */
  public void addStatSize(Date date, String dirName, long dirSize) {
    // should feed Size (see SilverStatistics.properties)
    if (statsConfig.isRun("Size")) {
      SilverTrace.debug("silverstatistics", "SilverStatistics.addStatSize",
          "dirName=" + dirName + " dirSize=" + dirSize);
      // creation du stringbuffer correspondant au type Acces du
      // silverstatistics.properties
      StringBuffer stat = new StringBuffer();

      stat.append(SilverStatisticsConstants.DATE_FORMAT.format(date));
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(dirName); // directoryName
      stat.append(SilverStatisticsConstants.SEPARATOR);
      stat.append(Long.toString(dirSize)); // directorySize

      // asynchrone : utilisation d'une queue avec jms
      if (isAsynchronStats("Size")) {
        try {
          SilverStatisticsSender mySilverStatisticsSender = new SilverStatisticsSender();
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatSize",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          mySilverStatisticsSender.send("Size"
              + SilverStatisticsConstants.SEPARATOR + stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatSize", "after send");
          mySilverStatisticsSender.close();
        } catch (Exception e) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatSize", "SilverStatisticsSender ",
              e);
        }
      } // synchrone = appel d'une methode direct de l'ejb SilverStatisticsEJB
      else {
        try {
          SilverTrace.info("silverstatistics",
              "SilverStatisticsManager.addStatSize",
              "root.MSG_GEN_PARAM_VALUE", "stat=" + stat.toString());
          getSilverStatistics().putStats("Size", stat.toString());
          SilverTrace.debug("silverstatistics",
              "SilverStatisticsManager.addStatSize", "after putStats");
        } catch (RemoteException ex) {
          SilverTrace.error("silverstatistics",
              "SilverStatisticsManager.addStatSize", "impossible de trouver "
              + JNDINames.SILVERSTATISTICS_EJBHOME, ex);
        }
      }
    }
  }

  /*
   * private boolean isRun(String typeStats) { boolean valueReturn = false; try { if (statsConfig ==
   * null) { statsConfig = new StatisticsConfig(); statsConfig.init(); } if
   * (statsConfig.isValidConfigFile()) { valueReturn = statsConfig.isRun(typeStats); } } catch
   * (SilverStatisticsConfigException e) { SilverTrace.error("silverstatistics",
   * "SilverStatisticsManager.isRun", "silverstatistics.MSG_CONFIG_FILE", e); } return valueReturn;
   * }
   */
  private boolean isAsynchronStats(String typeStats) {
    boolean valueReturn = false;

    try {
      if (statsConfig == null) {
        statsConfig = new StatisticsConfig();

        statsConfig.init();
      }
      if (statsConfig.isValidConfigFile()) {
        valueReturn = statsConfig.isAsynchron(typeStats);
      }
    } catch (SilverStatisticsConfigException e) {
      SilverTrace.error("silverstatistics", "SilverStatisticsManager.isRun",
          "silverstatistics.MSG_CONFIG_FILE", e);
    }

    return valueReturn;
  }

  private SilverStatistics getSilverStatistics() {
    if (silverStatistics == null) {
      try {
        SilverStatisticsHome silverStatisticsHome = (SilverStatisticsHome) EJBUtilitaire.getEJBObjectRef(JNDINames.SILVERSTATISTICS_EJBHOME,
            SilverStatisticsHome.class);

        silverStatistics = silverStatisticsHome.create();
      } catch (Exception e) {
        throw new EJBException(e.getMessage());
      }
    }
    return silverStatistics;
  }

  /**
   * Method declaration
   * @param directoryName
   * @return
   * @see
   */
  private long directorySize(String directoryName) {
    SilverTrace.debug("silverstatistics",
        "SilverStatisticsManager.directorySize", "directoryName="
        + directoryName);
    File file = new File(directoryName);

    if ((file != null) && (file.isDirectory())) {
      return returnSize(file);
    } else {
      return -1;
    }
  }

  /**
   * Method declaration
   * @param file
   * @return
   * @see
   */
  private long returnSize(File file) {

    if (file.isFile()) {
      return file.length();
    } else {
      File fDirContent[];

      fDirContent = file.listFiles();

      long fileslength = 0;

      for (int i = 0; i < fDirContent.length; i++) {
        if (fDirContent[i].isFile()) {
          fileslength = fileslength + fDirContent[i].length();
        } else {
          fileslength = fileslength + returnSize(fDirContent[i]);
        }
      }
      return fileslength;
    }
  }
}
