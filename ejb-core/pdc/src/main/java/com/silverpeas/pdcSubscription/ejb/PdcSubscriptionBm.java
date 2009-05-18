/*
 * Aliaksei_Budnikau
 * Date: Oct 24, 2002
 */
package com.silverpeas.pdcSubscription.ejb;

import com.silverpeas.pdcSubscription.model.PDCSubscription;

import java.util.ArrayList;
import java.util.List;
import java.rmi.RemoteException;

public interface PdcSubscriptionBm extends javax.ejb.EJBObject {

    /**
     * @return a list of <code>PDCSubscriptions</code> finded by id provided
     */
    public ArrayList getPDCSubscriptionByUserId(int userId) throws RemoteException;

    public PDCSubscription getPDCSubsriptionById(int id) throws RemoteException;

    /**
     * @return new  autogenerated PDCSubscription id
     */
    public int createPDCSubscription(PDCSubscription subscription) throws RemoteException;

    public void updatePDCSubscription(PDCSubscription subscription) throws RemoteException;

    public void removePDCSubscriptionById(int id) throws RemoteException;

    public void removePDCSubscriptionById(int[] ids) throws RemoteException;

    /**
     * This method check is any subscription that match criterias provided and sends notification if succeed
     * @param classifyValues Linst of ClassifyValues to be checked
     * @param componentId  component where classify event occures
     * @param silverObjectid object that was classified
     */
    public void checkSubscriptions(List classifyValues, String componentId, int silverObjectid) throws RemoteException;

    /**
     *  Implements PDCSubscription check for value deletion. It deletes all references to the path containing this value
     *  from PDCSubscription module DB
     *  @param axisId the axis to be checked
     *  @param axisName the name of the axis
     *  @param oldPath old path that would be removed soon
     *  @param newPath new path. That will be places instead of old for this axis
     *  @param pathInfo should contains PdcBm.getFullPath data structure
     */
    public void checkValueOnDelete(int axiId, String axisName, List oldPath, List newPath, List pathInfo) throws RemoteException;

    /**
     *  Implements PDCSubscription check for axis deletion. It deletes all references to this axis from PDCSubscription module DB
     *  @param axisId the axis to be checked
     *  @param axisName the name of the axis
     */
    public void checkAxisOnDelete(int axisId, String axisName) throws RemoteException;

}
