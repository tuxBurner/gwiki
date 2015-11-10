////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import de.micromata.genome.gwiki.model.GWikiSchedulerJob;
import de.micromata.genome.gwiki.model.GWikiStandardSchedulerProvider;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Just call it blocking.
 * 
 * @author roger
 * 
 */
public class GWikiBlockingSchedulerProvider extends GWikiStandardSchedulerProvider
{
  public synchronized boolean execAsyncOne(GWikiContext wikiContext, final Class< ? extends GWikiSchedulerJob> callback,
      final Map<String, String> args)
  {
    prepareContext(wikiContext, args);
    ClassUtils.createDefaultInstance(callback).call(args);
    return true;
  }

  public synchronized AbstractExecutorService getExecutor()
  {
    return new AbstractExecutorService() {
      @Override
      public void execute(Runnable command)
      {
        command.run();
      }

      @Override
      public void shutdown()
      {
      }

      @Override
      public List<Runnable> shutdownNow()
      {
        return new ArrayList<Runnable>();
      }

      @Override
      public boolean isShutdown()
      {
        return false;
      }

      @Override
      public boolean isTerminated()
      {
        return false;
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
      {
        return false;
      }

    };
  }
}