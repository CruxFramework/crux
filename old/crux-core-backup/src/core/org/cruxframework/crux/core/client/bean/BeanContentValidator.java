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

/**
 * An utility interface to allow content validation on objects.
 * <p> You can define content validators by extending this interface.</p>
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
 * You can define the following interface to check if objects have all of its
 * properties filled or if them are all empty:
 * <pre>
 * public interface PersonValidator extends BeanContentValidator<Person> {}
 * </pre>
 * </p>
 * <p>
 * To use the validator, just call GWT.create on the given interface, or inject it on 
 * your class. 
 * <pre>
 * public class MyController {
 *    {@code @Inject}
 *    private PersonValidator validator;
 *    
 *    {@code @Expose}
 *    public void myMethod() {
 *       // read the object...
 *       if(!validator.isFilled(person)) {
 *           // ...
 *       }
 *    }
 * }
 * </pre>
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 * @param <A> The type of the beans to be validated by the validator interface.
 */
public interface BeanContentValidator<A>
{
	/**
	 * Check if the given object has all of their properties empty
	 * @param a the object to check
	 * @return true if the object is empty
	 */
	boolean isEmpty(A a);
	/**
	 * Check if the given object has all of their properties filled
	 * @param a the object to check
	 * @return true if the object is filled
	 */
	boolean isFilled(A a);
}
