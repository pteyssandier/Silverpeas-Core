package com.stratelia.silverpeas.pdc.model;

import java.util.*;

import com.stratelia.silverpeas.silvertrace.*;
import com.stratelia.silverpeas.containerManager.*;

/**
 * This is the data structure that the content JSP is going to use (built by the
 * container router)
 * 
 */
public class ContainerContextImpl implements ContainerContext,
    java.io.Serializable {
  private int nContainerInstanceId = -1; // The instance of the container on
  // which the content is going to use
  private String sReturnURL = null; // URL to get back on the container
  // private String BrowseBar = null; //implement a BrowseBar object
  private URLIcone uClassifyURLIcone = null; // URL on the JSP to add classify
  // positions on the container
  // private URLIcone uGenericClassifyURLIcone = null; // URL on the JSP to add
  // classify positions across containers
  private ContainerPositionInterface curPosition = null; // Current position in
  // the container
  private ContainerPeas containerPeas = null; // ContainerPeas

  public ContainerContextImpl() {
  }

  public void setContainerInstanceId(int nGivenContainerInstanceId) {
    nContainerInstanceId = nGivenContainerInstanceId;
  }

  public int getContainerInstanceId() {
    return nContainerInstanceId;
  }

  public void setReturnURL(String sGivenReturnURL) {
    sReturnURL = sGivenReturnURL;
  }

  public String getReturnURL() {
    return sReturnURL;
  }

  public void setClassifyURLIcone(URLIcone uGivenClassifyURLIcone) {
    uClassifyURLIcone = uGivenClassifyURLIcone;
  }

  public URLIcone getClassifyURLIcone() {
    return uClassifyURLIcone;
  }

  public ContainerPositionInterface getContainerPositionInterface() {
    return curPosition;
  }

  public void setContainerPositionInterface(
      ContainerPositionInterface GivenPosition) {
    curPosition = GivenPosition;
  }

  public void setContainerPeas(ContainerPeas givenContainerPeas) {
    containerPeas = givenContainerPeas;
  }

  // -------------------------------------------------------------------------------
  // METHODS OF THE INTERFACE
  // -------------------------------------------------------------------------------

  /*
   * Get the classify URL with parameters to put as link on the Classify Icone
   */
  public String getClassifyURLWithParameters(String sComponentId,
      String sSilverContentId) {
    try {
      return uClassifyURLIcone.getActionURL()
          + "?"
          + containerPeas.getContainerInterface().getCallParameters(
              sComponentId, sSilverContentId);
    } catch (Exception e) {
      SilverTrace.error("containerManager",
          "ContainerContext.getClassifyURLWithParameters",
          "root.MSG_GEN_PARAM_VALUE", "Fatal Error", e);
      return null;
    }
  }

  /*
   * Find the SearchContext for the given SilverContentId
   */
  public ContainerPositionInterface getSilverContentIdSearchContext(
      int nSilverContentId, String sComponentId) {
    try {
      ContainerInterface ci = containerPeas.getContainerInterface();
      return ci.getSilverContentIdSearchContext(nSilverContentId, sComponentId);
    } catch (Exception e) {
      SilverTrace.error("containerManager",
          "ContainerContext.getSilverContentIdPositions",
          "root.MSG_GEN_PARAM_VALUE", "Fatal Error", e);
      return null;
    }
  }

  /*
   * Get All the SilverContentIds corresponding to the given position in the
   * given Components
   */
  public List getSilverContentIdByPosition(
      ContainerPositionInterface containerPosition, List alComponentIds) {
    try {
      ContainerInterface ci = containerPeas.getContainerInterface();
      return ci
          .findSilverContentIdByPosition(containerPosition, alComponentIds);
    } catch (Exception e) {
      SilverTrace.error("containerManager",
          "ContainerContext.getSilverContentIdByPosition",
          "root.MSG_GEN_PARAM_VALUE", "Fatal Error", e);
      return null;
    }
  }
}
