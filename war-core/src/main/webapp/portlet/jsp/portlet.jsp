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

<%@ page import="com.stratelia.silverpeas.portlet.*"%>
<%@ page import="com.stratelia.silverpeas.peasCore.URLManager"%>

<%@ page errorPage="../../admin/jsp/errorpagePopup.jsp"%>

<HTML>
<HEAD>
<jsp:useBean id="portlet" scope="request" class="com.stratelia.silverpeas.portlet.Portlet"/>
</HEAD>
<frameset rows="24,1*" cols="2,1*,2" frameborder="NO" border="0" framespacing="0">
<!-- Ligne 1 = HEADER -->
  <frame scrolling="NO" src="bordure.htm" noresize>
  <frame src="portletTitle?id=<%=portlet.getIndex()%>&spaceId=<%=request.getParameter("spaceId")%>" scrolling="NO"  marginheight="0" marginwidth="0" frameborder="NO" noresize>
  <frame scrolling="NO" src="bordure.htm" noresize>

  <!-- Ligne 2 = CONTENT -->
  <frame scrolling="NO" src="bordure.htm" noresize>
  <frame src="<%= URLManager.getApplicationURL()+portlet.getRequestRooter() + portlet.getContentUrl() +
                    "?space=WA" + request.getParameter("spaceId") + "&Component=" + portlet.getComponentName() +
                    portlet.getComponentInstanceId()%>"
         scrolling="AUTO" marginheight="0" marginwidth="0" frameborder="NO" noresize>
  <frame scrolling="NO" src="bordure.htm" noresize>
</frameset>
<noframes></noframes>
</HTML>
