/*
 * Created on 5 ao�t 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.stratelia.silverpeas.silverStatisticsPeas.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.jCharts.nonAxisChart.PieChart2D;

import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.Admin;
import com.stratelia.webactiv.beans.admin.AdminException;
import com.stratelia.webactiv.beans.admin.ComponentInstLight;
import com.stratelia.webactiv.beans.admin.SpaceInstLight;
import com.stratelia.webactiv.beans.admin.UserDetail;

/**
 * @author BERTINL
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPieChartBuilder {
  private static final int LEGEND_MAX_LENGTH = 20;

  Hashtable statsByInstance = null;
  Admin admin = new Admin();
  static final String FINESSE_TOUS = "FINESSE_TOUS";
  static final String FINESSE_GROUPE = "FINESSE_GROUPE";
  static final String FINESSE_USER = "FINESSE_USER";
  String niveau_finesse = FINESSE_TOUS;

  private void buildStatsByInstance() {

    // 0 - init new hashtable
    statsByInstance = new Hashtable();

    // 1 - Get stats for KM access
    Hashtable cmpStats = getCmpStats(); // Hashtable key=componentId, value=new
    // String[3] {tout, groupe, user}

    // 2 - build statItems
    Iterator it = cmpStats.keySet().iterator();
    String[] tabValue = null;
    while (it.hasNext()) {
      String cmpId = (String) it.next();

      tabValue = (String[]) cmpStats.get(cmpId);
      ComponentInstLight cmp = null;
      try {
        cmp = admin.getComponentInstLight(cmpId);
      } catch (AdminException e) {
        SilverTrace.error("silverStatisticsPeas",
            "AbstractPieChartBuilder.buildStatsByInstance()",
            "root.EX_SQL_QUERY_FAILED", e);
      }
      if (cmp != null) {
        long[] countValues = new long[3];
        countValues[0] = 0;
        countValues[1] = 0;
        countValues[2] = 0;

        if (tabValue[0] != null)
          countValues[0] = Long.parseLong(tabValue[0]);
        if (tabValue[1] != null)
          countValues[1] = Long.parseLong(tabValue[1]);
        if (tabValue[2] != null)
          countValues[2] = Long.parseLong(tabValue[2]);

        StatItem item = new StatItem(cmpId, cmp.getLabel(), countValues);
        statsByInstance.put(cmpId, item);
      }
    }

    if (tabValue != null) {
      if (tabValue[1] != null)
        niveau_finesse = FINESSE_GROUPE;
      if (tabValue[2] != null)
        niveau_finesse = FINESSE_USER;
    }
  }

  private boolean isIdBelongsTo(String spaceId, String[] tabAllSpaceIds) {
    // spaceId de type WA123
    if ((spaceId != null) && (!spaceId.startsWith("WA"))) {
      spaceId = "WA" + spaceId;
    }

    String theSpaceId;
    for (int i = 0; i < tabAllSpaceIds.length; i++) {
      theSpaceId = tabAllSpaceIds[i]; // de type WA123
      if ((theSpaceId != null) && (!theSpaceId.startsWith("WA"))) {
        theSpaceId = "WA" + theSpaceId;
      }
      if (spaceId.equals(theSpaceId))
        return true;
    }
    return false;
  }

  public PieChart2D getChart(String spaceId, String currentUserId,
      Vector currentStats) {
    PieChart2D pieChart = null;
    try {
      // Get statistics
      if (statsByInstance == null)
        buildStatsByInstance();

      // Get subspaces ids and components
      Collection listSpaceIds;
      String[] tabSpaceIds = null; // de type WA123
      String[] componentIds = null;

      // build instance list
      UserDetail userDetail = admin.getUserDetail(currentUserId);
      String manageableSpaceId;
      SpaceInstLight espace;
      String idEspace;
      int level;
      boolean trouve = false;

      if ((spaceId == null) || (spaceId.length() == 0)) {
        if (userDetail.getAccessLevel().equals("A")) {// Admin
          tabSpaceIds = admin.getAllRootSpaceIds(); // de type WA123
        } else {// Manager d'espaces ou de sous-espaces

          // manager d'espace
          listSpaceIds = new ArrayList();
          String[] tabManageableSpaceIds = admin
              .getUserManageableSpaceIds(currentUserId); // de type 123
          for (int i = 0; i < tabManageableSpaceIds.length; i++) {
            manageableSpaceId = tabManageableSpaceIds[i];
            espace = admin.getSpaceInstLightById(manageableSpaceId);
            level = espace.getLevel();

            idEspace = manageableSpaceId;
            trouve = false;
            while (level > 0) {
              idEspace = espace.getFatherId();
              espace = admin.getSpaceInstLightById(idEspace);
              level--;
              if (isIdBelongsTo(idEspace, tabManageableSpaceIds)) {
                trouve = true;
                break;
              }
            }

            if (!trouve) {
              listSpaceIds.add(manageableSpaceId);
            }
          }
          tabSpaceIds = (String[]) listSpaceIds.toArray(new String[listSpaceIds
              .size()]);
        }
        componentIds = new String[0];
      } else {
        tabSpaceIds = admin.getAllSubSpaceIds(spaceId); // de type WA123
        componentIds = admin.getAllComponentIds(spaceId);
      }

      // build data
      Vector legend = new Vector();
      Vector counts = new Vector();
      currentStats.clear();

      long count1 = 0;
      long count2 = 0;
      long count3 = 0;
      SpaceInstLight space;
      String[] allComponentsIds;

      // first managesubspaces
      for (int i = 0; i < tabSpaceIds.length; i++) {
        count1 = 0;
        count2 = 0;
        count3 = 0;
        space = admin.getSpaceInstLightById(tabSpaceIds[i]);
        allComponentsIds = admin.getAllComponentIdsRecur(tabSpaceIds[i]);

        for (int j = 0; j < allComponentsIds.length; j++) {
          StatItem item = (StatItem) statsByInstance.get(allComponentsIds[j]);
          if (item != null) {
            count1 += item.getCountValues()[0];
            count2 += item.getCountValues()[1];
            count3 += item.getCountValues()[2];
          }
        }

        if (FINESSE_TOUS.equals(niveau_finesse))
          counts.add(String.valueOf(count1));
        else if (FINESSE_GROUPE.equals(niveau_finesse))
          counts.add(String.valueOf(count2));
        else if (FINESSE_USER.equals(niveau_finesse))
          counts.add(String.valueOf(count3));

        legend.add("["
            + ((space.getName().length() > LEGEND_MAX_LENGTH) ? space.getName()
                .substring(0, LEGEND_MAX_LENGTH) : space.getName() + "]"));
        currentStats.add(new String[] { "SPACE", tabSpaceIds[i],
            space.getName(), String.valueOf(count1), String.valueOf(count2),
            String.valueOf(count3) });
      }

      // then manage components
      for (int i = 0; i < componentIds.length; i++) {
        StatItem item = (StatItem) statsByInstance.get(componentIds[i]);
        if (item != null) {
          count1 = item.getCountValues()[0];
          count2 = item.getCountValues()[1];
          count3 = item.getCountValues()[2];

          if (FINESSE_TOUS.equals(niveau_finesse))
            counts.add(String.valueOf(count1));
          else if (FINESSE_GROUPE.equals(niveau_finesse))
            counts.add(String.valueOf(count2));
          else if (FINESSE_USER.equals(niveau_finesse))
            counts.add(String.valueOf(count3));

          legend.add((item.getName().length() > LEGEND_MAX_LENGTH) ? item
              .getName().substring(0, LEGEND_MAX_LENGTH) : item.getName());
          currentStats.add(new String[] { "CMP", componentIds[i],
              item.getName(), String.valueOf(count1), String.valueOf(count2),
              String.valueOf(count3) });
        }
      }

      pieChart = ChartUtil.buildPieChart(getChartTitle(),
          buildDoubleArrayFromStringCollection(counts), (String[]) legend
              .toArray(new String[0]));
    } catch (Exception e) {
      SilverTrace.error("silverStatisticsPeas",
          "AbstractPieChartBuilder.getChart()", "root.EX_SQL_QUERY_FAILED", e);
    }

    return pieChart;

  }

  /**
   * @param collection
   * @return
   */
  private double[] buildDoubleArrayFromStringCollection(Collection collection) {
    double[] result = new double[collection.size()];
    int i = 0;
    Iterator it = collection.iterator();
    while (it.hasNext()) {
      String value = (String) it.next();
      result[i++] = Double.parseDouble(value);
    }

    return result;
  }

  public abstract String getChartTitle();

  abstract Hashtable getCmpStats(); // Hashtable key=componentId, value=new
  // String[3] {tout, groupe, user}
}
