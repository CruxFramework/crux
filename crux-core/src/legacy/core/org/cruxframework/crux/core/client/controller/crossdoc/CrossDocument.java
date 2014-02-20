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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;

/**
 * <p>
 * This is the base interface for all cross document objects. A cross document object
 * can be called from out of its document.
 * </p>
 * The first step to implement a cross document communication is creating an interface extending the CrossDocument interface.
 * <p>
 * For example:
 * <p>
 * <pre>
 *public interface MyControllerCrossDoc extends CrossDocument {
 *   void myMethod(String str, boolean b, MySerializableDTO dto);
 *   List{@code <String>} myOtherMethod() throws MyCustomException;
 *}
 *</pre>
 *<p>
 * The methods declared on this interface are those which you want to make available for calls from other 
 * controllers. All method parameters and return types must be a primitive type (or a primitive wrapper), 
 * an Enum or implement java.io.Serializable or com.google.gwt.user.client.rpc.IsSerializable.
 * <p>
 * The second step is to make your controller class implement the new interface.
 * <p>
 * For example:
 * <p>
 * <pre>
 *{@code @}{@link Controller}("myController")
 *public class MyController implements MyControllerCrossDoc {
 *   public void myMethod(String str, boolean b, MySerializableDTO dto){
 *     // code here
 *  }
 *   
 *  public List{@code <String>} myOtherMethod() throws MyCustomException{
 *     // code here
 *  }
 *   
 *  // You can have any other methods here.
 *  {@code @}{@link Expose}
 *  public void myEventHandlerMethod(ClickEvent event){
 *     // code here
 *  }
 *}
 * </pre>
 * To ensure that everything will run fine, you must obey the following rules:
 * <p>
 * <li>Both controller class and CrossDocument interface must be coded on the same package;</li>
 * <li>The name of the CrossDocument interface must have the form: "{@code <Controller Name>}CrossDoc".</li>
 * <p>
 * Now we can show how to make the call from a second controller.
 *</p>
 *<pre>
 *{@code @}{@link Controller}("mySecondController")
 *public class MySecondController {
 *   {@code @}{@link Create}
 *   protected MyControllerCrossDoc crossDoc; // you could also use GWT.create(MyControllerCrossDoc.class)
 *
 *   {@code @}{@link @Expose}
 *   public void onClick(ClickEvent event){
 *     
 *      crossDoc.myMethod("test", true, new MySerializableDTO());
 *     
 *      try{
 *         List{@code <String>} result = crossDoc.myOtherMethod();
 *         //do something with result...
 *      }catch(MyCustomException e){
 *         // handle error
 *      }
 *   }
 *}
 *</pre>
 *<p>
 * On the previous example, the crossDoc object will call the methods on a controller located on 
 * the same screen of the caller object.
 * <p>
 * If you want to inform a new target for the call, you must cast the crossDoc object to TargetDocument 
 * interface and set the target of the call.
 * <p>
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
 * @see Target
 * @see TargetDocument
 */
@Legacy
@Deprecated
public interface CrossDocument{
}
