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

import java.text.*;
import java.util.*;

import com.stratelia.silverpeas.silvertrace.*;

/**
 * This is the base class of all scheduler job classes. This class is abstract.
 * If you will implement your own special job class, you have to overrite the
 * method 'execute' and add your own job generation method in the class
 * 'SimpleScheduler'
 */
abstract public class SchedulerJob extends Thread {
  // Environment variables
  protected SimpleDateFormat logDateFormat;
  private SchedulerEventHandler theOwner;
  // private File theLogBaseFile;
  private String sJobName;
  // private String sJobLogFileName;

  // Converted cron string
  private Vector vMinutes;
  private Vector vHours;
  private Vector vDaysOfMonth;
  private Vector vMonths;
  private Vector vDaysOfWeek;

  // Next timestamp
  private Integer currentMinute;
  private Integer currentHour;
  private Integer currentDayOfMonth;
  private Integer currentMonth;
  private Integer currentYear;

  // Runtime variables
  private long nextTimeStamp = 0;
  private volatile boolean bRunnable;

  /**
   * This method returns the owner (or creator) of the job
   * 
   * @return The owner of the job
   */
  public SchedulerEventHandler getOwner() {
    return theOwner;
  }

  public void initTimeStamp(long nextTime) {
    if (nextTime != 0)
      nextTimeStamp = nextTime;
    else
      nextTimeStamp = getNextTimeStamp();
  }

  public long readNextTimeStamp() {
    return nextTimeStamp;
  }

  /**
   * This method returns the name of the job
   * 
   * @return The name of the job
   */
  public String getJobName() {
    return sJobName;
  }

  /**
   * This method handles the thread execution
   * 
   */
  public void run() {
    long sleepTime;

    // Get next schedule time
    if (nextTimeStamp == 0)
      nextTimeStamp = getNextTimeStamp();

    SilverTrace.info("scheduler", "SchedulerJob.run",
        "root.MSG_GEN_PARAM_VALUE", ": Job '" + sJobName
            + "' starts without errors.");
    SilverTrace.info("scheduler", "SchedulerJob.run",
        "root.MSG_GEN_PARAM_VALUE", ": Next schedule time: "
            + logDateFormat.format(new Date(nextTimeStamp)));
    /*
     * // Write a header into the log file try { logStream = new PrintStream
     * (new FileOutputStream (theLogBaseFile.getAbsolutePath () +
     * System.getProperties ().getProperty ("file.separator") + sJobName +
     * ".log", true)); logStream.println (logDateFormat.format (new Date ()) +
     * ": Job '" + sJobName + "' starts without errors."); logStream.println
     * (logDateFormat.format (new Date ()) + ": Next schedule time: " +
     * logDateFormat.format (new Date (nextTimeStamp)) + "\n"); logStream.close
     * (); } catch (Exception aException) { System.out.println
     * ("SchedulerJob.run: Exception while writing the log header occured (Reason: "
     * + aException.toString () + ")"); }
     */

    while (bRunnable) {
      try {
        // Calculate the delay time
        sleepTime = nextTimeStamp - (new Date()).getTime();
        SilverTrace.info("scheduler", "SchedulerJob.run",
            "root.MSG_GEN_PARAM_VALUE", ": Sleeptime = " + sleepTime);
        if (sleepTime < 0) {
          // Yields if there are problems with the date. Should normaly not
          // occour.
          sleep(0);
        } else {
          // Sleeps up to the next schedule time
          sleep(sleepTime);
        }
      } catch (InterruptedException aException) {
        /*
         * // Suppress mesages while shutdown if (bRunnable) {
         * System.out.println(
         * "SchedulerJob.run: InterruptedException while sleeping occured (Reason: "
         * + aException.toString () + ")"); }
         */
      }

      if (bRunnable && ((new Date()).getTime() >= nextTimeStamp)) {
        try {
          SilverTrace.info("scheduler", "SchedulerJob.run",
              "root.MSG_GEN_PARAM_VALUE", ": ---------------- Start of job '"
                  + sJobName + "' -------------------");
          // Open a new Stream to the log file
          /*
           * logStream = new PrintStream (new FileOutputStream
           * (theLogBaseFile.getAbsolutePath () + System.getProperties
           * ().getProperty ("file.separator") + sJobName + ".log", true));
           * logStream.println (logDateFormat.format (new Date ()) +
           * ": ---------------- Start of job '" + sJobName +
           * "' -------------------");
           */

          // Execute the functionality of the job and gets a new schedule time
          try {
            nextTimeStamp = getNextTimeStamp();
            // execute (logStream, new Date ());
            execute(new Date());
            // logStream.flush ();
            theOwner.handleSchedulerEvent(new SchedulerEvent(
                SchedulerEvent.EXECUTION_SUCCESSFULL, this));
          } catch (SchedulerException aException) {
            theOwner.handleSchedulerEvent(new SchedulerEvent(
                SchedulerEvent.EXECUTION_NOT_SUCCESSFULL, this));
          }

          SilverTrace.info("scheduler", "SchedulerJob.run",
              "root.MSG_GEN_PARAM_VALUE", ": ---------------- End of job '"
                  + sJobName + "' -------------------");
          SilverTrace.info("scheduler", "SchedulerJob.run",
              "root.MSG_GEN_PARAM_VALUE", ": Next schedule time: "
                  + logDateFormat.format(new Date(nextTimeStamp)));
          /*
           * logStream.println (logDateFormat.format (new Date ()) +
           * ": ---------------- End of job '" + sJobName +
           * "' -------------------"); logStream.println (logDateFormat.format
           * (new Date ()) + ": Next schedule time: " + logDateFormat.format
           * (new Date (nextTimeStamp)) + ".\n"); logStream.close ();
           */
        } catch (Exception aException) {
          // System.out.println
          // ("SchedulerJob.run: Exception while job execution occured (Reason: "
          // + aException.toString () + ")");
          SilverTrace.error("scheduler", "SchedulerJob.run",
              "root.EX_NO_MESSAGE", aException);
        }
      }
    }

    /*
     * // Write a footer into the log file try { logStream = new PrintStream
     * (new FileOutputStream (theLogBaseFile.getAbsolutePath () +
     * System.getProperties ().getProperty ("file.separator") + sJobName +
     * ".log", true)); logStream.println (logDateFormat.format (new Date ()) +
     * ": Job '" + sJobName + "' terminates without errors.\n\n");
     * logStream.close (); } catch (Exception aException) { System.out.println
     * ("SchedulerJob.run: Exception while writing the log footer occured (Reason: "
     * + aException.toString () + ")"); }
     */

    theOwner = null;
    sJobName = null;
  }

  /**
   * Stops the scheduling of the job
   */
  public synchronized void stopThread() {
    bRunnable = false;
    interrupt();
  }

  /**
   * This method holds the logic of the job. It has to be overwriten in
   * subclasses of this class
   * 
   * @param log
   *          A PrintStream for text writings in the log file for this job
   * @param theExecutionDate
   *          The date of the execution
   */
  abstract protected void execute(Date theExecutionDate)
      throws SchedulerException;

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
   * @param aLogBaseFile
   *          The log file for the job
   */
  protected SchedulerJob(SimpleScheduler aController,
      SchedulerEventHandler aOwner, String aJobName) throws SchedulerException {
    if (aController == null) {
      throw new SchedulerException(
          "SchedulerJob.SchedulerJob: Parameter 'aController' is null");
    }

    if (aOwner == null) {
      throw new SchedulerException(
          "SchedulerJob.SchedulerJob: Parameter 'aOwner' is null");
    }

    if (aJobName == null) {
      throw new SchedulerException(
          "SchedulerJob.SchedulerJob: Parameter 'aJobName' is null");
    }

    theOwner = aOwner;

    sJobName = aJobName;
    logDateFormat = SimpleScheduler.LOG_DATE_FORMAT;

    vMinutes = new Vector();
    vHours = new Vector();
    vDaysOfMonth = new Vector();
    vMonths = new Vector();
    vDaysOfWeek = new Vector();

    /*
     * currentMinute = new Integer (0); currentHour = new Integer (0);
     * currentDayOfMonth = new Integer (1); currentMonth = new Integer (0);
     * currentYear = new Integer ((Calendar.getInstance ()).get
     * (Calendar.YEAR));
     */

    // Instead
    Calendar calInit = Calendar.getInstance();
    currentMinute = new Integer(0);
    currentHour = new Integer(0);
    if (calInit.getActualMinimum(Calendar.DAY_OF_MONTH) == calInit
        .get(Calendar.DAY_OF_MONTH)) {
      currentDayOfMonth = new Integer(calInit
          .getActualMaximum(Calendar.DAY_OF_MONTH));
      if (calInit.getActualMinimum(Calendar.MONTH) == calInit
          .get(Calendar.MONTH)) {
        currentMonth = new Integer(calInit.getActualMaximum(Calendar.MONTH));
        currentYear = new Integer(calInit.get(Calendar.YEAR) - 1);
      } else {
        currentMonth = new Integer(calInit.get(Calendar.MONTH) - 1);
        currentYear = new Integer(calInit.get(Calendar.YEAR));
      }
    } else {
      currentDayOfMonth = new Integer(calInit.get(Calendar.DAY_OF_MONTH) - 1);
      currentMonth = new Integer(calInit.get(Calendar.MONTH));
      currentYear = new Integer(calInit.get(Calendar.YEAR));
    }
    if (calInit.getActualMinimum(Calendar.MONTH) == currentMonth.intValue()) {
      currentMonth = new Integer(calInit.getActualMaximum(Calendar.MONTH));
      currentYear = new Integer(currentYear.intValue() - 1);
    } else {
      currentMonth = new Integer(currentMonth.intValue() - 1);
    }
    nextTimeStamp = 0;
    bRunnable = true;
  }

  /**
   * This method sets the scheduling parameter. The time settings are given by
   * vectors. Each vector holds a list of Integer objects (currently ordered).
   * Every Integer represents a element of a timestamp (cron like).
   * 
   * @param startMinutes
   *          A list of minutes (0-59)
   * @param startHours
   *          A list of hours (0-23)
   * @param startDaysOfMonth
   *          A list of days of a month (1-31)
   * @param startMonths
   *          A list of months (1-12; starts with 1 for January)
   * @param startDaysOfWeek
   *          A list of day of a week (0-6; starts with 0 for Sunday)
   */
  protected synchronized void setSchedulingParameter(Vector startMinutes,
      Vector startHours, Vector startDaysOfMonth, Vector startMonths,
      Vector startDaysOfWeek) throws SchedulerException {
    Enumeration vectorEnumerator;

    Vector workVector;
    int workInt;

    // Check minute values
    if (startMinutes == null) {
      startMinutes = new Vector();
    }

    for (vectorEnumerator = startMinutes.elements(); vectorEnumerator
        .hasMoreElements();) {
      try {
        workInt = ((Integer) vectorEnumerator.nextElement()).intValue();

        if ((workInt < 0) || (workInt > 59)) {
          throw new SchedulerException(
              "SchedulerMethodJob.setParameter: A minute value is out of range");
        }
      } catch (ClassCastException aException) {
        throw new SchedulerException(
            "SchedulerMethodJob.setParameter: Can't convert a minute value");
      }
    }

    // Check hour values
    if (startHours == null) {
      startHours = new Vector();
    }

    for (vectorEnumerator = startHours.elements(); vectorEnumerator
        .hasMoreElements();) {
      try {
        workInt = ((Integer) vectorEnumerator.nextElement()).intValue();

        if ((workInt < 0) || (workInt > 23)) {
          throw new SchedulerException(
              "SchedulerMethodJob.setParameter: A hour value is out of range");
        }
      } catch (ClassCastException aException) {
        throw new SchedulerException(
            "SchedulerMethodJob.setParameter: Can't convert a hour value");
      }
    }

    // Check day of month values
    if (startDaysOfMonth == null) {
      startDaysOfMonth = new Vector();
    }

    for (vectorEnumerator = startDaysOfMonth.elements(); vectorEnumerator
        .hasMoreElements();) {
      try {
        workInt = ((Integer) vectorEnumerator.nextElement()).intValue();

        if ((workInt < 1) || (workInt > 31)) {
          throw new SchedulerException(
              "SchedulerMethodJob.setParameter: A day of month value is out of range");
        }
      } catch (ClassCastException aException) {
        throw new SchedulerException(
            "SchedulerMethodJob.setParameter: Can't convert a day of month value");
      }
    }

    // Check month values and normalize them for internal usage
    if (startMonths == null) {
      startMonths = new Vector();
    }

    workVector = new Vector();
    for (vectorEnumerator = startMonths.elements(); vectorEnumerator
        .hasMoreElements();) {
      try {
        workInt = ((Integer) vectorEnumerator.nextElement()).intValue();

        if ((workInt < 1) || (workInt > 12)) {
          throw new SchedulerException(
              "SchedulerMethodJob.setParameter: A month value is out of range");
        }

        workVector.add(new Integer(workInt - 1)); // Internal: zero based
      } catch (ClassCastException aException) {
        throw new SchedulerException(
            "SchedulerMethodJob.setParameter: Can't convert a month value");
      }
    }
    startMonths = workVector;

    // Check day of week values
    if (startDaysOfWeek == null) {
      startDaysOfWeek = new Vector();
    }

    workVector = new Vector();
    for (vectorEnumerator = startDaysOfWeek.elements(); vectorEnumerator
        .hasMoreElements();) {
      try {
        workInt = ((Integer) vectorEnumerator.nextElement()).intValue();

        if ((workInt < 0) || (workInt > 6)) {
          throw new SchedulerException(
              "SchedulerMethodJob.setParameter: A day of week value is out of range");
        }

        // Conversion not realy necessary, but what if SUN changes the
        // implementation .... :-))
        switch (workInt) {
          case 0:
            workVector.add(new Integer(Calendar.SUNDAY));
            break;
          case 1:
            workVector.add(new Integer(Calendar.MONDAY));
            break;
          case 2:
            workVector.add(new Integer(Calendar.TUESDAY));
            break;
          case 3:
            workVector.add(new Integer(Calendar.WEDNESDAY));
            break;
          case 4:
            workVector.add(new Integer(Calendar.THURSDAY));
            break;
          case 5:
            workVector.add(new Integer(Calendar.FRIDAY));
            break;
          case 6:
            workVector.add(new Integer(Calendar.SATURDAY));
            break;
        }
      } catch (ClassCastException aException) {
        throw new SchedulerException(
            "SchedulerMethodJob.setParameter: Can't convert a day of week value");
      }
    }
    startDaysOfWeek = workVector;

    // Assign the calculated values
    vMinutes = startMinutes;
    vHours = startHours;
    vDaysOfMonth = startDaysOfMonth;
    vMonths = startMonths;
    vDaysOfWeek = startDaysOfWeek;

    // Sort the calculated vectors
    sortCronVectors();
  }

  /**
   * This method sets the scheduling parameter. It is given by a cron like
   * string (currently ranges are not allowed). So the string '* 3,21 * 3 0'
   * starts the execution every Sunday in March on 03:00 and 21:00. The allowed
   * ranges are: minutes (0-59), hours (0-23), days of a month (1-31), months
   * (1-12; starts with 1 for January), day of a week (0-6; starts with 0 for
   * Sunday). Currently the parsing of the cron string ist not done by a state
   * machine but by StringTokenizers so this method is <B>very</B> sensitive for
   * syntax failures!
   * 
   */
  protected synchronized void setSchedulingParameter(String aCronString)
      throws SchedulerException {
    StringTokenizer fieldSeparator;
    StringTokenizer fieldContentSeparator;
    String workString;
    int workInt;

    if (aCronString == null) {
      throw new SchedulerException(
          "SchedulerShellJob.setCronString: Parameter 'aCronString' is null");
    }

    // Reset current values
    vMinutes = new Vector();
    vHours = new Vector();
    vDaysOfMonth = new Vector();
    vMonths = new Vector();
    vDaysOfWeek = new Vector();

    // This StringTokenizer splits the cron string into time fields
    fieldSeparator = new StringTokenizer(aCronString);

    // Get minute values
    if (fieldSeparator.hasMoreTokens()) {
      // This StringTokenizer splits each timefield list into single numbers
      fieldContentSeparator = new StringTokenizer(fieldSeparator.nextToken(),
          ",");
      while (fieldContentSeparator.hasMoreTokens()) {
        workString = fieldContentSeparator.nextToken();

        // Check ingnore token
        if (workString.equals("*")) {
          vMinutes = new Vector();
          break;
        }

        // Check integer value
        try {
          workInt = Integer.parseInt(workString);
        } catch (NumberFormatException aException) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: Can't convert a minute value");
        }

        if ((workInt < 0) || (workInt > 59)) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: A minute value is out of range");
        }

        vMinutes.add(new Integer(workInt));
      }
    }

    // Get hour values
    if (fieldSeparator.hasMoreTokens()) {
      fieldContentSeparator = new StringTokenizer(fieldSeparator.nextToken(),
          ",");
      while (fieldContentSeparator.hasMoreTokens()) {
        workString = fieldContentSeparator.nextToken();

        // Check ingnore token
        if (workString.equals("*")) {
          vHours = new Vector();
          break;
        }

        // Check iteger value
        try {
          workInt = Integer.parseInt(workString);
        } catch (NumberFormatException aException) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: Can't convert a hour value");
        }

        if ((workInt < 0) || (workInt > 23)) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: A hour value is out of range");
        }

        vHours.add(new Integer(workInt));
      }
    }

    // Get day of month values and normalize them for internal usage
    if (fieldSeparator.hasMoreTokens()) {
      fieldContentSeparator = new StringTokenizer(fieldSeparator.nextToken(),
          ",");
      while (fieldContentSeparator.hasMoreTokens()) {
        workString = fieldContentSeparator.nextToken();

        // Check ingnore token
        if (workString.equals("*")) {
          vDaysOfMonth = new Vector();
          break;
        }

        // Check iteger value
        try {
          workInt = Integer.parseInt(workString);
        } catch (NumberFormatException aException) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: Can't convert a day of month value");
        }

        if ((workInt < 1) || (workInt > 31)) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: A day of month value is out of range");
        }

        vDaysOfMonth.add(new Integer(workInt));
      }
    }

    // Get month values
    if (fieldSeparator.hasMoreTokens()) {
      fieldContentSeparator = new StringTokenizer(fieldSeparator.nextToken(),
          ",");
      while (fieldContentSeparator.hasMoreTokens()) {
        workString = fieldContentSeparator.nextToken();

        // Check ingnore token
        if (workString.equals("*")) {
          vMonths = new Vector();
          break;
        }

        // Check iteger value
        try {
          workInt = Integer.parseInt(workString);
        } catch (NumberFormatException aException) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: Can't convert a month value");
        }

        if ((workInt < 1) || (workInt > 12)) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: A month value is out of range");
        }

        vMonths.add(new Integer(workInt - 1)); // Internal: zero based
      }
    }

    // Get day of week values
    if (fieldSeparator.hasMoreTokens()) {
      fieldContentSeparator = new StringTokenizer(fieldSeparator.nextToken(),
          ",");
      while (fieldContentSeparator.hasMoreTokens()) {
        workString = fieldContentSeparator.nextToken();

        // Check ingnore token
        if (workString.equals("*")) {
          vDaysOfWeek = new Vector();
          break;
        }

        // Check iteger value
        try {
          workInt = Integer.parseInt(workString);
        } catch (NumberFormatException aException) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: Can't convert a day of week value");
        }

        if ((workInt < 0) || (workInt > 6)) {
          throw new SchedulerException(
              "SchedulerShellJob.setCronString: A day of week value is out of range");
        }

        switch (workInt) {
          case 0:
            vDaysOfWeek.add(new Integer(Calendar.SUNDAY));
            break;
          case 1:
            vDaysOfWeek.add(new Integer(Calendar.MONDAY));
            break;
          case 2:
            vDaysOfWeek.add(new Integer(Calendar.TUESDAY));
            break;
          case 3:
            vDaysOfWeek.add(new Integer(Calendar.WEDNESDAY));
            break;
          case 4:
            vDaysOfWeek.add(new Integer(Calendar.THURSDAY));
            break;
          case 5:
            vDaysOfWeek.add(new Integer(Calendar.FRIDAY));
            break;
          case 6:
            vDaysOfWeek.add(new Integer(Calendar.SATURDAY));
            break;
        }
      }
    }

    if (fieldSeparator.hasMoreTokens()) {
      throw new SchedulerException(
          "SchedulerShellJob.setCronString: Too much time fields in cron string");
    }

    if (vDaysOfWeek == null) {
      throw new SchedulerException(
          "SchedulerShellJob.setCronString: Not enough time fields in cron string");
    }

    // Sort the calculated vectors
    sortCronVectors();
  }

  /**
   * Generates a new timestamp
   */
  protected long getNextTimeStamp() {
    Calendar calcCalendar;
    long currentTime;
    boolean validTimeStamp;
    boolean carryMinute;
    boolean carryHour;
    boolean carryDayOfMonth;
    boolean carryMonth;
    boolean firstYearAccess;

    calcCalendar = Calendar.getInstance();

    SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        "Current TimeStamp: "
            + logDateFormat.format(new Date(
                getMillisecondsOfCalendar(calcCalendar))));

    currentTime = getMillisecondsOfCalendar(calcCalendar);

    calcCalendar.set(Calendar.YEAR, currentYear.intValue());
    calcCalendar.set(Calendar.MONTH, currentMonth.intValue());
    calcCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth.intValue());
    calcCalendar.set(Calendar.HOUR_OF_DAY, currentHour.intValue());
    calcCalendar.set(Calendar.MINUTE, currentMinute.intValue());

    SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        "Start TimeStamp: "
            + logDateFormat.format(new Date(
                getMillisecondsOfCalendar(calcCalendar))));

    // !!!!!! The values must be ordered ascend !!!!!
    validTimeStamp = false;
    carryMinute = false;
    carryHour = false;
    carryDayOfMonth = false;
    carryMonth = false;
    firstYearAccess = true;

    while (!validTimeStamp) {
      // Get new minute
      if (vMinutes.size() == 0) // Default ('*') -> Hit every minute
      {
        /*
         * if (currentMinute.intValue () < calcCalendar.getActualMaximum
         * (Calendar.MINUTE)) { currentMinute = new Integer
         * (currentMinute.intValue () + 1); carryMinute = false; carryHour =
         * false; carryDayOfMonth = false; carryMonth = false; } else {
         * currentMinute = new Integer (calcCalendar.getActualMinimum
         * (Calendar.MINUTE)); carryMinute = true; }
         */

        // If the cron setting for minutes is *, we don't have to care about
        // incrementing minutes
        // So do a carryHour
        carryMinute = true;
      } else {
        // Special handling for lists with one element
        if (vMinutes.size() == 1) {
          currentMinute = (Integer) vMinutes.elementAt(0);
          carryMinute = !carryMinute;
          if (!carryMinute) {
            carryHour = false;
            carryDayOfMonth = false;
            carryMonth = false;
          }
        } else {
          int indexOfMinutes = vMinutes.indexOf(currentMinute);
          if ((indexOfMinutes == -1)
              || (indexOfMinutes == (vMinutes.size() - 1))) {
            currentMinute = (Integer) vMinutes.elementAt(0);
            carryMinute = true;
          } else {
            currentMinute = (Integer) vMinutes.elementAt(indexOfMinutes + 1);
            carryMinute = false;
            carryHour = false;
            carryDayOfMonth = false;
            carryMonth = false;
          }
        }
      }
      calcCalendar.set(Calendar.MINUTE, currentMinute.intValue());
      // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
      // "MINUTE: " + Integer.toString(currentMinute.intValue()));

      // Get new hour
      if (carryMinute) {
        if (vHours.size() == 0) // Default ('*') -> Hit every hour
        {
          int maxHour = calcCalendar.getActualMaximum(Calendar.HOUR_OF_DAY);
          if (currentHour.intValue() < maxHour) {
            currentHour = new Integer(currentHour.intValue() + 1);
            carryHour = false;
            carryDayOfMonth = false;
            carryMonth = false;
          } else {
            currentHour = new Integer(calcCalendar
                .getActualMinimum(Calendar.HOUR_OF_DAY));
            carryHour = true;
          }
        } else {
          // Special handling for lists with one element
          if (vHours.size() == 1) {
            currentHour = (Integer) vHours.elementAt(0);
            carryHour = !carryHour;
            if (!carryHour) {
              carryDayOfMonth = false;
              carryMonth = false;
            }
          } else {
            int indexOfHours = vHours.indexOf(currentHour);
            if ((indexOfHours == -1) || (indexOfHours == (vHours.size() - 1))) {
              currentHour = (Integer) vHours.elementAt(0);
              carryHour = true;
            } else {
              currentHour = (Integer) vHours.elementAt(indexOfHours + 1);
              carryHour = false;
              carryDayOfMonth = false;
              carryMonth = false;
            }
          }
        }
        calcCalendar.set(Calendar.HOUR_OF_DAY, currentHour.intValue());
        // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        // "HOUR: " + Integer.toString(currentHour.intValue()));
      }

      // Get new day of month
      if (carryHour) {
        if (vDaysOfMonth.size() == 0) // Default ('*') -> Hit every month
        {
          int maxMonth = calcCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
          if (currentDayOfMonth.intValue() < maxMonth) {
            currentDayOfMonth = new Integer(currentDayOfMonth.intValue() + 1);
            carryDayOfMonth = false;
            carryMonth = false;
          } else {
            currentDayOfMonth = new Integer(calcCalendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));
            carryDayOfMonth = true;
          }
        } else {
          // Special handling for lists with one element
          if (vMinutes.size() == 1) {
            currentDayOfMonth = (Integer) vDaysOfMonth.elementAt(0);
            carryDayOfMonth = !carryDayOfMonth;
            if (!carryDayOfMonth) {
              carryMonth = false;
            }
          } else {
            int indexOfMonths = vDaysOfMonth.indexOf(currentDayOfMonth);
            if ((indexOfMonths == -1)
                || (indexOfMonths == (vDaysOfMonth.size() - 1))) {
              currentDayOfMonth = (Integer) vDaysOfMonth.elementAt(0);
              carryDayOfMonth = true;
            } else {
              currentDayOfMonth = (Integer) vDaysOfMonth
                  .elementAt(indexOfMonths + 1);
              carryDayOfMonth = false;
              carryMonth = false;
            }
          }
        }
        calcCalendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth.intValue());
        // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        // "DAY_OF_MONTH: " + Integer.toString(currentDayOfMonth.intValue()));
      }

      // Get new month
      if (carryDayOfMonth) {
        if (vMonths.size() == 0) // Default ('*') -> Hit every month
        {
          int maxMonth = calcCalendar.getActualMaximum(Calendar.MONTH);
          if (currentMonth.intValue() < maxMonth) {
            currentMonth = new Integer(currentMonth.intValue() + 1);
            carryMonth = false;
          } else {
            currentMonth = new Integer(calcCalendar
                .getActualMinimum(Calendar.MONTH));
            carryMonth = true;
          }
        } else {
          // Special handling for lists with one element
          if (vMinutes.size() == 1) {
            currentMonth = (Integer) vMonths.elementAt(0);
            carryMonth = !carryMonth;
          } else {
            int indexOfMonths = vMonths.indexOf(currentMonth);
            if ((indexOfMonths == -1)
                || (indexOfMonths == (vMonths.size() - 1))) {
              currentMonth = (Integer) vMonths.elementAt(0);
              carryMonth = true;
            } else {
              currentMonth = (Integer) vMonths.elementAt(indexOfMonths + 1);
              carryMonth = false;
            }
          }
        }
        calcCalendar.set(Calendar.MONTH, currentMonth.intValue());
        // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        // "MONTH: " + Integer.toString(currentMonth.intValue()));
      }

      // Get new year
      if (carryMonth) {
        // Prevent Check for the 'ever carry' of one element lists
        if ((!firstYearAccess)
            || ((currentMinute.intValue() == 0)
                && (currentHour.intValue() == 0)
                && (currentDayOfMonth.intValue() == 1) && (currentMonth
                .intValue() == 0))) {
          // Hit every year
          currentYear = new Integer(currentYear.intValue() + 1);
          calcCalendar.set(Calendar.YEAR, currentYear.intValue());
          // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
          // "YEAR: " + Integer.toString(currentYear.intValue()));
        }

        firstYearAccess = false;
      }

      // If time stamp is greater than the current time check the day of week
      if (getMillisecondsOfCalendar(calcCalendar) > currentTime) {
        // Check eventualy day movement while calculations
        if (calcCalendar.get(Calendar.DAY_OF_MONTH) == currentDayOfMonth
            .intValue()) {
          // Check for correct day of week
          if (vDaysOfWeek.size() == 0) {
            validTimeStamp = true;
          } else {
            for (Enumeration vectorEnumerator = vDaysOfWeek.elements(); vectorEnumerator
                .hasMoreElements();) {
              if (calcCalendar.get(Calendar.DAY_OF_WEEK) == ((Integer) vectorEnumerator
                  .nextElement()).intValue()) {
                validTimeStamp = true;
                break;
              }
            }
            // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
            // "!!! Invalid Day Of Week" + " : " +
            // calcCalendar.get(Calendar.DAY_OF_WEEK));
          }
        } else {
          // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
          // "!!! calcCalendar.get(Calendar.DAY_OF_MONTH) != currentDayOfMonth.intValue()"
          // + " : " + calcCalendar.get(Calendar.DAY_OF_MONTH) + " != " +
          // currentDayOfMonth.intValue());
        }
      } else {
        // SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        // "!!! getMillisecondsOfCalendar (calcCalendar) <= currentTime" + " : "
        // + getMillisecondsOfCalendar(calcCalendar) + " <= " + currentTime);
      }
    } // while (getMillisecondsOfCurrentTimeStamp (calcCalendar) < currentTime)

    SilverTrace.debug("scheduler", "SchedulerJob.getNextTimeStamp",
        "New TimeStamp: "
            + logDateFormat.format(new Date(
                getMillisecondsOfCalendar(calcCalendar))));
    return getMillisecondsOfCalendar(calcCalendar);
  }

  /**
   * Wraps calender date access
   */
  protected long getMillisecondsOfCalendar(Calendar aCalendar) {
    return aCalendar.getTime().getTime();
  }

  /**
   * Sorts the internal cron vectors nad remove doubled entries. This is
   * necessary to calculate the correct schedule time
   */
  private void sortCronVectors() {
    Collections.sort(vMinutes);
    Collections.sort(vHours);
    Collections.sort(vDaysOfMonth);
    Collections.sort(vMonths);
    Collections.sort(vDaysOfWeek);

    removeDoubled(vMinutes);
    removeDoubled(vHours);
    removeDoubled(vDaysOfMonth);
    removeDoubled(vMonths);
    removeDoubled(vDaysOfWeek);
  }

  /**
   * Removes doubled entries (Comparable) , if the list is sorted
   */
  private void removeDoubled(List aList) {
    Comparable lastComparable;
    Comparable currentComparable;

    lastComparable = null;
    for (Iterator listIterator = aList.iterator(); listIterator.hasNext();) {
      try {
        if (lastComparable == null) {
          lastComparable = (Comparable) listIterator.next();
        } else {
          currentComparable = (Comparable) listIterator.next();
          if (lastComparable.compareTo(currentComparable) == 0) {
            listIterator.remove();
          } else {
            lastComparable = currentComparable;
          }
        }
      } catch (Exception aException) {
        // Unequal
      }
    }
  }
}
