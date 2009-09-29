/*
 * Aliaksei_Budnikau
 * Date: Oct 14, 2002
 */
package com.silverpeas.interestCenter.model;

import java.util.ArrayList;

public class InterestCenter implements Cloneable, java.io.Serializable {

  /** This constant indicates that pk reference in class was not initilized */
  public static final int NULLID = -1;

  private int id;
  private String name;
  private String query;
  private String workSpaceID;
  private String peasID;
  private String authorID;
  private java.util.Date afterDate;
  private java.util.Date beforeDate;
  private ArrayList pdcContext;
  private int ownerID;

  /**
   * Default constuctor
   */
  public InterestCenter() {
    this.id = NULLID;
    this.ownerID = NULLID;
  }

  /**
   * Full constructor
   */
  public InterestCenter(int iD, String name, String query, String workSpaceID,
      String peasID, String authorID, java.util.Date afterDate,
      java.util.Date beforeDate, ArrayList pcdContext, int ownerID) {
    this.id = iD;
    this.name = name;
    this.query = query;
    this.workSpaceID = workSpaceID;
    this.peasID = peasID;
    this.authorID = authorID;
    this.afterDate = afterDate;
    this.beforeDate = beforeDate;
    this.pdcContext = pcdContext;
    this.ownerID = ownerID;
  }

  public int getId() {
    return id;
  }

  public void setId(int iD) {
    this.id = iD;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQuery() {
    return (query == null) ? "" : query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getWorkSpaceID() {
    return workSpaceID;
  }

  public void setWorkSpaceID(String workSpaceID) {
    this.workSpaceID = workSpaceID;
  }

  public String getPeasID() {
    return peasID;
  }

  public void setPeasID(String peasID) {
    this.peasID = peasID;
  }

  public String getAuthorID() {
    return authorID;
  }

  public void setAuthorID(String authorID) {
    this.authorID = authorID;
  }

  public java.util.Date getAfterDate() {
    return afterDate;
  }

  public void setAfterDate(java.util.Date afterDate) {
    this.afterDate = afterDate;
  }

  public java.util.Date getBeforeDate() {
    return beforeDate;
  }

  public void setBeforeDate(java.util.Date beforeDate) {
    this.beforeDate = beforeDate;
  }

  public ArrayList getPdcContext() {
    return pdcContext;
  }

  public void setPdcContext(ArrayList pdcContext) {
    this.pdcContext = pdcContext;
  }

  public int getOwnerID() {
    return ownerID;
  }

  public void setOwnerID(int ownerID) {
    this.ownerID = ownerID;
  }

  /**
   * Overriden toString method for debug/trace purposes
   */
  public String toString() {
    return "InterestCenter object : [ ID = " + id + ", name = " + name
        + ", query = " + query + ", workSpaceID = " + workSpaceID
        + ", peaseID = " + peasID + ", authorID = " + authorID
        + ", afterDate = " + afterDate + ", beforeDate = " + beforeDate
        + ", pcdContext = " + pdcContext + ", ownerID = " + ownerID + " ];";
  }

  /**
   * Support Cloneable Interface
   */
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null; // this should never happened
    }
  }
}
