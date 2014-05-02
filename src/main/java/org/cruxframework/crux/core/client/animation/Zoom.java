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
package org.cruxframework.crux.core.client.animation;

import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.user.client.Window;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * @see http://lab.hakim.se/zoom-js/
 * Licensed by http://opensource.org/licenses/MIT
 */
@PartialSupport
public class Zoom
{
	private static Zoom instance;
	
	private Zoom()
	{
	}
	
	public static Zoom getInstance()
	{
		if(isSupported())
		{
			if(instance == null)
			{
				instance = new Zoom();
				instance.setEasing();
			}
			return instance;
		}
		return null;
	}
	
	// The current zoom level (scale)
	private Integer level = 1;
	
	// The current mouse position, used for panning
	private	Integer mouseX = 0, mouseY = 0;

	// Timeout before pan is activated
	private Integer panEngageTimeout = -1, panUpdateInterval = -1;
	
	public native Integer getXScrollOffset() /*-{
		return window.scrollX !== undefined ? window.scrollX : window.pageXOffset;
	}-*/;

	public native Integer getYScrollOffset() /*-{
		return window.scrollY !== undefined ? window.scrollY : window.pageYOffset;
	}-*/;
	
	public native Double getWindowInnerWidth() /*-{
		return window.innerWidth;
	}-*/;
	
	public native Double getWindowInnerHeight() /*-{
		return window.innerHeight;
	}-*/;
	
	private static native boolean isSupported() /*-{
	 	return 'WebkitTransform' in document.body.style ||
			   'MozTransform' in document.body.style    ||
			   'msTransform' in document.body.style     ||
			   'OTransform' in document.body.style      ||
			   'transform' in document.body.style;
  	}-*/;
	
	// The easing that will be applied when we zoom in/out
	private native void setEasing() /*-{
	 	document.body.style.transition = 'transform 0.8s ease';
		document.body.style.OTransition = '-o-transform 0.8s ease';
		document.body.style.msTransition = '-ms-transform 0.8s ease';
		document.body.style.MozTransition = '-moz-transform 0.8s ease';
		document.body.style.WebkitTransition = '-webkit-transform 0.8s ease';
	}-*/;

	private native void addEventListeners() /*-{
		// Zoom out if the user hits escape
		document.addEventListener( 'keyup', function( event ) {
			if( this.@org.cruxframework.crux.core.client.animation.Zoom::getLevel()() !== 1 && event.keyCode === 27 ) {
				zoom.out();
			}
		} );
		
		// Monitor mouse movement for panning
		document.addEventListener( 'mousemove', function( event ) {
			if( this.@org.cruxframework.crux.core.client.animation.Zoom::getLevel()() !== 1 ) {
				this.@org.cruxframework.crux.core.client.animation.Zoom::setMouseX(Ljava.lang.Integer;)(event.clientX);
				this.@org.cruxframework.crux.core.client.animation.Zoom::setMouseY(Ljava.lang.Integer;)(event.clientY);
			}
		} );
	}-*/;

	/**
	 * Applies the CSS required to zoom in, prioritizes use of CSS3
	 * transforms but falls back on zoom for IE.
	 *
	 * @param {Number} pageOffsetX
	 * @param {Number} pageOffsetY
	 * @param {Number} elementOffsetX
	 * @param {Number} elementOffsetY
	 * @param {Number} scale
	 */
	public native void magnify(Integer pageOffsetX, Integer pageOffsetY, Integer elementOffsetX, Integer elementOffsetY, Integer scale) /*-{
		if( supportsTransforms ) {
			var origin = pageOffsetX +'px '+ pageOffsetY +'px',
				transform = 'translate('+ -elementOffsetX +'px,'+ -elementOffsetY +'px) scale('+ scale +')';

			document.body.style.transformOrigin = origin;
			document.body.style.OTransformOrigin = origin;
			document.body.style.msTransformOrigin = origin;
			document.body.style.MozTransformOrigin = origin;
			document.body.style.WebkitTransformOrigin = origin;

			document.body.style.transform = transform;
			document.body.style.OTransform = transform;
			document.body.style.msTransform = transform;
			document.body.style.MozTransform = transform;
			document.body.style.WebkitTransform = transform;
		}
		else {
			// Reset all values
			if( scale === 1 ) {
				document.body.style.position = '';
				document.body.style.left = '';
				document.body.style.top = '';
				document.body.style.width = '';
				document.body.style.height = '';
				document.body.style.zoom = '';
			}
			// Apply scale
			else {
				document.body.style.position = 'relative';
				document.body.style.left = ( - ( pageOffsetX + elementOffsetX ) / scale ) + 'px';
				document.body.style.top = ( - ( pageOffsetY + elementOffsetY ) / scale ) + 'px';
				document.body.style.width = ( scale * 100 ) + '%';
				document.body.style.height = ( scale * 100 ) + '%';
				document.body.style.zoom = scale;
			}
		}
		this.@org.cruxframework.crux.core.client.animation.Zoom::setLevel(Ljava.lang.Integer;)(scale)
	}-*/;

	/**
	 * Pan the document when the mosue cursor approaches the edges
	 * of the window.
	 */
	public void pan()
	{
		Double range = 0.12;
		Double rangeX = getWindowInnerWidth() * range;
		Double rangeY = getWindowInnerHeight() * range;
		
		// Up
		if( mouseY < rangeY ) {
			Window.scrollTo( getXScrollOffset(), new Double( getYScrollOffset() - ( 1 - ( mouseY / rangeY ) ) * ( 14 / level ) ).intValue());
		}
		// Down
		else if( mouseY > getWindowInnerHeight() - rangeY ) {
			Window.scrollTo( getXScrollOffset(), new Double( getYScrollOffset() + ( 1 - ( getWindowInnerHeight() - mouseY ) / rangeY ) * ( 14 / level ) ).intValue() );
		}
		// Left
		if( mouseX < rangeX ) {
			Window.scrollTo( new Double( getXScrollOffset() - ( 1 - ( mouseX / rangeX ) ) * ( 14 / level ) ).intValue(), getYScrollOffset() );
		}
		// Right
		else if( mouseX > getWindowInnerWidth() - rangeX ) {
			Window.scrollTo( new Double( getXScrollOffset() + ( 1 - ( getWindowInnerWidth() - mouseX ) / rangeX ) * ( 14 / level ) ).intValue(), getYScrollOffset() );
		}
	}

	public void setMouseX(Integer mouseX) 
	{
		this.mouseX = mouseX;
	}

	public Integer getMouseX() 
	{
		return mouseX;
	}
	
	public void setMouseY(Integer mouseY) 
	{
		this.mouseY = mouseY;
	}

	public Integer getMouseY() 
	{
		return mouseY;
	}
	
	public void setLevel(Integer level) 
	{
		this.level = level;
	}
	
	public Integer getLevel() 
	{
		return level;
	}
	
}
