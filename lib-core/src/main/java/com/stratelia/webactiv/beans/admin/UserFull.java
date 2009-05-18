package com.stratelia.webactiv.beans.admin;

import java.util.HashMap;

import com.stratelia.silverpeas.silvertrace.SilverTrace;

public class UserFull extends UserDetail
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap              m_hInfos = null;
	protected AbstractDomainDriver m_pDomainDriver = null;

	protected String 			   m_password = "";
	protected boolean			   m_isPasswordValid = false;
	protected boolean			   m_isPasswordAvailable = false;
    
    /** Creates new UserFull */
    public UserFull()
    {
        super();
        m_hInfos = new HashMap();
    }

    public UserFull(AbstractDomainDriver domainDriver)
    {
        super();
        m_hInfos = new HashMap();
        m_pDomainDriver = domainDriver;
    }

    public UserFull(AbstractDomainDriver domainDriver, UserDetail toClone)
    {
        super(toClone);
        m_hInfos = new HashMap();
        m_pDomainDriver = domainDriver;
    }

    // Password specific entries
    
    public boolean isPasswordAvailable()
    {
    	return m_isPasswordAvailable;
    }
    
    public void setPasswordAvailable(boolean pa)
    {
    	m_isPasswordAvailable = pa;
    }
    
    public boolean isPasswordValid()
    {
    	return m_isPasswordValid;
    }
    
    public void setPasswordValid(boolean pv)
    {
    	m_isPasswordValid = pv;
    }
    
    public String getPassword()
    {
    	return (m_password == null) ? "" : m_password;
    }
    
    public void setPassword(String p)
    {
    	m_password = p;
    }
    
    // Values' getters

    public String[] getPropertiesNames()
    {
    	if(m_pDomainDriver != null) {
    		return m_pDomainDriver.getPropertiesNames();
    	}
    	return new String[0];
    	
    }

    public HashMap getSpecificDetails()
    {
        return m_hInfos;
    }

    public String getValue(String propertyName, String defaultValue)
    {
        String valret;

        valret = (String)m_hInfos.get(propertyName);
        if (valret == null)
        {
            valret = defaultValue;
        }
        return valret;
    }
    
    public String getValue(String propertyName)
    {
        return getValue(propertyName,"");
    }
    
    public boolean getValue(String propertyName, boolean defaultValue)
    {
        String sValret;
        boolean valret = defaultValue;

        sValret = (String)m_hInfos.get(propertyName);
        if (sValret != null)
        {
            if (sValret.equalsIgnoreCase("true"))
            {
                valret = true;
            }
            else if (sValret.equalsIgnoreCase("false"))
            {
                valret = false;
            }
        }
        return valret;
    }
    
    // Labels' getters

    public HashMap getSpecificLabels(String language)
    {
        if (m_pDomainDriver != null)
        {
            return m_pDomainDriver.getPropertiesLabels(language);
        }
        return null;
    }

    public String getSpecificLabel(String language, String propertyName)
    {
        String valret = null;

        if (m_pDomainDriver != null)
        {
            HashMap theLabels = m_pDomainDriver.getPropertiesLabels(language);
            valret = (String)theLabels.get(propertyName);
        }
        if (valret == null)
        {
            valret = "";
        }
        return valret;
    }
    
    public String getPropertyType(String propertyName)
    {
        String valret = null;

        if (m_pDomainDriver != null)
        {
        	DomainProperty domainProperty = m_pDomainDriver.getProperty(propertyName);
        	if(domainProperty != null) {
        		valret = domainProperty.getType();
        	}
        }
        if (valret == null)
        {
            valret = "";
        }
        return valret;
    }
    
    public boolean isPropertyUpdatableByUser(String property)
    {
    	if (m_pDomainDriver != null)
        {
        	DomainProperty domainProperty = m_pDomainDriver.getProperty(property);
        	if(domainProperty != null) {
        		return domainProperty.isUpdateAllowedToUser();
        	}
        }
    	return false;
    }

    // Values' setters

    public void setValue(String propertyName, String value)
    {
        m_hInfos.put(propertyName,value);
    }
    
    public void setValue(String propertyName, boolean bValue)
    {
        if (bValue)
        {
            m_hInfos.put(propertyName,"true");
        }
        else
        {
            m_hInfos.put(propertyName,"false");
        }
    }
    
    // Equals and traces...

    public boolean equals(UserFull cmpUser)
    {
        if (super.equals(cmpUser) == false)
        {
            return false;
        }

        String[] keys = (String[])m_hInfos.keySet().toArray(new String[0]);
        int      i;
        boolean  isTheSame = true;
        
        for (i = 0; (i < keys.length) && isTheSame; i++)
        {
            isTheSame = getValue(keys[i]).equals(cmpUser.getValue(keys[i]));
        }
        return isTheSame;
    }
    
    public void traceUser()
    {
        super.traceUser();
        
        String[] keys = (String[])m_hInfos.keySet().toArray(new String[0]);
        int      i;
        
        for (i = 0; i < keys.length ; i++)
        {
            SilverTrace.info("admin","UserFull.traceUser","admin.MSG_DUMP_USER",keys[i] + " : " + (String)m_hInfos.get(keys[i]));
        }
   }
}
