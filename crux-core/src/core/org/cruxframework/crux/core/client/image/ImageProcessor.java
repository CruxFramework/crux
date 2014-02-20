/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.image;

import org.cruxframework.crux.core.client.file.Blob;
import org.cruxframework.crux.core.client.file.FileReader;
import org.cruxframework.crux.core.client.file.URL;
import org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback;
import org.cruxframework.crux.core.client.utils.FileUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.PartialSupport;

/**
 * An Image processor 
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public class ImageProcessor
{
	private NativeImage image;
	private CanvasElement canvas;

	/**
	 * Constructor
	 */
	protected ImageProcessor(){}
	
	/**
	 * Create ImageProcessor instance and automatically loads the image. 
	 * @param file Image File
	 * @param handler Called whem image is completely loaded
	 */
	public static void createIfSupportedAndLoadImage(final Blob image, final ImageCreateAndLoadHandler handler)
	{
		ImageProcessor imageProcessor = createIfSupported();
		if(imageProcessor == null) 
		{
			handler.onNotSupported();
			return;
		}
		imageProcessor.loadImage(image, handler);
	}
	
	/**
	 * Create ImageProcessor instance and automatically loads the image.
	 * @param url Image URL
	 * @param handler Called whem image is completely loaded
	 */
	public static void createIfSupportedAndLoadImage(String url, final ImageCreateAndLoadHandler handler)
	{
		ImageProcessor imageProcessor = createIfSupported();
		if(imageProcessor == null) 
		{
			handler.onNotSupported();
			return;
		}
		imageProcessor.loadImage(url, handler);
	}
	
	/**
	 * Loads an image to the processor.
	 * @param url Image URL
	 * @param handler Called whem image is completely loaded
	 */
	public void loadImage(String url, final ImageLoadHandler handler)
	{
		this.canvas = null;
		this.image = null;
		final NativeImage newImage = NativeImage.create();
		newImage.setLoadHandler(new ImageLoadHandler()
		{
			@Override
			public void onLoad(ImageProcessor processor)
			{
				image = newImage;
				if (handler != null)
				{
					handler.onLoad(processor);
				}
			}
			
			@Override
			public void onError()
			{
				if (handler != null)
				{
					handler.onError();
				}
			}
		}, this);
		newImage.setSrc(url);
	}
	
	/**
	 * Loads an image to the processor.
	 * @param file Image File
	 * @param handler Called whem image is completely loaded
	 */
	public void loadImage(final Blob image, final ImageLoadHandler handler)
	{
		if (URL.isSupported())
		{
			loadImage(URL.createObjectURL(image), handler);
		}
		else
		{
			FileReader.createIfSupported().readAsDataURL(image, new ReaderStringCallback()
			{
				@Override
				public void onComplete(String result)
				{
					loadImage(result, handler);
				}
			});
		}
	}
	
	/**
	 * Ensure a max size for the image
	 * @param maxHeight
	 * @param maxWidth
	 * @param keepProportions
	 */
	public void ensureMaxSize(int maxHeight, int maxWidth, boolean keepProportions)
	{
		assert (image != null || canvas != null) : "You must load an image first";
		
		int width = (image != null?image.getWidth():canvas.getWidth());
		int height = (image != null?image.getHeight():canvas.getHeight());

		if (keepProportions)
		{
			if (width > height)
			{
				if (width > maxWidth)
				{
					height = Math.round(height *= maxWidth / (double) width);
					width = maxWidth;
				}
			}
			else
			{
				if (height > maxHeight)
				{
					width = Math.round(width *= maxHeight / (double) height);
					height = maxHeight;
				}
			}
		}
		else
		{
			width = Math.min(width, maxWidth);
			height = Math.min(height, maxHeight);
		}
		resize(width, height);
	}

	/**
	 * Resize the internal image
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height)
    {
		assert (image != null || this.canvas != null) : "You must load an image first";
		
		CanvasElement canvas = Document.get().createCanvasElement().cast();
		canvas.setWidth(width);
		canvas.setHeight(height);
		if (image != null)
		{
			canvas.getContext2d().drawImage(image.asImageElement(), 0, 0, width, height);
		}
		else
		{
			canvas.getContext2d().drawImage(this.canvas, 0, 0, this.canvas.getWidth(), this.canvas.getHeight(), 0, 0, width, height);
		}
		
		this.canvas = canvas;
		image = null;
    }
	
	/**
	 * Export content image as a JPEG file.
	 * @param quality JPEG quality. It ranges from 0.0 to 1.0
	 * @return
	 */
	public Blob asJpeg(double quality)
	{
		assert (image != null || this.canvas != null) : "You must load an image first";
		
		if (image != null)
		{
			resize(image.getWidth(), image.getHeight());
		}
		return FileUtils.fromDataURI(toJpegURL(canvas, quality));
	}
	
	private native String toJpegURL(CanvasElement canvas, double quality)/*-{
		return canvas.toDataURL("image/jpeg", quality);
	}-*/;
	
	/**
	 * Export content image as a PNG file.
	 * @return
	 */
	public Blob asPng()
	{
		return exportImage("image/png");
	}

	/**
	 * Export content image as a GIF file.
	 * @return
	 */
	public Blob asGif()
	{
		return exportImage("image/gif");
	}

	private Blob exportImage(String imageType)
    {
	    assert (image != null || this.canvas != null) : "You must load an image first";
		
		if (image != null)
		{
			resize(image.getWidth(), image.getHeight());
		}
		return FileUtils.fromDataURI(canvas.toDataUrl(imageType));
    }

	/**
	 * Check if browser supports this feature
	 * @return
	 */
	public static boolean isSupported()
	{
		return Blob.isSupported() && FileReader.isSupported();
	}
	
	/**
	 * If the current browser supports, create a new ImageProcessor
	 * @return
	 */
	public static ImageProcessor createIfSupported()
	{
		if (isSupported())
		{
			return new ImageProcessor();
		}
		return null;
	}
	
	static class NativeImage extends JavaScriptObject
	{
		protected NativeImage(){}
		
		public final native ImageElement asImageElement()/*-{
	        return this;
        }-*/;

		public final native int getWidth()/*-{
			return this.width;
		}-*/;

		public final native int getHeight()/*-{
			return this.height;
		}-*/;

		public final native void setSrc(String url)/*-{
			this.src = url;
		}-*/;

		public final native void setLoadHandler(ImageLoadHandler handler, ImageProcessor processor)/*-{
			this.onload = function(){
				handler.@org.cruxframework.crux.core.client.image.ImageProcessor.ImageLoadHandler::onLoad(Lorg/cruxframework/crux/core/client/image/ImageProcessor;)(processor);
			};
			this.onerror = function(){
				handler.@org.cruxframework.crux.core.client.image.ImageProcessor.ImageLoadHandler::onError()();
			};
		}-*/;
		
		public static native NativeImage create()/*-{
			return new Image();
		}-*/;
	}
	
	public static interface ImageCreateAndLoadHandler extends ImageLoadHandler  
	{
		void onNotSupported();
	}
	
	public static interface ImageLoadHandler 
	{
		void onLoad(ImageProcessor processor);
		void onError();
	}
}
