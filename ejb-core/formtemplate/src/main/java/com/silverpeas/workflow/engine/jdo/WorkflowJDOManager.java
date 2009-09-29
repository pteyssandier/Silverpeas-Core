package com.silverpeas.workflow.engine.jdo;

import java.io.PrintWriter;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DatabaseNotFoundException;
import org.exolab.castor.jdo.JDOManager;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.mapping.MappingException;

import com.silverpeas.workflow.api.WorkflowException;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.ResourceLocator;

/**
 * This class offers services about database persistence. It uses Castor library
 * to read/write process instance information in database
 */
public class WorkflowJDOManager {
  /**
   * ResourceLocator object to retrieve messages in a properties file
   */
  private static ResourceLocator settings = new ResourceLocator(
      "com.silverpeas.workflow.engine.castorSettings", "fr");

  /**
   * JDO object used by Castor persistence mechanism
   */
  private static JDOManager jdo = null;

  /**
   * Get a connection to database
   * 
   * @return Database object
   */
  static public Database getDatabase() throws WorkflowException {
    return getDatabase(false);
  }

  /**
   * Get a connection to database
   * 
   * @return Database object
   */
  static public Database getDatabase(boolean fast) throws WorkflowException {
    String databaseFileURL;
    String castorLogFileURL;
    String processInstanceDBName;
    PrintWriter writer = null;

    Database db = null;

    if (db == null) {
      if (jdo == null) {
        // get configuration files url
        databaseFileURL = settings.getString("CastorJDODatabaseFileURL");

        castorLogFileURL = settings.getString("CastorJDOLogFileURL");
        processInstanceDBName = settings.getString("ProcessInstanceDBName");

        // Format these url
        databaseFileURL = "file:///" + databaseFileURL.replace('\\', '/');
        castorLogFileURL = castorLogFileURL.replace('\\', '/');

        // Constructs new JDO object
        try {
          JDOManager.loadConfiguration(databaseFileURL);
          jdo = JDOManager.createInstance(processInstanceDBName);
        } catch (MappingException me) {
          throw new WorkflowException("JDOManager.getDatabase",
              "EX_ERR_CASTOR_DATABASE_MAPPING_ERROR", me);
        }
      }

      try {
        db = jdo.getDatabase();
      } catch (DatabaseNotFoundException dbnfe) {
        throw new WorkflowException("JDOManager.getDatabase",
            "EX_ERR_CASTOR_DATABASE_NOT_FOUND", dbnfe);
      } catch (PersistenceException pe) {
        throw new WorkflowException("JDOManager.getDatabase",
            "EX_ERR_CASTOR_GET_DATABASE", pe);
      }
    }

    return db;
  }

  static public void closeDatabase(Database db) {
    if (db != null) {
      try {
        db.close();
      } catch (PersistenceException pe) {
        SilverTrace.warn("workflowEngine", "JDOManager.closeDatabase",
            "root.EX_CONNECTION_CLOSE_FAILED", pe);
      }
    }
  }
}