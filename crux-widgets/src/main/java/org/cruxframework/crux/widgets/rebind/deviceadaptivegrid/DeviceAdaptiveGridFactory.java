package org.cruxframework.crux.widgets.rebind.deviceadaptivegrid;


import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.formatter.Formatters;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.SequenceChildProcessor;
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
import org.cruxframework.crux.widgets.client.deviceadaptivegrid.ActionColumnDefinition;
import org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGrid;
import org.cruxframework.crux.widgets.client.deviceadaptivegrid.DeviceAdaptiveGridColumnDefinitions;
import org.cruxframework.crux.widgets.client.grid.ColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.DataColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.DataColumnEditorCreators;
import org.cruxframework.crux.widgets.client.grid.Grid.SortingType;
import org.cruxframework.crux.widgets.client.grid.RowSelectionModel;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition.WidgetColumnCreator;
import org.cruxframework.crux.widgets.rebind.event.RowEventsBind.RowRenderEvtBind;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

@DeclarativeFactory(id="adaptiveGrid", library="widgets", targetWidget=DeviceAdaptiveGrid.class)
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
	@TagAttributeDeclaration(value="keepEditorOnClickDisabledRows", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="showEditorButtons", type=Boolean.class, defaultValue="false")
})
@TagAttributes({
	@TagAttribute(value="dataSource", processor=DeviceAdaptiveGridFactory.DataSourceAttributeParser.class)
})
@TagEvents({
	@TagEvent(RowRenderEvtBind.class)
})

@TagChildren({
	@TagChild(value=DeviceAdaptiveGridFactory.DeviceAdaptiveColumnProcessor.class, autoProcess=false)
})
public class DeviceAdaptiveGridFactory extends WidgetCreator<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String columnsDefinitions = getColumnDefinitions(out, context);

		JSONObject widgetElement = context.getWidgetElement();

		out.println(className + " " + context.getWidget()+" = new "+className+"();");
		out.println(context.getWidget()+".initGrid("+columnsDefinitions+", "+getPageSize(widgetElement)+", "+
            getRowSelectionModel(widgetElement)+", "+getCellSpacing(widgetElement)+", "+getAutoLoad(widgetElement)+", "+
            getStretchColumns(widgetElement)+", "+getHighlightRowOnMouseOver(widgetElement)+", "+
            getEmptyDataFilling(widgetElement)+", "+isFixedCellSize(widgetElement)+", "+getSortingColumn(widgetElement)+", "+
            getSortingType(widgetElement) + ","+ getKeepEditorOnClickDisabledRows(widgetElement) +  "," + getShowEditorButtons(widgetElement) + ");");
	}

	@TagChildren({
		@TagChild(DeviceAdaptiveGridFactory.LargeColumnProcessor.class),
		@TagChild(DeviceAdaptiveGridFactory.SmallColumnProcessor.class)
	})
	public static class DeviceAdaptiveColumnProcessor extends SequenceChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="largeColumns")
	@TagChildren({
		@TagChild(LargeColumnProcessorChildren.class)
	})
	public static class LargeColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(maxOccurs="unbounded", minOccurs="1")
	@TagChildren({
		@TagChild(DataColumnProcessor.class),
		@TagChild(WidgetColumnProcessor.class)
	})
	public static class LargeColumnProcessorChildren extends ChoiceChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="smallColumns")
	@TagChildren({
		@TagChild(SmallColumnProcessorChildren.class),
	})
	public static class SmallColumnProcessor  extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(maxOccurs="unbounded", minOccurs="1")
	@TagChildren({
		@TagChild(DataColumnProcessor.class),
		@TagChild(WidgetColumnProcessor.class),
		@TagChild(value=ActionColumnProcessor.class, autoProcess=false)
	})
	public static class SmallColumnProcessorChildren  extends ChoiceChildProcessor<WidgetCreatorContext> {}

	protected boolean getShowRowDetailsIcon(JSONObject gridElem)
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

		protected boolean getFreezeHeaders(JSONObject gridElem)
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
		protected String getSortingType(JSONObject gridElem)
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
		protected String getSortingColumn(JSONObject gridElem)
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
		protected boolean isFixedCellSize(JSONObject gridElem)
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
		protected String getEmptyDataFilling(JSONObject gridElem)
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
		protected String getDataSource(JSONObject gridElem)
		{
			String dataSource = gridElem.optString("dataSource");

			if(dataSource != null && dataSource.trim().length() > 0)
			{
				return EscapeUtils.quote(dataSource);
			}

			return null;
		}

		/**
		 * @param gridElem
		 * @return
		 */
		protected boolean getHighlightRowOnMouseOver(JSONObject gridElem)
		{
			String highlight = gridElem.optString("highlightRowOnMouseOver");

			if(highlight != null && highlight.trim().length() > 0)
			{
				return Boolean.parseBoolean(highlight);
			}

			return false;
		}

		/**
		 * @param gridElem
		 * @return
		 */
		protected boolean getAutoLoad(JSONObject gridElem)
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
		private boolean getShowEditorButtons(JSONObject gridElem)
		{
			String highlight = gridElem.optString("showEditorButtons");
			
			if(highlight != null && highlight.trim().length() > 0)
			{
				return Boolean.parseBoolean(highlight);
			}
			
			return false;
		}

		/**
		 * @param gridElem
		 * @return
		 */
		protected boolean getStretchColumns(JSONObject gridElem)
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
		protected int getCellSpacing(JSONObject gridElem)
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
		protected String getRowSelectionModel(JSONObject gridElem)
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
		protected int getPageSize(JSONObject gridElem)
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
		protected String getColumnDefinitions(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String defs = createVariableName("defs");

			JSONObject[] deviceChildren = new JSONObject[2];

			try
			{
				deviceChildren[0] = context.getWidgetElement().getJSONArray("_children").getJSONObject(0);
				deviceChildren[1] = context.getWidgetElement().getJSONArray("_children").getJSONObject(1);
			}
			catch (JSONException e)
			{
				throw new CruxGeneratorException("The widget ["+context.getWidgetId()+"], declared on View ["+getView().getId()+"], must contain both large and small columns.");
			}

			if (deviceChildren == null || deviceChildren.length < 2)
			{
				throw new CruxGeneratorException("The widget ["+context.getWidgetId()+"], declared on View ["+getView().getId()+"], must contain both large and small columns.");
			}


			out.println(DeviceAdaptiveGridColumnDefinitions.class.getCanonicalName()+" "+defs+" = new "+DeviceAdaptiveGridColumnDefinitions.class.getCanonicalName()+"();");


			for (int deviceIndex = 0; deviceIndex < deviceChildren.length ; deviceIndex++)
			{
				JSONArray colElems = ensureChildren(deviceChildren[deviceIndex], false, context.getWidgetId());
				Size deviceSize = deviceChildren[deviceIndex].toString().contains("largeColumns") ? Size.large : Size.small;

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
								boolean wrapLine = (strWrapLine != null && strWrapLine.length() > 0) ? Boolean.parseBoolean(strWrapLine) : true;
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
											AlignmentAttributeParser.getHorizontalAlignment(hAlign,
													HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER") + ", " +
											AlignmentAttributeParser.getVerticalAlignment(vAlign,
													HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE") + ", " +
											editorCreatorVarName + ");");
								}
								else if("actionColumn".equals(columnType))
								{
									String widgetCreator = getWidgetColumnCreator(out, colElem, context);

									out.println(ColumnDefinition.class.getCanonicalName()+" "+def+" = new "+ActionColumnDefinition.class.getCanonicalName()+"("+
											label+", "+
											EscapeUtils.quote(width)+", "+
											widgetCreator+", "+
											Boolean.FALSE+", "+
											frozen+", "+
											AlignmentAttributeParser.getHorizontalAlignment(hAlign,
													HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER")+", "+
											AlignmentAttributeParser.getVerticalAlignment(vAlign,
													HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE")+");");
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
											AlignmentAttributeParser.getHorizontalAlignment(hAlign,
													HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_CENTER")+", "+
											AlignmentAttributeParser.getVerticalAlignment(vAlign,
													HasVerticalAlignment.class.getCanonicalName()+".ALIGN_MIDDLE")+");");
								}
								else
								{
									throw new CruxGeneratorException("Grid ["+context.readWidgetProperty("id")+"] has an invalid column (unexpected column type).");
								}

								out.print(defs+".add(" + Size.class.getCanonicalName() + "." + deviceSize.name() + ", "  +EscapeUtils.quote(key)+", "+def+");");
							}
						}
					}
				}
				else
				{
					throw new CruxGeneratorException("Grid ["+context.readWidgetProperty("id")+"] has no column.");
				}
			}

			return defs;
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
			@TagChild(value=DeviceAdaptiveGridFactory.DataColumnEditorProcessor.class, autoProcess=false)
		})
		public static class DataColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

		public static class DataColumnEditorProcessor  extends WidgetChildProcessor<WidgetCreatorContext> {}

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
		public static class WidgetColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext>
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException{}
		}

		@TagConstraints(tagName="actionColumn", minOccurs="0", maxOccurs="unbounded")
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="key", required=true)
		})
		@TagChildren({
			@TagChild(value=WidgetProcessor.class, autoProcess=false)
		})
		public static class ActionColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext>
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException{}
		}

		public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext>{}

		@Override
	    public WidgetCreatorContext instantiateContext()
	    {
		    return new WidgetCreatorContext();
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

				DeviceAdaptiveDataColumnEditorCreatorFactory dataColEditorFactory = new DeviceAdaptiveDataColumnEditorCreatorFactory(
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

				JSONObject[] deviceChildren = new JSONObject[2];

				try
				{
					deviceChildren[0] = gridElem.getJSONArray("_children").getJSONObject(0);
					deviceChildren[1] = gridElem.getJSONArray("_children").getJSONObject(1);
				}
				catch (JSONException e)
				{
					throw new CruxGeneratorException("Error parsing dataSource device Columns widget ["+gridId +"],  must contain both large and small columns.");
				}

				for (int deviceIndex = 0; deviceIndex < deviceChildren.length ; deviceIndex++)
				{

					JSONArray colElems = widgetCreator.ensureChildren(deviceChildren[deviceIndex], false, gridId);
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
