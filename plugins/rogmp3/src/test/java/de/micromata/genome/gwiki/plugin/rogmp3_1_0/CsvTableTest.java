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

package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class CsvTableTest
{
  @Test
  public void testLoad()
  {
    String fname = "D:\\ourMP3gwiki\\acccexp\\Titel.csv";
    if (new File(fname).exists() == false) {
      return;
    }
    CsvTable table = new CsvTable();
    table.load(new File(fname));
    table.createIndex(1);
    List<String[]> found = table.findEquals(1, "Tubin, Eduard");
    System.out.println("found: " + found);
  }
}
