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

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class DialogAnimation
{
	private Animation<?> entrance = getEntranceAnimation();
	private Animation<?> exit = getExitAnimation();

	public void animateExit(Widget widget, Animation.Callback callback)
	{
		exit.animate(widget, callback);
	}
	
	public void animateEntrance(Widget widget, Animation.Callback callback)
	{
		entrance.animate(widget, callback);
	}
	
	protected abstract Animation<?> getExitAnimation();
	protected abstract Animation<?> getEntranceAnimation();
	
	public static DialogAnimation bounce = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOut);
		}
	};

	public static DialogAnimation bounceUpDown = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};

	public static DialogAnimation bounceLeft = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};

	public static DialogAnimation bounceRight = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};

	public static DialogAnimation bounceDownUp = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};

	public static DialogAnimation fade = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		}
	};

	public static DialogAnimation fadeDownUp = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUp);
		}
	};

	public static DialogAnimation fadeUpDown = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDown);
		}
	};

	public static DialogAnimation fadeLeft = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeft);
		}
	};

	public static DialogAnimation fadeRight = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRight);
		}
	};
	
	public static DialogAnimation fadeDownUpBig = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};

	public static DialogAnimation fadeUpDownBig = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};

	public static DialogAnimation fadeLeftBig = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};

	public static DialogAnimation fadeRightBig = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};	

	public static DialogAnimation flipX = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInX);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutX);
		}
	};	

	public static DialogAnimation flipY = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInY);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutY);
		}
	};	

	public static DialogAnimation lightSpeed = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.lightSpeedIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedOut);
		}
	};	

	public static DialogAnimation rotate = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOut);
		}
	};	

	public static DialogAnimation rotateDownLeft = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpLeft);
		}
	};	

	public static DialogAnimation rotateDownRight = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpRight);
		}
	};	

	public static DialogAnimation rotateUpLeft = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownLeft);
		}
	};	

	public static DialogAnimation rotateUpRight = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownRight);
		}
	};	

	public static DialogAnimation slideDown = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutUp);
		}
	};	

	public static DialogAnimation slideLeft = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutLeft);
		}
	};	

	public static DialogAnimation slideRight = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutRight);
		}
	};	

	public static DialogAnimation roll = new DialogAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rollIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollOut);
		}
	};	
}
