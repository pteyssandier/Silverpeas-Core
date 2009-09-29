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
package com.stratelia.webactiv.beans.admin;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.silverpeas.util.i18n.AbstractI18NBean;
import com.stratelia.webactiv.organization.ComponentInstanceRow;

/**
 * The class ComponentInstLight is the representation in memory of a component
 * instance
 */
public class ComponentInstLight extends AbstractI18NBean implements
    Serializable {
  /* Unique identifier of the instance */
  private String m_sId;

  /* Unique identifier of the father of the space */
  private String m_sDomainFatherId;

  /* name of the instance */
  private String m_sLabel;

  /* description of the instance */
  private String m_sDescription;

  /* instance Type */
  private String m_sName;

  private Date createDate = null;
  private Date updateDate = null;
  private Date removeDate = null;
  private String status = null;
  private int createdBy = -1;
  private int updatedBy = -1;
  private int removedBy = -1;

  private String creatorName = null;
  private String updaterName = null;
  private String removerName = null;

  private List path = null;

  /**
   * Constructor
   */
  public ComponentInstLight() {
    m_sId = "";
    m_sDomainFatherId = "";
    m_sLabel = "";
    m_sName = "";
    m_sDescription = "";
  }

  /**
   * Constructor
   */
  public ComponentInstLight(ComponentInstanceRow compo) {
    m_sId = Integer.toString(compo.id);
    m_sDomainFatherId = Integer.toString(compo.spaceId);
    m_sLabel = compo.name;
    m_sDescription = compo.description;
    m_sName = compo.componentName;

    if (compo.createTime != null)
      createDate = new Date(Long.parseLong(compo.createTime));
    if (compo.updateTime != null)
      updateDate = new Date(Long.parseLong(compo.updateTime));
    if (compo.removeTime != null)
      removeDate = new Date(Long.parseLong(compo.removeTime));
    status = compo.status;

    createdBy = compo.createdBy;
    updatedBy = compo.updatedBy;
    removedBy = compo.removedBy;
  }

  /**
   * Set the space id
   */
  public void setId(String sId) {
    this.m_sId = sId;
  }

  /**
   * Get the space id
   * 
   * @return the requested space id
   */
  public String getId() {
    return m_sId;
  }

  /**
   * Set the domain father id
   */
  public void setDomainFatherId(String sDomainFatherId) {
    this.m_sDomainFatherId = sDomainFatherId;
  }

  /**
   * Get the domain father id
   * 
   * @return the space father id. If space has no father, returns an empty
   *         string.
   */
  public String getDomainFatherId() {
    return m_sDomainFatherId;
  }

  /**
   * Get the space name
   * 
   * @return the space name
   */
  public String getName() {
    return m_sName;
  }

  /**
   * Get the component type
   * 
   * @return the component type
   */
  public String getLabel() {
    return m_sLabel;
  }

  public void setLabel(String s) {
    m_sLabel = s;
  }

  /**
   * Get the component description
   * 
   * @return the component description
   */
  public String getDescription() {
    return m_sDescription;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Date getRemoveDate() {
    return removeDate;
  }

  public String getStatus() {
    return status;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getRemoverName() {
    return removerName;
  }

  public void setRemoverName(String removerName) {
    this.removerName = removerName;
  }

  public String getUpdaterName() {
    return updaterName;
  }

  public void setUpdaterName(String updaterName) {
    this.updaterName = updaterName;
  }

  public int getCreatedBy() {
    return createdBy;
  }

  public int getRemovedBy() {
    return removedBy;
  }

  public int getUpdatedBy() {
    return updatedBy;
  }

  public String getPath(String separator) {
    String sPath = "";
    if (path != null) {
      SpaceInstLight space = null;
      for (int i = 0; i < path.size(); i++) {
        if (i > 0)
          sPath += separator;

        space = (SpaceInstLight) path.get(i);
        sPath += space.getName();
      }
    }
    return sPath;
  }

  public void setPath(List path) {
    this.path = path;
  }

  /**
   * I18N
   * 
   */
  public String getLabel(String language) {
    ComponentI18N s = (ComponentI18N) getTranslations().get(language);
    if (s != null)
      return s.getName();
    else
      return getLabel();
  }

  public String getDescription(String language) {
    ComponentI18N s = (ComponentI18N) getTranslations().get(language);
    if (s != null)
      return s.getDescription();
    else
      return getDescription();
  }

}