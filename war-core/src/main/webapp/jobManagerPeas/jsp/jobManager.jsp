<%@ include file="check.jsp" %>
<html>
<head>
<title><%=resource.getString("GML.popupTitle")%></title>
<script language="javascript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
//-->
</script>
</head>
<frameset rows="0,95,*" cols="*" border="0" framespacing="0" frameborder="NO"> 
  <frame src="<%=m_context%>/admin/jsp/javascript.htm" name="scriptFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
  <frame src="TopBarManager" name="topFrame" marginwidth="0" marginheight="0" scrolling="NO" noresize frameborder="NO">
  <frame src="" name="bottomFrame" marginwidth="0" marginheight="0" scrolling="auto" noresize frameborder="NO">
</frameset><noframes></noframes>
</html>