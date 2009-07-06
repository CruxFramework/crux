package br.com.sysmap.crux.advanced.client.dynatabs;
import com.google.gwt.user.client.ui.Composite;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
class FlapPanel extends Composite
{
	private SimpleDecoratedPanel panel;
	private FlapController flapController;	
	
	/**
	 * @param tabs
	 * @param tabId
	 * @param tabLabel
	 * @param closeable
	 */
	public FlapPanel(DynaTabs tabs, String tabId, String tabLabel, boolean closeable)
	{
		panel = new SimpleDecoratedPanel();
		flapController = new FlapController(tabs, tabId, tabLabel, closeable);
		panel.setContentWidget(flapController);
		initWidget(panel);	
	}

	/**
	 * @return the flapController
	 */
	public FlapController getFlapController()
	{
		return flapController;
	}
}