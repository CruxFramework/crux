package org.cruxframework.cruxdevtools.crudgenerator.templates;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.widgets.client.maskedtextbox.MaskedTextBox;
import org.cruxframework.crux.widgets.client.select.SingleSelect;
import org.cruxframework.cruxdevtools.crudgenerator.Template;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class BaseControllerTemplate extends Template
{
	protected Set<String> imports = new HashSet<String>();
	protected final String packageName;

	public BaseControllerTemplate(File outputDir, String packageName)
	{
		super(null, outputDir);
		this.packageName = packageName;
	}

	@Override
	protected String getTemplateFile()
	{
		return "/"+BaseControllerTemplate.class.getPackage().getName().replace('.', '/')+"/base-controller-template.vm";
	}

	@Override
	protected File getOutputFile()
	{
		return new File(getOutputDir()+"/"+packageName.replace('.', '/'), getTypeName()+".java");
	}

	@Override
	protected Map<String, Object> getContextMap()
	{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("packageName", packageName);
		context.put("className", getTypeName());
        context.put("imports", imports);

        imports.add(Window.class.getCanonicalName());
        imports.add(Expose.class.getCanonicalName());
        imports.add(Map.class.getCanonicalName());
        imports.add(Map.Entry.class.getCanonicalName());
        imports.add(Widget.class.getCanonicalName());
        imports.add(SingleSelect.class.getCanonicalName());
        imports.add(TextBox.class.getCanonicalName());
        imports.add(MaskedTextBox.class.getCanonicalName());
        imports.add(TextArea.class.getCanonicalName());
        imports.add(DateBox.class.getCanonicalName());

		return context;
	}

	public String getTypeName()
	{
		return "BaseController";
	}
}
