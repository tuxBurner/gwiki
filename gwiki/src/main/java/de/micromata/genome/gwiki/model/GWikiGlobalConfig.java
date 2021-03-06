//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletConfig;

import de.micromata.genome.gwiki.model.config.GWikiDAOContextPropertyPlaceholderConfigurer;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.matcher.GWikiPageIdMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiConfigElement;
import de.micromata.genome.gwiki.page.impl.GWikiFileAttachment;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPage;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiScriptMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.macros.registry.GWikiMacroProviderRegistry;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.string.StartWithMatcher;
import de.micromata.genome.util.text.PlaceHolderReplacer;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Global Wiki configuration backing bean.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiGlobalConfig extends GWikiProps
{

  private static final long serialVersionUID = -3700759128425586958L;

  public static final String GWIKI_GLOBAL_CONFIG_PATH = "admin/config/GWikiConfig";

  public static final String GWIKI_PUBLIC_URL = "GWIKI_PUBLIC_URL";

  public static final String GWIKI_SEND_EMAIL = "GWIKI_SEND_EMAIL";

  public static final String GWIKI_ERROR_SHOW_STACK_TRACE = "GWIKI_ERROR_SHOW_STACK_TRACE";

  public static final String GWIKI_CHECK_STORAGE_MODTIMEOUT = "GWIKI_CHECK_STORAGE_MODTIMEOUT";

  /**
   * boolean if files should be checked, if they are modified outside the file system.
   * 
   * Only set this in development mode, if wiki files are edited outside the wiki itselv.
   */
  public static final String GWIKI_CHECK_EXTERNAL_MODIFICATIONS = "GWIKI_CHECK_EXTERNAL_MODIFICATIONS";

  public static final String GWIKI_FS_WRITE_ACCESSRULES = "GWIKI_FS_WRITE_ACCESSRULES";

  public static final String GWIKI_WIKI_MACROS = "GWIKI_WIKI_MACROS";

  public static final String GWIKI_WIKI_USER_SKINS = "GWIKI_WIKI_USER_SKINS";

  public static final String GWIKI_WIKI_DEFAULT_SKIN = "GWIKI_WIKI_DEFAULT_SKIN";

  public static final String GWIKI_WIKI_LANGUAGES = "GWIKI_WIKI_LANGUAGES";

  public static final String GWIKI_COMMON_HELP = "GWIKI_COMMON_HELP";

  public static final String GWIKI_WELCOME_PAGE = "GWIKI_WELCOME_PAGE";

  public static final String GWIKI_WIKI_ELEMENTS = "GWIKI_WIKI_ELEMENTS";

  /**
   * List of plugin ids
   */
  public static final String GWIKI_ACTIVE_PLUGINS = "GWIKI_ACTIVE_PLUGINS";

  public static final String GWIKI_ARCHIVE_MAX_COUNT = "GWIKI_ARCHIVE_MAX_COUNT";

  public static final String GWIKI_ARCHIVE_MAX_DAYS = "GWIKI_ARCHIVE_MAX_DAYS";

  private List<Pair<String, Matcher<String>>> writeAccessRules = null;

  private Map<String, GWikiMacroFactory> wikiFactories;

  private Map<String, GWikiElementFactory> elementFactories;

  private static Map<String, GWikiElementFactory> defaultElementFactories = new HashMap<String, GWikiElementFactory>();

  private List<String> availableSkins = null;
  static {
    defaultElementFactories.put("gwiki", new GWikiClassElementFactory(GWikiWikiPage.class));
    defaultElementFactories.put("config", new GWikiClassElementFactory(GWikiConfigElement.class));
    defaultElementFactories.put("attachment", new GWikiClassElementFactory(GWikiFileAttachment.class));
    defaultElementFactories.put("i18n", new GWikiClassElementFactory(GWikiI18nElement.class));
  }

  public GWikiGlobalConfig()
  {
    super();
  }

  public GWikiGlobalConfig(GWikiProps other)
  {
    super(other);
  }

  public GWikiGlobalConfig(Map<String, String> map)
  {
    super(map);
  }

  protected String resolve(String value)
  {
    if (value == null) {
      return value;
    }
    if (value.indexOf("${") == -1) {
      return value;
    }
    ServletConfig servletConfig = null;
    if (GWikiContext.getCurrent() != null) {
      servletConfig = GWikiContext.getCurrent().getServlet().getServletConfig();
    }
    GWikiDAOContextPropertyPlaceholderConfigurer cf = new GWikiDAOContextPropertyPlaceholderConfigurer(servletConfig);

    return PlaceHolderReplacer.resolveReplace(value, "${", "}", cf);
  }

  public String getPublicURL()
  {
    return resolve(getStringValue(GWIKI_PUBLIC_URL));
  }

  public String getSendEmail()
  {
    return resolve(getStringValue(GWIKI_SEND_EMAIL));
  }

  public boolean showErrorStackTrace()
  {
    return getBooleanValue(GWIKI_ERROR_SHOW_STACK_TRACE, false);
  }

  public long getCheckFileSystemForModTimeout()
  {
    return getLongValue(GWIKI_CHECK_STORAGE_MODTIMEOUT, TimeInMillis.SECOND * 30L);
  }

  public boolean checkFileSystemForExternalMod()
  {
    return getBooleanValue(GWIKI_CHECK_EXTERNAL_MODIFICATIONS);
  }

  public String getDefaultSkin()
  {
    return StringUtils.defaultIfEmpty(getStringValue(GWIKI_WIKI_DEFAULT_SKIN), "naked");
  }

  public String getWelcomePageId()
  {
    return resolve(StringUtils.defaultIfEmpty(getStringValue(GWIKI_WELCOME_PAGE), "index"));
  }

  public int getArchiveMaxCount()
  {
    return getIntValue(GWIKI_ARCHIVE_MAX_COUNT, -1);
  }

  public int getArchiveMaxDays()
  {
    return getIntValue(GWIKI_ARCHIVE_MAX_DAYS, -1);
  }

  public List<String> getAvailableSkins(GWikiContext wikiContext)
  {
    if (availableSkins != null) {
      return availableSkins;
    }
    List<String> all = getStringList(GWIKI_WIKI_USER_SKINS);
    List<String> fl = new ArrayList<String>();
    for (String s : all) {
      if (isSkinAvailable(wikiContext, s) == true) {
        fl.add(s);
      }
    }
    availableSkins = fl;
    return availableSkins;
  }

  public List<String> getAvailableLanguages(GWikiContext context)
  {
    return this.getStringList(GWIKI_WIKI_LANGUAGES);
  }

  public List<String> getAvailableTimeZones(GWikiContext context)
  {
    return Arrays.asList(TimeZone.getAvailableIDs());
  }

  public List<String> getActivePlugins()
  {
    return this.getStringList(GWIKI_ACTIVE_PLUGINS);
  }

  protected List<Pair<String, Matcher<String>>> parseRightRules(String text)
  {
    if (StringUtils.isEmpty(text) == true) {
      return Collections.emptyList();
    }
    List<Pair<String, Matcher<String>>> ret = new ArrayList<Pair<String, Matcher<String>>>();
    List<String> lines = Converter.parseStringTokens(text, "\n", false);
    for (String l : lines) {
      String[] tks = StringUtils.splitFirst(l, ':');
      if (tks.length != 2) {
        GWikiLog.warn("GlobalConfig invalid Access rule line: " + l);
        continue;
      }
      String right = StringUtils.trim(tks[0]);
      String m = StringUtils.trim(tks[1]);
      try {
        Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher(m);
        ret.add(Pair.make(right, matcher));
      } catch (Exception ex) {
        GWikiLog.warn("GlobalConfig invalid Access rule matcher: " + l, ex);
        continue;
      }
    }
    return ret;
  }

  public boolean hasWriteAccess(GWikiContext wikiContext, String pageId)
  {
    if (writeAccessRules == null) {
      writeAccessRules = parseRightRules(getStringValue(GWIKI_FS_WRITE_ACCESSRULES));
    }
    for (Pair<String, Matcher<String>> p : writeAccessRules) {
      if (p.getSecond().match(pageId) == true) {
        if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, p.getFirst()) == false) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isSkinAvailable(GWikiContext wikiContext, String skin)
  {
    String id = "inc/" + skin + "/standardtemplate";
    return wikiContext.getWikiWeb().findElementInfo(id) != null;
  }

  protected void initScriptMacros(GWikiContext wikiContext, Map<String, GWikiMacroFactory> factories)
  {
    List<GWikiElementInfo> eis = wikiContext.getElementFinder().getPageInfos(
        new GWikiPageIdMatcher(wikiContext, new StartWithMatcher<String>("admin/macros/")));
    for (GWikiElementInfo ei : eis) {
      if (StringUtils.equals(ei.getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE),
          "admin/templates/ScriptMacroMetaTemplate") == false) {
        continue;
      }
      factories.put(GWikiContext.getNamePartFromPageId(ei.getId()), new GWikiScriptMacroFactory(ei));
    }
  }

  @SuppressWarnings("unchecked")
  public Map<String, GWikiMacroFactory> getWikiMacros(GWikiContext wikiContext)
  {

    if (wikiFactories != null) {
      return wikiFactories;
    }
    Map<String, GWikiMacroFactory> facs = GWikiMacroProviderRegistry.getMacros();
    GWikiProps macros = getStringValueMap(GWIKI_WIKI_MACROS);
    for (Map.Entry<String, String> me : macros.getMap().entrySet()) {
      try {
        String key = StringUtils.trim(me.getKey());
        String v = StringUtils.trim(me.getValue());
        int idx = v.lastIndexOf('.');
        if (idx != -1) {
          if (Character.isLowerCase(v.charAt(idx + 1)) == true) {
            GWikiMacroFactory fac = ClassUtils
                .invokeDefaultStaticMethod(GWikiMacroFactory.class, v.substring(0, idx), v.substring(idx + 1));
            facs.put(key, fac);
            continue;
          }
        }
        Class<?> cls = ClassUtils.classForName(v);
        if (GWikiMacroFactory.class.isAssignableFrom(cls) == true) {
          facs.put(key, ClassUtils.createDefaultInstance(v, GWikiMacroFactory.class));
        } else if (GWikiMacro.class.isAssignableFrom(cls) == true) {
          facs.put(key, new GWikiMacroClassFactory((Class<? extends GWikiMacro>) cls));
        } else {
          throw new RuntimeException("Incompatible Class for Macro factory: " + cls.getName());
        }
      } catch (Throwable ex) {
        GWikiLog.warn("Cannot initialize Macro: [" + me.getKey() + "]=[" + me.getValue() + "]", ex);
      }
    }
    initScriptMacros(wikiContext, facs);
    wikiContext.getWikiWeb().getDaoContext().getPluginRepository().getMacros(wikiContext, facs);
    return wikiFactories = facs;
  }

  @SuppressWarnings("unchecked")
  public Map<String, GWikiElementFactory> getElementFactories()
  {
    if (elementFactories != null) {
      return elementFactories;
    }
    elementFactories = new HashMap<String, GWikiElementFactory>();
    elementFactories.putAll(defaultElementFactories);
    GWikiProps macros = getStringValueMap(GWIKI_WIKI_ELEMENTS);
    for (Map.Entry<String, String> me : macros.getMap().entrySet()) {
      String key = StringUtils.trim(me.getKey());
      String v = StringUtils.trim(me.getValue());
      Class<?> cls = ClassUtils.classForName(v);
      if (GWikiElementFactory.class.isAssignableFrom(cls) == true) {
        elementFactories.put(key, ClassUtils.createDefaultInstance(v, GWikiElementFactory.class));
      } else if (GWikiElement.class.isAssignableFrom(cls) == true) {
        elementFactories.put(key, new GWikiClassElementFactory((Class<? extends GWikiElement>) cls));
      }
    }
    return elementFactories;
  }
}
