<%@ include file="checkSilverStatistics.jsp" %>

<%
// Recuperation des parametres
ArrayLine arrayLine = null;
Iterator   iter = null;
Collection cResultData = (Collection)request.getAttribute("ConnectedUsersList");
String userProfile = (String)request.getAttribute("UserProfile");

%>

<%
	TabbedPane tabbedPane = gef.getTabbedPane();
	tabbedPane.addTab(resources.getString("silverStatisticsPeas.usersWithSession"), m_context+"/RsilverStatisticsPeas/jsp/Main",true);
	tabbedPane.addTab(resources.getString("silverStatisticsPeas.connectionNumber"), m_context+"/RsilverStatisticsPeas/jsp/ViewConnections",false);
	tabbedPane.addTab(resources.getString("silverStatisticsPeas.connectionFrequence"), m_context+"/RsilverStatisticsPeas/jsp/ViewFrequence",false);
%>


<HTML>
<HEAD>
<TITLE><%=resources.getString("GML.popupTitle")%></TITLE>
<%
   out.println(gef.getLookStyleSheet());
%>
<!--[ JAVASCRIPT ]-->
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>

<SCRIPT LANGUAGE="JAVASCRIPT">
<!--
	// This function open a silverpeas window
	function openSPWindow(fonction,windowName){
		SP_openWindow(fonction, windowName, '750', '250','scrollbars=yes, resizable, alwaysRaised');
	}

    function ConfirmAndSend(targetURL,textToDisplay)
    {
        if (window.confirm(textToDisplay))
        {
            window.location.href = targetURL;
        }
    }

//--------------------------------------------------------------------------------------DoIdle
ID = window.setTimeout("DoIdle();", 60000);
function DoIdle()
{ self.location.href = "Main"; }

//-->
</SCRIPT>

</HEAD>
<BODY marginheight=5 marginwidth=5 leftmargin=5 topmargin=5>

<%
	browseBar.setDomainName(resources.getString("silverStatisticsPeas.statistics"));
    browseBar.setComponentName(resources.getString("silverStatisticsPeas.Connections"));
    browseBar.setPath(resources.getString("silverStatisticsPeas.usersWithSession"));
    
	operationPane.addOperation(resources.getIcon("silverStatisticsPeas.icoNotifyAll"),resources.getString("silverStatisticsPeas.notifyAllUser"),"javascript:openSPWindow('DisplayNotifyAllSessions','DisplayNotifyAllSessions')");
	
    out.println(window.printBefore());
    if (userProfile.equals("A"))
    {
		out.println(tabbedPane.print());
    }
    out.println(frame.printBefore());
%>
<CENTER>
<%
		  // Tableau
          ArrayPane arrayPane = gef.getArrayPane("List", "", request,session);

		  if (cResultData != null)
			  arrayPane.setTitle(cResultData.size()+" "+resources.getString("silverStatisticsPeas.usersWithSession"));

		  ArrayColumn arrayColumn1 = arrayPane.addArrayColumn("");
		  arrayColumn1.setSortable(false);

          arrayPane.addArrayColumn(resources.getString("silverStatisticsPeas.ip"));
          arrayPane.addArrayColumn(resources.getString("GML.login"));
          arrayPane.addArrayColumn(resources.getString("silverStatisticsPeas.duration"));

          arrayColumn1 = arrayPane.addArrayColumn(resources.getString("silverStatisticsPeas.Actions")+"</A>");
          arrayColumn1.setSortable(false);

        ArrayCellText cellText;         

        if (cResultData != null)
        {
            long currentTime = new Date().getTime();        
        	iter = cResultData.iterator();
        	while (iter.hasNext())
        	{
            	SessionInfo item = (SessionInfo) iter.next();

          		arrayLine = arrayPane.addArrayLine();

                arrayLine.addArrayCellText("<div align=right><img src=\""+resources.getIcon("silverStatisticsPeas.icoMonitor")+"\" border=0></div>");

          		arrayLine.addArrayCellText(item.m_IP);
				
				arrayLine.addArrayCellText(item.getLog());

				long duration = currentTime - item.m_DateBegin;
				cellText = arrayLine.addArrayCellText(item.formatDuration(duration));
				cellText.setCompareOn(new Long(duration));

                arrayLine.addArrayCellText("<div align=left><a href=#><img src=\""+resources.getIcon("silverStatisticsPeas.icoNotifySession")+"\" border=0 onclick=\"javascript:openSPWindow('DisplayNotifySession?theUserId=" + item.m_User.getId() + "','DisplayNotifySession')\"></A>&nbsp;<a href=\"javascript:ConfirmAndSend('KickSession?theSessionId=" + URLEncoder.encode(item.m_SessionId) + "','" + Encode.javaStringToJsString(resources.getString("silverStatisticsPeas.ConfirmKickSession") + item.m_User.getLogin() + " (" + item.m_User.getLastName() + " " + item.m_User.getFirstName()) + ") ?')\"><img src=\""+resources.getIcon("silverStatisticsPeas.icoKillSession")+"\" border=0 onclick=\"\"></A></div>");
            }
        
        	out.println(arrayPane.print());
        }
        out.println(resources.getString("silverStatisticsPeas.RefreshedEveryMinutes") + "<BR>");
%>
</CENTER>
<%       
out.println(frame.printAfter());
out.println(window.printAfter());
%>
<form name="goBack" action="Main" method="post"></form>
</BODY>
</HTML>