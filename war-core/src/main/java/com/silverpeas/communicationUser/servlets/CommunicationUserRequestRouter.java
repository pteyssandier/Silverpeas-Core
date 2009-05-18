/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package com.silverpeas.communicationUser.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.*;

import com.stratelia.silverpeas.peasCore.servlets.*;
import com.stratelia.silverpeas.peasCore.*;

import com.stratelia.silverpeas.silvertrace.*;
import com.silverpeas.communicationUser.control.*;


/**
 * Class declaration
 *
 *
 * @author
 */
public class CommunicationUserRequestRouter extends ComponentRequestRouter
{

    /**
     * Method declaration
     *
     *
     * @param mainSessionCtrl
     * @param componentContext
     *
     * @return
     *
     * @see
     */
    public ComponentSessionController createComponentSessionController(MainSessionController mainSessionCtrl, ComponentContext componentContext)
    {
        return new CommunicationUserSessionController(mainSessionCtrl, componentContext);
    }

    /**
     * This method has to be implemented in the component request rooter class.
     * returns the session control bean name to be put in the request object
     * ex : for communicationUser, returns "communicationUser"
     */
    public String getSessionControlBeanName()
    {
        return "communicationUser";
    }

    /**
     * This method has to be implemented by the component request rooter
     * it has to compute a destination page
     * @param function The entering request function (ex : "Main.jsp")
     * @param componentSC The component Session Control, build and initialised.
     * @param request The entering request. The request rooter need it to get parameters
     * @return The complete destination URL for a forward (ex : "/communicationUser/jsp/communicationUser.jsp?flag=user")
     */
    public String getDestination(String function, ComponentSessionController componentSC, HttpServletRequest request)
    {
		//remarques
		//tous les param�tres des la jsp sont transfer� par la request.
		//le UserPanel �tant unique par session, il est imp�ratif de r�cup�r�r les objets selectionn�s via userPanel et de transporter
		//les id des ses  de jsp en jsp en soumettant un formulaire.
		//En effet, la communication peut �tre utilis� "en m�me temps" qu' le client utiliser userPanelPeas. Cela m�lange les objets selectionn�e.

        String destination = "";
        CommunicationUserSessionController  commUserSC = (CommunicationUserSessionController)componentSC;
        SilverTrace.info("communicationUser", "CommunicationUserRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE", "function="+function);
				
		try{
			if (function.startsWith("Main"))
	        {	
                request.setAttribute("ConnectedUsersList", commUserSC.getDistinctConnectedUsersList());
                
				destination = "/communicationUser/jsp/connectedUsers.jsp";
	        }
			//CBO : REMOVE
            /*else if (function.equals("NotifyUser"))
            {
                request.setAttribute("userDetail", commUserSC.getTargetUserDetail(request.getParameter("theUserId")));
                request.setAttribute("action", "NotifyUser");
                destination = "/communicationUser/jsp/writeMessage.jsp";
            }
            else if (function.equals("ToAlert"))
            {
            	commUserSC.notifySession(request.getParameter("theUserId"), request.getParameter("messageAux"));
                request.setAttribute("action", "Close");
                destination = "/communicationUser/jsp/writeMessage.jsp";
            }*/
			//CBO : FIN REMOVE
			
			//CBO : ADD
            else if (function.startsWith("OpenDiscussion"))
            {
        		String userId = (String) request.getParameter("userId");
        		if(userId != null) {
            		File fileDiscussion = commUserSC.getExistingFileDiscussion(userId);
            		if(fileDiscussion == null) {
            			fileDiscussion = commUserSC.createDiscussion(userId);
            		} 
            		
            		commUserSC.addCurrentDiscussion(fileDiscussion);
            		request.setAttribute("UserName", commUserSC.getUserDetail().getDisplayedName());
        			request.setAttribute("UserIdDest", userId);
        			request.setAttribute("UserNameDest", commUserSC.getOrganizationController().getUserDetail(userId).getDisplayedName());
        			destination = "/communicationUser/jsp/discussion.jsp";
        		} 	
            }
            else if (function.startsWith("ExportDiscussion"))
            {
        		String userId = (String) request.getParameter("userId");
        		if(userId != null) {
        			Collection listCurrentDiscussion = commUserSC.getListCurrentDiscussion();
        			Iterator it = listCurrentDiscussion.iterator();
        			String currentUserId = commUserSC.getUserId();
        			File fileDiscussion = null;
        			String fileName, userId1, userId2;
        			boolean trouve = false;
        			while(it.hasNext() && !trouve) {
        				fileDiscussion = (File) it.next();
        				fileName = fileDiscussion.getName(); //userId1.userId2.txt
        				userId1 = fileName.substring(0, fileName.indexOf("."));
        				userId2 = fileName.substring(fileName.indexOf(".")+1, fileName.lastIndexOf("."));
        				if((userId.equals(userId1) && currentUserId.equals(userId2)) || 
        					(userId.equals(userId2) && currentUserId.equals(userId1))) {
        					trouve = true;
        				}
        			}
        			
        			if(!trouve) {
        				throw new IOException("Fichier de discussion non trouv� !!");
        			}
        			
    				request.setAttribute("FileDiscussion", fileDiscussion);
        			
        			destination = "/communicationUser/jsp/historyMessages.jsp";
        		} 	
            }
			//CBO : FIN ADD
		}
		catch (Exception e)
		{
			request.setAttribute("javax.servlet.jsp.jspException", e);
			destination = "/admin/jsp/errorpageMain.jsp";
		}
		SilverTrace.info("communicationUser", "CommunicationUserRequestRouter.getDestination()", "root.MSG_GEN_PARAM_VALUE", "destination="+destination);
		return destination;
    }
}