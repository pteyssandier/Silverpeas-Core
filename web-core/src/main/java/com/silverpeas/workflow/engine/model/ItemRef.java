package com.silverpeas.workflow.engine.model;

import java.io.Serializable;
import java.util.Iterator;

import com.silverpeas.workflow.api.model.AbstractDescriptor;
import com.silverpeas.workflow.api.model.ContextualDesignation;
import com.silverpeas.workflow.api.model.ContextualDesignations;
import com.silverpeas.workflow.api.model.Input;
import com.silverpeas.workflow.api.model.Item;
import com.silverpeas.workflow.engine.AbstractReferrableObject;

/**
 * Class implementing the representation of the &lt;input&gt; element of a Process Model.
**/
public class ItemRef extends AbstractReferrableObject implements Input, AbstractDescriptor, Serializable 
{

	private Item                   item;
    private boolean                readonly      = false; // only used in forms construction
    private boolean                mandatory     = false; // only used in forms construction
    private String                 displayerName = null;  // only used in forms construction
    private String                 value         = null;  // default value
    private ContextualDesignations labels;                // collection of labels

    //~ Instance fields related to AbstractDescriptor ////////////////////////////////////////////////////////

    private AbstractDescriptor     parent;
    private boolean                hasId         = false;
    private int                    id;

	/**
	 * Constructor
	 */
    public ItemRef() 
	{
        reset();
    }

	/**
	 * reset attributes
	 */
	private void reset()
	{
        labels        = new SpecificLabelListHelper();
	}

	/**
	 * Get the referred item
     */
    public Item getItem()
    {
        return item;
    }

    /**
	 * Get value of readOnly attribute
	 * @return		true if item must be readonly
     */
    public boolean isReadonly()
    {
        return this.readonly;
    }

    /**
	 * Get value of mandatory attribute
	 * @return		true if item must be filled
     */
    public boolean isMandatory()
    {
        return this.mandatory;
    }

    /**
	 * Get name of displayer used to show the item
	 * @return displayer name
     */
    public String getDisplayerName()
    {
        return this.displayerName;
    }

    /**
	 * Get default value
	 * @return default value
     */
    public String getValue()
    {
        return this.value;
    }

	/**
	 * Set the referred item
     * @param item Item to refer
     */
    public void setItem(Item item)
    {
        this.item = item;
    }

    /**
     * Set the readonly attribute 
     */
    public void setReadonly(boolean readonly)
    {
        this.readonly = readonly;
    }

    /**
	 * Set value of mandatory attribute
	 * @param	mandatory	true if item must be filled
     */
    public void setMandatory(boolean mandatory)
    {
        this.mandatory = mandatory;
    }

    /**
	 * Set name of displayer used to show the item
	 * @param	displayerName	displayer name
     */
    public void setDisplayerName(String displayerName)
    {
        this.displayerName = displayerName;
    }

    /**
	 * Set default value
	 * @param	value	default value
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.Input#getLabels()
     */
    public ContextualDesignations getLabels() 
    {
        return labels;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.Input#getLabel(java.lang.String, java.lang.String)
     */
    public String getLabel(String role, String language)
    {
        return labels.getLabel(role, language);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.Input#addLabel(com.silverpeas.workflow.api.model.ContextualDesignation)
     */
    public void addLabel(ContextualDesignation label)
    {
        labels.addContextualDesignation(label);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.Input#iterateLabel()
     */
    public Iterator iterateLabel()
    {
        return labels.iterateContextualDesignation();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.Input#createDesignation()
     */
    public ContextualDesignation createDesignation()
    {
        return labels.createContextualDesignation();
    }
	
	/* (non-Javadoc)
     * @see com.silverpeas.workflow.engine.AbstractReferrableObject#getKey()
     */
    public String getKey()
    {
        StringBuffer sb = new StringBuffer();
        
        if ( item != null )
            sb.append( item.getName() );
            
        sb.append("|");
        
        if ( value != null )
            sb.append( value );
        
        return sb.toString();
    }

    /************* Implemented methods *****************************************/
    //~ Methods ////////////////////////////////////////////////////////////////

	/*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AbstractDescriptor#setId(int)
	 */
    public void setId(int id) 
    {
        this.id = id;
        hasId = true;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AbstractDescriptor#getId()
     */
    public int getId() 
    {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AbstractDescriptor#setParent(com.silverpeas.workflow.api.model.AbstractDescriptor)
     */
    public void setParent(AbstractDescriptor parent) 
    {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AbstractDescriptor#getParent()
     */
    public AbstractDescriptor getParent() 
    {
        return parent;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AbstractDescriptor#hasId()
     */
    public boolean hasId() 
    {
        return hasId;
    }
}