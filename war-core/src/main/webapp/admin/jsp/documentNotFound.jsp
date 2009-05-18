<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>
<%@ page import="com.stratelia.silverpeas.peasCore.URLManager"%>
<%@ include file="import.jsp" %>

<%
String componentId = (String) request.getAttribute("ComponentId");

String url = null;
if (componentId != null)
	url = m_context+URLManager.getURL(null, componentId)+"Main";
	
String buttonAction = "javaScript:history.back();";
if (url != null)
	buttonAction = "javascript:onClick=location.href='"+url+"';";
%>

<HTML>
<HEAD>
<%
out.println(gef.getLookStyleSheet());
%>
</HEAD>
<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5>

<%
	Window window = gef.getWindow();
	Frame frame = gef.getFrame();
	Board board = gef.getBoard();
	
	out.println(window.printBefore());
	out.println(frame.printBefore());
	out.println(board.printBefore());
%>
	<center>
		<br>
		<h3><%=generalMessage.getString("GML.DocumentNotFound")%></h3>
	</center>
<%
	out.println(board.printAfter());
	
	Button back = (Button) gef.getFormButton(generalMessage.getString("GML.back"), buttonAction, false);
	out.println("<br><center>"+back.print()+"</center><br>");
	
	out.println(frame.printAfter());
	out.println(window.printAfter());
%>
</BODY> 
</html>