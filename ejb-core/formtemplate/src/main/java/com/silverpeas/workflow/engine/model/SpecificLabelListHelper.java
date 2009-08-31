package com.silverpeas.workflow.engine.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silverpeas.workflow.api.WorkflowException;
import com.silverpeas.workflow.api.model.ContextualDesignation;
import com.silverpeas.workflow.api.model.ContextualDesignations;

/**
 * Class managing a collection of ContextualDesigantion objects.
 */
public class SpecificLabelListHelper implements ContextualDesignations
{
	List labels = null;  // a reference to the list we are going to manage
	
    /**
     * Constructor
     */
    public SpecificLabelListHelper()
    {
        this.labels = new ArrayList();
    }
    
	/**
	 * Constructor
	 */
	public SpecificLabelListHelper(List labels)
	{
		this.labels = labels;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.silverpeas.workflow.api.model.ContextualDesignations#getLabel(java.lang.String, java.lang.String)
	 */
	public String getLabel(String role, String language)
	{
		ContextualDesignation label = getSpecificLabel(role, language);
		
		if (label != null)
			return label.getContent();
		
		label = getSpecificLabel(role, "default"); //$NON-NLS-1$
		if (label != null)
			return label.getContent();
		
		label = getSpecificLabel("default", language); //$NON-NLS-1$
		if (label != null)
			return label.getContent();
		
		label = getSpecificLabel("default", "default"); //$NON-NLS-1$ //$NON-NLS-2$
		if (label != null)
			return label.getContent();
		
		return ""; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.silverpeas.workflow.api.model.ContextualDesignations#getSpecificLabel(java.lang.String, java.lang.String)
	 */
	public ContextualDesignation getSpecificLabel(String role, String language)
	{
		SpecificLabel label = null;
		for (int l=0; l<labels.size(); l++)
		{
			label = (SpecificLabel) labels.get(l);
			if (role != null && role.equals(label.getRole()) && language != null && language.equals(label.getLanguage()))
				return label;
		}
		return null;
	}

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.ContextualDesignations#addContextualDesignation(com.silverpeas.workflow.api.model.ContextualDesignation)
     */
    public void addContextualDesignation(ContextualDesignation contextualDesignation)
    {
        labels.add( contextualDesignation);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.ContextualDesignations#createContextualDesignation()
     */
    public ContextualDesignation createContextualDesignation()
    {
        return new SpecificLabel();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.ContextualDesignations#iterateContextualDesignation()
     */
    public Iterator iterateContextualDesignation()
    {
        if ( labels == null )
            return null;
        else
            return labels.iterator();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.ContextualDesignations#removeContextualDesignation(java.lang.String)
     */
    public void removeContextualDesignation(ContextualDesignation contextualDesignation) 
        throws WorkflowException
    {
        if ( labels == null )
            return;
        
        if ( !labels.remove( contextualDesignation ) )
            throw new WorkflowException("SpecificLabelListHelper.removeContextualDesignation()", //$NON-NLS-1$
                                        "workflowEngine.EX_DESIGNATION_NOT_FOUND",               // $NON-NLS-1$
                                        contextualDesignation == null
                                            ? "<null>"  //$NON-NLS-1$
                                            : contextualDesignation.getContent() );
    }
}