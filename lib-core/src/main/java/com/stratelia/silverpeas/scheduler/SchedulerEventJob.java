/* SimpleScheduler (a small library for scheduling jobs)
   Copyright (C) 2001  Thomas Breitkreuz

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

	Thomas Breitkreuz (tb@cdoc.de)
 */

package com.stratelia.silverpeas.scheduler;

import java.util.*;

/**
 * This class extends the class 'SchedulerJob' for the functionality of the
 * scheduled execution of shell scripts.
 */
public class SchedulerEventJob extends SchedulerJob {
  /**
   * The constructor has proteceted access, because the generation of jobs
   * should be done in a central way by the class 'SimpleScheduler'
   * 
   * @param aController
   *          The controller, that controls all job executions
   * @param aOwner
   *          The owner of the job
   * @param aJobName
   *          The name of the job
   */
  protected SchedulerEventJob(SimpleScheduler theJobController,
      SchedulerEventHandler theJobOwner, String theJobName)
      throws SchedulerException {
    super(theJobController, theJobOwner, theJobName);
  }

  /**
   * This method implements the abstract method of the base class. It creates a
   * new SchedulerEvent and sends it to the job owner.
   * 
   * @param theExecutionDate
   *          The date of the execution
   */
  protected void execute(Date theExecutionDate) throws SchedulerException {
    try {
      getOwner().handleSchedulerEvent(
          new SchedulerEvent(SchedulerEvent.EXECUTION, this));
    } catch (Exception aException) {
      throw new SchedulerException(
          "SchedulerShellJob.execute: Execution failed (Reason: "
              + aException.getMessage() + ")");
    }
  }
}
