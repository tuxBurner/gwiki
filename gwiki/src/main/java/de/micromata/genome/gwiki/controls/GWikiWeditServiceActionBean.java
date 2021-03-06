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

package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import de.micromata.genome.gwiki.model.GWikiAuthorization.UserPropStorage;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.parser.WeditWikiUtils;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyConfig;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyInfo;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.gwiki.utils.ScriptUtils;
import de.micromata.genome.util.types.Pair;

/**
 * Ajax services.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWeditServiceActionBean extends ActionBeanAjaxBase
{
  public static enum SearchType
  {
    All(""), Wiki("gwiki"),

    Image("image"),

    Attachment("attachment");
    private String elmentType;

    private SearchType(String wikiType)
    {
      this.elmentType = wikiType;
    }

    public static SearchType fromString(String type)
    {
      if (StringUtils.isBlank(type) == true) {
        return All;
      }
      if (type.equals("wiki") == true) {
        type = "gwiki";
      }
      for (SearchType tp : SearchType.values()) {
        if (tp.getElementType().equals(type) == true) {
          return tp;
        }
      }
      return All;
    }

    public static SearchType fromElementInfo(GWikiContext ctx, GWikiElementInfo ei)
    {
      if (StringUtils.equals(ei.getType(), "gwiki") == true) {
        return SearchType.Wiki;
      }
      String mimetype = ctx.getWikiWeb().getDaoContext().getMimeTypeProvider().getMimeType(ctx, ei);
      if (StringUtils.startsWith(mimetype, "image") == true) {
        return SearchType.Image;
      }
      return SearchType.Attachment;
    }

    public String getElementType()
    {
      return elmentType;
    }

    public String getElmentJsonType()
    {
      if (SearchType.Wiki == this) {
        return "wiki";
      }
      return getElementType();
    }

    public boolean matches(GWikiContext ctx, GWikiElementInfo ei)
    {
      String mimetype = ctx.getWikiWeb().getDaoContext().getMimeTypeProvider().getMimeType(ctx, ei);
      boolean isImage = StringUtils.startsWith(mimetype, "image");
      switch (this) {
        case Wiki:
          return elmentType.equals(ei.getType());
        case Image:
          return isImage;
        case All:
          return true;
        case Attachment:
        default:
          return isImage == false;
      }
    }

    public boolean matches(SearchType other)
    {
      if (this == All || other == All) {
        return true;
      }
      return this == other;
    }
  }

  public static final String GWIKI_DEFAULT_EDITOR = "gwikidefeditor";
  private String txt;
  private String macro;
  private String macroHead;
  private String macroBody;
  /**
   * The page currently is edited.
   */
  private String editPageId;

  public Object onWeditAutocomplete()
  {
    // {, !, [
    String format = wikiContext.getRequestParameter("c");
    String querystring = wikiContext.getRequestParameter("q");
    JsonObject resp = null;
    if (StringUtils.length(format) != 1) {
      resp = JsonBuilder.map("ret", 10, "message", "No type given");
    } else {
      JsonArray array = JsonBuilder.array();
      switch (format.charAt(0)) {
        case '!':
          fillImageLinks(querystring, array);
          break;
        case '[':
          fillPageLinks(querystring, array);
          break;
        case '{':
          fillMacroLinks(querystring, array);
          break;

        case 'x':
          array.add(JsonBuilder.map("title", "Erster!", "url", "first"));
          array.add(JsonBuilder.map("title", "Zweiter!", "url", "second"));
          break;
        default:
          resp = JsonBuilder.map("ret", 11, "message", "Unknown type: " + format);
          break;
      }
      if (resp == null) {
        resp = JsonBuilder.map("ret", 0, "list", array);
      }
    }

    return sendResponse(resp);

  }

  public Object onPageIdAutocomplete()
  {
    String querystring = wikiContext.getRequestParameter("q");

    String type = wikiContext.getRequestParameter("pageType");
    SearchType searchType = SearchType.fromString(type);

    //    JsonObject resp = JsonBuilder.map("ret", 0);
    JsonArray list = new JsonArray();
    String queryexpr = SearchUtils.createLinkExpression(querystring, true, searchType.elmentType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    for (SearchResult sr : qr.getResults()) {
      String pageid = sr.getPageId();
      String titel = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
      list.add(JsonBuilder.map(
          "url", pageid,
          "key", pageid,
          "title", titel,
          "label",
          StringEscapeUtils.escapeHtml(titel) + "<br/><small>("
              + StringEscapeUtils.escapeHtml(pageid) + ")</small>"));
    }

    //    resp.add("list", list);
    return sendResponse(list);
  }

  public Object onGetMacroInfos()
  {
    JsonArray array = JsonBuilder.array();
    fillMacroLinks("", array);
    JsonObject resp = JsonBuilder.map("ret", 0, "list", array);
    return sendResponse(resp);
  }

  /**
   * Returns a MacroInfo (see js)
   * 
   * @return
   */
  public Object onGetMacroInfo()
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    GWikiMacroFactory fac = mfm.get(macro);
    if (fac == null) {
      JsonObject resp = JsonBuilder.map("ret", 10, "message", "Unknown macro: " + macro);
      return sendResponse(resp);
    }
    JsonObject macroInfo = JsonBuilder.map("macroName", macro, "macroHead", macro);
    JsonObject resp = JsonBuilder.map("ret", 0, "macroInfo", macroInfo);
    JsonObject info = fillMacroInfo(macro, fac);
    macroInfo.set("macroMetaInfo", info);
    if (StringUtils.isNotBlank(macroHead) == true) {
      macroInfo.set("macroHead", macroHead);
      MacroAttributes ma = new MacroAttributes();
      ma.parse(macroHead);
      macroInfo.set("macroName", ma.getCmd());
      JsonArray jparams = JsonBuilder.array();
      for (Map.Entry<String, String> me : ma.getArgs().getMap().entrySet()) {
        jparams.add(JsonBuilder.map("name", me.getKey(), "value", me.getValue()));
      }
      macroInfo.set("macroParams", jparams);
    }
    return sendResponse(resp);
  }

  private void fillMacroLinks(String querystring, JsonArray array)
  {
    Map<String, GWikiMacroFactory> mfm = wikiContext.getWikiWeb().getWikiConfig().getWikiMacros(wikiContext);
    List<String> macroNames = new ArrayList<>(mfm.keySet());
    Collections.sort(macroNames);
    for (String macroName : macroNames) {
      GWikiMacroFactory fac = mfm.get(macroName);
      if (fac.isRteMacro() == true) {
        continue;
      }
      GWikiMacro macroinst = fac.createInstance();
      MacroAttributes mat = new MacroAttributes(macroName);
      if (macroinst.isRestricted(mat, wikiContext) == true) {
        continue;
      }
      JsonObject map = JsonBuilder.map("url", macroName, "key", macroName, "title", macroName, "label", macroName, "c",
          macroName);
      map.set("onInsert", "gwedit_ac_insert_macro");
      fillMacroInfo(macroName, fac);
      map.set("macroMetaInfo", fillMacroInfo(macroName, fac));
      array.add(map);
    }

  }

  private JsonObject fillMacroInfo(String macroName, GWikiMacroFactory fac)
  {
    GWikiMacroInfo mInfo = fac.getMacroInfo();
    List<MacroParamInfo> params = mInfo.getParamInfos();
    JsonArray jp = JsonBuilder.array();
    for (MacroParamInfo pi : params) {
      JsonObject pmi = JsonBuilder.map("name", pi.getName(),
          "type", pi.getType().name(),
          "required", pi.isRequired(),
          "defaultValue", pi.getDefaultValue(),
          "info", pi.getInfo());
      if (pi.getEnumValues().isEmpty() == false) {
        if (pi.getEnumValues().size() == 1 && pi.getEnumValues().get(0).startsWith("${") == true) {
          String script = pi.getEnumValues().get(0);
          script = script.substring(2);
          script = script.substring(0, script.length() - 1);
          Map<String, Object> gctx = new HashMap<>();
          gctx.put("gwikiContext", getWikiContext());
          Object ret = ScriptUtils.executeScriptCode(script, gctx);
          pmi.add("enumValues", JsonBuilder.array((Collection) ret));
        } else {
          pmi.add("enumValues", JsonBuilder.array(pi.getEnumValues()));
        }
      }
      jp.add(pmi);

    }
    Pair<String, String> templ = mInfo.getRteTemplate(macroName);
    JsonObject ret = JsonBuilder.map("info", mInfo.getInfo(),
        "macroName", macroName,
        "hasBody", mInfo.hasBody(),
        "evalBody", mInfo.evalBody(),
        "rteMacro", mInfo.isRteMacro(),
        "renderFlags", mInfo.getRenderFlags(),
        "macroTemplateBegin", templ.getFirst(),
        "macroTemplateEnd", templ.getSecond(),
        "macroParams", jp);
    return ret;
  }

  private void fillPageLinks(String querystring, JsonArray array)
  {
    String pageType = "gwiki";
    fillPageLinks(pageType, querystring, array);
  }

  private void fillPageLinks(String pageType, String querystring, JsonArray array)
  {
    String queryexpr = SearchUtils.createLinkExpression(querystring, true, pageType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    for (SearchResult sr : qr.getResults()) {
      String pageid = sr.getPageId();
      String titel = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
      array.add(JsonBuilder.map(
          "url", pageid,
          "key", pageid,
          "title", titel,
          "label",
          StringEscapeUtils.escapeHtml(titel) + "<br/><small>("
              + StringEscapeUtils.escapeHtml(pageid) + ")</small>",
          "onInsert", "gwedit_ac_insert_acpagelink"));
    }

  }

  private void fillImageLinks(String querystring, JsonArray array)
  {
    String pageType = "";
    String queryexpr = SearchUtils.createLinkExpression(querystring, true, pageType);
    SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());

    query.setMaxCount(1000);
    QueryResult qr = filter(query);
    for (SearchResult sr : qr.getResults()) {
      String pageid = sr.getPageId();
      if (StringUtils.endsWithAny(pageid, new String[] { ".png", ".jpeg", ".PNG", ".JPEG", ".JPG", ".jpg" }) == false) {
        continue;
      }
      String title = wikiContext.getTranslatedProp(sr.getElementInfo().getTitle());
      array.add(JsonBuilder.map("key", sr.getPageId(), "url", sr.getPageId(), "label",
          title, "title", title, "onInsert", "gwedit_ac_insert_imagelink"));
    }
  }

  protected QueryResult filter(SearchQuery query)
  {
    query.setFindUnindexed(true);
    QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
    return qr;
  }

  @Deprecated
  public Object onWikiToWedit()
  {

    String text = WeditWikiUtils.wikiToWedit(txt);
    JsonObject resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  @Deprecated
  public Object onWeditToWiki()
  {

    String text = WeditWikiUtils.weditToWiki(txt);
    JsonObject resp = JsonBuilder.map("ret", 0, "text", text);
    String ret = resp.toString();
    return sendStringResponse(ret);
  }

  public Object onWikiToRte()
  {

    String rte = WeditWikiUtils.wikiToRte(wikiContext, editPageId, txt);
    return sendStringResponse(rte);
  }

  public Object onRteToWiki()
  {
    String wiki = WeditWikiUtils.rteToWiki(wikiContext, editPageId, txt);
    return sendStringResponse(wiki);
  }

  public Object onSetDefaultEditorType()
  {
    String editorType = wikiContext.getRequestParameter("editorType");
    editorType = StringUtils.defaultString(editorType, "wiki");
    wikiContext.getWikiWeb().getAuthorization().setUserProp(wikiContext, GWIKI_DEFAULT_EDITOR, editorType,
        UserPropStorage.Client);
    return noForward();
  }

  public Object onGetSmileys()
  {
    JsonArray ret = new JsonArray();
    GWikiSmileyConfig config = GWikiSmileyConfig.get(wikiContext);
    List<GWikiSmileyInfo> smlist = config.getSmileyList();
    for (GWikiSmileyInfo info : smlist) {
      JsonObject jm = new JsonObject();
      jm.add("shortName", info.getShortName());
      jm.add("shortCut", info.getShortCut());
      jm.add("source", info.getSource());
      ret.add(jm);
    }
    return sendResponse(ret);

  }

  public String getTxt()
  {
    return txt;
  }

  public void setTxt(String txt)
  {
    this.txt = txt;
  }

  public String getMacro()
  {
    return macro;
  }

  public void setMacro(String macro)
  {
    this.macro = macro;
  }

  public String getMacroHead()
  {
    return macroHead;
  }

  public void setMacroHead(String macroHead)
  {
    this.macroHead = macroHead;
  }

  public String getMacroBody()
  {
    return macroBody;
  }

  public void setMacroBody(String macroBody)
  {
    this.macroBody = macroBody;
  }

  public String getEditPageId()
  {
    return editPageId;
  }

  public void setEditPageId(String editPageId)
  {
    this.editPageId = editPageId;
  }

}
