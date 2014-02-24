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
 * A Default UI handler for user lack of permissions scenarios.
 * It just set widget enabled property to false and add a dependent styleName to it, 
 * when user can not edit its content.   
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DefaultPermissionsUIHandler implements PermissionsUIHandler
{

	@Override
    public void markAsUnauthorizedForEdition(HasEnabled widget)
    {
		widget.setEnabled(false);
		((IsWidget)widget).asWidget().addStyleDependentName("disabled");
    }

	@Override
    public void markAsUnauthorizedForViewing(IsWidget widget)
    {
		widget.asWidget().setVisible(false);
    }
}
