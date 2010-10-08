/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.gadget.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface GadgetFeature
{

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface NeedsFeatures
	{
		Feature[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Feature
	{
		ContainerFeature value();
	}
	
	public enum ContainerFeature{
		ads("ads"), 
		dynamicHeight("dynamic-height"),
		googleAnalytics("com.google.gadgets.analytics"),
		lockedDomain("locked-domain"),
		osapi("osapi"),
		setPrefs("setprefs"),
		setTitle("settitle"),
		views("views");
		
		String value;
		ContainerFeature(String value)
		{
			this.value = value;
		}
		
		public String toString()
		{
			return value;
		}
	}
}
