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
import org.cruxframework.crux.core.client.file.FileReader.ReaderStringCallback;
import org.cruxframework.crux.core.client.file.URL;
import org.cruxframework.crux.core.client.utils.FileUtils;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.PartialSupport;

/**
 * An Image processor 
 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso
 */
@PartialSupport
public class ImageProcessor
{
	private NativeImage image;
	private CanvasElement canvas = Document.get().createCanvasElement().cast();

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
		final NativeImage newImage = NativeImage.create();
		newImage.setLoadHandler(new ImageLoadHandler()
		{
			@Override
			public void onLoad(ImageProcessor processor)
			{
				loadImageToCanvas(newImage, newImage.getWidth(), newImage.getHeight());
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
	 * Rotates an image to a certanly degree.
	 * @param degrees the degree of rotation. sample values: 90, 180, 270.
	 */
	public void rotate(int degrees)
	{
		Context2d context = canvas.getContext2d();
		
		//clear canvas
		canvas.getContext2d().clearRect(0,0,canvas.getWidth(), canvas.getHeight());

	    // move to the center of the canvas
	    context.translate(canvas.getWidth()/2,canvas.getHeight()/2);

	    // rotate the canvas to the specified degrees
	    context.rotate(degrees*Math.PI/180);

	    // draw the image
	    // since the context is rotated, the image will be rotated also
	    context.drawImage(image.asImageElement(),-image.getWidth()/2,image.getHeight()/2);
		
	    //restore previous rotation
	    context.rotate(-degrees*Math.PI/180);
	    
	    //translate back the image
	    context.translate(-canvas.getWidth()/2,-canvas.getHeight()/2);
	}
	
	private void loadImageToCanvas(NativeImage image, int width, int height)
	{
		this.image = image;
		canvas.setWidth(width);
		canvas.setHeight(height);
		canvas.getContext2d().drawImage(image.asImageElement(), 0, 0, width, height);
	}
	
	/**
	 * Undo the last operation: resize, rotate and so on.
	 */
	public void undoLastOperation()
	{
		canvas.getContext2d().restore();
	}
	
	/**
	 * Save the state of the operation: resize, rotate and so on.
	 */
	public void saveOperation()
	{
		canvas.getContext2d().save();
	}
	
	/**
	 * Loads an image to the processor.
	 * @param file Image File
	 * @param handler Called when image is completely loaded
	 */
	public void loadImage(final Blob image, final ImageLoadHandler handler)
	{
		if(FileReader.isSupported())
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
		else if (URL.isSupported())
		{
			loadImage(URL.createObjectURL(image), handler);
		} 
		else
		{
			handler.onError();
		}
	}
	
	/**
	 * @return true if the image is a portrait image.
	 */
	public boolean isPortrait()
	{
		if (canvas.getWidth() > canvas.getHeight())
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * @return true if the image is a landscape image.
	 */
	public boolean isLandscape()
	{
		return !isPortrait();
	}
	
	/**
	 * @return the image width.
	 */
	public int getWidth()
	{
		if(canvas != null)
		{
			return canvas.getWidth();
		}
		
		return 0;
	}

	/**
	 * @return the image height.
	 */
	public int getHeight()
	{
		if(canvas != null)
		{
			return canvas.getHeight();
		}
		
		return 0;
	}
	
	/**
	 * Ensure a max size for the image
	 * @param maxHeight
	 * @param maxWidth
	 * @param keepProportions
	 */
	public void ensureMaxSize(int maxWidth, int maxHeight, boolean keepProportions)
	{
		int width = canvas.getWidth();
		int height = canvas.getHeight();

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
		CanvasElement canvas = Document.get().createCanvasElement().cast();
		canvas.setWidth(width);
		canvas.setHeight(height);
		canvas.getContext2d().drawImage(this.canvas, 0, 0, this.canvas.getWidth(), this.canvas.getHeight(), 0, 0, width, height);
		this.canvas = canvas;
    }

	/**
	 * Export content image as a JPEG file.
	 * @param quality JPEG quality. It ranges from 0.0 to 1.0
	 * @return
	 */
	public Blob asJpeg(double quality)
	{
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
	
	public static class NativeImage extends JavaScriptObject
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

	/**
	 * @return the canvas.
	 */
	public CanvasElement getCanvas()
	{
		return canvas;
	}

	public NativeImage getImage()
	{
		return image;
	}
}
