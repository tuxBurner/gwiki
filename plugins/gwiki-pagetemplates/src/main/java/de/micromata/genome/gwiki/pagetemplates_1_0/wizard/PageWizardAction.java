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

package de.micromata.genome.gwiki.pagetemplates_1_0.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.impl.GWikiActionBeanArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBean;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanUtils;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.CallableX1;

/**
 * Wizard for creating articles
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class PageWizardAction extends ActionBeanBase
{
  private List<String> wizardSteps;

  public void renderHeaders()
  {
    for (String stepPageId : getVisibleWizardSteps()) {
      runInActionContext(stepPageId, null, new Callable<RuntimeException, Void>() {
        public Void call(final ActionBean bean) throws RuntimeException
        {
          ActionBeanUtils.dispatchToMethodImpl(bean, "onRenderHeader", wikiContext);
          return null;
        }});
    }
  }

  public Object onSave()
  {
    performValidation();
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }

    // if validation ok create new element
    final GWikiElement newPage = GWikiWebUtils.createNewElement(wikiContext, "", "admin/templates/StandardWikiPageMetaTemplate", "");

    // call step save handlers
    for (String wizardStep : getVisibleWizardSteps()) {
      runInActionContext(wizardStep, newPage, new Callable<RuntimeException, Void>() {
        public Void call(ActionBean bean) throws RuntimeException
        {
          ActionBeanUtils.dispatchToMethodImpl(bean, "onSave", wikiContext);
          return null;
        }
      });
    }

    // saves new page
    wikiContext.getWikiWeb().saveElement(wikiContext, newPage, false);

    // close box and load new page
    StringBuffer sb = new StringBuffer("<script type='text/javascript'>");
    sb.append("parent.$.fancybox.close();");
    sb.append("window.parent.location.href='/").append(newPage.getElementInfo().getId()).append("';");
    sb.append("</script>");
    wikiContext.append(sb.toString());
    wikiContext.flush();
    return noForward();
  }

  /**
   * calls validate methods of each wizard step
   */
  private void performValidation()
  {
    for (String wizardStep : getWizardSteps()) {
      runInActionContext(wizardStep, null, new Callable<RuntimeException, Void>() {
        public Void call(ActionBean bean) throws RuntimeException
        {
          ActionBeanUtils.dispatchToMethodImpl(bean, "onValidate", wikiContext);
          return null;
        }
      });
    }
  }

  /**
   * Runs the callbck code in scope of the action that is addresses by actionPageId
   * 
   * @param actionPageId The pageId of the action
   * @param element An optional element which is populated to the actionbean
   * @param callback The callback code
   * @return
   */
  public Void runInActionContext(final String actionPageId, final GWikiElement element, final Callable<RuntimeException, Void> callback)
  {
    final GWikiElement actionPage = wikiContext.getWikiWeb().getElement(actionPageId);
    return wikiContext.runElement(actionPage, new CallableX<Void, RuntimeException>() {
      public Void call() throws RuntimeException
      {
        GWikiArtefakt< ? > controller = actionPage.getPart("Controler");
        if (controller instanceof GWikiActionBeanArtefakt == false) {
          return null;
        }
        GWikiActionBeanArtefakt actionBeanArtefakt = (GWikiActionBeanArtefakt) controller;
        ActionBean bean = actionBeanArtefakt.getActionBean(wikiContext);
        bean.setWikiContext(wikiContext);

        // populate element to step action beans
        Map<String, Object> elementParam = new HashMap<String, Object>();
        elementParam.put("element", element);
        ClassUtils.populateBeanWithPuplicMembers(bean, elementParam);

        // populate form properies to step action bean
        ActionBeanUtils.fillForm((ActionBean) bean, wikiContext);
        return callback.call(bean);
      }
    });
  }

  /**
   * @return the wizardSteps
   */
  public List<String> getWizardSteps()
  {
    if (this.wizardSteps == null) {
      GWikiElement wizard = wikiContext.getWikiWeb().findElement("/edit/pagetemplates/PageWizard");
      if (wizard == null) {
        return Collections.emptyList();
      }
      List<String> wizardSteps = wizard.getElementInfo().getProps().getStringList("WIZARDSTEPS");
      if (wizardSteps == null) {
        return Collections.emptyList();
      }
      this.wizardSteps = wizardSteps;
    }
    return this.wizardSteps;
  }

  /**
   * Returns steps that are visible in the wizard
   */
  public List<String> getVisibleWizardSteps()
  {
    final List<String> visibleSteps = new ArrayList<String>();

    // call step visible handlers
    for (final String wizardStep : getWizardSteps()) {
      runInActionContext(wizardStep, null, new Callable<RuntimeException, Void>() {
        public Void call(ActionBean bean) throws RuntimeException
        {
          boolean visible = (Boolean) ActionBeanUtils.dispatchToMethodImpl(bean, "onIsVisible", wikiContext);
          if (visible == true) {
            visibleSteps.add(wizardStep);
          }
          return null;
        }
      });
    }
    return visibleSteps;
  }

  interface Callable<E extends RuntimeException, R>
  {
    R call(ActionBean bean) throws E;
  }
}