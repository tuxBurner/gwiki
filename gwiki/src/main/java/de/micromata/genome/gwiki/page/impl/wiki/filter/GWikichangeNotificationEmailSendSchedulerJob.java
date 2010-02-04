/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   14.12.2009
// Copyright Micromata 14.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiSchedulerJobBase;
import de.micromata.genome.util.runtime.CallableX;

public class GWikichangeNotificationEmailSendSchedulerJob extends GWikiSchedulerJobBase
{

  private static final long serialVersionUID = 8545136772683895346L;

  public void call()
  {
    String id = getPageId();
    GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(id);
    if (ei == null) {
      GWikiLog.warn("ChangeNotfication, pageId not found; " + id);
      return;
    }
    Properties props = GWikiChangeNotificationActionBean.getNotificationEmails(wikiContext);
    Set<String> userNames = new HashSet<String>();
    GWikiChangeNotificationFilter.findUser(wikiContext, props, ei, false, userNames);
    if (userNames.isEmpty() == true) {
      return;
    }
    String title = wikiContext.getTranslatedProp(ei.getTitle());
    final Map<String, String> ctx = new HashMap<String, String>();
    ctx.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    ctx.put(GWikiEmailProvider.SUBJECT, "GWiki; Page changed: " + title);
    String url = wikiContext.globalUrl(id);
    String body = "The Page " + title + " (" + url + ") has beend changed";
    ctx.put(GWikiEmailProvider.TEXT, body);
    for (String userName : userNames) {
      try {
        wikiContext.getWikiWeb().getAuthorization().runAsUser(userName, wikiContext, new CallableX<T, RuntimeException>() {

          public T call() throws RuntimeException
          {
            String email = wikiContext.getWikiWeb().getAuthorization().getCurrentUserEmail(wikiContext);
            if (StringUtils.isEmpty(email) == true) {
              return null;
            }
            ctx.put(GWikiEmailProvider.TO, email);
            wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(ctx);
            return null;
          }
        });
      } catch (AuthorizationFailedException ex) {
        GWikiLog.warn("Cannot determine email for user: " + userName, ex);
      }
    }

  }

}