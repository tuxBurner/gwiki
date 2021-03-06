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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.micromata.genome.util.collections.OrderedProperties;

/**
 * Map holding I18N keys.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18nMap extends HashMap<String, String> implements Serializable
{
  private static final long serialVersionUID = -5348824646939727080L;
  List<String> sortedKeys = null;

  public GWikiI18nMap()
  {
  }

  public GWikiI18nMap(OrderedProperties sortedProps)
  {
    putAll(sortedProps);
    sortedKeys = new ArrayList<>();
    sortedKeys.addAll(sortedProps.keySet());
  }

  public List<String> getSortedKeys()
  {
    return sortedKeys;
  }

  public void setSortedKeys(List<String> sortedKeys)
  {
    this.sortedKeys = sortedKeys;
  }

}
