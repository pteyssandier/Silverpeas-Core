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
 * FLOSS exception.  You should have recieved a copy of the text describing
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
package com.silverpeas.external.filesharing.model;

import java.rmi.RemoteException;
import java.util.List;


public interface FileSharingInterface {

	public List<TicketDetail> getTicketsByUser(String userId) throws RemoteException;

	public void deleteTicketsByFile(String fileId, boolean versioning) throws RemoteException;

	public TicketDetail getTicket(String key) throws RemoteException;

	public String createTicket(TicketDetail ticket);

	/**
	 * mise a jour des telechargements
	 * @param download
	 */
	public void addDownload(DownloadDetail download);

	public void updateTicket(TicketDetail ticket);

	public void deleteTicket(String key);


}

