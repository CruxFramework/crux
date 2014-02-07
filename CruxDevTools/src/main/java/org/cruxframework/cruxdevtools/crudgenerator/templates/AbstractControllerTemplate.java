package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.cruxdevtools.crudgenerator.GenerationException;
import org.cruxframework.cruxdevtools.crudgenerator.Template;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.EntityMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.FieldMetadata.GUI;
import org.cruxframework.cruxdevtools.crudgenerator.metadata.IdentifierMetadata;
import org.cruxframework.cruxdevtools.crudgenerator.util.TextUtils;

import com.google.gwt.user.client.ui.Widget;

public class AbstractControllerTemplate extends Template
{
	protected final String packageName;
	protected Set<String> imports = new HashSet<String>();
	protected final String dtoClassName;
	protected final String remoteServiceInterface;
	protected final String screenClassName;
	protected Map<String, String> requiredFields = new LinkedHashMap<String, String>();

	public AbstractControllerTemplate(EntityMetadata entity, File outputDir, String packageName, String dtoClassName, String remoteServiceInterface, String screenClassName)
	{
		super(entity, outputDir);
		this.packageName = packageName;
		this.dtoClassName = dtoClassName;
		this.remoteServiceInterface = remoteServiceInterface;
		this.screenClassName = screenClassName;
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".java");
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+AbstractControllerTemplate.class.getPackage().getName().replace('.', '/')+"/abstract-controller-template.vm";
	}

	@Override
	protected Map<String, Object> getContextMap()
	{
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
        context.put("imports", imports);
		String simpleDTOClassName = dtoClassName.substring(dtoClassName.lastIndexOf(".") + 1);
		context.put("entity", simpleDTOClassName);
		String simpleRemoteInterfaceClassName = remoteServiceInterface.substring(remoteServiceInterface.lastIndexOf(".") + 1);
		context.put("remoteInterface", simpleRemoteInterfaceClassName);
		context.put("textUtils", new TextUtils());
		context.put("screenClassName", screenClassName.substring(screenClassName.lastIndexOf(".") + 1));

		processIdentifier(context);
		processRequiredFields(context);

        imports.add(dtoClassName);
        imports.add(AsyncCallbackAdapter.class.getCanonicalName());
        imports.add(remoteServiceInterface+"Async");
        //imports.add(Create.class.getCanonicalName());
        imports.add(screenClassName);
        imports.add(Map.class.getCanonicalName());
        imports.add(Widget.class.getCanonicalName());
        imports.add(LinkedHashMap.class.getCanonicalName());

        return context;
	}


	public String getTypeName()
	{
		return TextUtils.toJavaIdentifier(getEntity().getName(), true)+"AbstractController";
	}

	protected void processIdentifier(Map<String, Object> context)
	{
		IdentifierMetadata identifier = getEntity().getIdentifier();

		List<String> fieldNames = identifier.getFieldNames();
		if (fieldNames.size() == 1)
		{
			FieldMetadata fieldMetadata = getEntity().getField(fieldNames.get(0));
			String javaType = TemplateUtils.getJavaType(fieldMetadata);
			String simpleJavaType = javaType.substring(javaType.lastIndexOf(".") + 1);

			if(javaType.contains("."))
			{
				imports.add(javaType);
			}

			context.put("compositeIdentifier", false);
			context.put("identifierType", simpleJavaType);
			context.put("identifierName", TextUtils.toJavaIdentifier(fieldMetadata.getName(), false));
		}
		else if (fieldNames.size() > 1)
		{
			String simpleJavaType = dtoClassName.substring(dtoClassName.lastIndexOf(".") + 1);
			context.put("compositeIdentifier", true);
			context.put("identifierType", "PrimaryKey()");
			context.put("identifierFields", fieldNames);
		}
		else
		{
			throw new GenerationException("Invalid Identifier declaration for entity "+getEntity().getName());
		}
	}

	private void processRequiredFields(Map<String, Object> context)
	{
		for (String fieldName : getEntity().getFieldNames())
		{
			FieldMetadata field = getEntity().getField(fieldName);

			if(field.isRequired() && !GUI.RadioBox.equals(field.getFieldGUI()))
			{
				requiredFields.put(field.getName(), field.getName());
			}
		}

		context.put("requiredFields", requiredFields);
	}


}
