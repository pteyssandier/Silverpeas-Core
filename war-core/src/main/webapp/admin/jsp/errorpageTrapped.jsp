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
    if (response.isCommitted() == false)
        response.resetBuffer();
%>
<%--
 % This page is invoked when an error happens at the server.  The
 % error details are available in the implicit 'exception' object.
 % We set the error page to this file in each of our screens.
 % (via the template.jsp)
--%>

<%@ page import="javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="javax.servlet.jsp.*"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.ObjectInputStream"%>
<%@ page import="java.util.Vector"%>
<%@ page import="java.beans.*"%>

<%@ page import="javax.ejb.EJBException, javax.ejb.FinderException, javax.ejb.NoSuchEntityException, java.rmi.RemoteException, java.sql.SQLException, javax.ejb.RemoveException, javax.ejb.CreateException, javax.naming.NamingException, javax.transaction.TransactionRolledbackException"%>
<%@ page import="java.util.Collection, java.util.Iterator, java.lang.Throwable"%>
<%@ page import="com.stratelia.webactiv.util.*"%>
<%@ page import="com.stratelia.silverpeas.silvertrace.*"%>
<%@ page import="com.stratelia.webactiv.util.exception.*"%>
<%@ page import="com.stratelia.webactiv.homepage.*"%>

<%@ include file="import.jsp" %>

<% 
	Exception exception = (Exception) request.getAttribute("javax.servlet.jsp.jspException");
	Throwable toDisplayException = HomePageUtil.getExceptionToDisplay(exception);
	String exStr = HomePageUtil.getMessageToDisplay(exception , language);
	String detailedString = HomePageUtil.getMessagesToDisplay(exception , language);
	String gobackPage = null;
	String extraInfos = "";

    if (exception instanceof SilverpeasTrappedException)
    {
        gobackPage = ((SilverpeasTrappedException)exception).getGoBackPage();
        extraInfos = ((SilverpeasTrappedException)exception).getExtraInfos();
        if ((extraInfos == null) || ("null".equalsIgnoreCase(extraInfos)))
            extraInfos = "";
    }
    if ((gobackPage == null) || (gobackPage.length() <= 0))
        gobackPage = m_context + "/admin/jsp/errorpageMainMax.jsp";

    HomePageUtil.traceException(exception);
%>
<HTML>
<HEAD>
<TITLE><%= generalMessage.getString("GML.popupTitle")%></TITLE>
<%
    out.println(gef.getLookStyleSheet());
%>
</HEAD>

<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5>
<%
        Window window = gef.getWindow();
		out.println(window.printBefore());
		Frame frame = gef.getFrame();
		out.println(frame.printBefore());
%>
<CENTER>
<table CELLPADDING=0 CELLSPACING=2 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
	<form name="formError" action="<%=gobackPage%>" method="post">
	<input type="Hidden" name="message" value="<% if (exStr != null){out.print(exStr);}%>">
    <input type="Hidden" name="detailedMessage" value="<% if (detailedString != null){out.print(detailedString);}%>">
	<input type="Hidden" name="pile" value="<% if (toDisplayException != null) {toDisplayException.printStackTrace(new PrintWriter(out));}%>">
	<tr>
		<td CLASS=intfdcolor4 NOWRAP>
			<center>
				<br>
				<span class="txtnav">
					<% if (exStr != null){out.print(exStr);}%>
                <br><br>
                <%=extraInfos%>
                </span>
			</center>
			<br>
		</td>
	</tr>
	</form>
</table>
<br>
<%
	  ButtonPane buttonPane = gef.getButtonPane();
      buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.ok"), "javascript:document.formError.submit();", false));
      out.println(buttonPane.print());
%>
</CENTER>
<%
	  out.println(frame.printAfter());
	  out.println(window.printAfter());
%>
</BODY>
</HTML>
