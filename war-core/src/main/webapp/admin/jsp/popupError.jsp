<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>
<%@ include file="import.jsp" %>
<%
//Récupération des paramètres
String action = (String) request.getParameter("action");
String messagePopup = (String) request.getParameter("messagePopup");
String detailedMessagePopup = (String) request.getParameter("detailedMessagePopup");
String pilePopup = (String) request.getParameter("pilePopup");
%>
<HTML>
<HEAD>
<TITLE><%= generalMessage.getString("GML.popupTitle")%></TITLE>
<%
out.println(gef.getLookStyleSheet());
%>
<script language="JavaScript">

function resizePopup(largeur,hauteur){
	window.resizeTo(largeur,hauteur);
}
</script>

</HEAD>

<%
// action =  affiche du detail
	if (action != null && action.equals("detail")) { %>
<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5 onload="javascript:resizePopup(650,400);">
<%
          Window window = gef.getWindow();
					out.println(window.printBefore());
					Frame frame = gef.getFrame();
					out.println(frame.printBefore());
%>
<CENTER>
<table CELLPADDING=0 CELLSPACING=2 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
	<form name="formPopup" action="popupError.jsp" method="post">
	<input type="Hidden" name="action" value="minimize">
	<input type="Hidden" name="messagePopup" value="<%=messagePopup %>">
	<input type="Hidden" name="detailedMessagePopup" value="<%=detailedMessagePopup %>">
	<input type="Hidden" name="pilePopup" value="<%=pilePopup %>">
	<tr>
		<td CLASS=intfdcolor4>
			<center>
				<br>
				<span class="txtnav">
				<%=detailedMessagePopup %>
				</span>
				<br><br>
				<textarea rows="12" cols="90" wrap="virtual" name="pile"><%=pilePopup %></textarea>
			</center>
			<br>
		</td>
	</tr>
	</form>
</table>
<br>
<%
		  ButtonPane buttonPane = gef.getButtonPane();
		  buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.close"), "javascript:window.close();", false));
			buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.minimize"), "javascript:document.formPopup.submit();", false));
		  out.println(buttonPane.print());
%>
</CENTER>
<%
				out.println(frame.printAfter());
				out.println(window.printAfter());
%>
</BODY>
<%}

// Affichage minimum
else if (action != null && action.equals("minimize")){
 %>
<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5 onload="javascript:resizePopup(650,180);">
<%
          Window window = gef.getWindow();
					out.println(window.printBefore());
					Frame frame = gef.getFrame();
					out.println(frame.printBefore());
%>
<CENTER>
<table CELLPADDING=0 CELLSPACING=2 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
	<form name="formPopup" action="popupError.jsp" method="post"> 
	<input type="Hidden" name="action" value="detail">
	<input type="Hidden" name="messagePopup" value="<%=messagePopup %>">
	<input type="Hidden" name="detailedMessagePopup" value="<%=detailedMessagePopup %>">
	<input type="Hidden" name="pilePopup" value="<%=pilePopup %>">
	<tr>
		<td CLASS=intfdcolor4>
			<center>
				<br>
				<span class="txtnav">
				<%=messagePopup %>
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
		  buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.close"), "javascript:window.close();", false));
			buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.detail"), "javascript:document.formPopup.submit()", false));
		  out.println(buttonPane.print());
%>
</CENTER>
<%
				out.println(frame.printAfter());
				out.println(window.printAfter());
%>
</BODY>
<%}

// Premier affichage
else {
 %>
<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5>
<%
          Window window = gef.getWindow();
					out.println(window.printBefore());
					Frame frame = gef.getFrame();
					out.println(frame.printBefore());
%>
<CENTER>
<table CELLPADDING=0 CELLSPACING=2 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
	<form name="formPopup" action="popupError.jsp" method="post">
	<input type="Hidden" name="action" value="detail">
	<input type="Hidden" name="messagePopup" value="">
	<input type="Hidden" name="detailedMessagePopup" value="<%=detailedMessagePopup %>">
	<input type="Hidden" name="pilePopup" value="">
	<tr>
		<td CLASS=intfdcolor4 NOWRAP>
			<center>
				<br>
				<span class="txtnav">
				<script language="JavaScript">
					window.document.write(window.opener.document.formulaire.message.value);
				</script>
				</span>
			</center>
			<br>
		</td>
	</tr>
	</form>
</table>
<br>
<script language="JavaScript">
	window.document.formPopup.pilePopup.value = window.opener.document.formulaire.pile.value;
	window.document.formPopup.messagePopup.value = window.opener.document.formulaire.message.value;
	window.document.formPopup.detailedMessagePopup.value = window.opener.document.formulaire.detailedMessage.value;
    <% if ((action == null) || (action.equals("NOBack") == false)){  %>
	    window.opener.document.formulaire.submit();
    <%} %>
</script>
<%
		  ButtonPane buttonPane = gef.getButtonPane();
		  buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.close"), "javascript:window.close()", false));
			buttonPane.addButton((Button) gef.getFormButton(generalMessage.getString("GML.detail"), "javascript:document.formPopup.submit();", false));
		  out.println(buttonPane.print());
%>
</CENTER>
<%
				out.println(frame.printAfter());
				out.println(window.printAfter());
%>
</BODY>
<%
}
%>
</HTML>