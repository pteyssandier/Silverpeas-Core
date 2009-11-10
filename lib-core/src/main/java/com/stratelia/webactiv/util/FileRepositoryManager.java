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
package com.stratelia.webactiv.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.fileFolder.FileFolderManager;

/**
 * 
 * @author Norbert CHAIX
 * @version
 */

public class FileRepositoryManager extends Object {
  static String s_sUpLoadPath = "";
  static String s_sIndexUpLoadPath = "";
  static String s_sTempPath = "";
  static String s_sApplicationURL = "";
  static FileFolderManager s_FileFolderManager = new FileFolderManager();
  static ResourceLocator uploadSettings = null;
  static ResourceLocator utilMessages = new ResourceLocator(
      "com.silverpeas.util.multilang.util", "");
  static String unknownFileIcon = null;
  public static final String CONTEXT_TOKEN = ",";

  static {
    try {
      ResourceLocator generalSettings = new ResourceLocator(
          "com.stratelia.webactiv.general", "");
      s_sApplicationURL = generalSettings.getString("ApplicationURL");
      s_sUpLoadPath = generalSettings.getString("uploadsPath");
      s_sTempPath = generalSettings.getString("tempPath");
      s_sIndexUpLoadPath = generalSettings.getString("uploadsIndexPath");
      uploadSettings = new ResourceLocator(
          "com.stratelia.webactiv.util.uploads.uploadSettings", "");
      unknownFileIcon = uploadSettings.getString("unknown");
    } catch (Exception e) {
      SilverTrace.error("util", "FileRepositoryManager static",
          "util.MSG_ERROR_LOADING_PROPS",
          "com.stratelia.webactiv.general.properties");
    }
  }

  /**
   * @deprecated
   * 
   * @param sSpaceId
   * @param sComponentId
   * @return
   */
  static public String getAbsolutePath(String sSpaceId, String sComponentId) {
    SilverTrace.debug("util", "FileRepositoryManager.getAbsolutePath",
        "concat: sSpaceId = " + sSpaceId + " sComponentId= " + sComponentId);
    return s_sUpLoadPath + File.separator + sComponentId + File.separator;
  }

  static public String getAbsolutePath(String sComponentId) {
    SilverTrace.debug("util", "FileRepositoryManager.getAbsolutePath",
        " sComponentId= " + sComponentId);
    return s_sUpLoadPath + File.separator + sComponentId + File.separator;
  }

  // Add by Jean-Claude Groccia
  // @param: sSpaceId: type String: the name of the space
  // @param: sComponentId: type String: the name of the componentId
  // @param: sDirectoryName: type Array of String: the name of sub directory.
  // this parameter represents the context of component
  /**
   * @deprecated
   */
  static public String getAbsolutePath(String sSpaceId, String sComponentId,
      String[] sDirectoryName) {
    return getAbsolutePath(sComponentId, sDirectoryName);
  }

  /**
   * 
   * @param sSpaceId
   * @param sComponentId
   * @param sDirectoryName
   * @return path
   */
  static public String getAbsolutePath(String sComponentId,
      String[] sDirectoryName) {
    int lg = sDirectoryName.length;
    String path = getAbsolutePath(sComponentId);
    for (int k = 0; k < lg; k++) {
      SilverTrace
          .debug(
              "util",
              "FileRepositoryManager.getAbsolutePath",
              ("concat: path = " + path + " sDirectoryName[" + k + "]=" + sDirectoryName[k]));
      path = path + sDirectoryName[k] + File.separatorChar;
    }
    return path;
  }

  // Add by Jean-Claude Groccia
  // @param: sDirectoryName: type of String: the name of sub directory.
  // @return path1/path2/
  static public String getRelativePath(String[] sDirectoryName) {
    int lg = sDirectoryName.length;
    String path = sDirectoryName[0];
    path = path.concat(File.separator);
    for (int k = 1; k < lg; k++) {
      SilverTrace.debug("util", "FileRepositoryManager.getAbsolutePath",
          "concat: path = " + path + " sDirectoryName[" + k + "]="
              + sDirectoryName[k]);
      path = path.concat(sDirectoryName[k]);
      path = path.concat(File.separator);
    }
    return path;
  }

  static public String getTemporaryPath() {
    return s_sTempPath + File.separator;
  }

  static public String getTemporaryPath(String sSpaceId, String sComponentId) {
    return s_sTempPath + File.separator;
  }

  static public String getComponentTemporaryPath(String sComponentId) {
    return getAbsolutePath(sComponentId) + "Temp" + File.separator;
  }

  static public String getAbsoluteIndexPath(String particularSpace,
      String sComponentId) {
    SilverTrace.debug("util", "FileRepositoryManager.getAbsoluteIndexPath",
        "particularSpace = " + particularSpace + " sComponentId= "
            + sComponentId);
    if (particularSpace != null
        && (particularSpace.startsWith("user@") || particularSpace
            .equals("transverse")))
      return s_sIndexUpLoadPath + File.separator + particularSpace
          + File.separator + sComponentId + File.separator + "index";
    else
      return s_sIndexUpLoadPath + File.separator + sComponentId
          + File.separator + "index";
  }

  /**
   * @deprecated
   */
  static public void createAbsolutePath(String sSpaceId, String sComponentId,
      String sDirectoryName) throws Exception {
    FileFolderManager.createFolder(getAbsolutePath(sComponentId)
        + sDirectoryName);
  }

  static public void createAbsolutePath(String sComponentId,
      String sDirectoryName) throws Exception {
    FileFolderManager.createFolder(getAbsolutePath(sComponentId)
        + sDirectoryName);
  }

  /**
   * @deprecated
   */
  static public void createTempPath(String sSpaceId, String sComponentId,
      String sDirectoryName) throws Exception {
    FileFolderManager.createFolder(getAbsolutePath(sComponentId));
  }

  static public void createTempPath(String sComponentId, String sDirectoryName)
      throws Exception {
    FileFolderManager.createFolder(getAbsolutePath(sComponentId));
  }

  static public void createGlobalTempPath(String sDirectoryName)
      throws Exception {
    FileFolderManager.createFolder(getTemporaryPath() + sDirectoryName);
  }

  static public void createAbsoluteIndexPath(String particularSpace,
      String sComponentId) throws Exception {
    FileFolderManager.createFolder(getAbsoluteIndexPath(particularSpace,
        sComponentId));
  }

  static public void deleteAbsolutePath(String sSpaceId, String sComponentId,
      String sDirectoryName) throws Exception {
    FileFolderManager.deleteFolder(getAbsolutePath(sComponentId)
        + sDirectoryName);
  }

  static public void deleteTempPath(String sSpaceId, String sComponentId,
      String sDirectoryName) throws Exception {
    FileFolderManager.deleteFolder(getAbsolutePath(sComponentId));
  }

  static public void deleteAbsoluteIndexPath(String particularSpace,
      String sComponentId) throws Exception {
    FileFolderManager.deleteFolder(getAbsoluteIndexPath(particularSpace,
        sComponentId));
  }

  static public String getFileIcon(boolean small, String extension) {
    return getFileIcon(small, extension, false);
  }

  static public String getFileIcon(String extension) {
    return getFileIcon(false, extension);
  }

  /**
   * Get File icon
   * 
   * @param extension
   * @param isReadOnly
   * @return
   */
  static public String getFileIcon(String extension, boolean isReadOnly) {
    return getFileIcon(false, extension, isReadOnly);
  }

  static public String getFileIcon(boolean small, String extension,
      boolean isReadOnly) {
    String path = s_sApplicationURL + uploadSettings.getString("FileIconsPath");

    String fileIcon = uploadSettings.getString(extension.toLowerCase());
    if (fileIcon == null)
      fileIcon = unknownFileIcon;
    else {
      if (isReadOnly)
        fileIcon = fileIcon.substring(0, fileIcon.lastIndexOf(".gif"))
            + "Lock.gif";
    }

    if (small) {
      String newFileIcon = fileIcon.substring(0, fileIcon.lastIndexOf(".gif"))
          + "Small.gif";
      if (newFileIcon != null)
        fileIcon = newFileIcon;
    }

    return path + fileIcon;
  }

  static public String getFileExtension(String fileName) {
    if (fileName == null) {
      return "";
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
  }

  /**
   * Get the file size with the suitable unit
   * 
   * @deprecated use formatFileSize method
   * @param long : size
   * @return String
   */
  static public String getFileSize(long size) {
    return formatFileSize(size);
  }

  /**
   * Get the file size with the suitable unit
   * 
   * @param long : size
   * @return String
   */
  static public String formatFileSize(long lSize) {
    String Mo = utilMessages.getString("mo", "fr");
    String Ko = utilMessages.getString("ko", "fr");
    String o = utilMessages.getString("o", "fr");

    float size = new Long(lSize).floatValue();

    if (size < 1024) {// inférieur à 1 ko (1024 octets)
      return Float.toString(size).concat(" ").concat(o);
    } else if (size < 1024 * 1024) {// inférieur à 1 mo (1024 * 1024 octets)
      return Integer.toString(Math.round(size / 1024)).concat(" ").concat(Ko);
    } else {// supérieur à 1 mo (1024 * 1024 octets)
      DecimalFormat format = new DecimalFormat();
      format.setMaximumFractionDigits(2);
      return format.format(size / (1024 * 1024)).concat(" ").concat(Mo);
    }
  }

  /**
   * Get the size of a file (in bytes)
   * 
   * @param String
   *          (sourceFile)
   * @return int
   */
  static public int getFileSize(String sourceFile) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try {
      int c;
      int i;
      byte[] b = new byte[1];
      FileInputStream in;

      // Ex: "c:/j2sdkee1.2.1/public_html/kmeliaAttachments/Image6.gif";
      File inputFile = new File(sourceFile);
      in = new FileInputStream(inputFile);
      i = 0;
      while ((c = in.read()) != -1) {
        i = i + 1;
        b[0] = (byte) (c & 0x000000ff);
        buffer.write(b, 0, 1);
      }
      in.close();
    } catch (IOException ioe) {
      SilverTrace.error("util", "FileFolderManager.getFileSize()",
          "util.CANT_GET_FILESIZE", sourceFile);
    }
    return buffer.size();
  }

  /**
   * Get the estimated download time
   * 
   * @param long (fileSize)
   * @return String
   */
  static public String getFileDownloadTime(long size) {
    int fileSizeReference = new Integer(uploadSettings
        .getString("FileSizeReference")).intValue();
    int theoricDownloadTime = new Integer(uploadSettings
        .getString("DownloadTime")).intValue();
    long fileDownloadEstimation = ((size * theoricDownloadTime) / fileSizeReference) / 60;
    if (fileDownloadEstimation < 1)
      return "t < 1 min";
    else if ((fileDownloadEstimation >= 1) && (fileDownloadEstimation < 5))
      return "1 < t < 5 mins";
    else
      return " t > 5 mins";
  }

  /**
   * Copy a contents from a file to another one
   * 
   * @author Seb
   * @param from
   *          The name of the source file, the one to copy.
   * @param to
   *          The name of the destination file, where to paste data.
   */
  static public void copyFile(String from, String to)
      throws FileNotFoundException, IOException {
    BufferedInputStream input = new BufferedInputStream(new FileInputStream(
        from));
    BufferedOutputStream output = new BufferedOutputStream(
        new FileOutputStream(to));
    int data = input.read();
    while (data != -1) {
      output.write(data);
      data = input.read();
    }
    input.close();
    output.close();
  }

  static public String formatFileUploadTime(long size) {
    String min = " m";
    String sec = " s";
    String ms = " ms";
    if (size < 1000) {
      return new Long(size).toString() + ms;
    } else if (size < 120000) {
      return new Long(size / 1000).toString() + sec;
    } else {
      return new Long(size / 60000).toString() + min;
    }
  }

  /**
   * to create the array of the string this array represents the repertories
   * where the files must be stored.
   * 
   * @param str
   *          : type String: the string of repertories
   * @param token
   *          : type String: the token separating the repertories
   */
  public static String[] getAttachmentContext(String str) {

    String strAt = "Attachment " + CONTEXT_TOKEN;

    if (str != null) {
      strAt = strAt.concat(str);
    }
    StringTokenizer strToken = new StringTokenizer(strAt, CONTEXT_TOKEN);
    // number of elements
    int nElt = strToken.countTokens();
    // to init array
    String[] context = new String[nElt];

    int k = 0;

    while (strToken.hasMoreElements()) {
      context[k] = ((String) strToken.nextElement()).trim();
      k++;
    }
    return context;
  }

}