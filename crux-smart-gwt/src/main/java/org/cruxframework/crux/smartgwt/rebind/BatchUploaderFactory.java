package org.cruxframework.crux.smartgwt.rebind;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;

import org.cruxframework.crux.smartgwt.client.BatchUploader;

/**
 * Factory for BatchUploader SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="batchUploader", targetWidget=BatchUploader.class)

@TagAttributes({
	@TagAttribute("cancelConfirmMessage"),
	@TagAttribute("commitConfirmationMessage"),
	@TagAttribute("dataURL"),
	@TagAttribute("defaultDelimiter"),
	@TagAttribute("defaultQuoteString"),
	@TagAttribute("defaultQuoteString"),
	@TagAttribute("partialCommitError"),
	@TagAttribute("partialCommitPrompt"),
	@TagAttribute("uploadButtonTitle"),
	@TagAttribute("uploadFieldPrefix"),
	@TagAttribute("uploadFileLabel"),
	@TagAttribute("uploadOperation")
}) 
	
public class BatchUploaderFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
