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
package org.cruxframework.crux.core.client.permission;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Handle any interface modifications needed to be done on widgets for 
 * lack of permissions reasons. 
 * @author Thiago da Rosa de Bustamante
 */
public interface PermissionsUIHandler
{
	/**
	 * Notify the given widget that current user has no role for edit its contents
	 * @param widget
	 */
	void markAsUnauthorizedForEdition(HasEnabled widget);
	
	/**
	 * Notify the given widget that current user has no role for view its contents
	 * @param widget
	 */
	void markAsUnauthorizedForViewing(IsWidget widget);
}
