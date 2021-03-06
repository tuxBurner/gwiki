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

/**
 * Combines a primary read/write and a secondary read only file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ReadWriteCombinedFileSystem extends CombinedFileSystem
{

  /**
   * Instantiates a new read write combined file system.
   */
  public ReadWriteCombinedFileSystem()
  {
    super();
  }

  /**
   * Instantiates a new read write combined file system.
   *
   * @param primary the primary
   * @param secondary the secondary
   */
  public ReadWriteCombinedFileSystem(FileSystem primary, FileSystem secondary)
  {
    super(primary, secondary);
  }

  @Override
  public String getFileSystemName()
  {
    return "readwrite(" + super.getFileSystemName() + ")";
  }

  @Override
  public FileSystem getFsForRead(String name)
  {
    if (primary.exists(name) == true) {
      return primary;
    }
    if (secondary.exists(name) == true) {
      return secondary;
    }
    return primary;
  }

  @Override
  public FileSystem getFsForWrite(String name)
  {
    return primary;
  }

}
