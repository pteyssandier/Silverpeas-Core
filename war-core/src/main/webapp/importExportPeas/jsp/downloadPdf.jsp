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
<%@ include file="check.jsp" %>
<%
	ExportPDFReport report = (ExportPDFReport) request.getAttribute("ExportPDFReport");
%>

<html>
	<head>
		<%
			out.println(gef.getLookStyleSheet());
		%>
	</head>
	<body bgcolor="#ffffff" leftmargin="5" topmargin="5" marginwidth="5" marginheight="5">
		<%
			browseBar.setComponentName(resource.getString("importExportPeas.Export"));
			
			out.println(window.printBefore());
			out.println(frame.printBefore());
			out.println(board.printBefore());
		%>
		<table>
			<tr>
				<td class="txtlibform"><%=resource.getString("importExportPeas.ExportDuration")%> :&nbsp;</td>
				<td><%=DateUtil.formatDuration(report.getDuration())%></td>
			</tr>
			<tr>
				<td class="txtlibform"><%=resource.getString("importExportPeas.FileSize")%> :&nbsp;</td>
				<td><%=FileRepositoryManager.getFileSize(report.getPdfFileSize())%></td>
			</tr>
			<tr>
				<td class="txtlibform"><%=resource.getString("importExportPeas.File")%> :&nbsp;</td>
				<td>
					<a href="<%=report.getPdfFilePath()%>"><%=report.getPdfFileName()%></a>
					<a href="<%=report.getPdfFilePath()%>"><img src="<%=FileRepositoryManager.getFileIcon("pdf")%>" border="0" align="absmiddle"></a>
				</td>
			</tr>
		</table>
		<%
			out.println(board.printAfter());
			out.println(frame.printMiddle());
			ButtonPane buttonPane = gef.getButtonPane();
			Button button = (Button) gef.getFormButton(resource.getString("GML.close"), "javaScript:window.close();", false);
			buttonPane.addButton(button);
			out.println("<BR><center>"+buttonPane.print()+"</center><BR>");
			out.println(frame.printAfter());
			out.println(window.printAfter());
		%>
	</body>
</html>