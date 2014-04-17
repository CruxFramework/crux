package org.cruxframework.cruxsite.client.accessor;

import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.widgets.client.rss.RssPanel;

public interface IndexAccessor extends WidgetAccessor {
	RssPanel lastBlogEntries();

}