<%@ page import="java.net.URLDecoder"%>
<%@ page import="com.stratelia.webactiv.util.FileRepositoryManager"%>
<%@ include file="checkAdvancedSearch.jsp"%>

<%!
String displayPertinence(float score, String fullStarSrc, String emptyStarSrc)
{
	StringBuffer stars = new StringBuffer();
	if (score <= 0.2) {
		for (int l = 0; l < 1; l++) {
			stars.append("").append(fullStarSrc);
		}
		for (int k = 2; k <= 5; k++) {
			stars.append("").append(emptyStarSrc);
		}
	} else if (score > 0.2 && score <= 0.4) {
		for (int l = 0; l < 2; l++) {
			stars.append("").append(fullStarSrc);
		}
		for (int k = 3; k <= 5; k++) {
			stars.append("").append(emptyStarSrc);
		}
	} else if (score > 0.4 && score <= 0.6) {
		for (int l = 0; l < 3; l++) {
			stars.append("").append(fullStarSrc);
		}
		for (int k = 4; k <= 5; k++) {
			stars.append("").append(emptyStarSrc);
		}
	} else if (score > 0.6 && score <= 0.8) {
		for (int l = 0; l < 4; l++) {
			stars.append("").append(fullStarSrc);
		}
		stars.append("").append(emptyStarSrc);
	} else if (score > 0.8 && score <= 1) {
		for (int l = 0; l < 5; l++) {
			stars.append("").append(fullStarSrc);
		}
	}
	return stars.toString();
}

void displayItemsListHeader(String query, Pagination pagination, ResourcesWrapper resource, JspWriter out) throws IOException {
	out.println("<tr valign=\"middle\" class=\"intfdcolor\">");
	out.println("<td align=\"center\" class=\"ArrayNavigation\">");
	out.println("<img align=\"absmiddle\" src=\""+resource.getIcon("pdcPeas.1px")+"\" height=\"20\">");
	out.println(pagination.printCounter());
	out.println(resource.getString("pdcPeas.ManyResultPages"));
	if (query != null && query.length() > 0)
		out.println(" "+resource.getString("pdcPeas.ForYourQuery")+query);
	out.println("</td>");
	out.println("</tr>");
	out.println("<tr><td>&nbsp;</td></tr>");
}
%>

<%
List results 			= (List) request.getAttribute("Results");
int nbTotalResults		= ((Integer) request.getAttribute("NbTotalResults")).intValue();

int indexOfFirstResult	= ((Integer) request.getAttribute("IndexOfFirstResult")).intValue();
Boolean exportEnabled	= (Boolean) request.getAttribute("ExportEnabled");
Boolean refreshEnabled	= (Boolean) request.getAttribute("RefreshEnabled");
Boolean	xmlSearch		= (Boolean) request.getAttribute("XmlSearchVisible");
boolean	showPertinence	= ((Boolean) request.getAttribute("PertinenceVisible")).booleanValue();

//CBO : ADD
String 	displayParamChoices = (String) request.getAttribute("DisplayParamChoices"); // All || Req || Res
List choiceNbResToDisplay = (List) request.getAttribute("ChoiceNbResToDisplay");
Integer nbResToDisplay		= (Integer) request.getAttribute("NbResToDisplay");
Integer sortValue		= (Integer) request.getAttribute("SortValue");
String sortOrder		= (String) request.getAttribute("SortOrder");

boolean isXmlSearchVisible = false;
if (xmlSearch != null)
	isXmlSearchVisible = xmlSearch.booleanValue();

// recuperation du choix de l'utilisateur
String keywords = (String) request.getAttribute("Keywords");
if (keywords == null)
	keywords = "";
else
	keywords = Encode.javaStringToHtmlString(keywords);
	
Boolean activeSelection = (Boolean) request.getAttribute("ActiveSelection");
if (activeSelection == null) {
	activeSelection = new Boolean(false);
}

// Contenu
String sName		= null;
String sDescription = null;
String sURL			= null;
String sDownloadURL = null;
String sLocation	= "";
String componentId	= "";
String sCreatorName = "";
String sCreationDate = "";
String fileType		= "";
String fileIcon		= "";

//pour le thesaurus
Map synonyms = (Map) request.getAttribute("synonyms");
String urlToRedirect = (String) request.getAttribute("urlToRedirect");
String backButtonClick;
if (urlToRedirect != null) {
    backButtonClick = "location.href='" + URLDecoder.decode(urlToRedirect) + "';";
}

String fullStarSrc		= "<img src=\""+m_context+"/pdcPeas/jsp/icons/starGreen.gif\">";
String emptyStarSrc		= "<img src=\""+m_context+"/pdcPeas/jsp/icons/pdcPeas_emptyStar.gif\">";
String downloadSrc		= "<img border=0 align=absmiddle src=\""+resource.getIcon("pdcPeas.download")+"\" alt=\""+resource.getString("pdcPeas.DownloadInfo")+"\">";
String attachmentSrc	= "<img border=0 align=absmiddle src=\""+resource.getIcon("pdcPeas.attachment")+"\">&nbsp;";

Board board = gef.getBoard();
Button searchButton = (Button) gef.getFormButton(resource.getString("pdcPeas.search"), "javascript:onClick=sendQuery()", false);
%>


<%@page import="com.silverpeas.util.StringUtil"%><html>
<HEAD>
<TITLE><%=resource.getString("GML.popupTitle")%></TITLE>
<%
   out.println(gef.getLookStyleSheet());
%>
<link type="text/css" rel="stylesheet" href="<%=m_context%>/util/styleSheets/modal-message.css">

<script type="text/javascript" src="<%=m_context%>/util/javaScript/modalMessage/modal-message.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>
<script type="text/javascript" src="<%=m_context%>/pdcPeas/jsp/javascript/formUtil.js"></script>

<script language="javascript">
	function submitContent(cUrl, componentId) {
	
		jumpToComponent(componentId);
		
		document.AdvancedSearch.contentURL.value = cUrl;
		document.AdvancedSearch.componentId.value = componentId;
		document.AdvancedSearch.action = "GlobalContentForward";
		document.AdvancedSearch.submit();
	}
	
	function jumpToComponent(componentId) {
		if (<%=refreshEnabled.booleanValue()%>)
		{
			//Reload DomainsBar
			parent.SpacesBar.document.privateDomainsForm.component_id.value=componentId;
			parent.SpacesBar.document.privateDomainsForm.privateDomain.value="";
			parent.SpacesBar.document.privateDomainsForm.privateSubDomain.value="";
			parent.SpacesBar.document.privateDomainsForm.submit();
			
			//Reload Topbar
			parent.SpacesBar.reloadTopBar(true);
		}
	}
	
	function goToSpace(spaceId)
	{
		//Reload DomainsBar
		parent.SpacesBar.document.privateDomainsForm.component_id.value=spaceId;
		parent.SpacesBar.document.privateDomainsForm.privateDomain.value="";
		parent.SpacesBar.document.privateDomainsForm.privateSubDomain.value="";
		parent.SpacesBar.document.privateDomainsForm.submit();
			
		//Reload Topbar
		parent.SpacesBar.reloadTopBar(true);
	}


	function openGlossary(uniqueId)
	{
		chemin="<%=m_context%>/RpdcSearch/jsp/AxisTree?query=&uniqueId="+uniqueId;
		largeur = "700";
		hauteur = "500";
		SP_openWindow(chemin,"Pdc_Pop",largeur,hauteur,"scrollbars=yes, resizable=yes");
	}
	
	function getSelectedOjects()
	{
		return getObjects(true);
	}
	function getNotSelectedOjects()
	{
		return getObjects(false);
	}
	
	function getObjects(selected)
	{
		var  items = "";
		var boxItems = document.AdvancedSearch.resultObjects;
		if (boxItems != null){
			// au moins une checkbox exist
			var nbBox = boxItems.length;
			if ( (nbBox == null) && (boxItems.checked == selected) ){
				// il n'y a qu'une checkbox non selectionn�e
				items += boxItems.value+",";
			} else{
				// search not checked boxes 
				for (i=0;i<boxItems.length ;i++ ){
					if (boxItems[i].checked == selected){
						items += boxItems[i].value+",";
					}
				}
			}
		}
		return items;
	}
	
	// this function get all checked boxes by the user and sent 
	// data to the router
	function getSelectedOjectsFromResultList()
	{
		var  selectItems 	= getSelectedOjects();
		var  notSelectItems = getNotSelectedOjects();
		
		if ( selectItems.length > 0) {
			// an axis has been selected !
			document.AdvancedSearch.selectedIds.value = selectItems;
			document.AdvancedSearch.notSelectedIds.value = notSelectItems;
			document.AdvancedSearch.action = "ValidateSelectedObjects";
			document.AdvancedSearch.submit();
		}
	}
	
	function selectEveryResult(chkMaster) {
		var boxItems = document.AdvancedSearch.resultObjects;
		if (boxItems != null){
			// r�f�rence
			var selected = chkMaster.checked;
			
			// au moins une checkbox existe
			var nbBox = boxItems.length;
			if (nbBox == null) {
				// il n'y a qu'une checkbox dont le "checked" est identique � la r�f�rence
				boxItems.checked = selected;
				
			} else {
				// on coche (ou d�coche) les checkboxs, en fonction de la valeur de r�f�rence
				for (i=0; i<boxItems.length; i++ ){
					if (!boxItems[i].disabled)
						boxItems[i].checked = selected;
				}
			}
		}
	}
	
	function openExportPopup() {
		openPopup("ExportPublications");
	}
	
	function openExportPDFPopup() {
		openPopup("ExportAttachementsToPDF");
	}

	function openPopup(url) {
		var selectItems = getSelectedOjects();
		var notSelectItems = getNotSelectedOjects();
		
		chemin = url + "?query=&selectedIds=" + selectItems + "&notSelectedIds=" + notSelectItems;
		largeur = "700";
		hauteur = "500";
		SP_openWindow(chemin, "ExportWindow", largeur, hauteur, "scrollbars=yes, resizable=yes");
		
	}
	
	function doPagination(index)
	{
		var  selectItems 	= getSelectedOjects();
		var  notSelectItems = getNotSelectedOjects();
		
		document.AdvancedSearch.Index.value 			= index;
		document.AdvancedSearch.selectedIds.value 		= selectItems;
		document.AdvancedSearch.notSelectedIds.value 	= notSelectItems;
		document.AdvancedSearch.action					= "Pagination";
		document.AdvancedSearch.submit();
	}
	
	function sendQuery() {
		top.topFrame.document.searchForm.query.value = "";
		document.AdvancedSearch.action 		= "AdvancedSearch";
		
		displayStaticMessage();
    	setTimeout("document.AdvancedSearch.submit();", 500);
	}

	//CBO : ADD
	function changeResDisplay() {
		document.AdvancedSearch.action = "AdvancedSearch";
		document.AdvancedSearch.submit();
	}

	function setSortOrder(order){
		document.AdvancedSearch.sortOrder.value = order;
		document.AdvancedSearch.action = "AdvancedSearch";
		document.AdvancedSearch.submit();
	}
</script>
</HEAD>
<BODY>
<form name="AdvancedSearch" action="javascript:sendQuery()" method="post">
<%
	browseBar.setComponentName(resource.getString("pdcPeas.ResultPage"));

	if (activeSelection.booleanValue())
		operationPane.addOperation(resource.getIcon("pdcPeas.folder_to_valid"), resource.getString("pdcPeas.tracker_to_select"), "javascript:getSelectedOjectsFromResultList()");

	if (exportEnabled.booleanValue())
	{		
		//To export elements
		operationPane.addOperation(resource.getIcon("pdcPeas.toExport"), resource.getString("pdcPeas.ToExport"), "javascript:openExportPopup();");
		operationPane.addOperation(resource.getIcon("pdcPeas.exportPDF"), resource.getString("pdcPeas.exportPDF"), "javascript:openExportPDFPopup();");
	}

	out.println(window.printBefore());
	
	tabs = gef.getTabbedPane();
	tabs.addTab(resource.getString("pdcPeas.SearchResult"), "#", true);
	tabs.addTab(resource.getString("pdcPeas.SearchSimple"), "ChangeSearchTypeToAdvanced", false);
	tabs.addTab(resource.getString("pdcPeas.SearchAdvanced"), "ChangeSearchTypeToExpert", false);
	if ( isXmlSearchVisible )
		tabs.addTab(resource.getString("pdcPeas.SearchXml"), "ChangeSearchTypeToXml", false);	
	
	out.println(tabs.print());
	
    out.println(frame.printBefore());
    
    out.println(board.printBefore());
%>
		<table border="0" cellspacing="0" cellpadding="5" width="100%">
        <tr align="center">
          <td valign="middle" align="left" class="txtlibform" width="30%"><%=resource.getString("pdcPeas.SearchFind")%></td>
          <td align="left" valign="middle">
          	<table border="0" cellspacing="0" cellpadding="0"><tr valign="middle">
          		<td valign="middle"><input type="text" name="query" size="60" value="<%=keywords%>"><input type="hidden" name="mode"></td>
          		<td valign="middle">&nbsp;</td>
          		<td valign="middle" align="left" width="100%"><% out.println(searchButton.print());%></td>
          	</tr></table>
          </td>
        </tr>
        </table>

<%
	//CBO : ADD
	if ("All".equals(displayParamChoices) || "Res".equals(displayParamChoices)) 
	{
%>
		<table border="0" cellspacing="0" cellpadding="5" width="100%">
		<tr align="center">
          <td valign="middle" align="left" class="txtlibform" width="30%"><%=resource.getString("pdcPeas.NbResultSearch")%></td>
          <td align="left" valign="selectNS"><select name="nbRes" size="1" onChange="javascript:changeResDisplay()">
            <%
				String selected = "";
				if (choiceNbResToDisplay != null)
		  		{
		  			Iterator it = (Iterator) choiceNbResToDisplay.iterator();
					String choice;
		  			while (it.hasNext()) 
			  		{
						selected = "";
						choice = (String) it.next();
						if(choice.equals(nbResToDisplay.toString())) {
							selected = "selected";
						}
						out.println("<option value=\""+choice+"\" "+selected+">"+choice+"</option>");
					}
				}
             %>
          </select>
		  <span class="txtlibform">&nbsp;&nbsp;&nbsp;<%=resource.getString("pdcPeas.SortResultSearch")%>&nbsp;&nbsp;&nbsp;</span>
		  <select name="sortRes" size="1" onChange="javascript:changeResDisplay()">
            <%		
				for (int i=1; i<=5; i++) {
					selected = "";
					if(sortValue.intValue() == i) {
						selected = "selected";
					}
					out.println("<option value=\""+i+"\""+selected+">"+resource.getString("pdcPeas.SortValueSearch."+i)+"</option>");
				}
             %>
          </select>
		  <%
				String classCSS = "";
				if("ASC".equals(sortOrder)) {
					classCSS = "ArrayNavigationOn";
				}
		  %>
		  <a href="javascript:setSortOrder('ASC')" class="<%=classCSS%>">ASC</a>&nbsp;
		  <%
				classCSS = "";
				if("DESC".equals(sortOrder)) {
					classCSS = "ArrayNavigationOn";
				}
		  %>
		  <a href="javascript:setSortOrder('DESC')" class="<%=classCSS%>">DESC</a>
		  </td>
        </tr>
		<% if (activeSelection.booleanValue() || exportEnabled.booleanValue()) { %>
			<tr>
				<td class="txtlibform"><%=resource.getString("pdcPeas.selectAll") %></td><td><input type="checkbox" name="selectAll" onClick="selectEveryResult(this);"></td></tr>
			</tr>
		<% }  %>	
		</table>
<%
	}
	//CBO : FIN ADD

	out.println(board.printAfter());
    out.println("<br>");
	out.println(board.printBefore());
	
	if (results != null)
	{
		Pagination	pagination			= gef.getPagination(nbTotalResults, nbResToDisplay.intValue(), indexOfFirstResult);

		List		resultsOnThisPage	= results;

		out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
		displayItemsListHeader(keywords, pagination, resource, out);
		out.println("<tr><td>");

		out.println("<table border=\"0\">");
		GlobalSilverResult	gsr			= null;
						
		for(int nI=0; resultsOnThisPage != null && nI < resultsOnThisPage.size(); nI++){
			gsr				= (GlobalSilverResult) resultsOnThisPage.get(nI);
			sName			= Encode.javaStringToHtmlString(gsr.getName(language));
			sDescription	= gsr.getDescription(language);
			if (sDescription != null && sDescription.length() > 400)
				sDescription = sDescription.substring(0, 400)+"...";
			sURL			= gsr.getTitleLink();
			sDownloadURL	= gsr.getDownloadLink();
			sLocation		= gsr.getLocation();
			sCreatorName	= gsr.getCreatorName();
			try	{
				sCreationDate	= resource.getOutputDate(gsr.getDate());
			} catch (Exception e) {
				sCreationDate	= null;
			}
			
			out.println("<tr>");
			
			if (showPertinence)
				out.println("<td valign=\"top\">"+displayPertinence(gsr.getRawScore(), fullStarSrc, emptyStarSrc)+"</td>");
			
			if (activeSelection.booleanValue() || exportEnabled.booleanValue()) {
				if (gsr.isExportable())
				{
					String checked = "";
					if (gsr.isSelected())
						checked = "checked";
				
					out.println("<td valign=\"top\"><input type=\"checkbox\" "+checked+" name=\"resultObjects\" value=\""+gsr.getId()+"-"+gsr.getInstanceId()+"\"></td>");
				}
			   	else 
			   		out.println("<td valign=\"top\"><input type=\"checkbox\" disabled name=\"resultObjects\" value=\""+gsr.getId()+"-"+gsr.getInstanceId()+"\"></td>");
			}
		
			if (gsr.getType() != null && (gsr.getType().startsWith("Attachment")|| gsr.getType().startsWith("Versioning") || gsr.getType().equals("LinkedFile")) ) {
                fileType	= sName.substring(sName.lastIndexOf(".")+1, sName.length());
				fileIcon	= FileRepositoryManager.getFileIcon(fileType);
				sName = "<img src=\""+fileIcon+"\" border=\"0\" width=\"30\" heigth=\"30\" align=\"absmiddle\"/>"+sName;
				//no preview, display this is an attachment
				if (gsr.getType().startsWith("Attachment") || gsr.getType().equals("LinkedFile"))
					sDescription = null;
			}
			
			out.println("<td>");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\"><tr>");
			
			if (gsr.getThumbnailURL() != null && gsr.getThumbnailURL().length()>0)
			{
				out.println("<td><img src=\""+gsr.getThumbnailURL()+"\" border=0 width=\""+gsr.getThumbnailWidth()+"\" height=\""+gsr.getThumbnailHeight()+"\"></td>");
				out.println("<td>&nbsp;</td>");
			}
			
			out.println("<td valign=\"top\">");
			if (activeSelection.booleanValue())
				out.println("<span class=\"textePetitBold\">"+sName+"</span>");
			else
				out.println("<a href=\""+sURL+"\"><span class=\"textePetitBold\">"+sName+"</span></a>");
			
			if (StringUtil.isDefined(sDownloadURL))
			{
				//affiche le lien pour le t�l�chargement
				out.println("<a href=\""+sDownloadURL+"\" target=\"_blank\">"+downloadSrc+"</a>");
			}
			if (sCreatorName != null && sCreatorName.length()>0)
				out.println(" - "+Encode.javaStringToHtmlString(sCreatorName));
			if (sCreationDate != null && sCreationDate.length()>0)
				out.print(" ("+sCreationDate + ")");
							
			if (sDescription != null && sDescription.length()>0)
				out.println("<BR><i>"+Encode.javaStringToHtmlString(sDescription)+"</i>");
					
			if (sLocation != null && sLocation.length()>0)
				out.println("<BR>"+Encode.javaStringToHtmlString(sLocation));
			out.println("<td>");
				
			out.println("</tr></table>");
			
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");
		}	
		out.println("</table>");

		out.println("</td></tr>");
		out.println("<tr class=\"intfdcolor4\"><td>&nbsp;</td></tr>");

		if (nbTotalResults > resultsOnThisPage.size())
		{
			out.println("<tr valign=\"middle\" class=\"intfdcolor\">");
			out.println("<td align=\"center\">");
			out.println(pagination.printIndex("doPagination"));
			out.println("</td>");
			out.println("</tr>");
		}
		
		out.println("</table>");
	} else {
		out.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\" class=\"intfdcolor\">");
		out.println("<td align=\"center\">"+resource.getString("pdcPeas.NoResult")+"</td>");
		out.println("</tr>");
		out.println("</table>");
	}
	out.println(board.printAfter());
	
    out.println("<br>");
	out.println(board.printBefore());
    %>
		<table width="100%" border="0"><tr><td valign="top" width="30%">
		<%=resource.getString("pdcPeas.helpCol1Header")%><br><br>
		<%=resource.getString("pdcPeas.helpCol1Content1")%><br>
		<%=resource.getString("pdcPeas.helpCol1Content2")%><br>
		<%=resource.getString("pdcPeas.helpCol1Content3")%><br>
		</td>
		<td>&nbsp;</td>
		<td valign="top" width="30%">
		<%=resource.getString("pdcPeas.helpCol2Header")%><br><br>
		<%=resource.getString("pdcPeas.helpCol2Content1")%><br>
		<%=resource.getString("pdcPeas.helpCol2Content2")%><br>
		<%=resource.getString("pdcPeas.helpCol2Content3")%><br>
		<%=resource.getString("pdcPeas.helpCol2Content4")%><br>
		<%=resource.getString("pdcPeas.helpCol2Content5")%><br>
		</td>
		<td>&nbsp;</td>
		<td valign="top" width="30%">
		<%=resource.getString("pdcPeas.helpCol3Header")%><br><br>
		<%=resource.getString("pdcPeas.helpCol3Content1")%><br>
		<%=resource.getString("pdcPeas.helpCol3Content2")%><br>
		<%=resource.getString("pdcPeas.helpCol3Content3")%><br>
		<%=resource.getString("pdcPeas.helpCol3Content4")%><br>
		</td>
		</tr></table>
	<%
	out.println(board.printAfter());
	out.println(frame.printAfter());
	out.println(window.printAfter());
%>
	<input type="hidden" name="selectedIds">
	<input type="hidden" name="notSelectedIds">
	<input type="hidden" name="Index">
	<input type="hidden" name="contentURL">
	<input type="hidden" name="componentId">
	<!-- CBO : ADD -->
	<input type="hidden" name="sortOrder" value="<%=sortOrder%>">
</form>
<%@ include file="modalMessage.jsp.inc" %>
</body>
</html>