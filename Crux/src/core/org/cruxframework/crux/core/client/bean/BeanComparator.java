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
 * An utility interface to allow comparation between two objects.
 * <p> You can define comparators by extending this interface.</p>
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
 * You can define the following interface to make comparation between two objects 
 * of the given class, considering its property values:
 * <pre>
 * public interface PersonComparator extends BeanComparator<Person> {}
 * </pre>
 * </p>
 * <p>
 * To use the comparator, just call GWT.create on the given interface, or inject it on 
 * your class. 
 * <pre>
 * public class MyController {
 *    {@code @Inject}
 *    private PersonComparator comparator;
 *    
 *    {@code @Expose}
 *    public void myMethod() {
 *       // read two objects...
 *       if(comparator.equals(person1, person2)) {
 *           // ...
 *       }
 *    }
 * }
 * </pre>
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 * @param <A> The type of the beans to be compared by the comparator interface.
 */
public interface BeanComparator<A>
{
	/**
	 * Compare two objects, considering the value of each property, that follow 
	 * the java bean notation.
	 * @param a1 object1
	 * @param a2 object2
	 * @return true if all properties have the same value on the two objects
	 */
	boolean equals(A a1, A a2);
}
