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

package de.micromata.genome.gwiki.page.impl.wiki.macros.registry;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiMacroProviderRegistry
{
  public static Map<String, GWikiMacroFactory> getMacros()
  {
    Map<String, GWikiMacroFactory> ret = new TreeMap<String, GWikiMacroFactory>();
    ServiceLoader<GWikiMacroProviderService> sl = ServiceLoader.load(GWikiMacroProviderService.class);
    for (GWikiMacroProviderService service : sl) {
      ret.putAll(service.getMacros());
    }
    return ret;
  }
}
