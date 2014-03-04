package org.cruxframework.crux.cruxfaces.rebind.list;

import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.cruxfaces.client.list.ScrollableList;
import org.cruxframework.crux.cruxfaces.rebind.panel.Constants;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

@DeclarativeFactory(id="scollableList", library=Constants.LIBRARY_NAME, targetWidget=ScrollableList.class, 
					description="A list of widgets, that use a datasource to provide data and a renderer to bound the data to a widget. This list searches the datasource automatically when the user scrolls down the list.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="renderer", required=true, description="The renderer identification. Use the @ScrollableList.Renderer annotation on the renderer class to specify a renderer.")
})
@TagAttributes({
	@TagAttribute(value="pageSize", type=Integer.class, description="The number of widgets that is loaded from the datasource on each data request."),
	@TagAttribute(value="autoLoadData", type=Boolean.class, description="If true, ask bound datasource for data when widget is created."),
	@TagAttribute(value="dataSource", processor=ListFactory.DataSourceAttributeParser.class, required=true, description="The datasource that provides data for this widget.")
})
public class ListFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName()+"<"+getDataObject(context)+">";
	    String rendererName = context.readWidgetProperty("renderer");
	    assert (!StringUtils.isEmpty(rendererName));
	    String rendererClass = Renderers.getRenderer(rendererName);
	    if (StringUtils.isEmpty(rendererClass))
	    {
	    	throw new CruxGeneratorException("Can not found the renderer class bound to ["+rendererName+"]");
	    }
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"(new "+rendererClass+"());");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	/**
	 * @param context
	 * @return
	 */
	protected String getDataObject(WidgetCreatorContext context)
    {
		JClassType dataSourceClass = getContext().getTypeOracle().findType(DataSources.getDataSource(context.readWidgetProperty("dataSource"), getDevice()));
		JClassType dtoType = JClassUtils.getReturnTypeFromMethodClass(dataSourceClass, "getBoundObject", new JType[]{}).isClassOrInterface();
		
		return dtoType.getQualifiedSourceName();
    }
	
	public static class DataSourceAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public DataSourceAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
		{
			JClassType dataSourceClass = getWidgetCreator().getContext().getTypeOracle().findType(DataSources.getDataSource(propertyValue, getWidgetCreator().getDevice()));
			JClassType dtoType = JClassUtils.getReturnTypeFromMethodClass(dataSourceClass, "getBoundObject", new JType[]{}).isClassOrInterface();
			org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions columnDefinitionsAnot = 
				dataSourceClass.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions.class);

			String colDefs = getWidgetCreator().createVariableName("colDefs");
			String dtoClassName = dtoType.getParameterizedQualifiedSourceName();
			String className = PagedDataSource.class.getCanonicalName()+"<"+dtoClassName+">";
			String dataSource = getWidgetCreator().createVariableName("dataSource");
			String columnDefinitionsClassName = org.cruxframework.crux.core.client.datasource.ColumnDefinitions.class.getCanonicalName()+"<"+dtoClassName+">";
			out.println(className+" "+dataSource+" = ("+className+") "+getViewVariable()+".createDataSource("+EscapeUtils.quote(propertyValue)+");");

			if (columnDefinitionsAnot != null)
			{
				out.println(columnDefinitionsClassName+" "+colDefs+" = "+dataSource+".getColumnDefinitions();");
			}
			else
			{
				out.println(columnDefinitionsClassName+" "+colDefs+" = new "+columnDefinitionsClassName+"();");
				out.println(dataSource+".setColumnDefinitions("+colDefs+");");
			}
			
			String widget = context.getWidget();			
			out.println(widget+".setDataSource("+dataSource+");");
		}
	}	
}