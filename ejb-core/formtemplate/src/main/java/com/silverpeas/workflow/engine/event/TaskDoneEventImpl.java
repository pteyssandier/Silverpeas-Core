package com.silverpeas.workflow.engine.event;

import java.util.Date;
import com.silverpeas.workflow.api.task.Task;
import com.silverpeas.workflow.api.event.TaskDoneEvent;
import com.silverpeas.workflow.api.user.User;
import com.silverpeas.workflow.api.instance.ProcessInstance;
import com.silverpeas.workflow.api.model.ProcessModel;
import com.silverpeas.workflow.api.model.State;
import com.silverpeas.form.DataRecord;

/**
 * A TaskDoneEvent object is the description of a done activity.
 *
 * Those descriptions are sent to the workflow engine
 * by the workflow tools when
 * the user has done a task in a process instance.
 */
public class TaskDoneEventImpl implements TaskDoneEvent
{
    /**
	 * A TaskDoneEventImpl is built
	 * from a resolved task, a choosen action and a filled form.
	 */
	public TaskDoneEventImpl(Task resolvedTask,
							 String actionName,
							 DataRecord data)
    {
	   this.user = resolvedTask.getUser();
	   this.processModel = resolvedTask.getProcessModel();
	   this.processInstance = resolvedTask.getProcessInstance();
	   this.resolvedState = resolvedTask.getState();
	   this.actionName = actionName;
	   this.actionDate = new Date();
	   this.userRoleName = resolvedTask.getUserRoleName();
	   this.data = data;
	}

    /**
	 * Returns the actor.
	 */
    public User getUser()
	{
	   return user;
	}

	/**
	 * Returns the process model (peas).
	 *
	 * Must be not null when the task is an instance creation.
	 */
    public ProcessModel getProcessModel()
	{
	   return processModel;
	}

	/**
	 * Returns the process instance.
	 *
	 * Returns null when the task is an instance creation.
	 */
    public ProcessInstance getProcessInstance()
	{
	   return processInstance;
	}

   /**
	 * Set the process instance (when created).
	 */
	 public void setProcessInstance(ProcessInstance processInstance)
	 {
	    this.processInstance = processInstance;
	 }
	/**
	 * Returns the state/activity resolved by the user.
	 */
	public State getResolvedState()
	{
	   return resolvedState;
	}

	/**
	 * Returns the name of the action chosen to resolve the activity.
	 */
	public String getActionName()
	{
	   return actionName;
	}

	/**
	 * Returns the action date.
	 */
	public Date getActionDate()
	{
	   return actionDate;
	}

    /**
	 * Returns the data filled when the action was processed.
	 */
	public DataRecord getDataRecord()
	{
	   return data;
	}

	/**
	 * Returns the role name of the actor
	 */
	public String getUserRoleName()
	{
		return userRoleName;
	}

	/**
	 * Internal states.
	 */
    private User user = null;
    private ProcessModel processModel = null;
    private ProcessInstance processInstance = null;
    private State resolvedState = null;
    private Date actionDate = null;
    private String actionName = null;
    private String userRoleName = null;
	 private DataRecord data = null;
}