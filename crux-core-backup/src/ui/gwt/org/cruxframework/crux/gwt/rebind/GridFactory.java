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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.json.JSONArray;

import com.google.gwt.user.client.ui.Grid;

class GridFactoryContext extends HTMLTableFactoryContext
{
	boolean cellsInitialized = false;
}

/**
 * Factory for Grid widget
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="grid", library="gwt", targetWidget=Grid.class)
@TagChildren({
	@TagChild(GridFactory.GridRowProcessor.class)
})
public class GridFactory extends HTMLTableFactory<GridFactoryContext>
{
	/**
	 * Populate the panel with declared items
	 * @param element
	 * @throws CruxGeneratorException 
	 */
	@Override
	public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException	
	{
		JSONArray children = ensureChildren(context.getWidgetElement(), true, context.getWidgetId());
		
		int count = getNonNullChildrenCount(children);
		
		if (count > 0)
		{
			String widget = context.getWidget();
			out.println(widget+".resizeRows("+count+");");
		}
	}

	/**
	 * @param children
	 * @return
	 */
	private static int getNonNullChildrenCount(JSONArray children)
	{
		int count = 0;
		if (children != null)
		{
			int size = children.length();
			for (int i=0; i<size; i++)
			{
				if (children.opt(i) != null)
				{
					count++;
				}
			}
		}
	    return count;
    }
	
	@TagConstraints(tagName="row", minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(GridCellProcessor.class)
	})
	public static class GridRowProcessor extends TableRowProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException
		{
			if (!context.cellsInitialized)
			{
				JSONArray children = getWidgetCreator().ensureChildren(context.getChildElement(), true, context.getWidgetId());
				String rootWidget = context.getWidget();
				out.println(rootWidget+".resizeColumns("+getNonNullChildrenCount(children)+");");
				context.cellsInitialized = true;
			}
			
			super.processChildren(out, context);
		}
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="cell")
	@TagChildren({
		@TagChild(GridChildrenProcessor.class)
	})
	public static class GridCellProcessor extends TableCellProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException
		{
			
			super.processChildren(out, context);
		}
	}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(GridCellTextProcessor.class),
		@TagChild(GridCellHTMLProcessor.class),
		@TagChild(GridCellWidgetProcessor.class)
	})
	public static class GridChildrenProcessor extends ChoiceChildProcessor<GridFactoryContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException {}
	}
	
	public static class GridCellTextProcessor extends CellTextProcessor<GridFactoryContext>{}
	public static class GridCellHTMLProcessor extends CellHTMLProcessor<GridFactoryContext>{}

	@TagChildren({
		@TagChild(GridWidgetProcessor.class)
	})	
	public static class GridCellWidgetProcessor extends CellWidgetProcessor<GridFactoryContext>
	{
		@Override
		public void processChildren(SourcePrinter out, GridFactoryContext context) throws CruxGeneratorException {}
		
	}
	public static class GridWidgetProcessor extends WidgetProcessor<GridFactoryContext>{} 
	
	@Override
    public GridFactoryContext instantiateContext()
    {
	    return new GridFactoryContext();
    }
}
