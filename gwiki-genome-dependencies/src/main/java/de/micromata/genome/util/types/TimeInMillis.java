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
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   04.04.2008
// Copyright Micromata 04.04.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.types;

/**
 * Just a few constants for Time in milli seconds
 * 
 * @author roger@micromata.de
 * 
 */
public interface TimeInMillis
{
  public static final long SECOND = 1000;

  public static final long MINUTE = SECOND * 60;

  public static final long HOUR = MINUTE * 60;

  public static final long DAY = HOUR * 24;

  public static final long WEEK = DAY * 7;

  public static final long MAX_MONTH = DAY * 31;

  public static final long YEAR = DAY * 265;
}
