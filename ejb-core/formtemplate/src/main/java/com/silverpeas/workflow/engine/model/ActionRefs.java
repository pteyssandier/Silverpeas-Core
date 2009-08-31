package com.silverpeas.workflow.engine.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import com.silverpeas.workflow.api.WorkflowException;
import com.silverpeas.workflow.api.model.Action;
import com.silverpeas.workflow.api.model.AllowedAction;
import com.silverpeas.workflow.api.model.AllowedActions;

/**
 * Class implementing the representation of the &lt;allowedActions&gt; element of a Process Model.
**/
public class ActionRefs implements Serializable, AllowedActions
{
    private Vector actionRefList;

	/**
	 * Constructor
	 */
    public ActionRefs() 
	{
        actionRefList = new Vector();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#addAllowedAction(com.silverpeas.workflow.api.model.AllowedAction)
     */
    public void addAllowedAction(AllowedAction allowedAction)
    {
        actionRefList.add( allowedAction );
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#createAllowedAction()
     */
    public AllowedAction createAllowedAction()
    {
        return new ActionRef();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#iterateAllowedAction()
     */
    public Iterator iterateAllowedAction()
    {
        return actionRefList.iterator();
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#getAllowedActions()
     */
    public Action[] getAllowedActions()
    {
        Action[] result = null;

        if (actionRefList == null)
            return null;

        // construct the Action array
        result = new ActionImpl[actionRefList.size()];
        for (int i=0; i<actionRefList.size(); i++)
        {
            result[i] = ((AllowedAction) actionRefList.get(i)).getAction();
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#getAllowedAction(java.lang.String)
     */
    public AllowedAction getAllowedAction(String strActionName)
    {
        AllowedAction allowedAction = new ActionRef();
        Action        action        = new ActionImpl();
        int           idx;
        
        action.setName(strActionName);
        allowedAction.setAction(action);
        
        idx = actionRefList.indexOf(allowedAction);
        
        if ( idx >= 0 )
            return (AllowedAction)actionRefList.get(idx);
        else
            return null;
    }

    /* (non-Javadoc)
     * @see com.silverpeas.workflow.api.model.AllowedActions#removeAllowedAction(java.lang.String)
     */
    public void removeAllowedAction(String strAllowedActionName) throws WorkflowException
    {
        AllowedAction actionRef = createAllowedAction();
        Action        action    = new ActionImpl();
        
        action.setName(strAllowedActionName);
        actionRef.setAction(action);
        if ( !actionRefList.remove(actionRef) )
            throw new WorkflowException("ActionRefs.removeAllowedAction(String)",
                    "workflowEngine.EX_ALLOWED_ACTION_NOT_FOUND", 
                    strAllowedActionName == null
                        ? "<null>" 
                        : strAllowedActionName );
    }
}