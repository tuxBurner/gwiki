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

package com.googlecode.sardine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;

/**
 * 
 * Unfortunately, this test suite can only be run at work since that is where my dav server is. At some point in the future, I'll consider
 * bundling a java based server with sardine and testing against that. For now, this is easier.
 * 
 * @author jonstevens
 */
public class SardineTest
{
  @Test
  public void dummy()
  {

  }

  // @Test
  public void testGetSingleFile() throws Exception
  {
    Sardine sardine = SardineFactory.begin();
    List<DavResource> resources = sardine
        .getResources("http://webdav.prod.365.kink.y/cybernetentertainment/imagedb/7761/v/h/320/7761_1.flv");
    assertEquals(1, resources.size());
    DavResource res = resources.get(0);
    assertEquals("7761_1.flv", res.getName());
    assertEquals("7761_1.flv", res.getNameDecoded());
    assertEquals("http://webdav.prod.365.kink.y/cybernetentertainment/imagedb/7761/v/h/320/", res.getBaseUrl());
    assertEquals("http://webdav.prod.365.kink.y/cybernetentertainment/imagedb/7761/v/h/320/7761_1.flv", res.getAbsoluteUrl());
    assertFalse(res.isDirectory());
    // server currently doesn't know what a .flv is. bug in apache in that a HEAD returns the default mimetype (text/plain) and PROPFIND
    // doesn't?
    // assertEquals(res.getContentType(), "video/x-flv");
  }

  // @Test
  public void testGetDirectoryListing() throws Exception
  {
    Sardine sardine = SardineFactory.begin();
    List<DavResource> resources = sardine.getResources("http://webdav.prod.365.kink.y/cybernetentertainment/imagedb/7761/v/h/320/");

    // for (DavResource res : resources)
    // {
    // System.out.println(res);
    // }
    assertEquals(31, resources.size());
  }

  // @Test
  public void testPutFile() throws Exception
  {
    Sardine sardine = SardineFactory.begin("admin", "admin");
    sardine.put("http://localhost/uploads/file2.txt", new FileInputStream(new File("/tmp/mysql.log.jon")));
  }
}
