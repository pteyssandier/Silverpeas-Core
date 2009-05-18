package com.silverpeas.form.fieldType;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silverpeas.form.PagesContext;
import com.silverpeas.util.EncodeHelper;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DBUtil;
import com.stratelia.webactiv.util.GeneralPropertiesManager;
import com.stratelia.webactiv.util.JNDINames;

/**
 * A TextFieldImpl stores use a String attribute to store its value.
 */
public class TextFieldImpl extends TextField
{
  private String value = "";
  
  private static final String suggestionsQuery = "select distinct(f.fieldValue)"
	  			+ " from sb_formtemplate_template t, sb_formtemplate_record r, sb_formtemplate_textfield f"
	  			+ " where t.templateId = r.templateId"
	  			+ " and r.recordId = f.recordId"
	  			+ " and f.fieldName = ?"
	  			+ " and t.externalId = ?"
	  			+ " order by f.fieldValue asc"; 
   
  public TextFieldImpl() {}
  /**
   * Returns the string value of this field.
   */
  public String getStringValue()
  {
     return value;
  }

  /**
   * Set the string value of this field.
   */
  public void setStringValue(String value)
  {
     this.value = value;
  }

  /**
   * Returns true if the value is read only.
   */
  public boolean isReadOnly()
  {
     return false;
  }
  
  public List getSuggestions(String fieldName, String templateName, String componentId)
  {
	  List suggestions = new ArrayList();
	  
	  Connection connection = null;
	  PreparedStatement statement = null;
	  ResultSet rs = null;
	  try {
		  connection = DBUtil.makeConnection(JNDINames.FORMTEMPLATE_DATASOURCE);
		  
		  statement = connection.prepareStatement(suggestionsQuery);
		  statement.setString(1, fieldName);
		  statement.setString(2, componentId+":"+templateName);
		  
		  SilverTrace.debug("formTemplate", "TextFieldImpl.getSuggestions", "root.MSG_GEN_PARAM_VALUE", "fieldName = "+fieldName+", componentId = "+componentId+", templateName = "+templateName);
		  
		  rs = statement.executeQuery();
		  
		  String oneSuggestion = "";
		  while (rs.next())
		  {
			  oneSuggestion = rs.getString(1);
			  if (StringUtil.isDefined(oneSuggestion))
				  suggestions.add(oneSuggestion);
		  }
	  } catch (Exception e) {
		  SilverTrace.error("formTemplate", "TextFieldImpl.getSuggestions", "root.EX_SQL_QUERY_FAILED", e);	
	  }
	  finally
	  {
		  DBUtil.close(rs, statement);
		  try {
			  if (connection != null && !connection.isClosed())
				  connection.close();
		  } catch (SQLException e) {
			  SilverTrace.error("formTemplate", "TextFieldImpl.getSuggestions", "root.EX_CONNECTION_CLOSE_FAILED", e);	
		  }
	  }
	  return suggestions;
  }
  
  public static void printSuggestionsIncludes(PagesContext pageContext, String fieldName, PrintWriter out)
  {
	  	String m_context = GeneralPropertiesManager.getGeneralResourceLocator().getString("ApplicationURL");
		int zindex = (pageContext.getLastFieldIndex() - new Integer(pageContext.getCurrentFieldIndex()).intValue()) * 9000;
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+m_context+"/util/yui/fonts/fonts-min.css\" />");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+m_context+"/util/yui/autocomplete/assets/skins/sam/autocomplete.css\" />");
		out.println("<script type=\"text/javascript\" src=\""+m_context+"/util/yui/yahoo-dom-event/yahoo-dom-event.js\"></script>");
		out.println("<script type=\"text/javascript\" src=\""+m_context+"/util/yui/animation/animation-min.js\"></script>\n");
		out.println("<script type=\"text/javascript\" src=\""+m_context+"/util/yui/autocomplete/autocomplete-min.js\"></script>\n");
		out.println("<style type=\"text/css\">\n");
		out.println("	#listAutocomplete"+fieldName+" {\n");
		out.println("		width:15em;\n");
		out.println("		padding-bottom:2em;\n");
		out.println("	}\n");
		out.println("	#listAutocomplete"+fieldName+" {\n");
		out.println("		z-index:"+zindex+"; /* z-index needed on top instance for ie & sf absolute inside relative issue */\n");
		out.println("	}\n");
		out.println("	#"+fieldName+" {\n");
		out.println("		_position:absolute; /* abs pos needed for ie quirks */\n");
		out.println("	}\n");
		out.println("</style>\n");
  }
  
  public static void printSuggestionsScripts(PagesContext pageContext, String fieldName, List suggestions, PrintWriter out)
  {
	  	out.println("<script type=\"text/javascript\">\n");
		out.println("listArray"+fieldName+" = [\n");
	
		Iterator itRes = suggestions.iterator();
		String val;
		while(itRes.hasNext()) {
			val = (String) itRes.next();
			
			out.println("\""+EncodeHelper.javaStringToJsString(val)+"\"");
			
			if (itRes.hasNext())
				out.println(",");
		}
	
		out.println("];\n");
		out.println("</script>\n");
		
		out.println("<script type=\"text/javascript\">\n");
		out.println("	this.oACDS"+fieldName+" = new YAHOO.widget.DS_JSArray(listArray"+fieldName+");\n");
		out.println("	this.oAutoComp"+fieldName+" = new YAHOO.widget.AutoComplete('"+fieldName+"','container"+fieldName+"', this.oACDS"+fieldName+");\n");
		out.println("	this.oAutoComp"+fieldName+".prehighlightClassName = \"yui-ac-prehighlight\";\n");
		out.println("	this.oAutoComp"+fieldName+".typeAhead = true;\n");
		out.println("	this.oAutoComp"+fieldName+".useShadow = true;\n");
		out.println("	this.oAutoComp"+fieldName+".minQueryLength = 0;\n");
		
		out.println("	this.oAutoComp"+fieldName+".textboxFocusEvent.subscribe(function(){\n");
		out.println("		var sInputValue = YAHOO.util.Dom.get('"+fieldName+"').value;\n");
		out.println("		if(sInputValue.length == 0) {\n");
		out.println("			var oSelf = this;\n");
		out.println("			setTimeout(function(){oSelf.sendQuery(sInputValue);},0);\n");
		out.println("		}\n");
		out.println("	});\n");
		out.println("</script>\n");
  }
  
}
