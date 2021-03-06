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

package de.micromata.genome.gwiki.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader;
import de.micromata.genome.gwiki.model.config.GWikiDAOContext;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.logging.LoggingServiceManager;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * Servlet for wiki, static and dav access.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiServlet extends HttpServlet
{

  private static final long serialVersionUID = 5250118043302579360L;

  /**
   * Root dao context.
   */
  private GWikiDAOContext daoContext;

  public static GWikiServlet INSTANCE;

  public String contextPath;

  public String servletPath;
  long etagTimeOut;

  public GWikiServlet()
  {
    INSTANCE = this;
    etagTimeOut = LocalSettings.get().getLongValue("gwiki.etag.timeoutms", TimeInMillis.HOUR);
  }

  public GWikiWeb getWikiWeb()
  {
    return daoContext.getWikiSelector().getWikiWeb(this);
  }

  public GWikiWeb getRootWikiWeb()
  {
    return daoContext.getWikiSelector().getRootWikiWeb(this);
  }

  public boolean hasWikiWeb()
  {
    return daoContext.getWikiSelector().hasWikiWeb(this);
  }

  @Override
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
    servletPath = config.getInitParameter("servletPath");
    contextPath = config.getInitParameter("contextPath");
    if (contextPath == null) {
      contextPath = config.getServletContext().getContextPath();
    }
    if (daoContext == null) {
      String className = config
          .getInitParameter("de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className");
      if (StringUtils.isBlank(className) == true) {
        throw new ServletException(
            "de.micromata.genome.gwiki.model.config.GWikiBootstrapConfigLoader.className is not set in ServletConfig for GWikiServlet");
      }
      GWikiBootstrapConfigLoader loader = ClassUtils.createDefaultInstance(className, GWikiBootstrapConfigLoader.class);
      daoContext = loader.loadConfig(config);
    }
  }

  public void initWiki(HttpServletRequest req, HttpServletResponse resp)
  {
    daoContext.getWikiSelector().initWiki(this, req, resp);
  }

  protected String getWikiPage(GWikiContext ctx)
  {
    String servPath = ctx.getRealServletPath();
    String pathInfo = ctx.getRealPathInfo();
    String page = servPath;
    if (StringUtils.isNotEmpty(pathInfo) == true) {
      page = pathInfo;
    }
    if (page.startsWith("/") == true) {
      page = page.substring(1);
    }
    return page;
  }

  public static boolean isIgnorableAppServeIOException(Exception ex)
  {
    return ex.getClass().getName().contains("RuntimeIOException") == true
        || ex.getClass().getName().contains("EofException") == true;
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    if (GLog.isTraceEnabled() == true) {
      String lopi = "Req: ctpath: " + req.getContextPath() + "; spath: " + req.getServletPath() + "; pi: "
          + req.getPathInfo();
      GLog.note(GWikiLogCategory.Wiki, lopi);
    }
    initWiki(req, resp);
    long start = System.currentTimeMillis();
    GWikiWeb wiki = getWikiWeb();
    GWikiContext ctx = new GWikiContext(wiki, this, req, resp);
    try {
      GWikiContext.setCurrent(ctx);
      String page = getWikiPage(ctx);

      final String method = req.getMethod();
      if (StringUtils.equals(method, "GET") == false && StringUtils.equals(method, "POST") == false) {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Gwiki Method " + method + " not supported");
        return;
      }
      if (page.startsWith("static/") == true) {
        serveStatic(page, ctx);
        return;
      }
      wiki.serveWiki(page, ctx);
    } catch (RuntimeIOException ex) {
      GLog.info(GWikiLogCategory.Wiki, "IO Error serving: " + ex.getMessage(), new LogExceptionAttribute(ex));
    } catch (Exception ex) {
      if (isIgnorableAppServeIOException(ex) == true) {
        GLog.note(GWikiLogCategory.Wiki, "IO Error serving: " + ex.getMessage());
      } else {
        GLog.error(GWikiLogCategory.Wiki, "GWikiWeb serve error: " + ex.getMessage(), new LogExceptionAttribute(ex));
      }
    } finally {
      LoggingServiceManager.get().getStatsDAO().addPerformance(GWikiLogCategory.Wiki, "GWikiServlet.doPost",
          System.currentTimeMillis() - start, 0);
      GWikiContext.setCurrent(null);
      if (daoContext != null) {
        daoContext.getWikiSelector().deinitWiki(this, req, resp);
      }
    }
  }

  public InputStream getStaticResource(String res, GWikiContext wikiContext) throws ServletException, IOException
  {
    FileSystem fs = daoContext.getStaticContentFileSystem();
    if (fs != null) {
      if (fs.exists(res) == true) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        fs.readBinaryFile(res, os);
        return new ByteArrayInputStream(os.toByteArray());
      }
    }
    if (daoContext.isStaticContentFromClassPath() == true) {
      if (daoContext.getPluginRepository().getActivePluginClassLoader() != null) {
        return daoContext.getPluginRepository().getActivePluginClassLoader().getResourceAsStream(res);
      }
      return GWikiServlet.class.getResourceAsStream(res);
    } else {
      return getServletContext().getResourceAsStream(res);
    }
  }

  public boolean hasStaticResource(String res, GWikiContext wikiContexte)
  {
    FileSystem fs = daoContext.getStaticContentFileSystem();
    if (fs != null) {
      return fs.exists(res);
    }

    InputStream is;
    if (daoContext.isStaticContentFromClassPath() == true) {
      is = GWikiServlet.class.getResourceAsStream(res);
    } else {
      is = getServletContext().getResourceAsStream(res);
    }
    if (is == null) {
      return false;
    }
    try {
      is.close();
    } catch (IOException ex) {
      // ignore

    }
    return true;
  }

  protected void serveStatic(String page, GWikiContext wikiContext) throws ServletException, IOException
  {
    String res = "/" + page;
    HttpServletResponse resp = wikiContext.getResponse();
    InputStream is = getStaticResource(res, wikiContext);
    if (is == null) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    if (etagTimeOut > 1) {
      String etag = wikiContext.getWikiWeb().geteTagWiki();
      String ifnm = wikiContext.getRequest().getHeader("If-None-Match");
      if (StringUtils.equals(etag, ifnm) == true) {
        wikiContext.getResponse().sendError(304, "Not modified");
        return;
      }

      wikiContext.getResponse().addHeader("ETag", etag);
      long nt = new Date().getTime() + etagTimeOut;
    }
    long nt = new Date().getTime() + etagTimeOut;
    String mime = wikiContext.getWikiWeb().getDaoContext().getMimeTypeProvider().getMimeType(wikiContext, page);
    if (StringUtils.equals(mime, "application/x-shockwave-flash")) {
      resp.setHeader("Cache-Control", "cache, must-revalidate");
      resp.setHeader("Pragma", "public");
    }
    if (mime != null) {
      resp.setContentType(mime);
    }
    resp.setDateHeader("Expires", nt);
    resp.setHeader("Cache-Control", "max-age=86400, public");
    if (mime != null) {
      resp.setContentType(mime);
    }

    byte[] data = IOUtils.toByteArray(is);
    IOUtils.closeQuietly(is);
    resp.setContentLength(data.length);
    IOUtils.write(data, resp.getOutputStream());
  }

  public GWikiDAOContext getDAOContext()
  {
    return daoContext;
  }

  public void setDAOContext(GWikiDAOContext daoContext)
  {
    this.daoContext = daoContext;
  }

  public String getContextPath()
  {
    return contextPath;
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  public String getServletPath()
  {
    return servletPath;
  }

  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

}
