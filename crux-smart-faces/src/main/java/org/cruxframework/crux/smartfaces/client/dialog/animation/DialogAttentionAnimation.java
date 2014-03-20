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
package org.cruxframework.crux.smartfaces.client.dialog.animation;

import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DialogAttentionAnimation extends DialogAnimation
{
	protected StandardAnimation getExitAnimation()
	{
		return new StandardAnimation(StandardAnimation.Type.fadeOut);
	}

	public static DialogAnimation bounce = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounce);
        }
	};

	public static DialogAnimation flash = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flash);
        }
	};

	public static DialogAnimation pulse = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.pulse);
        }
	};

	public static DialogAnimation rubberBand = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rubberBand);
        }
	};

	public static DialogAnimation shake = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.shake);
        }
	};

	public static DialogAnimation swing = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.swing);
        }
	};

	public static DialogAnimation tada = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.tada);
        }
	};

	public static DialogAnimation wobble = new DialogAttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.wobble);
        }
	};
}
