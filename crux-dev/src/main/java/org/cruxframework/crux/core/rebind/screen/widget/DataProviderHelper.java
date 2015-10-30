/*
 * Copyright 2015 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget;

import org.cruxframework.crux.core.client.bean.BeanCopier;
import org.cruxframework.crux.core.client.dataprovider.EagerDataLoader;
import org.cruxframework.crux.core.client.dataprovider.EagerLoadEvent;
import org.cruxframework.crux.core.client.dataprovider.EagerPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.FetchDataEvent;
import org.cruxframework.crux.core.client.dataprovider.LazyDataLoader;
import org.cruxframework.crux.core.client.dataprovider.LazyDataProvider;
import org.cruxframework.crux.core.client.dataprovider.MeasureDataEvent;
import org.cruxframework.crux.core.client.dataprovider.StreamingDataLoader;
import org.cruxframework.crux.core.client.dataprovider.StreamingDataProvider;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.DataProvider;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class DataProviderHelper
{
	private ViewFactoryCreator viewFactory;
	
	public DataProviderHelper(ViewFactoryCreator viewFactory)
    {
		this.viewFactory = viewFactory;
    }
	
	public String createDataProvider(SourcePrinter out, DataProvider dataProvider)
    {
		switch (dataProvider.getType())
        {
			case dataProvider: return createEagerPagedDataProvider(out, dataProvider);
			case lazyDataProvider: return createLazyDataProvider(out, dataProvider);
			case streamingDataProvider: return createStreamingDataProvider(out, dataProvider);
		}
		
		return null;
    }

	public JClassType getDataObjectFromProvider(String dataProviderId)
	{
		DataProvider dataProvider = viewFactory.getView().getDataProvider(dataProviderId);
		
		if(dataProvider == null)
		{
			throw new CruxGeneratorException("DataProvider ["+dataProviderId+"] on view ["+viewFactory.getView().getId()+"] must inform the dataObject to bind.");	
		}
		
		return getDataObject(dataProviderId, dataProvider.getMetadata());
	}
	
	protected String createEagerPagedDataProvider(SourcePrinter out, DataProvider dataProvider)
	{
		JSONObject metadata = dataProvider.getMetadata();
		String onLoadData = metadata.optString("onLoadData");

		JClassType dataObjectType = getDataObject(dataProvider.getId(), metadata);
		String dataObjectClassName = dataObjectType.getParameterizedQualifiedSourceName();
		String dataProviderVariable = ViewFactoryCreator.createVariableName("dataProvider");
		String dataLoaderClassName = EagerDataLoader.class.getCanonicalName();
		String dataEventClassName =  EagerLoadEvent.class.getCanonicalName();
		String dataProviderClassName = EagerPagedDataProvider.class.getCanonicalName();
		
		out.print(dataProviderClassName+"<"+dataObjectClassName+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObjectClassName+">(");
		createDataHandlerObject(out, dataObjectType);
		out.println(");");
		out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObjectClassName+">(){");
		out.println("public void onLoadData("+dataEventClassName+"<"+dataObjectClassName+"> event){");

		EvtProcessor.printEvtCall(out, onLoadData, "onLoadData", dataEventClassName, "event", viewFactory.getContext(),
			viewFactory.getView(), viewFactory.getControllerAccessHandler(), getDevice(), true);
		
		out.println("}");
		out.println("});");

		processCommonProperties(out, metadata, dataProviderVariable);
		return dataProviderVariable;
	}

	protected String createLazyDataProvider(SourcePrinter out, DataProvider dataProvider)
	{
		JSONObject metadata = dataProvider.getMetadata();
		String onMeasureData = metadata.optString("onMeasureData");
		String onFetchData = metadata.optString("onFetchData");

		JClassType dataObjectType = getDataObject(dataProvider.getId(), metadata);
		String dataObjectClassName = dataObjectType.getParameterizedQualifiedSourceName();
		String dataProviderVariable = ViewFactoryCreator.createVariableName("dataProvider");
		String dataProviderClassName = LazyDataProvider.class.getCanonicalName();
		String dataLoaderClassName = LazyDataLoader.class.getCanonicalName();
		String dataEventClassName =  FetchDataEvent.class.getCanonicalName();
		String dataMeasureEventClassName = MeasureDataEvent.class.getCanonicalName();
		
		out.println(dataProviderClassName+"<"+dataObjectClassName+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObjectClassName+">(");
		createDataHandlerObject(out, dataObjectType);
		out.println(");");

		out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObjectClassName+">(){");
		out.println("public void onMeasureData("+dataMeasureEventClassName+"<"+dataObjectClassName+"> event){");

		EvtProcessor.printEvtCall(out, onMeasureData, "onMeasureData", dataMeasureEventClassName, "event", viewFactory.getContext(),
			viewFactory.getView(), viewFactory.getControllerAccessHandler(), getDevice(), true);
		
		out.println("}");

		out.println("public void onFetchData("+dataEventClassName+"<"+dataObjectClassName+"> event){");

		EvtProcessor.printEvtCall(out, onFetchData, "onFetchData", dataEventClassName, "event", viewFactory.getContext(),
			viewFactory.getView(), viewFactory.getControllerAccessHandler(), getDevice(), true);
		
		out.println("}");
		out.println("});");

		processCommonProperties(out, metadata, dataProviderVariable);
		return dataProviderVariable;
	}
	
	protected String createStreamingDataProvider(SourcePrinter out, DataProvider dataProvider)
	{
		JSONObject metadata = dataProvider.getMetadata();
		String onFetchData = metadata.optString("onFetchData");

		JClassType dataObjectType = getDataObject(dataProvider.getId(), metadata);
		String dataObjectClassName = dataObjectType.getParameterizedQualifiedSourceName();
		String dataProviderVariable = ViewFactoryCreator.createVariableName("dataProvider");
		String dataProviderClassName = StreamingDataProvider.class.getCanonicalName();
		String dataLoaderClassName = StreamingDataLoader.class.getCanonicalName();
		String dataEventClassName =  FetchDataEvent.class.getCanonicalName();
		
		out.println(dataProviderClassName+"<"+dataObjectClassName+"> " + dataProviderVariable+" = new "+dataProviderClassName+"<"+dataObjectClassName+">(");
		createDataHandlerObject(out, dataObjectType);
		out.println(");");
		out.println(dataProviderVariable+".setDataLoader(new "+dataLoaderClassName+"<"+dataObjectClassName+">(){");
		out.println("public void onFetchData("+dataEventClassName+"<"+dataObjectClassName+"> event){");

		EvtProcessor.printEvtCall(out, onFetchData, "onFetchData", dataEventClassName, "event", viewFactory.getContext(),
			viewFactory.getView(), viewFactory.getControllerAccessHandler(), getDevice(), true);
		
		out.println("}");
		out.println("});");

		processCommonProperties(out, metadata, dataProviderVariable);
		return dataProviderVariable;
	}
	
	private void createDataHandlerObject(SourcePrinter out, JClassType dataObjectType)
	{
		String dataObject = dataObjectType.getParameterizedQualifiedSourceName();
		String copierSimpleClassName = dataObjectType.getSimpleSourceName() + "_Data_Copier";
		String packageName = dataObjectType.getPackage().getName();
		SourcePrinter subTypeWriter = viewFactory.getSubTypeWriter(packageName,
			copierSimpleClassName, null, new String[]{BeanCopier.class.getCanonicalName()+"<"+dataObject+", "+dataObject+">"}, null, true);
		// TODO tentar ler do cache primeiro (tryReuse....)
		if (subTypeWriter != null)
		{
			subTypeWriter.commit();
		}
		String copierClassName = packageName + "." + copierSimpleClassName;
		out.println("new " + org.cruxframework.crux.core.client.dataprovider.DataProvider.EditionDataHandler.class.getCanonicalName() + "<" + dataObject + ">(){");
		out.println(copierClassName + " copier = GWT.create(" + copierClassName + ".class);");
		out.println("public " + dataObject + " clone(" + dataObject + " value){");
		out.println(dataObject + " result = new " + dataObject + "();");
		out.println("copier.copyTo(value, result);");
		out.println("return result;");
		out.println("}");
		out.println("}");
	}
	
	private JClassType getDataObject(String dataProviderId, JSONObject elem)
    {
	    String dataObject = elem.optString("dataObject");
	    
	    if (StringUtils.isEmpty(dataObject))
	    {
	    	throw new CruxGeneratorException("DataProvider ["+dataProviderId+"] on view ["+viewFactory.getView().getId()+"] must inform the dataObject to bind.");
	    }
	    
	    String dataObjectClass = viewFactory.getContext().getDataObjects().getDataObject(dataObject);
	    if (StringUtils.isEmpty(dataObjectClass))
	    {
	    	throw new CruxGeneratorException("DataProvider ["+dataProviderId+"] on view ["+viewFactory.getView().getId()+"] informed an invalid "
	    		+ "dataObject ["+dataObject+"]. Can not found the informed value");
	    }
	    JClassType dtoType = viewFactory.getContext().getGeneratorContext().getTypeOracle().findType(dataObjectClass);
	    return dtoType;
    }
	
	private Device getDevice()
	{
		return (viewFactory.getDevice() == null?null:Device.valueOf(viewFactory.getDevice()));
	}
	
	private void processCommonProperties(SourcePrinter out, JSONObject metadata, String dataProviderVariable)
    {
	    int pageSize = metadata.optInt("pageSize", -1);
		if (pageSize > 0)
		{
			out.println(dataProviderVariable+".setPageSize(" + pageSize + ");");
		}
		
		boolean autoLoadData = metadata.optBoolean("autoLoadData", false);
		if (autoLoadData)
		{
			out.println(dataProviderVariable+".load();");
		}
    }
}
