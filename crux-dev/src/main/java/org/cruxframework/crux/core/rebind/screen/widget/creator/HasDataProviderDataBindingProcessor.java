/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ExpressionDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.ObjectDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.DataBindingProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HasDataProviderDataBindingProcessor implements DataBindingProcessor
{
	private String bindingContextVariable;
	private String collectionDataObject; 
	private String collectionObjectReference;
	private Set<String> converterClasses = new HashSet<String>();
	private Set<String> converterDeclarations = new HashSet<String>();
	private String itemVar;
	private String collectionDataObjectVariable;
	
	public HasDataProviderDataBindingProcessor(String bindingContextVariable, String collectionObjectReference, 
												String collectionDataObject, 
												String itemVar)
    {
		this.collectionDataObjectVariable = ViewFactoryCreator.createVariableName("value");
		this.bindingContextVariable = bindingContextVariable;
		this.collectionObjectReference = collectionObjectReference;
		this.collectionDataObject = collectionDataObject;
		this.itemVar = itemVar;
    }
	
	@Override
    public void processBindings(SourcePrinter out, WidgetCreatorContext context)
    {
		processDataObjectBindings(out, context);
		processBindingExpressions(out, context);
    }
	
	public String getCollectionObjectReference()
	{
		return collectionObjectReference;
	}

	public Set<String> getConverterDeclarations()
	{
		Set<String> result = new HashSet<String>();
		result.addAll(converterDeclarations);
		return result;
	}
	
	private void processBindingExpressions(SourcePrinter out, WidgetCreatorContext context)
    {
	    Iterator<ExpressionDataBinding> expressionBindings = context.iterateExpressionBindings();
		
		try
		{
			while (expressionBindings.hasNext())
			{
				ExpressionDataBinding expressionBinding = expressionBindings.next();
				out.println(expressionBinding.getWriteExpression(bindingContextVariable, context.getWidget(), 
							collectionObjectReference, itemVar));
				converterDeclarations.addAll(expressionBinding.getConverterDeclarations());
			}
		}
		catch(NoSuchFieldException e)
		{
			throw new CruxGeneratorException("Error processing data binding expression.", e);
		}
    }

	private void processDataObjectBindings(SourcePrinter out, WidgetCreatorContext context)
    {
		Iterator<String> dataObjects = context.iterateObjectDataBindingObjects();
		
		while (dataObjects.hasNext())
		{
			String dataObjectAlias = dataObjects.next();
			ObjectDataBinding dataBindingInfo = context.getObjectDataBinding(dataObjectAlias);
			Iterator<PropertyBindInfo> propertyBindings = dataBindingInfo.iterateBindings();
			
			while (propertyBindings.hasNext())
			{
				PropertyBindInfo bind = propertyBindings.next(); 
				
				if (bind.getConverterClassName() != null && bind.getConverterClassName().length() > 0 && 
					!converterClasses.contains(bind.getConverterClassName()))
				{
					String converterDeclaration = bind.getConverterDeclaration();
					if (converterDeclaration != null)
					{
						converterClasses.add(bind.getConverterClassName());
						converterDeclarations.add(converterDeclaration);
					}
				}

				out.println(bind.getWriteExpression(bindingContextVariable, context.getWidget()));
			}
		}
    }

	@Override
    public String getDataObjectAlias(String dataObject)
    {
		if (dataObject != null && dataObject.equals(itemVar))
		{
			return collectionDataObject;
		}
	    return dataObject;
    }
	
	public String getCollectionDataObjectVariable()
	{
		return collectionDataObjectVariable;
	}
	
	public String getCollectionItemVariable()
	{
		return itemVar;
	}
}
