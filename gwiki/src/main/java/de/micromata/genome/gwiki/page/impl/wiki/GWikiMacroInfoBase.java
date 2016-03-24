package de.micromata.genome.gwiki.page.impl.wiki;

import de.micromata.genome.util.types.Pair;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class GWikiMacroInfoBase implements GWikiMacroInfo
{
  static int htmlIdCounter = 0;

  @Override
  public Pair<String, String> getRteTemplate(String macroHead)
  {
    StringBuilder begin = new StringBuilder();
    StringBuilder end = new StringBuilder();
    int renderFlags = getRenderFlags();
    String eln = "div";
    if (GWikiMacroRenderFlags.RteSpan.isSet(renderFlags) == true) {
      eln = "span";
    }
    String macroName = macroHead;
    int idx = macroHead.indexOf(':');
    if (idx != -1) {
      macroName = macroHead.substring(0, idx);
    }
    ++htmlIdCounter;

    begin.append("<").append(eln).append(" class='mceNonEditable weditmacroframe' contenteditable='false'>")
        .append("<").append(eln).append(" class='mceNonEditable weditmacrohead' data-macrohead='").append(macroHead)
        .append("' data-macroname='").append(macroName).append("'  contenteditable='false'>");
    //    begin.append(
    //        "<div class='wedigopbar'><span class='weditopbutton weditopedit' >Edit</span> <span class='weditopbutton weditdel'>Delete</span></div>");
    begin.append("<span class='weditmacrn'>").append(macroHead).append("</span>");
    begin.append("</").append(eln).append(">");
    if (hasBody() == false) {
      return Pair.make(begin.toString(), "</" + eln + ">");
    }
    begin.append("<").append(eln).append(" tabindex='-1' class='mceEditable weditmacrobody");
    if (evalBody() == false) {
      begin.append(" editmacrobd_pre");
    }
    begin.append("' contenteditable='true'>");
    if (evalBody() == false) {
      begin.append("<pre style='-moz-tab-size: 2; -o-tab-size: 2; tab-size: 2;'>\n");
      end.append("</pre>");
    }
    end.append("</").append(eln).append("></").append(eln).append(">");
    return Pair.make(begin.toString(), end.toString());
  }
}