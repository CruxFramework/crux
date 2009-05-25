package br.com.sysmap.crux.advanced.collapsepanel;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * ImageBundle for CollapsePanel 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public interface CollapsePanelImages extends ImageBundle
{
	@Resource("/br/com/sysmap/crux/advanced/collapsepanel/expand.gif")
	AbstractImagePrototype expand();
	
	@Resource("/br/com/sysmap/crux/advanced/collapsepanel/collapse.gif")
	AbstractImagePrototype collapse();
}
