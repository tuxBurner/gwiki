/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   29.10.2009
// Copyright Micromata 29.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributesUtils;

public class GWikiFragmentImage extends GWikiFragementBase
{

  private static final long serialVersionUID = 4699829156802232146L;

  /**
   * requestAttribute.
   * 
   * Contains a set of included image pageIds
   */
  public static final String WIKIGENINCLUDEDIMAGES = "WIKIGENINCLUDEDIMAGES";

  public static final String WIKI_MAX_IMAGE_WIDTH = "WIKI_MAX_IMAGE_WIDTH";

  private String target;

  private String width;

  private String height;

  private String border;

  private String alt;

  private String hspace;

  private String vspace;

  public GWikiFragmentImage(String target)
  {
    this.target = target;
    if (target != null) {
      int idx = target.indexOf('|');
      if (idx != -1) {
        String rest = target.substring(idx + 1);
        this.target = target.substring(0, idx);
        Map<String, String> m = MacroAttributesUtils.decode(rest);
        try {
          BeanUtilsBean.getInstance().populate(this, m);
        } catch (Exception ex) {
          throw new RuntimeException("Invalid image arguments: " + ex.getMessage(), ex);
        }
      }
    }

  }

  public List<GWikiFragment> getChilds()
  {
    return Collections.emptyList();
  }

  public void getSource(StringBuilder sb)
  {
    sb.append("!").append(target);
    if (StringUtils.isNotEmpty(width) == true) {
      sb.append("|").append("width=").append(width);
    }
    if (StringUtils.isNotEmpty(height) == true) {
      sb.append("|").append("height=").append(height);
    }
    if (StringUtils.isNotEmpty(border) == true) {
      sb.append("|").append("border=").append(border);
    }
    if (StringUtils.isNotEmpty(alt) == true) {
      sb.append("|").append("alt=").append(alt);
    }
    if (StringUtils.isNotEmpty(hspace) == true) {
      sb.append("|").append("hspace=").append(hspace);
    }
    if (StringUtils.isNotEmpty(vspace) == true) {
      sb.append("|").append("vspace=").append(vspace);
    }

    sb.append("!");
  }

  @SuppressWarnings("unchecked")
  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.NoImages.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    String lwidth = width;
    String ltarget = target;
    if (RenderModes.LocalImageLinks.isSet(ctx.getRenderMode()) == true) {

      String o = ObjectUtils.toString(ctx.getRequestAttribute(WIKI_MAX_IMAGE_WIDTH));
      if (o != null && lwidth == null) {
        lwidth = o;
        // ctx.append("<img src='", target, "' width='" + o + "'>");
        // } else {
        // ctx.append("<img src='", target, "'>");
        // }

        // return true;
      }
    }
    Set<String> set = (Set<String>) ctx.getRequestAttribute(WIKIGENINCLUDEDIMAGES);
    if (set != null) {
      set.add(ltarget);
    }

    if (GWikiFragementLink.isGlobalUrl(target) == true) {
      // ctx.append("<img src='", target, "'>");
    } else {
      if (RenderModes.GlobalImageLinks.isSet(ctx.getRenderMode()) == true) {
        ltarget = ctx.globalUrl(target);
      } else {
        ltarget = ctx.localUrl(target);
      }
    }
    ctx.append("<img src=\"", ltarget, "\"");
    if (StringUtils.isNotEmpty(width) == true) {
      ctx.append(" width=\"", width, "\"");
    }
    if (StringUtils.isNotEmpty(height) == true) {
      ctx.append(" height=\"", height, "\"");
    }
    if (StringUtils.isNotEmpty(border) == true) {
      ctx.append(" border=\"", border, "\"");
    }
    if (StringUtils.isNotEmpty(alt) == true) {
      ctx.append(" alt=\"", alt, "\"");
    }
    if (StringUtils.isNotEmpty(hspace) == true) {
      ctx.append(" hspace=\"", hspace, "\"");
    }
    if (StringUtils.isNotEmpty(width) == true) {
      ctx.append(" hspace=\"", hspace, "\"");
    }
    return true;
  }

  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    // empty
  }

  public String getTarget()
  {
    return target;
  }

  public void setTarget(String target)
  {
    this.target = target;
  }

  public String getWidth()
  {
    return width;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  public String getHeight()
  {
    return height;
  }

  public void setHeight(String height)
  {
    this.height = height;
  }

  public String getBorder()
  {
    return border;
  }

  public void setBorder(String border)
  {
    this.border = border;
  }

  public String getAlt()
  {
    return alt;
  }

  public void setAlt(String alt)
  {
    this.alt = alt;
  }

  public String getHspace()
  {
    return hspace;
  }

  public void setHspace(String hspace)
  {
    this.hspace = hspace;
  }

  public String getVspace()
  {
    return vspace;
  }

  public void setVspace(String vspace)
  {
    this.vspace = vspace;
  }
}