/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ArrayUtils;

public class CommonMultipartRequest extends HttpServletRequestWrapper
{
  private Map<String, String[]> multipartParams;

  private Map<String, FileItem> fileItems;

  public CommonMultipartRequest(HttpServletRequest request)
  {
    super(request);
    multipartParams = new HashMap<String, String[]>();
    fileItems = new HashMap<String, FileItem>();

  }

  public CommonMultipartRequest(HttpServletRequest request, Map<String, String[]> multipartParams, Map<String, FileItem> fileItems)
  {
    super(request);
    this.multipartParams = multipartParams;
    this.fileItems = fileItems;
  }

  public void addFileItem(FileItem item)
  {
    fileItems.put(item.getFieldName(), item);
  }

  public void addFormField(FileItem item)
  {
    final String fn = item.getFieldName();
    final String fv;
    try {
      fv = item.getString("UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    if (multipartParams.containsKey(fn) == false) {
      multipartParams.put(fn, new String[] { fv});
    } else {
      String[] ci = multipartParams.get(fn);
      multipartParams.put(fn, (String[]) ArrayUtils.add(ci, fv));
    }
  }

  public Enumeration getParameterNames()
  {
    Set paramNames = new HashSet();
    Enumeration paramEnum = getRequest().getParameterNames();
    while (paramEnum.hasMoreElements()) {
      paramNames.add(paramEnum.nextElement());
    }
    paramNames.addAll(this.multipartParams.keySet());
    return Collections.enumeration(paramNames);
  }

  public String getParameter(String name)
  {
    String[] values = (String[]) this.multipartParams.get(name);
    if (values != null) {
      return (values.length > 0 ? values[0] : null);
    }
    return getRequest().getParameter(name);
  }

  public String[] getParameterValues(String name)
  {
    String[] values = (String[]) this.multipartParams.get(name);
    if (values != null) {
      return values;
    }
    return getRequest().getParameterValues(name);
  }

  public Map getParameterMap()
  {
    Map paramMap = new HashMap();
    paramMap.putAll(getRequest().getParameterMap());
    paramMap.putAll(this.multipartParams);
    return paramMap;
  }

  public Map<String, String[]> getMultipartParams()
  {
    return multipartParams;
  }

  public Map<String, FileItem> getFileItems()
  {
    return fileItems;
  }
}