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

package de.micromata.genome.gwiki.pagelifecycle_1_0.jobs;

import static de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.GWikiSchedulerJobBase;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.model.logging.GWikiLogAttributeType;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.model.mpt.GWikiMultipleWikiSelector;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.BranchFileStats;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.BranchState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcConstants;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.PlcUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Job that iterates offline branches and releases them if release date is reached
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class GWikiPlcReleaseJob extends GWikiSchedulerJobBase
{
  private static final long serialVersionUID = 7873208690449697070L;

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiSchedulerJobBase#call()
   */
  @Override
  public void call()
  {
    GWikiMultipleWikiSelector wikiSelector = getWikiSelector();
    List<String> branches = wikiSelector.getMptIdSelector().getTenants(GWikiWeb.getRootWiki());

    if (branches == null || branches.isEmpty() == true) {
      return;
    }

    // check all branches for release
    for (final String branch : branches) {
      wikiContext.runInTenantContext(branch, wikiSelector, new CallableX<Void, RuntimeException>()
      {
        @Override
        public Void call() throws RuntimeException
        {
          GWikiProps branchInfo = PlcUtils.getBranchInfo(wikiContext);
          String branchState = branchInfo.getStringValue(PlcConstants.BRANCH_INFO_BRANCH_STATE);

          // ignore online branches
          if (BranchState.ONLINE.name().equals(branchState) == true) {
            return null;
          }

          String releaseDateString = branchInfo.getStringValue(PlcConstants.BRANCH_INFO_RELEASE_DATE);
          // no release date given -> do not release
          if (StringUtils.isBlank(releaseDateString) == true) {
            return null;
          }
          Date releaseDate = GWikiProps.parseTimeStamp(releaseDateString);
          if (releaseDate == null || releaseDate.after(new Date())) {
            return null;
          }

          // retrieve content of branch
          Collection<GWikiElement> branchContents = getBranchContents(branch);
          if (branchContents == null || branchContents.isEmpty() == true) {
            return null;
          }

          // only release if all files are approved
          if (checkFileApprovals(branchContents) == false) {
            GLog.note(GWikiLogCategory.Wiki, "Release date expired but not all files in content released approved",
                new LogAttribute(GWikiLogAttributeType.BranchId, branch));
            return null;
          }

          return releaseBranch(branch, branchContents);
        }
      });
    }
  }

  /**
   * Collect and filter contents in branch
   * 
   * @param branch
   * @return
   */
  private Collection<GWikiElement> getBranchContents(final String branch)
  {
    final Matcher<String> blackListMatcher = new BooleanListRulesFactory<String>().createMatcher("*intern/*,*admin/*");
    final List<GWikiElement> branchContent = wikiContext.getElementFinder().getPages(new MatcherBase<GWikiElementInfo>()
    {
      private static final long serialVersionUID = -6020166500681050082L;

      @Override
      public boolean match(GWikiElementInfo ei)
      {
        String tid = ei.getProps().getStringValue(GWikiPropKeys.TENANT_ID);
        return StringUtils.equals(branch, tid);
      }
    });

    Collection<GWikiElement> filteredBranchContent = CollectionUtils.select(branchContent, new Predicate<GWikiElement>()
    {

      @Override
      public boolean evaluate(GWikiElement object)
      {
        return !blackListMatcher.match(object.getElementInfo().getId());
      }
    });

    return filteredBranchContent;
  }

  /**
   * Checks if each file in branch is really approved by content admin
   * 
   * @param filteredBranchContent the content
   * @return
   */
  private boolean checkFileApprovals(Collection<GWikiElement> filteredBranchContent)
  {
    BranchFileStats fileStats = PlcUtils.getBranchFileStats(wikiContext);
    if (fileStats == null) {
      return false;
    }

    // check file states
    for (GWikiElement pageToRelease : filteredBranchContent) {
      FileStatsDO stats = fileStats.getFileStatsForId(pageToRelease.getElementInfo().getId());
      if (stats == null || FileState.APPROVED_CONTENT_ADMIN.equals(stats.getFileState()) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Releases all files of a branch and marks branch as offline
   * 
   * @param branch id of branch
   * @param filteredBranchContent the content to release
   */
  private Void releaseBranch(final String branch, final Collection<GWikiElement> filteredBranchContent)
  {
    GLog.note(GWikiLogCategory.Wiki, "Release branch " + branch);

    // copy pages in online space
    wikiContext.runInTenantContext("", getWikiSelector(), new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        for (GWikiElement pageToRelease : filteredBranchContent) {
          wikiContext.getWikiWeb().saveElement(wikiContext, pageToRelease, true);
        }
        return null;
      }
    });

    // remove pages from branch
    for (GWikiElement pageToRelease : filteredBranchContent) {
      wikiContext.getWikiWeb().removeWikiPage(wikiContext, pageToRelease);
    }

    // add branch state offlien to branch meta data
    updateBranchInfo();

    GLog.note(GWikiLogCategory.Wiki, "Released pages", new LogAttribute(GWikiLogAttributeType.PageIds,
        StringUtils.join(
            filteredBranchContent.stream().map(e -> e.getElementInfo().getId()).collect(Collectors.toList()), ",")));
    return null;
  }

  /**
   * Sets the <code>BRANCH_STATE</code> attribute to <code>ONLINE</code>
   */
  private void updateBranchInfo()
  {
    final GWikiElement branchInfoElement = wikiContext.getWikiWeb().findElement(BRANCH_INFO_LOCATION);
    if (branchInfoElement == null) {
      return;
    }
    GWikiArtefakt<?> artefakt = branchInfoElement.getMainPart();
    if (artefakt instanceof GWikiPropsArtefakt == false) {
      return;
    }
    GWikiPropsArtefakt propsArtefakt = (GWikiPropsArtefakt) artefakt;
    final GWikiProps props = propsArtefakt.getCompiledObject();
    props.setStringValue(PlcConstants.BRANCH_INFO_BRANCH_STATE, BranchState.ONLINE.name());
    wikiContext.getWikiWeb().getAuthorization().runAsSu(wikiContext, new CallableX<Void, RuntimeException>()
    {
      @Override
      public Void call() throws RuntimeException
      {
        wikiContext.getWikiWeb().saveElement(wikiContext, branchInfoElement, true);
        return null;
      }
    });
  }

  private GWikiMultipleWikiSelector getWikiSelector()
  {
    GWikiWikiSelector wikiSelector = wikiContext.getWikiWeb().getDaoContext().getWikiSelector();
    if (wikiSelector == null) {
      return null;
    }

    if (wikiSelector instanceof GWikiMultipleWikiSelector == true) {
      GWikiMultipleWikiSelector multipleSelector = (GWikiMultipleWikiSelector) wikiSelector;
      return multipleSelector;
    }
    return null;
  }
}
