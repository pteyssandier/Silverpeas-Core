/**
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) 
 ---*/

package com.stratelia.webactiv.util.viewGenerator.html.arrayPanes;

/*
 * CVS Informations
 * 
 * $Id: ArrayPaneStatusBean.java,v 1.2 2005/12/30 18:47:39 neysseri Exp $
 * 
 * $Log: ArrayPaneStatusBean.java,v $
 * Revision 1.2  2005/12/30 18:47:39  neysseri
 * no message
 *
 * Revision 1.1.1.1  2002/08/06 14:48:19  nchaix
 * no message
 *
 * Revision 1.3  2002/01/04 14:04:23  mmarengo
 * Stabilisation Lot 2
 * SilverTrace
 * Exception
 *
 */

/**
 * Class declaration
 * @author
 */
public class ArrayPaneStatusBean {
  private int firstVisibleLine = 0;
  private int maximumVisibleLine = 10;
  private int sortColumn = 0; // no column is sorted by default

  /**
   * Method declaration
   * @param firstVisibleLine
   * @see
   */
  public void setFirstVisibleLine(int firstVisibleLine) {
    this.firstVisibleLine = firstVisibleLine;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public int getFirstVisibleLine() {
    return firstVisibleLine;
  }

  /**
   * Method declaration
   * @param maximumVisibleLine
   * @see
   */
  public void setMaximumVisibleLine(int maximumVisibleLine) {
    this.maximumVisibleLine = maximumVisibleLine;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public int getMaximumVisibleLine() {
    return maximumVisibleLine;
  }

  /**
   * Method declaration
   * @param sortColumn
   * @see
   */
  public void setSortColumn(int sortColumn) {
    this.sortColumn = sortColumn;
  }

  /**
   * Method declaration
   * @return
   * @see
   */
  public int getSortColumn() {
    return sortColumn;
  }
}