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
package org.cruxframework.crux.smartfaces.rebind.swapviewcontainer;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.CrawlableViewContainerFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapCrawlableViewContainer;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * @author Bruno M. Rafael bruno.rafael@triggolabs.com
 */
@DeclarativeFactory(id="swapCrawlableViewContainer", library=Constants.LIBRARY_NAME, targetWidget=SwapCrawlableViewContainer.class)
@TagAttributes({
	@TagAttribute(value="animationForward", type=SwapCrawlableViewContainerFactory.Animations.class, required=true, 
		processor=SwapCrawlableViewContainerFactory.AnimationForwardProcessor.class ,description="Defines the type of animation to be executed to advance the swap of view."),
	@TagAttribute(value="animationBackward",type=SwapCrawlableViewContainerFactory.Animations.class, required=true, 
		processor=SwapCrawlableViewContainerFactory.AnimationBackwardProcessor.class,   description="Defines the type of animation to be executed to back the swap of view.")
})
@TagChildren({
	@TagChild(SwapViewContainerFactory.ViewProcessor.class)
})
public class SwapCrawlableViewContainerFactory extends SwapViewContainerFactory implements CrawlableViewContainerFactory<SwapContainerContext>
{
	public static enum Animations{bounce, bounceUpDown, bounceLeft, bounceRight, bounceDownUp, fade, fadeDownUp, 
		fadeUpDown, fadeLeft, fadeRight, fadeDownUpBig, fadeUpDownBig, fadeLeftBig, fadeRightBig, flipX, flipY, lightSpeed, 
		rotate, rotateDownLeft, rotateDownRight, rotateUpLeft, rotateUpRight, roll, bounceUpward, bounceDownward,
		bounceForward, bounceBackward, fadeForward,fadeBackward, fadeUpward, fadeDownward}
	
	public static class AnimationForwardProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationForwardProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setAnimationForward("+SwapAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
	
	public static class AnimationBackwardProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationBackwardProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setAnimationBackward("+SwapAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
	
}
