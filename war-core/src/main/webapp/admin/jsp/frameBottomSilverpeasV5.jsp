<%@ page import="com.stratelia.webactiv.beans.admin.SpaceInstLight"%>
<%@ page import="com.silverpeas.look.LookSilverpeasV5Helper"%>
<%@ include file="importFrameSet.jsp" %>

<%
String strGoToNew 	= (String) session.getValue("gotoNew");
String spaceId 		= request.getParameter("SpaceId");
String subSpaceId 	= request.getParameter("SubSpaceId");
String fromTopBar 	= request.getParameter("FromTopBar");
String componentId	= request.getParameter("ComponentId");
String login		= request.getParameter("Login");

LookSilverpeasV5Helper 	helper = (LookSilverpeasV5Helper) session.getAttribute("Silverpeas_LookHelper");

/*System.out.println("frameBottom : spaceId = "+spaceId);
System.out.println("frameBottom : componentId = "+componentId);*/

ResourceLocator rsc = gef.getFavoriteLookSettings();
int framesetWidth = Integer.parseInt(rsc.getString("domainsBarFramesetWidth"));

String paramsForDomainsBar = "";
if (fromTopBar != null && fromTopBar.equals("1")) {
	paramsForDomainsBar = (spaceId == null) ? "" : "?privateDomain="+spaceId+"&privateSubDomain="+subSpaceId+"&FromTopBar=1";
} 
else if (componentId != null) 
{
	paramsForDomainsBar = "?privateDomain=&component_id="+componentId;
} 
else
{
	paramsForDomainsBar = "?privateDomain="+spaceId;
	/*if (spaceId != null && spaceId.length() > 0 && !"null".equals(spaceId))
	{
		SpaceInstLight rootSpace = m_MainSessionCtrl.getOrganizationController().getRootSpace(spaceId);
		String rootSpaceId = "";
		if (rootSpace != null)
			rootSpaceId = rootSpace.getFullId();
		
		paramsForDomainsBar = "?privateDomain="+rootSpaceId;
		if (!rootSpaceId.equals(spaceId))
			paramsForDomainsBar += "&privateSubDomain="+spaceId;
	}*/
}

//Allow to force a page only on login and when user clicks on logo
boolean displayLoginHomepage = false;
String loginHomepage = rsc.getString("loginHomepage");
if (StringUtil.isDefined(loginHomepage) && (StringUtil.isDefined(login) || (!StringUtil.isDefined(spaceId) && !StringUtil.isDefined(subSpaceId) && !StringUtil.isDefined(componentId) && !StringUtil.isDefined(strGoToNew))))
	displayLoginHomepage = true;

String frameURL = "";
if (displayLoginHomepage)
{
	frameURL = loginHomepage;
}
else if (strGoToNew==null)
{
	if (StringUtil.isDefined(componentId))
	{
		frameURL = URLManager.getApplicationURL()+URLManager.getURL(null, componentId)+"Main";
	}
	else
	{
		String homePage = rsc.getString("defaultHomepage", "/dt");
		String param = "";
		if (spaceId != null && spaceId.length() >= 3){
		    param = "?SpaceId=" + spaceId;
		}
		frameURL = URLManager.getApplicationURL()+homePage+param;
	}
} else {
	frameURL = URLManager.getApplicationURL()+strGoToNew;
} 

//System.out.println("frameBottom : frameURL = "+frameURL);

session.putValue("goto",null);
session.putValue("gotoNew", null);
session.putValue("RedirectToComponentId", null);
session.putValue("RedirectToSpaceId", null);
%>


<%@page import="com.silverpeas.util.StringUtil"%><html>
<head>
<script language="javascript">
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

setTimeout("init()",100);

</script>
<script language="javascript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
//-->
</script>
</head>
	<script language=javascript>
		<!--
		if (navigator.appName == 'Netscape')
		  	document.write('<frameset cols="<%=framesetWidth-18%>,*">');
		else
			document.write('<frameset cols="<%=framesetWidth%>,*">');
		-->
	</script>
	<frame src="DomainsBarSilverpeasV5.jsp<%=paramsForDomainsBar%>" marginwidth="0" marginheight="10" name="SpacesBar" frameborder="NO" scrolling="AUTO">
	<frame src="<%=frameURL%>" marginwidth="10" name="MyMain" marginheight="10" frameborder="NO" scrolling="AUTO">
  </frameset>
<noframes></noframes>
</html>