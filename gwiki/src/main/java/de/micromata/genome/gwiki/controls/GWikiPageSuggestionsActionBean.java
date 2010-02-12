/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.11.2009
// Copyright Micromata 14.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;

/**
 * ActionBean for Ajax page autocompletion.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageSuggestionsActionBean extends GWikiPageListActionBean
{

  public GWikiPageSuggestionsActionBean()
  {

  }

  @Override
  public Object onInit()
  {
    return onLinkAutocomplete();
  }

  public Object onLinkAutocomplete()
  {
    String q = wikiContext.getRequestParameter("q");
    String pageType = wikiContext.getRequestParameter("pageType");
    String queryexpr = "prop:PAGEID ~ \"" + q + "\" or prop:TITLE ~ \"" + q + "\"";
    if (StringUtils.isNotEmpty(pageType) == true) {
      if (pageType.equals("image") == true) {
        pageType = "attachment";
      }
      queryexpr = "prop:TYPE = " + pageType + " and (" + queryexpr + ")";
    }
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb().getPageInfos());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    StringBuilder sb = new StringBuilder();
    // int size = qr.getResults().size();
    for (SearchResult sr : qr.getResults()) {
      sb.append(sr.getPageId()).append("|").append(wikiContext.getTranslatedProp(sr.getElementInfo().getTitle())).append("\n");
    }
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

}
