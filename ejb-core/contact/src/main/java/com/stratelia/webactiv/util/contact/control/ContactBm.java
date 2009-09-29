package com.stratelia.webactiv.util.contact.control;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.*;

import com.stratelia.webactiv.util.contact.model.*;

import com.stratelia.webactiv.util.node.model.NodePK;

public interface ContactBm extends EJBObject {

  /**
   * getDetail() get details on the contact specified by the primary key given
   * in pubPK parameter
   */
  public ContactDetail getDetail(ContactPK pubPK) throws RemoteException;

  /**
   * createContact() Create a new contact A new line will be added in contact
   * table. The "id" in "detail" is not used (a new one will be computed). The
   * "ed" is used to know the table name.
   */
  public ContactPK createContact(ContactDetail detail) throws RemoteException;

  /**
   * removeContact() remove the contact designed by pubPK parameter.
   */
  public void removeContact(ContactPK pubPK) throws RemoteException;

  /**
   * setDetail() update the contact content.
   */
  public void setDetail(ContactDetail detail) throws RemoteException;

  /**
   * addFather() add a new father (designed by "fatherPK") to a contact
   * ("pubPK") The contact will be visible from its new father node.
   */
  public void addFather(ContactPK pubPK, NodePK fatherPK)
      throws RemoteException;

  /**
   * removeFather() remove a father (designed by "fatherPK") from a contact
   * ("pubPK") The contact won't be visible from its old father node.
   */
  public void removeFather(ContactPK pubPK, NodePK fatherPK)
      throws RemoteException;

  /**
   * removeAllFather() remove all father from a contact ("pubPK") The contact
   * won't be visible.
   */
  public void removeAllFather(ContactPK pubPK) throws RemoteException;

  /**
   * removeAllIssue() remove all links between contacts and node N N is a
   * descendant of the node designed by originPK
   */
  public void removeAllIssue(NodePK originPK, ContactPK pubPK)
      throws RemoteException;

  /**
   * getOrphanContacts() return the Detail of contact which are not linked to a
   * father
   */
  public Collection getOrphanContacts(ContactPK pubPK) throws RemoteException;

  public void deleteOrphanContactsByCreatorId(ContactPK pubPK, String creatorId)
      throws RemoteException;

  public Collection getUnavailableContactsByPublisherId(ContactPK pubPK,
      String publisherId, String nodeId) throws RemoteException;

  /**
   * getAllFatherPK() return a collection, containing all node primary key from
   * where the contact is visible
   */
  public Collection getAllFatherPK(ContactPK pubPK) throws RemoteException;

  /**
   * getDetailsByFatherPK() return a ContactDetail collection of all contact
   * visible from the node identified by "fatherPK" parameter
   */
  public Collection getDetailsByFatherPK(NodePK fatherPK)
      throws RemoteException;

  public Collection getDetailsByLastName(ContactPK pk, String query)
      throws RemoteException;

  public Collection getDetailsByLastNameOrFirstName(ContactPK pk, String query)
      throws RemoteException;

  public Collection getDetailsByLastNameAndFirstName(ContactPK pk,
      String lastName, String firstName) throws RemoteException;

  /**
   *
   *
   *
   */
  public void createInfoModel(ContactPK pubPK, String modelId)
      throws RemoteException;

  /**
   *
   *
   *
   */
  public CompleteContact getCompleteContact(ContactPK pubPK, String modelId)
      throws RemoteException;

  /**
   *
   *
   *
   */
  public Collection getContacts(Collection contactPKs) throws RemoteException;

  /**
   *
   *
   *
   */

  public int getNbPubInFatherPKs(Collection fatherPKs) throws RemoteException;

  public Collection getDetailsByFatherPKs(Collection fatherPKs,
      ContactPK pubPK, NodePK nodePK) throws RemoteException;

  public int getNbPubByFatherPath(NodePK fatherPK, String fatherPath)
      throws RemoteException;

}