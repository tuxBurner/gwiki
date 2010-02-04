/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.12.2009
// Copyright Micromata 08.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiTextContentArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.PropUtils;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;

public class GWikiChangeNotificationActionBean extends ActionBeanBase
{
  private String pageId;

  private boolean recursive;

  private String delPageId;

  private boolean validUser = false;

  private String userName;

  private boolean alreadyRegistered = false;

  private Map<String, Boolean> users = new HashMap<String, Boolean>();

  /**
   * first id, second title
   */
  private Map<String, Pair<String, Boolean>> registerdNotifications = Collections.emptyMap();

  public static final String ChangeNotificationsPageId = "edit/ChangeNotifications";

  public static final String METATEMPLATEID = "admin/templates/intern/ChangeNotificationMetaTemplate";

  /**
   * TODO gwiki ggf. in tools auslagern.
   * 
   * @param id
   * @param metaTemplateId
   * @param title
   * @return
   */

  private GWikiElement createNewElement()
  {
    GWikiElement el = GWikiWebUtils.createNewElement(wikiContext, ChangeNotificationsPageId, METATEMPLATEID, "Change notfications");
    return el;
  }

  public static Properties getNotificationEmails(GWikiContext wikiContext)
  {
    GWikiElement el = wikiContext.getWikiWeb().findElement(ChangeNotificationsPageId);
    if (el == null) {
      return new Properties();
    }
    return propertiesFromChangeNotfications(el);
  }

  public static Properties propertiesFromChangeNotfications(GWikiElement el)
  {
    GWikiTextContentArtefakt art = (GWikiTextContentArtefakt) el.getMainPart();
    String alltext = art.getStorageData();
    Properties props = PropUtils.toProperties(alltext);
    return props;
  }

  public static Map<String, Boolean> getNotificationEmailsForPage(GWikiContext wikiContext, String pageId)
  {
    Properties props = getNotificationEmails(wikiContext);

    return getNotificationEmailsForPage(wikiContext, pageId, props);
  }

  public static Map<String, Boolean> getNotificationEmailsForPage(GWikiContext wikiContext, String pageId, Properties props)
  {
    String text = props.getProperty(pageId);
    if (StringUtils.isEmpty(text) == true) {
      return new HashMap<String, Boolean>();
    }
    return parseNotLine(text);
  }

  public static SortedMap<String, Boolean> parseNotLine(String line)
  {
    SortedMap<String, Boolean> ret = new TreeMap<String, Boolean>();
    List<String> tks = Converter.parseStringTokens(line, ", ", false);
    for (String tk : tks) {
      List<String> t = Converter.parseStringTokens(tk, "=", false);
      if (t.size() == 2) {
        ret.put(t.get(0), Boolean.valueOf(t.get(1)));
      } else {
        ret.put(t.get(0), Boolean.FALSE);
      }
    }
    return ret;
  }

  /**
   * 
   * @param wikiContext
   * @param props
   * @param email
   * @return PageId -> Title, Recursive
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Pair<String, Boolean>> getNotificationPagesForEmail(GWikiContext wikiContext, Properties props, String email)
  {
    Map<String, Pair<String, Boolean>> ret = new TreeMap<String, Pair<String, Boolean>>();
    Map<String, String> m = (Map<String, String>) (Map) props;
    for (Map.Entry<String, String> me : m.entrySet()) {
      SortedMap<String, Boolean> pm = parseNotLine(me.getValue());
      if (pm.containsKey(email) == false) {
        continue;
      }

      GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(me.getKey());
      if (ei == null) {
        continue;
      }
      ret.put(me.getKey(), Pair.make(wikiContext.getTranslatedProp(ei.getTitle()), pm.get(email)));
    }
    return ret;
  }

  private void storeNotfications()
  {

    GWikiElement el = wikiContext.getWikiWeb().findElement(ChangeNotificationsPageId);
    if (el == null) {
      el = createNewElement();
    }

    Properties props = propertiesFromChangeNotfications(el);

    String text = props.getProperty(pageId);
    if (text == null) {
      text = "";
    }
    if (users.isEmpty() == true) {
      props.remove(pageId);
    } else {
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, Boolean> me : users.entrySet()) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(me.getKey()).append("=").append(Boolean.toString(me.getValue()));
      }
      props.put(pageId, sb.toString());
    }
    String data = PropUtils.fromProperties(props);
    GWikiTextContentArtefakt art = (GWikiTextContentArtefakt) el.getMainPart();
    art.setStorageData(data);
    wikiContext.getWikiWeb().saveElement(wikiContext, el, false);
    initFromProps(props);
  }

  protected void initFromProps(Properties props)
  {
    registerdNotifications = getNotificationPagesForEmail(wikiContext, props, userName);
    users = getNotificationEmailsForPage(wikiContext, pageId, props);
    validUser = true;
    alreadyRegistered = users.containsKey(userName) == true;
  }

  protected boolean init()
  {
    userName = wikiContext.getWikiWeb().getAuthorization().getCurrentUserName(wikiContext);
    if (StringUtils.isBlank(pageId) == true) {
      wikiContext.addSimpleValidationError("Keine gültige Seite definiert.");
      return false;
    }
    if (StringUtils.isBlank(userName) == true) {
      wikiContext.addSimpleValidationError("Sie haben im Profil keine Email definiert.");
      return false;
    }
    Properties props = getNotificationEmails(wikiContext);
    initFromProps(props);
    return true;
  }

  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    return null;
  }

  public Object onRegister()
  {
    if (init() == false) {
      return null;
    }
    users.put(userName, recursive);
    storeNotfications();
    return pageId;
  }

  public Object onUnregister()
  {
    if (init() == false) {
      return null;
    }
    users.remove(userName);
    storeNotfications();
    return pageId;
  }

  public Object onUnregisterSel()
  {
    pageId = delPageId;
    onUnregister();
    return null;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public boolean isValidUser()
  {
    return validUser;
  }

  public void setValidUser(boolean validUser)
  {
    this.validUser = validUser;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String email)
  {
    this.userName = email;
  }

  public boolean isAlreadyRegistered()
  {
    return alreadyRegistered;
  }

  public void setAlreadyRegistered(boolean alreadyRegistered)
  {
    this.alreadyRegistered = alreadyRegistered;
  }

  public Set<Map.Entry<String, Pair<String, Boolean>>> getRegisterdNotificationEntries()
  {
    return registerdNotifications.entrySet();
  }

  public String getDelPageId()
  {
    return delPageId;
  }

  public void setDelPageId(String delPageId)
  {
    this.delPageId = delPageId;
  }

  public boolean isRecursive()
  {
    return recursive;
  }

  public void setRecursive(boolean recursive)
  {
    this.recursive = recursive;
  }

  public Map<String, Pair<String, Boolean>> getRegisterdNotifications()
  {
    return registerdNotifications;
  }

  public void setRegisterdNotifications(Map<String, Pair<String, Boolean>> registerdNotifications)
  {
    this.registerdNotifications = registerdNotifications;
  }

}