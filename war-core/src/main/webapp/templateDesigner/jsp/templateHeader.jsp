<%@ include file="check.jsp" %>
<%
PublicationTemplate template = (PublicationTemplate) request.getAttribute("Template");

String name = "";
String description = "";
String thumbnail = "";
String fileName = "";
String visible = "";
String searchable = "";
String action = "AddTemplate";

if (template != null)
{
	name = template.getName();
	description = template.getDescription();
	thumbnail = template.getThumbnail();
	fileName = template.getFileName();
	if (template.isVisible())
		visible = "checked";
	if (template.getSearchForm() != null)
		searchable = "checked";
	action = "UpdateTemplate";
}

Button validateButton 	= (Button) gef.getFormButton(resource.getString("GML.validate"), "javascript:onClick=sendData();", false);
Button cancelButton 	= (Button) gef.getFormButton(resource.getString("GML.cancel"), "Main", false);
%>
<html>
<head>
<%
	out.println(gef.getLookStyleSheet());
%>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>
<script language="javascript">
	
	function sendData() 
	{
		if (isCorrectForm()) 
		{
			document.templateForm.submit();
		}
	}

	function isCorrectForm() 
	{
     	var errorMsg = "";
     	var errorNb = 0;
     	var title = stripInitialWhitespace(document.templateForm.Name.value);
     	if (title == "") 
     	{
           	errorMsg+="  - '<%=resource.getString("GML.name")%>' <%=resource.getString("GML.MustBeFilled")%>\n";
           	errorNb++;
     	} 
     	switch(errorNb) 
     	{
        	case 0 :
            	result = true;
            	break;
        	case 1 :
            	errorMsg = "<%=resource.getString("GML.ThisFormContains")%> 1 <%=resource.getString("GML.error")%> : \n" + errorMsg;
            	window.alert(errorMsg);
            	result = false;
            	break;
        	default :
            	errorMsg = "<%=resource.getString("GML.ThisFormContains")%> " + errorNb + " <%=resource.getString("GML.errors")%> :\n" + errorMsg;
            	window.alert(errorMsg);
            	result = false;
            	break;
     	} 
     	return result;
	}
</script>
</head>
<body>
<%
browseBar.setDomainName(resource.getString("templateDesigner.toolName"));
browseBar.setComponentName(resource.getString("templateDesigner.templateList"), "Main");
browseBar.setPath(resource.getString("templateDesigner.template"));

TabbedPane tabbedPane = gef.getTabbedPane();
if (template != null)
	tabbedPane.addTab(resource.getString("GML.preview"), "ViewTemplate", false);
tabbedPane.addTab(resource.getString("templateDesigner.template"), "#", true);
if (template != null)
	tabbedPane.addTab(resource.getString("templateDesigner.fields"), "ViewFields", false);

out.println(window.printBefore());

out.println(tabbedPane.print());
out.println(frame.printBefore());
out.println(board.printBefore());
%>
<TABLE CELLPADDING=5 WIDTH="100%">
<form name="templateForm" action="<%=action%>" method="POST">
<tr>
<td class="txtlibform"><%=resource.getString("GML.name")%> :</td><td><input type="text" name="Name" value="<%=name%>" size="60"/><input type="hidden" name="Scope" value="0"/>&nbsp;<img border="0" src="<%=resource.getIcon("templateDesigner.mandatory")%>" width="5" height="5"></td>
</tr>
<% if (template != null) { %>
<tr>
<td class="txtlibform"><%=resource.getString("templateDesigner.file")%> :</td><td><%=fileName%></td>
</tr>
<% } %>
<tr>
<td class="txtlibform"><%=resource.getString("GML.description")%> :</td><td><input type="text" name="Description" value="<%=description%>" size="60"/></td>
</tr>
<tr>
<td class="txtlibform"><%=resource.getString("templateDesigner.visible")%> :</td><td><input type="checkbox" name="Visible" value="true" <%=visible%>/></td>
</tr>
<tr>
<td class="txtlibform"><%=resource.getString("templateDesigner.searchable")%> :</td><td><input type="checkbox" name="Searchable" value="true" <%=searchable%>/></td>
</tr>
<tr>
<td class="txtlibform"><%=resource.getString("templateDesigner.image")%> :</td><td><input type="text" name="Image" value="<%=thumbnail%>" size="60"/></td>
</tr>
</form>
</table>
<%
out.println(board.printAfter());

ButtonPane buttonPane = gef.getButtonPane();
buttonPane.addButton(validateButton);
buttonPane.addButton(cancelButton);
out.println("<BR><center>"+buttonPane.print()+"</center><BR>");

out.println(frame.printAfter());
out.println(window.printAfter());
%>
</body>
</html>