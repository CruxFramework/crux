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

package org.cruxframework.crux.smartfaces.client.swappanel;

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.css.animation.Animation.Callback;
import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author bruno.rafael
 *
 */
public abstract class SwapAnimation
{
	protected abstract Animation<?> getEntranceAnimation();
	protected abstract Animation<?> getExitAnimation();
	
	public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
	{
		animateParallel(in, out, handler, callback);
	}
	
	protected void animateParallel(final Widget in, final Widget out, final SwapAnimationHandler handler, final SwapAnimationCallback callback)
    {
	    getExitAnimation().animate(out, null);
		if (handler != null)
		{
			handler.setInElementInitialState(in);
		}
		getEntranceAnimation().animate(in, new Callback()
		{

			@Override
            public void onAnimationCompleted()
            {
				handler.setInElementFinalState(in);
				handler.setOutElementFinalState(out);
	            callback.onAnimationCompleted();
            }
		});
    }
	
	protected void animateInOrder(final Widget in, final Widget out, final SwapAnimationHandler handler, final SwapAnimationCallback callback)
    {
		getExitAnimation().animate(out, new Callback()
		{
			@Override
            public void onAnimationCompleted()
            {
				if (handler != null)
				{
					handler.setOutElementInitialState(out);
					handler.setInElementInitialState(in);
				}
				getEntranceAnimation().animate(in, new Callback()
				{

					@Override
                    public void onAnimationCompleted()
                    {
						handler.setInElementFinalState(in);
						handler.setOutElementFinalState(out);
	                    callback.onAnimationCompleted();
                    }
				});
            }
		});
    }
	
	public static interface SwapAnimationHandler
	{
		void setInElementInitialState(Widget in);
		void setInElementFinalState(Widget in);
		void setOutElementInitialState(Widget out);
		void setOutElementFinalState(Widget out);
	}
	
	public static interface SwapAnimationCallback
	{
		void onAnimationCompleted();
	}


	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounce = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOut);
		}

		@Override
        public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
        {
			animateInOrder(in, out, handler, callback);
        }
	};
	
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceUpDown = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceLeft = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceRight = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};


	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceDownUp = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};


	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fade = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeDownUp = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUp);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeUpDown = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDown);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeLeft = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeft);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeRight = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRight);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeDownUpBig = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeUpDownBig = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeLeftBig = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeRightBig = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation flipX = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipInX);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutX);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation flipY = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipInY);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutY);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation lightSpeed = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedOut);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotate = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOut);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateDownLeft = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInDownLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpLeft);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateDownRight = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInDownRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpRight);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateUpLeft = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInUpLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownLeft);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateUpRight = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInUpRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownRight);
		}
		
		@Override
		public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
		{
			animateInOrder(in, out, handler, callback);
		}
	};

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation roll = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollOut);
		}
	};
	
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceUpward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceDownward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceForward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceBackward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeForward = new SwapAnimation()
	{
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeBackward = new SwapAnimation()
	{
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeUpward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeDownward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};
}
