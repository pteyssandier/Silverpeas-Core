<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>

<%@ page import="com.stratelia.silverpeas.silvertrace.SilverTrace"%>
<%@ page import="com.stratelia.silverpeas.peasCore.URLManager"%>

<%@ page import="com.stratelia.webactiv.beans.admin.ComponentInstLight"%>
<%@ page import="com.stratelia.webactiv.util.ResourceLocator"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.browseBars.BrowseBar"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.window.Window"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.buttonPanes.ButtonPane"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.buttons.Button"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.Encode"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.GraphicElementFactory "%>
<%@ page import="com.stratelia.webactiv.util.GeneralPropertiesManager"%>

<%@ page import="com.stratelia.silverpeas.wysiwyg.control.WysiwygController"%>
<%@ page import="com.stratelia.silverpeas.wysiwyg.*"%>
<%@ page import="com.silverpeas.util.StringUtil"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.io.File"%>
<%@ page import="javax.servlet.http.*"%>

<%
    String spaceId = "";
    String componentId = "";
    String spaceName = "";
    String componentName = "";
    String objectId = "";
    String language = "";
    String contentLanguage = "";
    String actionWysiwyg = null;
    String codeWysiwyg = "";
    String returnUrl = null;
    String browseInformation = null;
    String[][] collectionImages = null;

		//For parsing absolute url (Bug FCKEditor)
		String server = request.getRequestURL().substring(0,request.getRequestURL().toString().lastIndexOf(URLManager.getApplicationURL()));
	
		String fileName = "";
		String path = "";
		String[][] collectionPages = null;
		String specificURL = "";		//For Websites only

    String wysiwygTextValue = "";
    String editedText;
    String context = URLManager.getApplicationURL();
    String iconsPath = context + "/util/icons/wysiwyg/";
    String imagesContext = "";
    String userId = "";
    String exit = "";
    String indexIt = "";

    actionWysiwyg = (String) request.getParameter("actionWysiwyg");
    SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "actionWysiwyg="+actionWysiwyg);

    if (actionWysiwyg == null)
        actionWysiwyg = "Load";
    
    if (actionWysiwyg.equals("SaveHtmlAndExit") || actionWysiwyg.equals("Refresh") || actionWysiwyg.equals("SaveHtml")) {
        codeWysiwyg 	= request.getParameter("codeWysiwyg");
        spaceId 		= (String) session.getAttribute("WYSIWYG_SpaceId");
        spaceName 		= (String) session.getAttribute("WYSIWYG_SpaceName");
        componentId 	= (String) session.getAttribute("WYSIWYG_ComponentId");
        componentName 	= (String) session.getAttribute("WYSIWYG_ComponentName");
        objectId 		= (String) session.getAttribute("WYSIWYG_ObjectId");
        browseInformation = (String) session.getAttribute("WYSIWYG_BrowseInfo");
        language 		= (String) session.getAttribute("WYSIWYG_Language");
        contentLanguage = (String) session.getAttribute("WYSIWYG_ContentLanguage");
        returnUrl 		= (String) session.getAttribute("WYSIWYG_ReturnUrl");
        userId			= (String) session.getAttribute("WYSIWYG_UserId");
        fileName		= (String) session.getAttribute("WYSIWYG_FileName");
        path			= (String) session.getAttribute("WYSIWYG_Path");
		specificURL		= (String) session.getAttribute("WYSIWYG_SpecificURL");
		indexIt			= (String) session.getAttribute("WYSIWYG_IndexIt");
		exit			= request.getParameter("Exit");

        if (actionWysiwyg.equals("SaveHtmlAndExit") || actionWysiwyg.equals("SaveHtml"))
        {
        	//codeWysiwyg = Encode.encodeSpecialChar(codeWysiwyg);
        	
        	if (componentId.startsWith(WysiwygController.WYSIWYG_WEBSITES)) { 
			
        		codeWysiwyg = codeWysiwyg.replaceAll("../../../../../", "/");
        		codeWysiwyg = codeWysiwyg.replaceAll(server+":", "");
        		codeWysiwyg = codeWysiwyg.replaceAll(server, "");
		
			} else {
				
				codeWysiwyg = codeWysiwyg.replaceAll("../../../../", URLManager.getApplicationURL()+"/");
        		codeWysiwyg = codeWysiwyg.replaceAll(server+":", "");
        		codeWysiwyg = codeWysiwyg.replaceAll(server, "");
			}
        		
	        if (componentId.startsWith(WysiwygController.WYSIWYG_WEBSITES))
	        {
            	WysiwygController.updateWebsite(path, fileName, codeWysiwyg);
            }
            else
            {
            	boolean bIndexIt = (!StringUtil.isDefined(indexIt) || !"false".equalsIgnoreCase(indexIt));
            	if (StringUtil.isDefined(contentLanguage))
            		WysiwygController.save(codeWysiwyg, spaceId, componentId, objectId, userId, contentLanguage, bIndexIt);
            	else
            		WysiwygController.updateFileAndAttachment(codeWysiwyg, spaceId, componentId, objectId, userId, bIndexIt);
            }
        }
        if (actionWysiwyg.equals("Refresh") || actionWysiwyg.equals("SaveHtml")) {
            wysiwygTextValue = codeWysiwyg;
            if (componentId.startsWith(WysiwygController.WYSIWYG_WEBSITES))
            {
	            collectionImages = WysiwygController.getWebsiteImages(path, componentId);
	            collectionPages = WysiwygController.getWebsitePages(path, componentId);
			    SilverTrace.info("wysiwyg", "wysiwyg.htmlEditor.jsp", "root.MSG_GEN_PARAM_VALUE", "nb collectionPages = " + collectionPages.length+" nb collectionImages="+collectionImages.length);
			}
			else
			{
				imagesContext = WysiwygController.getImagesFileName(objectId);
				SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "objectId="+objectId+" imagesContext="+imagesContext);
				collectionImages = WysiwygController.searchAllAttachments(objectId, spaceId, componentId, imagesContext);
			}
        }
    }
    else if (actionWysiwyg.equals("Load")) {

        spaceId = request.getParameter("SpaceId");
        session.setAttribute("WYSIWYG_SpaceId", spaceId);

        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite", "spaceId = "+spaceId);

        spaceName = request.getParameter("SpaceName");
        session.setAttribute("WYSIWYG_SpaceName", spaceName);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite", "spaceName = "+spaceName);

        componentId = request.getParameter("ComponentId");
        session.setAttribute("WYSIWYG_ComponentId", componentId);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite", "componentId = "+componentId);

        componentName = request.getParameter("ComponentName");
        session.setAttribute("WYSIWYG_ComponentName", componentName);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite",  "componentName = "+componentName);

        objectId = request.getParameter("ObjectId");
        session.setAttribute("WYSIWYG_ObjectId", objectId);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite",  "ObjectId = "+objectId);

        returnUrl = request.getParameter("ReturnUrl");
        session.setAttribute("WYSIWYG_ReturnUrl", returnUrl);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite",  "return Url= "+returnUrl);

        browseInformation = request.getParameter("BrowseInfo");
        session.setAttribute("WYSIWYG_BrowseInfo", browseInformation);
        
        userId = request.getParameter("UserId");
        session.setAttribute("WYSIWYG_UserId", userId);

        fileName = request.getParameter("FileName");
        session.setAttribute("WYSIWYG_FileName", fileName);
		path = request.getParameter("Path");
        session.setAttribute("WYSIWYG_Path", path);
        SilverTrace.debug("wysiwyg", "Wysiwyg.htmlEditorJSP", "createSite",  "fileName= "+fileName+" Path="+path);
       
		language = request.getParameter("Language");
        if (language == null)
            language = "en";
        session.setAttribute("WYSIWYG_Language", language);
        
        contentLanguage = request.getParameter("ContentLanguage");
        session.setAttribute("WYSIWYG_ContentLanguage", contentLanguage);
        
        indexIt = request.getParameter("IndexIt");
        session.setAttribute("WYSIWYG_IndexIt", indexIt);

        if (componentId.startsWith(WysiwygController.WYSIWYG_WEBSITES))
        {
          collectionImages = WysiwygController.getWebsiteImages(path, componentId);
          collectionPages = WysiwygController.getWebsitePages(path, componentId);
          SilverTrace.info("wysiwyg", "wysiwyg.htmlEditor.jsp", "root.MSG_GEN_PARAM_VALUE", "nb collectionPages = " + collectionPages.length+" nb collectionImages="+collectionImages.length);
          specificURL = "/website/"+componentId+"/"+objectId+"/";
		}
		else
		{
			imagesContext = WysiwygController.getImagesFileName(objectId);
	        collectionImages = WysiwygController.searchAllAttachments(objectId, spaceId, componentId, imagesContext);
		}
        session.setAttribute("WYSIWYG_SpecificURL", specificURL);

        try {
	        if (componentId.startsWith(WysiwygController.WYSIWYG_WEBSITES))
	        	wysiwygTextValue = WysiwygController.loadFileWebsite(path, fileName);
			else
			{
				if (StringUtil.isDefined(contentLanguage))
					wysiwygTextValue = WysiwygController.load(spaceId, componentId, objectId, contentLanguage);
				else
					wysiwygTextValue = WysiwygController.loadFileAndAttachment(spaceId, componentId, objectId);
			}
	        if (wysiwygTextValue==null)
	        	wysiwygTextValue = "";
        } catch (WysiwygException exc) {
              // no file
        }
    }

	ResourceLocator 		message 	= new ResourceLocator("com.stratelia.silverpeas.wysiwyg.multilang.wysiwygBundle", language);
	ResourceLocator 		settings 	= new ResourceLocator("com.stratelia.silverpeas.wysiwyg.settings.wysiwygSettings", language);
	GraphicElementFactory 	gef 		= (GraphicElementFactory) session.getAttribute("SessionGraphicElementFactory");
    
    Window 		window 		= gef.getWindow();
    BrowseBar 	browseBar 	= window.getBrowseBar();
    
	browseBar.setDomainName(spaceName);
    browseBar.setComponentName(componentName, returnUrl);
    browseBar.setExtraInformation(browseInformation);
%>

<HTML>
<HEAD>
<TITLE>Silverpeas Wysiwyg Editor</TITLE>
<%
	out.print(gef.getLookStyleSheet());
%>
<style>
	.TB_Expand
	{
		background-color: #efefde;
		padding: 2px 2px 2px 2px;
		border: #efefde 1px outset;
	}
</style>
<script type="text/javascript" src="<%=context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=context%>/wysiwyg/jsp/FCKeditor/fckeditor.js"></script>
</head>
<body>
<%  
    out.println(window.printBefore());
	String wysiwygContent = "";
%>

<script language="javascript">
	var oEditor;
	var galleryWindow = window;
	window.onload = function()
	{
			var oFCKeditor = new FCKeditor('codeWysiwyg');
			oFCKeditor.BasePath = "<%=context%>/wysiwyg/jsp/FCKeditor/";
			oFCKeditor.Width = "750";
			oFCKeditor.Height = "500";
			oFCKeditor.Config[ "AutoDetectLanguage" ] = false;
			oFCKeditor.Config[ "DefaultLanguage" ] = "<%=language%>";
			<% if (!settings.getString("configFile").equals("")) { %>
				oFCKeditor.Config['CustomConfigurationsPath'] = "<%=settings.getString("configFile")%>";
			<% } else { %>
				oFCKeditor.Config[ "EditorAreaCSS" ] = "<%=context%>/util/styleSheets/globalSP_SilverpeasV4.css";
			<% } %>
			oFCKeditor.ReplaceTextarea();
	}	

	function FCKeditor_OnComplete( editorInstance )
	{
			oEditor = FCKeditorAPI.GetInstance(editorInstance.Name);
	}

	function B_VALIDER_ONCLICK() {
		oEditor.Focus();
		document.recupHtml.actionWysiwyg.value = "SaveHtmlAndExit";
		document.recupHtml.Exit.value = "1";
		document.recupHtml.submit();
	}
	
	function refresh(){
		oEditor.Focus();
	  	document.recupHtml.actionWysiwyg.value = "Refresh";
	  	document.recupHtml.submit();
	}
	
	function Save()
	{
		oEditor.Focus();
	  	document.recupHtml.actionWysiwyg.value = "SaveHtml";
	  	document.recupHtml.Exit.value = "0";
	  	document.recupHtml.submit();
	}
	
	function choixImage() {
		oEditor.Focus();
		index = document.recupHtml.images.selectedIndex;
		var str = document.recupHtml.images.options[index].value;
		var title = document.recupHtml.images.options[index].text;

		if (index != 0 && str != null) {
			var ext = title.substring(title.length - 4);

    	    if (ext.toLowerCase() == ".swf") {//du flash
				oEditor.InsertHtml('<embed type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" 					src="'+str+'"></embed>');
			} else {
				oEditor.InsertHtml('<img border="0" src="'+str+'">');
			}
		}
		//var wysiwygContent = oEditor.GetHTML();
		//re = new RegExp("http:///", "gi");
		//var newstr = wysiwygContent.replace(re, "/");
		//oEditor.SetHTML(newstr);
	}
	
	function choixGallery()
	{
		index = document.recupHtml.galleries.selectedIndex;
		var componentId = document.recupHtml.galleries.options[index].value;
		if (index != 0)
		{
			url = "<%=context%>/gallery/jsp/wysiwygBrowser.jsp?ComponentId="+componentId+"&Language=<%=language%>";
			windowName = "galleryWindow";
			larg = "820";
			haut = "600";
			windowParams = "directories=0,menubar=0,toolbar=0, alwaysRaised";
			if (!galleryWindow.closed && galleryWindow.name=="galleryWindow")
				galleryWindow.close();
			galleryWindow = SP_openWindow(url, windowName, larg, haut, windowParams);
		}
	}
	
	function choixImageInGallery(url)
	{
		oEditor.Focus();
		oEditor.InsertHtml('<img border="0" src="'+url+'">');
		//var wysiwygContent = oEditor.GetHTML();
		//re = new RegExp("http:///", "gi");
		//var newstr = wysiwygContent.replace(re, "/");
		//oEditor.SetHTML(newstr);
	}
	
	function choixLien() {
		oEditor.Focus();
		index = document.recupHtml.liens.selectedIndex;
		var str = document.recupHtml.liens.options[index].value;
		if (index != 0 && str != null)
				oEditor.InsertHtml('<a href="'+str+'">'+str.substring(str.lastIndexOf("/")+1)+"</a>");
	}
</script>

<%
if (actionWysiwyg.startsWith("SaveHtml") && "1".equals(exit)) {
%>
	<script language="javascript">
	  location.href = '<%=Encode.javaStringToJsString(returnUrl)%>';
	</script>
<%
}
else if (actionWysiwyg.equals("Load") || actionWysiwyg.equals("Refresh") || actionWysiwyg.equals("SaveHtml"))
{
%>
	<form method="post" name="recupHtml" action="<%=context%>/wysiwyg/jsp/htmlEditor.jsp" onSubmit="return Save();">
		<table border=0 cellpadding=0 cellspacing=0>
			<tr class="TB_Expand">
			<td class="TB_Expand" align="center">
				<select id="images" name="images" onchange="choixImage();this.selectedIndex=0">
					<option selected><%=message.getString("Image")%></option>
							<%
							if (collectionImages != null) {
								int nbImages = collectionImages.length;
								for(int i=0; i < nbImages; i++ ) {
									out.println("<option value=\""+specificURL+collectionImages[i][0]+"\">"+collectionImages[i][1]+"</option>");
								}
							}
							%>
				</select>
				<%
					List galleries = WysiwygController.getGalleries();
					if (galleries != null)
					{
						%>
						<select id="galleries" name="galleries" onchange="choixGallery();this.selectedIndex=0;">
							<option selected><%=message.getString("Galleries")%></option>
							<%
							for(int i=0; i < galleries.size(); i++ ) {
								ComponentInstLight gallery = (ComponentInstLight) galleries.get(i);
								out.println("<option value=\""+gallery.getId()+"\">"+gallery.getLabel()+"</option>");
							}
							%>
						</select>
						<%
					}
				%>
				<%
				 // Only for WebSites
				 if (collectionPages != null) { %>
					<select name="liens" onchange="choixLien(); this.selectedIndex=0">
				        <option selected><%=message.getString("Links")%></option>
				        <%
								int nbPages = collectionPages.length;
								for(int i=0; i < nbPages; i++ ) {
									out.println("<option value=\""+specificURL+collectionPages[i][0]+"\">"+collectionPages[i][1]+"</option>");
								}
					      %>
						</option>
					</select>
				<% } %>
			</td></tr>
			<tr><td>
				<textarea id="codeWysiwyg" name="codeWysiwyg"><%=wysiwygTextValue%></textarea>
			</td></tr>
		</table>
		<INPUT name="code" type="hidden">
		<INPUT name="actionWysiwyg" type="hidden">
		<INPUT name="origin" type="hidden" value="<%=componentId%>">
		<INPUT name="Exit" type="hidden">
<%
		ButtonPane buttonPane = gef.getButtonPane();
		Button button = gef.getFormButton(message.getString("SaveAndExit"), "javascript:onClick=B_VALIDER_ONCLICK();", false);
		Button buttonExit = gef.getFormButton(message.getString("Cancel"), "javascript:location.href='"+returnUrl+"';", false);
		buttonPane.addButton(button);
		buttonPane.addButton(buttonExit);
    	out.println("<center><BR>"+buttonPane.print()+"</center>");
%>
	</form>
<%
}
	out.println(window.printAfter());
%>
	</body>
</HTML>