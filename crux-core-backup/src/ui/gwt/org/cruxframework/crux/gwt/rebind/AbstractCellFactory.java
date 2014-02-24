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

import java.util.ArrayList;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.cell.CustomCells;
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
import org.cruxframework.crux.gwt.client.DateFormatUtil;
import org.cruxframework.crux.gwt.client.NumberFormatUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */

@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataObject", required=true)
})
public abstract class AbstractCellFactory<C extends WidgetCreatorContext> extends WidgetCreator<C> 
{
	/**
	 * @param metaElem
	 * @return
	 */
	protected String getDataObject(JSONObject metaElem)
    {
		String dataObject = metaElem.optString("dataObject");
		String dataObjectClass = DataObjects.getDataObject(dataObject);
		if (StringUtils.isEmpty(dataObjectClass))
		{
			throw new CruxGeneratorException("DataObject ["+dataObject+"], referenced by widget ["+metaElem.optString("id")+"] not found.");
		}
		
		return dataObjectClass;
    }

	/**
	 * @param out
	 * @param metaElem
	 * @return
	 */
	protected String getCell(SourcePrinter out, JSONObject metaElem, String parentWidgetId)
	{
		JSONObject child = ensureFirstChild(metaElem, false, parentWidgetId);
		String childName = getChildName(child);
		String cell = createVariableName("cell");
		
		out.print(AbstractCell.class.getCanonicalName()+" "+cell+"=("+AbstractCell.class.getCanonicalName()+")");
		if (childName.equals("customCell"))
		{
			getCustomCell(out, child);
		}
		else if (childName.equals("buttonCell"))
		{
			out.println("new "+ButtonCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("textCell"))
		{
			out.println("new "+TextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("clickableTextCell"))
		{
			out.println("new "+ClickableTextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("selectionCell"))
		{
			getSelectionCell(out, child, parentWidgetId);
		}
		else if (childName.equals("textInputCell"))
		{
			out.println("new "+TextInputCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("checkboxCell"))
		{
			getCheckboxCell(out, child);
		}
		else if (childName.equals("datePickerCell"))
		{
			getDatePickerCell(out, child);
		}
		else if (childName.equals("editTextCell"))
		{
			out.println("new "+EditTextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("actionCell"))
		{
			getActionCell(out, child, metaElem);
		}
		else if (childName.equals("dateCell"))
		{
			getDateCell(out, child);
		}
		else if (childName.equals("imageCell"))
		{
			out.println("new "+ImageCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("imageLoadingCell"))
		{
			out.println("new "+ImageLoadingCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("imageResourceCell"))
		{
			out.println("new "+ImageResourceCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("numberCell"))
		{
			getNumberCell(out, metaElem);
		}
		else if (childName.equals("safeHtmlCell"))
		{
			out.println("new "+SafeHtmlCell.class.getCanonicalName()+"();");
		}
		else
		{
			out.println("null;");
		}
		
		return cell;
	}

	/**
	 * @param out
	 * @param metaElem
	 */
	protected void getNumberCell(SourcePrinter out, JSONObject metaElem)
    {
	    String numberPattern = metaElem.optString("numberPattern");
	    if (numberPattern == null || numberPattern.length() == 0)
	    {
	    	numberPattern = NumberFormatUtil.DECIMAL_PATTERN;
	    }
	    out.println("new "+NumberCell.class.getCanonicalName()+"("+NumberFormatUtil.class.getCanonicalName()+".getNumberFormat("+
	    			EscapeUtils.quote(numberPattern)+"));");
    }

	/**
	 * @param out
	 * @param child
	 * @param parentMetaElem 
	 */
	protected void getActionCell(SourcePrinter out, JSONObject child, JSONObject parentMetaElem)
    {
	    String text = child.optString("text");
	    String delegateMethod = child.optString("delegateMethod");
	    
	    assert (!StringUtils.isEmpty(delegateMethod) && (!StringUtils.isEmpty(text)));

		String dataObjectClass = getDataObject(parentMetaElem);

		out.println("new "+ActionCell.class.getCanonicalName()+"<"+dataObjectClass+">("+EscapeUtils.quote(text)+
	    		", new "+Delegate.class.getCanonicalName()+"<"+dataObjectClass+">(){");
	    out.println("void execute("+dataObjectClass+" object){");
	    EvtProcessor.printEvtCall(out, delegateMethod, "loadCell", dataObjectClass, "object", this);
	    out.println("}");
	    out.println(");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getDatePickerCell(SourcePrinter out, JSONObject child)
    {
	    String datePattern = child.optString("datePattern");
	    if (datePattern == null || datePattern.length() == 0)
	    {
	    	datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
	    }
	    out.println("new "+DatePickerCell.class.getCanonicalName()+"("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
	    		EscapeUtils.quote(datePattern)+"));");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getDateCell(SourcePrinter out, JSONObject child)
    {
	    String datePattern = child.optString("datePattern");
	    if (datePattern == null || datePattern.length() == 0)
	    {
	    	datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
	    }
	    out.println("new "+DateCell.class.getCanonicalName()+"("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
	    		EscapeUtils.quote(datePattern)+"));");
    }
	
	/**
	 * @param out
	 * @param child
	 */
	protected void getCheckboxCell(SourcePrinter out, JSONObject child)
    {
	    boolean dependsOnSelection = false;
	    String dependsOnSelectionStr = child.optString("dependsOnSelection");
	    if (StringUtils.isEmpty(dependsOnSelectionStr))
	    {
	    	dependsOnSelection = Boolean.parseBoolean(dependsOnSelectionStr);
	    }
	    boolean handlesSelection = dependsOnSelection;
	    String handlesSelectionStr = child.optString("handlesSelection");
	    if (StringUtils.isEmpty(handlesSelectionStr))
	    {
	    	handlesSelection = Boolean.parseBoolean(handlesSelectionStr);
	    }
	    
	    out.println("new "+CheckboxCell.class.getCanonicalName()+"("+dependsOnSelection+", "+handlesSelection+");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getSelectionCell(SourcePrinter out, JSONObject child, String parentWidgetId)
    {
	    String options = createVariableName("options");
	    out.println(ArrayList.class.getCanonicalName()+"<String> "+options+" = "+ArrayList.class.getCanonicalName()+"<String>();");
	    JSONArray children = ensureChildren(child, true, parentWidgetId);
	    if (children != null)
	    {
	    	for (int i=0; i< children.length(); i++)
	    	{
	    		JSONObject optionElement = children.optJSONObject(i);
	    		String textOption = ensureTextChild(optionElement, true, parentWidgetId, true);
	    		out.println(options+".add("+textOption+");");
	    	}
	    }
	    out.println("new "+SelectionCell.class.getCanonicalName()+"("+options+");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getCustomCell(SourcePrinter out, JSONObject child)
    {
	    String cellName = child.optString("cellName");
	    assert (!StringUtils.isEmpty(cellName));
		out.println("new "+CustomCells.getCustomCell(cellName)+"();");
		//TODO validar se o tipo do objeto informado no dataObject é compatível com o tipo do objeto renderizado pelo customCell (e também pelo dataProvider)
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@TagConstraints(minOccurs="1", maxOccurs="1")
	@TagChildren({
		@TagChild(CustomCellProcessor.class),
		@TagChild(ButtonCellProcessor.class),
		@TagChild(TextCellProcessor.class),
		@TagChild(ClickableTextCellProcessor.class),
		@TagChild(SelectionCellProcessor.class), 
		@TagChild(TextInputCellProcessor.class),
		@TagChild(CheckboxCellProcessor.class),
		@TagChild(DatePickerCellProcessor.class),
		@TagChild(EditTextCellProcessor.class),
		@TagChild(ActionCellProcessor.class),
		@TagChild(DateCellProcessor.class),
		@TagChild(ImageCellProcessor.class),
		@TagChild(ImageLoadingCellProcessor.class),
		@TagChild(ImageResourceCellProcessor.class),
		@TagChild(NumberCellProcessor.class),
		@TagChild(SafeHtmlCellProcessor.class)
	})
	public static class CellListChildProcessor extends ChoiceChildProcessor<WidgetCreatorContext> {}	
	
	@TagConstraints(tagName="customCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="cellName", required=true)
	})
	public static class CustomCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="buttonCell")
	public static class ButtonCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="textCell")
	public static class TextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="clickableTextCell")
	public static class ClickableTextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="selectionCell")
	@TagChildren({
		@TagChild(ListOptionProcessor.class)
	})
	public static class SelectionCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="option", type=String.class, minOccurs="0", maxOccurs="unbounded")
	public static class ListOptionProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="textInputCell")
	public static class TextInputCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="checkboxCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="dependsOnSelection", type=Boolean.class),
		@TagAttributeDeclaration(value="handlesSelection", type=Boolean.class)
	})
	public static class CheckboxCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="datePickerCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("datePattern")
	})
	public static class DatePickerCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="editTextCell")
	public static class EditTextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="actionCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true),
		@TagAttributeDeclaration(value="delegateMethod", required=true)
	})
	public static class ActionCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

/*	@TagConstraints(tagName="compositeCell")
	@TagChildren({
		@TagChild(CompositeChildProcessor.class)
	})
	public static class CompositeCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="column")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", required=true),
		@TagAttributeDeclaration(value="verticalAlignment", required=true)//TODO: e o fieldUpdater??? 
	})
	@TagChildren({
		@TagChild(CellListChildProcessor.class)
	})
	public static class CompositeChildProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
*/
	@TagConstraints(tagName="dateCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("datePattern")
	})
	public static class DateCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="imageCell")
	public static class ImageCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="imageLoadingCell")
	public static class ImageLoadingCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="imageResourceCell")
	public static class ImageResourceCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
		
	@TagConstraints(tagName="numberCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("numberPattern")
	})
	public static class NumberCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="safeHtmlCell")
	public static class SafeHtmlCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
}
