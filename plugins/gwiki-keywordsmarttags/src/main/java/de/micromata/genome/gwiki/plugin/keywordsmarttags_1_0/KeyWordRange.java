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

package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

public class KeyWordRange
{
  public int start;

  public int end;

  public List<GWikiElementInfo> links = new ArrayList<GWikiElementInfo>();

  public KeyWordRange()
  {

  }

  public KeyWordRange(int start, int end, List<GWikiElementInfo> links)
  {
    this.start = start;
    this.end = end;
    this.links = links;
  }
}