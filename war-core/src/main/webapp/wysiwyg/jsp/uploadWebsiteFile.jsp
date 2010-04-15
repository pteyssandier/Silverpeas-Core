<%--

    Copyright (C) 2000 - 2009 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have received a copy of the text describing
    the FLOSS exception, and it is also available here:
    "http://repository.silverpeas.com/legal/licensing"

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>

<%
response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires",-1); //prevents caching at the proxy server
%>
<%@page import="com.silverpeas.util.EncodeHelper"%>
<%@ page import="com.stratelia.webactiv.util.ResourceLocator"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.board.Board"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.buttonPanes.ButtonPane"%>
<%@ page import="com.stratelia.webactiv.util.viewGenerator.html.buttons.Button"%>
<%@ page import="com.silverpeas.util.web.servlet.FileUploadUtil"%>
<%@ page import="org.apache.commons.fileupload.FileItem"%>
<%@ page import="java.io.File"%>
<%@ include file="checkScc.jsp" %>

<% 
	String language = request.getParameter("Language");
	String thePath = request.getParameter("Path");
	
    String m_context = GeneralPropertiesManager.getGeneralResourceLocator().getString("ApplicationURL");
    ResourceLocator message = new ResourceLocator("com.stratelia.silverpeas.wysiwyg.multilang.wysiwygBundle", language);

    //Le cadre
    Board board = gef.getBoard();
		
    //Icons
    String mandatoryField = m_context + "/util/icons/mandatoryField.gif";
    if (FileUploadUtil.isRequestMultipart(request))
    {
	    FileItem fileItem = FileUploadUtil.getFile(request);
	    if (fileItem != null)
	    {
		  	String fichierName = FileUploadUtil.getFileName(fileItem);
		  	File fichier = new File(thePath, fichierName);
		  	FileUploadUtil.saveToFile(fichier, fileItem);
		  	
		  	String urlPath = thePath.substring(thePath.indexOf("/website"));
		  	
		  	%>
			<script language="javascript">
				window.opener.document.getElementById('txtUrl').value='<%=EncodeHelper.javaStringToJsString(urlPath+'/'+fichierName)%>';
				window.close();
			</script>
			<%
	    }
    }
%>
	<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>
	<script language="javascript">
		function isCorrect(nom) {
	    	if (nom.indexOf("&")>-1 || nom.indexOf(";")>-1 || nom.indexOf("+")>-1 ||
		        nom.indexOf("%")>-1 || nom.indexOf("#")>-1 || nom.indexOf("'")>-1 ||
		        nom.indexOf("�")>-1 || nom.indexOf("�")>-1 || nom.indexOf("�")>-1 ||
		        nom.indexOf("�")>-1 || nom.indexOf("�")>-1 || nom.indexOf("^")>-1 ||
		        nom.indexOf("�")>-1 || nom.indexOf("�")>-1 || /*nom.indexOf("�")>-1 ||*/
		        nom.indexOf("�")>-1 || nom.indexOf("�")>-1 || nom.indexOf("�")>-1 ||
		        nom.indexOf("�")>-1) {
		    }
		    return true;
	     }
	    function B_VALIDER_ONCLICK() {                  
	        if (checkString(document.descriptionFile.fichier, "<%=message.getString("ErreurmPrefix")+message.getString("FichierUpload")+message.getString("ErreurmSuffix")%>")) {
	            var file = document.descriptionFile.fichier.value;
	            var indexPoint = file.lastIndexOf(".");
	            var ext = file.substring(indexPoint + 1);
	            if (ext.toLowerCase() != "gif" && ext.toLowerCase()!= "jpg" && 
	        		ext.toLowerCase() != "bmp" && ext.toLowerCase() != "png" && 
	        		ext.toLowerCase() != "pcd" && ext.toLowerCase() != "tga" && 
	        		ext.toLowerCase() != "tif" && ext.toLowerCase() != "swf")
	            { 
	            	alert("<%=message.getString("ErreurFichierUpload")%>");
	            }
	            else if (!isCorrect(file))
	            {
		        	// verif caract�res speciaux contenus dans le nom du fichier
	            	alert("<%=message.getString("NameFile")%> <%=message.getString("MustNotContainSpecialChar")%>\n<%=EncodeHelper.javaStringToJsString(message.getString("Char7"))%>\n");
	            }	        
	            else 
		        {
	            	document.descriptionFile.submit();
	            }
	       }
	    }
	</script>

<% out.println(board.printBefore()); %>

	  <FORM NAME="descriptionFile" ACTION="uploadFile.jsp" METHOD="POST" ENCTYPE="multipart/form-data">
		<table>      
	    <TR>
	        <TD class="txtlibform"><%=message.getString("FichierUpload")%> : </TD>
	        <td valign="top">&nbsp;<input type="file" name="fichier" size="50">&nbsp;<img border="0" src="<%=mandatoryField%>" width="5" height="5"/></td>
	    </TR>
        <TR> 
            <TD colspan="2">(<img border="0" src="<%=mandatoryField%>" width="5" height="5"> : <%=message.getString("RequiredField")%>)</TD>
        </TR>
        </table>
	</FORM>
  <%
  out.println(board.printAfter());
    
	//fin du code
	ButtonPane buttonPane = gef.getButtonPane();
	Button validerButton = (Button) gef.getFormButton(message.getString("Valider"), "javascript:onClick=B_VALIDER_ONCLICK();", false);
	buttonPane.addButton(validerButton);
          
  String bodyPart ="<center><br/>";
  bodyPart += buttonPane.print();
  bodyPart +="</center>";
	out.println(bodyPart);

 %>