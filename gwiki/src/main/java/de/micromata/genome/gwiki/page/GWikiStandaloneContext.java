/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   07.12.2009
// Copyright Micromata 07.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.gspt.BodyContentImpl;
import de.micromata.genome.gwiki.page.gspt.StandAlonePageContext;
import de.micromata.genome.gwiki.web.StandaloneHttpServletRequest;
import de.micromata.genome.gwiki.web.StandaloneHttpServletResponse;

public class GWikiStandaloneContext extends GWikiContext
{
  private StandAlonePageContext standAlonepageContext;

  private Map<String, Object> requestAttributes = new HashMap<String, Object>();

  private BodyContentImpl jspWriter = new BodyContentImpl();

  private StandaloneHttpServletRequest standaloneRequest;

  private StandaloneHttpServletResponse standaloneResponse;

  private String skin;

  /**
   * used only for test cases.
   * 
   * @param contextPath
   * @param servletPath
   */
  protected GWikiStandaloneContext(String contextPath, String servletPath)
  {
    super(null, null, new StandaloneHttpServletRequest(contextPath, servletPath), new StandaloneHttpServletResponse());
    standAlonepageContext = new StandAlonePageContext(jspWriter, null, getRequest(), getResponse());
    setPageContext(standAlonepageContext);
    standaloneRequest = (StandaloneHttpServletRequest) getRequest();
    standaloneResponse = (StandaloneHttpServletResponse) getResponse();
  }

  /**
   * used only for test cases.
   * 
   */
  public GWikiStandaloneContext()
  {
    this("/", "/");
  }

  public GWikiStandaloneContext(GWikiWeb wikiWeb, HttpServlet servlet, String contextPath, String servletPath)
  {
    super(wikiWeb, servlet, new StandaloneHttpServletRequest(contextPath, servletPath), new StandaloneHttpServletResponse());
    ServletContext servletContext = null;
    if (servlet != null) {
      servletContext = servlet.getServletContext();
    }
    standAlonepageContext = new StandAlonePageContext(jspWriter, servletContext, getRequest(), getResponse());
    setPageContext(standAlonepageContext);
    standaloneRequest = (StandaloneHttpServletRequest) getRequest();
    standaloneResponse = (StandaloneHttpServletResponse) getResponse();
  }

  public GWikiStandaloneContext(GWikiContext wikiContext)
  {
    this(wikiContext.getWikiWeb(), wikiContext.getServlet(), wikiContext.getRequest().getContextPath(), wikiContext.getRequest()
        .getServletPath());
    
  }

  public HttpSession getSession(boolean create)
  {
    return getRequest().getSession(create);
  }

  public Object getRequestAttribute(String key)
  {
    return requestAttributes.get(key);
  }

  public void setRequestAttribute(String key, Object value)
  {
    requestAttributes.put(key, value);
  }

  public void setCharacterEncoding(String enc) throws UnsupportedEncodingException
  {

  }

  public StandAlonePageContext getStandAlonepageContext()
  {
    return standAlonepageContext;
  }

  public void setStandAlonepageContext(StandAlonePageContext standAlonepageContext)
  {
    this.standAlonepageContext = standAlonepageContext;
  }

  public Map<String, Object> getRequestAttributes()
  {
    return requestAttributes;
  }

  public void setRequestAttributes(Map<String, Object> requestAttributes)
  {
    this.requestAttributes = requestAttributes;
  }

  public String getOutString()
  {
    if (pageContext != null) {
      return ((BodyContentImpl) pageContext.getOut()).getString();
    }
    return jspWriter.getString();
  }

  public BodyContentImpl getJspWriter()
  {
    return jspWriter;
  }

  public void setJspWriter(BodyContentImpl jspWriter)
  {
    this.jspWriter = jspWriter;
  }

  public StandaloneHttpServletRequest getStandaloneRequest()
  {
    return standaloneRequest;
  }

  public void setStandaloneRequest(StandaloneHttpServletRequest standaloneRequest)
  {
    this.standaloneRequest = standaloneRequest;
  }

  public StandaloneHttpServletResponse getStandaloneResponse()
  {
    return standaloneResponse;
  }

  public void setStandaloneResponse(StandaloneHttpServletResponse standaloneResponse)
  {
    this.standaloneResponse = standaloneResponse;
  }

  public String getSkin()
  {
    if (StringUtils.isEmpty(skin) == false) {
      return skin;
    }
    return super.getSkin();
  }

  public void setSkin(String skin)
  {
    this.skin = skin;
  }
}