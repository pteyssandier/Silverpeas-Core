package com.stratelia.silverpeas.versioningPeas.servlets;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.silverpeas.form.DataRecord;
import com.silverpeas.form.Form;
import com.silverpeas.form.PagesContext;
import com.silverpeas.form.RecordSet;
import com.silverpeas.publicationTemplate.PublicationTemplate;
import com.silverpeas.publicationTemplate.PublicationTemplateImpl;
import com.silverpeas.publicationTemplate.PublicationTemplateManager;
import com.silverpeas.util.FileUtil;
import com.silverpeas.util.ForeignPK;
import com.silverpeas.util.StringUtil;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.ComponentSessionController;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.servlets.ComponentRequestRouter;
import com.stratelia.silverpeas.selection.Selection;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.silverpeas.versioning.ejb.RepositoryHelper;
import com.stratelia.silverpeas.versioning.model.Document;
import com.stratelia.silverpeas.versioning.model.DocumentPK;
import com.stratelia.silverpeas.versioning.model.DocumentVersion;
import com.stratelia.silverpeas.versioning.model.DocumentVersionPK;
import com.stratelia.silverpeas.versioning.model.Worker;
import com.stratelia.silverpeas.versioningPeas.control.VersioningSessionController;
import com.stratelia.webactiv.beans.admin.Group;
import com.stratelia.webactiv.beans.admin.ProfileInst;
import com.stratelia.webactiv.util.FileRepositoryManager;
import com.stratelia.webactiv.util.ResourceLocator;
import com.stratelia.webactiv.util.publication.model.PublicationPK;
import java.util.StringTokenizer;

public class VersioningRequestRouter extends ComponentRequestRouter {
  public ComponentSessionController createComponentSessionController(
      MainSessionController mainSessionCtrl, ComponentContext componentContext) {
    return new VersioningSessionController(mainSessionCtrl, componentContext);
  }

  public String getSessionControlBeanName() {
    return "versioningPeas";
  }

  public String getDestination(String function,
      ComponentSessionController componentSC, HttpServletRequest request) {
    String destination = "";
    VersioningSessionController versioningSC = (VersioningSessionController) componentSC;
    SilverTrace.info("versioningPeas",
        "VersioningRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE",
        "User=" + versioningSC.getUserId() + " Function=" + function);
    String rootDestination = "/versioningPeas/jsp/";
    ResourceLocator messages = new ResourceLocator(
        "com.stratelia.silverpeas.versioningPeas.multilang.versioning",
        versioningSC.getLanguage());
    try {
      String flag = versioningSC.getProfile();

      request.setAttribute("Profile", flag);
      if (function.startsWith("ViewReadersList")) {
        List<Group> groups = new ArrayList<Group>();
        List<String> users = new ArrayList<String>();
        ProfileInst profile = versioningSC
            .getCurrentProfile(VersioningSessionController.READER);
        if (profile != null) {
          groups = versioningSC.groupIds2Groups(profile.getAllGroups());
          users = versioningSC.userIds2Users(profile.getAllUsers());
        }
        request.setAttribute("Groups", groups);
        request.setAttribute("Users", users);
        destination = rootDestination + "ReadList.jsp";
      } else if (function.startsWith("ViewWritersList")) {
        Document document = versioningSC.getEditingDocument();

        ProfileInst profile = versioningSC
            .getCurrentProfile(VersioningSessionController.WRITER);
        ArrayList<Worker> workers = new ArrayList<Worker>();
        if (profile != null) {
          workers = document.getWorkList();
          if (new Integer(document.getCurrentWorkListOrder()).toString()
              .equals(VersioningSessionController.WRITERS_LIST_ORDERED)
              && !versioningSC.isAlreadyMerged()
              && !profile.getAllGroups().isEmpty()) {
            // Need to merge users from groups with other users
            workers = versioningSC.mergeUsersFromGroupsWithWorkers(profile
                .getAllGroups(), workers);
          }
        }
        request.setAttribute("Workers", workers);
        destination = rootDestination + "WorkList.jsp";
      } else if (function.startsWith("ChangeOrder")) {
        String lines = request.getParameter("lines");
        Document document = versioningSC.getEditingDocument();
        ArrayList users = document.getWorkList();
        if (lines != null) {
          int users_count = (new Integer(lines)).intValue();
          if (users_count == users.size()) {
            ArrayList new_users = new ArrayList(users_count);

            for (int i = 0; i < users_count; i++) {
              Worker user = (Worker) users.get(i);
              boolean v_value = false;

              // Validator
              String chvi = request.getParameter("chv" + i);
              if (chvi != null)
                v_value = true;
              user.setApproval(v_value);
              new_users.add(user);
            }

            // Sorting begin
            int upIndex = Integer.parseInt((String) request.getParameter("up"));
            int downIndex = Integer.parseInt((String) request
                .getParameter("down"));
            int addIndex = Integer.parseInt(request.getParameter("add"));

            // Remove user to change order
            if (upIndex > 0 && upIndex < users_count) {
              Worker user = (Worker) new_users.remove(upIndex);
              new_users.add(upIndex - 1, user);
            }
            if (downIndex >= 0 && downIndex < users_count - 1) {
              Worker user = (Worker) new_users.remove(downIndex);
              new_users.add(downIndex + 1, user);
            }

            if (addIndex >= 0 && addIndex < users_count) {
              Worker user = (Worker) new_users.get(addIndex);
              Worker new_user = new Worker(user.getUserId(), Integer
                  .parseInt(versioningSC.getEditingDocument().getPk().getId()),
                  0, user.isApproval(), true, versioningSC.getComponentId(),
                  user.getType(), user.isSaved(), user.isUsed(), user
                      .getListType());
              new_users.add(addIndex + 1, new_user);
              users_count++;
            }

            for (int i = 0; i < users_count; i++) {
              ((Worker) new_users.get(i)).setOrder(i);
            }
            document.setWorkList(new_users);
          }
        }
        destination = getDestination("ViewWritersList", versioningSC, request);
      } else if (function.startsWith("ChangeListType")) {
        Document document = versioningSC.getEditingDocument();
        String listType = request.getParameter("ListType");
        if (!StringUtil.isDefined(listType))
          listType = VersioningSessionController.WRITERS_LIST_SIMPLE;
        document.setCurrentWorkListOrder(new Integer(listType).intValue());
        ProfileInst profile = versioningSC
            .getProfile(VersioningSessionController.WRITER);
        ArrayList workers = new ArrayList();
        if (profile != null) {
          if (listType.equals(VersioningSessionController.WRITERS_LIST_ORDERED)) {
            // Need to merge users from groups with other users
            workers = document.getWorkList();
            workers = versioningSC.mergeUsersFromGroupsWithWorkers(profile
                .getAllGroups(), workers);
            versioningSC.setAlreadyMerged(true);
          } else {
            ArrayList workersUsers = new ArrayList();
            ArrayList workersGroups = new ArrayList();

            workersGroups = versioningSC.convertGroupsToWorkers(workers,
                profile.getAllGroups());
            workers.addAll(workersGroups);

            workersUsers = versioningSC.convertUsersToWorkers(workers, profile
                .getAllUsers());
            workers.addAll(workersUsers);
          }
        }
        document.setWorkList(workers);
        versioningSC.updateWorkList(document);
        versioningSC.updateDocument(document);
        destination = getDestination("ViewWritersList", versioningSC, request);
      } else if (function.startsWith("SaveListType")) {
        Document document = versioningSC.getEditingDocument();
        ArrayList users = document.getWorkList();
        ArrayList updateUsers = new ArrayList();
        for (int i = 0; i < users.size(); i++) {
          Worker user = (Worker) users.get(i);
          // Set approval rights to users
          String chvi = request.getParameter("chv" + i);
          boolean v_value = false;
          if (chvi != null)
            v_value = true;
          user.setApproval(v_value);
          updateUsers.add(user);
        }
        versioningSC.updateWorkList(document);
        versioningSC.updateDocument(document);
        destination = getDestination("ViewWritersList", versioningSC, request);
      } else if (function.startsWith("ViewVersions")) {
        destination = rootDestination + "versions.jsp";
      } else if (function.equals("SelectUsersGroupsProfileInstance")) {
        String role = (String) request.getParameter("Role");
        String listType = (String) request.getParameter("ListType");
        if (StringUtil.isDefined(listType))
          versioningSC.getEditingDocument().setCurrentWorkListOrder(
              new Integer(listType).intValue());
        versioningSC.initUserPanelInstanceForGroupsUsers(role);
        destination = Selection.getSelectionURL(Selection.TYPE_USERS_GROUPS);
      } else if (function.startsWith("DocumentProfileSetUsersAndGroups")) {
        String role = (String) request.getParameter("Role");
        ProfileInst profile = versioningSC.getProfile(role);
        versioningSC.updateDocumentProfile(profile);
        if (role.equals(VersioningSessionController.WRITER)) {

          ArrayList oldWorkers = versioningSC.getEditingDocument()
              .getWorkList();
          ArrayList workers = new ArrayList();
          ArrayList workersUsers = new ArrayList();
          ArrayList workersGroups = new ArrayList();

          workersGroups = versioningSC.convertGroupsToWorkers(oldWorkers,
              profile.getAllGroups());
          workers.addAll(workersGroups);

          workersUsers = versioningSC.convertUsersToWorkers(oldWorkers, profile
              .getAllUsers());
          workers.addAll(workersUsers);
          ArrayList sortedWorkers = new ArrayList();
          if (workers != null) {
            for (int i = 0; i < workers.size(); i++) {
              Worker sortedWorker = ((Worker) workers.get(i));
              sortedWorker.setOrder(i);
              sortedWorkers.add(sortedWorker);
            }
            workers = sortedWorkers;
          }

          versioningSC.getEditingDocument().setWorkList(workers);
          versioningSC.updateWorkList(versioningSC.getEditingDocument());
          request.setAttribute("urlToReload", "ViewWritersList");
        } else {
          request.setAttribute("urlToReload", "ViewReadersList");
        }
        destination = rootDestination + "closeWindow.jsp";
      } else if (function.startsWith("SaveList")) {
        String role = (String) request.getParameter("Role");
        String fromFunction = (String) request.getParameter("From");
        if (versioningSC.isAccessListExist(role))
          versioningSC.removeAccessList(role);
        versioningSC.saveAccessList(role);
        request.setAttribute("Message", messages.getString(
            "versioning.ListSaved", ""));
        destination = getDestination(fromFunction, versioningSC, request);
      } else if (function.startsWith("DeleteReaderProfile")) {
        ProfileInst profile = versioningSC
            .getDocumentProfile(VersioningSessionController.READER);
        if (profile != null) {
          profile.removeAllGroups();
          profile.removeAllUsers();
          versioningSC.updateProfileInst(profile);
        }
        destination = getDestination("ViewReadersList", versioningSC, request);
      } else if (function.startsWith("DeleteWriterProfile")) {
        ProfileInst profile = versioningSC
            .getDocumentProfile(VersioningSessionController.WRITER);
        if (profile != null) {
          profile.removeAllGroups();
          profile.removeAllUsers();
          versioningSC.updateProfileInst(profile);
        }
        versioningSC.deleteWorkers(true);
        versioningSC.setAlreadyMerged(false);
        destination = getDestination("ViewWritersList", versioningSC, request);
      } else if (function.startsWith("Update")) {
        String docId = request.getParameter("DocId");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String comments = request.getParameter("comments");
        Document document = versioningSC.getDocument(new DocumentPK(
            new Integer(docId).intValue(), versioningSC.getComponentId()));
        document.setDescription(description);
        document.setName(name);
        document.setAdditionalInfo(comments);
        versioningSC.updateDocument(document);
        versioningSC.setEditingDocument(document);
        destination = getDestination("ViewVersions", versioningSC, request);
      } else if (function.equals("CloseWindow")) {
        destination = rootDestination + "closeWindow.jsp";
      } else if (function.equals("AddNewVersion")) {
    	  
    	  //Display xmlForm if used
    	  if (StringUtil.isDefined(versioningSC.getXmlForm()))
    		  setXMLFormIntoRequest(request.getParameter("documentId"), versioningSC, request);
    	  
        destination = rootDestination + "newVersion.jsp";
      } 
      else if (function.equals("AddNewOnlineVersion")) 
      {
    	  //Display xmlForm if used
    	  if (StringUtil.isDefined(versioningSC.getXmlForm()))
    		  setXMLFormIntoRequest(request.getParameter("documentId"), versioningSC, request);
    	  
        destination = rootDestination + "newOnlineVersion.jsp";
      }else if (function.equals("ChangeValidator")) {
        String setTypeId = request.getParameter("VV");
        String setType = request.getParameter("SetType"); // 'U'for users or 'G'
                                                          // for groups
        versioningSC.setWorkerValidator(versioningSC.getEditingDocument()
            .getWorkList(), new Integer(setTypeId).intValue(), setType);
        destination = getDestination("ViewWritersList", versioningSC, request);
      } else if (function.equals("ListPublicVersionsOfDocument")) {
        String documentId = request.getParameter("DocId");
        String isAlias = request.getParameter("Alias");
        String componentId = request.getParameter("ComponentId");
        versioningSC.setComponentId(componentId);
        DocumentPK documentPK = new DocumentPK(Integer.parseInt(documentId),
            versioningSC.getSpaceId(), componentId);

        Document document = versioningSC.getDocument(documentPK);
        List publicVersions = versioningSC
            .getPublicDocumentVersions(documentPK);

        request.setAttribute("Document", document);
        request.setAttribute("PublicVersions", publicVersions);
        request.setAttribute("Alias", isAlias);
        destination = "/versioningPeas/jsp/publicVersions.jsp";
      } else if ("saveOnline".equals(function)) {
    	  List items = getRequestItems(request);
    	  
        String documentId = getParameterValue(items, "documentId");
        DocumentPK documentPK = new DocumentPK(Integer.parseInt(documentId),
            versioningSC.getSpaceId(), versioningSC.getComponentId());
        Document document = versioningSC.getDocument(documentPK);
        String userId = componentSC.getUserId();
        String radio = getParameterValue(items, "radio");
        String comments = getParameterValue(items, "comments");
        boolean force = "true".equalsIgnoreCase(request.getParameter("force_release"));
        destination = "/versioningPeas/jsp/documentSaved.jsp";
        
        boolean addXmlForm = !isXMLFormEmpty(versioningSC, items);
        
        DocumentVersionPK newVersionPK = saveOnline(document, versioningSC, comments, radio, Integer.parseInt(userId), force, addXmlForm);
        if (newVersionPK != null)
        {
        	if(addXmlForm) {
              saveXMLData(versioningSC, newVersionPK, items);
        	}
        }
        else {
          if("admin".equals(componentSC.getUserRoleLevel())) {
            //TODO MANU ecrire la page pour ressoumettre en forcant
            destination = "/versioningPeas/jsp/forceDocumentLocked.jsp";
          }else {
            destination = "/versioningPeas/jsp/documentLocked.jsp";
          }
        }
      } else if (function.equals("Checkout")) {
        String documentId = request.getParameter("DocId");
        DocumentPK documentPK = new DocumentPK(Integer.parseInt(documentId),
            versioningSC.getSpaceId(), versioningSC.getComponentId());
        Document document = versioningSC.getDocument(documentPK);
        document.setStatus(1);
        document.setLastCheckOutDate(new Date());
        versioningSC.checkDocumentOut(documentPK, new Integer(versioningSC
            .getUserId()).intValue(), new Date());
        document = versioningSC.getDocument(documentPK);
        versioningSC.setEditingDocument(document);
        request.setAttribute("DocId", documentId);
        destination = rootDestination + "versions.jsp";
      } else if (function.equals("DeleteDocumentRequest")) {
        String documentId = request.getParameter("DocId");
        String url = request.getParameter("Url");
        request.setAttribute("DocId", documentId);
        request.setAttribute("Url", url);
        destination = rootDestination + "deleteDocument.jsp";
      } else if (function.equals("DeleteDocument")) {
        String documentId = request.getParameter("DocId");
        String url = request.getParameter("Url");
        DocumentPK documentPK = new DocumentPK(Integer.parseInt(documentId),
            versioningSC.getSpaceId(), versioningSC.getComponentId());
        versioningSC.deleteDocument(documentPK);
        SilverTrace.info("versioningPeas",
            "VersioningRequestRouter.getDestination()",
            "root.MSG_GEN_PARAM_VALUE", "url=" + url);
        request.setAttribute("urlToReload", url);
        destination = rootDestination + "closeWindow.jsp";
      } else if (function.equals("AddNewDocument")) {
        String pubId = request.getParameter("PubId");
        request.setAttribute("PubId", pubId);
        
        if (StringUtil.isDefined(versioningSC.getXmlForm()))
        	setXMLFormIntoRequest(null, versioningSC, request);
        
        destination = rootDestination + "newDocument.jsp";
      } else if (function.equals("SaveNewDocument")) {
        saveNewDocument(request, versioningSC);
        destination = getDestination("ViewVersions", versioningSC, request);
      }
      else if (function.equals("SaveNewVersion"))
      {
    	  List items = getRequestItems(request);
            
          String name = getParameterValue(items, "name");
          String description = getParameterValue(items, "description");
          String type = getParameterValue(items, "type");
    	  String comments = getParameterValue(items, "comments");
    	  String radio = getParameterValue(items, "radio");
    	  String publicationId = getParameterValue(items, "publicationId");
    	  String versionType = getParameterValue(items, "versionType");
    	  String documentId = getParameterValue(items, "documentId");

    	  //Save file on disk
    	  FileItem fileItem = getUploadedFile(items, "file_upload");
    	  ResourceLocator settings = new ResourceLocator("com.stratelia.webactiv.util.attachment.Attachment", "");
		    boolean runOnUnix = settings.getBoolean("runOnSolaris", false);
		    String logicalName = fileItem.getName();
		    String physicalName = "dummy";
		    String mimeType = "dummy";
		    File dir = null;
		    int size = 0;
		    if (logicalName != null) {

		      if (runOnUnix)
		        logicalName = logicalName.replace('\\', File.separatorChar);

		      logicalName = logicalName.substring(logicalName
		          .lastIndexOf(File.separator) + 1, logicalName.length());
		      type = logicalName.substring(logicalName.lastIndexOf(".") + 1,
		          logicalName.length());
		      physicalName = new Long(new Date().getTime()).toString() + "." + type;
		      mimeType = FileUtil.getMimeType(logicalName);
		      if (!StringUtil.isDefined(mimeType))
		        mimeType = "unknown";
		      dir = new File(versioningSC.createPath(versioningSC.getComponentId(),
		          null)
		          + physicalName);
		      size = new Long(fileItem.getSize()).intValue();
		      fileItem.write(dir);
		    }
		    
		    //create DocumentVersion
		    String componentId = versioningSC.getComponentId();
            ForeignPK pubPK = new ForeignPK(publicationId, componentId);
            DocumentPK docPK = new DocumentPK(Integer.parseInt(documentId), "useless", componentId );
            int userId = Integer.parseInt(versioningSC.getUserId());
            
            DocumentVersion documentVersion = null;
            DocumentVersion lastVersion = versioningSC.getLastVersion(docPK);
            if(com.stratelia.silverpeas.versioning.ejb.RepositoryHelper.getJcrDocumentService().isNodeLocked(lastVersion)) {
              destination = rootDestination+"documentLocked.jsp";
            }else {

              List versions = versioningSC.getDocumentVersions(docPK);
              int majorNumber = 0;
              int minorNumber = 1;
              if ( versions != null && versions.size() > 0 )
              {
                 documentVersion =  (DocumentVersion) versions.get( versions.size()-1 );                 
                 majorNumber = documentVersion.getMajorNumber();
                 minorNumber = documentVersion.getMinorNumber();
                 Document currdoc = versioningSC.getEditingDocument();
                 DocumentVersion newVersion = new DocumentVersion( null, docPK, majorNumber, minorNumber, userId, new Date(),
                		 					comments, Integer.parseInt(radio), documentVersion.getStatus(),
							                physicalName, logicalName, mimeType, size, componentId);
                 
                 boolean addXmlForm = !isXMLFormEmpty(versioningSC, items);
                 if (addXmlForm)
                	 newVersion.setXmlForm(versioningSC.getXmlForm());

                 newVersion = versioningSC.addNewDocumentVersion(newVersion);
                 
                 boolean actifyPublisherEnable = settings.getBoolean("ActifyPublisherEnable", false);
				//Specific case: 3d file to convert by Actify Publisher
				if (actifyPublisherEnable)
				{
					String extensions = settings.getString("Actify3dFiles");
					StringTokenizer tokenizer = new StringTokenizer(extensions, ",");
					//3d native file ?
					boolean fileForActify = false;
					SilverTrace.info("versioningPeas", "saveFile.jsp", "root.MSG_GEN_PARAM_VALUE", "nb tokenizer ="+tokenizer.countTokens());
					while (tokenizer.hasMoreTokens() && !fileForActify)
					{
						String extension =  tokenizer.nextToken();
						if (type.equalsIgnoreCase(extension))
							fileForActify = true;
					}
					if (fileForActify)
					{
						String dirDestName 			= "v_" + componentId + "_" + documentId;
						String actifyWorkingPath 	= settings.getString("ActifyPathSource") + File.separator + dirDestName;
		
						String destPath = FileRepositoryManager.getTemporaryPath() + actifyWorkingPath; 
						if (!new File(destPath).exists())
							FileRepositoryManager.createGlobalTempPath(actifyWorkingPath);
		
						String destFile			= FileRepositoryManager.getTemporaryPath() + actifyWorkingPath + File.separator + logicalName;
						FileRepositoryManager.copyFile(versioningSC.createPath(componentId, null) + File.separator + physicalName, destFile);
					}
				}
				if (addXmlForm)
					saveXMLData(versioningSC, newVersion.getPk(), items);
              }
              
             destination = getDestination("ViewVersions", componentSC, request); 
            }
      }
      else {
        destination = rootDestination + function;
      }
    } catch (Exception e) {
      SilverTrace.error("versioning", "VersioningRequestRouter.getDestination",
          "root.EX_CANT_GET_REQUEST_DESTINATION", e);
      request.setAttribute("javax.servlet.jsp.jspException", e);
      destination = "/admin/jsp/errorpageMain.jsp";
    }
    SilverTrace.info("versioningPeas",
        "VersioningRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE",
        "Destination=" + destination);
    return destination;
  }
  
  protected DocumentVersionPK saveOnline(Document document,
      VersioningSessionController versioningSC, String comments, String radio,
      int userId, boolean force, boolean addXmlForm) throws RemoteException {
    DocumentVersion lastVersion = versioningSC.getLastVersion(document.getPk());
    if(!force && RepositoryHelper.getJcrDocumentService().isNodeLocked(lastVersion)) {
      return null;
    }
    String physicalName = new Long(new Date().getTime()).toString()
        + "."
        + lastVersion.getLogicalName().substring(
            lastVersion.getLogicalName().indexOf(".") + 1,
            lastVersion.getLogicalName().length());
    DocumentVersion newVersion = new DocumentVersion(null, document.getPk(),
        lastVersion.getMajorNumber(), lastVersion.getMinorNumber(), userId,
        new Date(), comments, Integer.parseInt(radio), lastVersion.getStatus(),
        physicalName, lastVersion.getLogicalName(), lastVersion.getMimeType(),
        lastVersion.getSize(), lastVersion.getInstanceId());
    RepositoryHelper.getJcrDocumentService().getUpdatedDocument(newVersion);
    if (addXmlForm)
    	newVersion.setXmlForm(versioningSC.getXmlForm());
    newVersion = versioningSC.addNewDocumentVersion(newVersion);
    versioningSC.checkDocumentIn(document.getPk(), userId);
    return newVersion.getPk();
  }

  /**
   * 
   * @param document
   * @param versioningSC
   * @param comments
   * @param radio
   * @param userId
   * @throws RemoteException
   */
  protected DocumentVersionPK saveOnline(Document document,
      VersioningSessionController versioningSC, String comments, String radio,
      int userId, boolean addXmlForm) throws RemoteException {
    return saveOnline(document, versioningSC, comments, radio, userId, false, addXmlForm);
  }

  /**
   * Process request form
   * 
   * @param request
   * @param versioningSC
   * @return
   * @throws IOException
   */
  private void saveNewDocument(HttpServletRequest request,
      VersioningSessionController versioningSC) throws FileUploadException,
      Exception, RemoteException {
    SilverTrace.debug("versioningPeas",
        "VersioningRequestRooter.saveNewDocument()",
        "root.MSG_GEN_ENTER_METHOD");
    int majorNumber = 0;
    int minorNumber = 1;
    String type = "dummy";
    String physicalName = "dummy";
    String logicalName = "dummy";
    String mimeType = "dummy";
    File dir = null;
    int size = 0;
    DocumentPK docPK = new DocumentPK(-1, versioningSC.getComponentId());

    List items = getRequestItems(request);
    String comments = getParameterValue(items, "comments");
    int versionType = new Integer(getParameterValue(items, "versionType"))
        .intValue();

    FileItem fileItem = getUploadedFile(items, "file_upload");
    ResourceLocator settings = new ResourceLocator(
        "com.stratelia.webactiv.util.attachment.Attachment", "");
    boolean runOnUnix = settings.getBoolean("runOnSolaris", false);
    logicalName = fileItem.getName();
    if (logicalName != null) {

      if (runOnUnix)
        logicalName = logicalName.replace('\\', File.separatorChar);

      logicalName = logicalName.substring(logicalName
          .lastIndexOf(File.separator) + 1, logicalName.length());
      type = logicalName.substring(logicalName.lastIndexOf(".") + 1,
          logicalName.length());
      physicalName = new Long(new Date().getTime()).toString() + "." + type;
      mimeType = FileUtil.getMimeType(logicalName);
      if (!StringUtil.isDefined(mimeType))
        mimeType = "unknown";
      dir = new File(versioningSC.createPath(versioningSC.getComponentId(),
          null)
          + physicalName);
      size = new Long(fileItem.getSize()).intValue();
      fileItem.write(dir);
    }

    if (versionType == VersioningSessionController.PUBLIC_VERSION) {
      majorNumber = 1;
      minorNumber = 0;
    }
    if (size == 0) {
      majorNumber = 0;
      minorNumber = 0;
    }
    DocumentVersion documentVersion = new DocumentVersion(null, docPK,
        majorNumber, minorNumber, new Integer(versioningSC.getUserId())
            .intValue(), new Date(), comments, versionType, 0, physicalName,
        logicalName, mimeType, size, versioningSC.getComponentId());
    
    boolean addXmlForm = !isXMLFormEmpty(versioningSC, items);
    if (addXmlForm)
    	documentVersion.setXmlForm(versioningSC.getXmlForm());

    // Document
    docPK = new DocumentPK(-1, versioningSC.getComponentId());

    String name = getParameterValue(items, "name");
    String publicationId = getParameterValue(items, "publicationId");
    String description = getParameterValue(items, "description");

    PublicationPK pubPK = new PublicationPK(publicationId, versioningSC
        .getComponentId());
    Document document = new Document(docPK, pubPK, name, description, 0,
        new Integer(versioningSC.getUserId()).intValue(), new Date(), comments,
        versioningSC.getComponentId(), null, null, 0, new Integer(
            VersioningSessionController.WRITERS_LIST_SIMPLE).intValue());

    String docId = versioningSC.createDocument(document, documentVersion)
        .getId();
    
    if (addXmlForm)
    {
    	//Save additional informations
    	saveXMLData(versioningSC, documentVersion.getPk(), items);
    }
    
    versioningSC.setEditingDocument(document);
    versioningSC.setFileRights();
    versioningSC.updateWorkList(document);
    versioningSC.updateDocument(document);

    // Specific case: 3d file to convert by Actify Publisher
    ResourceLocator attachmentSettings = new ResourceLocator(
        "com.stratelia.webactiv.util.attachment.Attachment", "");
    boolean actifyPublisherEnable = attachmentSettings.getBoolean(
        "ActifyPublisherEnable", false);
    if (actifyPublisherEnable)
      versioningSC
          .saveFileForActify(docId, documentVersion, attachmentSettings);

    SilverTrace
        .debug("versioningPeas", "VersioningRequestRooter.saveNewDocument()",
            "root.MSG_GEN_EXIT_METHOD");
  }

  private List getRequestItems(HttpServletRequest request)
      throws FileUploadException {
    DiskFileUpload dfu = new DiskFileUpload();
    List items = dfu.parseRequest(request);
    return items;
  }

  private String getParameterValue(List items, String parameterName) {
    Iterator iter = items.iterator();
    while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();
      if (item.isFormField() && parameterName.equals(item.getFieldName())) {
        return item.getString();
      }
    }
    return null;
  }

  private FileItem getUploadedFile(List items, String parameterName) {
    Iterator iter = items.iterator();
    while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();
      if (!item.isFormField() && parameterName.equals(item.getFieldName())) {
        return item;
      }
    }
    return null;
  }
  
  private void saveXMLData(VersioningSessionController versioningSC, DocumentVersionPK newVersionPK, List items) throws Exception
  {
	  	String xmlFormName = versioningSC.getXmlForm();
	  	if (StringUtil.isDefined(xmlFormName) && newVersionPK != null)
	  	{
			String xmlFormShortName = xmlFormName.substring(xmlFormName.indexOf("/")+1, xmlFormName.indexOf("."));
			String objectId 		= newVersionPK.getId();
			String objectType 		= "Versioning";
			
			String externalId = versioningSC.getComponentId()+":"+objectType+":"+xmlFormShortName; 
			
			//register xmlForm to object
			PublicationTemplateManager.addDynamicPublicationTemplate(externalId, xmlFormName);
			
			PublicationTemplate pub = PublicationTemplateManager.getPublicationTemplate(externalId);
			
			RecordSet 	set 	= pub.getRecordSet();			
			Form 		form 	= pub.getUpdateForm();
						
			DataRecord data = set.getRecord(objectId);
			if (data == null) {
				data = set.getEmptyRecord();
				data.setId(objectId);
			}
			
			PagesContext context = new PagesContext("myForm", "3", versioningSC.getLanguage(), false, versioningSC.getComponentId(), versioningSC.getUserId());
			context.setObjectId(objectId);
			
			form.update(items, data, context);
			set.save(data);	
	  	}
  }
  
  private boolean isXMLFormEmpty(VersioningSessionController versioningSC, List items) throws Exception
  {
	  boolean isEmpty = true;
	  	String xmlFormName = versioningSC.getXmlForm();
	  	if (StringUtil.isDefined(xmlFormName))
	  	{
			String xmlFormShortName = xmlFormName.substring(xmlFormName.indexOf("/")+1, xmlFormName.indexOf("."));
			String objectId 		= "unknown";
			String objectType 		= "Versioning";
			
			String externalId = versioningSC.getComponentId()+":"+objectType+":"+xmlFormShortName; 
			
			//register xmlForm to object
			PublicationTemplateManager.addDynamicPublicationTemplate(externalId, xmlFormName);
			
			PublicationTemplate pub = PublicationTemplateManager.getPublicationTemplate(externalId);
			
			RecordSet 	set 	= pub.getRecordSet();			
			Form 		form 	= pub.getUpdateForm();
						
			DataRecord data = set.getRecord(objectId);
			if (data == null) {
				data = set.getEmptyRecord();
				data.setId(objectId);
			}
			
			PagesContext context = new PagesContext("myForm", "3", versioningSC.getLanguage(), false, versioningSC.getComponentId(), versioningSC.getUserId());
			context.setObjectId(objectId);
			
			isEmpty = form.isEmpty(items, data, context);
	  	}
	  	return isEmpty;
  }
  
  private void setXMLFormIntoRequest(String documentId, VersioningSessionController versioningSC, HttpServletRequest request) throws Exception
  {
	  String componentId 	= versioningSC.getComponentId();
	  String objectId		= request.getParameter("ObjectId");
	  String objectType		= "Versioning";
	  String xmlFormName	= versioningSC.getXmlForm();
					
	  String xmlFormShortName = xmlFormName.substring(xmlFormName.indexOf("/")+1, xmlFormName.indexOf("."));
	
	  //register xmlForm to object
	  PublicationTemplateManager.addDynamicPublicationTemplate(componentId+":"+objectType+":"+xmlFormShortName, xmlFormName);
					
	  PublicationTemplateImpl pubTemplate = (PublicationTemplateImpl) PublicationTemplateManager.getPublicationTemplate(componentId+":"+objectType+":"+xmlFormShortName, xmlFormName);
	  Form formUpdate = pubTemplate.getUpdateForm();
	  RecordSet recordSet = pubTemplate.getRecordSet();

	  if (StringUtil.isDefined(documentId))
	  {
		  //Get last version to display its additional informations instead of blank fields
		  DocumentVersion lastVersion = versioningSC.getLastVersion(new DocumentPK(Integer.parseInt(documentId), versioningSC.getComponentId()));
		  if (lastVersion != null)
			  objectId = lastVersion.getPk().getId();
	  }
		  
	  DataRecord data = recordSet.getRecord(objectId);
	  if (data == null) {
		  data = recordSet.getEmptyRecord();
		  data.setId(objectId);
	  }
	
	  PagesContext pageContext = new PagesContext("toDefine", "toDefine", versioningSC.getLanguage(), false, componentId, versioningSC.getUserId());
	  pageContext.setObjectId(objectId);
	
	  request.setAttribute("XMLForm", formUpdate);
	  request.setAttribute("XMLData", data);
	  request.setAttribute("XMLFormName", xmlFormName);
	  request.setAttribute("PagesContext", pageContext);
  }
}
