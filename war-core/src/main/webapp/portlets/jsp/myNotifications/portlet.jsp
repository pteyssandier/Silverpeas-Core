<%@ page import="com.stratelia.silverpeas.notificationserver.channel.silvermail.SILVERMAILMessage"%>
<%@ page import="com.stratelia.webactiv.beans.admin.UserDetail" %>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.arrayPanes.*"%>

<%@ include file="../portletImport.jsp"%>

<%@ taglib uri="/WEB-INF/portlet.tld" prefix="portlet" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<portlet:defineObjects/>
<portlet:actionURL var="actionURL"/>

<script type="text/javascript" src="<%=m_sContext%>/util/javaScript/animation.js"></script>
<script type="text/javascript">
function readMessage(id)
{
	SP_openWindow("<%=m_sContext%>/RSILVERMAIL/jsp/ReadMessage.jsp?ID="+id+"&from=homePage","readMessage","600","380","scrollable=yes,scrollbars=yes");
}
</script>

<%
RenderRequest 	pReq 			= (RenderRequest)request.getAttribute("javax.portlet.request");
Iterator 		messageIterator = (Iterator) pReq.getAttribute("Messages");

//Arraypane
ArrayPane list = gef.getArrayPane("silvermail", actionURL, request, session);
ArrayColumn col = list.addArrayColumn(message.getString("notification.date"));
col = list.addArrayColumn(message.getString("notification.source"));
col = list.addArrayColumn(message.getString("notification.from"));
col = list.addArrayColumn(message.getString("notification.url"));
col = list.addArrayColumn(message.getString("notification.subject"));

String	hasBeenReadenOrNotBegin	= "";
String	hasBeenReadenOrNotEnd	= "";

while(messageIterator.hasNext())
{
	hasBeenReadenOrNotBegin = "";
	hasBeenReadenOrNotEnd 	= "";
	SILVERMAILMessage smMessage = (SILVERMAILMessage)messageIterator.next();
	if (smMessage.getReaden() == 0) {
		hasBeenReadenOrNotBegin = "<b>";
		hasBeenReadenOrNotEnd = "</b>";
	}

	String link = "<A HREF =\"javascript:onClick=readMessage(" + smMessage.getId() + ");\">";
	ArrayLine line = list.addArrayLine();
	Date date = smMessage.getDate();
	ArrayCellText cell1 = line.addArrayCellText(hasBeenReadenOrNotBegin + DateUtil.getOutputDate(date, language) + hasBeenReadenOrNotEnd );
	cell1.setCompareOn(date);
	
	ArrayCellText cell2 = line.addArrayCellText(hasBeenReadenOrNotBegin + Encode.javaStringToHtmlString(smMessage.getSource()) + "</A>" + hasBeenReadenOrNotEnd );
	cell2.setCompareOn(smMessage.getSource());
	
	ArrayCellText cell3 = line.addArrayCellText(hasBeenReadenOrNotBegin + link + Encode.javaStringToHtmlString(smMessage.getSenderName()) + "</A>" + hasBeenReadenOrNotEnd );
	cell3.setCompareOn(smMessage.getSenderName());

	if ( smMessage.getUrl()!=null && smMessage.getUrl().length()>0 )
		line.addArrayCellText(hasBeenReadenOrNotBegin + "<A HREF =\"" + Encode.javaStringToHtmlString(smMessage.getUrl()) + "\" target=_top><img src=\""+m_sContext+"/util/icons/Lien.gif\" border=\"0\"></A>" + hasBeenReadenOrNotEnd );
	else
		line.addArrayCellText( "" );
	ArrayCellText cell5 = line.addArrayCellText(hasBeenReadenOrNotBegin + link + Encode.javaStringToHtmlString(smMessage.getSubject()) + "</A>" + hasBeenReadenOrNotEnd );
	cell5.setCompareOn(smMessage.getSubject());
}
out.println(list.print());
out.flush();
%>