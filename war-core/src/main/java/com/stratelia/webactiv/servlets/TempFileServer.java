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
package com.stratelia.webactiv.servlets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.silverpeas.util.FileUtil;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.GeneralPropertiesManager;
import com.stratelia.webactiv.util.ResourceLocator;

/**
 * To get files from temp directory
 * @author neysseri
 */
public class TempFileServer extends HttpServlet {

  @Override
  public void init(ServletConfig config) {
    try {
      super.init(config);
    } catch (ServletException se) {
      SilverTrace.fatal("peasUtil", "TempFileServer.init", "peasUtil.CANNOT_ACCESS_SUPERCLASS");
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    doPost(req, res);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException {
    SilverTrace.info("peasUtil", "TempFileServer.doPost", "root.MSG_GEN_ENTER_METHOD");
    String encodedFileName =
        req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/') + 1);
    String fileName = URLDecoder.decode(encodedFileName, "UTF-8");
    HttpSession session = req.getSession(true);
    MainSessionController mainSessionCtrl =
        (MainSessionController) session.getAttribute("SilverSessionController");
    if (mainSessionCtrl == null) {
      SilverTrace.warn("peasUtil", "TempFileServer.doPost", "root.MSG_GEN_SESSION_TIMEOUT",
          "NewSessionId=" + session.
          getId() +
          GeneralPropertiesManager.getGeneralResourceLocator().getString("ApplicationURL") +
          GeneralPropertiesManager.getGeneralResourceLocator().
          getString("sessionTimeout"));
      res.sendRedirect(GeneralPropertiesManager.getGeneralResourceLocator().getString(
          "ApplicationURL") +
          GeneralPropertiesManager.
          getGeneralResourceLocator().getString("sessionTimeout"));
    }
    String filePath = FileRepositoryManager.getTemporaryPath() + fileName;
    res.setContentType(FileUtil.getMimeType(fileName));
    res.setHeader("Content-Disposition", "inline; filename=\"" + fileName + '"');
    write(res, filePath);

  }

  /**
   * This method writes the result of the preview action.
   * @param res - The HttpServletResponse where the html code is write
   * @param htmlFilePath - the canonical path of the html document generated by the parser tools. if
   * this String is null that an exception had been catched the html document generated is empty !!
   * also, we display a warning html page
   */
  private void write(HttpServletResponse res, String htmlFilePath) throws IOException {
    OutputStream out2 = res.getOutputStream();
    int read;
    BufferedInputStream input = null; // for the html document generated
    SilverTrace.info("peasUtil", "TempFileServer.write()", "root.MSG_GEN_ENTER_METHOD",
        " htmlFilePath " + htmlFilePath);
    try {
      input = new BufferedInputStream(new FileInputStream(htmlFilePath));
      read = input.read();
      SilverTrace.info("peasUtil", "TempFileServer.write()", "root.MSG_GEN_ENTER_METHOD",
          " BufferedInputStream read " + read);
      if (read == -1) {
        writeWarningMessage(res);
      } else {
        while (read != -1) {
          out2.write(read); // writes bytes into the response
          read = input.read();
        }
      }
    } catch (Exception e) {
      SilverTrace.warn("peasUtil", "TempFileServer.write", "root.EX_CANT_READ_FILE", "file name=" +
          htmlFilePath);
      writeWarningMessage(res);
    } finally {
      // we must close the in and out streams
      try {
        if (input != null) {
          input.close();
        }
        out2.close();
      } catch (Exception e) {
        SilverTrace.warn("peasUtil", "TempFileServer.write", "root.EX_CANT_READ_FILE",
            "close failed");
      }
    }
  }

  private void writeWarningMessage(HttpServletResponse res) throws IOException {
    StringReader sr = null;
    OutputStream out2 = res.getOutputStream();
    int read;
    ResourceLocator resourceLocator = new ResourceLocator(
        "com.stratelia.webactiv.util.peasUtil.multiLang.fileServerBundle", "");

    sr = new StringReader(resourceLocator.getString("warning"));
    try {
      read = sr.read();
      while (read != -1) {
        out2.write(read); // writes bytes into the response
        read = sr.read();
      }
    } catch (Exception e) {
      SilverTrace.warn("peasUtil", "TempFileServer.displayWarningMessage",
          "root.EX_CANT_READ_FILE",
          "warning properties");
    } finally {
      try {
        if (sr != null) {
          sr.close();
        }
        out2.close();
      } catch (Exception e) {
        SilverTrace.warn("peasUtil", "TempFileServer.displayWarningMessage",
            "root.EX_CANT_READ_FILE", "close failed");
      }
    }
  }
}
