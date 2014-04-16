package org.cruxframework.crux.smartgwt.rebind.chart;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.chart.FacetChart;

/**
 * Factory for FacetChart SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="facetChart", targetWidget=FacetChart.class)

@TagAttributes({
	@TagAttribute("highErrorMetric"),
	@TagAttribute("lowErrorMetric"),
	@TagAttribute("probabilityMetric"),
	@TagAttribute("styleName"),
	@TagAttribute("title"),
	@TagAttribute("valueProperty"),
	@TagAttribute("valueTitle"),	
	@TagAttribute("xAxisMetric"),	
	@TagAttribute("yAxisMetric")
}) 

public class FacetChartFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
