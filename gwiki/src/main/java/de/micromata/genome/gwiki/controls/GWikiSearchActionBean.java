/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.matcher.InvalidMatcherGrammar;

/**
 * ActionBean for searching.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiSearchActionBean extends ActionBeanBase
{
  /**
   * Set as hidden parameter with prefix.
   */
  private String searchPrefix;

  private String searchExpression;

  private List<SearchResult> foundPages;

  private String searchMessage = "";

  private String backUrl;

  private int searchOffset;

  private int pageSize = 10;

  private int totalFound = 0;

  private String pageUrlArgs = "";

  protected void initFromParams()
  {
    StringBuilder sb = new StringBuilder();
    for (String cmd : wikiContext.getWikiWeb().getContentSearcher().getSearchMacros()) {
      String val = wikiContext.getRequestParameter(cmd);
      if (StringUtils.isBlank(val) == true) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(cmd).append(":").append(val);
    }
    if (StringUtils.isBlank(searchExpression) == true) {
      if (StringUtils.isNotBlank(wikiContext.getRequestParameter("se")) == true) {
        searchExpression = wikiContext.getRequestParameter("se");
      }
    }

    if (StringUtils.isBlank(searchExpression) == true) {
      searchExpression = sb.toString();
    } else if (sb.toString().length() == 0) {
      ;
    } else {
      searchPrefix = sb.toString();
      // searchExpression = sb.toString() + " and(" + searchExpression + ")";
    }
  }

  protected String buildSearchExpression()
  {
    if (StringUtils.isBlank(searchPrefix) == true) {
      return searchExpression;
    } else if (StringUtils.isBlank(searchExpression) == true) {
      return searchPrefix;
    } else {
      return searchPrefix + " and(" + searchExpression + ")";
    }
  }

  public Object onInit()
  {
    initFromParams();
    return null;
  }

  public Object onSearch()
  {
    initFromParams();
    String se = buildSearchExpression();
    if (StringUtils.isBlank(se) == true) {
      wikiContext.addSimpleValidationError("Kein Suchausdruck angegeben");
      return null;
    }
    Collection<GWikiElementInfo> webInfos = wikiContext.getWikiWeb().getPageInfos().values();
    List<SearchResult> sr = new ArrayList<SearchResult>(webInfos.size());
    for (GWikiElementInfo wi : webInfos) {
      sr.add(new SearchResult(wi));
    }
    SearchQuery query = new SearchQuery(se, true, sr);
    query.setSearchOffset(searchOffset);
    query.setMaxCount(pageSize);
    try {
      QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
      if (qr.getLookupWords().isEmpty() == false) {
        pageUrlArgs = "?_gwhiwords=" + WebUtils.encodeUrlParam(StringUtils.join(qr.getLookupWords(), ","));
      }
      foundPages = qr.getResults();
      totalFound = qr.getTotalFoundItems();
      searchMessage = qr.getTotalFoundItems()
          + " Seiten gefunden (von "
          + wikiContext.getWikiWeb().getPageInfos().size()
          + ") in "
          + (qr.getSearchTime())
          + " ms. Zeige "
          + searchOffset
          + " bis "
          + (searchOffset + (pageSize < foundPages.size() ? pageSize : foundPages.size()))
          + " an";

    } catch (InvalidMatcherGrammar ex) {
      wikiContext.addSimpleValidationError(ex.getMessage());
    }
    return null;
  }

  public String getSearchExpression()
  {
    return searchExpression;
  }

  public void setSearchExpression(String searchExpression)
  {
    this.searchExpression = searchExpression;
  }

  public List<SearchResult> getFoundPages()
  {
    return foundPages;
  }

  public void setFoundPages(List<SearchResult> foundPages)
  {
    this.foundPages = foundPages;
  }

  public String getSearchMessage()
  {
    return searchMessage;
  }

  public void setSearchMessage(String searchMessage)
  {
    this.searchMessage = searchMessage;
  }

  public String getSearchPrefix()
  {
    return searchPrefix;
  }

  public void setSearchPrefix(String searchPrefix)
  {
    this.searchPrefix = searchPrefix;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }

  public int getSearchOffset()
  {
    return searchOffset;
  }

  public void setSearchOffset(int searchOffset)
  {
    this.searchOffset = searchOffset;
  }

  public int getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(int pageSize)
  {
    this.pageSize = pageSize;
  }

  public int getTotalFound()
  {
    return totalFound;
  }

  public void setTotalFound(int totalFound)
  {
    this.totalFound = totalFound;
  }

  public String getPageUrlArgs()
  {
    return pageUrlArgs;
  }

  public void setPageUrlArgs(String lookupWords)
  {
    this.pageUrlArgs = lookupWords;
  }

}