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

package de.micromata.genome.gdbfs;

import java.io.InputStream;

/**
 * Load RAM File system from Zip loaded from Classpath.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class CpZipRamFileSystem extends ZipRamFileSystem
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -6217635628070031205L;

  /**
   * Instantiates a new cp zip ram file system.
   *
   * @param zipFile the zip file
   */
  public CpZipRamFileSystem(String zipFile)
  {
    this(zipFile, "embeddedcp(" + zipFile + ")s");
  }

  /**
   * Instantiates a new cp zip ram file system.
   *
   * @param zipFile the zip file
   * @param fsName the fs name
   */
  public CpZipRamFileSystem(String zipFile, String fsName)
  {
    this(zipFile, fsName, false);
  }

  /**
   * Instantiates a new cp zip ram file system.
   *
   * @param zipFile the zip file
   * @param fsName the fs name
   * @param readOnly the read only
   */
  public CpZipRamFileSystem(String zipFile, String fsName, boolean readOnly)
  {
    super(fsName);
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(zipFile);
    if (is == null) {
      throw new FsException("Cannot open ZipFile: " + zipFile);
    }
    load(is);
    setReadOnly(readOnly);
  }
}
