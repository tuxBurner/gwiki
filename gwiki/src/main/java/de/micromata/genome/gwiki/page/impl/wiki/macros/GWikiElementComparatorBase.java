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

import java.util.Comparator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * Base implementation for comparing GWikiElementInfo.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiElementComparatorBase implements Comparator<GWikiElementInfo>
{
  protected Comparator<GWikiElementInfo> parentComparator;

  public GWikiElementComparatorBase()
  {

  }

  public GWikiElementComparatorBase(Comparator<GWikiElementInfo> parentComparator)
  {
    this.parentComparator = parentComparator;
  }

  public int compareParent(GWikiElementInfo o1, GWikiElementInfo o2)
  {
    if (parentComparator == null) {
      return 0;
    }
    return parentComparator.compare(o1, o2);
  }

  public Comparator<GWikiElementInfo> getParentComparator()
  {
    return parentComparator;
  }

  public void setParentComparator(Comparator<GWikiElementInfo> parentComparator)
  {
    this.parentComparator = parentComparator;
  }

}
