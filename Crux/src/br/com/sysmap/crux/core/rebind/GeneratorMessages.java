package br.com.sysmap.crux.core.rebind;

/**
 * Messages from generator.
 * @author Thiago
 *
 */
public interface GeneratorMessages 
{
	String errorGeneratingProxy(String typeName);
	String errorGeneratingRegisteredComponentsNotRegistered();
	String errorGeneratingRegisteredClientHandler(String component, String errMesg);
	String errorGeneratingRegisteredClientCallback(String component, String errMesg);
	String errorGeneratingRegisteredFormatter(String component, String errMesg);
	String errorGeneratingRegisteredElement(String errMsg);
	String errorGeneratingRegisteredElementInvalidScreenID();
	String errorGeneratingCruxConfig();
	String errorinvokingGeneratedMethod();
}
