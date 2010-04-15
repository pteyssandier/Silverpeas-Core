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

<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.RenderResponse" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="com.silverpeas.portlets.FormNames" %>
<%@ page session="false" %>

<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/viewGenerator.tld" prefix="view"%>

<portlet:defineObjects/>
<portlet:actionURL var="actionURL"/>
<view:setBundle basename="com.silverpeas.portlets.multilang.portletsBundle"/>

<%
    RenderRequest pReq = (RenderRequest)request.getAttribute("javax.portlet.request");
    RenderResponse rRes = (RenderResponse)request.getAttribute("javax.portlet.response");
    PortletPreferences pref = pReq.getPreferences();
%>               

    <form name="inputForm" target="_self" method="POST" action="<%=actionURL.toString()%>">
        <table border="0" width="100%" style="align: center">

            <!-- START "url" text box -->
            <tr>
                <td class="txtlibform"><fmt:message key="portlets.portlet.iFrame.pref.url" /> :</td>
                <td><input name="url" value="<%=pref.getValue("url","")%>" type="text" size="40"/></td>
            </tr>
                     
            <!-- START "finished" and "cancel" buttons -->
            <tr>
                <td colspan="2" style="text-align: center; vertical-align: top">
                    <input class="portlet-form-button" name="<%=FormNames.SUBMIT_FINISHED%>" type="submit" value="<fmt:message key="portlets.validate"/>"/>
                    <input class="portlet-form-button" name="<%=FormNames.SUBMIT_CANCEL%>" type="submit" value="<fmt:message key="portlets.cancel"/>"/>
                </td>
            </tr>
        </table>
        <!-- END "finished" and "cancel" buttons -->
    </form>
