////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementLink;

/**
 * Render a help link.
 * 
 * gwikidocs/help/HelpPage -> lang de gwikicos/help/de/HelpPage gwikicos/help/HelpPage gwikicos/help/en/HelpPage
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHelpLinkMacro extends GWikiMacroBean
{

  private static final long serialVersionUID = -5790766666703611334L;

  /**
   * Page id of the help page. Optional.
   * 
   * If not set use HELP_PAGE of current element or HELP_PAGE of Metatemplate or common help page of GWiki.
   */
  private String helpPageId;

  /**
   * Optional. If not set current page.
   */
  private String pageId;

  /**
   * Optional. if not set, language of current page or user language preference.
   */
  private String lang;

  /**
   * Do not render, if no spezial help page was found.
   */
  private boolean noCommonHelp = false;

  /**
   * Optional Title
   */
  private String title;

  /**
   * Optional class used for the link.
   */
  private String linkClass;

  public String getCoreHelpPage(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (helpPageId != null) {
      return helpPageId;
    }
    String localHelpPageId = null;
    if (ei == null) {
      return null;
    }
    localHelpPageId = ei.getProps().getStringValue(GWikiPropKeys.HELP_PAGE);
    if (localHelpPageId != null) {
      return localHelpPageId;
    }
    if (ei.getMetaTemplate() == null) {
      return null;
    }
    localHelpPageId = ei.getMetaTemplate().getHelpPageId();
    if (localHelpPageId != null) {
      return localHelpPageId;
    }
    if (noCommonHelp == false) {
      localHelpPageId = ctx.getWikiWeb().getWikiConfig().getStringValue(GWikiGlobalConfig.GWIKI_COMMON_HELP);
    }
    return localHelpPageId;
  }

  public String getLang(GWikiContext ctx, GWikiElementInfo ei)
  {
    if (lang != null) {
      return lang;
    }
    String pageLang = ei.getLang(ctx);
    if (pageLang != null) {
      return pageLang;
    }
    Locale loc = ctx.getWikiWeb().getAuthorization().getCurrentUserLocale(ctx);
    if (loc != null) {
      return loc.getLanguage();
    }
    return null;
  }

  public String getLangHelpPage(GWikiContext ctx, GWikiElementInfo ei)
  {
    helpPageId = getCoreHelpPage(ctx, ei);
    if (helpPageId == null) {
      return null;
    }
    if (StringUtils.isEmpty(helpPageId) == true) {
      return null;
    }
    lang = getLang(ctx, ei);
    if (lang == null) {
      return helpPageId;
    }
    String hp = FileNameUtils.getParentDir(helpPageId);
    String fp = FileNameUtils.getNamePart(helpPageId);
    String lp = FileNameUtils.join(hp, lang, fp);
    GWikiElementInfo lpi = ctx.getWikiWeb().findElementInfo(lp);
    if (lpi != null) {
      return lpi.getId();
    }
    lpi = ctx.getWikiWeb().findElementInfo(helpPageId);
    if (lpi != null) {
      return lpi.getId();
    }
    lp = FileNameUtils.join(hp, "en", fp);
    lpi = ctx.getWikiWeb().findElementInfo(lp);
    if (lpi != null) {
      return lpi.getId();
    }
    return null;
  }

  public String getLink(GWikiContext ctx)
  {
    if (pageId == null) {
      pageId = ctx.getCurrentElement().getElementInfo().getId();
    }
    GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo(pageId);
    if (ei == null) {
      return null;
    }
    String hpi = getLangHelpPage(ctx, ei);
    if (hpi == null) {
      return null;
    }
    GWikiElementInfo helpEi = ctx.getWikiWeb().findElementInfo(hpi);
    if (helpEi == null) {
      return null;
    }
    if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, helpEi) == false) {
      return null;
    }
    return hpi;
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    String link = getLink(ctx);
    if (link == null) {
      return true;
    }
    GWikiFragementLink lf = new GWikiFragementLink(link);
    lf.setTitle(title);
    lf.setLinkClass(linkClass);
    lf.render(ctx);
    return true;
  }

  public String getHelpPageId()
  {
    return helpPageId;
  }

  public void setHelpPageId(String helpPageId)
  {
    this.helpPageId = helpPageId;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

  public boolean isNoCommonHelp()
  {
    return noCommonHelp;
  }

  public void setNoCommonHelp(boolean noCommonHelp)
  {
    this.noCommonHelp = noCommonHelp;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getLinkClass()
  {
    return linkClass;
  }

  public void setLinkClass(String linkClass)
  {
    this.linkClass = linkClass;
  }

}