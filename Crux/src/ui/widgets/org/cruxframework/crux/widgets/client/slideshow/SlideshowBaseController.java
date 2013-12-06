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
package org.cruxframework.crux.widgets.client.slideshow;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.OrientationChangeHandler;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService.Callback;
import org.cruxframework.crux.widgets.client.slideshow.data.Photo;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class SlideshowBaseController extends DeviceAdaptiveController implements Slideshow, OrientationChangeHandler
{
	protected static final boolean SCALE_IMAGES = true;//TODO make this customizable
	protected static enum SlideshowEvent{AlbumLoaded, PhotoLoaded, StartPlaying, StopPlaying}

	protected SlideshowPhotoPanel photoPanel;
	protected PhotoAlbum album;
	protected DockPanel table;
	protected FastMap<Image> imagesCache;
	protected FastList<SlideshowComponent> components;
	protected int activeImage = -1;
	protected int previousImage = -1;
	protected Timer autoPlayTimer;
	protected boolean preloadNextImages = true;
	protected int transitionDelay = 5000;
	private boolean useLayout = false;
	private AlbumService albumService;
	private DivElement dataURILoader;
	
	/**
	 * 
	 * @param layout
	 */
	public void setLayout(Layout layout)
	{
		table.clear();
		components.clear();
		useLayout = true;
		setPhotoPanel();
		layout.createComponents(this);
		if (album != null)
		{
			notifyComponents(SlideshowEvent.AlbumLoaded);
			if (getActivePhoto() >= 0)
			{
				notifyComponents(SlideshowEvent.PhotoLoaded);
			}
		}
	}

	/**
	 * 
	 */
	public void play()
	{
		if (!isPlaying() && hasMorePhotos())
		{
			autoPlayTimer = new Timer()
			{
				@Override
				public void run()
				{
					if (next())
					{
						if ((getActivePhoto()+1) >= getPhotoCount())
						{
							stop();
						}
					}
					else
					{
						stop();
					}
				}
			};
			autoPlayTimer.schedule(transitionDelay);
			notifyComponents(SlideshowEvent.StartPlaying);
		}
	}
	
	/**
	 * 
	 */
	public void stop()
    {
        if (autoPlayTimer != null)
        {
        	autoPlayTimer.cancel();
        	autoPlayTimer = null;
        	notifyComponents(SlideshowEvent.StopPlaying);
        }
    }

	/**
	 * 
	 * @return
	 */
	public boolean isPlaying()
	{
		return (autoPlayTimer != null);
	}

	/**
	 * 
	 * @return
	 */
	public boolean next()
	{
		if (hasMorePhotos())
		{
			showPhoto(getActivePhoto()+1);
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean previous()
	{
		if (album != null)
		{
			int index = getActivePhoto() - 1;

			if (index >= 0 && index < getPhotoCount())
			{
				showPhoto(index);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param component
	 * @param direction
	 */
	public void addComponent(SlideshowComponent component, Position position)
	{
		if (!useLayout && photoPanel == null)
		{
	    	setPhotoPanel();
		}
		
		DockLayoutConstant direction;
		switch (position)
        {
        	case lineStart: direction = DockPanel.LINE_START; break; 
        	case lineEnd: direction = DockPanel.LINE_END; break;
        	case east: direction = DockPanel.EAST; break;
        	case north: direction = DockPanel.NORTH; break;
        	case south: direction = DockPanel.SOUTH; break;
        	case west: direction = DockPanel.WEST; break;
        	case none: direction = null; break;
	        default: direction = DockPanel.CENTER; break;
        }
		
		if (direction != null)
		{
			table.add(component, direction);
		}
		components.add(component);
		component.setSlideShow(this);
	}

	/**
	 * 
	 * @return
	 */
	public int getTransitionDelay()
    {
    	return transitionDelay;
    }

	/**
	 * 
	 * @param transitionDelay
	 */
	public void setTransitionDelay(int transitionDelay)
    {
    	this.transitionDelay = transitionDelay;
    }

	/**
	 * 
	 * @param component
	 * @param align
	 */
	public void setHorizontalAlignment(SlideshowComponent component, HorizontalAlignmentConstant align)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellHorizontalAlignment(component, align);
	}
	
	/**
	 * 
	 * @param component
	 * @param align
	 */
	public void setVerticalAlignment(SlideshowComponent component, VerticalAlignmentConstant align)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellVerticalAlignment(component, align);
	}
	
	/**
	 * 
	 * @param component
	 * @param height
	 */
	public void setHeight(SlideshowComponent component, String height)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellHeight(component, height);
	}

	/**
	 * 
	 * @param component
	 * @param width
	 */
	public void setWidth(SlideshowComponent component, String width)
	{
		assert(table.getWidgetIndex(component) != -1):"Slideshow does not contains the requested component";
		table.setCellWidth(component, width);
	}

	/**
	 * 
	 * @param album
	 */
	public void setAlbum(PhotoAlbum album)
	{
		reset();
		this.album = album;
		if (album != null)
		{
			notifyComponents(SlideshowEvent.AlbumLoaded);
			showFirstPhoto();
		}
	}

	/**
	 * 
	 */
	public void showFirstPhoto()
    {
		assert(this.album != null):"There is no photo album loaded";
	    FastList<Photo> images = album.getImages();
	    if (images != null)
	    {
	    	if (images.size() > 0)
	    	{
	    		showPhoto(0);
	    	}
	    }
    }

	/**
	 * 
	 * @return
	 */
	public PhotoAlbum getAlbum()
	{
		return album;
	}
	
	/**
	 * 
	 * @param index
	 */
	public void showPhoto(final int index)
    {
		assert(this.album != null):"There is no photo album loaded";
		int photoCount = getPhotoCount();
		if (index != activeImage && index < photoCount && index > -1)
		{
		    String key = Integer.toString(index);
			boolean alreadyLoaded = imagesCache.containsKey(key);
			Image image = loadImage(index, preloadNextImages);
			if (image != null)
			{
				previousImage = activeImage;
				activeImage = index;
				preloadAnNotifyComponents(image, alreadyLoaded);
			}
		}
    }
	
	/**
	 * It is needed to attach data uri images to DOM to ensure that it is fully initialized by the 
	 * browser before it is passed to swapcontainer animate it. Other urls are preloaded when we create the 
	 * image object.
	 * @param image
	 * @param alreadyLoaded
	 */
	private void preloadAnNotifyComponents(final Image image, boolean alreadyLoaded)
	{
		String url = image.getUrl();
		if (!alreadyLoaded && url != null && url.startsWith("data:"))
		{
			Element dataURIImageLoader = getDataURIImageLoader();
			dataURIImageLoader.appendChild(image.getElement());
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					image.getElement().removeFromParent();
					notifyComponents(SlideshowEvent.PhotoLoaded);
				}
			});
		}
		else
		{
			notifyComponents(SlideshowEvent.PhotoLoaded);
		}
	}
	
	private Element getDataURIImageLoader()
    {
		if (dataURILoader == null)
		{
			dataURILoader = Document.get().createDivElement();
			dataURILoader.getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
			dataURILoader.getStyle().setLeft(-10000, Unit.PX);
			dataURILoader.getStyle().setTop(0, Unit.PX);
			dataURILoader.getStyle().setOpacity(0);
			Document.get().getBody().appendChild(dataURILoader);
		}
		return dataURILoader;
    }

	/**
	 * 
	 * @param index
	 * @return
	 */
	public Image loadImage(int index)
	{
		assert(this.album != null):"There is no photo album loaded";
		return loadImage(index, false);
	}
	
	protected void showComponents()
	{
		//Do nothing
	}
	
	/**
	 * 
	 * @return
	 */
	public int getActivePhoto()
	{
		return activeImage;
	}
	
	@Override
	public SlideshowPhotoPanel getPhotoPanel() {
		return photoPanel;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public Photo getPhoto(int index)
	{
		if (index < 0 || album == null || album.getImages() == null || album.getImages().size() <= index)
		{
			return null;
		}
		return album.getImages().get(index);
	}

	/**
	 * 
	 * @return
	 */
	public int getPhotoCount()
	{
		if (album == null || album.getImages() == null)
		{
			return 0;
		}
		return album.getImages().size();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPreloadNextImages()
    {
    	return preloadNextImages;
    }

	/**
	 * 
	 * @param preloadNextImages
	 */
	public void setPreloadNextImages(boolean preloadNextImages)
    {
    	this.preloadNextImages = preloadNextImages;
    }

	/**
	 * 
	 */
	public AlbumService getAlbumService()
	{
		return albumService;
	}
	
	/**
	 * 
	 */
	public void setAlbumService(AlbumService albumService)
	{
		this.albumService = albumService;
		this.albumService.setSlideshow(this);
	}
	
	/**
	 * 
	 */
	public void load(Callback callback)
	{
		assert(albumService != null):"You must initialize slideshow albumService property first."; 
		albumService.loadAlbum(callback);
	}
	
	@Override
    public void onOrientationChange()
    {
		FastList<String> keys = imagesCache.keys();
		
        for (int i = 0; i < keys.size(); i++)
        {
        	String index = keys.get(i);
        	adjustImageSize(photoPanel, getPhoto(Integer.parseInt(index)), imagesCache.get(index));
        }
    }
	
	@Override
	protected void init()
	{
		imagesCache = new FastMap<Image>();
		components = new FastList<SlideshowComponent>();
		table = getChildWidget("table");
		if (SCALE_IMAGES)
		{
			addAttachHandler(new Handler()
			{
				private HandlerRegistration orientationHandlerRegistration;

				@Override
				public void onAttachOrDetach(AttachEvent event)
				{
					if (event.isAttached())
					{
						orientationHandlerRegistration = Screen.addOrientationChangeHandler(SlideshowBaseController.this);
					}
					else if (orientationHandlerRegistration != null)
					{
						orientationHandlerRegistration.removeHandler();
						orientationHandlerRegistration = null;
					}
				}
			});
		}
		setStyleName("crux-Slideshow");
	}

	private void setPhotoPanel()
    {
	    photoPanel = new SlideshowPhotoPanel();
		components.add(photoPanel);
		photoPanel.setSlideShow(this);
		configurePhotoPanel();
    }
	
	public abstract void configurePhotoPanel();

	/**
	 * 
	 * @param index
	 * @param preloadNext
	 * @return
	 */
	protected Image loadImage(int index, boolean preloadNext)
    {
	    String key = Integer.toString(index);
		Image image = imagesCache.get(key);
		if (image == null)
		{
			Photo photo = album.getImages().get(index);
			image = new Image(photo.getUrl());
			adjustImageSize(photoPanel, photo, image);
			imagesCache.put(key, image);
		}
		final int nextIndex = index + 1;
		if (preloadNext && nextIndex < getPhotoCount())
		{
			image.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					loadImage(nextIndex, false);
				}
			});
		}
		
		return image;
    }

	/**
	 * 
	 * @param referencePanel
	 * @param photo
	 * @param image
	 */
    protected void adjustImageSize(Widget referencePanel, Photo photo, Image image)
    {
    	if(photo.getWidth() == 0 || photo.getWidth() == 0)
    	{
    		return;
    	}
    	
		if (SCALE_IMAGES)
		{
			double scaleWidth = referencePanel.getOffsetWidth() / ((double)photo.getWidth());
			double scaleHeight = referencePanel.getOffsetHeight() / ((double)photo.getHeight());

			double scale = Math.min(scaleWidth, scaleHeight);
			if (scale > 0)
			{
				image.setWidth(Math.round(photo.getWidth()*scale)+"px");
				image.setHeight(Math.round(photo.getHeight()*scale)+"px");
			}
		}
		else
		{
			image.setWidth(photo.getWidth()+"px");
			image.setHeight(photo.getHeight()+"px");
		}
    }
	
	/**
	 * 
	 * @return
	 */
	protected boolean hasMorePhotos()
	{
		if (album != null)
		{
			int index = getActivePhoto() + 1;

			if (index < getPhotoCount())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param event
	 */
	protected void notifyComponents(SlideshowEvent event)
    {
	    for (int i = 0; i < components.size(); i++)
	    {
	    	SlideshowComponent component = components.get(i);
	    	switch (event)
            {
	            case AlbumLoaded:
	            	component.onAlbumLoaded();
	            break;
	            case PhotoLoaded:
	            	component.onPhotoLoaded(previousImage, activeImage);
	            break;
	            case StartPlaying:
	            	component.onStartPlaying();
	            break;
	            case StopPlaying:
	            	component.onStopPlaying();
	            break;
            }
	    }
    }

	protected void reset()
    {
	    imagesCache.clear();
		activeImage = -1;
		previousImage = -1;
	}
}
