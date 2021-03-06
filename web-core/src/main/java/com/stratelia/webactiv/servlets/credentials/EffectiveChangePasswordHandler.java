/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stratelia.webactiv.servlets.credentials;

import com.stratelia.silverpeas.authentication.AuthenticationException;
import com.stratelia.silverpeas.authentication.LoginPasswordAuthentication;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.AdminException;
import com.stratelia.webactiv.beans.admin.UserDetail;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Navigation case : user has committed change password form.
 * @author ehugonnet
 */
public class EffectiveChangePasswordHandler extends FunctionHandler {

  private ForcePasswordChangeHandler forcePasswordChangeHandler = new ForcePasswordChangeHandler();

  @Override
  public String doAction(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    String key = (String) session.getAttribute("svplogin_Key");

    try {
      String userId = getAdmin().authenticate(key, session.getId(), false, false);
      UserDetail ud = getAdmin().getUserDetail(userId);
      String login = ud.getLogin();
      String domainId = ud.getDomainId();
      String oldPassword = request.getParameter("oldPassword");
      String newPassword = request.getParameter("newPassword");
      LoginPasswordAuthentication auth = new LoginPasswordAuthentication();
      auth.changePassword(login, oldPassword, newPassword, domainId);
      return "/LoginServlet";
    } catch (AdminException e) {
      SilverTrace.error("peasCore", "effectiveChangePasswordHandler.doAction()",
          "peasCore.EX_USER_KEY_NOT_FOUND", "key=" + key);
      return forcePasswordChangeHandler.doAction(request);
    } catch (AuthenticationException e) {
      SilverTrace.error("peasCore", "effectiveChangePasswordHandler.doAction()",
          "peasCore.EX_USER_KEY_NOT_FOUND", "key=" + key);
      request.setAttribute("message", getM_Multilang().getString("badCredentials"));
      return forcePasswordChangeHandler.doAction(request);
    }
  }
}
