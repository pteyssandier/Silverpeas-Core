<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have recieved a copy of the text describing
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
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>
<%@ include file="check.jsp" %>
<HTML>
<HEAD>
<TITLE><%=resource.getString("GML.popupTitle")%></TITLE>
<%
out.println(gef.getLookStyleSheet());
%>
</HEAD>

<%
	String componentURL = (String)request.getAttribute("myComponentURL");
	PairObject hostComponentNameObject = (PairObject) request.getAttribute("HostComponentName");
	String hostSpaceName = (String) request.getAttribute("HostSpaceName");
	String hostComponentName = (String) hostComponentNameObject.getFirst(); 
%>

<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5>

<%
	browseBar.setDomainName(hostSpaceName);
	browseBar.setComponentName(hostComponentName);

	out.println(window.printBefore());
	out.println(frame.printBefore());
%>
<TABLE ALIGN=CENTER CELLPADDING=2 CELLSPACING=0 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
	<tr><td>
		<TABLE ALIGN=CENTER CELLPADDING=5 CELLSPACING=0 BORDER=0 WIDTH="100%" CLASS=intfdcolor4>
			<tr><td>
				<center><%=resource.getString("AlertInProgress")%></center>
			</td></tr>
		</table>
	</td></tr>
</table>
<%
	out.println(frame.printAfter());
	out.println(window.printAfter());
%>
</BODY>
</HTML>

<script>location.replace("<%=componentURL%>Notify");</script>