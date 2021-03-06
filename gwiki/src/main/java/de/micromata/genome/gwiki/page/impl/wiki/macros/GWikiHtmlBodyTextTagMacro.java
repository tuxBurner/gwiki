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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * Html body tag, where body will not interpreted, but outputed as text.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyTextTagMacro extends GWikiHtmlTagMacro implements GWikiBodyMacro
{

  private static final long serialVersionUID = 1251867687172353344L;

  public GWikiHtmlBodyTextTagMacro()
  {

  }

  public GWikiHtmlBodyTextTagMacro(int renderModes)
  {
    setRenderModes(renderModes);
  }

  public Html2WikiTransformInfo getTransformInfo()
  {
    // no transform needed
    return null;
  }

  @Override
  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    super.ensureRight(attrs, ctx);
    ctx.ensureAllowTo(GWikiAuthorizationRights.GWIKI_EDITHTML.name());
  }
}
