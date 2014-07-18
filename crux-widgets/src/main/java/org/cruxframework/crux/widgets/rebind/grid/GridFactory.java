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
package org.cruxframework.crux.widgets.rebind.grid;

import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.formatter.Formatters;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinitions;
import org.cruxframework.crux.widgets.client.grid.DataColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.DataColumnEditorCreators;
import org.cruxframework.crux.widgets.client.grid.DataRow;
import org.cruxframework.crux.widgets.client.grid.Grid;
import org.cruxframework.crux.widgets.client.grid.Grid.SortingType;
import org.cruxframework.crux.widgets.client.grid.RowDetailWidgetCreator;
import org.cruxframework.crux.widgets.client.grid.RowSelectionModel;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition.WidgetColumnCreator;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.BeforeRowSelectEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.BeforeShowRowDetailsEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.LoadRowDetailsEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.RowClickEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.RowDoubleClickEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.RowRenderEvtBind;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.ShowRowDetailsEvtBind;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Gesse S. F. Dafe
 */
//TODO: documentation - don't forget @CellName
@DeclarativeFactory(id="grid", library="widgets", targetWidget=Grid.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="pageSize", type=Integer.class, defaultValue="8"),
	@TagAttributeDeclaration(value="rowSelection", type=RowSelectionModel.class, defaultValue="unselectable"),
	@TagAttributeDeclaration(value="cellSpacing", type=Integer.class, defaultValue="1"),
	@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="stretchColumns", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="highlightRowOnMouseOver", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="fixedCellSize", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="emptyDataFilling", type=String.class, defaultValue=" "),
	@TagAttributeDeclaration(value="defaultSortingColumn", type=String.class),
	@TagAttributeDeclaration(value="defaultSortingType", type=SortingType.class, defaultValue="ascending"),
	@TagAttributeDeclaration(value="showRowDetailsIcon", type=Boolean.class, defaultValue="true"),
	@TagAttributeDeclaration(value="freezeHeaders", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="caseSensitive", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="keepEditorOnClickDisabledRows", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="showEditorButtons", type=Boolean.class),
	@TagAttributeDeclaration(value="editButtonTooltip", type=String.class,defaultValue="false",supportsI18N=true,supportsResources=true),
	@TagAttributeDeclaration(value="saveButtonTooltip", type=String.class,defaultValue="false",supportsI18N=true,supportsResources=true),
	@TagAttributeDeclaration(value="cancelButtonTooltip", type=String.class,defaultValue="false",supportsI18N=true,supportsResources=true)
})
@TagAttributes({
	@TagAttribute(value="dataSource", processor=GridFactory.DataSourceAttributeParser.class)
})
@TagEvents({
	@TagEvent(RowClickEvtBind.class),
	@TagEvent(RowDoubleClickEvtBind.class),
	@TagEvent(RowRenderEvtBind.class),
	@TagEvent(BeforeRowSelectEvtBind.class),
	@TagEvent(BeforeShowRowDetailsEvtBind.class),
	@TagEvent(ShowRowDetailsEvtBind.class),
	@TagEvent(LoadRowDetailsEvtBind.class)
})
@TagChildren({
	@TagChild(value=GridFactory.ColumnProcessor.class, autoProcess=false),
	@TagChild(value=GridFactory.RowDetailsProcessor.class, autoProcess=false)
})
public class GridFactory extends WidgetCreator<WidgetCreatorContext>
{
	protected static int childWidgetsIdSuffix = 0;
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String columnsDefinitions = getColumnDefinitions(out, context);
		String rowDetailsCreator = getRowDetailCreator(out, context);
		
		JSONObject widgetElement = context.getWidgetElement();
		
		out.println(className + " " + context.getWidget()+" = new "+className+"("+columnsDefinitions+", "+getPageSize(widgetElement)+", "+
            getRowSelectionModel(widgetElement)+", "+getCellSpacing(widgetElement)+", "+getAutoLoad(widgetElement)+", "+
            getStretchColumns(widgetElement)+", "+getHighlightRowOnMouseOver(widgetElement)+", "+
            getEmptyDataFilling(widgetElement)+", "+isFixedCellSize(widgetElement)+", "+getSortingColumn(widgetElement)+", "+
            getSortingType(widgetElement) + ", "+ rowDetailsCreator + ", "+ 
            getShowRowDetailsIcon(widgetElement) + ", " + getFreezeHeaders(widgetElement) +", "+getCaseSensitive(widgetElement) + "," + getKeepEditorOnClickDisabledRows(widgetElement) + "," + 
            getShowEditorButtons(widgetElement) + ", " + getEditButtonTooltip(widgetElement) + ", " + getSaveButtonTooltip(widgetElement)+ ", " + getCancelButtonTooltip(widgetElement) + ");");
	}
	
	private boolean getShowRowDetailsIcon(JSONObject gridElem) 
	{	
		String showRowDetailsIcon = gridElem.optString("showRowDetailsIcon");
		if(!StringUtils.isEmpty(showRowDetailsIcon))
		{
			return Boolean.parseBoolean(showRowDetailsIcon);
		}
		else
		{
			return true;
		}
	}
	
	private boolean getFreezeHeaders(JSONObject gridElem) 
	{	
		String strFreeze = gridElem.optString("freezeHeaders");
		if(!StringUtils.isEmpty(strFreeze))
		{
			return Boolean.parseBoolean(strFreeze);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private String getSortingType(JSONObject gridElem)
	{
		String sortingType = gridElem.optString("defaultSortingType");
		if(!StringUtils.isEmpty(sortingType))
		{
			SortingType sort = SortingType.valueOf(sortingType);
			return SortingType.class.getCanonicalName()+"."+sort.toString();
		}
		return null;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private String getSortingColumn(JSONObject gridElem)
	{
		String sort = gridElem.optString("defaultSortingColumn");
		if (!StringUtils.isEmpty(sort))
		{
			return EscapeUtils.quote(sort);
		}
		return null;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private boolean getCaseSensitive(JSONObject gridElem)
	{
		String caseSensitive = gridElem.optString("caseSensitive");
		
		if(caseSensitive != null && caseSensitive.trim().length() > 0)
		{
			return Boolean.parseBoolean(caseSensitive);
		}
		return false;
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private boolean isFixedCellSize(JSONObject gridElem)
	{
		String fixedCellSize = gridElem.optString("fixedCellSize");
		
		if(fixedCellSize != null && fixedCellSize.trim().length() > 0)
		{
			return Boolean.parseBoolean(fixedCellSize);
		}
		
		return false;
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private String getEmptyDataFilling(JSONObject gridElem)
	{
		String emptyDataFilling = gridElem.optString("emptyDataFilling");
		
		if(emptyDataFilling != null && emptyDataFilling.trim().length() > 0)
		{
			return EscapeUtils.quote(emptyDataFilling);
		}
		
		return null;
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private boolean getHighlightRowOnMouseOver(JSONObject gridElem)
	{
		String highlight = gridElem.optString("highlightRowOnMouseOver");
		
		if(highlight != null && highlight.trim().length() > 0)
		{
			return Boolean.parseBoolean(highlight);
		}
		
		return false;
	}
	
	private boolean getShowEditorButtons(JSONObject gridElem)
	{
		String highlight = gridElem.optString("showEditorButtons");
		
		if(highlight != null && highlight.trim().length() > 0)
		{
			return Boolean.parseBoolean(highlight);
		}
		
		return false;
	}
	
	private String getEditButtonTooltip(JSONObject gridElem)
	{
		String emptyDataFilling = gridElem.optString("editButtonTooltip");
		
		if(emptyDataFilling != null && emptyDataFilling.trim().length() > 0)
		{
			return EscapeUtils.quote(emptyDataFilling);
		}
		
		return null;
	}
	
	private String getSaveButtonTooltip(JSONObject gridElem)
	{
		String emptyDataFilling = gridElem.optString("saveButtonTooltip");
		
		if(emptyDataFilling != null && emptyDataFilling.trim().length() > 0)
		{
			return EscapeUtils.quote(emptyDataFilling);
		}
		
		return null;
	}

	
	private String getCancelButtonTooltip(JSONObject gridElem)
	{
		String emptyDataFilling = gridElem.optString("cancelButtonTooltip");
		
		if(emptyDataFilling != null && emptyDataFilling.trim().length() > 0)
		{
			return EscapeUtils.quote(emptyDataFilling);
		}
		
		return null;
	}



	/**
	 * @param gridElem
	 * @return
	 */
	private boolean getAutoLoad(JSONObject gridElem)
	{
		String autoLoad = gridElem.optString("autoLoadData");
		
		if(autoLoad != null && autoLoad.trim().length() > 0)
		{
			return Boolean.parseBoolean(autoLoad);
		}
		
		return false;
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private boolean getKeepEditorOnClickDisabledRows(JSONObject gridElem)
	{
		String keepEditor = gridElem.optString("keepEditorOnClickDisabledRows");
		
		if(keepEditor != null && keepEditor.trim().length() > 0)
		{
			return Boolean.parseBoolean(keepEditor);
		}
		
		return false;
	}
	
	/**
	 * @param gridElem
	 * @return
	 */
	private boolean getStretchColumns(JSONObject gridElem)
	{
		String stretchColumns = gridElem.optString("stretchColumns");
		
		if(stretchColumns != null && stretchColumns.trim().length() > 0)
		{
			return Boolean.parseBoolean(stretchColumns);
		}
		
		return false;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private int getCellSpacing(JSONObject gridElem)
	{
		String spacing = gridElem.optString("cellSpacing");
		
		if(spacing != null && spacing.trim().length() > 0)
		{
			return Integer.parseInt(spacing);
		}
		
		return 1;
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private String getRowSelectionModel(JSONObject gridElem)
	{
		String rowSelection = gridElem.optString("rowSelection");
		
		RowSelectionModel ret = RowSelectionModel.unselectable;
		if(rowSelection != null && rowSelection.length() > 0)
		{
			if("unselectable".equals(rowSelection))
			{
				ret = RowSelectionModel.unselectable;
			}
			else if("single".equals(rowSelection))
			{
				ret = RowSelectionModel.single;
			}
			else if("multiple".equals(rowSelection))
			{
				ret = RowSelectionModel.multiple;
			}
			else if("singleRadioButton".equals(rowSelection))
			{
				ret = RowSelectionModel.singleRadioButton;
			}
			else if("multipleCheckBox".equals(rowSelection))
			{
				ret = RowSelectionModel.multipleCheckBox;
			}
			else if("multipleCheckBoxSelectAll".equals(rowSelection))
			{
				ret = RowSelectionModel.multipleCheckBoxSelectAll;
			}
		}
		
		return RowSelectionModel.class.getCanonicalName()+"."+ret.toString();
	}

	/**
	 * @param gridElem
	 * @return
	 */
	private int getPageSize(JSONObject gridElem)
	{
		String pageSize = gridElem.optString("pageSize");
		
		if(pageSize != null && pageSize.length() > 0)
		{
			return Integer.parseInt(pageSize);
		}
		
		return Integer.MAX_VALUE;
	}

	/**
	 * @param out
	 * @param gridElem
	 * @return
	 * @throws CruxGeneratorException
	 */
	private String getColumnDefinitions(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String defs = createVariableName("defs");
		
		out.println(ColumnDefinitions.class.getCanonicalName()+" "+defs+" = new "+ColumnDefinitions.class.getCanonicalName()+"();");

		JSONArray colElems = ensureChildren(context.getWidgetElement(), false, context.getWidgetId());
		int colsSize = colElems.length();
		if(colsSize > 0)
		{
			for (int i=0; i<colsSize; i++)
			{
				JSONObject colElem = colElems.optJSONObject(i);
				if (colElem != null)
				{
					if(!getChildName(colElem).equals("rowDetails"))
					{
						String width = colElem.optString("width");
						String strVisible = colElem.optString("visible");
						String strSortable = colElem.optString("sortable");					
						String strWrapLine = colElem.optString("wrapLine");
						String strFrozen = colElem.optString("frozen");
						String label = colElem.optString("label");
						String key = colElem.optString("key");
						String strFormatter = colElem.optString("formatter");
						String hAlign = colElem.optString("horizontalAlignment");
						String vAlign = colElem.optString("verticalAlignment");
	
						boolean visible = (strVisible != null && strVisible.length() > 0) ? Boolean.parseBoolean(strVisible) : true;
						boolean sortable = (strSortable != null && strSortable.length() > 0) ? Boolean.parseBoolean(strSortable) : true;
						boolean wrapLine = (strWrapLine != null && strWrapLine.length() > 0) ? Boolean.parseBoolean(strWrapLine) : false;
						boolean frozen = (strFrozen != null && strFrozen.length() > 0) ? Boolean.parseBoolean(strFrozen) : false;
						String formatter = (strFormatter != null && strFormatter.length() > 0) ? strFormatter : null;
						label = (label != null && label.length() > 0) ? getDeclaredMessage(label) : EscapeUtils.quote("");
	
						String def = createVariableName("def");
	
						String columnType = getChildName(colElem);
						if("dataColumn".equals(columnType))
						{
							String editorCreatorVarName = getDataColumnEditorCreator(out, colElem, context);
							
							out.println(ColumnDefinition.class.getCanonicalName()+" "+def+" = new "+DataColumnDefinition.class.getCanonicalName()+"("+
									label+", "+
									EscapeUtils.quote(width)+", "+
									Formatters.getFormatterInstantionCommand(formatter)+", "+ 
									visible+", "+
									sortable+", "+
									wrapLine+", "+
									frozen + ", " +
									AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER") + ", " +
									AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE") + ", " +
									editorCreatorVarName + ");");
						}
						else if("widgetColumn".equals(columnType))
						{
							String widgetCreator = getWidgetColumnCreator(out, colElem, context);
							
							out.println(ColumnDefinition.class.getCanonicalName()+" "+def+" = new "+WidgetColumnDefinition.class.getCanonicalName()+"("+
									label+", "+
									EscapeUtils.quote(width)+", "+
									widgetCreator+", "+
									visible+", "+
									frozen+", "+
									AlignmentAttributeParser.getHorizontalAlignment(hAlign, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER")+", "+
									AlignmentAttributeParser.getVerticalAlignment(vAlign, HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE")+");");
						}
						else
						{
							throw new CruxGeneratorException("Grid ["+context.readWidgetProperty("id")+"] has an invalid column (unexpected column type).");
						}
	
						out.print(defs+".add("+EscapeUtils.quote(key)+", "+def+");");
					}
				}
			}
		}
		else
		{
			throw new CruxGeneratorException("Grid ["+context.readWidgetProperty("id")+"] has no column.");
		}
				
		return defs;
	}
	
	private String getDataColumnEditorCreator(SourcePrinter out, JSONObject colElem, WidgetCreatorContext context)
    {
	    String editorCreatorVarName = "null";
	    
	    JSONObject child = ensureFirstChild(colElem, true, context.getWidgetId());
	    
	    if(child != null)
	    {
		    editorCreatorVarName = createVariableName("dataColumnEditor");

		    String packageName = DataColumnEditorCreators.class.getPackage().getName();
		    String classSimpleName = ("DColEdit_" + getView().getId() + "_" + context.getWidgetId() + "_" + colElem.optString("key")).replaceAll("[^a-zA-Z0-9\\$]", "_");
			String classCanonicalName = packageName + "." + classSimpleName;
		    
			out.println(classCanonicalName + " " + editorCreatorVarName + " = new " + classCanonicalName + "();");
		    
		    DataColumnEditorCreatorFactory dataColEditorFactory = new DataColumnEditorCreatorFactory(
		    		classSimpleName, packageName, this, context, child, 
		    		this.getLoggerVariable(), getDeclaredMessages());
		   
		    dataColEditorFactory.createEditorCreator();
	    }

	    return editorCreatorVarName;
    }
	
	/**
	 * @param out
	 * @param colElem
	 * @return
	 */
	private String getWidgetColumnCreator(SourcePrinter out, JSONObject colElem, WidgetCreatorContext context)
    {
		String colDef = createVariableName("widgetColumnCreator");
	    String className = WidgetColumnCreator.class.getCanonicalName();
	    
	    out.println(className+" "+colDef+" = new "+className+"(){");
	    out.println("public Widget createWidgetForColumn(){");
	    
	    JSONObject child = ensureFirstChild(colElem, false, context.getWidgetId());
		String childWidget = createChildWidget(out, child, null, true, context);
        out.println("return "+childWidget+";");
	    
	    out.println("};");
	    out.println("};");

	    return colDef;
    }
	
	/**
	 * @param out
	 * @param colElem
	 * @return
	 */
	private String getRowDetailCreator(SourcePrinter out, WidgetCreatorContext context)
    {
		String rowDetailCreatorVar = "null";
		
		JSONArray childElems = ensureChildren(context.getWidgetElement(), false, context.getWidgetId());
		int childrenSize = childElems.length();
		if(childrenSize > 0)
		{
			for (int i = 0; i < childrenSize; i++)
			{
				JSONObject child = childElems.optJSONObject(i);
				if (child != null)
				{
					String childType = getChildName(child);
					if(childType.equals("rowDetails"))
					{
						String className = RowDetailWidgetCreator.class.getCanonicalName();
						rowDetailCreatorVar = createVariableName("rowDetailCreator");
						JSONObject detailElem = ensureChildren(child, false, context.getWidgetId()).optJSONObject(0);
						out.println("final "+ className + " " + rowDetailCreatorVar + " = new " + className+"(){");
						out.println("public Widget createWidgetForRowDetail(final " + DataRow.class.getCanonicalName() + " row){");
						String childWidget = createChildWidget(out, detailElem, new RowDetailsWidgetConsumer("row"), true, context);
						out.println("return "+childWidget+";");
						out.println("};");
						out.println("};");
						return rowDetailCreatorVar;
					}
				}
			}
		}
		
	    return rowDetailCreatorVar;
    }
	
	/**
	 * 
	 * @author Gesse Dafe
	 */
	private class RowDetailsWidgetConsumer implements WidgetConsumer
	{
		private final String rowVariableName;

		public RowDetailsWidgetConsumer(String rowVariableName) 
		{
			this.rowVariableName = rowVariableName;
		}
		
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem) 
		{
			out.println("registerWidget(" + rowVariableName + "," + EscapeUtils.quote(widgetId) + ", " + widgetVariableName + ");");
		}
	}
	
	@TagConstraints(maxOccurs="unbounded")
	@TagChildren({
		@TagChild(DataColumnProcessor.class),
		@TagChild(WidgetColumnProcessor.class)
	})
	public static class ColumnProcessor extends ChoiceChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	}

	
	@TagConstraints(tagName="dataColumn", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="visible", type=Boolean.class),
		@TagAttributeDeclaration(value="sortable", type=Boolean.class, defaultValue="true"),
		@TagAttributeDeclaration(value="wrapLine", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration(value="frozen", type=Boolean.class, defaultValue="false"),
		@TagAttributeDeclaration("label"),
		@TagAttributeDeclaration(value="key", required=true),
		@TagAttributeDeclaration("formatter"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(value=GridFactory.DataColumnEditorProcessor.class, autoProcess=false)
	})
	public static class DataColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="widgetColumn", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="visible", type=Boolean.class),
		@TagAttributeDeclaration("label"),
		@TagAttributeDeclaration(value="key", required=true),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})
	public static class WidgetColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext>{}
	@TagConstraints(tagName="rowDetails", maxOccurs="1", minOccurs="0")
	@TagChildren({
		@TagChild(value=WidgetProcessor.class, autoProcess=false)
	})
	public static class RowDetailsProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="editor", maxOccurs="1", minOccurs="0")
	@TagChildren({
		@TagChild(value=WidgetProcessor.class, autoProcess=false)
	})
	public static class DataColumnEditorProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
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
			autoCreateDataSourceColumnDefinitions(out, context.getWidgetElement(), dtoType, context.getWidgetId(), colDefs, columnDefinitionsAnot);
			out.println(widget+".setDataSource("+dataSource+");");
		}

		private void autoCreateDataSourceColumnDefinitions(SourcePrinter out, JSONObject gridElem, JClassType dtoType, String gridId, String colDefs,
				org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions columnDefinitions)
        {
			String dtoClassName = dtoType.getParameterizedQualifiedSourceName();

			JSONArray colElems = widgetCreator.ensureChildren(gridElem, false, gridId);
			int colsSize = colElems.length();
			if(colsSize > 0)
			{
				for (int i=0; i<colsSize; i++)
				{
					JSONObject colElem = colElems.optJSONObject(i);
					if (colElem != null)
					{
						String columnType = getChildName(colElem);
						if("dataColumn".equals(columnType))
						{
							StringBuilder getValueExpression = new StringBuilder();
							String colKey = colElem.optString("key");
							
							if (!isDatasourceColumnBound(colKey, columnDefinitions))
							{
								JType propType;
								try
								{
									propType = JClassUtils.buildGetValueExpression(getValueExpression, dtoType, colKey, "recordObject", true);
								}
								catch (Exception e)
								{
									throw new CruxGeneratorException("Grid ["+gridId+"] has an invalid column ["+colKey+"].");
								}

								JClassType comparableType = getWidgetCreator().getContext().getTypeOracle().findType(Comparable.class.getCanonicalName());

								boolean isSortable = (propType.isPrimitive() != null) || (comparableType.isAssignableFrom((JClassType) propType));
								String propTypeName = JClassUtils.getGenericDeclForType(propType);
								out.println(colDefs+".addColumn(new "+org.cruxframework.crux.core.client.datasource.ColumnDefinition.class.getCanonicalName()+
										"<"+propTypeName+","+dtoClassName+">("+EscapeUtils.quote(colKey)+","+isSortable+"){");
								out.println("public "+propTypeName+" getValue("+dtoClassName+" recordObject){");
								out.println("return "+getValueExpression.toString());
								out.println("}");
								out.println("});");
							}
						}
					}
				}
			}
        }

		private boolean isDatasourceColumnBound(String colKey, org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinitions columnDefinitions)
        {
	        if (columnDefinitions != null)
	        {
	        	for (org.cruxframework.crux.core.client.datasource.annotation.ColumnDefinition columnDefinition : columnDefinitions.value())
                {
	                if (columnDefinition.value().equals(colKey))
	                {
	                	return true;
	                }
                }
	        }
			
			return false;
        }
	}	
}