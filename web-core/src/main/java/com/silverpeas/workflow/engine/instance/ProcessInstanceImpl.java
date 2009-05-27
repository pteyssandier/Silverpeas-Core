package com.silverpeas.workflow.engine.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.QueryResults;

import com.silverpeas.form.DataRecord;
import com.silverpeas.form.DataRecordUtil;
import com.silverpeas.form.Field;
import com.silverpeas.form.FormException;
import com.silverpeas.form.RecordSet;
import com.silverpeas.workflow.api.ProcessModelManager;
import com.silverpeas.workflow.api.UserManager;
import com.silverpeas.workflow.api.Workflow;
import com.silverpeas.workflow.api.WorkflowException;
import com.silverpeas.workflow.api.instance.Actor;
import com.silverpeas.workflow.api.instance.HistoryStep;
import com.silverpeas.workflow.api.instance.Participant;
import com.silverpeas.workflow.api.instance.ProcessInstance;
import com.silverpeas.workflow.api.instance.Question;
import com.silverpeas.workflow.api.instance.UpdatableProcessInstance;
import com.silverpeas.workflow.api.model.Form;
import com.silverpeas.workflow.api.model.Input;
import com.silverpeas.workflow.api.model.Presentation;
import com.silverpeas.workflow.api.model.ProcessModel;
import com.silverpeas.workflow.api.model.QualifiedUsers;
import com.silverpeas.workflow.api.model.RelatedUser;
import com.silverpeas.workflow.api.model.State;
import com.silverpeas.workflow.api.model.UserInRole;
import com.silverpeas.workflow.api.user.User;
import com.silverpeas.workflow.engine.WorkflowHub;
import com.silverpeas.workflow.engine.dataRecord.ProcessInstanceDataRecord;
import com.silverpeas.workflow.engine.dataRecord.ProcessInstanceRowRecord;
import com.silverpeas.workflow.engine.jdo.WorkflowJDOManager;
import com.stratelia.silverpeas.silvertrace.SilverTrace;


/**
 * This class is one implementation of interface UpdatableProcessInstance.
 * It uses Castor library to read/write process instance information in database
 * 
 * @table SB_Workflow_ProcessInstance
 * @key-generator MAX
 */
public class ProcessInstanceImpl implements UpdatableProcessInstance //, TimeStampable
{
	/**
	 * Abstract process model
	 */
	private transient ProcessModel model = null;

	/**
	 * Flag that indicates validity of this processInstance
	 */
	private transient boolean valid	= false;

	/**
	 * Flag that indicates if this instance is locked by admin
	 * @field-name locked
	 * @get-method isLockedByAdmin
	 * @set-method setLockedByAdmin
	 */
	private boolean locked	= false;

	/**
	 * Flag that indicates if this instance status is "error"
	 * @field-name errorStatus
	 */
	private boolean errorStatus	= false;

	/**
	 * Flag that indicates if this instance is in an active state for a long long time
	 * @field-name timeoutStatus
	 */
	private boolean timeoutStatus	= false;

	/**
	 * the instance Id 
	 * @field-name instanceId
	 * @sql-type integer
	 * @primary-key
	 */
	private String instanceId = null;

	/**
	 * the model Id 
	 * @field-name modelId
	 */
	private String modelId = null;

	/**
	 * Vector of all history step that trace events occured on this process instance
	 * @field-name historySteps
	 * @field-type com.silverpeas.workflow.engine.instance.HistoryStepImpl
	 * @many-key instanceId
	 * @set-method	castor_setHistorySteps
	 * @get-method	castor_getHistorySteps
	 */
	private Vector historySteps	= null;

	/**
	 * Vector of all questions asked on this process instance
	 * @field-name questions
	 * @field-type com.silverpeas.workflow.engine.instance.QuestionImpl
	 * @many-key instanceId
	 * @set-method	castor_setQuestions
	 * @get-method	castor_getQuestions
	 */
	private Vector questions = null;

	/**
	 * The current history step
	 * used to add actomic operations in history
	 */
	private transient HistoryStep currentStep = null;

	/**
	 * the status of this instance regarding 'undo' process
	 * while true, the atomic operations are not stored anymore
	 */
	private transient boolean inUndoProcess = false;

	/**
	 * Vector of all users who can see this process instance
	 * @field-name interestedUsers
	 * @field-type com.silverpeas.workflow.engine.instance.InterestedUser
	 * @many-key instanceId
	 * @set-method	castor_setInterestedUsers
	 * @get-method	castor_getInterestedUsers
	 */
	private Vector interestedUsers = null;

	/**
	 * Vector of all users who can act on this process instance
	 * @field-name workingUsers
	 * @field-type com.silverpeas.workflow.engine.instance.WorkingUser
	 * @set-method	castor_setWorkingUsers
	 * @get-method	castor_getWorkingUsers
	 */
	private Vector workingUsers = null;

	/**
	 * Vector of all users who can have locked a state of this process instance
	 * @field-name lockingUsers
	 * @field-type com.silverpeas.workflow.engine.instance.LockingUser
	 * @many-key instanceId
	 * @set-method	castor_setLockingUsers
	 * @get-method	castor_getLockingUsers
	 */
	private Vector lockingUsers = null;

	/**
	 * Vector of all states that are due to be resolved for this process instance
	 * @field-name activeStates
	 * @field-type com.silverpeas.workflow.engine.instance.ActiveState
	 * @many-key instanceId
	 * @set-method	castor_setActiveStates
	 * @get-method	castor_getActiveStates
	 */
	private Vector activeStates = null;

   /**
	 * The DataRecord where are stored all the folder fields.
	 */
	private transient DataRecord folder = null;

   /**
	 * A Map action -> DataRecord
	 */
	private transient Map actionData = null;

	/**
	 * Default constructor
	 */
	public ProcessInstanceImpl() 
	{
		historySteps	= new Vector();
		questions		= new Vector();
		interestedUsers = new Vector();
		workingUsers	= new Vector();
		activeStates	= new Vector();
		lockingUsers	= new Vector();
		valid			   = false;
		folder         = null;
		actionData    = null;
	}

	/**
	 * Get the workflow instance id
	 * @return	instance id
	 */
	public String getInstanceId()
	{
		return instanceId;
	}

	/**
	 * Set the workflow instance id
	 * @param	instanceId	instance id
	 */
	public void setInstanceId(String instanceId)
	{
		this.instanceId = instanceId;
	}

	/**
	 * Get the workflow model id
	 * @return	model id
	 */
	public String getModelId()
	{
		return modelId;
	}

	/**
	 * Set the workflow model id
	 * @param	instanceId	model id
	 */
	public void setModelId(String modelId)
	{
		this.modelId = modelId;
	}

	/**
	 * Add an history step for this instance
	 * @param	step	the history step to add
	 */
	public void addHistoryStep(HistoryStep step) throws WorkflowException
	{
		((HistoryStepImpl) step).setProcessInstance( (ProcessInstance) this );
		historySteps.add(step);

		this.currentStep = step;
	}
   
	/**
	 * Update an history step for this instance
	 * @param	step	the history step to update
	 */
	public void updateHistoryStep(HistoryStep step) throws WorkflowException
	{
		this.currentStep = step;
	}

	/**
	 * Set a state active for this instance
	 * @param	state	State to be activated
	 */
	public void addActiveState(State state) throws WorkflowException
	{
		this.addActiveState(state.getName());
	}


	/**
	 * Set a state active for this instance
	 * @param	state	The name of state to be activated
	 */
	private void addActiveState(String state) throws WorkflowException
	{
		ActiveState activeState = new ActiveState(state);
		activeState.setProcessInstance( this );

		// if this active state is add in a "question" context, it must be marked as in back status for a special treatment
		if (this.currentStep!=null && this.currentStep.getAction().equals("#question#"))
			activeState.setBackStatus(true);

		// if this state wasn't already active, add it in list of active states
		if (!activeStates.contains(activeState))
			activeStates.add(activeState);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("addActiveState", state);
	}

	/**
	 * Set a state inactive for this instance
	 * @param	state	State to be desactivated
	 */
	public void removeActiveState(State state) throws WorkflowException
	{
		this.removeActiveState(state.getName());
	}

	/**
	 * Set a state inactive for this instance
	 * @param	state	The name of state to be desactivated
	 */
	private void removeActiveState(String state) throws WorkflowException
	{
		ActiveState activeState = new ActiveState(state);

		// try to find and delete the right active state
		activeStates.remove(activeState);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("removeActiveState", state);
	}
   
	/**
	 * @param state
	 */
	public void addTimeout(State state) throws WorkflowException
	{
		ActiveState activeState = null;
		boolean found = false;

		if (activeStates==null || activeStates.size()==0)
			return;

		for (int i=0; (!found) && i<activeStates.size(); i++)
		{
			activeState = (ActiveState) activeStates.get(i);
			if ( activeState.getState().equals( state.getName() ) )
			{
				found = true;
				activeState.setTimeoutStatus(true);
				this.setTimeoutStatus(true);
			}
		}
	}

	/**
	 * @param state
	 */
	public void removeTimeout(State state) throws WorkflowException
	{
		SilverTrace.debug("workflowEngine", "ProcessInstanceImpl.removeTimeout", "root.MSG_GEN_ENTER_METHOD", "state =" + state.getName());
		ActiveState activeState = null;
		boolean found = false;

		if (activeStates==null || activeStates.size()==0)
			return;

		for (int i=0; i<activeStates.size(); i++)
		{
			activeState = (ActiveState) activeStates.get(i);
			SilverTrace.debug("workflowEngine", "ProcessInstanceImpl.removeTimeout", "root.MSG_GEN_ENTER_METHOD", "activeState =" + activeState.getState());
			if ( activeState.getState().equals( state.getName() ) )
			{
				activeState.setTimeoutStatus(false);
			}
			else if ( activeState.getTimeoutStatus() )
				found = true;
		}

		if (!found)
			this.setTimeoutStatus(false);
	}

	/**
	 * Add an user in the working user list
	 * @param	user	user to add
	 * @param	state	state for which the user can make an action
	 * @param	role	role name under which the user can make an action
	 */
	public void addWorkingUser(User user, State state, String role) throws WorkflowException
	{
		this.addWorkingUser(user, state.getName(), role);
	}

	/**
	 * Add an user in the working user list
	 * @param	user	user to add
	 * @param	state	name of state for which the user can make an action
	 * @param	role	role name under which the user can make an action
	 */
	private void addWorkingUser(User user, String state, String role) throws WorkflowException
	{
		WorkingUser wkUser = new WorkingUser();
		wkUser.setUserId( user.getUserId() );
		wkUser.setState( state );
		wkUser.setRole( role );
		wkUser.setProcessInstance( this );
		if (!workingUsers.contains(wkUser))
					workingUsers.add(wkUser);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("addWorkingUser", user.getUserId() + "##" + state + "##" + role);
	}
   
	/**
	 * Remove an user from the working user list
	 * @param	user	user to remove
	 * @param	state	state for which the user could make an action
	 * @param	role	role name under which the user could make an action
	 */
	public void removeWorkingUser(User user, State state, String role) throws WorkflowException
	{
		this.removeWorkingUser(user, state.getName(), role);
	}
	
	/**
	 * Remove an user from the working user list
	 * @param	user	user to remove
	 * @param	state	name of state for which the user could make an action
	 * @param	role	role name under which the user could make an action
	 */
	private void removeWorkingUser(User user, String state, String role) throws WorkflowException
	{
		WorkingUser userToDelete = null;

		// Build virtual working user to find the true one end delete it
		userToDelete = new WorkingUser();
		userToDelete.setUserId(user.getUserId());
		userToDelete.setState(state);
		userToDelete.setRole(role);

		// try to find and delete the right working user
		workingUsers.remove(userToDelete);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("removeWorkingUser", user.getUserId() + "##" + state + "##" + role);
	}

	/**
	 * Add an user in the interested user list
	 * @param	user	user to add
	 * @param	state	state for which the user is interested
	 * @param	role	role name under which the user is interested
	 */
	public void addInterestedUser(User user, State state, String role) throws WorkflowException
	{
		this.addInterestedUser(user, state.getName(),  role);
	}
	
	/**
	 * Add an user in the interested user list
	 * @param	user	user to add
	 * @param	state	the name of state for which the user is interested
	 * @param	role	role name under which the user is interested
	 */
	private void addInterestedUser(User user, String state, String role) throws WorkflowException
	{
		InterestedUser intUser = new InterestedUser();
		intUser.setUserId( user.getUserId() );
		intUser.setState( state );
		intUser.setRole( role );
		intUser.setProcessInstance( this );
		if (!interestedUsers.contains(intUser))
			interestedUsers.add(intUser);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("addInterestedUser", user.getUserId() + "##" + state + "##" + role);
	}
   
	/**
	 * Remove an user from the interested user list
	 * @param	user	user to remove
	 * @param	state	state for which the user is interested
	 * @param	role	role name under which the user is interested
	 */
	public void removeInterestedUser(User user, State state, String role) throws WorkflowException
	{
		this.removeInterestedUser(user, state.getName(), role);
	}

	/**
	 * Remove an user from the interested user list
	 * @param	user	user to remove
	 * @param	state	the name of state for which the user is interested
	 * @param	role	role name under which the user is interested
	 */
	private void removeInterestedUser(User user, String state, String role) throws WorkflowException
	{
		InterestedUser userToDelete = null;

		// Build virtual interestedUser user to find the true one end delete it
		userToDelete = new InterestedUser();
		userToDelete.setUserId(user.getUserId());
		userToDelete.setState(state);
		userToDelete.setRole(role);

		// try to find and delete the right interestedUser user
		interestedUsers.remove(userToDelete);

		// add this operation in undo history
		if (!inUndoProcess)
			this.addUndoHistoryStep("removeInterestedUser", user.getUserId() + "##" + state + "##" + role);
	}

	/**
	 * Add a question for this instance
	 * @param	question	the question to add
	 */
	public void addQuestion(Question question) throws WorkflowException
	{
		questions.add(question);
	}

	/**
	 */
	public void computeValid() 
	{
		this.valid = ( workingUsers.size() > 0 );
	}

	/**
	* @return ProcessModel
	*/
	public ProcessModel getProcessModel() 
		throws WorkflowException
	{
		if (model == null)
		{
			ProcessModelManager modelManager = Workflow.getProcessModelManager();
			model = modelManager.getProcessModel(modelId);
		}
		return model;
	}

	/**
	 * Creates this instance in database
	 * @return the newly created instance id
	 */
	public String create() 
		throws WorkflowException
	{
		Database db = null;
		try
		{
			db = WorkflowJDOManager.getDatabase();
			synchronized (db)
			{
				db.begin();
				db.create(this);
				db.commit();
			}
		
			return this.getInstanceId();
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceImpl.create", "workflowEngine.EX_ERR_CASTOR_CREATE_INSTANCE", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}
	}

	/**
	 * Permanently removes this instance from database
	 */
	public void delete() 
		throws WorkflowException
	{
		Database db = null;
		try
		{
			db = WorkflowJDOManager.getDatabase();
			synchronized (db)
			{
				db.begin();
				db.remove(this);
				db.commit();
			}
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceImpl.delete", "workflowEngine.EX_ERR_CASTOR_DELETE_INSTANCE", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}
	}

	/**
	 * Store modifications of this instance in database
	 */
	public void update() 
		throws WorkflowException
	{
		Database db = null;
		try
		{
			db = WorkflowJDOManager.getDatabase();
			synchronized (db)
			{
				db.begin();
				db.update(this);
				db.commit();
			}
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceImpl.update", "workflowEngine.EX_ERR_CASTOR_UPDATE_INSTANCE", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}
	}

	/**
	* @return HistoryStep[]
	*/
	public HistoryStep[] getHistorySteps() 
	{
		if (historySteps != null)
		{
			Collections.sort(historySteps);
			return (HistoryStep[]) historySteps.toArray(new HistoryStep[0]);
		}
		else
			return null;
	}

	/**
	 * @return HistoryStep
	 */
	public HistoryStep getHistoryStep(String stepId) throws WorkflowException
	{
		for (int i=0; i<historySteps.size(); i++)
		{
			if ( ((HistoryStep) historySteps.get(i)).getId().equals(stepId) )
				return (HistoryStep) historySteps.get(i);
		}

		throw new WorkflowException("ProcessInstanceImpl.getHistoryStep", "workflowEngine.EX_ERR_HISTORYSTEP_NOT_FOUND");
	}

	/**
	* @return Vector
	*/
	public Vector getParticipants() 
		throws WorkflowException
	{
		Vector participants = new Vector();
		HistoryStepImpl	step = null;
		User user = null;
		State state = null;

		for ( int i=0; i<historySteps.size(); i++ ) 
		{
			step = (HistoryStepImpl) historySteps.get(i);
			try
			{
				user = WorkflowHub.getUserManager().getUser( step.getUserId() );
			}
			catch (WorkflowException we)
			{
				user = null;
			}

			if (step.getResolvedState() == null)
				state = null;
			else
				state = this.getProcessModel().getState(step.getResolvedState());
			ParticipantImpl participant = new ParticipantImpl(user, step.getUserRoleName(), state, step.getAction());
			participants.add(participant);
		}
		return participants;
	}

	/**
	 * Get the last user who resolved the given state
	 * @param	resolvedState	the resolved state
	 * @return this user as a Participant object
	 */
	public Participant getParticipant(String resolvedState) 
		throws WorkflowException
	{
		HistoryStep			step = null;
		User				user = null;
		State				state = null;

		// Get the most recent step
		step = this.getMostRecentStepOnState(resolvedState);

		// Get the user who worked at this step
		try
		{
			user = step.getUser();
		}
		catch (WorkflowException we)
		{
			user = null;
		}

		// Get the state 
		if (step.getResolvedState() == null)
			state = null;
		else
			state = this.getProcessModel().getState(step.getResolvedState());

		// return the participant
		return (Participant) (new ParticipantImpl(user, step.getUserRoleName(), state, step.getAction()));
	}

   /**
	 * Returns the folder as a DataRecord
	 */
	public DataRecord getFolder() throws WorkflowException
	{
	   if (folder == null)
		{
			String folderId = instanceId ;

		   try
			{
		      RecordSet folderSet = getProcessModel().getFolderRecordSet();
			   folder = folderSet.getRecord(folderId);

				if (folder == null) createFolder();
			}
			catch (FormException e)
			{
		      throw new WorkflowException("ProcessInstanceImpl",
			   	"workflowEngine.EXP_UNKNOWN_FOLDER",
					"folder="+folderId, e);
			}
		}

		return folder;
	}

   /**
	 * Returns a data record with all the accessible data in this instance.
	 */
	public DataRecord getAllDataRecord(String role, String lang)
	   throws WorkflowException
	{
	   return new ProcessInstanceDataRecord(this, role, lang);
	}

   /**
	 * Returns a data record with all the main data in this instance.
	 */
	public DataRecord getRowDataRecord(String role, String lang)
	   throws WorkflowException
	{
	   return new ProcessInstanceRowRecord(this, role, lang);
	}

   /**
	 * Creates a new empty folder.
	 */
	private void createFolder() throws WorkflowException
	{
		String folderId = instanceId ;

		try
		{

		   RecordSet folderSet = getProcessModel().getFolderRecordSet();
		   folder = folderSet.getEmptyRecord();
			folder.setId(folderId);
		}
		catch (FormException e)
		{
	      throw new WorkflowException("ProcessInstanceImpl",
		   	"workflowEngine.EXP_FOLDER_CREATE_FAILED",
				"folder="+folderId, e);
		}
	}

   /**
	 * Updates the folder with the data filled within an action.
	 */
	private void updateFolder(DataRecord actionData) throws WorkflowException
	{
		try
		{
		   RecordSet folderSet = getProcessModel().getFolderRecordSet();

         String fieldNames[] = folderSet.getRecordTemplate().getFieldNames();
			Field updatedField = null;

			for (int i=0; i<fieldNames.length ; i++)
			{
			   try
				{
				   updatedField = actionData.getField(fieldNames[i]);
				   if (updatedField==null)
					   continue;
				}
				catch (FormException e)
				{
				   // the field i is not updated (unknown in the action context)
					continue;
				}

				setField(fieldNames[i], updatedField);
	      }

		   folderSet.save(getFolder());
		}
		catch (FormException e)
		{
	      throw new WorkflowException("ProcessInstanceImpl",
		   	"workflowEngine.EXP_FOLDER_UPDATE_FAILED",
				"folder="+instanceId, e);
		}
	}


   /**
	 * Returns the required field from the folder.
	 */
	public Field getField(String fieldName) throws WorkflowException
	{
		DataRecord folder = getFolder();
		if (folder==null)
			throw new WorkflowException("ProcessInstanceImpl.getField", "workflowEngine.EX_ERR_GET_FOLDER");

		try
		{
			Field returnedField = folder.getField(fieldName);
			if (returnedField==null)
				throw new WorkflowException("ProcessInstanceImpl.getField",
				"workflowEngine.EXP_UNKNOWN_ITEM", "folder."+fieldName);
			return folder.getField(fieldName);
		}
		catch (FormException e)
		{
			throw new WorkflowException("ProcessInstanceImpl.getField",
				"workflowEngine.EXP_UNKNOWN_ITEM", "folder."+fieldName, e);
		}
	}

	/**
	 * Update the named field with the value of the given field.
	 */
	public void setField(String fieldName, Field copiedField)
	   throws WorkflowException
	{
	   Field updatedField = getField(fieldName);

      try
		{
		    if (updatedField.getTypeName().equals(copiedField.getTypeName()))
			 {
			    updatedField.setObjectValue(copiedField.getObjectValue());
			 }
			 else
			 {
			    updatedField.setValue(copiedField.getValue(""), "");
			 }
		}
		catch (FormException e)
		{
		   throw new WorkflowException("ProcessInstanceImpl",
				"workflowEngine.EXP_ITEM_UPDATE_FAILED", "folder."+fieldName, e);
		}
	}

	/**
	 * Get the data associated to the given action
	 * @param	actionName	action name
	 */
	public DataRecord getActionRecord(String actionName) throws WorkflowException
	{
	   if (actionData == null) actionData = new HashMap();

	   DataRecord data = (DataRecord) actionData.get(actionName); 
	   if (data == null)
		{
		   HistoryStep step = getMostRecentStep(actionName);
			if (step != null)
			{
			   data = step.getActionRecord();
				if (data == null) return null;
				actionData.put(actionName, data);
			}
		}

		return data;
	}

 	/**
	 * @return DataRecord
	 */
	public DataRecord getFormRecord(String formName, String role, String lang)
	   throws WorkflowException
	{
		try
		{
			Form form = getProcessModel().getForm(formName, role);
			if (form == null) return null;

			String[] fieldNames = form.toRecordTemplate(role, lang).getFieldNames();
			DataRecord data = form.getDefaultRecord(
			   role, lang, getAllDataRecord(role,lang));
			DataRecordUtil.updateFields(fieldNames, data, getFolder());

			return data;
		}
		catch (FormException e)
		{
	      throw new WorkflowException("ProcessInstanceImpl",
		   	"workflowEngine.EXP_FORM_READ_FAILED",
				formName, e);
		}
	}


	/**
	 * Get a new data record associated to the given action
	 * @param	actionName	action name
	 */
	public DataRecord getNewActionRecord(String actionName)
	   throws WorkflowException
	{
		try
		{
		   Form form = getProcessModel().getActionForm(actionName);
			if (form == null) return null;
		   //RecordSet formSet = getProcessModel().getFormRecordSet(form.getName());

		   DataRecord data = getProcessModel().getNewActionRecord(actionName,
																  "", "",
																  this.getAllDataRecord("",""));

         //String[] fieldNames = formSet.getRecordTemplate().getFieldNames();
		   Input[] inputs = form.getInputs();
		   List fNames = new ArrayList(); 
		   for (int i=0; inputs != null && i<inputs.length; i++)
		   {
			   if (inputs[i] != null  && inputs[i].getItem() != null)
				   fNames.add(inputs[i].getItem().getName());
		   }
         DataRecordUtil.updateFields((String[]) fNames.toArray(new String[0]), data, getFolder());

			return data;
		}
		catch (FormException e)
		{
	      throw new WorkflowException("ProcessInstanceImpl",
		   	"workflowEngine.EXP_FORM_CREATE_FAILED",
				"action="+actionName, e);
		}
	}

	/**
	 * Set the form associated to the given action
	 * @param	actionName	action name
	 */
	public void saveActionRecord(HistoryStep step, DataRecord actionData)
	   throws WorkflowException
	{
	   updateFolder(actionData);
		step.setActionRecord(actionData);
	}

   /**
	 * Returns the most recent step where this action was performed.
	 */
	public HistoryStep getMostRecentStep(String actionName)
	{
		Date			actionDate = null;
		HistoryStep		mostRecentStep = null;
		HistoryStep		step = null;

		for ( int i=0; i<historySteps.size(); i++ ) 
		{
			step = (HistoryStep) historySteps.get(i);

			// if step matches the searched action, tests if the step is most recent
			if (step.getAction().equals(actionName))
			{
				// choose this step, if no previous step found or action date is more recent
				if (mostRecentStep==null || step.getActionDate().after(actionDate))
				{
					mostRecentStep = step;
					actionDate = step.getActionDate();
				}
			}
		}

		return mostRecentStep;
	}

	/**
	 * Get the most recent step where an action has been performed on the given state.
	 * If no action has been performed on this state, return the step that activate this state.
	 */
	public HistoryStep getMostRecentStep(State state)
	{
		try
		{
			if (state==null) return null;

			HistoryStep mostRecentStep = getMostRecentStepOnState(state.getName());

			if (mostRecentStep != null)
				return mostRecentStep;

			return getOriginStep(state.getName());
		}
		catch (WorkflowException we)
		{
			return null;
		}
	}

   /**
	 * Returns the most recent step where an action was performed on the given state.
	 * @param	stateName	name of state for which we want the most recent step
	 * @return	the most recent step
	 */
	private HistoryStep getMostRecentStepOnState(String stateName)
	{
		HistoryStepImpl		step = null;
		HistoryStepImpl		mostRecentStep = null;
		Date				actionDate = null;
		boolean				stepMatch = false;

		for ( int i=0; i<historySteps.size(); i++ ) 
		{
			stepMatch = false;
			step = (HistoryStepImpl) historySteps.get(i);

			// special case : searched stateName is null or empty (the step is representing the creation)
			if (stateName==null || stateName.length()==0)
			{
				if (step.getResolvedState()==null || step.getResolvedState().length()==0)
				{
					stepMatch = true;
				}
			}
			else if (step.getResolvedState()!=null && step.getResolvedState().equals(stateName))
			{
				stepMatch = true;
			}

			// if step matches the searched state, tests if the step is most recent
			if (stepMatch)
			{
				// choose this step, if no previous step found or action date is more recent
				if (mostRecentStep==null || step.getActionDate().after(actionDate))
				{
					mostRecentStep = step;
					actionDate = step.getActionDate();
				}
			}
		}

		return mostRecentStep;
	}

   /**
	 * Returns the most recent step where an action caused the activation of the given state
	 * @param	stateName	name of state 
	 * @return	the most recent step where an action caused the activation of the given state
	 */
	private HistoryStep getOriginStep(String stateName) throws WorkflowException
	{
		OQLQuery		query 	= null;
		QueryResults	results;
		String 			stepId 	= null;
		Database 		db 		= null;
		try
		{
			// Constructs the query
			db = WorkflowJDOManager.getDatabase();
			db.begin();
			query = db.getOQLQuery( "SELECT undoStep FROM com.silverpeas.workflow.engine.instance.UndoHistoryStep undoStep "
									+ "WHERE undoStep.instanceId = $1 "
									+ "AND undoStep.action = \"addActiveState\" "
									+ "AND undoStep.parameters = $2");

			// Execute the query
			query.bind( ( Integer.parseInt( instanceId ) ) );
			query.bind( stateName );
			results = query.execute();

			while ( results.hasMore() ) 
			{
				UndoHistoryStep	undoStep = (UndoHistoryStep) results.next();
				stepId = undoStep.getStepId();
			}

			db.commit();
		}
		catch (WorkflowException we)
		{
			throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_UNDO_STEP", we);
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceManagerImpl.getProcessInstances", "workflowEngine.EX_ERR_CASTOR_UNDO_STEP", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}

		if (stepId != null)
			return getHistoryStep(stepId);

		else return null;
	}

	/**
	* @return String[]
	*/
	public String[] getActiveStates()
	{
		String[] states = null;

		if (activeStates == null || activeStates.size() == 0)
			return new String[0];

		states = new String[activeStates.size()];
		for (int i=0; i<activeStates.size(); i++)
		{
			states[i] = ( (ActiveState) activeStates.get(i)).getState();
		}

		return states;
	}

	/**
	 * Test is a active state is in back status
	 * @param	stateName	name of active state
	 * @return true if resolution of active state involves a cancel of actions
	 */
	public boolean isStateInBackStatus(String stateName)
	{
		boolean result = false;

		for (int i=0; i<activeStates.size(); i++)
		{
			if ( (( (ActiveState) activeStates.get(i)).getState()).equals(stateName)
				 && ( (ActiveState) activeStates.get(i)).getBackStatus()==true)
				result = true;
		}

		return result;
	}

	/**
	* @return Actor[]
	*/
	public Actor[] getWorkingUsers() throws WorkflowException
	{
		Vector actors = new Vector();

		for (int i=0; i<workingUsers.size(); i++)
		{
			WorkingUser wkUser = (WorkingUser) workingUsers.get(i);
			actors.add( wkUser.toActor() );
		} 

		return (Actor[]) actors.toArray(new Actor[0]);
	}

	/**
	* @param state
	* @return Actor[]
	*/
	public Actor[] getWorkingUsers(String state) throws WorkflowException
	{
		Vector actors = new Vector();

	    for (int i=0; i<workingUsers.size() ; i++)
		{
			WorkingUser wkUser = (WorkingUser) workingUsers.get(i);
			if (wkUser.getState().equals(state))
				actors.add( wkUser.toActor() );
		} 

		return (Actor[]) actors.toArray(new Actor[0]);
	}

	/**
	* @param state
	* @return Actor[]
	*/
	public Actor[] getWorkingUsers(String state, String role) throws WorkflowException
	{
		Vector actors = new Vector();

	    for (int i=0; i<workingUsers.size() ; i++)
		{
			WorkingUser wkUser = (WorkingUser) workingUsers.get(i);
			if (wkUser.getState().equals(state) && wkUser.getRole().equals(role))
				actors.add( wkUser.toActor() );
		} 

		return (Actor[]) actors.toArray(new Actor[0]);
	}

	/**
	* Returns all the state name assigned to the user.
	*/
	public String[] getAssignedStates(User user, String roleName) throws WorkflowException
	{
		Vector stateNames = new Vector();
		String userId = user.getUserId();

	    for (int i=0; i<workingUsers.size() ; i++)
		{
			WorkingUser wkUser = (WorkingUser) workingUsers.get(i);
			if (wkUser.getUserId().equals(userId) && wkUser.getRole().equals(roleName))
				stateNames.add( wkUser.getState() );
		} 

		return (String[]) stateNames.toArray(new String[0]);
	}

	/**
	* @param state
	* @return User
	*/
	public User getLockingUser(String state) 
		throws WorkflowException
	{
		// Constructs a new LockingUser to proceed search
		LockingUser searchedUser = new LockingUser();
		searchedUser.setState(state);

		int indexUser = lockingUsers.indexOf(searchedUser);
		if (indexUser != -1)
		{
			LockingUser foundUser = (LockingUser) lockingUsers.get(indexUser);
				return WorkflowHub.getUserManager().getUser(foundUser.getUserId());
		}
		else
			return null;
	}

	/**
	 * Locks this instance for the given instance and state
	 * @param	state	state that have to be locked
	 * @param	user	the locking user
	 */
	public void lock(State state, User user) 
		throws WorkflowException
	{
		this.lock(state.getName(), user);
	}

	/**
	 * Locks this instance for the given instance and state
	 * @param	state	state that have to be locked
	 * @param	user	the locking user
	 */
	private void lock(String state, User user) 
		throws WorkflowException
	{
		// Test if lock already exists
		LockingUser searchedUser = new LockingUser();
		LockingUser foundUser = null;
		searchedUser.setState(state);
		searchedUser.setUserId(user.getUserId());
		searchedUser.setProcessInstance(this);

		int indexUser = lockingUsers.indexOf(searchedUser);
		if (indexUser != -1)
			foundUser = (LockingUser) lockingUsers.get(indexUser);

		if (foundUser != null)
		{
			// if lock found for this state,
			// test if user is the same as requested
			if ( ! foundUser.getUserId().equals( user.getUserId() ) )
				throw new WorkflowException("ProcessInstanceImpl.lock", "workflowEngine.EX_ERR_INSTANCE_LOCKED_BY_ANOTHER_PERSON");
			else
				// no need to lock, already done
				return;
		}

		// No previous lock, creates one.
		lockingUsers.add(searchedUser);
	}

	/**
	 * Un-locks this instance for the given instance and state
	 * @param	state	state that have to be un-locked
	 * @param	user	the current locking user
	 */
	public void unLock(State state, User user) 
		throws WorkflowException
	{
		this.unLock(state.getName(), user);
	}

	/**
	 * Un-locks this instance for the given instance and state
	 * @param	state	state that have to be un-locked
	 * @param	user	the current locking user
	 */
	private void unLock(String state, User user) 
		throws WorkflowException
	{
		// Test if lock already exists
		LockingUser searchedUser = new LockingUser();
		LockingUser foundUser = null;
		searchedUser.setState(state);
		searchedUser.setUserId(user.getUserId());
		searchedUser.setProcessInstance(this);

		int indexUser = lockingUsers.indexOf(searchedUser);
		if (indexUser != -1)
			foundUser = (LockingUser) lockingUsers.get(indexUser);

		if (foundUser == null)
		{
			// no need to unlock, already done
			return;
		}

		// if lock found for this state,
		// test if user is the same as requested
		if ( ! foundUser.getUserId().equals( user.getUserId() ) )
			throw new WorkflowException("ProcessInstanceImpl.unlock", "workflowEngine.EX_ERR_INSTANCE_LOCKED_BY_ANOTHER_PERSON");

		// Unlocks the previous one.
		lockingUsers.remove(searchedUser);
	}

	/**
	 * Lock this instance for the engine
	 */
	public void lock() 
		throws WorkflowException
	{
		// Test if lock already exists
		if (locked)
			throw new WorkflowException("ProcessInstanceImpl.lock()", "workflowEngine.EX_ERR_INSTANCE_ALREADY_LOCKED");

		locked = true;
	}

	/**
	 * Unlock this instance for the engine
	 */
	public void unLock() 
		throws WorkflowException
	{
		// Test if the instance is locked
		if (locked)
		{
			locked = false;
		}
	}

	/**
     * Get the validity state of this instance
     * @return true is this instance is valid
 	 */
	public boolean isValid() 
	{
		return valid;
	}

   /**
    * Get the lock Admin status of this instance
    * @return true is this instance is locked by admin
    */
	public boolean isLockedByAdmin() 
	{
		return locked;
	}
	
	public int isLockedByAdminCastor()
	{
		if (isLockedByAdmin())
			return 1;
		else
			return 0;
	}

   /**
    * Set the lock Admin status of this instance
    * @param locked true is this instance is locked by admin
    */
	public void setLockedByAdmin(boolean locked) 
	{
		this.locked = locked;
	}
	
	public void setLockedByAdminCastor(int locked) 
	{
		this.locked = (locked == 1);
	}

   /**
    * Get the error status of this instance
    * @return true if this instance is in error
    */
	public boolean getErrorStatus() 
	{
		return errorStatus;
	}
	
	public int getErrorStatusCastor()
	{
		if (getErrorStatus())
			return 1;
		else
			return 0;
	}

   /**
    * Set the error status of this instance
    * @param errorStatus true if this instance is in error
    */
	public void setErrorStatus(boolean errorStatus) 
	{
		this.errorStatus = errorStatus;
	}
	
	public void setErrorStatusCastor(int errorStatus) 
	{
		this.errorStatus = (errorStatus == 1);
	}

   /**
    * Get the timeout status of this instance
    * @return true if this instance is in an active state for a long long time
    */
	public boolean getTimeoutStatus() 
	{
		return timeoutStatus;
	}
	
	public int getTimeoutStatusCastor()
	{
		if (getTimeoutStatus())
			return 1;
		else
			return 0;
	}

   /**
    * Set the timeout status of this instance
    * @param	timeoutStatus	true if this instance is in an active state for a long long time
    */
	public void setTimeoutStatus(boolean timeoutStatus) 
	{
		this.timeoutStatus = timeoutStatus;
	}
	
	public void setTimeoutStatusCastor(int timeoutStatus) 
	{
		this.timeoutStatus = (timeoutStatus == 1);
	}

	/**
     * Computes tuples role/user/state (stored in an Actor object) from a QualifiedUsers object
	 * @param	qualifiedUsers		Users defined by their role or by a relation with a participant
	 * @param	state				State for which these user were/may be actors
	 * @return	tuples role/user as an array of Actor objects
	 */
	public Actor[] getActors(QualifiedUsers qualifiedUsers, State state) 
			throws WorkflowException
	{
		Vector			actors = new Vector();
		UserManager		userManager = WorkflowHub.getUserManager();
		UserInRole[]	userInRoles = qualifiedUsers.getUserInRoles();
		RelatedUser[]	relatedUsers = qualifiedUsers.getRelatedUsers();
		String			resolvedState;
		String			relation;
		String			role;
		
		// Process first "user in Role"
		for (int i=0; i<userInRoles.length; i++)
		{	
			User[] users = userManager.getUsersInRole(userInRoles[i].getRoleName(), modelId);
			for (int j=0; users!=null && j<users.length; j++)
			{
				actors.add( new ActorImpl( users[j], userInRoles[i].getRoleName(), state ) );
			}
		}

		// Then process related users
		for (int i=0; i<relatedUsers.length; i++)
		{
			User user = null;
			relation = relatedUsers[i].getRelation();

			if (relatedUsers[i].getParticipant() != null)
			{
				resolvedState = relatedUsers[i].getParticipant().getResolvedState();

				Participant participant = this.getParticipant(resolvedState);
				if (participant != null)
				{
					user = participant.getUser();
				}
			}
			else if (relatedUsers[i].getFolderItem() != null)
			{
				String fieldName = relatedUsers[i].getFolderItem().getName();
				Field field = getField(fieldName);
				String userId = field.getStringValue();
				if (userId != null && userId.length()>0)
					user = userManager.getUser(userId);
			}

			if (user != null)
			{
				if (relation!=null && relation.length()!=0 && !relation.equals("itself"))
				{
					user = userManager.getRelatedUser(user, relation, modelId);
				}
				
				// Get the role to which affect the user
				// if no role defined in related user
				// then get the one defined in qualifiedUser
				role = relatedUsers[i].getRole();
				if (role == null)
					role = qualifiedUsers.getRole();

				if (user != null)
					actors.add( new ActorImpl( user, role, state ) );
			}
		}

		return (Actor[]) actors.toArray(new Actor[0]);
	}

	/**
	 * Add a undo step in history
	 * @param	action	action description
	 * @param	params	params concatenated as "param1##param2...paramN"
	 */
	private void addUndoHistoryStep(String action, String params) throws WorkflowException
	{
		UndoHistoryStep undoStep = new UndoHistoryStep();
		undoStep.setStepId(( (HistoryStepImpl) this.currentStep ).getId());
		undoStep.setInstanceId(instanceId);
		undoStep.setAction(action);
		undoStep.setParameters(params);

		Database db = null;
		try
		{
			db = WorkflowJDOManager.getDatabase();
			db.begin();
			db.create(undoStep);
			db.commit();
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceImpl.addUndoHistoryStep", "workflowEngine.EX_ERR_CASTOR_CREATE_UNDO_HISTORYSTEP", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}
	}

	/**
	 * Undo all atomic operations that had occured for a given historyStep
	 * @param	historyStep		the historyStep when the atomic operations had occured
	 */
	private void undoStep(HistoryStep historyStep) throws WorkflowException
	{
		OQLQuery		query = null;
		QueryResults	results;
		Database 		db = null; 

		try
		{
			// Mark this instance as beeing in undo process
			// to avoid storing atomic operation done here
			this.inUndoProcess = true;

			// Constructs the query
			db = WorkflowJDOManager.getDatabase();
			db.begin();
			query = db.getOQLQuery( "SELECT undoStep FROM com.silverpeas.workflow.engine.instance.UndoHistoryStep undoStep "
									+ "WHERE undoStep.stepId = $1 ");

			// Execute the query
			query.bind( ( Integer.parseInt( ((HistoryStepImpl) historyStep).getId() ) ) );
			results = query.execute();

			while ( results.hasMore() ) 
			{
				UndoHistoryStep	undoStep = (UndoHistoryStep) results.next();
				String action = undoStep.getAction();
				StringTokenizer st = new StringTokenizer(undoStep.getParameters(), "##");
				
				if (action.equals("addActiveState"))
				{
					String state = undoStep.getParameters();
					this.removeActiveState(state);
				}

				else if (action.equals("removeActiveState"))
				{
					String state = undoStep.getParameters();
					this.addActiveState(state);
				}

				else if (action.equals("addWorkingUser"))
				{
					// The number of parameters must be : 3
					if (st.countTokens() != 3)
						throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_ILLEGAL_PARAMETERS", "method addWorkingUser - found:" + st.countTokens() + " instead of 3");

					String userId = st.nextToken();
					String state = st.nextToken();
					String role = st.nextToken();
					User user = WorkflowHub.getUserManager().getUser(userId);

					this.removeWorkingUser(user, state, role);
					this.unLock(state,user);
				}

				else if (action.equals("removeWorkingUser"))
				{
					// The number of parameters must be : 3
					if (st.countTokens() != 3)
						throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_ILLEGAL_PARAMETERS", "method addWorkingUser - found:" + st.countTokens() + " instead of 3");

					String userId = st.nextToken();
					String state = st.nextToken();
					String role = st.nextToken();
					User user = WorkflowHub.getUserManager().getUser(userId);

					this.addWorkingUser(user, state, role);
				}

				else if (action.equals("addInterestedUser"))
				{
					// The number of parameters must be : 3
					if (st.countTokens() != 3)
						throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_ILLEGAL_PARAMETERS", "method addInterestedUser - found:" + st.countTokens() + " instead of 3");

					String userId = st.nextToken();
					String state = st.nextToken();
					String role = st.nextToken();
					User user = WorkflowHub.getUserManager().getUser(userId);

					this.removeInterestedUser(user, state, role);
				}

				else if (action.equals("removeInterestedUser"))
				{
					// The number of parameters must be : 3
					if (st.countTokens() != 3)
						throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_ILLEGAL_PARAMETERS", "method removeInterestedUser - found:" + st.countTokens() + " instead of 3");

					String userId = st.nextToken();
					String state = st.nextToken();
					String role = st.nextToken();
					User user = WorkflowHub.getUserManager().getUser(userId);

					this.addInterestedUser(user, state, role);
				}

				else 
					throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EXP_UNKNOWN_ACTION");

				// as the atomic operation has been undone, remove it from undoHistory
				db.remove(undoStep);
			}

			db.commit();
		}
		catch (WorkflowException we)
		{
			throw new WorkflowException("ProcessInstanceManagerImpl.undoStep", "workflowEngine.EX_ERR_UNDO_STEP", we);
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceManagerImpl.getProcessInstances", "workflowEngine.EX_ERR_CASTOR_UNDO_STEP", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
			this.inUndoProcess = false;
		}
	}

	/**
	 * Cancel all the atomic operations since the step where first action had occured
	 * @param	state	the name of state where ac action has been discussed
	 * @param	actionDate	date of state re-resolving
	 */
	public void reDoState(String state, Date actionDate) throws WorkflowException
	{
		// Get the most recent step that logged an action to this state (=the action that is now the One)
		HistoryStep step = this.getMostRecentStepOnState(state);

		// Undo all steps between now and the action date of above step
		HistoryStep[] steps = this.getHistorySteps();
		boolean started = false;
		boolean stop = false;

		for (int i=steps.length-1; !stop && i>=0; i--)
		{
			if (steps[i].getId().equals(step.getId()))
			{
				started = true;
			}
			else if (started)
			{
				this.undoStep(steps[i]);
				if (steps[i].getResolvedState().equals(state) && (!steps[i].getAction().equals("#question#")) && (!steps[i].getAction().equals("#response#")))
					stop = true;
			}
		}
	}

	/**
	 * Get all the steps where given user (with given role) can go back from the given state
	 * @param	user	user that can do the back actions
	 * @param	roleName	role name of this user
	 * @param	roleName	role name of this user
	 * @param	stateName	name of state where user want to go back from 
	 * @return		an array of HistoryStep objects
	 */
	public HistoryStep[] getBackSteps(User user, String roleName, String stateName) throws WorkflowException
	{
		String stepId = null;
		Vector stepIds = new Vector();
		Vector steps = new Vector();
		OQLQuery query = null;
		QueryResults	results;
		HistoryStep[] allSteps = this.getHistorySteps();
		Database db = null;
		try
		{
			// Constructs the query
			db = WorkflowJDOManager.getDatabase();
			db.begin();
			query = db.getOQLQuery( "SELECT undoStep FROM com.silverpeas.workflow.engine.instance.UndoHistoryStep undoStep "
									+ "WHERE undoStep.instanceId = $1 "
									+ "AND undoStep.action = $2 "
									+ "AND undoStep.parameters = $3 ");

			// Search for all steps that activates the given state


			// Tests if user is a working user for this state
			WorkingUser wkUser = new WorkingUser();
			wkUser.setUserId( user.getUserId() );
			wkUser.setState( stateName );
			wkUser.setRole( roleName );
			if (workingUsers.contains(wkUser))
			{
				// Execute the query
				query.bind( Integer.parseInt(instanceId) );
				query.bind("addActiveState");
				query.bind( stateName );
				results = query.execute();

				while ( results.hasMore() ) 
				{
					stepId =( (UndoHistoryStep) results.next() ).getStepId();
					if (!stepIds.contains(stepId))
						stepIds.add(stepId);
				}
			}
			db.commit();

			// Build vector of HistoryStep found
			for (int i=0; i<allSteps.length; i++)
			{
				ActiveState state = new ActiveState(allSteps[i].getResolvedState());

				if ( stepIds.contains( allSteps[i].getId() )
					 && (!allSteps[i].getAction().equals("#question#"))
					 && (!allSteps[i].getAction().equals("#response#"))
					 && (allSteps[i].getResolvedState()!=null)
					 && (!activeStates.contains(state))
				   )	
					steps.add(allSteps[i]);
			}

			return (HistoryStep[]) steps.toArray(new HistoryStep[0]);			
		}
		catch (WorkflowException we)
		{
			throw new WorkflowException("ProcessInstanceImpl.getBackSteps", "workflowEngine.EX_ERR_GET_BACKSTEPS", we);
		}
		catch (PersistenceException pe)
		{
			throw new WorkflowException("ProcessInstanceImpl.getBackSteps", "workflowEngine.EX_ERR_CASTOR_GET_BACKSTEPS", pe);
		}
		finally
		{
			WorkflowJDOManager.closeDatabase(db);
		}
	}

	/**
	 * Search for the step with given id
	 * @param	stepId	the search step id
	 */
	private HistoryStep getStep(String stepId) throws WorkflowException
	{
		HistoryStep[] steps = getHistorySteps();
		HistoryStep foundStep = null;

		// Search for the step with given id
		for (int i=0; i<steps.length && foundStep==null; i++)
		{
			if (steps[i].getId().equals(stepId))
				foundStep = steps[i];
		}

		if (foundStep==null)
			throw new WorkflowException("ProcessInstanceImpl.getStep", "workflowEngine.EX_ERR_HISTORYSTEP_NOT_FOUND", "id : " + stepId);

		return foundStep;
	}
	
	/**
	 * Add a question
	 * @param	content		question text
	 * @param	stepId		id of destination step for the question
	 * @param	fromState	the state where the question was asked
	 * @param	fromuser	the user who asked the question
	 * @return	The state to which the question is
	 */
	public State addQuestion(String content, String stepId, State fromState, User fromUser) throws WorkflowException
	{
		HistoryStep step		= getStep(stepId);
		State targetState		= getProcessModel().getState(step.getResolvedState());
		Participant participant = getParticipant(targetState.getName());

		// Save the question
		QuestionImpl question = new QuestionImpl((ProcessInstance) this, content, fromState.getName(), step.getResolvedState(), fromUser, participant.getUser());
		addQuestion(question);

		return getProcessModel().getState(step.getResolvedState());
	}

	/**
	 * Answer a question
	 * @param	content			response text
	 * @param	questionId		id of question corresponding to this response
	 * @return	The state where the question was asked
	 */
	public State answerQuestion(String content, String questionId) throws WorkflowException
	{
		Question question = null;

		// search for the question with given id
		for (int i=0; question==null && i<questions.size(); i++)
		{
			if ( ( (Question) questions.get(i) ).getId().equals(questionId) )
				question = (Question) questions.get(i);
		}

		// if question not found, throw exception
		if (question==null)
			throw new WorkflowException("ProcessInstanceImpl.answerQuestion", "workflowEngine.ERR_QUESTION_NOT_FOUND", "id : " + questionId);

		// put the answer in question
		question.answer(content);

		// return the state where the question was asked
		return question.getTargetState();
	}

	/**
	 * Get all the questions asked to the given state
	 * @param	stateName	given state name
	 * @return all the questions (not yet answered) asked to the given state
	 */
	public Question[] getPendingQuestions(String stateName)
	{
		Vector questionsAsked = new Vector();
		Question question = null;

		for (int i=0; i<questions.size(); i++)
		{
			question = (Question) questions.get(i);
			if (question.getTargetState().getName().equals(stateName) && question.getResponseDate()==null)
				questionsAsked.add(question);
		}

		return (Question[]) questionsAsked.toArray(new QuestionImpl[0]);
	}

	/**
	 * Get all the questions asked from the given state
	 * @param	stateName	given state name
	 * @return all the questions (not yet answered) asked from the given state
	 */
	public Question[] getSentQuestions(String stateName)
	{
		Vector questionsAsked = new Vector();
		Question question = null;

		for (int i=0; i<questions.size(); i++)
		{
			question = (Question) questions.get(i);
			if (question.getFromState().getName().equals(stateName) && question.getResponseDate()==null)
				questionsAsked.add(question);
		}

		return (Question[]) questionsAsked.toArray(new QuestionImpl[0]);
	}

	/**
	 * Get all the questions asked from the given state and that have been aswered
	 * @param	stateName	given state name
	 * @return all the answered questions asked from the given state
	 */
	public Question[] getRelevantQuestions(String stateName)
	{
		Vector questionsAsked = new Vector();
		Question question = null;

		for (int i=0; i<questions.size(); i++)
		{
			question = (Question) questions.get(i);
			if (question.getFromState().getName().equals(stateName) && question.getResponseDate()!=null && question.isRelevant())
				questionsAsked.add(question);
		}

		return (Question[]) questionsAsked.toArray(new QuestionImpl[0]);
	}

	/**
	 * Cancel a question without response
	 *
	 *		1 - make a fictive answer
	 *		2 - remove active state
	 *		3 - remove working user
	 *		4 - recurse in question target state, if questions have been asked in cascade
	 *
	 * @param	question		the question to cancel
	 */
	public void cancelQuestion(Question question) throws WorkflowException
	{
		// 0 - recurse if necessary
		Question[] questions = getSentQuestions(question.getTargetState().getName());
		for (int i=0; questions!=null && i<questions.length; i++)
		{
			cancelQuestion(questions[i]);
		}

		// 1 - make a fictive answer
		State state = answerQuestion("", question.getId()); 

		// 2 - remove active state
		removeActiveState(state);

		// 3 - remove working user
		HistoryStep step = getMostRecentStepOnState(state.getName());
		removeWorkingUser(step.getUser(), state, step.getUserRoleName());
	}

	/**
	 * Get all the questions asked in this processInstance
	 * @return all the questions
	 */
	public Question[] getQuestions()
	{
		return (Question[]) questions.toArray(new QuestionImpl[0]);
	}

	// METHODS FOR CASTOR

	/**
	 * Set the instance history steps
	 * @param	historySteps	history steps
	 */
	public void castor_setHistorySteps(Vector historySteps)
	{
		this.historySteps = historySteps;
	}

	/**
	 * Get the instance history steps
	 * @return history steps as a Vector
	 */
	public Vector castor_getHistorySteps()
	{
		return historySteps;
	}

	/**
	 * Set the instance questions
	 * @param	questions	questions
	 */
	public void castor_setQuestions(Vector questions)
	{
		this.questions = questions;
	}

	/**
	 * Get the instance questions
	 * @return questions as a Vector
	 */
	public Vector castor_getQuestions()
	{
		return questions;
	}

	/**
	 * Set users who can see this process instance
	 * @param	interestedUsers	users as a Vector
	 * @return
	 */
	public void castor_setInterestedUsers(Vector interestedUsers)
	{
		this.interestedUsers = interestedUsers;
	}

	/**
	 * Get users who can see this process instance
	 * @return	users as a Vector
	 */
	public Vector castor_getInterestedUsers()
	{
		return interestedUsers;
	}

	/**
	 * Set users who can act on this process instance
	 * @param	workingUsers	users as a Vector
	 * @return
	 */
	public void castor_setWorkingUsers(Vector workingUsers)
	{
		this.workingUsers = workingUsers;
	}

	/**
	 * Get users who can act on this process instance
	 * @return	users as a Vector
	 */
	public Vector castor_getWorkingUsers()
	{
		return workingUsers;
	}

	/**
	 * Set users who have locked a state of this process instance
	 * @param	lockingUsers	users as a Vector
	 */
	public void castor_setLockingUsers(Vector lockingUsers)
	{
		this.lockingUsers = lockingUsers;
	}
		
	/**
	 * Get users who have locked a state of this process instance
	 * @return	users as a Vector
	 */
	public Vector castor_getLockingUsers()
	{
		return lockingUsers;
	}

	/**
	 * Set states that are due to be resolved for this process instance
	 * @param	activeStates	states as a Vector
	 */
	public void castor_setActiveStates(Vector activeStates)
	{
		this.activeStates = activeStates;
	}

	/**
	 * Get states that are due to be resolved for this process instance
	 * @return	states as a Vector
	 */
	public Vector castor_getActiveStates()
	{
		return activeStates;
	}

   /**
	 * Returns this instance title.
	 */
	public String getTitle(String role, String lang)
	{
	   String title = null;
	   Presentation template = null;

		try { template = getProcessModel().getPresentation(); }
		catch (WorkflowException e) {}

		if (template != null)
		{
		   title = template.getTitle(role, lang);
			DataRecord data = null;
			try
			{
			   data = getAllDataRecord(role, lang);
			}
			catch (WorkflowException e)
			{
			   data = null;
			}

			if (data != null)
			{
			   title = DataRecordUtil.applySubstitution(title, data, lang);
			}
		}

		if (title == null)
		{
		   title = ""+instanceId;
		}

		return title;
	}

	public boolean equals(Object obj) {
		ProcessInstance instance = (ProcessInstance) obj;
		return instance.getInstanceId().equals(this.instanceId);
	}

}