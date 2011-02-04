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
package com.silverpeas.importExport.report;


import com.silverpeas.form.DataRecord;
import com.silverpeas.form.Form;
import com.silverpeas.form.PagesContext;
import com.silverpeas.form.RecordSet;
import com.silverpeas.importExport.model.PublicationType;
import com.silverpeas.publication.importExport.DBModelContentType;
import com.silverpeas.publication.importExport.XMLModelContentType;
import com.silverpeas.publicationTemplate.PublicationTemplateImpl;
import com.silverpeas.publicationTemplate.PublicationTemplateManager;
import com.silverpeas.util.FileUtil;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DateUtil;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.FileServerUtils;
import com.stratelia.webactiv.util.attachment.model.AttachmentDetail;
import com.stratelia.webactiv.util.publication.info.model.ModelDetail;
import com.stratelia.webactiv.util.publication.model.PublicationDetail;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.apache.ecs.xhtml.a;

/**
 * Classe générant le code html d'une publication exportée
 * @author sdevolder
 */
public class HtmlExportPublicationGenerator {

  // Variables
  private PublicationDetail publicationDetail;
  private DBModelContentType dbModelContent;
  private XMLModelContentType xmlModelContent;
  private ModelDetail modelDetail;
  private String wysiwygText;
  private List<AttachmentDetail> listAttDetail;
  private String urlPub;

  public HtmlExportPublicationGenerator(PublicationType publicationType, ModelDetail modelDetail,
      String wysiwygText, String urlPub) {
    this.publicationDetail = publicationType.getPublicationDetail();
    if (publicationType.getPublicationContentType() != null) {
      this.dbModelContent = publicationType.getPublicationContentType().getDBModelContentType();
      this.xmlModelContent = publicationType.getPublicationContentType().getXMLModelContentType();
    }
    if (publicationType.getAttachmentsType() != null) {
      this.listAttDetail = publicationType.getAttachmentsType().getListAttachmentDetail();
    }

    this.modelDetail = modelDetail;
    this.wysiwygText = wysiwygText;
    this.urlPub = urlPub;
  }

  /**
   * Display header of publication
   * @return
   */
  public String toHtmlSommairePublication() {
    return toHtmlSommairePublication(null);
  }

  /**
   * Display header of publication
   * @param target : name of iframe destination
   * @return
   */
  public String toHtmlSommairePublication(String target) {

    String htmlPubName = HtmlExportGenerator.encode(publicationDetail.getName());
    String htmlPubDescription = HtmlExportGenerator.encode(publicationDetail.getDescription());
    String htmlCreatorName = HtmlExportGenerator.encode(publicationDetail.getCreatorName());
    String dateString = DateUtil.dateToString(publicationDetail.getCreationDate(), "fr");

    StringBuilder sb = new StringBuilder();
    sb.append("&#149;&nbsp;");
    if (StringUtil.isDefined(target)) {
      a link = new a();
      sb.append("<a target=\"").append(target).append("\" href=\"").append(urlPub).append("\">");
    } else {
      sb.append("<a href=\"").append(urlPub).append("\">");
    }
    sb.append("<b>").append(htmlPubName).append("</b>").append("</a>").append("\n");
    if (StringUtil.isDefined(htmlCreatorName)) {
      sb.append(" - ").append(htmlCreatorName);
    }
    if (StringUtil.isDefined(dateString)) {
      sb.append(" (").append(dateString).append(")");
    }
    sb.append("<br/>\n");
    if (StringUtil.isDefined(htmlPubDescription)) {
      sb.append("<i>").append(htmlPubDescription).append("</i>").append("<br/><br/>\n");
    }
    return sb.toString();
  }

  /**
   * @return
   */
  private String toHtmlEnTetePublication() {

    String htmlPubName = HtmlExportGenerator.encode(publicationDetail.getName());
    String htmlPubDescription = HtmlExportGenerator.encode(publicationDetail.getDescription());
    String htmlCreatorName = HtmlExportGenerator.encode(publicationDetail.getCreatorName());
    String dateString = DateUtil.dateToString(publicationDetail.getCreationDate(), "fr");

    StringBuilder sb = new StringBuilder();

    sb.append(
        "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" bgcolor=\"#B3BFD1\">\n");
    sb.append("<tr><td>\n");
    sb.append(
        "<table width=\"100%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" bgcolor=\"#EFEFEF\"><tr>\n");
    sb.append("<td><strong>").append(htmlPubName).append("</strong></td>\n");
    sb.append("<td><div align=\"right\">").append(htmlCreatorName).append(
        "</div></td> </tr>\n");
    sb.append("<tr><td>").append(htmlPubDescription).append("</td>\n");
    sb.append("<td><div align=\"right\">").append(dateString).append(
        "</div></td></tr>\n");
    sb.append("</table></td>");
    sb.append("</tr></table>");
    return sb.toString();
  }

  /**
   * @return
   */
  private String toHtmlInfoModel() {
    StringBuilder sb = new StringBuilder();
    // TODO: faire passer ce parametre depuis le controleur?
    String toParse = modelDetail.getHtmlDisplayer();

    List textList = dbModelContent.getListTextParts();
    List imageList = dbModelContent.getListImageParts();

    Iterator textIterator = null;
    Iterator imageIterator = null;

    if (textList != null) {
      textIterator = textList.iterator();
    }
    if (imageList != null) {
      imageIterator = imageList.iterator();
    }

    int posit = toParse.indexOf("%WA");
    while (posit != -1) {
      if (posit > 0) {
        sb.append(toParse.substring(0, posit));
        toParse = toParse.substring(posit);
      }
      if (toParse.startsWith("%WATXTDATA%")) {
        if (textIterator != null && textIterator.hasNext()) {
          String textPart = (String) textIterator.next();
          sb.append(HtmlExportGenerator.encode(textPart));
        }
        toParse = toParse.substring(11);
      } else if (toParse.startsWith("%WAIMGDATA%")) {
        if (imageIterator != null && imageIterator.hasNext()) {
          String imagePath = (String) imageIterator.next();
          String imageName = imagePath.substring(imagePath.lastIndexOf(File.separator) + 1, imagePath.
              length());
          if (FileUtil.isImage(imageName)) {
            sb.append("<img border=\"0\" src=\"" + imageName + "\">");
          } else {
            sb.append("<b>FileNotImage</b>");
          }
        }
        toParse = toParse.substring(11);
      }

      // et on recommence
      posit = toParse.indexOf("%WA");
    }
    sb.append(toParse);
    return sb.toString();
  }

  public String toHtmlXMLModel() {
    PublicationTemplateImpl template = null;
    try {
      template = (PublicationTemplateImpl) PublicationTemplateManager.getInstance().
          getPublicationTemplate(publicationDetail.getPK().getInstanceId()
          + ":" + publicationDetail.getInfoId());
    } catch (Exception e) {
      return "Error getting publication template !";
    }

    try {
      Form formView = template.getViewForm();
      RecordSet recordSet = template.getRecordSet();
      DataRecord dataRecord = recordSet.getRecord(publicationDetail.getPK().getId());
      PagesContext context = new PagesContext();
      String htmlResult = formView.toString(context, dataRecord);
      htmlResult = replaceImagesPathForExport(htmlResult);
      htmlResult = replaceFilesPathForExport(htmlResult);
      return htmlResult;
    } catch (Exception e) {
      SilverTrace.error("form", "HtmlExportPublicationGenerator.toHtmlXMLModel",
          "root.MSG_GEN_PARAM_VALUE", e);
    }
    return null;
  }

  /**
   * @return
   */
  public String toHtml() {
    StringBuilder sb = new StringBuilder();
    String htmlPubName = HtmlExportGenerator.encode(publicationDetail.getName());

    // Entête du fichier HTML
    sb.append("<html>\n").append("<head>\n");
    sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
    // ajout du style
    sb.append(HtmlExportGenerator.getHtmlStyle());
    sb.append("<title>").append(htmlPubName).append("</title>");

    sb.append("</head>\n").append("<body>\n");
    // Titre de la publication
    sb.append(toHtmlEnTetePublication());
    sb.append("<br/>").append("\n");
    // Contenu de la publication
    sb.append("<table valign=\"top\" width=\"100%\" border=\"0\" cellpadding=\"5\"");
    sb.append("cellspacing=\"4\">").append("\n");
    sb.append("<tr><td>").append("\n");

    if (dbModelContent != null) {
      sb.append(toHtmlInfoModel());
    } else if (wysiwygText != null) {
      sb.append(HtmlExportGenerator.encode(wysiwygText));
    } else if (xmlModelContent != null) {
      sb.append(toHtmlXMLModel());
    }

    sb.append("</td>").append("\n");
    if (listAttDetail != null && !listAttDetail.isEmpty()) {
      sb.append("<td valign=\"top\" align=\"right\" width=\"20%\">");
      sb.append("<table  border=\"0\" cellpadding=\"3\" cellspacing=\"1\" bgcolor=\"#B3BFD1\">");
      sb.append("<tr><td bgcolor=\"#FFFFFF\">");
      sb.append(toHtmlAttachments());
      sb.append("</td></tr>");
      sb.append("</table>");
      sb.append("</td>");
    }
    sb.append("</tr>").append("\n");
    sb.append("</table>").append("\n");
    sb.append("</body>").append("\n");
    sb.append("</html>").append("\n");

    return sb.toString();
  }

  /**
   * @return
   */
  private String toHtmlAttachments() {
    StringBuilder sb = new StringBuilder();
    if (listAttDetail != null) {
      for(AttachmentDetail attDetail : listAttDetail) {
        sb.append(toHtmlAttachmentInfos(attDetail));
      }
    }
    return sb.toString();
  }

  /**
   * @param attDetail
   * @return
   */
  public static String toHtmlAttachmentInfos(AttachmentDetail attDetail) {
    String htmlLogicalName = attDetail.getLogicalName();
    String htmlFormatedFileSize = HtmlExportGenerator.encode(FileRepositoryManager.formatFileSize(attDetail.
        getSize()));
    StringBuilder sb = new StringBuilder();

    sb.append("&#149;&nbsp;");
    sb.append("<a href=\"").append(
        FileServerUtils.replaceAccentChars(htmlLogicalName)).append("\">");
    sb.append(FileServerUtils.replaceAccentChars(htmlLogicalName)).append("</a>&nbsp;");
    sb.append(htmlFormatedFileSize).append("<br>\n");
    if (attDetail.getTitle() != null) {
      sb.append("<I>").append(attDetail.getTitle()).append("<I>");
      if (attDetail.getInfo() != null) {
        sb.append(" - ").append("<I>").append(
            HtmlExportGenerator.encode(attDetail.getInfo())).append("</I>");
      }
      sb.append("<br/>\n");
    } else if (attDetail.getInfo() != null) {
      sb.append("<I>").append(HtmlExportGenerator.encode(attDetail.getInfo())).append("</I>").append(
          "<BR>\n");
    }

    return sb.toString();
  }

  /**
   * @param htmlText
   * @return
   */
  public static String replaceImagesPathForExport(String htmlText) {
    if (!StringUtil.isDefined(htmlText)) {
      return htmlText;
    }

    String lowerHtml = htmlText.toLowerCase();
    int finPath = 0;
    int debutPath = 0;
    StringBuffer newHtmlText = new StringBuffer();
    String imageSrc = "";
    if (lowerHtml.indexOf("src=\"", finPath) == -1) {
      // pas d'images dans le fichier
      return htmlText;
    } else {
      while ((debutPath = lowerHtml.indexOf("src=\"", finPath)) != -1) {
        debutPath += 5;

        newHtmlText.append(htmlText.substring(finPath, debutPath));
        finPath = lowerHtml.indexOf("\"", debutPath);
        imageSrc = lowerHtml.substring(debutPath, finPath);
        int d = imageSrc.indexOf("/fileserver/");
        if (d != -1) {
          // C'est une image stockée dans Silverpeas : extraction du nom de
          // l'image
          d += 12;
          int f = imageSrc.indexOf("?");
          imageSrc = imageSrc.substring(d, f);
          newHtmlText.append(imageSrc);
        } else {
          newHtmlText.append(htmlText.substring(debutPath, finPath));
        }
      }
      newHtmlText.append(htmlText.substring(finPath, htmlText.length()));
    }
    return newHtmlText.toString();
  }

  private String replaceFilesPathForExport(String htmlText) {
    String lowerHtml = htmlText.toLowerCase();
    int finPath = 0;
    int debutPath = 0;
    StringBuffer newHtmlText = new StringBuffer();
    String imageSrc = "";
    if (lowerHtml.indexOf("href=\"", finPath) == -1) {
      // pas d'images dans le fichier
      return htmlText;
    } else {
      while ((debutPath = lowerHtml.indexOf("href=\"", finPath)) != -1) {
        debutPath += 6;

        newHtmlText.append(htmlText.substring(finPath, debutPath));
        finPath = lowerHtml.indexOf("\"", debutPath);
        imageSrc = lowerHtml.substring(debutPath, finPath);
        int d = imageSrc.indexOf("/fileserver/");
        if (d != -1) {
          // C'est une image stockée dans Silverpeas : extraction du nom de
          // l'image
          d += 12;
          int f = imageSrc.indexOf("?");
          imageSrc = imageSrc.substring(d, f);
          newHtmlText.append(imageSrc);
        } else {
          newHtmlText.append(htmlText.substring(debutPath, finPath));
        }
      }
      newHtmlText.append(htmlText.substring(finPath, htmlText.length()));
    }
    return newHtmlText.toString();
  }
}
