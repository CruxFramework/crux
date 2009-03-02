package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.client.event.annotation.Validate;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;

/**
 * Default dispatcher for handle crux events at the server side.
 * @author Thiago
 *
 */
public abstract class AbstractDispatchPhase implements Phase 
{
	private static final Log logger = LogFactory.getLog(AbstractDispatchPhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	protected Object dispatch(Object controller, String methodName, Class<?>[] parametersTypes, Object[] parametersValues) throws Exception 
	{
			Method method = controller.getClass().getMethod(methodName, parametersTypes);
			validateMethod(method, controller);
			return method.invoke(controller, parametersValues);
	}
	
	protected void validateMethod(Method method, Object controller) throws Exception
	{
		Validate annot = method.getAnnotation(Validate.class);
		if (annot != null)
		{
			String validateMethod = annot.value();
			if (validateMethod == null || validateMethod.length() == 0)
			{
				validateMethod = "validate"+ method.getName();
			}
			try 
			{
				Method validate = controller.getClass().getMethod(validateMethod, new Class<?>[]{});
				validate.invoke(controller, new Object[]{});
			} 
			catch (Exception e) 
			{
				throw new ValidateException (e.getCause().getLocalizedMessage());
			} 
		}
	}
	
	protected Object getController(String controllerName) throws PhaseException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return getControllerFactory().getController(controllerName);
	}
	
	protected ControllerFactory getControllerFactory() throws PhaseException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return ControllerFactoryInitializer.getControllerFactory();
	}
	
	protected void bindControllerParameters(Object controller, DispatchData dispatchData)
	{
		for (String key: dispatchData.getParameters())
		{
			try
			{
				BeanUtils.copyProperty(controller, key, dispatchData.getParameter(key));
			}
			catch (Throwable e) 
			{
				if (logger.isInfoEnabled()) logger.info(messages.dispatchPhasePropertyNotBound(key));
			}
		}
	}
}
