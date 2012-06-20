package org.cruxframework.crux.crossdevice.client.labeledtextbox;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */

@Templates({
	@Template(name="labeledTextBoxLarge", device=Device.all),
	@Template(name="labeledTextBoxLargeTouch", device=Device.largeDisplayTouch),
	@Template(name="labeledTextBoxLargeMouse", device=Device.largeDisplayMouse),
	@Template(name="labeledTextBoxSmall", device=Device.smallDisplayArrows),
	@Template(name="labeledTextBoxSmallTouch", device=Device.smallDisplayTouch)
})
public interface LabeledTextBox extends DeviceAdaptive, HasText, HasValue<String>, HasEnabled, HasName, Focusable, HasAllFocusHandlers, HasAllMouseHandlers, HasAllKeyHandlers
{

	void setMaxLength(int maxLength);
	int getMaxLength();

	void setReadOnly(boolean readOnly);
	boolean isReadOnly();

	void setPlaceholder(String placeholder);
	String getPlaceholder();

}
