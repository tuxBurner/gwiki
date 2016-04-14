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

/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobEvent.java,v $
//
// Project   jchronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   02.01.2007
// Copyright Micromata 02.01.2007
//
// $Id: JobEvent.java,v 1.4 2007/02/25 13:38:59 roger Exp $
// $Revision: 1.4 $
// $Date: 2007/02/25 13:38:59 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import de.micromata.genome.gwiki.chronos.spi.jdbc.JobResultDO;
import de.micromata.genome.gwiki.chronos.spi.jdbc.TriggerJobDO;

public interface JobEvent
{

  public Scheduler getScheduler();

  public TriggerJobDO getJob();

  public JobDefinition getJobDefinition();

  public JobResultDO getJobResult();

  public State getJobStatus();
}
