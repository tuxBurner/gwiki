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

package de.micromata.genome.gwiki.page.impl.wiki;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class GWikiCompileTimeMacroBase extends GWikiMacroBase implements GWikiCompileTimeMacro
{
  public GWikiCompileTimeMacroBase(GWikiMacroInfo macroInfo)
  {
    super(macroInfo);
  }

  public GWikiCompileTimeMacroBase()
  {
  }

  private static final long serialVersionUID = -8053026294841163346L;

  @Override
  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    if (this instanceof GWikiBodyEvalMacro && attrs.getChildFragment() != null) {
      attrs.getChildFragment().render(ctx);
    } else if (StringUtils.isNotEmpty(attrs.getBody()) == true) {
      ctx.append(attrs.getBody());
    }
    return true;
  }

}
