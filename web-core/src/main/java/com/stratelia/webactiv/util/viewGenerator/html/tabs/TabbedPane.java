/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

/*
 * TabbedPane.java
 * 
 * Created on 10 octobre 2000, 16:11
 */

package com.stratelia.webactiv.util.viewGenerator.html.tabs;

import com.stratelia.webactiv.util.viewGenerator.html.SimpleGraphicElement;

/**
 * TabbedPane is an interface to be implemented by a graphic element to print
 * tabs in an html format.
 * 
 * @author squere
 * @version 1.0
 */
public interface TabbedPane extends SimpleGraphicElement {

  /**
   * Method declaration
   * 
   * 
   * @param nbLines
   * 
   * @see
   */
  public void init(int nbLines);

  /**
   * Set the indentation on the left (the default is on the rigth).
   */
  public void setIndentationLeft();

  /**
   * Set the number of level between the root directory and the current one (the
   * directory from the page we are generating). The default is 2.
   * 
   * @param level
   *          The new level count.
   */
  public void setLevelRootImage(int level);

  /**
   * Add a new tab to our TabPane.
   * 
   * @param label
   *          The label of the tab.
   * @param action
   *          The action associated with this pane. Exemple :
   *          "javascript:onClick=viewByDay()"
   * @param selected
   *          Specify if the tab is selected.
   */
  public void addTab(String label, String action, boolean selected);

  /**
   * Add a new tab to our TabPane.
   * 
   * @param label
   *          The label of the tab.
   * @param action
   *          The action associated with this pane. Exemple :
   *          "javascript:onClick=viewByDay()"
   * @param selected
   *          Specify if the tab is selected.
   * @param enabled
   *          Specify if the tab is enabled. If enabled is false, the action
   *          won't be possible.
   */
  public void addTab(String label, String action, boolean selected,
      boolean enabled);

  /**
   * Add a new tab to our TabPane.
   * 
   * @param label
   *          The label of the tab.
   * @param action
   *          The action associated with this pane. Exemple :
   *          "javascript:onClick=viewByDay()"
   * @param selected
   *          Specify if the tab is selected.
   * @param the
   *          number of lines to our TabPane
   */
  public void addTab(String label, String action, boolean selected, int nbLines);

  /**
   * Add a new tab to our TabPane.
   * 
   * @param label
   *          The label of the tab.
   * @param action
   *          The action associated with this pane. Exemple :
   *          "javascript:onClick=viewByDay()"
   * @param selected
   *          Specify if the tab is selected.
   * @param enabled
   *          Specify if the tab is enabled. If enabled is false, the action
   *          won't be possible
   * @param the
   *          number of lines to our TabPane
   */
  public void addTab(String label, String action, boolean disabled,
      boolean enabled, int nbLines);

  /**
   * Print the TabbedPane in an html format
   * 
   * @return The TabbedPane representation
   */
  public String print();

}
