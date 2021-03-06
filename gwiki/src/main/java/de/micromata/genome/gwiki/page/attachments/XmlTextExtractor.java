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

package de.micromata.genome.gwiki.page.attachments;

import java.io.InputStream;

import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

import de.micromata.genome.gwiki.utils.html.Html2TextFilter;

/**
 * Extracts text from a xml file.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class XmlTextExtractor implements TextExtractor
{

  public String extractText(String fileName, InputStream data)
  {
    Html2TextFilter filter = new Html2TextFilter();
    XMLParserConfiguration parser = new HTMLConfiguration();
    parser.setProperty("http://cyberneko.org/html/properties/filters", new XMLDocumentFilter[] { filter});
    XMLInputSource source = new XMLInputSource(null, null, null, data, "UTF-8");
    try {
      parser.parse(source);
      return filter.getResultText().toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
