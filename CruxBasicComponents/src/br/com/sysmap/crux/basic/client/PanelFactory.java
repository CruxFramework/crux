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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.screen.HasWidgetsFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;

import com.google.gwt.user.client.ui.Panel;

/**
 * Base factory class for all panels
 * @author Thiago Bustamante
 */
public abstract class PanelFactory <T extends Panel> extends WidgetFactory<T> implements HasWidgetsFactory<T>
{
}
