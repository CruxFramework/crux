package br.com.sysmap.crux.core.server.lifecycle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.sysmap.crux.core.server.lifecycle.Phase;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ParametersBindPhase 
{
	Class<? extends Phase> value() default br.com.sysmap.crux.core.server.lifecycle.phase.bind.AutoParametersBindPhase.class;
}
