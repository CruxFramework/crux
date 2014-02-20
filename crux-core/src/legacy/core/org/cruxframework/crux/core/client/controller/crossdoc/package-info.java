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
 * Provides communication between controllers of an application, 
 * even if running on different documents.
 *
 * <h2>Package Specification</h2>
 *
 * <p>This package contains the interface needed to write controllers 
 * that can be called from other documents.</p>
 * <p>Start with the following classes:</p>
 * <li>{@link org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument}</li>
 * <li>{@link org.cruxframework.crux.core.client.controller.crossdoc.Target}</li>
 * <li>{@link org.cruxframework.crux.core.client.controller.crossdoc.TargetDocument}</li>
 *
 */
package org.cruxframework.crux.core.client.controller.crossdoc;