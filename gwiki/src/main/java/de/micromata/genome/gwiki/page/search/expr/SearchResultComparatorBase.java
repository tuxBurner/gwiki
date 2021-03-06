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

package de.micromata.genome.gwiki.page.search.expr;

import de.micromata.genome.gwiki.page.search.SearchResult;

public abstract class SearchResultComparatorBase implements SearchResultComparator
{
  protected SearchResultComparator nextComparator;

  protected boolean desc = false;

  public SearchResultComparatorBase()
  {

  }

  public SearchResultComparatorBase(SearchResultComparator nextComparator, boolean desc)
  {
    this.nextComparator = nextComparator;
    this.desc = desc;
  }

  public abstract int compareThis(SearchResult o1, SearchResult o2);

  public int compare(SearchResult o1, SearchResult o2)
  {
    int res = compareThis(o1, o2);
    if (res != 0) {
      if (desc == true) {
        return res < 0 ? 1 : -1;
      }
      return res;
    }
    if (nextComparator != null) {
      return nextComparator.compare(o1, o2);
    }
    return 0;
  }

  public SearchResultComparator getNextComparator()
  {
    return nextComparator;
  }

  public void setNextComparator(SearchResultComparator nextComparator)
  {
    this.nextComparator = nextComparator;
  }

  public boolean isDesc()
  {
    return desc;
  }

  public void setDesc(boolean desc)
  {
    this.desc = desc;
  }
}
