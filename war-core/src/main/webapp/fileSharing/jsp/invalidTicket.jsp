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
<%@ page isELIgnored="false"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/viewGenerator.tld" prefix="view"%>
<view:setBundle basename="com.silverpeas.external.filesharing.multilang.fileSharingBundle"/>
<view:setBundle basename="com.silverpeas.external.filesharing.settings.fileSharingIcons" var="icons" />
<html>
<head>
<script type="text/javascript" src="<c:url value="/util/javaScript/animation.js" />"></script>
<style type="text/css">
td { font-family: "Verdana", "Arial", sans-serif; font-size: 10px}
</style>
</head>

<c:set var="attachment" value="${requestScope.attAttachment}" />

<body>
<br/>
<center>
<table>
	<tr><td><fmt:message key="fileSharing.expiredTicket" /></td></tr>
</table>
</center>
</body>
</html>