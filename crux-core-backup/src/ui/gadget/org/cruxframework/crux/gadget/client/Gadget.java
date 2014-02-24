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
/**
 * This package contains all the crux's client engine.
 */
package org.cruxframework.crux.gadget.client;

import org.cruxframework.crux.gadget.client.features.AdsFeature;
import org.cruxframework.crux.gadget.client.features.DynamicHeightFeature;
import org.cruxframework.crux.gadget.client.features.GoogleAnalyticsFeature;
import org.cruxframework.crux.gadget.client.features.MiniMessageFeature;
import org.cruxframework.crux.gadget.client.features.PubsubFeature;
import org.cruxframework.crux.gadget.client.features.RpcFeature;
import org.cruxframework.crux.gadget.client.features.SetPrefsFeature;
import org.cruxframework.crux.gadget.client.features.SetTitleFeature;
import org.cruxframework.crux.gadget.client.features.TabsFeature;
import org.cruxframework.crux.gadget.client.features.UserPreferences;
import org.cruxframework.crux.gadget.client.features.ViewFeature;
import org.cruxframework.crux.gadget.client.features.osapi.OsapiFeature;



/**
 * Provides access to gadgets specification features
 * @author Thiago da Rosa de Bustamante
 */
public interface Gadget 
{
	/**
	 * Returns the AdsFeature. Your class must implement NeedsAds to retrieve this value. If does not, null will be returned
	 * @return AdsFeature
	 */
	AdsFeature getAdsFeature();
	
	/**
	 * Returns the DynamicHeightFeature. Your class must implement NeedsDynamicHeight to retrieve this value. If does not, null will be returned
	 * @return DynamicHeightFeature
	 */
	DynamicHeightFeature getDynamicHeightFeature();

	/**
	 * Returns the GoogleAnalyticsFeature. Your class must implement NeedsGoogleAnalyticsFeature to retrieve this value. If does not, null will be returned
	 * @return GoogleAnalyticsFeature
	 */
	GoogleAnalyticsFeature getGoogleAnalyticsFeature();

	/**
	 * Returns the MiniMessageFeature. Your class must implement NeedsMiniMessageFeature to retrieve this value. If does not, null will be returned
	 * @return MiniMessageFeature
	 */
	MiniMessageFeature getMiniMessageFeature();	
	
	/**
	 * Returns the OsapiFeature. Your class must implement NeedsOsapiFeature to retrieve this value. If does not, null will be returned
	 * @return OsapiFeature
	 */
	OsapiFeature getOsapiFeature();

	/**
	 * Returns the PubsubFeature. Your class must implement NeedsPubsubFeatureFeature to retrieve this value. If does not, null will be returned
	 * @return PubsubFeature
	 */
	PubsubFeature getPubsubFeature();

	/**
	 * Returns the RpcFeature. Your class must implement NeedsRpc to retrieve this value. If does not, null will be returned
	 * @return RpcFeature
	 */
	RpcFeature getRpcFeature();

	/**
	 * Returns the SetPrefsFeature. Your class must implement NeedsSetPrefs to retrieve this value. If does not, null will be returned
	 * @return SetPrefsFeature
	 */
	SetPrefsFeature getSetPrefsFeature();

	/**
	 * Returns the SetTitleFeature. Your class must implement NeedsSetTitle to retrieve this value. If does not, null will be returned
	 * @return SetTitleFeature
	 */
	SetTitleFeature getSetTitleFeature();

	/**
	 * Returns the TabsFeature. Your class must implement NeedsTabsFeature to retrieve this value. If does not, null will be returned
	 * @return TabsFeature
	 */
	TabsFeature getTabsFeature();
	
	/**
	 * @return the UserPreferences object
	 */
	UserPreferences getUserPreferences();

	/**
	 * Returns the ViewFeature. Your class must implement NeedsViews to retrieve this value. If does not, null will be returned
	 * @return ViewFeature
	 */
	ViewFeature getViewFeature();
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	boolean hasFeature(String feature);
}
