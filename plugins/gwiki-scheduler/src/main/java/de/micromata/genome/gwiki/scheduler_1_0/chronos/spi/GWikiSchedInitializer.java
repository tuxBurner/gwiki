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

package de.micromata.genome.gwiki.scheduler_1_0.chronos.spi;

import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.plugin.GWikiAbstractPluginLifecycleListener;
import de.micromata.genome.gwiki.plugin.GWikiPlugin;
import de.micromata.genome.logging.GLog;

/**
 * Life cycle.
 * 
 * TODO provide ServiceLoader class to initialize
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiSchedInitializer extends GWikiAbstractPluginLifecycleListener
{
  @Override
  public void webLoaded(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {
    GLog.info(GWikiLogCategory.Wiki, "GWikiSchedInitializer:webLoaded");
    // TODO genome/gwiki
    //    SchedulerManager schedm = SchedulerManager.get();
    //    SchedulerFactory ssf = new SchedulerFactory();
    //    ssf.setSchedulerName("standard");
    //    schedm.getScheduleFactories().add(ssf);
    //    schedm.startup();
  }

  @Override
  public void deactivate(GWikiWeb wikiWeb, GWikiPlugin plugin)
  {

  }

}
