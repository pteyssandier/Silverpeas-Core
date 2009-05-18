<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>

<%@ page import="java.util.*"%>
<%@ page import="javax.ejb.*,java.sql.SQLException,javax.naming.*,javax.rmi.PortableRemoteObject"%>
<%@ page import="com.stratelia.webactiv.util.*"%>
<%@ page import="com.stratelia.webactiv.beans.admin.*"%>
<%@ page import="com.stratelia.silverpeas.clipboardPeas.control.*"%>
<%@ page import="com.stratelia.webactiv.clipboard.model.*"%>
<%@ page import="com.stratelia.webactiv.util.indexEngine.model.*"%>

<%@ include file="checkClipboard.jsp.inc" %>

<html>
<HEAD>
<Script language="JavaScript">

var counter = 0;
var interval = 5; //secondes
// call Update function in 1 second after first load
ID = window.setTimeout ("DoIdle();", interval * 1000);

//--------------------------------------------------------------------------------------DoIdle
// Idle function
function DoIdle()
{
	counter ++;
	window.status="Elapsed time = " + counter  + " seconds";
	// set another timeout for the next count
	// ID=window.setTimeout("DoIdle();",1000);

	self.location.href = "../../Rclipboard/jsp/Idle.jsp?message=IDLE";
}

//--------------------------------------------------------------------------------------DoTask
// Do taks javascript function
function DoTask() {
	alert ("<%if (clipboardSC != null) out.println (clipboardSC.getMessageError());%>");
}

//--------------------------------------------------------------------------------------test
// Developer test
function test () {
  //window.alert ('clipboardName='+top.ClipboardWindow.name);
  status = top.ClipboardWindow.document.pasteform.compR.value;
}


</script>
</HEAD>

<body onLoad="DoTask();"><PRE>
Frame cach�e, Time = <%if (clipboardSC != null) out.print (String.valueOf(clipboardSC.getCounter()));%> <a href="../../Rclipboard/jsp/Idle.jsp?message=IDLE">idle...</a>
<%
		Enumeration values = request.getParameterNames();
		String sep = "";
		while(values.hasMoreElements()) {
			String name = (String)values.nextElement();
			if (name != null) {
		      String value = request.getParameter(name);
            if(name.compareTo("submit") != 0) {
				   if (value != null)
					   out.print(sep + name + "=" + value);
				   else
					   out.print(sep + name + "=null");
				   sep = "&";
            }
			}
      }
	%>
	<a href="javascript:onClick=test()">test...</a>
	</PRE>
<%if (clipboardSC != null) out.println (clipboardSC.getHF_HTMLForm(request));%>

</body>
</html>