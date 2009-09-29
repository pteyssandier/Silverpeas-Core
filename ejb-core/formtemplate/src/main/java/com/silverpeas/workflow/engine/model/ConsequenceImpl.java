package com.silverpeas.workflow.engine.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import com.silverpeas.workflow.api.model.AbstractDescriptor;
import com.silverpeas.workflow.api.model.Consequence;
import com.silverpeas.workflow.api.model.QualifiedUsers;
import com.silverpeas.workflow.api.model.State;
import com.silverpeas.workflow.api.model.StateSetter;
import com.silverpeas.workflow.api.model.Trigger;
import com.silverpeas.workflow.api.model.Triggers;
import com.silverpeas.workflow.engine.AbstractReferrableObject;

/**
 * Class implementing the representation of the &lt;consequence&gt; element of a
 * Process Model.
 */
public class ConsequenceImpl extends AbstractReferrableObject implements
    Consequence, AbstractDescriptor, Serializable {
  private String item;
  private String operator;
  private String value;
  private boolean kill;
  private Vector targetStateList;
  private Vector unsetStateList;
  private QualifiedUsers notifiedUsers;
  private int step;
  private Triggers triggers;

  // ~ Instance fields related to AbstractDescriptor
  // ////////////////////////////////////////////////////////

  private AbstractDescriptor parent;
  private boolean hasId = false;
  private int id;

  /**
   * Constructor
   */
  public ConsequenceImpl() {
    targetStateList = new Vector();
    unsetStateList = new Vector();
    triggers = new TriggersImpl();
    kill = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.silverpeas.workflow.api.model.Consequence#getTargetState(java.lang.
   * String)
   */
  public State getTargetState(String strStateName) {
    for (int i = 0; i < targetStateList.size(); i++)
      if (((StateSetter) targetStateList.get(i)).getState().getName().equals(
          strStateName))
        return ((StateSetter) targetStateList.get(i)).getState();

    return null;
  }

  /**
   * Get the target states
   * 
   * @return the target states as a Vector
   */
  public State[] getTargetStates() {
    if (targetStateList == null)
      return null;

    State[] states = new StateImpl[targetStateList.size()];
    for (int i = 0; i < targetStateList.size(); i++) {
      StateRef ref = (StateRef) targetStateList.get(i);
      states[i] = ref.getState();
    }

    return (State[]) states;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.silverpeas.workflow.api.model.Consequence#addTargetState(com.silverpeas
   * .workflow.api.model.StateSetter)
   */
  public void addTargetState(StateSetter stateSetter) {
    targetStateList.add(stateSetter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#createStateSetter()
   */
  public StateSetter createStateSetter() {
    return new StateRef();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#iterateTargetState()
   */
  public Iterator iterateTargetState() {
    return targetStateList.iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.silverpeas.workflow.api.model.Consequence#getUnsetState(java.lang.String
   * )
   */
  public State getUnsetState(String strStateName) {
    for (int i = 0; i < unsetStateList.size(); i++)
      if (((StateSetter) unsetStateList.get(i)).getState().getName().equals(
          strStateName))
        return ((StateSetter) unsetStateList.get(i)).getState();

    return null;
  }

  /**
   * Get the states to unset
   * 
   * @return the states to unset as a Vector
   */
  public State[] getUnsetStates() {
    if (unsetStateList == null)
      return null;

    State[] states = new StateImpl[unsetStateList.size()];
    for (int i = 0; i < unsetStateList.size(); i++) {
      StateRef ref = (StateRef) unsetStateList.get(i);
      states[i] = ref.getState();
    }

    return (State[]) states;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.silverpeas.workflow.api.model.Consequence#addUnsetState(com.silverpeas
   * .workflow.api.model.StateSetter)
   */
  public void addUnsetState(StateSetter stateSetter) {
    unsetStateList.add(stateSetter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#iterateUnsetState()
   */
  public Iterator iterateUnsetState() {
    return unsetStateList.iterator();
  }

  /**
   * Get the flag that specifies if instance has to be removed
   * 
   * @return true if instance has to be removed
   */
  public boolean getKill() {
    return kill;
  }

  /**
   * Set the flag that specifies if instance has to be removed
   * 
   * @param kill
   *          true if instance has to be removed
   */
  public void setKill(boolean kill) {
    this.kill = kill;
  }

  /**
   * Get all the users that have to be notified
   * 
   * @return QualifiedUsers object containing notified users
   */
  public QualifiedUsers getNotifiedUsers() {
    if (notifiedUsers == null)
      return new QualifiedUsersImpl();
    else
      return this.notifiedUsers;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#getNotifiedUsersEx()
   */
  public QualifiedUsers getNotifiedUsersEx() {
    return notifiedUsers;
  }

  /**
   * Set all the users that have to be notified
   * 
   * @param QualifiedUsers
   *          object containing notified users
   */
  public void setNotifiedUsers(QualifiedUsers notifiedUsers) {
    this.notifiedUsers = notifiedUsers;
  }

  /**
   * Create and return an object implementing QalifiedUsers
   */
  public QualifiedUsers createQualifiedUsers() {
    return new QualifiedUsersImpl();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#getItem()
   */
  public String getItem() {
    return item;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#getOperator()
   */
  public String getOperator() {
    return operator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.Consequence#getValue()
   */
  public String getValue() {
    return value;
  }

  /**
   * Check if the consequence is verified or not
   * 
   * @param itemValue
   *          - the value of the folder item (specified in xml attribute 'item'
   * @return true if the consequence is verified
   */
  public boolean isVerified(String itemValue) {
    if (getItem() == null && getOperator() == null && getValue() == null)
      return true;

    boolean processValueAsString = false;
    boolean processValueAsInt = false;

    // Like we don't know field type
    // We try to parse value as an int
    int iValue = -9999;
    try {
      iValue = Integer.parseInt(itemValue);
      processValueAsInt = true;
    } catch (NumberFormatException nfe) {
      processValueAsInt = false;
    }

    float fValue = -9999;
    if (!processValueAsInt) {
      // itemValue is not an int value
      // try to parse as a float
      try {
        fValue = Float.parseFloat(itemValue);
      } catch (NumberFormatException nfe) {
        processValueAsString = true;
      }
    }

    if (getValue() != null && getValue().length() == 0) {
      processValueAsInt = false;
      processValueAsString = true;
    }

    if (getOperator().equals("=")) {
      if (processValueAsString)
        return itemValue.equalsIgnoreCase(getValue());
      else if (processValueAsInt)
        return iValue == getValueAsInt();
      else
        return fValue == getValueAsFloat();
    } else if (getOperator().equals("!=")) {
      if (processValueAsString)
        return !itemValue.equalsIgnoreCase(getValue());
      else if (processValueAsInt)
        return iValue != getValueAsInt();
      else
        return fValue != getValueAsFloat();
    } else if (getOperator().equals(">")) {
      if (processValueAsString)
        return itemValue.compareTo(getValue()) > 0;
      else if (processValueAsInt)
        return iValue > getValueAsInt();
      else
        return fValue > getValueAsFloat();
    } else if (getOperator().equals(">=")) {
      if (processValueAsString)
        return itemValue.compareTo(getValue()) >= 0;
      else if (processValueAsInt)
        return iValue >= getValueAsInt();
      else
        return fValue >= getValueAsFloat();
    } else if (getOperator().equals("<")) {
      if (processValueAsString)
        return itemValue.compareTo(getValue()) < 0;
      else if (processValueAsInt)
        return iValue < getValueAsInt();
      else
        return fValue < getValueAsFloat();
    } else if (getOperator().equals("<=")) {
      if (processValueAsString)
        return itemValue.compareTo(getValue()) <= 0;
      else if (processValueAsInt)
        return iValue <= getValueAsInt();
      else
        return fValue <= getValueAsFloat();
    } else if (getOperator().equals("contains")) {
      if (processValueAsString)
        return (itemValue.indexOf(getValue()) != -1);
      else
        return false;
    }

    return false;
  }

  private float getValueAsFloat() {
    return Float.parseFloat(getValue());
  }

  private int getValueAsInt() {
    return Integer.parseInt(getValue());
  }

  public void setItem(String string) {
    item = string;
  }

  public void setOperator(String string) {
    operator = string;
  }

  public void setValue(String string) {
    value = string;
  }

  public void setStep(int id) {
    step = id;
  }

  public int getStep() {
    return step;
  }

  public Triggers createTriggers() {
    return new TriggersImpl();
  }

  public Triggers getTriggers() {
    return triggers;
  }

  public void setTriggers(Triggers triggers) {
    this.triggers = triggers;
  }

  /************* Implemented methods *****************************************/
  // ~ Methods ////////////////////////////////////////////////////////////////

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.AbstractDescriptor#setId(int)
   */
  public void setId(int id) {
    this.id = id;
    hasId = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.AbstractDescriptor#getId()
   */
  public int getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.silverpeas.workflow.api.model.AbstractDescriptor#setParent(com.silverpeas
   * .workflow.api.model.AbstractDescriptor)
   */
  public void setParent(AbstractDescriptor parent) {
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.AbstractDescriptor#getParent()
   */
  public AbstractDescriptor getParent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.api.model.AbstractDescriptor#hasId()
   */
  public boolean hasId() {
    return hasId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.silverpeas.workflow.engine.AbstractReferrableObject#getKey()
   */
  public String getKey() {
    StringBuffer sb = new StringBuffer();

    if (item != null)
      sb.append(item);

    sb.append("|");

    if (operator != null)
      sb.append(operator);

    sb.append("|");

    if (value != null)
      sb.append(value);

    return sb.toString();
  }

}