/*
 * Copyright 2013 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.tools.build;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LegacyInfo
{
	private boolean legacy = false;
	private String targetClassName = null;
	
	public boolean isLegacy()
    {
    	return legacy;
    }
	public void setLegacy(boolean legacy)
    {
    	this.legacy = legacy;
    }
	public String getTargetClassName()
    {
    	return targetClassName;
    }
	public void setTargetClassName(String targetClassName)
    {
    	this.targetClassName = targetClassName;
    }
}
