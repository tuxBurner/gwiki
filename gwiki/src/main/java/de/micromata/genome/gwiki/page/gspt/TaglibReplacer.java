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

package de.micromata.genome.gwiki.page.gspt;

import java.util.Map;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class TaglibReplacer extends ReplacerBase
{
  GsptPreprocessor processor;

  public TaglibReplacer(GsptPreprocessor processor)
  {
    this.processor = processor;
  }

  public String getEnd()
  {
    return "@>";
  }

  public String getStart()
  {
    return "<@tag";
  }

  public String replace(ReplacerContext ctx, Map<String, String> attr, boolean isClosed)
  {
    String cname = attr.get("class");
    String tname = attr.get("name");
    processor.addTagLib(tname, cname);
    return "";
  }

}
