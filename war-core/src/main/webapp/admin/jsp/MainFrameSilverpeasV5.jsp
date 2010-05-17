<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have received a copy of the text describing
    the FLOSS exception, and it is also available here:
    "http://repository.silverpeas.com/legal/licensing"

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>

<%
	response.setHeader( "Expires", "Tue, 21 Dec 1993 23:59:59 GMT" );
	response.setHeader( "Pragma", "no-cache" );
	response.setHeader( "Cache-control", "no-cache" );
	response.setHeader( "Last-Modified", "Fri, Jan 25 2099 23:59:59 GMT" );
	response.setStatus( HttpServletResponse.SC_CREATED );
%>
<%@ include file="importFrameSet.jsp" %>
<%@ include file="usefullFunctions.jsp" %>
<%@ page import="com.silverpeas.util.StringUtil"%>
<%@ page import="com.silverpeas.look.LookSilverpeasV5Helper"%>

<%
String			componentIdFromRedirect = (String) session.getValue("RedirectToComponentId");
String			spaceIdFromRedirect 	= (String) session.getValue("RedirectToSpaceId");
if (!StringUtil.isDefined(spaceIdFromRedirect))
	spaceIdFromRedirect 	= request.getParameter("RedirectToSpaceId");
String			attachmentId		 	= (String) session.getValue("RedirectToAttachmentId");
ResourceLocator generalMessage			= new ResourceLocator("com.stratelia.webactiv.multilang.generalMultilang", language);
String			topBarParams			= "";
String			frameBottomParams		= "";
boolean			login					= false;

//System.out.println("attachmentId = "+attachmentId);

if (m_MainSessionCtrl == null)
{
%>
	<script> 
		top.location="../../Login.jsp";
	</script>
<%
}
else
{	
	LookSilverpeasV5Helper 	helper 	= (LookSilverpeasV5Helper) session.getAttribute("Silverpeas_LookHelper");
	if (helper == null)
	{
		helper = new LookSilverpeasV5Helper(m_MainSessionCtrl, gef.getFavoriteLookSettings());
		helper.setMainFrame("MainFrameSilverpeasV5.jsp");
		
		session.setAttribute("Silverpeas_LookHelper", helper);
		login = true;
	}
	
	boolean componentExists = false;
	if (StringUtil.isDefined(componentIdFromRedirect))
		componentExists = (organizationCtrl.getComponentInstLight(componentIdFromRedirect) != null);
		
	//System.out.println("componentExists = "+componentExists);
	
	if (!componentExists)
	{
		String spaceId = m_MainSessionCtrl.getFavoriteSpace();
		//System.out.println("favoriteSpace = "+spaceId);
		
		boolean spaceExists = false;
		if (StringUtil.isDefined(spaceIdFromRedirect))
			spaceExists = (organizationCtrl.getSpaceInstById(spaceIdFromRedirect) != null);
		
		//System.out.println("spaceExists = "+spaceExists+" for spaceId = "+spaceIdFromRedirect);
		
		if (spaceExists)
		{
			spaceId = spaceIdFromRedirect;
		}
		else
		{
			if (helper != null && helper.getSpaceId() != null) {
				spaceId = helper.getSpaceId();
			}
		}
		//System.out.println("MainFrame : spaceId = "+spaceId);
		helper.setSpaceIdAndSubSpaceId(spaceId);
		
		String 	workSpace 	= "?SpaceId="+spaceId;
		frameBottomParams 	= workSpace;
	}
	else
	{
		helper.setComponentIdAndSpaceIds(null, null, componentIdFromRedirect);
		frameBottomParams 	= "?SpaceId=&ComponentId="+componentIdFromRedirect;
	}
	
	if (login)
		frameBottomParams += "&Login=1";
	
	if (!"MainFrameSilverpeasV5.jsp".equalsIgnoreCase(helper.getMainFrame()))
	{
		session.putValue("RedirectToSpaceId", spaceIdFromRedirect);
		%>
			<script>
				top.location="<%=helper.getMainFrame()%>";
			</script>
		<%
	}
	
	String framesetRows = "0,115,*,0,0";
	if (helper.displayPDCFrame())
		framesetRows = "0,115,*,0,0,26";
%>

<html>
<head>
<title><%=generalMessage.getString("GML.popupTitle")%></title>
<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/util/icons/favicon.ico">
<script type="text/javascript" src="<%=m_sContext%>/util/javaScript/animation.js"></script>
<script language="javascript">
<!--
var columntype=""
var defaultsetting=""

function getCurrentSetting(){
	if (document.body)
		return (document.body.cols)? document.body.cols : document.body.rows
}

function setframevalue(coltype, settingvalue){
	if (coltype=="rows")
		document.body.rows=settingvalue
	else if (coltype=="cols")
		document.body.cols=settingvalue
}

function resizeFrame(contractsetting){
	if (getCurrentSetting()!=defaultsetting)
		setframevalue(columntype, defaultsetting)
	else
		setframevalue(columntype, contractsetting)
}

function init(){
	if (!document.all && !document.getElementById) return
	if (document.body!=null){
		columntype=(document.body.cols)? "cols" : "rows"
		defaultsetting=(document.body.cols)? document.body.cols : document.body.rows
	} 
	else
		setTimeout("init()",100)
}

function showPdcFrame() {
	resizeFrame("0,115,*,0,0,26");
}

function hidePdcFrame() {
	resizeFrame("0,115,*,0,0");
}

setTimeout("init()",100);

//-->
</script>
</head>
<% if (attachmentId != null) 
   {
   	session.putValue("RedirectToAttachmentId", null);
   	String mapping = (String) session.getValue("RedirectToMapping");
%>
	<script language="javascript">
		SP_openWindow('<%=m_sContext%>/<%=mapping%>/<%=attachmentId%>', 'Fichier', '800', '600', 'directories=0,menubar=1,toolbar=1,scrollbars=1,location=1,alwaysRaised');
	</script>
<% } %>

<frameset rows="<%=framesetRows%>" border="0" framespacing="0" frameborder="NO" id="mainFramesetId">
  	<frame src="../../clipboard/jsp/IdleSilverpeasV5.jsp" name="IdleFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
  	<frame src="TopBarSilverpeasV5.jsp" name="topFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
  	<frame src="frameBottomSilverpeasV5.jsp<%=frameBottomParams%>" name="bottomFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
	<frame src="javascript.htm" name="scriptFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
	<frame src="<%=m_sContext%>/Ragenda/jsp/importCalendar" name="importFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
	<% if (helper.displayPDCFrame()) { %>
		<frame src="<%=m_sContext%>/RpdcSearch/jsp/ChangeSearchTypeToExpert?SearchPage=/admin/jsp/pdcSearchSilverpeasV5.jsp" name="pdcFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="0">
	<% } %>
</frameset><noframes></noframes>
</html>
<%
}
%>