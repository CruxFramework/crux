package ${package}.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.BindView;
import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.widgets.client.swapcontainer.HorizontalSwapContainer;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Controller("homeController")
public class HomeController 
{
	@Inject
	public InputView inputView;
	
	@Inject
	public OutputView outputView;
	
	@Expose
	public void onLoad()
	{
		HorizontalSwapContainer views = (HorizontalSwapContainer) Screen.get("views");
		views.showView("input");
	}
	
	@Expose
	public void sayHello()
	{
		HorizontalSwapContainer views = (HorizontalSwapContainer) Screen.get("views");
		views.showView("output");
		String name = inputView.nameTextBox().getValue();
		name = StringUtils.isEmpty(name) ? "Annonymous" : name;
		outputView.helloLabel().setText("Hello, " + name + "!");
	}
	
	@Expose
	public void back()
	{
		HorizontalSwapContainer views = (HorizontalSwapContainer) Screen.get("views");
		views.showView("input", Direction.BACKWARDS);
	}
	
	@BindView("input")
	public static interface InputView extends WidgetAccessor
	{
		TextBox nameTextBox();
	}
	
	@BindView("output")
	public static interface OutputView extends WidgetAccessor
	{
		Label helloLabel();
	}
}
