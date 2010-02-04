/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.XmlNode;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class GWikiPropsEditorArtefakt extends GWikiEditorArtefaktBase
{
  private GWikiPropsArtefakt props;

  private GWikiPropsDescriptor propDescriptor;

  public GWikiPropsEditorArtefakt(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName, GWikiPropsArtefakt props,
      GWikiPropsDescriptor propDescriptor)
  {
    super(elementToEdit, editBean, partName);
    this.props = props;
    this.propDescriptor = propDescriptor;
  }

  private void lookupPropDescriptor(GWikiContext ctx)
  {
    if (propDescriptor != null)
      return;
    String id = "admin/templates/" + partName + "PropDescriptor";
    GWikiElement el = ctx.getWikiWeb().findElement(id);
    if (el == null) {
      return;
    }
    if ((el instanceof GWikiConfigElement) == false) {
      return;
    }
    GWikiConfigElement cfel = (GWikiConfigElement) el;
    Object o = cfel.getConfig().getCompiledObject();
    if (o == null || (o instanceof GWikiPropsDescriptor) == false) {
      return;
    }
    propDescriptor = (GWikiPropsDescriptor) o;
  }

  private void initPropDescriptor(GWikiContext ctx)
  {
    lookupPropDescriptor(ctx);
    if (propDescriptor == null) {
      buildPropDescriptor(ctx);
    }

  }

  private void buildPropDescriptor(GWikiContext ctx)
  {
    propDescriptor = new GWikiPropsDescriptor();
    for (Map.Entry<String, String> me : props.getCompiledObject().getMap().entrySet()) {
      GWikiPropsDescriptorValue val = new GWikiPropsDescriptorValue();
      val.setKey(me.getValue());
      propDescriptor.getDescriptors().add(val);
    }
  }

  public void onParseRequest(PropsEditContext pct)
  {
    if (pct.invokeOnControlerBean("onParseRequest") == true) {
      return;
    }
    if (pct.isReadOnly() == true || pct.isDisplayable() == false) {
      return;
    }
    String value = pct.getRequestValue();

    if (StringUtils.isEmpty(value) == true) {
      value = pct.getDefaultValue();
    }
    if (StringUtils.equals(pct.getControlType(), "DATE") == true) {
      value = GWikiProps.formatTimeStamp(pct.getWikiContext().parseUserDateString(value));
    }
    pct.setPropsValue(value);
  }

  public void onValidate(PropsEditContext pct)
  {
    if (pct.invokeOnControlerBean("onValidate") == true) {
      return;
    }
    if (pct.isReadOnly() == true || pct.isDisplayable() == false) {
      return;
    }
    String reqValue = pct.getPropsValue();
    if (pct.getPropDescriptor().isRequiresValue() == true) {
      if (StringUtils.isBlank(reqValue) == true) {
        pct.addSimpleValidationError("Property Wert " + pct.getPropName() + " muss einen Wert haben");
      }
    }
    // pct.setValue(reqValue);
  }

  public void onSave(GWikiContext ctx)
  {
    initPropDescriptor(ctx);

    GWikiElement el = ctx.getWikiElement();
    String metaTemplateId = el.getMetaTemplate().getPageId();
    for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
      if (isForThisElement(d, metaTemplateId) == false) {
        continue;
      }
      PropsEditContext pct = createPropContext(ctx, d);
      onParseRequest(pct);
      onValidate(pct);
      //      
      // if (StringUtils.isNotBlank(d.getOnValidate()) == true) {
      // value = StringUtils.defaultString(value);
      // value = (String) ScriptUtils.invokeScriptFunktion(d.getOnValidate(), "onValidate", value, ctx, el, d);
      // }
      // if (d.isReadOnly() == true && d.getDefaultValue() == null && StringUtils.isBlank(d.getOnValidate()) == true) {
      // continue;
      // }
      // if (d.isRequiresValue() == true) {
      // if (StringUtils.isBlank(value) == true) {
      // ctx.addSimpleValidationError("Property Wert " + d.getKey() + " muss einen Wert haben");
      // }
      // }
      // setValue(d.getKey(), value);
    }
  }

  protected GWikiProps getCompiledProps()
  {
    if (props.getCompiledObject() == null) {
      props.setCompiledObject(new GWikiProps());
    }
    return props.getCompiledObject();
  }

  private void setValue(String key, String value)
  {

    getCompiledProps().setStringValue(key, value);
  }

  public boolean isForThisElement(GWikiPropsDescriptorValue d, String metaTemplateId)
  {
    String s = d.getRequiredMetaTemplateId();
    if (StringUtils.isBlank(s) == true) {
      return true;
    }
    List<String> reqs = Converter.parseStringTokens(s, ", ", false);
    for (String req : reqs) {
      if (StringUtils.equals(metaTemplateId, req) == true) {
        return true;
      }
    }
    return false;
  }

  protected XmlNode[] renderOptions(PropsEditContext pct)
  {
    Map<String, String> m = pct.getOptionValues();
    XmlElement el = Xml.element("select", "name", pct.getRequestKey()/* , "value", pct.getPropsValue() */);
    for (Map.Entry<String, String> me : m.entrySet()) {
      XmlElement option;
      if (StringUtils.equals(pct.getPropsValue(), me.getKey()) == true) {
        option = Xml.element("option", "value", me.getKey(), "selected", "selected");
      } else {
        option = Xml.element("option", "value", me.getKey());
      }
      option.nest(Xml.text(me.getValue()));
      el.nest(option);
    }
    return Xml.nodes(el);
  }

  protected PropsEditContext createPropContext(GWikiContext ctx, GWikiPropsDescriptorValue d)
  {
    PropsEditContext pc = new PropsEditContext(ctx, props, partName, d);
    if (props.getCompiledObject() == null) {
      props.setCompiledObject(new GWikiProps());
    }
    return pc;
  }

  public void onRenderInternal(PropsEditContext pct)
  {

    String value = pct.getPropsValue();
    // if (StringUtils.isEmpty(value) && StringUtils.isNotEmpty(pct.getPropDescriptor().getDefaultValue()) == true) {
    // value = evalDefaultValue(ctx, d, name);
    // }
    
    XmlNode[] controlNodes = new XmlNode[0];
    String type = pct.getControlType();
    if (StringUtils.equals(type, "BOOLEAN") == true) {
      List<String> attrs = Xml.asList("type", "checkbox", "name", pct.getRequestKey(), "value", "true");
      if (pct.isReadOnly() == true) {
        attrs = Xml.add(attrs, "disabled", "true");
      }
      if (StringUtils.equals(pct.getPropsValue(), "true") == true) {
        attrs = Xml.add(attrs, "checked", "true");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    } else if (StringUtils.equals(type, "OPTION") == true) {
      controlNodes = renderOptions(pct);
    } else if (StringUtils.equals(type, "DATE") == true) {
      String dates = pct.getPropsValue();
      Date date = GWikiProps.parseTimeStamp(dates);
      String fdate = pct.getWikiContext().getUserDateString(date);
      List<String> attrs = Xml.asList("type", "text", "size", "30", "name", pct.getRequestKey(), "value", fdate);
      if (pct.isReadOnly() == true) {
        Xml.add(attrs, "disabled", "true");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    } else {
      List<String> attrs = Xml.asList("type", "text", "size", "40", "name", pct.getRequestKey(), "value", value);
      if (pct.isReadOnly() == true) {
        Xml.add(attrs, "disabled", "true");
      }
      if (StringUtils.equals(type, "PAGEID") == true) {
        Xml.add(attrs, "class", "wikiPageEditText");
      }
      controlNodes = Xml.nodes(Html.input(Xml.listAsAttrs(attrs)));
    }
    for (XmlNode n : controlNodes) {
      pct.append(n.toString());
    }
  }

  public String onRender(PropsEditContext pct)
  {
    StringWriter sout = new StringWriter();
    pct.getWikiContext().getCreatePageContext().pushBody(sout);
    if (pct.invokeOnControlerBean("onRender") == false) {
      onRenderInternal(pct);
    }
    pct.getWikiContext().getCreatePageContext().popBody();
    return sout.getBuffer().toString();
  }

  public void renderViaDescriptor(GWikiContext ctx)
  {
    XmlElement table = Html.table(Xml.attrs("width", "100%", "class", "gwikiProperties"), //
        Html.tr( //
            Html.th(Xml.attrs("width", "70", "align", "left"), Xml.text("Key")), //
            Html.th(Xml.attrs("width", "300", "align", "left"), Xml.text("Value")), //
            Html.th(Xml.attrs("align", "left"), Xml.text("Description"))));
    GWikiEditPageActionBean bean = ((GWikiEditPageActionBean) ctx.getRequest().getAttribute("form"));
    String metaTemplateId = bean.getMetaTemplate().getPageId();
    // String metaTemplateId = el.getMetaTemplate().getPageId();

    for (GWikiPropsDescriptorValue d : propDescriptor.getDescriptors()) {
      if (isForThisElement(d, metaTemplateId) == false)
        continue;
      PropsEditContext pctx = createPropContext(ctx, d);
      if (pctx.isDisplayable() == false) {
        continue;
      }
      String nested = onRender(pctx);
      String label = d.getKey();
      if (d.getLabel() != null) {
        label = d.getLabel();
      }
      table.add( //
          Html.tr( //
              Html.td(Xml.text(label)), //
              Html.td(Xml.code(nested)), //
              Html.td(Xml.text(d.getDescription()))));
    }
    ctx.append(table.toString());

  }

  public boolean renderWithParts(GWikiContext ctx)
  {

    initPropDescriptor(ctx);

    renderViaDescriptor(ctx);
    return true;

  }
}