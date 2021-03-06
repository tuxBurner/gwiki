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

package de.micromata.genome.gwiki.page.impl;

import java.io.ByteArrayInputStream;
import java.util.List;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiBinaryArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.attachments.TextExtractorUtils;
import de.micromata.genome.gwiki.page.search.GWikiIndexedArtefakt;
import de.micromata.genome.gwiki.utils.AppendableI;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;

/**
 * Containing a binary attachment.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiBinaryAttachmentArtefakt extends GWikiBinaryArtefaktBase<byte[]>
    implements GWikiEditableArtefakt, GWikiIndexedArtefakt
{

  private static final long serialVersionUID = 2413110416852468772L;

  public GWikiBinaryAttachmentArtefakt()
  {
  }

  @Override
  public void getPreview(GWikiContext ctx, AppendableI sb)
  {
    if (ctx.getCurrentElement() == null) {
      return;
    }
    String pid = ctx.getCurrentElement().getElementInfo().getId();
    if (pid.contains(".") == false) {
      return;
    }
    if (getStorageData() == null) {
      return;
    }
    try {
      String t = TextExtractorUtils.getTextExtract(ctx, pid, new ByteArrayInputStream(getStorageData()));
      sb.append(t);
    } catch (Exception ex) {
      GLog.note(GWikiLogCategory.Wiki, "Failure extracting text: " + ex.getMessage(), new LogExceptionAttribute(ex));
    }
  }

  @Override
  public GWikiEditorArtefakt<?> getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiAttachmentEditorArtefakt(elementToEdit, bean, partKey, this);
  }

  @Override
  public String buildFileName(String pageId, String partName)
  {
    return partName + pageId;
  }

  @Override
  public void collectArtefakts(List<GWikiArtefakt<?>> al)
  {
    al.add(this);

  }

  @Override
  public byte[] getCompiledObject()
  {
    return getStorageData();
  }

  @Override
  public String getFileSuffix()
  {
    return "";
  }

  @Override
  public void setCompiledObject(byte[] compiledObject)
  {
    // nix
  }

}
