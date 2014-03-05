package org.cruxframework.cruxsite.client.accessor;

import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.widgets.client.anchor.Anchor;

public interface ManualAccessor extends WidgetAccessor {
	Anchor manualSinglePageBtn();
	Anchor manualMultiplePageBtn();
	Anchor manualPdfBtn();
	Anchor javadocBtn();
	
}