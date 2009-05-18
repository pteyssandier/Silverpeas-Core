<%@ include file="check.jsp" %>

<%
List removedSpaces 		= (List) request.getAttribute("Spaces");
List removedComponents 	= (List) request.getAttribute("Components");
	
operationPane.addOperation(resource.getIcon("JSPP.restoreAll"),resource.getString("JSPP.BinRestore"),"javascript:onClick=restore()");
operationPane.addOperation(resource.getIcon("JSPP.deleteAll"),resource.getString("JSPP.BinDelete"),"javascript:onClick=remove()");

browseBar.setComponentName(resource.getString("JSPP.Bin"));

boolean emptyBin = true;
%>

<HTML>
<HEAD>
<%
out.println(gef.getLookStyleSheet());
%>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/animation.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/checkForm.js"></script>
<script type="text/javascript" src="<%=m_context%>/util/javaScript/overlib.js"></script>
<script language="JavaScript">
<!--
function removeItem(id) {	
    if (window.confirm("<%=resource.getString("JSPP.BinDeleteConfirm")%>")) { 
    	location.href = "RemoveDefinitely?ItemId="+id;
	}
}

function remove() {	
    if (window.confirm("<%=resource.getString("JSPP.BinDeleteConfirmSelected")%>")) {
    	window.document.binForm.action = "RemoveDefinitely";
    	window.document.binForm.submit();
	}
}

function restore() {
	if (window.confirm("<%=resource.getString("JSPP.BinRestoreSelected")%>")) {
    	window.document.binForm.action = "RestoreFromBin";
    	window.document.binForm.submit();
	}
}

-->
</script>
</HEAD>

<BODY marginheight=5 marginwidth=5 leftmargin=5 topmargin=5>
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>
<%
out.println(window.printBefore());
out.println(frame.printBefore());
%>
<center>
<form name="binForm" action="" method="POST">
<%
	ArrayPane arrayPane = gef.getArrayPane("binContentSpaces", "ViewBin", request, session);
	arrayPane.addArrayColumn(resource.getString("GML.space"));
	arrayPane.addArrayColumn(resource.getString("JSPP.BinRemoveDate"));
	ArrayColumn columnOp = arrayPane.addArrayColumn(resource.getString("GML.operation"));
	columnOp.setSortable(false);

	//Array of deleted spaces
	if (removedSpaces != null && removedSpaces.size() != 0) 
	{
		emptyBin = false;
		Iterator it = (Iterator) removedSpaces.iterator();
		while (it.hasNext()) 
		{
			ArrayLine line = arrayPane.addArrayLine();
			SpaceInstLight space = (SpaceInstLight) it.next();
			ArrayCellText cellLabel = null;
			if (space.isRoot())
				cellLabel = line.addArrayCellText(space.getName());
			else
				cellLabel = line.addArrayCellText("<div onmouseover=\"return overlib('"+Encode.javaStringToJsString(space.getPath(" > "))+"', CAPTION, '"+resource.getString("GML.path")+"');\" onmouseout=\"return nd();\">"+space.getName()+"</div>");
			cellLabel.setCompareOn(space.getName());
			ArrayCellText cell = line.addArrayCellText(resource.getOutputDateAndHour(space.getRemoveDate())+"&nbsp;("+space.getRemoverName()+")");
			cell.setCompareOn(space.getRemoveDate());
		
			IconPane iconPane = gef.getIconPane();
			Icon restoreIcon = iconPane.addIcon();
			restoreIcon.setProperties(resource.getIcon("JSPP.restore"), resource.getString("JSPP.BinRestore"), "RestoreFromBin?ItemId="+space.getFullId());
			Icon deleteIcon = iconPane.addIcon();
			deleteIcon.setProperties(resource.getIcon("JSPP.delete"), resource.getString("JSPP.BinDelete"), "javaScript:onClick=removeItem('"+space.getFullId()+"')");
			line.addArrayCellText(restoreIcon.print()+"&nbsp;&nbsp;&nbsp;"+deleteIcon.print()+"&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"ItemIds\" value=\""+space.getFullId()+"\">");
		}
		out.println(arrayPane.print());
	}
	
	//Array of deleted components
	arrayPane = gef.getArrayPane("binContentComponents", "ViewBin", request, session);
	arrayPane.addArrayColumn(resource.getString("GML.component"));
	arrayPane.addArrayColumn(resource.getString("JSPP.BinRemoveDate"));
	columnOp = arrayPane.addArrayColumn(resource.getString("GML.operation"));
	columnOp.setSortable(false);

	if (removedComponents != null && removedComponents.size() != 0) 
	{
		if (!emptyBin)
			out.println("<BR/>");
			
		emptyBin = false;
		Iterator it = (Iterator) removedComponents.iterator();
		while (it.hasNext()) 
		{
			ArrayLine line = arrayPane.addArrayLine();
			ComponentInstLight component = (ComponentInstLight) it.next();
			line.addArrayCellText("<div onmouseover=\"return overlib('"+Encode.javaStringToJsString(component.getPath(" > "))+"', CAPTION, '"+resource.getString("GML.path")+"');\" onmouseout=\"return nd();\">"+component.getLabel()+"</div>");
			ArrayCellText cell = line.addArrayCellText(resource.getOutputDateAndHour(component.getRemoveDate())+"&nbsp;("+component.getRemoverName()+")");
			cell.setCompareOn(component.getRemoveDate());
		
			IconPane iconPane = gef.getIconPane();
			Icon restoreIcon = iconPane.addIcon();
			restoreIcon.setProperties(resource.getIcon("JSPP.restore"), resource.getString("JSPP.BinRestore"), "RestoreFromBin?ItemId="+component.getId());
			Icon deleteIcon = iconPane.addIcon();
			deleteIcon.setProperties(resource.getIcon("JSPP.delete"), resource.getString("JSPP.BinDelete"), "javaScript:onClick=removeItem('"+component.getId()+"')");
			line.addArrayCellText(restoreIcon.print()+"&nbsp;&nbsp;&nbsp;"+deleteIcon.print()+"&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"ItemIds\" value=\""+component.getId()+"\">");
		}
		out.println(arrayPane.print());
	}
	
	if (emptyBin)
	{
		out.println(board.printBefore());
		out.println("<center>"+resource.getString("JSPP.BinEmpty")+"</center>");
		out.println(board.printAfter());
	}
%>
</form>
</center>
<% 
out.println(frame.printAfter());
out.println(window.printAfter());
%>
</BODY>
</HTML>