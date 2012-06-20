/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.rebind.wizard;

import org.cruxframework.crux.core.client.context.ContextManager;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.crossdocument.CrossDocumentProxyCreator;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.SerializationUtils;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.Shared;
import org.cruxframework.crux.core.rebind.crossdocument.gwt.TypeSerializerCreator;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;

/**
 * This class generates a proxy for context access. Used by wizard to write its data into context
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class WizardDataSerializerProxyCreator extends CrossDocumentProxyCreator
{
	private static final String WIZARD_DATA_SUFFIX = "_WizardDataProxy";
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @param context
	 * @param baseProxyType
	 */
	public WizardDataSerializerProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseProxyType)
	{
		super(logger, context, baseProxyType);
	}
	
	/**
	 * Generate any fields required by the proxy.
	 * @throws CruxGeneratorException 
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
		String typeSerializerName = SerializationUtils.getTypeSerializerQualifiedName(baseProxyType);
		srcWriter.println("private static final " + typeSerializerName + " SERIALIZER = new " + typeSerializerName + "();");
		srcWriter.println("private String wizard;");
		srcWriter.println();
	}

	/**
	 * Generates the method for context reading.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyReadMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		w.println();

		JType returnType = method.getReturnType().getErasedType();
		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.println("try {");
		w.indent();

		String retName = nameFactory.createName("ret");
		w.println("String "+retName + "="+ContextManager.class.getName()+".getContextHandler().read(wizard+\"_WizardData\");");
		w.println("if (!"+StringUtils.class.getCanonicalName()+".isEmpty("+retName+")){");
		w.indent();
		w.print("return ("+returnType.getQualifiedSourceName()+") ");
		w.println("CrossDocumentReader." + getReaderFor(returnType).name()+".read(createStreamReader("+retName+"));");;
		w.outdent();
		w.println("}");
		w.println("else{");
		w.indent();
		w.println("return null;");
		w.outdent();
		w.println("}");
		
		w.outdent();
		generateSerializationCatchBlock(w, nameFactory);

		w.outdent();
		w.println("}");
	}

	/**
	 * @param w
	 * @param serializableTypeOracle
	 * @throws CruxGeneratorException 
	 */
	@Override
	protected void generateProxyMethods(SourceWriter w) throws CruxGeneratorException
	{
		JMethod[] syncMethods = baseProxyType.getOverridableMethods();
		for (JMethod method : syncMethods)
		{
			if (method.getName().equals("readObject"))
			{
				generateProxyReadMethod(w, method);
			}
			else if (method.getName().equals("setWizard")) 
			{
				generateProxySetWizardMethod(w, method);
			}
			else if (method.getName().equals("writeObject"))
			{
				generateProxyWriteMethod(w, method);
			}
			else if (method.getName().equals("getResource"))
			{
				generateProxyGetResourceMethod(w, method);
			}
		}
	}

	/**
	 * Generates method for context writing.
	 * @throws CruxGeneratorException 
	 */
	protected void generateProxyWriteMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		w.println();

		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.print(SerializationStreamWriter.class.getSimpleName());
		w.print(" ");
		String streamWriterName = nameFactory.createName("streamWriter");
		w.println(streamWriterName + " = createStreamWriter();");

		w.println("try {");
		w.indent();

		JParameter[] params = method.getParameters();
		JParameter param = params[0];

		w.println("if ("+param.getName()+" == null){");
		w.indent();
		w.println(ContextManager.class.getName()+".getContextHandler().erase(wizard+\"_WizardData\");");
		w.outdent();
		w.println("} else {");
		w.indent();
		w.print(streamWriterName + ".");
		w.print(Shared.getStreamWriteMethodNameFor(param.getType()));
		w.println("(" + param.getName() + ");");
		String payloadName = nameFactory.createName("payload");
		w.println("String " + payloadName + " = "+ streamWriterName + ".toString();");
		w.println(ContextManager.class.getName()+".getContextHandler().write(wizard+\"_WizardData\", "+payloadName+");");
		w.outdent();
		w.println("}");
		
		w.outdent();
		generateSerializationCatchBlock(w, nameFactory);

		w.outdent();
		w.println("}");
	}
	
	/**
	 * @param w
	 * @param method
	 * @throws CruxGeneratorException
	 */
	protected void generateProxySetWizardMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		w.println();

		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.println("this.wizard = "+method.getParameters()[0].getName()+";");

		w.outdent();
		w.println("}");
	}

	protected void generateProxyGetResourceMethod(SourceWriter w, JMethod method) throws CruxGeneratorException
	{
		JType returnType = method.getReturnType().getErasedType();
		
		w.println();

		NameFactory nameFactory = new NameFactory();
		generateProxyMethodSignature(w, nameFactory, method);
		w.println("{");
		w.indent();		

		w.println("return new "+returnType.getQualifiedSourceName()+"();");

		w.outdent();
		w.println("}");
	}
	
	/**
	 * @param logger
	 * @param context
	 * @param typesSentFromBrowser
	 * @param typesSentToBrowser
	 * @throws CruxGeneratorException
	 */
	@Override
	protected void generateTypeSerializers(SerializableTypeOracle typesSentFromBrowser,
			                            SerializableTypeOracle typesSentToBrowser) throws CruxGeneratorException
	{
		try
        {
	        TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentFromBrowser, typesSentToBrowser, context, 
					SerializationUtils.getTypeSerializerQualifiedName(baseProxyType),
	                SerializationUtils.getTypeSerializerSimpleName(baseProxyType));
	        tsc.realize(logger);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @return the full qualified name of the proxy object.
	 */
	public String getProxyQualifiedName()
	{
		return baseProxyType.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	public String getProxySimpleName()
	{
		return baseProxyType.getSimpleSourceName() + WIZARD_DATA_SUFFIX;
	}
	
	/**
	 * @param w
	 * @param nameFactory
	 */
	private void generateSerializationCatchBlock(SourceWriter w, NameFactory nameFactory)
    {
	    w.print("} catch (SerializationException ");
	    String exceptionName = nameFactory.createName("ex");
	    w.println(exceptionName + ") {");
		w.indent();
		w.println("throw new CrossDocumentException(ex.getMessage(), ex);");
		w.outdent();
	    w.println("}");
    }
}
