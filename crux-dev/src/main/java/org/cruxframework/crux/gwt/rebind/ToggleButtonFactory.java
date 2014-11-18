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

import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.ToggleButton;

/**
 * A Factory for ToggleButton widgets
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="toggleButton", library="gwt", targetWidget=ToggleButton.class)
@TagAttributes({
	@TagAttribute(value="down", type=Boolean.class)
})
@TagChildren({
	@TagChild(ToggleButtonFactory.FacesProcessor.class)
})
public class ToggleButtonFactory extends CustomButtonFactory 
{
	@TagConstraints(minOccurs="0", maxOccurs="6")
	@TagChildren({
		@TagChild(UpFaceProcessor.class),
		@TagChild(UpDisabledFaceProcessor.class),
		@TagChild(UpHoveringFaceProcessor.class),
		@TagChild(DownFaceProcessor.class),
		@TagChild(DownDisabledFaceProcessor.class),
		@TagChild(DownHoveringFaceProcessor.class)
	})
	public static class FacesProcessor extends ChoiceChildProcessor<CustomButtonContext> {}
	
	@TagChildren({
		@TagChild(TextFaceProcessor.class),
		@TagChild(HTMLFaceProcessor.class),
		@TagChild(ImageFaceProcessor.class)
	})
	public static class FaceChildrenProcessor extends ChoiceChildProcessor<CustomButtonContext> {}

	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class UpFaceProcessor extends AbstractUpFaceProcessor {}
	
	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class UpDisabledFaceProcessor extends AbstractUpDisabledFaceProcessor {}

	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class UpHoveringFaceProcessor extends AbstractUpHoveringFaceProcessor {}

	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class DownFaceProcessor extends AbstractDownFaceProcessor {}

	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class DownDisabledFaceProcessor extends AbstractDownDisabledFaceProcessor {}

	@TagChildren({
		@TagChild(FaceChildrenProcessor.class)
	})
	public static class DownHoveringFaceProcessor extends AbstractDownHoveringFaceProcessor {}
	
	public static class TextFaceProcessor extends AbstractTextFaceProcessor {}
	public static class HTMLFaceProcessor extends AbstractHTMLFaceProcessor {}
	public static class ImageFaceProcessor extends AbstractImageFaceProcessor {}	
}
