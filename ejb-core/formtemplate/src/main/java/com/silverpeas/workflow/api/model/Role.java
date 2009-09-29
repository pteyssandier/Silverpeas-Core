package com.silverpeas.workflow.api.model;

import java.util.Iterator;

/**
 * Interface describing a representation of the &lt;role&gt; element of a
 * Process Model.
 */
public interface Role {
  /**
   * Get the name of the Role
   * 
   * @return role's name
   */
  public String getName();

  /**
   * Set the name of the Role
   * 
   * @param role
   *          's name
   */
  public void setName(String name);

  /**
   * Get label in specific language for the given role
   * 
   * @param lang
   *          label's language
   * @param role
   *          role for which the label is
   * @return wanted label as a String object. If label is not found, search
   *         label with given role and default language, if not found again,
   *         return the default label in given language, if not found again,
   *         return the default label in default language, if not found again,
   *         return empty string.
   */
  public String getLabel(String role, String language);

  /**
   * Get all the labels
   * 
   * @return an object containing the collection of the labels
   */
  public ContextualDesignations getLabels();

  /**
   * Iterate through the Labels
   * 
   * @return an iterator
   */
  public Iterator iterateLabel();

  /**
   * Add a label Method needed primarily by Castor
   */
  public void addLabel(ContextualDesignation label);

  /**
   * Create an object implementing ContextualDesignation Method needed primarily
   * by Castor
   */
  public ContextualDesignation createDesignation();

  /**
   * Get description in specific language for the given role
   * 
   * @param lang
   *          description's language
   * @param role
   *          role for which the description is
   * @return wanted description as a String object. If description is not found,
   *         search description with given role and default language, if not
   *         found again, return the default description in given language, if
   *         not found again, return the default description in default
   *         language, if not found again, return empty string.
   */
  public String getDescription(String role, String language);

  /**
   * Get all the descriptions
   * 
   * @return an object containing the collection of the descriptions
   */
  public ContextualDesignations getDescriptions();

  /**
   * Iterate through the descriptions
   * 
   * @return an iterator
   */
  public Iterator iterateDescription();

  /**
   * Add a description Method needed primarily by Castor
   */
  public void addDescription(ContextualDesignation description);
}
