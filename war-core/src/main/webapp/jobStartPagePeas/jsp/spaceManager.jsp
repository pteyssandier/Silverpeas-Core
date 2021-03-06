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
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="check.jsp" %>
<%
	SpaceProfileInst 	m_Profile 			= (SpaceProfileInst) request.getAttribute("Profile");
	List 				m_listGroup 		= (List) request.getAttribute("listGroupSpace");
	List 				m_listUser 			= (List) request.getAttribute("listUserSpace");
	Boolean 			m_ProfileEditable 	= (Boolean) request.getAttribute("ProfileEditable");
	String				role				= (String) request.getAttribute("Role");
	DisplaySorted 		m_SpaceExtraInfos 	= (DisplaySorted)request.getAttribute("SpaceExtraInfos");
	boolean 			isInHeritanceEnable = ((Boolean)request.getAttribute("IsInheritanceEnable")).booleanValue();
	
	SpaceProfileInst	inheritedProfile 	= (SpaceProfileInst) request.getAttribute("InheritedProfile");
	List 				inheritedGroups		= (List) request.getAttribute("listInheritedGroups"); //List of GroupDetail
	List 				inheritedUsers 		= (List) request.getAttribute("listInheritedUsers"); //List of UserDetail
	String 				spaceId				= (String) request.getAttribute("CurrentSpaceId");
	
	String nameProfile = null;
	if (m_Profile == null)
		nameProfile = resource.getString("JSPP."+role);
	else {
		nameProfile = m_Profile.getLabel();
		if (!StringUtil.isDefined(nameProfile))
			nameProfile = resource.getString("JSPP."+role);
	}
	
	browseBar.setSpaceId(spaceId);
	browseBar.setExtraInformation(nameProfile);
		
	//Onglets
    TabbedPane tabbedPane = gef.getTabbedPane();
	tabbedPane.addTab(resource.getString("GML.description"),"StartPageInfo", false);
	
	tabbedPane.addTab(resource.getString("JSPP.SpaceAppearance"), "SpaceLook", false);
	
    tabbedPane.addTab(resource.getString("JSPP.Manager"), "SpaceManager", role.equals("Manager"));
    
    if (isInHeritanceEnable)
    {
	    tabbedPane.addTab(resource.getString("JSPP.admin"), "SpaceManager?Role=admin", role.equals("admin"));
	    tabbedPane.addTab(resource.getString("JSPP.publisher"), "SpaceManager?Role=publisher", role.equals("publisher"));
	    tabbedPane.addTab(resource.getString("JSPP.writer"), "SpaceManager?Role=writer", role.equals("writer"));
	    tabbedPane.addTab(resource.getString("JSPP.reader"), "SpaceManager?Role=reader", role.equals("reader"));
    }
%>
<HTML>
<HEAD>
<TITLE><%=resource.getString("GML.popupTitle")%></TITLE>
<%
out.println(gef.getLookStyleSheet());
%>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script language="javascript">
function goToOperationInAnotherWindow(larg, haut) {
	windowName = "userPanelWindow";
	windowParams = "directories=0,menubar=0,toolbar=0,alwaysRaised,scrollbars,resizable";
	userPanelWindow = SP_openWindow("SelectUsersGroupsSpace?Role=<%=role%>", windowName, larg, haut, windowParams, false);
}    

function deleteRoleContent() {	
    if (window.confirm("<%=resource.getString("JSPP.MessageSuppressionSpaceManager")%>")) { 
    	location.href = "DeleteSpaceManager?Role=<%=role%>";
	}
}	
</script>
</HEAD>
<BODY id="admin-role">
<%
	if (m_SpaceExtraInfos.isAdmin)
	{
		// Space edition
		if (m_Profile == null) //creation
			operationPane.addOperation(resource.getIcon("JSPP.userManage"),resource.getString("JSPP.SpaceProfilePanelCreateTitle"),"javaScript:onClick=goToOperationInAnotherWindow(850, 800)");
		else {//update
			operationPane.addOperation(resource.getIcon("JSPP.userManage"),resource.getString("JSPP.SpaceProfilePanelModifyTitle"),"javaScript:onClick=goToOperationInAnotherWindow(850, 800)");
			
			if (m_ProfileEditable.equals(Boolean.TRUE))
				operationPane.addOperation(resource.getIcon("JSPP.spaceManagerDescription"),resource.getString("JSPP.ProfilePanelModifyTitle"),"javaScript:onClick=goToOperationInAnotherWindow('SpaceManagerDescription', 750, 250)");
				
			if (m_listGroup.size() > 0 || m_listUser.size() > 0) 
				operationPane.addOperation(resource.getIcon("JSPP.usersGroupsDelete"),resource.getString("JSPP.SpaceProfilePanelDeleteTitle"),"javaScript:onClick=deleteRoleContent()");
		}
	}
	
out.println(window.printBefore());
out.println(tabbedPane.print());
out.println(frame.printBefore());
%>
<center>
<%
out.println(board.printBefore());
%>
<br><br>
	<% if (inheritedProfile != null && (inheritedGroups.size() > 0 || inheritedUsers.size()>0)) { %>
	<TABLE width="70%" align="center" border="0" cellPadding="0" cellSpacing="0">
		<TR>
			<TD colspan="2" class="txttitrecol"><%=resource.getString("JSPP.inheritedRights")%></TD>
		</TR>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor" height="1"><img src="<%=resource.getIcon("JSPP.px")%>"></TD>
		</TR>
		<TR>
			<TD align="center" class="txttitrecol"><%=resource.getString("GML.type")%></TD>
			<TD align="center" class="txttitrecol"><%=resource.getString("GML.name")%></TD>
		</TR>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor" height="1"><img src="<%=resource.getIcon("JSPP.px")%>"></TD>
		</TR>
		
		<%
		// La boucle sur les groupes 
		Iterator groups = inheritedGroups.iterator();
		Group group = null;
		while (groups.hasNext())
		{
			group = (Group) groups.next();
			out.println("<TR>");
			if (group.isSynchronized())
				out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.scheduledGroup")+"\" class=\"group-icon\"/></TD>");
			else
				out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.group")+"\" class=\"group-icon\"/></TD>");
			out.println("<TD align=\"center\">"+group.getName()+"</TD>");
			out.println("</TR>");
		}
		
		// La boucle sur les users
		Iterator users = inheritedUsers.iterator();
		UserDetail user = null;
		while (users.hasNext())
		{
			user = (UserDetail) users.next();
			out.println("<TR>");
			out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.user")+"\" class=\"user-icon\"/></TD>");
			out.println("<TD align=\"center\">"+user.getDisplayedName()+"</TD>");
			out.println("</TR>");
		}
		%>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor"  height="1"><img src="<%=resource.getIcon("JSPP.px")%>"></TD>
		</TR>
	</TABLE>
	<br/><br/>
	<% } %>

	<TABLE width="70%" align="center" border="0" cellPadding="0" cellSpacing="0">
		<% if (isInHeritanceEnable) { %>
		<TR>
			<TD colspan="2" class="txttitrecol"><%=resource.getString("JSPP.localRights")%></TD>
		</TR>
		<% } %>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor" height="1"><img src="<%=resource.getIcon("JSPP.px")%>"></TD>
		</TR>
		<TR>
			<TD align="center" class="txttitrecol"><%=resource.getString("GML.type")%></TD>
			<TD align="center" class="txttitrecol"><%=resource.getString("GML.name")%></TD>
		</TR>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor" height="1"><img src="<%=resource.getIcon("JSPP.px")%>"></TD>
		</TR>		
		<%
		// La boucle sur les groupes 
		Iterator groups = m_listGroup.iterator();
		Group group = null;
		while (groups.hasNext())
		{
			group = (Group) groups.next();
			out.println("<TR>");
			if (group.isSynchronized())
				out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.scheduledGroup")+"\" class=\"group-icon\"/></TD>");
			else
				out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.group")+"\" class=\"group-icon\"/></TD>");
			out.println("<TD align=\"center\">"+group.getName()+"</TD>");
			out.println("</TR>");
		}
		
		//La boucle sur les users
		Iterator users = m_listUser.iterator();
		UserDetail user = null;
		while (users.hasNext())
		{
			user = (UserDetail) users.next();
			out.println("<TR>");
			out.println("<TD align=\"center\"><img src=\""+resource.getIcon("JSPP.user")+"\" class=\"user-icon\"/></TD>");
			out.println("<TD align=\"center\">"+user.getDisplayedName()+"</TD>");
			out.println("</TR>");
		}
		%>
		<TR>
			<TD colspan="2" align="center" class="intfdcolor" height="1"><img src="<%=resource.getIcon("JSPP.px")%>"/></TD>
		</TR>
	</TABLE>
<br><br>
<%
out.println(board.printAfter());
%>
</center>
<%
out.println(frame.printAfter());
out.println(window.printAfter());
%>
</BODY>
</HTML>