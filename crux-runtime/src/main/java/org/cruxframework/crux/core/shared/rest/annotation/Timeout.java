package org.cruxframework.crux.core.shared.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the number of milliseconds that the annotated method or proxy class must wait for a request to complete.
 * @author Carlos de Sa
 * @since 5.3.5
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {

	/**
	 * The number of milliseconds to wait for a request to complete.
	 * @return number of milliseconds to wait before canceling the request, a value of zero disables timeouts.
	 */
	int value();
	
}
