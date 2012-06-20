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
package org.cruxframework.crux.core.client.controller.crossdoc;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.JSWindow;

/**
 * All cross document objects created by Crux implements that interface. It can be
 * used to allow the caller to define where the method will be invoked.
 * <p>
 * You must cast your {@link CrossDocument} interface to TargetDocument to change the
 * target of the cross document call.
 * <p>
 * For example:
 * <pre>
 *{@code @}{@link Controller}("mySecondController")
 *public class MySecondController {
 *   {@code @}{@link Create}
 *   protected MyControllerCrossDoc crossDoc; // you could also use GWT.create(MyControllerCrossDoc.class)
 *
 *   {@code @}{@link Expose}
 *   public void onClick(ClickEvent event){
 *      ((TargetDocument)crossDoc).setTarget(Target.TOP);       
 *      crossDoc.myMethod("test", true, new MySerializableDTO());
 *   }
 *}
 *</pre>
 * 
 * @author Thiago da Rosa de Bustamante
 * @see CrossDocument
 * @see Target
 */
public interface TargetDocument extends CrossDocument
{
	/**
	 * Sets the target for the cross document call.
	 * 
	 * @param target - the target
	 */
	void setTarget(Target target);
	
	/**
	 * Sets the target frame for the cross document call.
	 * 
	 * @param frame - the name of the frame
	 */
	void setTargetFrame(String frame);
	
	/**
	 * Sets the target sibling frame for the cross document call.
	 * 
	 * @param frame - the name of the sibling frame
	 */
	void setTargetSiblingFrame(String frame);
	
	/**
	 * Sets the target window for the cross document call.
	 * 
	 * @param jsWindow - the window where the call will occur
	 */
	void setTargetWindow(JSWindow jsWindow);
}
