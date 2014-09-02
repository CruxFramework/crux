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
package org.cruxframework.crux.core.client.bean;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * An utility interface to allow object serialization to/from json notation.
 * <p> You can define encoders by extending this interface.</p>
 * <p>
 * Imagine you have the following java bean class defined as:
 * <pre>
 * public class Person {
 *    private String name;
 *    private int age;
 *    
 *    public String getName(){return name;}
 *    public void setName(String name){this.name = name;}
 *    public int getAge(){return age;}
 *    public void setAge(int age){this.age = age;}
 * }
 * </pre>
 * </p>
 * <p>
 * You can define the following interface to make serialization to / from json
 * notation:
 * <pre>
 * public interface PersonEncoder extends JsonEncoder<Person> {}
 * </pre>
 * </p>
 * <p>
 * To use the encoder, just call GWT.create on the given interface, or inject it on 
 * your class. 
 * <pre>
 * public class MyController {
 *    {@code @Inject}
 *    private PersonEncoder encoder;
 *    
 *    {@code @Expose}
 *    public void myMethod() {
 *       // read the object...
 *       String json = encoder.encode(person);
 *       Person decoded = encoder.decode(json);
 *    }
 * }
 * </pre>
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 * @param <T> The type of the beans to be serialized by the encoder interface.
 */
public interface JsonEncoder<T>
{
	/**
	 * Encode the given object in a JSON notation string 
	 * @param object object to be encoded
	 * @return a string in JSON notation representing the object
	 */
	String encode(T object);
	
	/**
	 * Transform the given object in a javascript native object 
	 * @param object object to be encoded
	 * @return a native javascript object
	 */
	JavaScriptObject toJavaScriptObject(T object);
	
	/**
	 * Encode the given JSON notation string in a object 
	 * 
	 * @param jsonText JSON notation string
	 * @return a java object
	 */
	T decode(String jsonText);

	/**
	 * Transform the given javascript native object in a java object 
	 * @param object javascript native object to be decoded
	 * @return a java object
	 */
	T fromJavaScriptObject(JavaScriptObject object);
}
