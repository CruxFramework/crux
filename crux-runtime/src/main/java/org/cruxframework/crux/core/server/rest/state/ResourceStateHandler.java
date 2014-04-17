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
package org.cruxframework.crux.core.server.rest.state;


/**
 * Control the etags, dateModified and expires of rest services that can be cached. 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public interface ResourceStateHandler
{
	/**
	 * Contains state information about a given rest URI
	 * @author Thiago da Rosa de Bustamante
	 */
	public static interface ResourceState
	{
		long getDateModified();
		boolean isExpired();
		String getEtag();
	}

	/**
	 * Add state information about one rest URI
	 * @param uri
	 * @param dateModified
	 * @param expires
	 * @param etag
	 * @return
	 */
	ResourceState add(String uri, long dateModified, long expires, String etag);
	
	/**
	 * Retrieve state information about one rest URI
	 * @param uri
	 * @return
	 */
	ResourceState get(String uri);
	
	/**
	 * Clear the state information about one rest URI
	 * @param uri
	 */
	void remove(String uri);
	
	/**
	 * Clear the state information about any rest URI that starts with one of the baseURIs provided 
	 * @param baseURIs
	 */
	void removeSegments(String... baseURIs);
	
	/**
	 * Clear all state information from rest URIs
	 */
	void clear();
}