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
package com.stratelia.webactiv.util.statistic.control;

import java.util.Comparator;
import java.util.Date;

import com.stratelia.webactiv.util.statistic.model.HistoryByUser;

public class LastAccessComparatorDesc implements Comparator {
  static public LastAccessComparatorDesc comparator = new LastAccessComparatorDesc();

  public int compare(Object o1, Object o2) {
    HistoryByUser historyUser1 = (HistoryByUser) o1;
    HistoryByUser historyUser2 = (HistoryByUser) o2;

    Date dateUser1 = historyUser1.getLastAccess();
    Date dateUser2 = historyUser2.getLastAccess();

    int compareResult = 0;

    if (dateUser1 != null && dateUser2 != null) {
      compareResult = dateUser1.compareTo(dateUser2);
    } else {
      if (dateUser1 == null && dateUser2 != null) {
        compareResult = -1;
      }
      if (dateUser1 != null && dateUser2 == null) {
        compareResult = 1;
      }
    }

    return 0 - compareResult;
  }

  public boolean equals(Object o) {
    return o == this;
  }
}
