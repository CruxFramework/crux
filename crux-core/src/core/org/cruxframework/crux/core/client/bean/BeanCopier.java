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
 * An utility interface to allow copies between two objects.
 * <p> You can define copiers by extending this interface.</p>
 * <p>
 * Imagine you have the following java bean classes defined as:
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
 * <pre>
 * public class User {
 *    private String login;
 *    private String name;
 *    private int age;
 *    
 *    public String getLogin(){return login;}
 *    public void setLogin(String login){this.login = login;}
 *    public String getName(){return name;}
 *    public void setName(String name){this.name = name;}
 *    public int getAge(){return age;}
 *    public void setAge(int age){this.age = age;}
 * }
 * </pre>
 * </p>
 * <p>
 * You can define the following interface to make copies between objects 
 * of the type Person to/from objects of type User, considering its property values:
 * <pre>
 * public interface PersonCopier extends BeanCopier<Person, User> {}
 * </pre>
 * </p>
 * <p>
 * To use the copier, just call GWT.create on the given interface, or inject it on 
 * your class. 
 * <pre>
 * public class MyController {
 *    {@code @Inject}
 *    private PersonCopier copier;
 *    
 *    {@code @Expose}
 *    public void myMethod() {
 *       // read two objects...
 *       copier.copyTo(person, user);
 *       copier.copyFrom(user, person);
 *       }
 *    }
 * }
 * </pre>
 * </p>
 * <p>
 * Note, on the above example, that class User has a property login and Person class does not. No problem, that property
 * will be ignored during copies. 
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 * @param <A> The type of the origin bean of the copy.
 * @param <B> The type of the target bean of the copy.
 */
public interface BeanCopier<A, B>
{
	/**
	 * Copy property values from one object to another
	 * @param from the origin object
	 * @param to the target object
	 */
	public void copyTo(A from, B to);
	/**
	 * Copy property values from one object to another
	 * @param from the origin object
	 * @param to the target object
	 */
	public void copyFrom(B from, A to);
}
