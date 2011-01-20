/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   19.02.2007
// Copyright Micromata 19.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author roger
 * 
 */
public class TriggerJobDisplayDO extends TriggerJobDO
{

  private static final long serialVersionUID = -2766329112135471673L;

  private List<ResultDisplayDO> results = new ArrayList<ResultDisplayDO>();

  public TriggerJobDisplayDO()
  {

  }

  public TriggerJobDisplayDO(TriggerJobDO other)
  {
    super(other);
  }

  @Override
  public void setCurrentResultPk(Long resultPk)
  {
    super.setCurrentResultPk(resultPk);
    if (resultPk == null)
      return;
    if (getResult() == null)
      setResult(new JobResultDO());
    getResult().setPk(resultPk);
  }

  public String getCurrentJobResultString()
  {
    if (getResult() == null)
      return "";
    return getResult().getResultString();
  }

  public List<ResultDisplayDO> getResults()
  {
    return results;
  }

  public void setResults(List<ResultDisplayDO> results)
  {
    this.results = results;
  }

  @Override
  public String getJobDefinitionString()
  {
    return StringEscapeUtils.escapeHtml(super.getJobDefinitionString());
  }

  public String getJobDefinitionStringShort()
  {
    String s = super.getJobDefinitionString();

    if (s == null || s.length() == 0)
      return "";
    String st = "<classToStart>";
    int idx = s.indexOf("<classToStart>");
    if (idx != -1) {
      s = s.substring(idx + st.length());
      idx = s.indexOf("<");
      if (idx != -1)
        s = s.substring(0, idx);
      return s;
    }
    // GWAFactoryBean
    st = "<className>";
    idx = s.indexOf("<className>");
    if (idx != -1) {
      s = s.substring(idx + st.length());
      idx = s.indexOf("<");
      if (idx != -1)
        s = s.substring(0, idx);
      return s;
    }

    // if (s.startsWith("<") == true) {
    // s = StringUtils.substring(s, 1, 50);
    // } else {
    s = StringUtils.abbreviate(s, 50);
    // }
    idx = s.indexOf('\n');
    if (idx != -1)
      s = s.substring(0, idx);
    return s;
  }
}