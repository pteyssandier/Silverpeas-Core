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
package com.stratelia.silverpeas.pdcPeas.servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.silverpeas.interestCenter.model.InterestCenter;
import com.stratelia.silverpeas.pdc.model.Axis;
import com.stratelia.silverpeas.pdc.model.SearchAxis;
import com.stratelia.silverpeas.pdc.model.SearchContext;
import com.stratelia.silverpeas.pdc.model.SearchCriteria;
import com.stratelia.silverpeas.pdc.model.Value;
import com.stratelia.silverpeas.pdcPeas.control.PdcSearchSessionController;
import com.stratelia.silverpeas.pdcPeas.model.QueryParameters;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.searchEngine.model.MatchingIndexEntry;

public class PdcSearchRequestRouterHelper {

  public static QueryParameters saveUserChoicesAndSetPdcInfo(
      PdcSearchSessionController pdcSC, HttpServletRequest request,
      boolean setPdcInfo) throws Exception {
    QueryParameters queryParameters = saveUserChoices(pdcSC, request);
    setUserChoices(request, pdcSC);
    setAttributesAdvancedSearch(pdcSC, request, setPdcInfo);
    if (setPdcInfo) {
      setPertinentAxis(pdcSC, request);
      setContext(pdcSC, request);
    }
    return queryParameters;
  }

  public static QueryParameters saveFavoriteRequestAndSetPdcInfo(
      PdcSearchSessionController pdcSC, HttpServletRequest request)
      throws Exception {
    String favoriteRequestId = (String) request.getParameter("iCenterId");
    return saveFavoriteRequestAndSetPdcInfo(pdcSC, request, favoriteRequestId);
  }

  public static QueryParameters saveFavoriteRequestAndSetPdcInfo(
      PdcSearchSessionController pdcSC, HttpServletRequest request,
      String favoriteRequestId) throws Exception {
    // this parameter is for Back Button on result page
    String urlToRedirect = (String) request.getParameter("urlToRedirect");
    request.setAttribute("urlToRedirect", urlToRedirect);

    // load settings of selected Interest center
    InterestCenter ic = pdcSC.loadICenter(favoriteRequestId);
    QueryParameters queryParameters = saveFavoriteRequest(pdcSC, ic);
    setUserChoices(request, pdcSC);
    setAttributesAdvancedSearch(pdcSC, request, true);
    setPertinentAxis(pdcSC, request);
    setContext(pdcSC, request);

    return queryParameters;
  }

  public static QueryParameters saveFavoriteRequest(
      PdcSearchSessionController pdcSC, InterestCenter favoriteRequest)
      throws Exception {
    String query = favoriteRequest.getQuery();
    String spaceId = favoriteRequest.getWorkSpaceID();
    String componentId = favoriteRequest.getPeasID();
    String authorSearch = favoriteRequest.getAuthorID();
    Date afterdate = favoriteRequest.getAfterDate();
    Date beforedate = favoriteRequest.getBeforeDate();

    if (spaceId != null)
      spaceId = spaceId.trim();
    if (componentId != null)
      componentId = componentId.trim();
    if (authorSearch != null)
      authorSearch = authorSearch.trim();

    SilverTrace.debug("pdcPeas",
        "PdcPeasRequestRouterHelper.saveFavoriteRequest()",
        "root.MSG_GEN_PARAM_VALUE", "authorSearch = " + authorSearch);

    QueryParameters queryParameters = pdcSC.getQueryParameters();
    queryParameters.setKeywords(query);
    queryParameters.setSpaceId(spaceId);
    queryParameters.setInstanceId(componentId);
    queryParameters.setCreatorId(authorSearch);
    queryParameters.setAfterDate(afterdate);
    queryParameters.setBeforeDate(beforedate);

    return queryParameters;
  }

  public static QueryParameters saveUserChoices(
      PdcSearchSessionController pdcSC, HttpServletRequest request)
      throws Exception {
    // build information for the home jsp for the advancedsearch plain text
    // We get user choices about advanced search and store it in the
    // PdcSearchSessionController
    String query = (String) request.getParameter("query");

    QueryParameters queryParameters = pdcSC.getQueryParameters();
    queryParameters.setKeywords(query);

    if (pdcSC.getSearchType() >= PdcSearchSessionController.SEARCH_ADVANCED) {
      String spaceId = request.getParameter("spaces");
      String componentId = request.getParameter("componentSearch");
      String authorSearch = request.getParameter("authorSearch");
      String afterdate = request.getParameter("afterdate");
      String beforedate = request.getParameter("beforedate");

      SilverTrace.debug("pdcPeas",
          "PdcPeasRequestRouterHelper.saveUserChoices()",
          "root.MSG_GEN_PARAM_VALUE", "authorSearch = " + authorSearch);

      queryParameters.setSpaceId(spaceId);
      queryParameters.setInstanceId(componentId);
      queryParameters.setCreatorId(authorSearch);
      queryParameters.setAfterDate(afterdate);
      queryParameters.setBeforeDate(beforedate);
    }

    // CBO : ADD
    String paramNbResToDisplay = (String) request.getParameter("nbRes");
    if (paramNbResToDisplay != null) {
      int nbResToDisplay = new Integer(paramNbResToDisplay).intValue();
      pdcSC.setNbResToDisplay(nbResToDisplay);
    }
    String paramSortRes = (String) request.getParameter("sortRes");
    if (paramSortRes != null) {
      int sortRes = new Integer(paramSortRes).intValue();
      pdcSC.setSortValue(sortRes);
    }
    String paramSortOrder = (String) request.getParameter("sortOrder");
    if (paramSortOrder != null) {
      pdcSC.setSortOrder(paramSortOrder);
    }

    return queryParameters;
  }

  /**
   * Get user choices from the PdcSearchSessionController and put it in the Request
   */
  public static void setUserChoices(HttpServletRequest request,
      PdcSearchSessionController pdcSC) throws Exception {

    QueryParameters queryParameters = pdcSC.getQueryParameters();

    if (queryParameters != null) {
      String authorSearch = queryParameters.getCreatorId();
      // travail sur l'auteur
      if (authorSearch != null) {
        UserDetail userDetail = pdcSC.getOrganizationController()
            .getUserDetail(authorSearch);
        queryParameters.setCreatorDetail(userDetail);
      }
      request.setAttribute("QueryParameters", queryParameters);
    }

    // CBO : Add
    request.setAttribute("DisplayParamChoices", pdcSC.getDisplayParamChoices());
    request.setAttribute("ChoiceNbResToDisplay", pdcSC
        .getListChoiceNbResToDisplay());
    request.setAttribute("NbResToDisplay", new Integer(pdcSC
        .getNbResToDisplay()));
    request.setAttribute("SortValue", new Integer(pdcSC.getSortValue()));
    request.setAttribute("SortOrder", pdcSC.getSortOrder());

    // List of user favorite requests
    List favoriteRequests = buildICentersList(pdcSC);
    String requestSelected = request.getParameter("iCenterId");
    request.setAttribute("RequestList", favoriteRequests);
    if (requestSelected != null) {
      request.setAttribute("RequestSelected", requestSelected);
    }

    String showAllAxis = (String) request
        .getParameter("showNotOnlyPertinentAxisAndValues");
    if ("true".equals(showAllAxis)) {
      pdcSC.setShowOnlyPertinentAxisAndValues(false);
      request.setAttribute("showAllAxis", "true");
    } else {
      pdcSC.setShowOnlyPertinentAxisAndValues(true);
    }

    Map synonyms = pdcSC.getSynonyms();
    request.setAttribute("synonyms", synonyms);

    // put search type
    request.setAttribute("SearchType", new Integer(pdcSC.getSearchType()));
  }

  /**
   * set attributes into the request Attributes are build by information about both
   * sessioncontroller
   */
  public static void setAttributesAdvancedSearch(
      PdcSearchSessionController pdcSC, HttpServletRequest request,
      boolean setSpacesAndComponents) throws Exception {
    String selectedSpace = null;
    String selectedComponent = null;

    QueryParameters queryParameters = pdcSC.getQueryParameters();
    if (queryParameters != null) {
      selectedSpace = queryParameters.getSpaceId();
      selectedComponent = queryParameters.getInstanceId();
    }

    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setAttributesAdvancedSearch()",
        "root.MSG_GEN_PARAM_VALUE", "selectedSpace = " + selectedSpace);
    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setAttributesAdvancedSearch()",
        "root.MSG_GEN_PARAM_VALUE", "selectedComponent = " + selectedComponent);

    if (setSpacesAndComponents) {
      request.setAttribute("SpaceList", pdcSC.getAllowedSpaces());

      if (selectedSpace != null) {
        request.setAttribute("ComponentList", pdcSC
            .getAllowedComponents(selectedSpace));
      }
    }

    if (!pdcSC.isSelectionActivated()) {
      pdcSC.buildComponentListWhereToSearch(selectedSpace, selectedComponent);
    }

    // The selection is active ?
    request.setAttribute("ActiveSelection", new Boolean(pdcSC
        .isSelectionActivated()));
  }

  /**
   * put in the request the primary axis and eventually the secondary axis accroding to search
   * context
   */
  public static void setPertinentAxis(PdcSearchSessionController pdcSC,
      HttpServletRequest request) throws Exception {
    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setPertinentAxis()",
        "root.MSG_GEN_ENTER_METHOD");

    String showSecondarySearchAxis = request.getParameter("ShowSndSearchAxis");

    // does the user want to see secondary axis ?
    if (showSecondarySearchAxis == null) {
      showSecondarySearchAxis = pdcSC.getSecondaryAxis();
    } else {
      pdcSC.setSecondaryAxis(showSecondarySearchAxis);
    }

    SearchAxis axis = null;

    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setAttributesAdvancedSearch()",
        "root.MSG_GEN_PARAM_VALUE", "avant getAxis(P)");

    // we get primary and eventually secondary axis
    List primarySearchAxis = pdcSC.getAxis("P");
    for (int p = 0; p < primarySearchAxis.size(); p++) {
      axis = (SearchAxis) primarySearchAxis.get(p);
      axis.setValues(pdcSC.getDaughterValues(
          Integer.toString(axis.getAxisId()), "0"));
    }

    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setAttributesAdvancedSearch()",
        "root.MSG_GEN_PARAM_VALUE", "avant getAxis(S)");
    List secondarySearchAxis = null;
    if ((showSecondarySearchAxis != null)
        && (showSecondarySearchAxis.equals("YES"))) {
      // user wants to see secondary axis
      secondarySearchAxis = pdcSC.getAxis("S");
      for (int s = 0; s < secondarySearchAxis.size(); s++) {
        axis = (SearchAxis) secondarySearchAxis.get(s);
        axis.setValues(pdcSC.getDaughterValues(Integer.toString(axis
            .getAxisId()), "0"));
      }
    }

    // We set axis into the request
    request.setAttribute("ShowPrimaryAxis", primarySearchAxis);
    request.setAttribute("ShowSecondaryAxis", secondarySearchAxis);
    request.setAttribute("ShowSndSearchAxis", pdcSC.getSecondaryAxis());

    SilverTrace.info("pdcPeas",
        "PdcPeasRequestRouterHelper.setPertinentAxis()",
        "root.MSG_GEN_EXIT_METHOD");
  }

  /**
   * put in the request the primary axis and eventually the secondary axis accroding to search
   * context
   */
  public static void setContext(PdcSearchSessionController pdcSC,
      HttpServletRequest request) throws Exception {
    SilverTrace.info("pdcPeas", "PdcPeasRequestRouterHelper.setContext()",
        "root.MSG_GEN_ENTER_METHOD");

    // on retire du searchcontext tous les criteres qui ne sont pas dans
    // l'espace choisi par l'utilisateur.
    // Dans ce cas, on retire de la list de searchContext, le critere de
    // recherche.
    SearchContext searchContext = pdcSC.getSearchContext();

    List primaryAxis = (List) request.getAttribute("ShowPrimaryAxis");
    List secondaryAxis = (List) request.getAttribute("ShowSecondaryAxis");
    boolean isExistInPrimaryAxis = false;
    boolean isExistInSecondaryAxis = false;
    SearchAxis sa = null;

    ArrayList c = searchContext.getCriterias();
    Axis axis = null;
    int searchAxisId;
    String searchValue = "";
    String treeId = "";
    SearchCriteria sc = null;
    ArrayList pathCriteria = new ArrayList();
    if (c.size() > 0) {
      for (int i = 0; i < c.size(); i++) {
        sc = (SearchCriteria) c.get(i);
        searchAxisId = sc.getAxisId();

        if (primaryAxis != null) {
          // on parcourt la liste des axes primaires
          // si l'on trouve un axisId de searchCriteria = axisId de l'axe
          // primaire alors on le laisse
          // dans le searchCriteria sinon on le supprime
          int j = 0;
          for (; j < primaryAxis.size(); j++) {
            sa = (SearchAxis) primaryAxis.get(j);
            if (searchAxisId == sa.getAxisId()) {
              isExistInPrimaryAxis = true;
              break;
            }
          }

        }
        if ((!isExistInPrimaryAxis) && (secondaryAxis != null)) {
          // on parcourt la liste des axes secondaires
          // si l'on trouve un axisId de searchCriteria = axisId de l'axe
          // secondaire alors on l'enleve
          for (int j = 0; j < secondaryAxis.size(); j++) {
            sa = (SearchAxis) secondaryAxis.get(j);
            if (searchAxisId == sa.getAxisId()) {
              isExistInSecondaryAxis = true;
              break;
            }
          }
        }

        if (isExistInSecondaryAxis || isExistInPrimaryAxis) {
          searchValue = getLastValueOf(sc.getValue());
          // on creait un axis
          axis = pdcSC.getAxisDetail(new Integer(searchAxisId).toString());
          treeId = new Integer(axis.getAxisHeader().getRootId()).toString();
          List fullPath = pdcSC.getFullPath(searchValue, treeId);
          pathCriteria.add(fullPath);
        }
      }
    }
    request.setAttribute("PathCriteria", pathCriteria);
    // on ajoute le contexte de recherche
    request.setAttribute("SearchContext", searchContext);
    SilverTrace.info("pdcPeas", "PdcPeasRequestRouterHelper.setContext()",
        "root.MSG_GEN_EXIT_METHOD");
  }

  public static String getLastValueOf(String path) {
    // cherche l'id de la valeur
    // valeur de la forme /0/1/2/

    String newValueId = path;
    int len = path.length();
    path = path.substring(0, len - 1); // on retire le slash

    if (path.equals("/")) {
      newValueId = newValueId.substring(1);// on retire le slash
    } else {
      int lastIdx = path.lastIndexOf("/");
      newValueId = path.substring(lastIdx + 1);
    }
    return newValueId;
  }

  private static List buildICentersList(PdcSearchSessionController pdcSC) {
    List icList = (List) pdcSC.getICenters();
    return icList;
  }

  public static void processItemsPagination(String function,
      PdcSearchSessionController pdcSC, HttpServletRequest request) {
    String index = (String) request.getParameter("Index");
    if (index != null && index.length() > 0)
      pdcSC.setIndexOfFirstItemToDisplay(index);

    request.setAttribute("ResultList", pdcSC.getPDCResults());
    request.setAttribute("NbItemsPerPage", new Integer(pdcSC
        .getNbItemsPerPage()));
    request.setAttribute("FirstItemIndex", new Integer(pdcSC
        .getIndexOfFirstItemToDisplay()));

    Value value = pdcSC.getCurrentValue();
    request.setAttribute("SelectedValue", value);
  }

  public static void processSearchDomains(PdcSearchSessionController pdcSC,
      HttpServletRequest request, String currentDomain) {
    // Set search domains
    request.setAttribute("searchDomains", pdcSC.getSearchDomains());
    request.setAttribute("currentSearchDomainId", currentDomain);
  }

  /**
   * Checks the list of result and marks a result as read
   * @param pdcSC PdcSearchSessionController object
   * @param request HttpRequest object
   */
  public static void markResultAsRead(PdcSearchSessionController pdcSC,
      HttpServletRequest request) {
    String resultId = request.getParameter("id");
    if (StringUtils.isNotEmpty(resultId)) {
      List currentEntries = pdcSC.getIndexEntries();
      if (currentEntries != null && !currentEntries.isEmpty()) {
        for (Object entry : currentEntries) {
          if (resultId.endsWith(((MatchingIndexEntry) entry).getPK().toString()))
            ((MatchingIndexEntry) entry).setHasRead(true);
        }
      }

    }

  }
}