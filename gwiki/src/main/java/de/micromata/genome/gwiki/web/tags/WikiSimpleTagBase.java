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

package de.micromata.genome.gwiki.web.tags;

import javax.servlet.jsp.tagext.TagSupport;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Base implementation for GWiki Tags.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class WikiSimpleTagBase extends TagSupport
{

  private static final long serialVersionUID = -7658522982913202856L;

  protected GWikiContext getWikiContext()
  {
    return (GWikiContext) pageContext.getAttribute("wikiContext");
  }
}
