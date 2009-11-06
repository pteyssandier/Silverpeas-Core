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
/*
 * Created on 24 janv. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.silverpeas.importExport.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.stratelia.silverpeas.util.ResourcesWrapper;
import com.stratelia.webactiv.util.DateUtil;
import com.stratelia.webactiv.util.FileRepositoryManager;

/**
 * @author tleroi
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportReport {

  private Date startDate;
  private Date endDate;
  private int nbFilesProcessed;
  private int nbFilesNotImported;
  private List listComponentReport;

  public void addComponentReport(ComponentReport componentReport) {
    if (listComponentReport == null)
      listComponentReport = new ArrayList();

    listComponentReport.add(componentReport);
  }

  /**
   * @return
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * @return
   */
  public int getNbFilesProcessed() {
    return nbFilesProcessed;
  }

  public int getNbFilesNotImported() {
    return nbFilesNotImported;
  }

  /**
   * @return
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * @param date
   */
  void setEndDate(Date date) {
    endDate = date;
  }

  /**
   * @param date
   */
  void setStartDate(Date date) {
    startDate = date;
  }

  /**
   * @return Returns the listComponentReport.
   */
  public List getListComponentReport() {
    return listComponentReport;
  }

  public void addNumberOfFilesProcessed(int n) {
    this.nbFilesProcessed = this.nbFilesProcessed + n;
  }

  public String getDuration() {
    return DateUtil.formatDuration(getEndDate().getTime()
        - getStartDate().getTime());
  }

  /**
   * @param i
   */
  public void addNbFilesNotImported(int i) {
    this.nbFilesNotImported = this.nbFilesNotImported + i;
  }

  public long getTotalImportedFileSize() {
    long size = 0;
    Iterator itCompReport = getListComponentReport().iterator();
    ComponentReport componentRpt = null;
    while (itCompReport.hasNext()) {
      componentRpt = (ComponentReport) itCompReport.next();
      size += componentRpt.getTotalImportedFileSize();
    }
    return size;
  }

  /**
   * Méthode de formatage du rapport en vue de l'écriture d'un fichier de log
   * 
   * @param resource
   * @return
   */
  public String writeToLog(ResourcesWrapper resource) {

    String returnValue = null;
    StringBuffer sb = new StringBuffer();
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        "[yyyy-MM-dd-HH'H'mm'm'ss's']");
    String dateFormatee = dateFormat.format(date);
    sb.append("**********************************************\n");
    sb.append(dateFormatee).append("\n\n");
    sb.append(resource.getString("importExportPeas.StatGlobal") + "\n\n");
    sb.append(resource.getString("importExportPeas.ImportDuration") + " : "
        + getDuration() + "\n");
    sb.append(resource.getString("importExportPeas.NbFilesImported") + " : "
        + getNbFilesProcessed() + "\n");
    sb.append(resource.getString("importExportPeas.NbFilesNotFound") + " : "
        + getNbFilesNotImported() + "\n");
    sb.append(resource.getString("importExportPeas.TotalFileUploadedSize")
        + " : "
        + FileRepositoryManager.formatFileSize(getTotalImportedFileSize())
        + "\n\n");
    sb.append(resource.getString("importExportPeas.StatComponent") + "\n");

    Iterator itCompReport = getListComponentReport().iterator();
    while (itCompReport.hasNext()) {
      ComponentReport componentRpt = (ComponentReport) itCompReport.next();
      sb.append("\n" + resource.getString("importExportPeas.Composant") + " : "
          + componentRpt.getComponentName() + " : " + "("
          + componentRpt.getComponentId() + ")\n");
      sb.append(resource.getString("importExportPeas.NbPubCreated") + " : "
          + componentRpt.getNbPublicationsCreated() + "\n");
      sb.append(resource.getString("importExportPeas.NbPubUpdated") + " : "
          + componentRpt.getNbPublicationsUpdated() + "\n");
      sb.append(resource.getString("importExportPeas.NbTopicCreated") + " : "
          + componentRpt.getNbTopicsCreated() + "\n");
      sb.append(resource.getString("importExportPeas.TotalFileUploadedSize")
          + " : "
          + FileRepositoryManager.formatFileSize(componentRpt
              .getTotalImportedFileSize()) + "\n");
      // Affichage des rapports unitaires
      List unitReports = componentRpt.getListUnitReports();
      if (unitReports != null) {
        Iterator itUnitReports = unitReports.iterator();
        UnitReport unitReport = null;
        while (itUnitReports.hasNext()) {
          unitReport = (UnitReport) itUnitReports.next();
          if (unitReport.getError() != -1) {
            sb.append(unitReport.getLabel()
                + " : "
                + unitReport.getItemName()
                + ", "
                + resource.getString("GML.error")
                + " : "
                + resource.getString("importExportPeas.ImportError"
                    + unitReport.getError())
                + ", "
                + resource.getString("importExportPeas.Status")
                + " : "
                + resource.getString("importExportPeas.ImportStatus"
                    + unitReport.getStatus()) + "\n");
          }
        }
      }
      // Affichage des rapports massifs
      List massiveReports = componentRpt.getListMassiveReports();
      if (massiveReports != null) {
        Iterator itMassiveReports = massiveReports.iterator();
        MassiveReport massiveReport = null;
        while (itMassiveReports.hasNext()) {
          massiveReport = (MassiveReport) itMassiveReports.next();

          sb.append(resource.getString("importExportPeas.Repository") + " "
              + massiveReport.getRepositoryPath() + "\n");
          if (massiveReport.getError() != -1) {
            sb.append(resource.getString("GML.error")
                + " : "
                + resource.getString("importExportPeas.ImportError"
                    + massiveReport.getError()) + "\n");
          }
          sb.append(resource.getString("importExportPeas.NbPubCreated") + " : "
              + massiveReport.getNbPublicationsCreated() + "\n");
          sb.append(resource.getString("importExportPeas.NbPubUpdated") + " : "
              + massiveReport.getNbPublicationsUpdated() + "\n");
          sb.append(resource.getString("importExportPeas.NbTopicCreated")
              + " : " + massiveReport.getNbTopicsCreated() + "\n");

          unitReports = massiveReport.getListUnitReports();
          if (unitReports != null) {
            Iterator itUnitReports = unitReports.iterator();
            UnitReport unitReport = null;
            while (itUnitReports.hasNext()) {
              unitReport = (UnitReport) itUnitReports.next();
              if (unitReport.getError() != -1) {
                sb.append(unitReport.getLabel()
                    + " : "
                    + unitReport.getItemName()
                    + ", "
                    + resource.getString("GML.error")
                    + " : "
                    + resource.getString("importExportPeas.ImportError"
                        + unitReport.getError())
                    + ", "
                    + resource.getString("importExportPeas.Status")
                    + " : "
                    + resource.getString("importExportPeas.ImportStatus"
                        + unitReport.getStatus()) + "\n");
              }
            }
          }
        }
      }
      sb.append("\n");
      returnValue = sb.toString();
    }
    return returnValue;
  }

}