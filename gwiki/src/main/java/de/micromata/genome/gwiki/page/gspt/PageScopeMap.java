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

package de.micromata.genome.gwiki.page.gspt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.PageContext;
import static javax.servlet.jsp.PageContext.PAGE_SCOPE;

/**
 * Internal implementation for jsp/GSPT-Parsing.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class PageScopeMap implements Map<String, Object> {
  
  private final PageContext parentPageContext;
  
  public PageScopeMap(final PageContext parentPageContext) {
    this.parentPageContext = parentPageContext;
  }

  public void clear()
  {
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
     parentPageContext.removeAttribute(enu.nextElement().toString());
    }
  }

  public boolean containsKey(Object key)
  {
    return parentPageContext.getAttribute(key.toString()) != null;
  }

  public boolean containsValue(Object value)
  {
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
     if (value.equals(parentPageContext.getAttribute(enu.nextElement().toString())) == true) {
       return true;
     }
    }
    return false;
  }

  public Set<Map.Entry<String, Object>> entrySet()
  {
    Set<Map.Entry<String, Object>> rVal = new HashSet<Entry<String,Object>>();
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
      final String key = enu.nextElement().toString();
      rVal.add(new Map.Entry<String, Object>() {

      public String getKey()
      {
        return key;
      }

      public Object getValue()
      {
        return parentPageContext.getAttribute(key);
      }

      public Object setValue(Object newValue)
      {
        Object oldValue = parentPageContext.getAttribute(key);
        parentPageContext.setAttribute(key, newValue);
        return oldValue;
      }
        
     });
    }
    return rVal;
  }

  public Object get(Object key)
  {
    return parentPageContext.getAttribute(key.toString());
  }

  public boolean isEmpty()
  {
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    return (enu.hasMoreElements() == false);
  }

  public Set<String> keySet()
  {
    Set<String> rVal = new HashSet<String>();
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
     rVal.add(enu.nextElement().toString()); 
    }
    return rVal;
  }

  public Object put(String key, Object value)
  {
    Object old = parentPageContext.getAttribute(key);
    parentPageContext.setAttribute(key, value);
    return old;
  }

  public void putAll(Map< ? extends String, ? extends Object> t)
  {
    for (Map.Entry< ? extends String, ? extends Object> entry : t.entrySet()) {
      parentPageContext.setAttribute(entry.getKey(), entry.getValue());
    }
  }

  public Object remove(Object key)
  {
    Object old = parentPageContext.getAttribute(key.toString());
    parentPageContext.removeAttribute(key.toString());
    return old;
  }

  public int size()
  {
    int i = 0;
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
      ++i;
      enu.nextElement();
    }
    return i;
  }

  public Collection<Object> values()
  {
    List<Object> rVal = new ArrayList<Object>();
    Enumeration<?> enu = parentPageContext.getAttributeNamesInScope(PAGE_SCOPE);
    while (enu.hasMoreElements() == true) {
     rVal.add(parentPageContext.getAttribute(enu.nextElement().toString())); 
    }
    return rVal;
  }
  
}
