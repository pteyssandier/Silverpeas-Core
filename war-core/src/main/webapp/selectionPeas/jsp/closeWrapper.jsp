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

<%@ include file="check.jsp" %>

<%
   String formName = (String) request.getAttribute("formName");
   String elementId = (String) request.getAttribute("elementId");
   String elementName = (String) request.getAttribute("elementName");
   //String userId = (String) request.getAttribute("userId");
   //String userName = (String) request.getAttribute("userName");
   
   UserDetail 	user 	= (UserDetail) request.getAttribute("user");
   UserDetail[] users 	= (UserDetail[]) request.getAttribute("users");
   
   String userId = "";
   String userName = "";
   if (user != null)
   {
	   userId = user.getId();
	   userName = user.getDisplayedName();
   }
   else if (users != null)
   {
	   for (int u=0; u<users.length; u++)
	   {
		   user = users[u];
		   userId += user.getId()+",";
		   userName += user.getDisplayedName()+"\\n";
	   }
   }
   
%>

<HTML>
<HEAD>
<TITLE></TITLE>
<SCRIPT language='Javascript'>
function resetOpener()
{
	window.opener.document.forms['<%=formName%>'].elements['<%=elementId%>'].value="<%=userId%>";
	window.opener.document.forms['<%=formName%>'].elements['<%=elementName%>'].value="<%=userName%>";
	window.close();
}
</SCRIPT>
</HEAD>
<BODY onload="javascript:resetOpener();">
</BODY>
</HTML>
