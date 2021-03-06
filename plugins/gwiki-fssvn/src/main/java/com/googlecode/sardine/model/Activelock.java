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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-10/27/2009 06:09 PM(mockbuild)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.23 at 06:27:19 PM PST 
//

package com.googlecode.sardine.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{DAV:}lockscope"/&gt;
 *         &lt;element ref="{DAV:}locktype"/&gt;
 *         &lt;element ref="{DAV:}depth"/&gt;
 *         &lt;element ref="{DAV:}owner" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}timeout" minOccurs="0"/&gt;
 *         &lt;element ref="{DAV:}locktoken" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "lockscope",
    "locktype",
    "depth",
    "owner",
    "timeout",
    "locktoken"
})
@XmlRootElement(name = "activelock")
public class Activelock
{

  @XmlElement(required = true)
  protected Lockscope lockscope;
  @XmlElement(required = true)
  protected Locktype locktype;
  @XmlElement(required = true)
  protected String depth;
  protected Owner owner;
  protected String timeout;
  protected Locktoken locktoken;

  /**
   * Gets the value of the lockscope property.
   * 
   * @return possible object is {@link Lockscope }
   * 
   */
  public Lockscope getLockscope()
  {
    return lockscope;
  }

  /**
   * Sets the value of the lockscope property.
   * 
   * @param value allowed object is {@link Lockscope }
   * 
   */
  public void setLockscope(Lockscope value)
  {
    this.lockscope = value;
  }

  /**
   * Gets the value of the locktype property.
   * 
   * @return possible object is {@link Locktype }
   * 
   */
  public Locktype getLocktype()
  {
    return locktype;
  }

  /**
   * Sets the value of the locktype property.
   * 
   * @param value allowed object is {@link Locktype }
   * 
   */
  public void setLocktype(Locktype value)
  {
    this.locktype = value;
  }

  /**
   * Gets the value of the depth property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDepth()
  {
    return depth;
  }

  /**
   * Sets the value of the depth property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setDepth(String value)
  {
    this.depth = value;
  }

  /**
   * Gets the value of the owner property.
   * 
   * @return possible object is {@link Owner }
   * 
   */
  public Owner getOwner()
  {
    return owner;
  }

  /**
   * Sets the value of the owner property.
   * 
   * @param value allowed object is {@link Owner }
   * 
   */
  public void setOwner(Owner value)
  {
    this.owner = value;
  }

  /**
   * Gets the value of the timeout property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getTimeout()
  {
    return timeout;
  }

  /**
   * Sets the value of the timeout property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setTimeout(String value)
  {
    this.timeout = value;
  }

  /**
   * Gets the value of the locktoken property.
   * 
   * @return possible object is {@link Locktoken }
   * 
   */
  public Locktoken getLocktoken()
  {
    return locktoken;
  }

  /**
   * Sets the value of the locktoken property.
   * 
   * @param value allowed object is {@link Locktoken }
   * 
   */
  public void setLocktoken(Locktoken value)
  {
    this.locktoken = value;
  }

}
