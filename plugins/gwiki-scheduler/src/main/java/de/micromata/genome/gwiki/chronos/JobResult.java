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

/////////////////////////////////////////////////////////////////////////////
//
// $RCSfile: JobResult.java,v $
//
// Project   chronos
//
// Author    Wolfgang Jung (w.jung@micromata.de)
// Created   19.12.2006
// Copyright Micromata 19.12.2006
//
// $Id: JobResult.java,v 1.3 2007/02/16 14:31:34 hx Exp $
// $Revision: 1.3 $
// $Date: 2007/02/16 14:31:34 $
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos;

import java.util.Date;

/**
 * Das Ergebnis eines Joblaufs
 * 
 * @author Wolfgang Jung (w.jung@micromata.de)
 * 
 */
@Deprecated
public interface JobResult
{
  /**
   * Das Ergebniss, falls bereits vorhanden.
   * 
   * @return
   * @throws SchedulerException
   */
  public Object getResult() throws SchedulerException;

  /**
   * Wurde der Job beendet?
   * 
   * @return true, falls der Job fertig ist
   */
  public boolean isCompleted();

  /**
   * Gab es einen Fehler bei der Ausführung des Jobs
   * 
   * @return true, falls es einen Fehler gab.
   */
  public boolean isFailed();

  /**
   * entfernt das Ergebnis aus der Liste.
   * 
   */
  public void remove();

  /**
   * Wann wurde der Job gestartet
   * 
   * @return Zeitpunkt des Starts
   */
  public Date getExecutionStart();

  /**
   * wieviele Versuche wurden bereits unternommen.
   * 
   * @return Anzahl der erfolglosen Versuche
   */
  public int getRetryCount();

  /**
   * Laufzeit des Jobs bis zum Ergebnis oder eines Fehlers
   * 
   * @return Millisekunden der Laufzeit
   */
  public long getDuration();
}
