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

package de.micromata.genome.gwiki.model.config;

import javax.servlet.ServletConfig;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.env.StandardEnvironment;

/**
 * Loads a spring application context file from standard file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GwikiFileContextBootstrapConfigLoader extends GWikiAbstractSpringContextBootstrapConfigLoader
{

  @Override
  protected ConfigurableApplicationContext createApplicationContext(ServletConfig config, String fileName)
  {
    FileSystemXmlApplicationContext ret = new FileSystemXmlApplicationContext(new String[] { fileName }, false, null);
    ret.setEnvironment(new StandardEnvironment()
    {
    });
    return ret;
  }
}
