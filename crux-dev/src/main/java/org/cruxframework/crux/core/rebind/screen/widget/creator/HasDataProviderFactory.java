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

import org.cruxframework.crux.core.client.dataprovider.EagerDataLoader;
import org.cruxframework.crux.core.client.dataprovider.EagerDataProvider;
import org.cruxframework.crux.core.client.dataprovider.EagerLoadEvent;
import org.cruxframework.crux.core.client.dataprovider.EagerPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.FetchDataEvent;
import org.cruxframework.crux.core.client.dataprovider.LazyDataLoader;
import org.cruxframework.crux.core.client.dataprovider.LazyDataProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasureDataEvent;
import org.cruxframework.crux.core.client.dataprovider.StreamingDataLoader;
import org.cruxframework.crux.core.client.dataprovider.StreamingDataProvider;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * A helper class to help on HasData widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagChildren({
	@TagChild(HasDataProviderFactory.DataProviderChildren.class)
})
public abstract class HasDataProviderFactory<C extends WidgetCreatorContext> extends WidgetCreator<C>
{
	/**
	 * @param context
	 * @return
	 */
	protected JClassType getDataObject(String widgetId, JSONObject elem)
    {
		String childName = getChildName(elem);
		
		if (childName.equals("data"))
		{
			return getDataObjectFromCollection(widgetId, elem);
			
		}
		else
		{
			return getDataObjectFromProvider(widgetId, elem);
		}
    }

	protected JClassType getDataObjectFromCollection(String widgetId, JSONObject elem)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	protected JClassType getDataObjectFromProvider(String widgetId, JSONObject elem)
    {
	    String dataObject = elem.optString("dataObject");
	    
	    if (StringUtils.isEmpty(dataObject))
	    {
	    	throw new CruxGeneratorException("Widget ["+widgetId+"] on view ["+getView().getId()+"] must inform the dataObject to bind into the declared DataProvider");
	    }
	    
	    String dataObjectClass = DataObjects.getDataObject(dataObject);
	    if (StringUtils.isEmpty(dataObjectClass))
	    {
	    	throw new CruxGeneratorException("Widget ["+widgetId+"] on view ["+getView().getId()+"] informed an invalid dataObject. Can not found the informed value");
	    }
	    JClassType dtoType = getContext().getTypeOracle().findType(dataObjectClass);
	    return dtoType;
    }
	
	@TagChildren({
		@TagChild(DataProcessor.class),
		@TagChild(ScrollableDataProviderProcessor.class),
		@TagChild(DataProviderProcessor.class),
		@TagChild(LazyDataProviderProcessor.class),
		@TagChild(StreamingDataProviderProcessor.class)
	})
	public static class DataProviderChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="data", description="Inform the collection of data directly")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="collection", required=true, description="the collection of data")
	})
	public static class DataProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	//TODO implementar o acesso direto via data
	
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="dataObject", required=true, description="Identify the object type provided by the DataProvider. "
																	+ "Use the annotation @DataObject to create an identifier to your types."),
		@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, defaultValue="false", 
								description="If true, ask bound DataProvider for data when widget is created.")
	})
	public static abstract class AbstractDataProviderProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagEventsDeclaration({
		@TagEventDeclaration(value="onLoadData", required=true, description="Event called to load the data set into this dataprovider.")
	})
	public static abstract class AbstractEagerDataProviderProcessor extends AbstractDataProviderProcessor 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context, String dataProviderClassName) throws CruxGeneratorException
		{
			String onLoadData = context.readChildProperty("onLoadData");

			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(
																context.getWidgetId(), 
																context.getChildElement()).getParameterizedQualifiedSourceName();
			String dataProviderVariable = getWidgetCreator().createVariableName("dataProvider");
			String dataLoaderClassName = EagerDataLoader.class.getCanonicalName();
			String dataEventClassName =  EagerLoadEvent.class.getCanonicalName();
			
			out.println(dataProviderClassName+"<"+dataObject+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObject+">();");
			out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObject+">(){");
			out.println("public void onLoadData("+dataEventClassName+"<"+dataObject+"> event){");

			EvtProcessor.printEvtCall(out, onLoadData, "onLoadData", dataEventClassName, "event", getWidgetCreator(), true);
			
			out.println("}");
			out.println("});");
			out.println(context.getWidget()+".setDataProvider("+dataProviderVariable+", "+
						context.readBooleanChildProperty("autoLoadData", false)+");");
		}
	}

	@TagConstraints(tagName="scrollableDataProvider", description="Define an eager data provider. You must provide a dataLader "
																+ "function for this provider")
	public static class ScrollableDataProviderProcessor extends AbstractEagerDataProviderProcessor 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String dataProviderClassName = EagerDataProvider.class.getCanonicalName();
			processChildren(out, context, dataProviderClassName);
		}
	}

	@TagConstraints(tagName="dataProvider", description="Define an eager paged data provider. You must provide a dataLader "
													  + "function for this provider")
	public static class DataProviderProcessor extends AbstractEagerDataProviderProcessor 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String dataProviderClassName = EagerPagedDataProvider.class.getCanonicalName();
			processChildren(out, context, dataProviderClassName);
		}
	}

	@TagConstraints(tagName="lazyDataProvider", description="Define a lazy paged data provider. You must provide a lazyDataLader functions for this provider")
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onMeasureData", required=true, description="Event called when the data provider is initialized. Use this to set the data set size."),
		@TagEventDeclaration(value="onFetchData", required=true, description="Event called to load a page of data into this dataprovider.")
	})
	public static class LazyDataProviderProcessor extends AbstractDataProviderProcessor 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String onMeasureData = context.readChildProperty("onMeasureData");
			String onFetchData = context.readChildProperty("onFetchData");

			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(
					context.getWidgetId(), 
					context.getChildElement()).getParameterizedQualifiedSourceName();
			String dataProviderVariable = getWidgetCreator().createVariableName("dataProvider");
			String dataProviderClassName = LazyDataProvider.class.getCanonicalName();
			String dataLoaderClassName = LazyDataLoader.class.getCanonicalName();
			String dataEventClassName =  FetchDataEvent.class.getCanonicalName();
			String dataMeasureEventClassName = MeasureDataEvent.class.getCanonicalName();
			
			out.println(dataProviderClassName+"<"+dataObject+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObject+">();");
			out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObject+">(){");
			out.println("public void onMeasureData("+dataMeasureEventClassName+"<"+dataObject+"> event){");

			EvtProcessor.printEvtCall(out, onMeasureData, "onMeasureData", dataMeasureEventClassName, "event", getWidgetCreator(), true);
			
			out.println("}");

			out.println("public void onFetchData("+dataEventClassName+"<"+dataObject+"> event){");

			EvtProcessor.printEvtCall(out, onFetchData, "onFetchData", dataEventClassName, "event", getWidgetCreator(), true);
			
			out.println("}");
			out.println("});");
			out.println(context.getWidget()+".setDataProvider("+dataProviderVariable+", "+
						context.readBooleanChildProperty("autoLoadData", false)+");");
		}
	}

	@TagConstraints(tagName="streamingDataProvider", description="Define a streaming paged data provider. This kind of data provider will only know the size of the data set when it has requested for more data and no data is available. You must provide a streamingDataLader functions for this provider")
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onFetchData", required=true, description="Event called to load a page of data into this dataprovider.")
	})
	public static class StreamingDataProviderProcessor extends AbstractDataProviderProcessor 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String onFetchData = context.readChildProperty("onFetchData");

			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(
					context.getWidgetId(), 
					context.getChildElement()).getParameterizedQualifiedSourceName();
			String dataProviderVariable = getWidgetCreator().createVariableName("dataProvider");
			String dataProviderClassName = StreamingDataProvider.class.getCanonicalName();
			String dataLoaderClassName = StreamingDataLoader.class.getCanonicalName();
			String dataEventClassName =  FetchDataEvent.class.getCanonicalName();
			
			out.println(dataProviderClassName+"<"+dataObject+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObject+">();");
			out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObject+">(){");
			out.println("public void onFetchData("+dataEventClassName+"<"+dataObject+"> event){");

			EvtProcessor.printEvtCall(out, onFetchData, "onFetchData", dataEventClassName, "event", getWidgetCreator(), true);
			
			out.println("}");
			out.println("});");
			out.println(context.getWidget()+".setDataProvider("+dataProviderVariable+", "+
						context.readBooleanChildProperty("autoLoadData", false)+");");
		}
	}
}

