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

package de.micromata.genome.gwiki.page.impl.wiki2;

public class GWikiWikiParserNlTest extends GWikiWikiParserTestBase
{
  public void testhr()
  {
    w2htest("x\n----\nb", "<p>x</p>\n<hr/>\n<p>b</p>\n");
  }

  public void testBr()
  {
    w2htest("a.\nb", "<p>a.<br/>\nb</p>\n");
  }

  public void testExpliciteNl()
  {
    w2htest("a\\\\\nb", "<p>a<br/>\nb</p>\n");
  }

  public void testNonExpliciteNl()
  {
    w2htest("a\nb", "<p>a<br/>\nb</p>\n");
  }

}
