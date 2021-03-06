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

package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * 
 * Provides navigations menus for GWiki
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiMenuProvider
{
  /**
   * Menu for AdminMenu.
   * 
   * @return null if no adminMenu should be rendered
   */
  GWikiMenu getAdminMenu(GWikiContext wikiContext);

  /**
   * Menu for a user.
   * 
   * @param wikiContext
   * @return null if no menu
   */
  GWikiMenu getUserMenu(GWikiContext wikiContext);

  /**
   * Menu with template items.
   * 
   * @param wikiContext
   * @return
   */
  GWikiMenu getNewItemsMenu(GWikiContext wikiContext);

  /**
   * Get Menu for site, page hierarchie, keywords.
   * 
   * @param wikiContext
   * @return
   */
  GWikiMenu getHeadSiteMenu(GWikiContext wikiContext);
}
