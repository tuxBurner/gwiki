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
 * If file lock is not possible due timeout.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsFileLockException extends FsException
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -5030674480778775399L;

  /**
   * Instantiates a new fs file lock exception.
   */
  public FsFileLockException()
  {
  }

  /**
   * Instantiates a new fs file lock exception.
   *
   * @param cause the cause
   */
  public FsFileLockException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Instantiates a new fs file lock exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public FsFileLockException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Instantiates a new fs file lock exception.
   *
   * @param message the message
   */
  public FsFileLockException(String message)
  {
    super(message);
  }

}
