/**
 *
 * @author  nchaix
 * @version
 */

package com.stratelia.webactiv.beans.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.silverpeas.util.i18n.AbstractI18NBean;
import com.stratelia.webactiv.beans.admin.instance.control.SPParameter;
import com.stratelia.webactiv.beans.admin.instance.control.SPParameters;

public class ComponentInst extends AbstractI18NBean implements Serializable,
    Cloneable, Comparable {
  public final static String STATUS_REMOVED = "R";

  private String m_sId;
  private String m_sName;
  private String m_sLabel;
  private String m_sDescription;
  private String m_sDomainFatherId;
  private int m_iOrderNum;

  private Date createDate = null;
  private Date updateDate = null;
  private Date removeDate = null;
  private String status = null;

  private String creatorUserId;
  private UserDetail creator;
  private String updaterUserId;
  private UserDetail updater;
  private String removerUserId;
  private UserDetail remover;

  private boolean isPublic = false;
  private boolean isHidden = false;
  private boolean isInheritanceBlocked = false;

  private ArrayList m_alProfileInst;
  private SPParameters parameters = null;

  /** Creates new ComponentInst */
  public ComponentInst() {
    m_sId = "";
    m_sName = "";
    m_sLabel = "";
    m_sDescription = "";
    m_sDomainFatherId = "";
    m_iOrderNum = 0;
    m_alProfileInst = new ArrayList();
    isPublic = false;
    isHidden = false;
  }

  public int compareTo(Object o) {
    return m_iOrderNum - ((ComponentInst) o).m_iOrderNum;
  }

  public Object clone() {
    ComponentInst ci = new ComponentInst();
    Iterator it;

    ci.m_sId = m_sId;
    ci.m_sName = m_sName;
    ci.m_sLabel = m_sLabel;
    ci.m_sDescription = m_sDescription;
    ci.m_sDomainFatherId = m_sDomainFatherId;
    ci.m_iOrderNum = m_iOrderNum;
    ci.isPublic = isPublic;
    ci.isHidden = isHidden;

    if (m_alProfileInst == null) {
      ci.m_alProfileInst = null;
    } else {
      ci.m_alProfileInst = new ArrayList();
      it = m_alProfileInst.iterator();
      while (it.hasNext()) {
        ci.m_alProfileInst.add(((ProfileInst) it.next()).clone());
      }
    }
    ci.parameters = (SPParameters) this.parameters.clone();
    return ci;
  }

  protected String[] cloneStringArray(String[] src) {
    String[] valret;

    if (src == null) {
      return null;
    } else {
      valret = new String[src.length];
      for (int i = 0; i < src.length; i++) {
        valret[i] = src[i];
      }
    }
    return valret;
  }

  public void setId(String sId) {
    m_sId = sId;
  }

  public String getId() {
    return m_sId;
  }

  public void setName(String sName) {
    m_sName = sName;
  }

  public String getName() {
    return m_sName;
  }

  public void setLabel(String sLabel) {
    m_sLabel = sLabel;
  }

  public String getLabel() {
    return m_sLabel;
  }

  public void setDescription(String sDescription) {
    m_sDescription = sDescription;
  }

  public String getDescription() {
    return m_sDescription;
  }

  public void setDomainFatherId(String sDomainFatherId) {
    m_sDomainFatherId = sDomainFatherId;
  }

  public String getDomainFatherId() {
    return m_sDomainFatherId;
  }

  public void setOrderNum(int iOrderNum) {
    m_iOrderNum = iOrderNum;
  }

  public int getOrderNum() {
    return m_iOrderNum;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public Date getRemoveDate() {
    return removeDate;
  }

  public void setRemoveDate(Date removeDate) {
    this.removeDate = removeDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public String getRemoverUserId() {
    return removerUserId;
  }

  public void setRemoverUserId(String removerUserId) {
    this.removerUserId = removerUserId;
  }

  public String getUpdaterUserId() {
    return updaterUserId;
  }

  public void setUpdaterUserId(String updaterUserId) {
    this.updaterUserId = updaterUserId;
  }

  public int getNumProfileInst() {
    return m_alProfileInst.size();
  }

  public void addProfileInst(ProfileInst profileInst) {
    m_alProfileInst.add(profileInst);
  }

  public void deleteProfileInst(ProfileInst profileInst) {
    for (int nI = 0; nI < m_alProfileInst.size(); nI++)
      if (((ProfileInst) m_alProfileInst.get(nI)).getName().equals(
          profileInst.getName()))
        m_alProfileInst.remove(nI);
  }

  public ArrayList getAllProfilesInst() {
    return m_alProfileInst;
  }

  public List getInheritedProfiles() {
    List profiles = new ArrayList();
    for (int nI = 0; nI < m_alProfileInst.size(); nI++) {
      ProfileInst profile = (ProfileInst) m_alProfileInst.get(nI);
      if (profile.isInherited())
        profiles.add(profile);
    }

    return profiles;
  }

  public List getProfiles() {
    List profiles = new ArrayList();
    for (int nI = 0; nI < m_alProfileInst.size(); nI++) {
      ProfileInst profile = (ProfileInst) m_alProfileInst.get(nI);
      if (!profile.isInherited())
        profiles.add(profile);
    }

    return profiles;
  }

  public void removeAllProfilesInst() {
    m_alProfileInst = new ArrayList();
  }

  public ProfileInst getProfileInst(String sProfileName) {
    for (int nI = 0; nI < m_alProfileInst.size(); nI++) {
      ProfileInst profile = (ProfileInst) m_alProfileInst.get(nI);
      if (!profile.isInherited() && profile.getName().equals(sProfileName))
        return profile;
    }
    return null;
  }

  public ProfileInst getInheritedProfileInst(String sProfileName) {
    for (int nI = 0; nI < m_alProfileInst.size(); nI++) {
      ProfileInst profile = (ProfileInst) m_alProfileInst.get(nI);
      if (profile.isInherited() && profile.getName().equals(sProfileName))
        return profile;
    }
    return null;
  }

  public ProfileInst getProfileInst(int nIndex) {
    return (ProfileInst) m_alProfileInst.get(nIndex);
  }

  public void setSPParameters(SPParameters parameters) {
    this.parameters = parameters;
  }

  public SPParameters getSPParameters() {
    if (parameters == null) {
      parameters = new SPParameters();
    }
    return parameters;
  }

  public List getParameters() {
    return parameters.getParameters();
  }

  public void setParameters(List parameters) {
    getSPParameters().setParameters(parameters);
  }

  public SPParameter getParameter(String parameterName) {
    return parameters.getParameter(parameterName);
  }

  public String getParameterValue(String parameterName) {
    return parameters.getParameterValue(parameterName);
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

  public boolean isHidden() {
    return isHidden;
  }

  public void setHidden(boolean isHidden) {
    this.isHidden = isHidden;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public boolean isInheritanceBlocked() {
    return isInheritanceBlocked;
  }

  public void setInheritanceBlocked(boolean isInheritanceBlocked) {
    this.isInheritanceBlocked = isInheritanceBlocked;
  }

  public String getCreatorUserId() {
    return creatorUserId;
  }

  public void setCreatorUserId(String creatorUserId) {
    this.creatorUserId = creatorUserId;
  }

  public UserDetail getCreator() {
    return creator;
  }

  public void setCreator(UserDetail creator) {
    this.creator = creator;
  }

  public UserDetail getUpdater() {
    return updater;
  }

  public void setUpdater(UserDetail updater) {
    this.updater = updater;
  }

  public UserDetail getRemover() {
    return remover;
  }

  public void setRemover(UserDetail remover) {
    this.remover = remover;
  }

}