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
<%@ include file="../portletImport.jsp"%>

<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/viewGenerator.tld" prefix="view"%>

<portlet:defineObjects/>

<view:setBundle basename="com.silverpeas.portlets.multilang.portletsBundle"/>

<%
RenderRequest pReq = (RenderRequest)request.getAttribute("javax.portlet.request");
WindowState windowState = pReq.getWindowState();

String height = "350px";
if (windowState.equals(WindowState.MAXIMIZED))
	height = "750px";

PortletPreferences pref = pReq.getPreferences();
String url = pref.getValue("url","");

if (StringUtil.isDefined(url)) { %>
	<iframe src="<%=url%>" frameborder="0" scrolling="auto" width="98%" height="<%=height%>"></iframe>
<% } else { %>
	<fmt:message key="portlets.portlet.iframe.pleaseURL"/>
<% } %>