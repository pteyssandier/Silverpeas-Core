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
package com.silverpeas.form.fieldDisplayer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import com.silverpeas.form.Field;
import com.silverpeas.form.FieldDisplayer;
import com.silverpeas.form.FieldTemplate;
import com.silverpeas.form.Form;
import com.silverpeas.form.FormException;
import com.silverpeas.form.PagesContext;
import com.silverpeas.form.Util;
import com.silverpeas.form.fieldType.TextField;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

/**
 * A CheckBoxDisplayer is an object which can display a checkbox in HTML the content of a checkbox
 * to a end user and can retrieve via HTTP any updated value.
 * @see Field
 * @see FieldTemplate
 * @see Form
 * @see FieldDisplayer
 */
public class CheckBoxDisplayer extends AbstractFieldDisplayer {

  /**
   * Constructeur
   */
  public CheckBoxDisplayer() {
  }

  /**
   * Returns the name of the managed types.
   */
  public String[] getManagedTypes() {
    String[] s = new String[0];
    s[0] = TextField.TYPE;
    return s;
  }

  /**
   * Prints the javascripts which will be used to control the new value given to the named field.
   * The error messages may be adapted to a local language. The FieldTemplate gives the field type
   * and constraints. The FieldTemplate gives the local labeld too. Never throws an Exception but
   * log a silvertrace and writes an empty string when :
   * <UL>
   * <LI>the fieldName is unknown by the template.
   * <LI>the field type is not a managed type.
   * </UL>
   */
  @Override
  public void displayScripts(PrintWriter out, FieldTemplate template, PagesContext pagesContext)
      throws java.io.IOException {
    String language = pagesContext.getLanguage();

    if (!template.getTypeName().equals(TextField.TYPE)) {
      SilverTrace.info("form", "CheckBoxDisplayer.displayScripts", "form.INFO_NOT_CORRECT_TYPE",
          TextField.TYPE);
    }

    if (template.isMandatory() && pagesContext.useMandatory()) {
      int currentIndex = new Integer(pagesContext.getCurrentFieldIndex()).intValue();
      int fin = currentIndex + getNbHtmlObjectsDisplayed(template, pagesContext);
      out.println("	var checked = false;\n");
      out.println("	for (var i = " + currentIndex + "; i < " + fin + "; i++) {\n");
      out.println("		if (document.forms[" + pagesContext.getFormIndex() +
          "].elements[i].checked) {\n");
      out.println("			checked = true;\n");
      out.println("		}\n");
      out.println("	}\n");
      out.println("	if(checked == false) {\n");
      out.println("		errorMsg+=\"  - '" + template.getLabel(language) + "' " +
          Util.getString("GML.MustBeFilled", language) + "\\n \";");
      out.println("		errorNb++;");
      out.println("	}");
    }

    Util.getJavascriptChecker(template.getFieldName(), pagesContext, out);
  }

  /**
   * Prints the HTML value of the field. The displayed value must be updatable by the end user. The
   * value format may be adapted to a local language. The fieldName must be used to name the html
   * form input. Never throws an Exception but log a silvertrace and writes an empty string when :
   * <UL>
   * <LI>the field type is not a managed type.
   * </UL>
   */
  @Override
  public void display(PrintWriter out, Field field, FieldTemplate template,
      PagesContext PagesContext) throws FormException {
    String selectedValues = "";
    List<String> valuesFromDB = new ArrayList<String>();
    String keys = "";
    String values = "";
    String html = "";
    int cols = 1;
    String language = PagesContext.getLanguage();
    String cssClass = null;

    String mandatoryImg = Util.getIcon("mandatoryField");

    String fieldName = template.getFieldName();
    Map<String, String> parameters = template.getParameters(language);

    if (!field.getTypeName().equals(TextField.TYPE)) {
      SilverTrace.info("form", "CheckBoxDisplayer.display", "form.INFO_NOT_CORRECT_TYPE",
          TextField.TYPE);
    }

    if (!field.isNull()) {
      selectedValues = field.getValue(language);
    }

    StringTokenizer st = new StringTokenizer(selectedValues, "##");
    while (st.hasMoreTokens()) {
      valuesFromDB.add(st.nextToken());
    }

    if (parameters.containsKey("keys")) {
      keys = parameters.get("keys");
    }

    if (parameters.containsKey("values")) {
      values = parameters.get("values");
    }

    if (parameters.containsKey("class")) {
      cssClass = (String) parameters.get("class");
      if (StringUtil.isDefined(cssClass))
        cssClass = "class=\"" + cssClass + "\"";
    }

    try {
      if (parameters.containsKey("cols")) {
        cols = (Integer.valueOf(parameters.get("cols"))).intValue();
      }
    } catch (NumberFormatException nfe) {
      SilverTrace.error("form", "CheckBoxDisplayer.display", "form.EX_ERR_ILLEGAL_PARAMETER_COL",
          (String) parameters.get("cols"));
      cols = 1;
    }

    // if either keys or values is not filled
    // take the same for keys and values
    if (keys.equals("") && !values.equals("")) {
      keys = values;
    }
    if (values.equals("") && !keys.equals("")) {
      values = keys;
    }

    StringTokenizer stKeys = new StringTokenizer(keys, "##");
    StringTokenizer stValues = new StringTokenizer(values, "##");
    String optKey = "";
    String optValue = "";
    int nbTokens = getNbHtmlObjectsDisplayed(template, PagesContext);

    if (stKeys.countTokens() != stValues.countTokens()) {
      SilverTrace.error("form", "CheckBoxDisplayer.display", "form.EX_ERR_ILLEGAL_PARAMETERS",
          "Nb keys=" + stKeys.countTokens() + " & Nb values=" + stValues.countTokens());
    } else {
      html += "<table border=0>";
      int col = 0;
      for (int i = 0; i < nbTokens; i++) {
        if (col == 0) {
          html += "<tr>";
        }

        col++;
        html += "<td>";
        optKey = stKeys.nextToken();
        optValue = stValues.nextToken();

        html +=
            "<INPUT type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" value=\"" + optKey + "\" ";

        if (template.isDisabled() || template.isReadOnly()) {
          html += " disabled ";
        }

        if (valuesFromDB.contains(optKey)) {
          html += " checked ";
        }

        html += ">&nbsp;" + optValue;

        if (StringUtil.isDefined(cssClass))
          html += "</span>";

        // last checkBox
        if (i == nbTokens - 1) {
          if (template.isMandatory() && !template.isDisabled() && !template.isReadOnly() &&
              !template.isHidden() && PagesContext.useMandatory()) {
            html +=
                "&nbsp;<img src=\"" + mandatoryImg + "\" width=\"5\" height=\"5\" border=\"0\">";
          }
        }

        html += "\n";

        if (col == cols) {
          html += "<tr>";
          col = 0;
        }
      }

      if (col != 0) {
        html += "</tr>";
      }

      html += "</table>";
    }
    out.println(html);
  }

  @Override
  public List<String> update(List<FileItem> items, Field field, FieldTemplate template,
      PagesContext pageContext) throws FormException {
    SilverTrace.debug("form", "AbstractForm.getParameterValues", "root.MSG_GEN_ENTER_METHOD",
        "parameterName = " + template.getFieldName());
    String value = "";
    Iterator<FileItem> iter = items.iterator();
    String parameterName = template.getFieldName();
    while (iter.hasNext()) {
      FileItem item = iter.next();
      if (parameterName.equals(item.getFieldName())) {
        value += item.getString();
        if (iter.hasNext()) {
          value += "##";
        }
      }
    }
    SilverTrace.debug("form", "AbstractForm.getParameterValues", "root.MSG_GEN_EXIT_METHOD",
        "parameterValue = " + value);
    if (pageContext.getUpdatePolicy() == PagesContext.ON_UPDATE_IGNORE_EMPTY_VALUES &&
        !StringUtil.isDefined(value)) {
      return new ArrayList<String>();
    }
    return update(value, field, template, pageContext);
  }

  @Override
  public List<String> update(String values, Field field, FieldTemplate template,
      PagesContext PagesContext) throws FormException {

    if (!field.getTypeName().equals(TextField.TYPE)) {
      throw new FormException("CheckBoxDisplayer.update", "form.EX_NOT_CORRECT_TYPE",
          TextField.TYPE);
    }

    String valuesToInsert = values;

    if (field.acceptValue(valuesToInsert, PagesContext.getLanguage())) {
      field.setValue(valuesToInsert, PagesContext.getLanguage());
    } else {
      throw new FormException("CheckBoxDisplayer.update", "form.EX_NOT_CORRECT_VALUE",
          TextField.TYPE);
    }
    return new ArrayList<String>();
  }

  @Override
  public boolean isDisplayedMandatory() {
    return true;
  }

  @Override
  public int getNbHtmlObjectsDisplayed(FieldTemplate template, PagesContext pagesContext) {
    String keys = "";
    String values = "";
    Map<String, String> parameters = template.getParameters(pagesContext.getLanguage());
    if (parameters.containsKey("keys")) {
      keys = parameters.get("keys");
    }
    if (parameters.containsKey("values")) {
      values = parameters.get("values");
    }

    // if either keys or values is not filled
    // take the same for keys and values
    if (keys.equals("") && !values.equals("")) {
      keys = values;
    }
    if (values.equals("") && !keys.equals("")) {
      values = keys;
    }

    // Calculate numbers of html elements
    StringTokenizer stKeys = new StringTokenizer(keys, "##");
    return stKeys.countTokens();

  }
}
