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

import org.cruxframework.crux.core.client.dataprovider.AsyncDataProviderEvent;
import org.cruxframework.crux.core.client.dataprovider.AsyncPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.AsyncStreamingDataProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasurableAsyncDataProviderEvent;
import org.cruxframework.crux.core.client.dataprovider.SynchronousDataProviderEvent;
import org.cruxframework.crux.core.client.dataprovider.SyncPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.SyncScrollableDataProvider;
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

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * A helper class to help on HasData widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataObject", required=true, description="Identify the object type provided by the DataProvider. Use the annotation @DataObject to create an identifier to your types."),
	@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, description="If true, ask bound DataProvider for data when widget is created.", defaultValue="false")
})
@TagChildren({
	@TagChild(HasDataProviderFactory.DataProviderChildProcessor.class)
})
public abstract class HasDataProviderFactory<C extends WidgetCreatorContext> extends WidgetCreator<C>
{
	/**
	 * @param context
	 * @return
	 */
	protected JClassType getDataObject(WidgetCreatorContext context)
    {
		String dataObject = context.readWidgetProperty("dataObject");
		if (StringUtils.isEmpty(dataObject))
		{
			throw new CruxGeneratorException("Widget ["+context.getWidgetId()+"] on view ["+getView().getId()+"] must inform the dataObject to bind into the declared DataProvider");
		}
		
		String dataObjectClass = DataObjects.getDataObject(dataObject);
		if (StringUtils.isEmpty(dataObjectClass))
		{
			throw new CruxGeneratorException("Widget ["+context.getWidgetId()+"] on view ["+getView().getId()+"] informed an invalid dataObject. Can not found the informed value");
		}
		JClassType dtoType = getContext().getTypeOracle().findType(dataObjectClass);
		return dtoType;
    }
	
	@TagConstraints(tagName="dataProvider", description="Specify a data provider for this widget")
	@TagChildren({
		@TagChild(DataProviderChildren.class)
	})
	public static class DataProviderChildProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	/////////////////////////////////////////////////////////////////////////

	@TagChildren({
		@TagChild(SynchronousScrollableProcessor.class),
		@TagChild(SynchronousPagedProcessor.class),
		@TagChild(AsynchronousMeasurableProcessor.class),
		@TagChild(AsynchronousStreamingProcessor.class)
	})
	public static class DataProviderChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

	/////////////////////////////////////////////////////////////////////////
	@TagConstraints(tagName="sync", description="Define a synchronous non paged data provider. It is like a database result set, with a scrollable interface")
	@TagChildren({
//		@TagChild(SynchronousScrollableProcessorChildren.class)
		@TagChild(EventsSynchronousScrollableProcessor.class)
	})
	public static class SynchronousScrollableProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="syncPaged", description="Define a synchronous paged data provider.")
	@TagChildren({
//		@TagChild(SynchronousPagedProcessorChildren.class)
		@TagChild(EventsSynchronousPagedProcessor.class)
	})
	public static class SynchronousPagedProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="async", description="Define an asynchronous paged data provider. That dataProvider must be measurable.")
	@TagChildren({
//		@TagChild(AsynchronousMeasurableProcessorChildren.class)
		@TagChild(EventsAsynchronousPagedProcessor.class)
	})
	public static class AsynchronousMeasurableProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="asyncStreaming", description="Define an asynchronous paged data provider that is not measurable. This kind of data provider will only know the size of the data set when it has requested for more data and no data is available.")
	@TagChildren({
//		@TagChild(AsynchronousStreamingProcessorChildren.class)
		@TagChild(EventsAsynchronousStreamingProcessor.class)
	})
	public static class AsynchronousStreamingProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	/////////////////////////////////////////////////////////////////////////
	@TagChildren({
		@TagChild(EventsSynchronousScrollableProcessor.class)
	})
	public static class SynchronousScrollableProcessorChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

	@TagChildren({
		@TagChild(EventsSynchronousPagedProcessor.class)
	})
	public static class SynchronousPagedProcessorChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}
	
	@TagChildren({
		@TagChild(EventsAsynchronousPagedProcessor.class)
	})
	public static class AsynchronousMeasurableProcessorChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}
	
	@TagChildren({
		@TagChild(EventsAsynchronousStreamingProcessor.class)
	})
	public static class AsynchronousStreamingProcessorChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

	/////////////////////////////////////////////////////////////////////////
	@TagConstraints(tagName="event", description="Inform the events on controller that will be used to provide informations for this data provider.")
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onLoad", required=true, description="Event called to load the data set into this dataprovider.")
	})
	public static abstract class EventsSynchronousProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		public void processChildren(SourcePrinter out, WidgetCreatorContext context, String dataProviderClassName) throws CruxGeneratorException
		{
			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(context).getParameterizedQualifiedSourceName();
			out.println(context.getWidget()+".setDataProvider(new "+dataProviderClassName+"<"+dataObject+">(){");
			out.println("public void load(){");
			
			String eventClassName = SynchronousDataProviderEvent.class.getCanonicalName();
			out.println(eventClassName+"<"+dataObject+"> event = createSynchronousDataProviderEvent();");
			String onLoad = context.readChildProperty("onLoad");
	    	EvtProcessor.printEvtCall(out, onLoad, "onLoad", eventClassName, "event", getWidgetCreator(), false);
			
			out.println("}");
			out.println("}, "+context.readBooleanWidgetProperty("autoLoadData", false)+");");
		}
	}

	public static class EventsSynchronousScrollableProcessor extends EventsSynchronousProcessor 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			processChildren(out, context, SyncScrollableDataProvider.class.getCanonicalName());
		}
	}

	public static class EventsSynchronousPagedProcessor extends EventsSynchronousProcessor 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			processChildren(out, context, SyncPagedDataProvider.class.getCanonicalName());
		}
	}
	
	@TagConstraints(tagName="event", description="Inform the events on controller that will be used to provide informations for this data provider.")
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onPageFetch", required=true, description="Event called every time a new page with data must be fetched by the data provider.")
	})
	public static class EventsAsynchronousStreamingProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(context).getParameterizedQualifiedSourceName();
			String dataProviderClassName = AsyncStreamingDataProvider.class.getCanonicalName();
			out.println(context.getWidget()+".setDataProvider(new "+dataProviderClassName+"<"+dataObject+">(){");
			out.println("public void fetch(int startRecord, int endRecord){");
			
			String eventClassName = AsyncDataProviderEvent.class.getCanonicalName();
			out.println(eventClassName+"<"+dataObject+"> event = createAsynchronousDataProviderEvent(startRecord, endRecord);");
			String onPageFetch = context.readChildProperty("onPageFetch");
	    	EvtProcessor.printEvtCall(out, onPageFetch, "onPageFetch", eventClassName, "event", getWidgetCreator(), false);
			
			out.println("}");
			out.println("}, "+context.readBooleanWidgetProperty("autoLoadData", false)+");");
		}
	}

	@TagConstraints(tagName="event", description="Inform the events on controller that will be used to provide informations for this data provider.")
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onPageFetch", required=true, description="Event called every time a new page with data must be fetched by the data provider."),
		@TagEventDeclaration(value="onInitialize", required=true, description="Event called when the data provider is initialized. Use this to set the data set size.")
	})
	public static class EventsAsynchronousPagedProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String dataProviderClassName = AsyncPagedDataProvider.class.getCanonicalName();
			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(context).getParameterizedQualifiedSourceName();
			
			out.println(context.getWidget()+".setDataProvider(new "+dataProviderClassName+"<"+dataObject+">(){");
			out.println("public void fetch(int startRecord, int endRecord){");
			
			String eventClassName = AsyncDataProviderEvent.class.getCanonicalName();
			out.println(eventClassName+"<"+dataObject+"> event = createAsynchronousDataProviderEvent(startRecord, endRecord);");
			String onPageFetch = context.readChildProperty("onPageFetch");
	    	EvtProcessor.printEvtCall(out, onPageFetch, "onPageFetch", eventClassName, "event", getWidgetCreator(), false);
			
			out.println("}");

			out.println("public void initialize(){");
			
			eventClassName = MeasurableAsyncDataProviderEvent.class.getCanonicalName();
			out.println(eventClassName+"<"+dataObject+"> event = createMeasurableDataProviderEvent();");
			String onInitialize = context.readChildProperty("onInitialize");
	    	EvtProcessor.printEvtCall(out, onInitialize, "onInitialize", eventClassName, "event", getWidgetCreator(), false);
			
			out.println("}");
			out.println("}, "+context.readBooleanWidgetProperty("autoLoadData", false)+");");
		}
	}

//////////////////////////////////////////////////////////////////////////////
//	
//	@TagConstraints(tagName="rest")
//	@TagEventsDeclaration({
//		@TagEventDeclaration(value="onPageFetch", required=true),
//		@TagEventDeclaration(value="onInitialize", required=true)
//	})
//	public static class RestAsynchronousPagedProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
//	{
//		@Override
//		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
//		{
//			String dataProviderClassName = AsyncStreamingDataProvider.class.getCanonicalName();
//			String dataObject = ((HasDataProviderFactory<?>)getWidgetCreator()).getDataObject(context).getParameterizedQualifiedSourceName();
//			
//			out.println(context.getWidget()+".setDataProvider(new "+dataProviderClassName+"<"+dataObject+">(){");
//			out.println("public void fetch(int startRecord, int endRecord){");
//			
//			String eventClassName = AsyncDataProviderEvent.class.getCanonicalName()+"<"+dataObject+">";
//			out.println(eventClassName+" event = createAsynchronousDataProviderEvent(startRecord, endRecord);");
//			String onPageFetch = context.readChildProperty("onPageFetch");
//	    	EvtProcessor.printEvtCall(out, onPageFetch, "onPageFetch", eventClassName, "event", getWidgetCreator(), false);
//			
//			out.println("}");
//
//			out.println("public void initialize(){");
//			
//			eventClassName = MeasurableAsyncDataProviderEvent.class.getCanonicalName()+"<"+dataObject+">";
//			out.println(eventClassName+" event = createMeasurableDataProviderEvent();");
//			String onInitialize = context.readChildProperty("onInitialize");
//	    	EvtProcessor.printEvtCall(out, onInitialize, "onInitialize", eventClassName, "event", getWidgetCreator(), false);
//			
//			out.println("}");
//			out.println("}, "+context.readBooleanWidgetProperty("autoLoadData", false)+");");
//		}
//	}
	
}

