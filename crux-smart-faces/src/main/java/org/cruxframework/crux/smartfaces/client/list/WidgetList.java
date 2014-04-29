package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.datasource.pager.AbstractPageable;
import org.cruxframework.crux.core.client.factory.WidgetFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A list of widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetList<T> extends AbstractPageable<T>
{
	private static final String DEFAULT_STYLE_NAME = "faces-WidgetList";
	
	private FlowPanel contentPanel = new FlowPanel();
	private final WidgetFactory<T> widgetFactory;

	/**
	 * Constructor
	 * @param widgetFactory
	 */
	public WidgetList(WidgetFactory<T> widgetFactory)
    {
		assert(widgetFactory != null);
		this.widgetFactory = widgetFactory;
		initWidget(contentPanel);
		setStyleName(DEFAULT_STYLE_NAME);
    }
	
	@Override
	public void reset(boolean reloadData)
	{
		contentPanel.clear();
		super.reset(reloadData);
	}
	
	@Override
	protected AbstractPageable.Renderer<T> getRenderer()
	{
	    return new AbstractPageable.Renderer<T>(){
			@Override
            public void render(T value)
            {
				IsWidget widget = widgetFactory.createWidget(dataSource.getBoundObject());
				contentPanel.add(widget);
            }
	    };
	}
}
