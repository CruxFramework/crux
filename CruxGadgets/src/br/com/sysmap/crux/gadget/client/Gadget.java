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
/**
 * This package contains all the crux's client engine.
 */
package br.com.sysmap.crux.gadget.client;

import com.google.gwt.gadgets.client.AdsFeature;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.GoogleAnalyticsFeature;
import com.google.gwt.gadgets.client.SetPrefsFeature;
import com.google.gwt.gadgets.client.SetTitleFeature;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.ViewFeature;
import com.google.gwt.gadgets.client.osapi.OsapiFeature;
import com.google.gwt.gadgets.client.rpc.RpcFeature;


/**
 * Provides access to gadgets specif features
 * @author Thiago da Rosa de Bustamante
 */
public interface Gadget 
{
	/**
	 * Returns the AddsFeature. Your class must implement NeedsAds to retrieve this value. If does not, null will be returned
	 * @return AddsFeature
	 */
	AdsFeature getAddFeature();
	
	/**
	 * Returns the DynamicHeightFeature. Your class must implement NeedsDynamicHeight to retrieve this value. If does not, null will be returned
	 * @return DynamicHeightFeature
	 */
	DynamicHeightFeature getDynamicHeightFeature();

	/**
	 * Returns the GoogleAnalyticsFeature. Your class must implement NeedsGoogleAnalytics to retrieve this value. If does not, null will be returned
	 * @return GoogleAnalyticsFeature
	 */
	GoogleAnalyticsFeature getGoogleAnalyticsFeature();

	/**
	 * Returns the GoogleAnalyticsFeature. Your class must implement NeedsGoogleAnalytics to retrieve this value. If does not, null will be returned
	 * @return GoogleAnalyticsFeature
	 */
	OsapiFeature getOsapiFeature();

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
	 * @return the UserPreferences object
	 */
	UserPreferences getUserPreferences();

	/**
	 * Returns the ViewFeature. Your class must implement NeedsViews to retrieve this value. If does not, null will be returned
	 * @return ViewFeature
	 */
	ViewFeature getViewFeature();
}
