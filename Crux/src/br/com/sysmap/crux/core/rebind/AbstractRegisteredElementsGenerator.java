package br.com.sysmap.crux.core.rebind;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.screen.Screen;
import br.com.sysmap.crux.core.server.screen.ScreenFactory;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public abstract class AbstractRegisteredElementsGenerator extends Generator
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException 
	{
		try 
		{
			TypeOracle typeOracle = context.getTypeOracle(); 
			JClassType classType = typeOracle.getType(typeName);
			
			String screenID; 
			try
			{
				screenID = CruxScreenBridge.getInstance().getLastPageRequested();
			}
			catch (Throwable e) 
			{
				logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementInvalidScreenID(),e);
				throw new UnableToCompleteException();
			}
			Screen screen = ScreenFactory.getInstance().getScreen(screenID);
			
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName() + "Impl";
			generateClass(logger, context, classType, screen);
			return packageName + "." + className;
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElement(e.getLocalizedMessage()), e);
			throw new UnableToCompleteException();
		}
	}
	
	protected abstract void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, Screen screen);
}
