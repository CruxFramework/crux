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

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="simplePager", library="gwt", targetWidget=SimplePager.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="textlocation", type=TextLocation.class),
	@TagAttributeDeclaration(value="page", type=Integer.class),
	@TagAttributeDeclaration(value="pageStart", type=Integer.class)
})
public class SimplePagerFactory extends AbstractPagerFactory  
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		
		String textLocationStr = context.readWidgetProperty("textlocation");
		TextLocation textLocation = TextLocation.CENTER;
		if (!StringUtils.isEmpty(textLocationStr))
		{
			textLocation = TextLocation.valueOf(textLocationStr);
		}
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+TextLocation.class.getCanonicalName()+"."+textLocation.toString()+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}

