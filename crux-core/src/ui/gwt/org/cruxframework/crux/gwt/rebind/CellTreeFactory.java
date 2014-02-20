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
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.RevealAnimation;
import com.google.gwt.user.cellview.client.CellTree.SlideAnimation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="cellTree", library="gwt", targetWidget=CellTree.class)
@TagAttributes({
	@TagAttribute(value="defaultColumnWidth", type=Integer.class), 
	@TagAttribute(value="minimumColumnWidth", type=Integer.class), 
	@TagAttribute(value="defaultNodeSize", type=Integer.class), 
	@TagAttribute(value="animation", type=CellTreeFactory.Animations.class, processor=CellTreeFactory.AnimationAttributeProcessor.class) 
})
public class CellTreeFactory extends AbstractCellTreeFactory implements HasAnimationFactory<WidgetCreatorContext>, 
													FocusableFactory<WidgetCreatorContext>
{
	public static enum Animations{slide, reveal}
	
	public static class AnimationAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public AnimationAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        Animations animation = Animations.valueOf(attributeValue);
	        
	        switch (animation)
            {
            	case slide: out.println(context.getWidget()+".setAnimation("+SlideAnimation.class.getCanonicalName()+".create());"); break;
            	case reveal: out.println(context.getWidget()+".setAnimation("+RevealAnimation.class.getCanonicalName()+".create());"); break;
            }
        }
	}
	
}

