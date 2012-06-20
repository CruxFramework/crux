/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gadget.client.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.gadget.client.features.AdsFeature;
import org.cruxframework.crux.gadget.client.features.DynamicHeightFeature;
import org.cruxframework.crux.gadget.client.features.GoogleAnalyticsFeature;
import org.cruxframework.crux.gadget.client.features.MiniMessageFeature;
import org.cruxframework.crux.gadget.client.features.PubsubFeature;
import org.cruxframework.crux.gadget.client.features.RpcFeature;
import org.cruxframework.crux.gadget.client.features.SetPrefsFeature;
import org.cruxframework.crux.gadget.client.features.SetTitleFeature;
import org.cruxframework.crux.gadget.client.features.TabsFeature;
import org.cruxframework.crux.gadget.client.features.ViewFeature;
import org.cruxframework.crux.gadget.client.features.osapi.OsapiFeature;


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
	public @interface WantsFeatures
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
		minimessage("minimessage", MiniMessageFeature.class),
		osapi("osapi", OsapiFeature.class),
		pubsub("pubsub", PubsubFeature.class),
		rpc("rpc", RpcFeature.class),
		setPrefs("setprefs", SetPrefsFeature.class),
		setTitle("settitle", SetTitleFeature.class),
		tabs("tabs", TabsFeature.class),
		views("views", ViewFeature.class),
		opensocial08("opensocial-0.8", null),
		opensocial09("opensocial-0.9", null),
		opensocial10("opensocial-1.0", null),
		opensocial11("opensocial-1.1", null),
		opensocialData("opensocial-data", null),
		opensocialTemplates("opensocial-templates", null);
		
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
	
	@NeedsFeatures({
		@Feature(ContainerFeature.ads)
	})
	public interface NeedsAdsFeature
	{
		/**
		 * Returns the AdsFeature. 
		 * @return AdsFeature
		 */
		AdsFeature getAdsFeature();		
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.dynamicHeight)
	})
	public interface NeedsDynamicHeightFeature
	{
		/**
		 * Returns the DynamicHeightFeature. 
		 * @return DynamicHeightFeature
		 */
		DynamicHeightFeature getDynamicHeightFeature();	
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.googleAnalytics)
	})
	public interface NeedsGoogleAnalyticsFeature
	{
		/**
		 * Returns the GoogleAnalyticsFeature. 
		 * @return GoogleAnalyticsFeature
		 */
		GoogleAnalyticsFeature getGoogleAnalyticsFeature();
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.lockedDomain)
	})
	public interface NeedsLockedDomain
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.minimessage)
	})
	public interface NeedsMiniMessageFeature
	{
		/**
		 * Returns the MiniMessageFeature. 
		 * @return MiniMessageFeature
		 */
		MiniMessageFeature getMiniMessageFeature();
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.opensocial08)
	})
	public interface NeedsOpenSocial08
	{
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.opensocial09)
	})
	public interface NeedsOpenSocial09
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.opensocial10)
	})
	public interface NeedsOpenSocial10
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.opensocial11)
	})
	public interface NeedsOpenSocial11
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.opensocialTemplates)
	})
	public interface NeedsOpenSocialTemplates
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.opensocialData)
	})
	public interface NeedsOpenSocialData
	{
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.osapi)
	})
	public interface NeedsOsapiFeature
	{
		/**
		 * Returns the OsapiFeature. 
		 * @return OsapiFeature
		 */
		OsapiFeature getOsapiFeature();
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.pubsub)
	})
	public interface NeedsPubsubFeature
	{
		/**
		 * Returns the PubsubFeature. 
		 * @return PubsubFeature
		 */
		PubsubFeature getPubsubFeature();
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.rpc)
	})
	public interface NeedsRpcFeature
	{
		/**
		 * Returns the RpcFeature.
		 * @return RpcFeature
		 */
		RpcFeature getRpcFeature();
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.setPrefs)
	})
	public interface NeedsSetPrefsFeature
	{
		/**
		 * Returns the SetPrefsFeature.
		 * @return SetPrefsFeature
		 */
		SetPrefsFeature getSetPrefsFeature();
	}
	
	@NeedsFeatures({
		@Feature(ContainerFeature.setTitle)
	})
	public interface NeedsSetTitleFeature
	{
		/**
		 * Returns the SetTitleFeature.
		 * @return SetTitleFeature
		 */
		SetTitleFeature getSetTitleFeature();
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.tabs)
	})
	public interface NeedsTabsFeature
	{
		/**
		 * Returns the TabsFeature.
		 * @return TabsFeature
		 */
		TabsFeature getTabsFeature();
	}

	@NeedsFeatures({
		@Feature(ContainerFeature.views)
	})
	public interface NeedsViewFeature
	{
		/**
		 * Returns the ViewFeature. 
		 * @return ViewFeature
		 */
		ViewFeature getViewFeature();	
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.ads)
	})
	public interface WantsAdsFeature
	{
		/**
		 * Returns the AdsFeature. 
		 * @return AdsFeature
		 */
		AdsFeature getAdsFeature();		
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.dynamicHeight)
	})
	public interface WantsDynamicHeightFeature
	{
		/**
		 * Returns the DynamicHeightFeature. 
		 * @return DynamicHeightFeature
		 */
		DynamicHeightFeature getDynamicHeightFeature();	
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.googleAnalytics)
	})
	public interface WantsGoogleAnalyticsFeature
	{
		/**
		 * Returns the GoogleAnalyticsFeature. 
		 * @return GoogleAnalyticsFeature
		 */
		GoogleAnalyticsFeature getGoogleAnalyticsFeature();
	}

	@WantsFeatures({
		@Feature(ContainerFeature.lockedDomain)
	})
	public interface WantsLockedDomain
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.minimessage)
	})
	public interface WantsMiniMessageFeature
	{
		/**
		 * Returns the MiniMessageFeature. 
		 * @return MiniMessageFeature
		 */
		MiniMessageFeature getMiniMessageFeature();
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.opensocial08)
	})
	public interface WantsOpenSocial08
	{
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.opensocial09)
	})
	public interface WantsOpenSocial09
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.opensocial10)
	})
	public interface WantsOpenSocial10
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.opensocial11)
	})
	public interface WantsOpenSocial11
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.opensocialTemplates)
	})
	public interface WantsOpenSocialTemplates
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.opensocialData)
	})
	public interface WantsOpenSocialData
	{
	}

	@WantsFeatures({
		@Feature(ContainerFeature.osapi)
	})
	public interface WantsOsapiFeature
	{
		/**
		 * Returns the OsapiFeature. 
		 * @return OsapiFeature
		 */
		OsapiFeature getOsapiFeature();
	}

	@WantsFeatures({
		@Feature(ContainerFeature.pubsub)
	})
	public interface WantsPubsubFeature
	{
		/**
		 * Returns the PubsubFeature. 
		 * @return PubsubFeature
		 */
		PubsubFeature getPubsubFeature();
	}

	@WantsFeatures({
		@Feature(ContainerFeature.rpc)
	})
	public interface WantsRpcFeature
	{
		/**
		 * Returns the RpcFeature.
		 * @return RpcFeature
		 */
		RpcFeature getRpcFeature();
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.setPrefs)
	})
	public interface WantsSetPrefsFeature
	{
		/**
		 * Returns the SetPrefsFeature.
		 * @return SetPrefsFeature
		 */
		SetPrefsFeature getSetPrefsFeature();
	}
	
	@WantsFeatures({
		@Feature(ContainerFeature.setTitle)
	})
	public interface WantsSetTitleFeature
	{
		/**
		 * Returns the SetTitleFeature.
		 * @return SetTitleFeature
		 */
		SetTitleFeature getSetTitleFeature();
	}

	@WantsFeatures({
		@Feature(ContainerFeature.tabs)
	})
	public interface WantsTabsFeature
	{
		/**
		 * Returns the TabsFeature.
		 * @return TabsFeature
		 */
		TabsFeature getTabsFeature();
	}

	@WantsFeatures({
		@Feature(ContainerFeature.views)
	})
	public interface WantsViewFeature
	{
		/**
		 * Returns the ViewFeature. 
		 * @return ViewFeature
		 */
		ViewFeature getViewFeature();	
	}	
}
