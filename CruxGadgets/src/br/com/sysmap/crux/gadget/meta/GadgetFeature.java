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

import com.google.gwt.gadgets.client.AdsFeature;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.GoogleAnalyticsFeature;
import com.google.gwt.gadgets.client.SetPrefsFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;
import com.google.gwt.gadgets.client.ViewFeature;
import com.google.gwt.gadgets.client.osapi.OsapiFeature;
import com.google.gwt.gadgets.client.rpc.RpcFeature;

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
		ads("ads", AdsFeature.class), 
		dynamicHeight("dynamic-height", DynamicHeightFeature.class),
		googleAnalytics("com.google.gadgets.analytics", GoogleAnalyticsFeature.class),
		lockedDomain("locked-domain", null),
		osapi("osapi", OsapiFeature.class),
		rpc("rpc", RpcFeature.class),
		setPrefs("setprefs", SetPrefsFeature.class),
		setTitle("settitle", SetTitleFeature.class),
		views("views", ViewFeature.class);
		
		String featureName;
		Class<?> featureClass;
		ContainerFeature(String featureName, Class<?> featureClass)
		{
			this.featureName = featureName;
			this.featureClass = featureClass;
		}
		
		public String getFeatureName()
		{
			return featureName;
		}

		public Class<?> getFeatureClass()
		{
			return featureClass;
		}
	}
}
