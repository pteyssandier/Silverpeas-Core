<%@ include file="checkNotification.jsp" %>
<%@ page errorPage="../../admin/jsp/errorpage.jsp"%>

<%
   String action = (String) request.getParameter("Action");
	
   String notificationId = Encode.htmlStringToJavaString((String) request.getAttribute("notificationId"));
   String priorityId = (String) request.getAttribute("priorityId");
   String txtTitle = Encode.htmlStringToJavaString((String) request.getAttribute("txtTitle"));
   String txtMessage = Encode.htmlStringToJavaString((String) request.getAttribute("txtMessage"));
   String popupMode = (String) request.getParameter("popupMode");
   String editTargets = (String) request.getParameter("editTargets");

	 String[] selectedIdUsers = (String[])request.getAttribute("SelectedIdUsers");
	 String[] selectedIdGroups = (String[])request.getAttribute("SelectedIdGroups");


   if (action == null) {
       action = "NotificationView";
   }
   if (notificationId == null) {
       notificationId = "";
   }
   if (priorityId == null) {
       priorityId = "";
   }
   if (txtTitle == null) {
       txtTitle = "";
   }
   if (txtMessage == null) {
       txtMessage = "";
   }
   if (popupMode == null) {
       popupMode = "No";
   }
   if (editTargets == null) { 
       editTargets = "Yes";
   }

   if ((action.equals("sendNotif") || action.equals("emptyAll")) && popupMode.equals("Yes"))
   { 
%>
    <HTML>
      <BODY onLoad="javascript:window.close()"> 
      </BODY>
    </HTML> 
<% } else { %>

<HTML>
<HEAD>
<TITLE>___/ Silverpeas - Corporate Portal Organizer \________________________________________________________________________</TITLE>
<%
   out.println(gef.getLookStyleSheet());
%> 

</HEAD> 
<BODY marginwidth=5 marginheight=5 leftmargin=5 topmargin=5 onLoad="document.notificationSenderForm.txtTitle.focus();">
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>
<script language="JavaScript">

function Submit(){
	SP_openUserPanel('about:blank', 'OpenUserPanel', 'menubar=no,scrollbars=no,statusbar=no');
	document.notificationSenderForm.action = "<%=m_context+URLManager.getURL(URLManager.CMP_NOTIFICATIONUSER)%>"+"SetTarget";
	document.notificationSenderForm.target = "OpenUserPanel";
	document.notificationSenderForm.submit();
}
function ClosePopup(){
	window.close()
}
function SubmitWithAction(action,verifParams)  
{
    var title = stripInitialWhitespace(document.notificationSenderForm.txtTitle.value);
    var errorMsg = "";
		//window.alert("txtMessage="+document.notificationSenderForm.txtMessage.value+ " txtTitle="+document.notificationSenderForm.txtTitle.value);

    if (verifParams) 
    {
         if (isWhitespace(title)) 
            errorMsg = "<% out.print(notificationScc.getString("missingFieldStart")+notificationScc.getString("name")+notificationScc.getString("missingFieldEnd")); %>";
    }
    if (errorMsg == "")
    {
			document.notificationSenderForm.action = "<%=m_context+URLManager.getURL(URLManager.CMP_NOTIFICATIONUSER)%>"+ action;
			document.notificationSenderForm.submit();
    }
    else
    {
        window.alert(errorMsg);
    }
}


</script>
<%
    Window window = gef.getWindow();

    BrowseBar browseBar = window.getBrowseBar(); 
    browseBar.setComponentName(notificationScc.getString("MesNotifications"));
	browseBar.setPath(notificationScc.getString("domainName"));

    if (editTargets.equals("Yes"))
    {
        OperationPane operationPane = window.getOperationPane();
		    operationPane.addOperation(m_context + "/util/icons/notification_assign.gif", notificationScc.getString("Opane_addressees"),"javascript:Submit()");
    }

    out.println(window.printBefore());
    
    //Instanciation du cadre avec le view generator
    Frame frame = gef.getFrame(); 
    out.println(frame.printBefore());
%>

<CENTER>
<table CELLPADDING=0 CELLSPACING=2 BORDER=0 WIDTH="98%" CLASS=intfdcolor>
  <tr>
    <td CLASS=intfdcolor4 NOWRAP>
      <table CELLPADDING=5 CELLSPACING=0 BORDER=0 WIDTH="100%">

<form name="notificationSenderForm" Action=""  Method="POST">

        <tr>
          <td valign="baseline" align=left  class="txtlibform">
            <%=notificationScc.getString("name")%> :&nbsp;
          </td>
          <td align=left valign="baseline">
           <input type="text" name="txtTitle" size="50" maxlength="<%=NotificationParameters.MAX_SIZE_TITLE%>" value="<%=Encode.javaStringToHtmlString(txtTitle)%>">
			 <img border="0" src="<%=mandatoryField%>" width="5" height="5">
          </td>
        </tr>
                
        <tr>
          <td class="txtlibform" valign="top">
            <%=notificationScc.getString("description")%> :
          </td>
          <td align=left valign="top" class="txtnav">
		<textarea type="text" name="txtMessage" value="<%=Encode.javaStringToHtmlString(txtMessage)%>" cols="49" rows="4"><%=Encode.javaStringToHtmlString(txtMessage)%></textarea>
          </td>
        </tr>
	    <tr>			
          <td valign="baseline" align=left  class="txtlibform">
            <%=notificationScc.getString("method")%> :
          </td>
          <td align=left valign="baseline">
            <select name="notificationId">
               <% out.println(notificationScc.buildOptions(notificationScc.getDefaultAddresses(), notificationId, notificationScc.getString("DefaultMedia"))); %>
            </select>
	      </td>
        </tr>
<!--	    <tr>			
          <td valign="baseline" align=left  class="txtlibform">
            <%=notificationScc.getString("notif_type")%> :
          </td>
          <td align=left valign="baseline">
            <select name="priorityId">
               <% out.println(notificationScc.buildOptions(notificationScc.getNotifPriorities(), priorityId, null)); %>
            </select>
          </td>
        </tr>
-->        
         
        <tr>			
          <td valign="top" align=left  class="txtlibform">
            <%=notificationScc.getString("users_dest")%> :&nbsp;
          </td>
          <td align=left valign="top"  class="txtnote">
               <select name="displaySelectedUsers" multiple size="3">
                 <% out.println(notificationScc.buildOptions(notificationScc.getSelectedUsers(selectedIdUsers), "", null)); %>
               </select>
          </td>
        </tr>

        <tr>			
          <td valign="top" align=left  class="txtlibform">
            <%=notificationScc.getString("groups_dest")%> :&nbsp;
          </td>
          <td align=left valign="top"  class="txtnote">
               <select name="displaySelectedGroups" multiple size="3">
                 <% out.println(notificationScc.buildOptions(notificationScc.getSelectedGroups(selectedIdGroups), "", null)); %>
               </select>
          </td>
        </tr>
        
	<tr> 
          <td colspan="2">
	    (<img border="0" src="<%=mandatoryField%>" width="5" height="5"> : <%=notificationScc.getString("requiredFields")%>)
          </td>
        </tr>
				<%
					for (int i=0; i<selectedIdUsers.length; i++ )
					{%>
						<input type="hidden" name="selectedUsers" value="<%=selectedIdUsers[i]%>">
					<%
					}
					for (int i=0; i<selectedIdGroups.length; i++)
					{%>
						<input type="hidden" name="selectedGroups" value="<%=selectedIdGroups[i]%>">
					<%
					}
					%>

					<input type="hidden" name="popupMode" value="<%=popupMode%>">
					<input type="hidden" name="editTargets" value="<%=editTargets%>"> 
				
</form>

      </table>
    </td>
  </tr>
</table>
<BR>
<%
    ButtonPane buttonPane = gef.getButtonPane();
    buttonPane.addButton((Button) gef.getFormButton(notificationScc.getString("Envoyer"), "javascript:SubmitWithAction('sendNotif',true)", false));
		buttonPane.addButton((Button) gef.getFormButton(notificationScc.getString("Cancel"), "javascript:SubmitWithAction('emptyAll',false)", false));
		
    out.println(buttonPane.print());
%>
</CENTER>
<%
out.println(frame.printAfter());
out.println(window.printAfter());
%>
</BODY>
</HTML>
<%} 
%>
