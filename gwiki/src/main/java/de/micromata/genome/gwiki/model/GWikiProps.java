/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.10.2009
// Copyright Micromata 19.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributesUtils;
import de.micromata.genome.gwiki.utils.CommaListParser;

/**
 * Wrapper to a property (string/string) map.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiProps implements Serializable
{

  private static final long serialVersionUID = 6530388862209381595L;

  public static ThreadLocal<SimpleDateFormat> internalTimestamp = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {
      return new SimpleDateFormat("yyyyMMddHHmmssSSS");
    }

  };

  private Map<String, String> map;

  public GWikiProps()
  {
    this.map = new HashMap<String, String>();
  }

  public GWikiProps(GWikiProps other)
  {
    this();
    map.putAll(other.getMap());
  }

  public GWikiProps(Map<String, String> map)
  {
    this.map = map;
  }

  @SuppressWarnings("unchecked")
  public GWikiProps(Properties map)
  {
    this.map = (Map<String, String>) (Map) map;
  }

  public String toString()
  {
    return map.toString();
  }

  public int size()
  {
    return map.size();
  }

  public boolean isEmpty()
  {
    return map.isEmpty();
  }

  public boolean containsKey(String key)
  {
    return map.containsKey(key);
  }

  public static String formatTimeStamp(Date date)
  {
    if (date == null)
      return null;
    return internalTimestamp.get().format(date);
  }

  public static Date parseTimeStamp(String d)
  {
    if (StringUtils.isBlank(d) == true) {
      return null;
    }
    try {
      return internalTimestamp.get().parse(d);
    } catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String getStringValue(String key)
  {
    return map.get(key);
  }

  public String getStringValue(String key, String defaultValue)
  {
    String ret = map.get(key);
    if (ret != null) {
      return ret;
    }
    return defaultValue;
  }

  public void setStringValue(String key, String value)
  {
    map.put(key, value);
  }

  public List<String> getStringList(String key)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true)
      return Collections.emptyList();
    return CommaListParser.parseCommaList(map.get(key));
  }

  public GWikiProps getStringValueMap(String key)
  {
    String argstr = getStringValue(key);
    if (StringUtils.isEmpty(argstr) == true) {
      return new GWikiProps();
    }
    return new GWikiProps(MacroAttributesUtils.decode(argstr));
  }

  public void setDateValue(String key, Date date)
  {
    if (date == null)
      map.remove(key);
    else
      map.put(key, internalTimestamp.get().format(date));
  }

  public Date getDateValue(String key)
  {
    String sv = map.get(key);
    if (StringUtils.isBlank(sv) == true) {
      return null;
    }
    try {
      return internalTimestamp.get().parse(sv);
    } catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean getBooleanValue(String key)
  {
    return getBooleanValue(key, false);
  }

  public boolean getBooleanValue(String key, boolean defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    return StringUtils.equals(val, "true");
  }

  public void setBooleanValue(String key, boolean value)
  {
    String val = value ? "true" : "false";
    map.put(key, val);
  }

  public int getIntValue(String key, int defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(val);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public void setIntValue(String key, int value)
  {
    map.put(key, Integer.toString(value));
  }

  public long getLongValue(String key, long defaultValue)
  {
    String val = map.get(key);
    if (StringUtils.isEmpty(val) == true) {
      return defaultValue;
    }
    try {
      return Long.parseLong(val);
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public void setLongValue(String key, long value)
  {
    map.put(key, Long.toString(value));
  }

  // etc.

  public Map<String, String> getMap()
  {
    return map;
  }

  public void setMap(Map<String, String> map)
  {
    this.map = map;
  }
}