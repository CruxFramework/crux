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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.media.client.Audio;

/**
 * Factory for Audio Widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="audio", library="gwt", targetWidget=Audio.class)
@TagAttributes({
	@TagAttribute(value="autoplay", type=Boolean.class, property="audioElement.autoplay"),
	@TagAttribute(value="showControls", type=Boolean.class, property="audioElement.controls"),
	@TagAttribute(value="currentTime", type=Double.class, property="audioElement.currentTime"),
	@TagAttribute(value="defaultPlaybackRate", type=Double.class, property="audioElement.defaultPlaybackRate"),
	@TagAttribute(value="loop", type=Boolean.class, property="audioElement.loop"),
	@TagAttribute(value="muted", type=Boolean.class, property="audioElement.muted"),
	@TagAttribute(value="playbackRate", type=Double.class, property="audioElement.playbackRate"),
	@TagAttribute(value="preload", type=AudioFactory.PreLoadType.class, processor=AudioFactory.PreLoadAttributeProcessor.class),
	@TagAttribute(value="src", type=String.class, property="audioElement.src", supportsResources=true),
	@TagAttribute(value="volume", type=Double.class, property="audioElement.volume")
})
public class AudioFactory extends FocusWidgetFactory<WidgetCreatorContext>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum PreLoadType{auto, metadata, none}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class PreLoadAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public PreLoadAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			PreLoadType preLoadType = PreLoadType.valueOf(attributeValue);
			String mediaElementClass = MediaElement.class.getCanonicalName();
			switch (preLoadType)
            {
	            case auto: out.println(context.getWidget()+".getAudioElement().setPreload("+mediaElementClass+".PRELOAD_AUTO);"); break;
	            case metadata: out.println(context.getWidget()+".getAudioElement().setPreload("+mediaElementClass+".PRELOAD_METADATA);"); break;
	            case none: out.println(context.getWidget()+".getAudioElement().setPreload("+mediaElementClass+".PRELOAD_NONE);"); break;
            }
        }
	}
	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = "+className+".createIfSupported();");
	}
	
}
