<%@ include file="checkPdc.jsp"%>
<%
    String Id = (String) request.getAttribute("uniqueId"); // pour la surbrillance
    String component_id = (String) request.getAttribute("component_id");
%>
<html>
<HEAD>
<TITLE><%=resource.getString("GML.popupTitle")%></TITLE>
<%
   out.println(gef.getLookStyleSheet());
%>
<SCRIPT type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></SCRIPT>
<script type="text/javascript">
<!--
function MM_reloadPage(init) {  //Updated by PVII. Reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) history.go(0);
}

MM_reloadPage(true);

function submitForm() {
	var type;
	if (document.all.query.value == "" && document.all.search[0].checked)  {
		document.all.query.value = "*";
	}
	if (document.all.search[0].checked)
		type=document.all.search[0].value;
	else
		type=document.all.search[1].value;
	
	location.replace("<%=m_context%>/RpdcSearch/jsp/searchResult?uniqueId=<%=Id%>&type="+type+"&query="+escape(document.all.query.value)+"&component_id=<%=component_id%>");
}
//-->
</script>
</HEAD>
<BODY marginheight=5 marginwidth=5 leftmargin=5 topmargin=5 bgcolor="#FFFFFF">

<%
	browseBar.setDomainName(resource.getString("pdcPeas.pdc"));
    browseBar.setComponentName(resource.getString("pdcPeas.pdcConsultation"));
    out.println(window.printBefore());

	TabbedPane tabbedPane = gef.getTabbedPane();
	tabbedPane.addTab(resource.getString("pdcPeas.Navigation"),"AxisTree?uniqueId="+Id+"&component_id="+component_id,false);
    tabbedPane.addTab(resource.getString("GML.search"),"#",true);
	out.println(tabbedPane.print());

    out.println(frame.printBefore());
%>
<CENTER>
<table width="98%" border="0" cellspacing="0" cellpadding="0" class=intfdcolor4>
<tr><td>
<table border="0" cellspacing="0" cellpadding="5" class="contourintfdcolor" width="100%">
<tr><td align="center">
<table border="0" cellspacing="0" cellpadding="5" class="intfdcolor4" width="100%">
<tr>
<td valign="top" nowrap align="left"><span class="txtlibform"><%=resource.getString("pdcPeas.requete")%> :</span></td>
<td align="left"><input type="text" name="query" size="50" value=""></td>
<td width="1%"><%
  out.println("<CENTER>");
  ButtonPane buttonPane = gef.getButtonPane();
  Button validateButton = (Button) gef.getFormButton(resource.getString("GML.search"), "javascript:submitForm()", false);
  buttonPane.addButton(validateButton);
  buttonPane.setHorizontalPosition();
  out.println(buttonPane.print());
  out.println("</CENTER>");
%>
</td><td width="100%"></td>
</tr>
<tr>
<td valign="top" align="absmiddle" nowrap><span class="txtlibform"><%=resource.getString("pdcPeas.typerecherche")%> :</span></td>
<td align="left" valign="top" align="absmiddle"><input type="radio" name="search" value="filter" align="absmiddle" checked>&nbsp;<%=resource.getString("pdcPeas.filtre")%>&nbsp;&nbsp;&nbsp;<input type="radio" name="search" value="fullText" align="absmiddle">&nbsp;<%=resource.getString("pdcPeas.fullText")%>&nbsp;
</td><td></td><td></td>
</tr></table></td></tr>
</table></td></tr>
</table>
</center>
<%
	out.println(frame.printAfter());
	out.println(window.printAfter());
%>
<form name="refresh" action="searchResult" method="post">
<input type="hidden" name="ValueId" >
</form>
</BODY>
</HTML>