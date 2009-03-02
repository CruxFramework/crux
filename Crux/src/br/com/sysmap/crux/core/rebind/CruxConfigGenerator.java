package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.screen.ScreenStateManagerInitializer;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class CruxConfigGenerator extends Generator
{
	protected GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException 
	{
		try 
		{
			Class<?> requestedClass = Class.forName(typeName);
			TypeOracle typeOracle = context.getTypeOracle(); 
			JClassType classType = typeOracle.getType(typeName);
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName() + "Impl";
			generateClass(logger, context, classType, requestedClass);
			return packageName + "." + className;
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingCruxConfig(), e);
			throw new UnableToCompleteException();
		}
	}

	protected void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, Class<?> requestedClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		PrintWriter printWriter = null;
		String packageName = classType.getPackage().getName();
		String wrapperClassName = classType.getSimpleSourceName() + "Impl";

		printWriter = context.tryCreate(logger, packageName, wrapperClassName);
		// if printWriter is null, source code has ALREADY been generated, return
		if (printWriter == null) return;

		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, wrapperClassName);
		composer.addImplementedInterface("br.com.sysmap.crux.core.client.config.CruxConfig");
		SourceWriter sourceWriter = null;
		sourceWriter = composer.createSourceWriter(context, printWriter);

		generateMethods(sourceWriter, classType);

		sourceWriter.outdent();
		sourceWriter.println("}");

		context.commit(logger, printWriter);
	}

	private void generateMethods(SourceWriter sourceWriter, JClassType classType) throws InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		sourceWriter.println("public boolean clientMustPreserveState(){");
		sourceWriter.println("return "+ScreenStateManagerInitializer.getScreenStateManager().clientMustPreserveState()+";");
		sourceWriter.println("}");
	}
	
}
