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
package org.cruxframework.crux.core.client.screen.views;

/**
 * A marker interface used to generate value objects bound to widgets on the view. Each "get" method
 * will read the value of a widget, and the "set" methods will write the value into the widget. The name
 * of the property on the get or set method will be used to discover the widget on the view (view identifier)
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ViewBinder extends ViewBindable
{

}
