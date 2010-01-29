package br.com.sysmap.crux.module.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleDependency
{
	String value();
	String minVersion() default "";
	String maxVersion() default "";
}
