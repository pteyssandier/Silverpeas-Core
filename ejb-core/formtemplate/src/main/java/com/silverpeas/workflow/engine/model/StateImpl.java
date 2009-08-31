package com.silverpeas.workflow.engine.model;

import java.io.Serializable;
import java.util.Iterator;

import com.silverpeas.workflow.api.model.AbstractDescriptor;
import com.silverpeas.workflow.api.model.Action;
import com.silverpeas.workflow.api.model.AllowedActions;
import com.silverpeas.workflow.api.model.ContextualDesignation;
import com.silverpeas.workflow.api.model.ContextualDesignations;
import com.silverpeas.workflow.api.model.QualifiedUsers;
import com.silverpeas.workflow.api.model.State;
import com.silverpeas.workflow.engine.AbstractReferrableObject;


/**
 * Class implementing the representation of the &lt;state&gt; element of a Process Model.
**/
public class StateImpl extends AbstractReferrableObject implements State, AbstractDescriptor, Serializable
{
    private String                 name;
    private ContextualDesignations labels;            // collection of labels
    private ContextualDesignations descriptions;      // collection of  descriptions
    private ContextualDesignations activities;        // collection of activities
    private QualifiedUsers         workingUsers;
    private QualifiedUsers         interestedUsers;
    private AllowedActions         allowedActions;
    private AllowedActions         filteredActions;
    private Action                 timeoutAction;
    private int                    timeoutInterval;
    private boolean                timeoutNotifyAdmin;

    // ~ Instance fields related to AbstractDescriptor ////////////////////////////////////////////////////////

    private AbstractDescriptor     parent;
    private boolean                hasId = false;
    private int                    id;

    /**
     * Constructor
     */
    public StateImpl() 
    {
        reset();
    }

    /**
     * Constructor
     * @param    name    state name
     */
    public StateImpl(String name) 
    {
        this();
        this.name = name;
    }

    /**
     * reset attributes
     */
    private void reset()
    {
        labels        = new SpecificLabelListHelper();
        descriptions  = new SpecificLabelListHelper();
        activities    = new SpecificLabelListHelper();
        timeoutAction        = null;
        timeoutInterval        = -1;
        timeoutNotifyAdmin    = true;
    }

    ////////////////////
    // labels
    ////////////////////

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getLabels()
     */
    public ContextualDesignations getLabels() 
    {
        return labels;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getLabel(java.lang.String, java.lang.String)
     */
    public String getLabel(String role, String language)
    {
        return labels.getLabel(role, language);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#addLabel(com.silverpeas.workflow.api.model.ContextualDesignation)
     */
    public void addLabel(ContextualDesignation label)
    {
        labels.addContextualDesignation(label);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#iterateLabel()
     */
    public Iterator iterateLabel()
    {
        return labels.iterateContextualDesignation();
    }

    ////////////////////
    // activities
    ////////////////////

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getActivities()
     */
    public ContextualDesignations getActivities() 
    {
        return activities;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getActivity(java.lang.String, java.lang.String)
     */
    public String getActivity(String role, String language)
    {
        return activities.getLabel(role, language);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#addActivity(com.silverpeas.workflow.api.model.ContextualDesignation)
     */
    public void addActivity(ContextualDesignation label)
    {
        activities.addContextualDesignation(label);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#iterateActivity()
     */
    public Iterator iterateActivity()
    {
        return activities.iterateContextualDesignation();
    }

    ////////////////////
    // descriptions
    ////////////////////

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getDescriptions()
     */
    public ContextualDesignations getDescriptions() 
    {
        return descriptions;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getDescription(java.lang.String, java.lang.String)
     */
    public String getDescription(String role, String language)
    {
        return descriptions.getLabel(role, language);
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#addDescription(com.silverpeas.workflow.api.model.ContextualDesignation)
     */
    public void addDescription(ContextualDesignation description)
    {
        descriptions.addContextualDesignation(description);
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#iterateDescription()
     */
    public Iterator iterateDescription()
    {
        return descriptions.iterateContextualDesignation();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#createDesignation()
     */
    public ContextualDesignation createDesignation()
    {
        return labels.createContextualDesignation();
    }

    ////////////////////
    // Miscellaneous
    ////////////////////

    /**
     * Get actions available in this state
     * @return allowedActions allowed actions
     */
    public Action[] getAllowedActions()
    {
        // check for allowedActions attribute
        if (allowedActions == null)
            return null;

        return allowedActions.getAllowedActions();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getAllAllowedActions()
     */
    public AllowedActions getAllowedActionsEx() 
    {
        return allowedActions;
    }

    public AllowedActions createAllowedActions()
    {
        return new ActionRefs();
    }
    
    public Action[] getFilteredActions() {
    	if (filteredActions == null)
            return null;

        return filteredActions.getAllowedActions();
	}

	@Override
	public void setFilteredActions(AllowedActions allowedActions) {
		filteredActions = allowedActions;
	}

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getInterestedUsers()
     */
    public QualifiedUsers getInterestedUsers()
    {
        if (interestedUsers == null)
            return (QualifiedUsers) (new QualifiedUsersImpl());
        else
            return this.interestedUsers;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getInterestedUsersEx()
     */
    public QualifiedUsers getInterestedUsersEx()
    {
        return interestedUsers;
    }

    /**
     * Get the name of this state
     * @return state's name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the pre-conditions to enter this state
     * @return state's Preconditions object containing re-conditions
     */
/*    public Preconditions getPreconditions()
    {
        return this.preconditions;
    }
*/

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getWorkingUsers()
     */
    public QualifiedUsers getWorkingUsers()
    {
        if (workingUsers == null)
            return (QualifiedUsers) (new QualifiedUsersImpl());
        else
            return this.workingUsers;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getWorkingUsersEx()
     */
    public QualifiedUsers getWorkingUsersEx()
    {
        return workingUsers;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#setAllowedActions(com.silverpeas.workflow.api.model.AllowedActions)
     */
    public void setAllowedActions(AllowedActions allowedActions) 
    {
        this.allowedActions = allowedActions;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#createQualifiedUsers()
     */
    public QualifiedUsers createQualifiedUsers()
    {
        return new QualifiedUsersImpl();
    }
    
    /**
     * Set all the users interested by this state
     * @param QualifiedUsers object containing interested users
     */
    public void setInterestedUsers(QualifiedUsers interestedUsers)
    {
        this.interestedUsers = interestedUsers;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the pre-conditions to enter this state
     * @param state's Preconditions object containing re-conditions
     */
/*    public void setPreconditions(Preconditions preconditions)
    {
        this.preconditions = preconditions;
    }
*/

    /**
     * Set all the users who can act in this state
     * @param QualifiedUsers object containing these users
     */
    public void setWorkingUsers(QualifiedUsers workingUsers)
    {
        this.workingUsers = workingUsers;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#getTimeoutInterval()
     */
    public int getTimeoutInterval()
    {
        return timeoutInterval;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#setTimeoutInterval(int)
     */
    public void setTimeoutInterval(int hours) 
    {
        timeoutInterval = hours;
    }

    /**
     * Get the timeout interval of this state
     * @return timeoutInterval interval in hours (as a String)
     */
    public String castor_getTimeoutInterval()
    {
        if ( timeoutInterval != -1 )
            return String.valueOf(timeoutInterval);
        else
            return null;
    }

    /**
     * Set the timeout interval of this state
     * @param timeoutInterval interval in hours
     */
    public void castor_setTimeoutInterval(String timeoutInterval)
    {
        try
        {
            this.timeoutInterval = (Integer.valueOf(timeoutInterval)).intValue();
        }
        catch (NumberFormatException e)
        {
            this.timeoutInterval = -1;
        }
    }

    /**
     * Get the timeout action of this state
     * Action that will played if timeout is triggered
     * @return timeout action
     */
    public Action getTimeoutAction()
    {
        return timeoutAction;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#setTimeoutAction(com.silverpeas.workflow.api.model.Action)
     */
    public void setTimeoutAction(Action timeoutAction) 
    {
        this.timeoutAction = timeoutAction;
    }

    /**
     * Get flag for admin notification
     * if true, the timeout manager will send a notification to all supervisors
     * @return admin notification flag
     */
    public boolean getTimeoutNotifyAdmin()
    {
        return timeoutNotifyAdmin;
    }

    /*
     * (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.State#setTimeoutNotifyAdmin(boolean)
     */
    public void setTimeoutNotifyAdmin(boolean timeoutAction)
    {
        this.timeoutNotifyAdmin = timeoutAction;
    }

    /**
     * Get the unique key, used by equals method
     * @return    unique key 
     */
    public String getKey()
    {
        return (this.name);
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