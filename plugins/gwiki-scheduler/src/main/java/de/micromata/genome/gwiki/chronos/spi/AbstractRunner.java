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
// Project Genome Core
//
// Author    roger@micromata.de
// Created   18.08.2008
// Copyright Micromata 18.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;

/**
 * 
 * @author roger@micromata.de
 * 
 */
@Deprecated
public class AbstractRunner
{
  private static String HOSTNAME;
  static {
    try {
      HOSTNAME = InetAddress.getLocalHost().getHostName();
    } catch (final UnknownHostException ex) {
      HOSTNAME = "UnkownHost: " + new VMID().toString();
    }
  }

  public static String getThisHostName()
  {
    return HOSTNAME;
  }
}
