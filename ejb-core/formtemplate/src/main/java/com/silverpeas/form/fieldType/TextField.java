package com.silverpeas.form.fieldType;

import com.silverpeas.form.Field;
import com.silverpeas.form.FormException;

/**
 * A TextField stores a text value.
 *
 * @see Field
 * @see FieldDisplayer
 */
public abstract class TextField implements Field
{
  /**
   * The text field type name.
   */
  static public final String TYPE = "text";
  
  static public final String CONTENT_TYPE = "contentType";
  static public final String CONTENT_TYPE_INT = "int";
  static public final String CONTENT_TYPE_FLOAT = "float";

  /**
   * Returns the type name.
   */
  public String getTypeName()
  {
     return TYPE;
  }

  /**
   * Returns true if the value is read only.
   */
  public abstract boolean isReadOnly();

  /**
   * Returns the string value of this field.
   */
  public String getValue()
  {
  	  return getStringValue();
  }

  /**
   * Returns the local value of this field.
   *
   * There is no local format for a text field,
   * so the language parameter is unused.
   */
  public String getValue(String language)
  {
  	  return getStringValue();
  }

  /**
   * Set this field value from a local string value.
   *
   * @throw FormException when the field is readOnly.
   * @throw FormException when the string value is ill formed.
   */
  public void setValue(String value) throws FormException
  {
  	 setStringValue(value);
  }

  /**
   * Set this field value from a local string value.
   *
   * There is no local format for a text field,
   * so the language parameter is unused.
   *
   * @throw FormException when the field is readOnly.
   * @throw FormException when the string value is ill formed.
   */
  public void setValue(String value, String language) throws FormException
  {
  	 setStringValue(value);
  }

  /**
   * Returns true if the value isn't ill formed and this field isn't read only.
   *
   * Here any string value is accepted unless the field is read only.
   */
  public boolean acceptValue(String value)
  {
     return ! isReadOnly();
  }

  /**
   * Returns true if the value isn't ill formed and this field isn't read only.
   *
   * Here any string value is accepted unless the field is read only.
   */
  public boolean acceptStringValue(String value)
  {
     return ! isReadOnly();
  }

  /**
   * Returns true if the value isn't ill formed and this field isn't read only.
   *
   * Here any string value is accepted unless the field is read only.
   */
  public boolean acceptValue(String value, String language)
  {
     return ! isReadOnly();
  }

  /**
   * Returns the value of this field.
   */
  public Object getObjectValue()
  {
  	return getStringValue();
  }

  /**
   * Set this field value.
   *
   * @throw FormException when the field is readOnly.
   * @throw FormException when the value is not a String.
   */
  public void setObjectValue(Object value) throws FormException
  {
     if (value instanceof String)
	 {
	     setStringValue((String) value);
	 }
	 else
	 {
	 	if (value != null)
	    	throw new FormException("TextField.setObjectValue", "form.EXP_NOT_A_STRING");
	    else
			setNull();
	 }
  }

  /**
   * Returns true if the value is a String
   * and this field isn't read only.
   */
  public boolean acceptObjectValue(Object value)
  {
     if (value instanceof String) return ! isReadOnly();
	 else return false;
  }

  /**
   * Returns true if this field is not set.
   */
  public boolean isNull()
  {
     return (getStringValue() == null || getStringValue().trim().equals(""));
  }

  /**
   * Set to null this field.
   *
   * @throw FormException when the field is mandatory.
   * @throw FormException when the field is read only.
   */
  public void setNull() throws FormException
  {
	 setStringValue(null);
  }

  /**
   * Tests equality beetwen this field and the specified field.
	*/
  public boolean equals(Object o)
  {
	  String s = getStringValue();
	  if (s==null) s="";

     if (o instanceof TextField)
	  {
		  String t = ((TextField) o).getStringValue();
		  if (t==null) t="";

		  return s.equalsIgnoreCase(t);
	  }
	  else if (o instanceof Field)
	  {
		  String t = ((Field) o).getValue("");
		  if (t==null) t="";

		  return s.equalsIgnoreCase(t);
	  }
	  else return false;
  }

  /**
   * Compares this field with the specified field.
	*/
  public int compareTo(Object o)
  {
	  String s = getStringValue();
	  if (s==null) s="";

     if (o instanceof TextField)
	  {
		  String t = ((TextField) o).getStringValue();
		  if (t==null) t="";

		  return s.compareTo(t);
	  }
	  else if (o instanceof Field)
	  {
		  String t = ((Field) o).getValue("");
		  if (t==null) t="";

		  return s.compareTo(t);
	  }
	  else return -1;
  }

  public int hashCode()
  {
	  String s = getStringValue();
	  return (""+s).toLowerCase().hashCode();
  }
}
