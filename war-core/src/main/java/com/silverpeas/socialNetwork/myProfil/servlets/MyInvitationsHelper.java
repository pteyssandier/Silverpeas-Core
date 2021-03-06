package com.silverpeas.socialNetwork.myProfil.servlets;

import javax.servlet.http.HttpServletRequest;

import com.silverpeas.socialNetwork.myProfil.control.MyProfilSessionController;

public class MyInvitationsHelper {

  public void getAllInvitationsReceived(MyProfilSessionController mpsc, HttpServletRequest request) {
    request.setAttribute("Inbox", mpsc.getAllMyInvitationsReceived());
  }
  
  public void getAllInvitationsSent(MyProfilSessionController mpsc, HttpServletRequest request) {
    request.setAttribute("Outbox", mpsc.getAllMyInvitationsSent());
  }
}
