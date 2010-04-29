var dNdVisible 	= false;
var dNdLoaded	= false;
function showHideDragDrop(targetURL1, message1, properties, targetURL2, message2, max_upload, pathJreInstaller, expandLabel, collapseLabel)
{
  var actionDND = document.getElementById("dNdActionLabel");
	
  if (dNdVisible)
  {
    //hide applet
    hideApplet('DragAndDrop');
    hideApplet('DragAndDropDraft');

    //change link's label
    actionDND.innerHTML = expandLabel;
  }
  else
  {
    actionDND.innerHTML = collapseLabel;
		
    if (dNdLoaded)
    {
      showApplet('DragAndDrop');
      showApplet('DragAndDropDraft');
    }
    else
    {
      try {
        loadApplet('DragAndDrop', targetURL1, message1, properties, max_upload, pathJreInstaller);
      } catch (e) {
      }
      try {
        loadApplet('DragAndDropDraft', targetURL2, message2, properties, max_upload, pathJreInstaller);
      } catch (e) {
      }
      dNdLoaded = true;
    }
  }
  dNdVisible = !dNdVisible;
}

function hideApplet(divId)
{
  try
  {
    var appletObj = document.getElementById("applet"+divId);
    appletObj.setAttribute("height", "1");
  } catch (e) {
  }
}

function showApplet(divId)
{
  try
  {
    var appletObj = document.getElementById("applet"+divId);
    appletObj.setAttribute("height", "70");
  } catch (e) {
  }
}

function loadApplet(divId, targetURL, message, properties, max_upload, pathJreInstaller)
{
  var divDND = document.getElementById(divId);
	
  var _info = navigator.userAgent;
  var ie = (_info.indexOf("MSIE") > 0);
  var win = (_info.indexOf("Win") > 0);

  var objectDND;
  if (win)
  {
    objectDND = document.createElement("object");

    if (!ie)
    {
      objectDND.setAttribute("id", "applet"+divId);
      objectDND.setAttribute("width", "100%");
      objectDND.setAttribute("height", "70");
      objectDND.setAttribute("type", "application/x-java-applet;version=1.4.1");
    }
    addParam(objectDND, "archive", "wjhk.jupload.jar,jakarta-commons-net.jar");
    addParam(objectDND, "code", "wjhk.jupload2.JUploadApplet");
    addParam(objectDND, "name", "Silverpeas Drag And Drop");
    addParam(objectDND, "codebase","/weblib/upload/");
  }
  else
  {
    /* mac and linux */
    objectDND = document.createElement("applet");
   		
    objectDND.setAttribute("id", "applet"+divId);
    objectDND.setAttribute("width", "100%");
    objectDND.setAttribute("height", "70");
    objectDND.setAttribute("codebase", "/weblib/upload/");
    objectDND.setAttribute("archive", "wjhk.jupload.jar,jakarta-commons-net.jar");
    objectDND.setAttribute("code", "wjhk.jupload2.JUploadApplet");
    objectDND.setAttribute("name", "Silverpeas Drag And Drop");
    objectDND.setAttribute("hspace", "0");
    objectDND.setAttribute("vspace", "0");
    objectDND.setAttribute("MAYSCRIPT", "yes");
    objectDND.setAttribute("align", "middle");
  }

  addParam(objectDND, "postURL", targetURL);
  addParam(objectDND, "message", message);
  addParam(objectDND, "uploadPolicy", "SilverpeasUploadPolicy");
  addParam(objectDND, "showLogWindow", "false");
  addParam(objectDND, "showStatusBar", "true");
  addParam(objectDND, "afterUploadURL", "javascript:uploadCompleted('%message%')");
  addParam(objectDND, "ftpCreateDirectoryStructure", "true");
  //addParam(objectDND, "max_file", "<%=maxFileSizeForApplet%>");

  divDND.appendChild(objectDND);
	   
  if (ie)
  {
    objectDND.setAttribute("id", "applet"+divId);
    objectDND.setAttribute("width", "100%");
    objectDND.setAttribute("height", "70");
    objectDND.setAttribute("classid", "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93");
    objectDND.setAttribute("codebase", pathJreInstaller);
  }
}

function addParam(object, paramName, paramValue)
{
  var param = document.createElement("param");
  param.setAttribute("name", paramName);
  param.setAttribute("value", paramValue);

  object.appendChild(param);
}