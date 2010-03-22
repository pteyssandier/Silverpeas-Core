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
package com.stratelia.webactiv.searchEngine.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.silverpeas.util.StringUtil;
import com.silverpeas.util.i18n.I18NHelper;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.silverpeas.util.SilverpeasSettings;
import com.stratelia.webactiv.util.DateUtil;
import com.stratelia.webactiv.util.ResourceLocator;
import com.stratelia.webactiv.util.indexEngine.model.FieldDescription;
import com.stratelia.webactiv.util.indexEngine.model.IndexEntry;
import com.stratelia.webactiv.util.indexEngine.model.IndexEntryPK;
import com.stratelia.webactiv.util.indexEngine.model.IndexManager;
import com.stratelia.webactiv.util.indexEngine.model.SpaceComponentPair;

/**
 * The WAIndexSearcher class implements search over all the WebActiv's index. A WAIndexSearcher
 * manages a set of cached lucene IndexSearcher.
 */
public class WAIndexSearcher {
  /**
   * The primary and secondary factor are used to give a better score to entries whose title or
   * abstract match the query.
   * @see #merge
   */
  private int primaryFactor = 3;
  private int secondaryFactor = 1;
  private QueryParser.Operator defaultOperand = QueryParser.OR_OPERATOR;
  /**
   * indicates the number maximum of results returned by the search
   */
  private int maxNumberResult = 0;

  /**
   * The no parameters constructor retrieves all the needed data from the IndexEngine.properties
   * file.
   */

  public WAIndexSearcher() {
    indexManager = new IndexManager();
    primaryFactor = getFactorFromProperties("PrimaryFactor", primaryFactor);
    secondaryFactor = getFactorFromProperties("SecondaryFactor",
        secondaryFactor);
    try {
      ResourceLocator resource = new ResourceLocator(
          "com.silverpeas.searchEngine.searchEngineSettings", "");
      int paramOperand = Integer.parseInt(resource.getString("defaultOperand",
          "0"));
      if (paramOperand == 0)
        defaultOperand = QueryParser.OR_OPERATOR;
      else
        defaultOperand = QueryParser.AND_OPERATOR;

      maxNumberResult = SilverpeasSettings.readInt(resource, "maxResults", 100);
    } catch (MissingResourceException e) {
    } catch (NumberFormatException e) {
    }
  }

  /**
   * Get the primary factor from the IndexEngine.properties file.
   */
  public static int getFactorFromProperties(String propertyName,
      int defaultValue) {
    int factor = defaultValue;

    try {
      ResourceLocator resource = new ResourceLocator(
          "com.stratelia.webactiv.util.indexEngine.IndexEngine", "");

      String factorString = resource.getString(propertyName);

      factor = Integer.parseInt(factorString);
    } catch (MissingResourceException e) {
    } catch (NumberFormatException e) {
    }

    return factor;
  }

  /**
   * Search the documents of the given component's set. All entries found whose startDate is not
   * reached or whose endDate is passed are pruned from the results set.
   */
  public MatchingIndexEntry[] search(QueryDescription query)
      throws com.stratelia.webactiv.searchEngine.model.ParseException {
    long startTime = System.nanoTime();
    List<MatchingIndexEntry> results = null;

    Searcher searcher = getSearcher(query.getSpaceComponentPairSet());

    try {
      if (query.getXmlQuery() != null) {
        results = makeList(getXMLHits(query, searcher), query, searcher);
      } else {
        if (query.getMultiFieldQuery() != null) {
          results = makeList(getMultiFieldHits(query, searcher), query, searcher);
        } else {
          if (!StringUtil.isDefined(query.getQuery())) {
            TopDocs topDocs = null;
            RangeQuery rangeQuery = null;
            // realizes the search on space without keywords indicated by the user
            if (query.isSearchBySpace() && !query.isPeriodDefined()) {
              String beginDate = "1900/01/01";
              String endDate = "2200/01/01";
              Term lowerTerm = new Term(IndexManager.CREATIONDATE, beginDate);
              Term upperTerm = new Term(IndexManager.CREATIONDATE, endDate);

              rangeQuery = new RangeQuery(lowerTerm, upperTerm, true);
            }
            if (query.isPeriodDefined()) {
              String beginDate = query.getRequestedCreatedAfter();
              if (!StringUtil.isDefined(beginDate)) {
                beginDate = "1977/12/13";
              }
              String endDate = query.getRequestedCreatedBefore();
              if (!StringUtil.isDefined(endDate)) {
                endDate = "2100/01/01";
              }

              Term lowerTerm = new Term(IndexManager.CREATIONDATE, beginDate);
              Term upperTerm = new Term(IndexManager.CREATIONDATE, endDate);

              rangeQuery = new RangeQuery(lowerTerm, upperTerm, true);
            }

            if (StringUtil.isDefined(query.getRequestedAuthor())) {
              Term authorTerm = new Term(IndexManager.CREATIONUSER, query.getRequestedAuthor());
              TermQuery authorQuery = new TermQuery(authorTerm);
              if (rangeQuery == null) {
                topDocs = searcher.search(authorQuery, maxNumberResult);
              } else {
                BooleanQuery booleanQuery = new BooleanQuery();
                booleanQuery.add(rangeQuery, BooleanClause.Occur.MUST);
                booleanQuery.add(authorQuery, BooleanClause.Occur.MUST);

                topDocs = searcher.search(booleanQuery, maxNumberResult);
              }
            } else {
              topDocs = searcher.search(rangeQuery, maxNumberResult);
            }

            results = makeList(topDocs, query, searcher);
          } else {
            List<MatchingIndexEntry> contentMatchingResults =
                makeList(getHits(query, IndexManager.CONTENT, searcher), query, searcher);

            if (contentMatchingResults.size() > 0) {
              List<MatchingIndexEntry> headerMatchingResults =
                  makeList(getHits(query, IndexManager.HEADER, searcher), query, searcher);

              if (headerMatchingResults.size() > 0) {
                results = merge(headerMatchingResults, primaryFactor,
                    contentMatchingResults, secondaryFactor);
              } else {
                results = contentMatchingResults;
              }
            } else {
              results = new ArrayList<MatchingIndexEntry>(); // empty results list.
            }
          }
        }
      }
    } catch (IOException ioe) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher.search()",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", ioe);
      results = new ArrayList<MatchingIndexEntry>();
    } finally {
      try {
        if (searcher != null)
          searcher.close();
      } catch (IOException ioe) {
        SilverTrace.fatal("searchEngine", "WAIndexSearcher.search()",
            "searchEngine.MSG_CANNOT_CLOSE_SEARCHER", ioe);
      }
    }
    long endTime = System.nanoTime();

    SilverTrace.debug("searchEngine", WAIndexSearcher.class.toString(), " search duration in ms " +
        (endTime - startTime) / 1000000);
    return (MatchingIndexEntry[]) results
        .toArray(new MatchingIndexEntry[results.size()]);
  }

  /**
   * Returns the lucene TopDocs of the query
   * @param query the search criteria
   * @param searchField the search field within the index.
   * @param searcher Searcher object
   * @return the TopDoc which contain the score and the index of a document
   * @throws com.stratelia.webactiv.searchEngine.model.ParseException
   */
  private TopDocs getHits(QueryDescription query, String searchField,
      Searcher searcher)
      throws com.stratelia.webactiv.searchEngine.model.ParseException {
    TopDocs topDocs = null;

    try {
      Analyzer analyzer = indexManager.getAnalyzer(Locale.getDefault()
          .getLanguage());

      String language = query.getRequestedLanguage();

      Query parsedQuery = null;
      if (I18NHelper.isI18N && "*".equals(language)) {
        // search over all languages
        String[] fields = new String[I18NHelper.getNumberOfLanguages()];
        String[] queries = new String[I18NHelper.getNumberOfLanguages()];

        int l = 0;
        Iterator<String> languages = I18NHelper.getLanguages();
        while (languages.hasNext()) {
          language = (String) languages.next();

          if (I18NHelper.isDefaultLanguage(language))
            fields[l] = searchField;
          else
            fields[l] = searchField + "_" + language;
          queries[l] = query.getQuery();
          l++;
        }

        parsedQuery = MultiFieldQueryParser.parse(queries, fields, analyzer);
      } else {
        // search only specified language
        if (I18NHelper.isI18N && !"*".equals(language)
            && !I18NHelper.isDefaultLanguage(language))
          searchField = searchField + "_" + language;

        QueryParser queryParser = new QueryParser(searchField, analyzer);
        queryParser.setDefaultOperator(defaultOperand);
        SilverTrace.info("searchEngine", "WAIndexSearcher.getHits",
            "root.MSG_GEN_PARAM_VALUE", "defaultOperand = " + defaultOperand);
        parsedQuery = queryParser.parse(query.getQuery());
        SilverTrace.info("searchEngine", "WAIndexSearcher.getHits",
            "root.MSG_GEN_PARAM_VALUE", "getOperator() = "
            + queryParser.getDefaultOperator());
      }

      SilverTrace
          .info("searchEngine", "WAIndexSearcher.getHits",
          "root.MSG_GEN_PARAM_VALUE", "parsedQuery = "
          + parsedQuery.toString());
      topDocs = searcher.search(parsedQuery, maxNumberResult);
    } catch (org.apache.lucene.queryParser.ParseException e) {
      throw new com.stratelia.webactiv.searchEngine.model.ParseException(
          "WAIndexSearcher", e);
    } catch (IOException e) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", e);
      topDocs = null;
    } catch (ArrayIndexOutOfBoundsException e) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", e);
      topDocs = null;
    }

    return topDocs;
  }

  private TopDocs getXMLHits(QueryDescription query, Searcher searcher)
      throws com.stratelia.webactiv.searchEngine.model.ParseException,
      IOException {
    TopDocs topDocs = null;

    try {
      Hashtable<String, String> xmlQuery = query.getXmlQuery();
      String xmlTitle = query.getXmlTitle();

      int nbFields = xmlQuery.size();
      if (xmlTitle != null)
        nbFields++;

      String[] fields = (String[]) xmlQuery.keySet().toArray(
          new String[nbFields]);
      String[] queries = (String[]) xmlQuery.values().toArray(
          new String[nbFields]);

      if (xmlTitle != null) {
        fields[nbFields - 1] = IndexManager.TITLE;
        queries[nbFields - 1] = xmlTitle;
      }

      Analyzer analyzer = indexManager
          .getAnalyzer(query.getRequestedLanguage());

      BooleanClause.Occur[] flags = new BooleanClause.Occur[fields.length];
      for (int f = 0; f < fields.length; f++) {
        flags[f] = BooleanClause.Occur.MUST;
      }

      Query parsedQuery = MultiFieldQueryParser.parse(queries, fields, flags,
          analyzer);
      SilverTrace
          .info("searchEngine", "WAIndexSearcher.getXMLHits",
          "root.MSG_GEN_PARAM_VALUE", "parsedQuery = "
          + parsedQuery.toString());

      topDocs = searcher.search(parsedQuery, maxNumberResult);
    } catch (org.apache.lucene.queryParser.ParseException e) {
      throw new com.stratelia.webactiv.searchEngine.model.ParseException(
          "WAIndexSearcher", e);
    } catch (IOException e) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher.getXMLHits",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", e);
      topDocs = null;
    }

    return topDocs;
  }

  private TopDocs getMultiFieldHits(QueryDescription query, Searcher searcher)
      throws com.stratelia.webactiv.searchEngine.model.ParseException,
      IOException {
    TopDocs topDocs = null;

    try {
      List<FieldDescription> fieldQueries = query.getMultiFieldQuery();
      String keyword = query.getQuery();

      int nbFields = fieldQueries.size();
      if (StringUtil.isDefined(keyword))
        nbFields++;

      String[] fields = new String[nbFields];
      String[] queries = new String[nbFields];
      BooleanClause.Occur[] flags = new BooleanClause.Occur[nbFields];

      if (StringUtil.isDefined(keyword)) {
        flags[nbFields - 1] = BooleanClause.Occur.MUST;
        fields[nbFields - 1] = IndexManager.HEADER;
        queries[nbFields - 1] = keyword;
      }

      FieldDescription fieldQuery;
      for (int f = 0; f < fieldQueries.size(); f++) {
        fieldQuery = fieldQueries.get(f);

        flags[f] = BooleanClause.Occur.MUST;
        fields[f] = fieldQuery.getFieldName();
        queries[f] = fieldQuery.getContent();
      }

      Analyzer analyzer = indexManager
          .getAnalyzer(query.getRequestedLanguage());
      Query parsedQuery = MultiFieldQueryParser.parse(queries, fields, flags,
          analyzer);
      SilverTrace
          .info("searchEngine", "WAIndexSearcher.getMultiFieldHits",
          "root.MSG_GEN_PARAM_VALUE", "parsedQuery = "
          + parsedQuery.toString());

      topDocs = searcher.search(parsedQuery, maxNumberResult);
    } catch (org.apache.lucene.queryParser.ParseException e) {
      throw new com.stratelia.webactiv.searchEngine.model.ParseException(
          "WAIndexSearcher", e);
    } catch (IOException e) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher.getMultiFieldHits",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", e);
      topDocs = null;
    }

    return topDocs;
  }

  /**
   * Makes a List of MatchingIndexEntry from a lucene hits. All entries found whose startDate is not
   * reached or whose endDate is passed are pruned from the results list.
   */
  private List<MatchingIndexEntry> makeList(TopDocs topDocs, QueryDescription query,
      Searcher searcher) throws IOException {
    List<MatchingIndexEntry> results = new ArrayList<MatchingIndexEntry>();
    String today = DateUtil.today2SQLDate();
    String user = query.getSearchingUser();
    String beforeDate = query.getRequestedCreatedBefore();
    String afterDate = query.getRequestedCreatedAfter();
    String requestedAuthor = query.getRequestedAuthor();

    SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
        "root.MSG_GEN_PARAM_VALUE", "beforeDate = " + beforeDate);
    SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
        "root.MSG_GEN_PARAM_VALUE", "afterDate = " + afterDate);

    if (topDocs != null) {
      ScoreDoc scoreDoc = null;

      for (int i = 0; i < topDocs.scoreDocs.length; i++) {
        MatchingIndexEntry indexEntry;
        scoreDoc = topDocs.scoreDocs[i];
        Document doc = searcher.doc(scoreDoc.doc);

        String startDate = doc.get(IndexManager.STARTDATE);
        String endDate = doc.get(IndexManager.ENDDATE);
        String creationDate = doc.get(IndexManager.CREATIONDATE);

        SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
            "root.MSG_GEN_PARAM_VALUE", "startDate = " + startDate);
        SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
            "root.MSG_GEN_PARAM_VALUE", "endDate = " + endDate);
        SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
            "root.MSG_GEN_PARAM_VALUE", "creationDate = " + creationDate);

        boolean testDate = true;

        if ((beforeDate != null) && (afterDate != null)) {
          if (creationDate.compareTo(afterDate) >= 0) {
            if (beforeDate.compareTo(creationDate) >= 0)
              testDate = true;
            else
              testDate = false;
          } else {
            testDate = false;
          }
          SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
              "root.MSG_GEN_PARAM_VALUE", "testDate = " + testDate);

        }
        if ((beforeDate == null) && (afterDate != null)) {
          if (creationDate.compareTo(afterDate) >= 0) {
            testDate = true;
          } else {
            testDate = false;
          }
          SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
              "root.MSG_GEN_PARAM_VALUE", "testDate = " + testDate);
        }
        if ((beforeDate != null) && (afterDate == null)) {

          if (beforeDate.compareTo(creationDate) >= 0)
            testDate = true;
          else
            testDate = false;
          SilverTrace.info("searchEngine", "WAIndexSearcher.makeList",
              "root.MSG_GEN_PARAM_VALUE", "testDate = " + testDate);
        }
        String author = doc.get(IndexManager.CREATIONUSER);

        if (startDate == null || startDate.equals("")) {
          startDate = IndexEntry.STARTDATE_DEFAULT;
        }
        if (endDate == null || endDate.equals("")) {
          endDate = IndexEntry.ENDDATE_DEFAULT;
        }

        // the document must be published onless the searcher is the author
        if ((startDate.compareTo(today) <= 0 && today.compareTo(endDate) <= 0)
            || user.equals(author)) {
          if (testDate
              && (requestedAuthor == null || requestedAuthor.equals(author))) {
            indexEntry = new MatchingIndexEntry(IndexEntryPK.create(doc
                .get(IndexManager.KEY)));

            Iterator<String> languages = I18NHelper.getLanguages();
            while (languages.hasNext()) {
              String language = languages.next();

              if (I18NHelper.isDefaultLanguage(language)) {
                indexEntry.setTitle(doc.get(IndexManager.TITLE), language);
                indexEntry.setPreview(doc.get(IndexManager.PREVIEW), language);
              } else {
                indexEntry.setTitle(doc
                    .get(IndexManager.TITLE + "_" + language), language);
                indexEntry.setPreview(doc.get(IndexManager.PREVIEW + "_"
                    + language), language);
              }
            }

            indexEntry.setKeyWords(doc.get(IndexManager.KEYWORDS));
            indexEntry.setCreationUser(doc.get(IndexManager.CREATIONUSER));
            indexEntry.setCreationDate(doc.get(IndexManager.CREATIONDATE));
            indexEntry.setThumbnail(doc.get(IndexManager.THUMBNAIL));
            indexEntry.setThumbnailMimeType(doc
                .get(IndexManager.THUMBNAIL_MIMETYPE));
            indexEntry.setThumbnailDirectory(doc
                .get(IndexManager.THUMBNAIL_DIRECTORY));
            indexEntry.setStartDate(startDate);
            indexEntry.setEndDate(endDate);
            indexEntry.setScore(scoreDoc.score); // TODO check the score.
            results.add(indexEntry);
          }
        }
      }
    }
    return results;
  }

  /**
   * Merges two MatchingIndexEntry List and re-computes the scores. The new score is :
   * 
   * <PRE>
   * primaryScore * primaryFactor + secondaryScore * secondaryFactor
   * ---------------------------------------------------------------
   * primaryFactor + primaryScore
   * </PRE>
   * 
   * If an entry is in the secondary list but not in the primary, his score is left unchanged. If
   * any, all entries in the primary list but not the secondary are ignored. In practice this case
   * should not occurs as the secondary is extracted from the CONTENT index which contains all the
   * HEADER contents from which is extracted the primary list.
   */
  private List<MatchingIndexEntry> merge(List<MatchingIndexEntry> primaryList, int primaryFactor,
      List<MatchingIndexEntry> secondaryList,
      int secondaryFactor) {
    List<MatchingIndexEntry> result = new ArrayList<MatchingIndexEntry>();

    float newScore = 0;
    MatchingIndexEntry primaryEntry = null;
    MatchingIndexEntry secondaryEntry = null;

    // Create a map key -> entry for the primaryList.
    Map<IndexEntryPK, MatchingIndexEntry> primaryMap =
        new HashMap<IndexEntryPK, MatchingIndexEntry>();
    Iterator<MatchingIndexEntry> i = primaryList.iterator();

    while (i.hasNext()) {
      primaryEntry = i.next();
      primaryMap.put(primaryEntry.getPK(), primaryEntry);
    }

    Iterator<MatchingIndexEntry> j = secondaryList.iterator();

    while (j.hasNext()) {
      secondaryEntry = j.next();

      primaryEntry = primaryMap.get(secondaryEntry.getPK());
      if (primaryEntry != null) {
        newScore = secondaryFactor * secondaryEntry.getScore();
        newScore += primaryFactor * primaryEntry.getScore();
        newScore /= (primaryFactor + secondaryFactor);
        secondaryEntry.setScore(newScore);
      }

      result.add(secondaryEntry);
    }

    Collections.sort(result, ScoreComparator.comparator);
    return result;
  }

  /**
   * The manager of all the Web'Activ index.
   */
  private final IndexManager indexManager;

  /**
   * Return a multi-searcher built on the searchers list matching the (space, component) pair set.
   */
  private Searcher getSearcher(Set<SpaceComponentPair> spaceComponentPairSet) {
    List<Searcher> searcherList = new ArrayList<Searcher>();
    Set<String> indexPathSet = getIndexPathSet(spaceComponentPairSet);

    Iterator<String> i = indexPathSet.iterator();

    while (i.hasNext()) {
      String path = i.next();
      Searcher searcher = getSearcher(path);

      if (searcher != null) {
        searcherList.add(searcher);
      }
    }

    try {
      return new MultiSearcher((Searcher[]) searcherList
          .toArray(new Searcher[searcherList.size()]));
    } catch (IOException e) {
      SilverTrace.fatal("searchEngine", "WAIndexSearcher",
          "searchEngine.MSG_CORRUPTED_INDEX_FILE", e);
      return null;
    }
  }

  /**
   * Build the set of all the path to the directories index corresponding the given (space,
   * component) pairs.
   */
  public Set<String> getIndexPathSet(Set<SpaceComponentPair> spaceComponentPairSet) {
    Set<String> pathSet = new HashSet<String>();

    Iterator<SpaceComponentPair> i = spaceComponentPairSet.iterator();

    SpaceComponentPair pair = null;
    String space = null;
    String component = null;
    while (i.hasNext()) {
      pair = i.next();
      space = pair.getSpace();
      component = pair.getComponent();

      // Both cases.
      // 1 - space == null && component != null : search in an component's
      // instance
      // 2 - space != null && component != null : search in pdc or user's
      // components (todo, agenda...)

      if (component != null) {
        pathSet.add(indexManager.getIndexDirectoryPath(space, component));
      }
    }
    return pathSet;
  }

  /**
   * Retrieve the index searcher over the specified index directory. The index readers are cached in
   * a Map (path -> (timestamp, reader)). If a reader is found in the cache but appear to be too old
   * (according to the timestamp) then it is re-open. If the index files are not found, null is
   * returned without any error (as this case comes each time a request is made on a space/component
   * without any indexed documents).
   */
  private Searcher getSearcher(String path) {
    /*
     * if (!(new File(path).exists())) { return null; } CachedIndex cached = (CachedIndex)
     * cache.get(path); try { if (cached == null || cached.timestamp !=
     * IndexReader.lastModified(path)) { if (cached != null) { cached.reader.close();
     * SilverTrace.info("searchEngine", "WAIndexSearcher", "searchEngine.INFO_REOPEN_INDEX_FILE",
     * path); } else { SilverTrace.info("searchEngine", "WAIndexSearcher",
     * "searchEngine.INFO_OPEN_INDEX_FILE", path); } cached = new CachedIndex(path); cache.put(path,
     * cached); } } catch (IOException e) { SilverTrace.error("searchEngine", "WAIndexSearcher",
     * "searchEngine.MSG_CANT_READ_INDEX_FILE", e); return null; } return new
     * IndexSearcher(cached.reader);
     */

    Searcher searcher = null;

    try {
      searcher = new IndexSearcher(path);
    } catch (IOException ioe) {
      SilverTrace.debug("searchEngine", "WAIndexSearcher.getSearcher()",
          "searchEngine.MSG_CANT_READ_INDEX_FILE", ioe);
    }

    return searcher;
  }
}