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
package org.cruxframework.crux.gwt.rebind;

import java.util.Comparator;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ListDataProvider;

class CellTableContext extends WidgetCreatorContext
{
	String rowDataObject;
	JClassType rowDataObjectType;
	boolean asyncDataProvider;
	String dataProvider = null;;
	String header;
	String footer;
	String colDataObject;
	String column;
	JType colDataObjectType;
	String columnExpression;
	
	void clearColumnInformation()
	{
		header = null;
		footer = null;
		colDataObject = null;
		colDataObjectType = null;
		columnExpression = null;
		column = null;
	}
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="cellTable", library="gwt", targetWidget=CellTable.class)
@TagAttributes({
	@TagAttribute(value="tableLayoutFixed", type=Boolean.class) //TODO RowStyles??
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataProviderFactoryMethod", required=true),
	@TagAttributeDeclaration(value="autoLoad", type=Boolean.class)
})
@TagChildren({
	@TagChild(value=CellTableFactory.ColumnsProcessor.class)
})
public class CellTableFactory extends AbstractHasDataFactory<CellTableContext>
{
	private static final int DEFAULT_PAGE_SIZE = 15;

	@Override
	public void processAttributes(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
	{
	    super.processAttributes(out, context);
	    context.rowDataObject = getDataObject(context.getChildElement());
	    context.rowDataObjectType = getContext().getTypeOracle().findType(context.rowDataObject);
	    
	    String dataProviderFactoryMethod = context.readChildProperty("dataProviderFactoryMethod");
		if (!StringUtils.isEmpty(dataProviderFactoryMethod))
		{
			processDataProvider(out, context, dataProviderFactoryMethod);
		}
	}

	private void processDataProvider(SourcePrinter out, CellTableContext context, String dataProviderFactoryMethod)
    {
	    context.dataProvider = createVariableName("dataProvider");
	    
	    out.print(AbstractDataProvider.class.getCanonicalName()+"<"+context.rowDataObject+"> " + context.dataProvider + " = ");
	    EvtProcessor.printEvtCall(out, dataProviderFactoryMethod, "loadDataProvider", (String)null, null, this);
	    out.println(context.dataProvider+".addDataDisplay("+context.getWidget()+");");

    	Event event = EventFactory.getEvent("loadDataProvider", dataProviderFactoryMethod);
    	String controller = ClientControllers.getController(event.getController(), getDevice());
    	JClassType controllerClass = getContext().getTypeOracle().findType(controller);
	    JMethod loadDataProviderMethod = JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{});
	    if (loadDataProviderMethod == null)
	    {
	    	throw new CruxGeneratorException("DataProvider factory method not found: Controller["+event.getController()+"], Method["+event.getMethod()+"].");
	    }
	    JType returnType = loadDataProviderMethod.getReturnType();
	    if (returnType instanceof JClassType)
	    {
	    	context.asyncDataProvider = 
	    		((JClassType)returnType).isAssignableTo(getContext().getTypeOracle().findType(AsyncDataProvider.class.getCanonicalName()));
	    }
	    else
	    {
	    	context.asyncDataProvider = false;
	    }
    }

	@Override
	public void postProcess(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
	{
		String autoLoadProperty = context.readWidgetProperty("autoLoad");
		if (!StringUtils.isEmpty(autoLoadProperty))
		{
			boolean autoLoad = Boolean.parseBoolean(autoLoadProperty);
			if (autoLoad)
			{
				String pageSizeProperty = context.readWidgetProperty("pageSize");
				int pageSize;
				if (StringUtils.isEmpty(pageSizeProperty))
				{
					pageSize = DEFAULT_PAGE_SIZE;
				}
				else
				{
					pageSize = Integer.parseInt(pageSizeProperty);
				}
				out.println(context.getWidget()+".setPageSize("+pageSize+");");
			}
		}
	}
	
	@TagConstraints(tagName="column", minOccurs="1", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="sortable", type=Boolean.class),
		@TagAttributeDeclaration(value="property", required=true),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class),
		@TagAttributeDeclaration("fieldUpdaterFactoryMethod"),
		@TagAttributeDeclaration("width")
	})
	@TagChildren({
		@TagChild(ColumnHeaderProcessor.class),
		@TagChild(ColumnCellProcessor.class),
		@TagChild(ColumnFooterProcessor.class)
	})
	public static class ColumnsProcessor extends WidgetChildProcessor<CellTableContext> implements HasPostProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String property = context.readChildProperty("property");
			StringBuilder getValueExpression = new StringBuilder();			
			try
            {
				context.colDataObjectType = JClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, 
						property, "object", true);
				context.colDataObject = context.colDataObjectType.getParameterizedQualifiedSourceName();
				context.columnExpression = getValueExpression.toString();
            }
            catch (NoSuchFieldException e)
            {
            	throw new CruxGeneratorException("Can not access property ["+property+"] on row object["+context.rowDataObject+"].");
            }
		}
		
		public void postProcessChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
        {
			String columnWidth = context.readChildProperty("width");
		    if (!StringUtils.isEmpty(columnWidth))
		    {
		    	out.println(context.getWidget()+".setColumnWidth("+context.column+", "+EscapeUtils.quote(columnWidth)+");");
		    }
			String columnsortable = context.readChildProperty("sortable");
		    if (!StringUtils.isEmpty(columnsortable))
		    {
		    	if (Boolean.parseBoolean(columnsortable))
		    	{
		    		setColumnSortable(out, context);
		    	}
		    }
			String columnHorizontalAlignment = context.readChildProperty("horizontalAlignment");
			if (!StringUtils.isEmpty(columnHorizontalAlignment))
			{
				out.println(context.column+".setHorizontalAlignment("+
					  AlignmentAttributeParser.getHorizontalAlignment(columnHorizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			String columnVerticalAlignment = context.readChildProperty("verticalAlignment");
			if (!StringUtils.isEmpty(columnVerticalAlignment))
			{
				out.println(context.column+".setVerticalAlignment("+AlignmentAttributeParser.getVerticalAlignment(columnVerticalAlignment)+");");
			}
			String columnFieldUpdaterFactoryMethod = context.readChildProperty("fieldUpdaterFactoryMethod");
			if (!StringUtils.isEmpty(columnFieldUpdaterFactoryMethod))
			{
				String updater = getWidgetCreator().createVariableName("updater");
				
				out.print(FieldUpdater.class.getCanonicalName()+"<"+context.rowDataObject+","+
						context.colDataObject+"> "+ updater + " = ("+FieldUpdater.class.getCanonicalName()+"<"+context.rowDataObject+","+
						context.colDataObject+">)");
				
				EvtProcessor.printEvtCall(out, columnFieldUpdaterFactoryMethod, "loadFieldUpdater", (String)null, null, getWidgetCreator());
				out.println(context.column+".setFieldUpdater("+updater+");");
			}
			
		    generateAddColumnMethod(out, context);
			
			context.clearColumnInformation();
        }

		private void generateAddColumnMethod(SourcePrinter out, CellTableContext context)
        {
	        if (context.header != null)
			{
				if (context.footer != null)
				{
					out.println(context.getWidget()+".addColumn("+context.column+","+context.header+","+context.footer+");");
				}
				else 
				{
					out.println(context.getWidget()+".addColumn("+context.column+","+context.header+",("+Header.class.getCanonicalName()+")null);");
				}
			}
			else
			{
				if (context.footer != null)
				{
					out.println(context.getWidget()+".addColumn("+context.column+",("+Header.class.getCanonicalName()+")null,"+context.footer+");");
				}
				else
				{
					out.println(context.getWidget()+".addColumn("+context.column+");");
				}
			}
        }

		private void setColumnSortable(SourcePrinter out, CellTableContext context)
		{
			if (context.asyncDataProvider)
			{
				out.println(context.getWidget()+".addColumnSortHandler(new "+AsyncHandler.class.getCanonicalName()+"("+context.getWidget()+"));");
			}
			else
			{
				JClassType colType = context.colDataObjectType.isClassOrInterface();

				String columnSortHandler = getWidgetCreator().createVariableName("sortHandler");
				String listHandlerClassName = ListHandler.class.getCanonicalName()+"<"+context.rowDataObject+">";
				out.println(listHandlerClassName+" "+columnSortHandler+" = new "+listHandlerClassName+"((("+
						ListDataProvider.class.getCanonicalName()+"<"+context.rowDataObject+">)"+context.dataProvider+").getList());");
				out.println(columnSortHandler+".setComparator("+context.column+", new "+Comparator.class.getCanonicalName()+"<"+context.rowDataObject+">() {");
				out.println("public int compare("+context.rowDataObject+" o1, "+context.rowDataObject+" o2) {");
				out.println("if (o1 == o2) {");
				out.println("return 0;");
				out.println("}");
				out.println("if (o1 != null) {");
				out.println("if (o2 != null){");
				String property = context.readChildProperty("property");
				try
				{
					if (colType != null && colType.isAssignableTo(getWidgetCreator().getContext().getTypeOracle().findType(Comparable.class.getCanonicalName())))
					{
						StringBuilder getValueExpression = new StringBuilder();			
						JClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, property, "o1", true);
						out.println(Comparable.class.getCanonicalName()+" c1 = "+getValueExpression.toString());
						getValueExpression = new StringBuilder();			
						JClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, property, "o2", true);
						out.println(Comparable.class.getCanonicalName()+" c2 = "+getValueExpression.toString());
						out.println("if (c1 == c2) {");
						out.println("return 0;");
						out.println("}");
						out.println("if (c1 != null) {");
						out.println("return (c2 != null) ? c1.compareTo(c2) : 1;");
						out.println("}");
						out.println("return -1;");
					}
					else
					{
						JPrimitiveType primitive = context.colDataObjectType.isPrimitive();
						if (primitive != null)
						{
							StringBuilder getValueExpression = new StringBuilder();			
							JClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, property, "o1", true);
							out.println(primitive.getSimpleSourceName()+" c1 = "+getValueExpression.toString());
							getValueExpression = new StringBuilder();			
							JClassUtils.buildGetValueExpression(getValueExpression, context.rowDataObjectType, property, "o2", true);
							out.println(primitive.getSimpleSourceName()+" c2 = "+getValueExpression.toString());
							out.println("return (c1==c2) ? 0 : (c1<c2) ? -1 : 1;");
						}
						else
						{
							throw new CruxGeneratorException("Can not sort column for property ["+property+"] on row object["+context.rowDataObject+"]. Property must have a primitive or Comparable type.");
						}
					}
				}
				catch (NoSuchFieldException e)
				{
	            	throw new CruxGeneratorException("Can not access property ["+property+"] on row object["+context.rowDataObject+"].");
				}
				out.println("}");
				out.println("return 1;");
				out.println("}");
				out.println("return -1;");
				out.println("}");
				out.println("});");
				out.println(context.getWidget()+".addColumnSortHandler("+columnSortHandler+");");	        	
			}
			out.println(context.getWidget()+".getColumnSortList().push("+context.column+");");
			out.println(context.column+".setSortable(true);");
        }
	}
	
	@TagConstraints(tagName="header", minOccurs="0")
	@TagChildren({
		@TagChild(ColumnHeaderChoiceProcessor.class)
	})
	public static class ColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext> {}

	@TagChildren({
		@TagChild(TextColumnHeaderProcessor.class),
		@TagChild(HTMLColumnHeaderProcessor.class),
		@TagChild(CustomColumnHeaderProcessor.class)
	})
	public static class ColumnHeaderChoiceProcessor extends ChoiceChildProcessor<CellTableContext> {}
	
	@TagConstraints(tagName="text", type=String.class)
	public static class TextColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(),true, context.getWidgetId(), false);
			context.header = "new "+TextHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}

	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(),true, context.getWidgetId(), false);
			context.header = "new "+SafeHtmlHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}
	
	@TagConstraints(tagName="custom")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="factoryMethod", required=true)
	})
	public static class CustomColumnHeaderProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String factoryMethod = context.getChildElement().optString("factoryMethod");
		    assert (!StringUtils.isEmpty(factoryMethod));
			String header = getWidgetCreator().createVariableName("header");
			out.print(Header.class.getCanonicalName()+"<"+context.colDataObject+">"+" "+header+
					"=("+Header.class.getCanonicalName()+"<"+context.colDataObject+">)");
		    EvtProcessor.printEvtCall(out, factoryMethod, "loadHeader", (String)null, null, getWidgetCreator());
		    context.header = header;
		}
	}

	@TagConstraints(tagName="footer", minOccurs="0", maxOccurs="1")
	@TagChildren({
		@TagChild(ColumnFooterChoiceProcessor.class)
	})
	public static class ColumnFooterProcessor extends WidgetChildProcessor<CellTableContext> {}	

	@TagChildren({
		@TagChild(TextColumnFooterProcessor.class),
		@TagChild(HTMLColumnFooterProcessor.class),
		@TagChild(CustomColumnFooterProcessor.class)
	})
	public static class ColumnFooterChoiceProcessor extends ChoiceChildProcessor<CellTableContext> {}
	
	@TagConstraints(tagName="text", type=String.class)
	public static class TextColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(),true, context.getWidgetId(), false);
			context.footer = "new "+TextHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}
	}

	@TagConstraints(tagName="html", type=HTMLTag.class)
	public static class HTMLColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String innerText = getWidgetCreator().ensureTextChild(context.getChildElement(),true, context.getWidgetId(), false);
			context.footer = "new "+SafeHtmlHeader.class.getCanonicalName()+"("+getWidgetCreator().getDeclaredMessage(innerText)+")";
		}		
	}

	@TagConstraints(tagName="custom")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="factoryMethod", required=true)
	})
	public static class CustomColumnFooterProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
		    String factoryMethod = context.getChildElement().optString("factoryMethod");
		    assert (!StringUtils.isEmpty(factoryMethod));
			String footer = getWidgetCreator().createVariableName("footer");
			out.print(Header.class.getCanonicalName()+"<"+context.colDataObject+">"+" "+footer+
					"=("+Header.class.getCanonicalName()+"<"+context.colDataObject+">)");
		    EvtProcessor.printEvtCall(out, factoryMethod, "loadFooter", (String)null, null, getWidgetCreator());
		    context.footer = footer;
		}
	}
	
	@TagConstraints(tagName="cell")
	@TagChildren({
		@TagChild(value=CellListChildProcessor.class, autoProcess=false)
	})
	public static class ColumnCellProcessor extends WidgetChildProcessor<CellTableContext>
	{
		@Override
		public void processChildren(SourcePrinter out, CellTableContext context) throws CruxGeneratorException
		{
			String cell = ((CellTableFactory)getWidgetCreator()).getCell(out, context.getChildElement(), context.getWidgetId());
			String column = getWidgetCreator().createVariableName("column");
			
			String colDataObject = context.colDataObject;
			if (context.colDataObjectType.isPrimitive() != null)
			{
				colDataObject = context.colDataObjectType.isPrimitive().getQualifiedBoxedSourceName();
			}
			
			out.println(Column.class.getCanonicalName()+"<"+context.rowDataObject+","+colDataObject+">"+" "+column+
					"=new "+Column.class.getCanonicalName()+"<"+context.rowDataObject+","+colDataObject+">("+cell+"){");
			out.println("public "+colDataObject+" getValue("+context.rowDataObject+" object){");
			out.println("return "+context.columnExpression);
			out.println("}");
			out.println("};");
			context.column = column;
		}
	}

	@Override
    public CellTableContext instantiateContext()
    {
	    return new CellTableContext();
    }
}

