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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService.Callback;
import org.cruxframework.crux.widgets.client.slideshow.data.Photo;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Image;

//TODO: implementar um icone de carregando enquando baixa uma foto
//TODO: criar css para esse componente (pedir ao Junior)
//TODO: criar componente para exibicao do sub-titulo(descricao) do album

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="slideshowTv", device=Device.largeDisplayArrows),
	@Template(name="slideshowKeyboard", device=Device.largeDisplayMouse),
	@Template(name="slideshowKeyboard", device=Device.smallDisplayArrows),
	@Template(name="slideshowTouch", device=Device.largeDisplayTouch),
	@Template(name="slideshowTouch", device=Device.smallDisplayTouch)
})
public interface Slideshow extends DeviceAdaptive
{
	public static enum Position{lineStart, lineEnd, east, north, south, west, none}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Name 
	{
		String value();
	}

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface Layout
	{
		void createComponents(Slideshow slideshow);
	}
	
	void play();
	void stop();
	boolean isPlaying();
	boolean next();
	boolean previous();
	void addComponent(SlideshowComponent component, Position position);
	int getTransitionDelay();
	void setTransitionDelay(int transitionDelay);
	void setHorizontalAlignment(SlideshowComponent component, HorizontalAlignmentConstant align);
	void setVerticalAlignment(SlideshowComponent component, VerticalAlignmentConstant align);
	void setHeight(SlideshowComponent component, String height);
	void setWidth(SlideshowComponent component, String width);
	PhotoAlbum getAlbum();
	void setAlbum(PhotoAlbum album);
	void showFirstPhoto();
	void showPhoto(final int index);
	Image loadImage(int index);
	int getActivePhoto();
	Photo getPhoto(int index);
	int getPhotoCount();
	boolean isPreloadNextImages();
	void setPreloadNextImages(boolean preloadNextImages);
	void setLayout(Layout layout);
	AlbumService getAlbumService();
	void setAlbumService(AlbumService albumService);
	void load(Callback callback);
	SlideshowPhotoPanel getPhotoPanel();
	boolean isScaleImages(); 
	void setScaleImages(boolean scaleImages); 
}