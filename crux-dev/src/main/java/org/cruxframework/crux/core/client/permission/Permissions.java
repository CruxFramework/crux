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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Check if the current user is authorized to realize the job related with given roles. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Permissions
{
	private static RoleManager roleManager;
	private static PermissionsUIHandler permissionsUIHandler;

	/**
	 * Return true if the current user has the given role associated to him
	 * @param role
	 * @return
	 */
	public static boolean hasRole(String role)
	{
		return getRoleManager().hasRole(role);
	}
	
	/**
	 * Notify the given widget that current user has no role for edit its contents
	 * @param widget
	 */
	public static void markAsUnauthorizedForEdition(HasEnabled widget)
	{
		getPermissionsUIHandler().markAsUnauthorizedForEdition(widget);
	}
	
	/**
	 * Notify the given widget that current user has no role for view its contents
	 * @param widget
	 */
	public static void markAsUnauthorizedForViewing(IsWidget widget)
	{
		getPermissionsUIHandler().markAsUnauthorizedForViewing(widget);
	}
	
	private static RoleManager getRoleManager()
	{
		if (roleManager == null)
		{
			roleManager = GWT.create(RoleManager.class);
		}
		return roleManager;
	}
	
	private static PermissionsUIHandler getPermissionsUIHandler()
	{
		if (permissionsUIHandler == null)
		{
			permissionsUIHandler = GWT.create(PermissionsUIHandler.class);
		}
		return permissionsUIHandler;
	}
}
