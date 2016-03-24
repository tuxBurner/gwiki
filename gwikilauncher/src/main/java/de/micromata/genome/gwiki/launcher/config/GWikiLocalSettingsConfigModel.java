package de.micromata.genome.gwiki.launcher.config;

import org.apache.commons.codec.Charsets;

import de.micromata.genome.util.runtime.config.AbstractCompositLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.AbstractTextConfigFileConfigModel;
import de.micromata.genome.util.runtime.config.HibernateSchemaConfigModel;
import de.micromata.genome.util.runtime.config.JdbcLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsWriter;
import de.micromata.genome.util.runtime.config.MailSessionLocalSettingsConfigModel;
import de.micromata.mgc.application.webserver.config.JettyConfigModel;
import de.micromata.mgc.javafx.launcher.gui.generic.LauncherLocalSettingsConfigModel;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLocalSettingsConfigModel extends AbstractCompositLocalSettingsConfigModel
{
  @SuppressWarnings("unused")
  private LauncherLocalSettingsConfigModel launcherConfig = new LauncherLocalSettingsConfigModel();

  @SuppressWarnings("unused")
  private JettyConfigModel jettyConfig = new JettyConfigModel();

  @SuppressWarnings("unused")
  private GWikiSystemUserConfigModel systemUser = new GWikiSystemUserConfigModel();

  @SuppressWarnings("unused")
  private GWikiFilesystemConfigModel fileSystem = new GWikiFilesystemConfigModel();

  private MailSessionLocalSettingsConfigModel emailConfig = new MailSessionLocalSettingsConfigModel("gwikimailsession");

  @SuppressWarnings("unused")
  private HibernateSchemaConfigModel hibernateSchemaConfig = new HibernateSchemaConfigModel();
  @SuppressWarnings("unused")
  private AbstractTextConfigFileConfigModel log4jConfig = new AbstractTextConfigFileConfigModel("Log4J",
      "log4j.properties", Charsets.ISO_8859_1);

  public GWikiLocalSettingsConfigModel()
  {
    emailConfig.setDefaultEmailSender("gwiki@locahost");
    emailConfig.setJndiName("java:/comp/env/gwiki/mail/mailSession");
  }

  @Override
  public <T extends LocalSettingsConfigModel> T castToForConfigDialog(Class<T> other)
  {
    if (other == JdbcLocalSettingsConfigModel.class) {
      return null;
    }
    return super.castToForConfigDialog(other);
  }

  @Override
  public LocalSettingsWriter toProperties(LocalSettingsWriter writer)
  {
    writer.put("gwiki.enable.webdav", "false");
    return super.toProperties(writer);
  }

}