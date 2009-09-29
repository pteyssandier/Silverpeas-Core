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
package com.sun.portal.portletcontainer.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.stratelia.webactiv.util.ResourceLocator;
import com.sun.portal.portletcontainer.admin.registry.PortletRegistryTags;
import com.sun.portal.portletcontainer.context.registry.PortletRegistryException;
import com.sun.portal.portletcontainer.warupdater.PortletWarUpdaterUtil;

/**
 * PortletRegistryHelper is a Helper class to write to and read from the portlet
 * registry xml files.
 */
public class PortletRegistryHelper implements PortletRegistryTags {

  // private static final String PORTLET_CONTAINER_DIR_PROPERTY =
  // "com.sun.portal.portletcontainer.dir";
  private static final String PORTLET_CONTAINER_DIR_PROPERTY = "portlets.configDir";
  private static final String PORTLET_CONTAINER_DEFAULT_DIR_NAME = ".portlet-container";
  private static final String PORTLET_CONTAINER_DEFAULT_DATA_DIR_NAME = "data";
  private static final String PORTLET_CONTAINER_DEFAULT_LOG_DIR_NAME = "logs";
  private static final String PORTLET_CONTAINER_DEFAULT_WAR_DIR_NAME = "war";
  private static final String PORTLET_CONTAINER_DEFAULT_AUTODEPLOY_DIR_NAME = "autodeploy";
  private static final String PORTLET_CONTAINER_DEFAULT_CONFIG_DIR_NAME = "config";
  private static final String PORTLET_CONTAINER_HOME_TOKEN = "@portlet-container-home@";

  private static Logger logger = Logger.getLogger(
      "com.sun.portal.portletcontainer.admin",
      "com.silverpeas.portlets.PALogMessages");

  private static String CONFIG_FILE = "ContainerConfig.properties";
  private static Properties configProperties = null;

  private PortletRegistryHelper() {
  }

  public static DocumentBuilder getDocumentBuilder()
      throws PortletRegistryException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    try {
      docBuilder = dbf.newDocumentBuilder();
      docBuilder.setEntityResolver(new NoOpEntityResolver());
    } catch (ParserConfigurationException pce) {
      throw new PortletRegistryException(pce);
    }
    return docBuilder;
  }

  public static Document readFile(File file) throws PortletRegistryException {
    try {
      return getDocumentBuilder().parse(file);
    } catch (SAXException saxe) {
      throw new PortletRegistryException(saxe);
    } catch (IOException ioe) {
      throw new PortletRegistryException(ioe);
    }
  }

  public static Element getRootElement(Document document) {
    if (document != null)
      return document.getDocumentElement();
    return null;
  }

  public static synchronized void writeFile(Document document, File file)
      throws PortletRegistryException {
    try {
      // Use a Transformer for output
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
          "{http://xml.apache.org/xslt}indent-amount", "5");

      DOMSource source = new DOMSource(document);
      // StreamResult result = new StreamResult(System.out);
      StreamResult result = new StreamResult(file);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException tce) {
      throw new PortletRegistryException(tce);
    } catch (TransformerException te) {
      throw new PortletRegistryException(te);
    } catch (Exception e) {
      throw new PortletRegistryException(e);
    }
  }

  private static void loadConfigFile() {
    // If the config file is already loaded don't load it again
    InputStream is = null;
    if (configProperties == null) {
      try {
        // is =
        // Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);
        // configProperties = new Properties();
        // configProperties.load(is);
        ResourceLocator rl = new ResourceLocator(
            "com.silverpeas.portlets.portletsSettings", "");
        configProperties = rl.getProperties();
      } catch (Exception e) {
        logger.log(Level.SEVERE, "PSPL_CSPPAM0016", e);
      } finally {
        try {
          if (is != null) {
            is.close();
          }
        } catch (Exception ignored) {
        }
      }
    }
  }

  private static String getPortletContainerDir() {
    String registryLocation = null;
    loadConfigFile();
    // In case loadConfigFile throws an exception configProperties will be null
    if (configProperties != null) {
      registryLocation = configProperties
          .getProperty(PORTLET_CONTAINER_DIR_PROPERTY);
    }
    if (registryLocation == null
        || PORTLET_CONTAINER_HOME_TOKEN.equals(registryLocation)) {
      registryLocation = System.getProperty("user.home") + File.separator
          + PORTLET_CONTAINER_DEFAULT_DIR_NAME;
      if (logger.isLoggable(Level.WARNING)) {
        logger.log(Level.WARNING, "PSPL_CSPPAM0003", new String[] {
            PORTLET_CONTAINER_DIR_PROPERTY, registryLocation });
      }
    }
    return registryLocation;
  }

  public static String getAutoDeployLocation() {
    String autoDeployLocation = getPortletContainerDir() + File.separator
        + PORTLET_CONTAINER_DEFAULT_AUTODEPLOY_DIR_NAME;
    if (!makeDir(autoDeployLocation)) {
      throw new RuntimeException("cannotCreateDirectory");
    }
    return autoDeployLocation;
  }

  public static String getRegistryLocation() throws PortletRegistryException {
    String registryLocation = getPortletContainerDir() + File.separator
        + PORTLET_CONTAINER_DEFAULT_DATA_DIR_NAME;
    if (!makeDir(registryLocation)) {
      throw new PortletRegistryException("cannotCreateDirectory");
    }
    return registryLocation;
  }

  public static String getLogLocation() throws PortletRegistryException {
    String logDirLocation = getPortletContainerDir() + File.separator
        + PORTLET_CONTAINER_DEFAULT_LOG_DIR_NAME;
    if (!makeDir(logDirLocation)) {
      throw new PortletRegistryException("cannotCreateDirectory");
    }
    return logDirLocation;
  }

  public static String getWarFileLocation() throws PortletRegistryException {
    String warFileLocation = getPortletContainerDir() + File.separator
        + PORTLET_CONTAINER_DEFAULT_WAR_DIR_NAME;
    if (!makeDir(warFileLocation)) {
      throw new PortletRegistryException("cannotCreateDirectory");
    }
    return warFileLocation;
  }

  public static String getUpdatedAbsoluteWarFileName(String warFileName) {
    StringBuffer warFileLocation = new StringBuffer();
    try {
      warFileLocation.append(PortletRegistryHelper.getWarFileLocation());
    } catch (PortletRegistryException pre) {
      warFileLocation.append("");
    }

    String warName = PortletWarUpdaterUtil.getWarName(warFileName);
    warFileLocation.append(File.separator);
    warFileLocation.append(warName);
    return warFileLocation.toString();
  }

  public static String getConfigFileLocation() {
    String configFileLocation = getPortletContainerDir() + File.separator
        + PORTLET_CONTAINER_DEFAULT_CONFIG_DIR_NAME;
    return configFileLocation;
  }

  private static boolean makeDir(String dirName) {
    File dir = new File(dirName);
    if (dir.exists()) {
      return true;
    } else {
      return dir.mkdirs();
    }
  }
}
