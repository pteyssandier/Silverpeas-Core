<%@ page errorPage="../../admin/jsp/errorpage.jsp"%>
<%@ include file="checkAttachment.jsp"%>

<%
  //initialisation des variables
  Date		creationDate = new Date();
  String	id			= request.getParameter("Id");
  String	componentId	= request.getParameter("ComponentId");
  String	path		= request.getParameter("Path");
  String	context		= request.getParameter("Context");
  String	url			= request.getParameter("Url");
  String	title		= request.getParameter("Title");
  String	description = request.getParameter("Description");
  String	logicalName = null;
  String	mimeType	= null;
  boolean	isExistFile	= true; // par defaut, le fichier que l'utilisateur veut lier existe.
   
	//create AttachmentPK with componentId
	AttachmentPK atPK = new AttachmentPK(null, componentId);

	//create foreignKey with componentId and id
	//use AttachmentPK to build the foreign key of customer object.
	AttachmentPK foreignKey =  new AttachmentPK(id, componentId);

	//Création d'un objet fichier
	File f = new File(path);
	if(f.isFile()){
		//récupération du nom, la dernière occurrence contiendra le nom du fichier
		StringTokenizer strt = new java.util.StringTokenizer(path, File.separator);
		int nbToken = strt.countTokens();
		int k = 0;
		String physicalName = new String();
		while(strt.hasMoreElements()){
			k++;
			logicalName = strt.nextToken();
		}
		physicalName= path;
	
		//récupération du mimeType
		mimeType = AttachmentController.getMimeType(logicalName);
		//create AttachmentDetail Object
		AttachmentDetail ad = new AttachmentDetail(atPK, physicalName, logicalName, "link", mimeType, f.length(), context, creationDate, foreignKey);
		ad.setAuthor(m_MainSessionCtrl.getUserId());
		ad.setTitle(title);
		ad.setInfo(description);
		AttachmentController.createAttachment(ad);
	}
	else{
		SilverTrace.info("attachment", "SaveLink.jsp", "root.MSG_GEN_PARAM_VALUE","f="+f.getPath());
		isExistFile = false;
	}
%>
<HTML>
	<HEAD>
		<TITLE>_________________/ Silverpeas - Corporate portal organizer \_________________/</TITLE>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<% out.println(gef.getLookStyleSheet()); %>

		<style type="text/css">
		<!--
		.eventCells {  padding-right: 3px; padding-left: 3px; vertical-align: top; background-color: #FFFFFF}
		-->
		</style>
	</HEAD>

	<BODY>
		<%
		Frame frame=gef.getFrame();
		out.println(frame.printBefore());
		%>
		<center>
			<table width="98%" border="0" cellspacing="0" cellpadding="0" class=intfdcolor4><!--tablcontour-->
				<tr>
					<td valign="top" align="center"> <!-- SEPARATION NAVIGATION / CONTENU DU COMPOSANT -->
						<table border="0" cellspacing="0" cellpadding="5" width="100%" align="center" class="contourintfdcolor">
							<tr>
								<td align="center">
									<B><%if (isExistFile) 
												out.println(messages.getString("liaisonTerminée"));
											else
												out.println(messages.getString("liaisonInaccessible"));%>
									</B>
								</td>
							</tr>	
						</table> 
					</td>
				</tr>	
			</table>
			<br>
			<%
			ButtonPane buttonPane2 = gef.getButtonPane();
        String paramDelimiter = "?";
        if (url.indexOf("?") != -1) {
                //Il existe déjà un '?' dans la chaine url
                paramDelimiter = "&";
        }
			buttonPane2.addButton((Button) gef.getFormButton(resources.getString("GML.back"), "javascript:window.opener.location.href='"+m_Context+ url + paramDelimiter + "Id="+id + "&Component=" + componentId + "'; window.close()", false));
			out.println(buttonPane2.print());

			out.println(frame.printMiddle());
			out.println(frame.printAfter());
			%>
	</BODY>
	<script language='javascript'>
		window.focus();	
	</script>
</HTML>
