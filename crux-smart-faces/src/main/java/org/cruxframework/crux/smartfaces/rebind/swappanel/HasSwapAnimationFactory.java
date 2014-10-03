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
package org.cruxframework.crux.smartfaces.rebind.swappanel;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation;

/**
 * @author bruno.rafael
 *
 */
@TagAttributes({ @TagAttribute(value = "animation", processor = HasSwapAnimationFactory.AnimationProcessor.class, type = HasSwapAnimationFactory.SwapAnimations.class) })
public interface HasSwapAnimationFactory<C extends WidgetCreatorContext> extends HasAnimationFactory<C>
{
	public static enum SwapAnimations
	{
		bounce, bounceUpDown, bounceLeft, bounceRight, bounceDownUp, fade, fadeDownUp, fadeUpDown, fadeLeft, fadeRight, 
		fadeDownUpBig, fadeUpDownBig, fadeLeftBig, fadeRightBig, flipX, flipY, lightSpeed, rotate, rotateDownLeft, rotateDownRight,
		rotateUpLeft, rotateUpRight, roll, bounceUpward, bounceDownward, bounceForward, bounceBackward, fadeForward, fadeBackward, fadeUpward
	}

	public static class AnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public AnimationProcessor(WidgetCreator<?> widgetCreator)
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
		{
			out.println(context.getWidget() + ".setAnimation(" + SwapAnimation.class.getCanonicalName() + "." + attributeValue + ");");
		}
	}
}
