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

package de.micromata.genome.gwiki.model.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiGlobalConfig;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.plugin.GWikiPluginFilterDescriptor;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Filter registered in GWiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFilters
{
  private Set<Class< ? >> knownFilter = new HashSet<Class< ? >>();

  private List<GWikiServeElementFilter> serveElementFilters = new ArrayList<GWikiServeElementFilter>();

  private List<GWikiWikiPageRenderFilter> renderWikiPageFilters = new ArrayList<GWikiWikiPageRenderFilter>();

  private List<GWikiWikiPageCompileFilter> wikiCompileFilters = new ArrayList<GWikiWikiPageCompileFilter>();

  private List<GWikiLoadElementInfosFilter> loadPageInfosFilters = new ArrayList<GWikiLoadElementInfosFilter>();

  private List<GWikiStorageStoreElementFilter> storageStoreElementFilters = new ArrayList<GWikiStorageStoreElementFilter>();

  private List<GWikiStorageDeleteElementFilter> storageDeleteElementFilters = new ArrayList<GWikiStorageDeleteElementFilter>();

  private List<GWikiPageChangedFilter> pageChangedFilters = new ArrayList<GWikiPageChangedFilter>();

  public ThreadLocal<List<Class< ? >>> skipFilters = new ThreadLocal<List<Class< ? >>>() {

    @Override
    protected List<Class< ? >> initialValue()
    {
      return new ArrayList<Class< ? >>();
    }

  };

  public <R> R runWithoutFilters(Class< ? >[] filtersToSkip, CallableX<R, RuntimeException> callback)
  {
    List<Class< ? >> of = skipFilters.get();
    List<Class< ? >> nf = new ArrayList<Class< ? >>();
    nf.addAll(of);
    for (Class< ? > cls : filtersToSkip) {
      nf.add(cls);
    }
    skipFilters.set(nf);
    try {
      return callback.call();
    } finally {
      skipFilters.set(of);
    }
  }

  public <R, E extends GWikiFilterEvent, F extends GWikiFilter<R, E, F>> boolean skipFilter(GWikiFilter<R, E, F> filter)
  {
    List<Class< ? >> fscp = skipFilters.get();
    for (Class< ? > cls : fscp) {
      if (cls.isAssignableFrom(filter.getClass()) == true) {
        return true;
      }
    }
    return false;
  }

  public void storeElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt< ? >> parts, GWikiStorageStoreElementFilter target)
  {
    GWikiStorageStoreElementFilterEvent event = new GWikiStorageStoreElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageStoreElementFilterEvent, GWikiStorageStoreElementFilter>(this, target,
        storageStoreElementFilters).nextFilter(event);
  }

  public void deleteElement(GWikiContext ctx, GWikiElement el, Map<String, GWikiArtefakt< ? >> parts, GWikiStorageDeleteElementFilter target)
  {
    GWikiStorageDeleteElementFilterEvent event = new GWikiStorageDeleteElementFilterEvent(ctx, el, parts);
    new GWikiFilterChain<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter>(this, target,
        storageDeleteElementFilters).nextFilter(event);
  }

  public void serveElement(final GWikiContext ctx, final GWikiElement el, final GWikiServeElementFilter target)
  {
    ctx.runElement(el, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiServeElementFilterEvent event = new GWikiServeElementFilterEvent(ctx, el);

        new GWikiFilterChain<Void, GWikiServeElementFilterEvent, GWikiServeElementFilter>(GWikiFilters.this, target, serveElementFilters)
            .nextFilter(event);
        return null;
      }
    });

  }

  public Boolean renderWikiWikiPage(GWikiContext ctx, GWikiWikiPageArtefakt artefakt, GWikiWikiPageRenderFilter target)
  {
    GWikiWikiPageRenderFilterEvent event = new GWikiWikiPageRenderFilterEvent(ctx, artefakt);
    return new GWikiFilterChain<Boolean, GWikiWikiPageRenderFilterEvent, GWikiWikiPageRenderFilter>(this, target, renderWikiPageFilters)
        .nextFilter(event);
  }

  public void compileWikiWikiPage(GWikiContext ctx, GWikiElement element, GWikiWikiPageArtefakt artefakt, GWikiWikiPageCompileFilter target)
  {
    GWikiWikiPageCompileFilterEvent event = new GWikiWikiPageCompileFilterEvent(ctx, element, artefakt);
    new GWikiFilterChain<Void, GWikiWikiPageCompileFilterEvent, GWikiWikiPageCompileFilter>(this, target, wikiCompileFilters)
        .nextFilter(event);
  }

  public void pageChanged(GWikiContext ctx, GWikiWeb wikiWeb, GWikiElementInfo newElement, GWikiElementInfo oldElement)
  {
    if (newElement != null) {
      String id = newElement.getId();
      if (oldElement != null) {
        GWikiLog.note("Page changed:" + id);
      } else {
        GWikiLog.note("Page New:" + id);
      }
    } else if (oldElement != null) {
      GWikiLog.note("Page Deleted:" + oldElement.getId());
    }
    pageChanged(ctx, wikiWeb, newElement, oldElement, new GWikiPageChangedFilter() {

      public Void filter(GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter> chain,
          GWikiPageChangedFilterEvent event)
      {
        return null;
      }
    });
  }

  public void pageChanged(GWikiContext ctx, GWikiWeb wikiWeb, GWikiElementInfo newElement, GWikiElementInfo oldElement,
      GWikiPageChangedFilter target)
  {
    GWikiPageChangedFilterEvent event = new GWikiPageChangedFilterEvent(ctx, wikiWeb, newElement, oldElement);
    new GWikiFilterChain<Void, GWikiPageChangedFilterEvent, GWikiPageChangedFilter>(this, target, pageChangedFilters).nextFilter(event);
  }

  public Map<String, GWikiElementInfo> loadPageInfos(GWikiContext ctx, Map<String, GWikiElementInfo> npageInfos,
      GWikiLoadElementInfosFilter target)
  {
    GWikiLoadElementInfosFilterEvent event = new GWikiLoadElementInfosFilterEvent(ctx, npageInfos);
    new GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter>(this, target, loadPageInfosFilters)
        .nextFilter(event);
    return event.getPageInfos();
  }

  public void registerNewFilterClass(GWikiWeb wikiWeb, Class< ? > filter)
  {
    Object obj = ClassUtils.createDefaultInstance(filter);
    registerNewFilterObject(obj);
    if (obj instanceof GWikiFilterInit) {
      ((GWikiFilterInit) obj).init(wikiWeb);
    }
  }

  public void registerNewFilterObject(Object obj)
  {
    if (obj instanceof GWikiServeElementFilter) {
      serveElementFilters.add(0, (GWikiServeElementFilter) obj);
    }
    if (obj instanceof GWikiWikiPageRenderFilter) {
      renderWikiPageFilters.add(0, (GWikiWikiPageRenderFilter) obj);
    }
    if (obj instanceof GWikiLoadElementInfosFilter) {
      loadPageInfosFilters.add(0, (GWikiLoadElementInfosFilter) obj);
    }
    if (obj instanceof GWikiWikiPageCompileFilter) {
      wikiCompileFilters.add(0, (GWikiWikiPageCompileFilter) obj);
    }
    if (obj instanceof GWikiStorageStoreElementFilter) {
      storageStoreElementFilters.add(0, (GWikiStorageStoreElementFilter) obj);
    }
    if (obj instanceof GWikiStorageDeleteElementFilter) {
      storageDeleteElementFilters.add(0, (GWikiStorageDeleteElementFilter) obj);
    }
    if (obj instanceof GWikiPageChangedFilter) {
      pageChangedFilters.add(0, (GWikiPageChangedFilter) obj);
    }
  }

  private Set<String> getCreateSet(Map<String, Set<String>> m, String k)
  {
    Set<String> ret = m.get(k);
    if (ret != null) {
      return ret;
    }
    ret = new HashSet<String>();
    m.put(k, ret);
    return ret;
  }

  public List<String> getSortedClasses(List<String> regClasses, final List<GWikiPluginFilterDescriptor> pluginFilters)
  {
    final Map<String, Set<String>> afterMap = new HashMap<String, Set<String>>();

    List<String> allFilters = new ArrayList<String>();
    if (regClasses != null) {
      allFilters.addAll(regClasses);
    }
    for (GWikiPluginFilterDescriptor pfd : pluginFilters) {
      allFilters.add(pfd.getClassName());
      if (pfd.getAfter() != null) {
        for (String as : pfd.getAfter()) {
          getCreateSet(afterMap, pfd.getClassName()).add(as);
        }
      }
      if (pfd.getBefore() != null) {
        for (String as : pfd.getBefore()) {
          getCreateSet(afterMap, as).add(pfd.getClassName());
        }
      }
    }
    Collections.sort(allFilters, new Comparator<String>() {

      public int compare(String o1, String o2)
      {
        if (afterMap.containsKey(o1) == true && afterMap.get(o1).contains(o2) == true) {
          return 1;
        }
        if (afterMap.containsKey(o2) == true && afterMap.get(o2).contains(o1) == true) {
          return -1;
        }
        return 0;
      }
    });
    return allFilters;
  }

  public void init(GWikiWeb wikiWeb, GWikiGlobalConfig wikiConfig)
  {
    List<String> regClasses = wikiConfig.getStringList("GWIKI_FILTER_CLASSES");
    final List<GWikiPluginFilterDescriptor> pluginFilters = wikiWeb.getDaoContext().getPluginRepository().getPluginFilters();
    List<String> allClasses = getSortedClasses(regClasses, pluginFilters);
    for (String rc : allClasses) {
      rc = StringUtils.trim(rc);
      if (StringUtils.isEmpty(rc) == true) {
        continue;
      }
      try {
        registerFilter(wikiWeb, ClassUtils.classForName(rc));
      } catch (Throwable ex) {
        GWikiLog.warn("Cannot register filter class: " + rc + "; " + ex.getMessage(), ex);
      }
    }
  }

  public void registerFilter(GWikiWeb wikiWeb, Class< ? > filter)
  {
    if (knownFilter.contains(filter) == true) {
      return;
    }
    registerNewFilterClass(wikiWeb, filter);
  }
}
