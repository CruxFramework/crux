/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.tools.quickstart.client.controller;

import java.util.List;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.tools.quickstart.client.QuickStartMessages;
import org.cruxframework.crux.tools.quickstart.client.dto.DirectoryInfo;
import org.cruxframework.crux.tools.quickstart.client.dto.ProjectInfo;
import org.cruxframework.crux.tools.quickstart.client.remote.QuickStartServiceAsync;
import org.cruxframework.crux.tools.quickstart.client.screen.QuickStartScreen;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;
import org.cruxframework.crux.widgets.client.dialog.ProgressDialog;
import org.cruxframework.crux.widgets.client.event.OkEvent;
import org.cruxframework.crux.widgets.client.event.OkHandler;
import org.cruxframework.crux.widgets.client.rollingpanel.RollingPanel;
import org.cruxframework.crux.widgets.client.transferlist.TransferList.Item;
import org.cruxframework.crux.widgets.client.wizard.WizardControlBar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@Controller("quickStartController")
public class QuickStartController
{
	@Create
	protected QuickStartScreen screen;
	
	@Create
	protected ProjectInfo projectInfo;

	@Create
	protected QuickStartServiceAsync service;
	
	@Create
	protected QuickStartMessages messages;

	private String outputDir;
	
	@Expose
	public void onLoad()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
		service.getProjectInfoDefaultValues(new AsyncCallbackAdapter<ProjectInfo>(this)
		{
			@Override
            public void onComplete(ProjectInfo result)
            {
				projectInfo = result;
				populateProjectLayoutList();
            }
		});
	}
	
	@Expose
	public void loadDirectory()
	{
		loadDirectoryInfo(".");
	}

	/**
	 * @param path
	 */
	private void loadDirectoryInfo(String path)
    {
	    service.getDirectoryInfo(path, new AsyncCallbackAdapter<DirectoryInfo>(this)
		{
			@Override
            public void onComplete(DirectoryInfo result)
            {
				if (result!= null)
				{
					outputDir = result.getFullPath();
					if (outputDir.endsWith("/"))
					{
						outputDir = outputDir.substring(0, outputDir.length()-1);
					}
				}
				else
				{
					outputDir = "";
				}
				if (result.getFullPath().length() > 0) {
					updateDirSelectorRollingPanel(result);
					updateDirSelectorBox(result);
				}
            }
		});
    }	
	
	/**
	 * @param result
	 */
	private void updateDirSelectorRollingPanel(DirectoryInfo result)
	{
		final RollingPanel rollingPanel = screen.getDirSelectorRollingPanel();
		rollingPanel.clear();
		if (result != null)
		{
			boolean needsSeparator = false;


			String[] folders = result.getFullPath().split("/");
			if (folders == null || folders.length ==0)
			{
				folders = new String[]{""};
			}

			for (int i=0; i< folders.length; i++)
			{
				Label separator = new Label();
				separator.setStyleName("FileSystemSeparator");
				if (needsSeparator)
				{
					separator.setText("/");
				}
				rollingPanel.add(separator);
				needsSeparator = (i >= 1) || (i==0 && !folders[0].equals(""));
				final Label label = new Label(folders[i].trim().length()>0?folders[i]:"/");
				label.setStyleName("FileSystemLabel");

				final StringBuilder fullPath = new StringBuilder("");

				for (int j=0; j<=i; j++)
				{
					fullPath.append(folders[j]+"/");
				}

				label.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						loadDirectoryInfo(fullPath.toString());
					}
				});
				rollingPanel.add(label);
			}
		}
	}
	
	/**
	 * @param result
	 */
	private void updateDirSelectorBox(DirectoryInfo result)
    {
        VerticalPanel selectorBox = screen.getDirSelectorBox();
		selectorBox.clear();
		if (result != null)
		{
			if(result.isHasParent())
			{
				addDirectory("..");
			}
			
			for (String item : result.getContents())
			{
				addDirectory(item);
			}
		}
    }
	
	/**
	 * Adds a directory into the selection panel
	 * @param item
	 */
	private void addDirectory(final String item)
	{
		Image icon = new Image("style/img/folder.gif");
		Label label = new Label(item);
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(2);
		panel.add(icon);
		panel.add(label);
		
		FocusPanel clickable = new FocusPanel();
		clickable.setStyleName("DirectoryItem");
		clickable.add(panel);
		clickable.addClickHandler(new ClickHandler()
		{	
			public void onClick(ClickEvent event)
			{
				loadDirectoryInfo(outputDir + "/" + item);				
			}
		});
		
		screen.getDirSelectorBox().add(clickable);
	}
	
	@Expose
	public void finish()
	{
		ProgressDialog.show(messages.waitGeneratingProject());
		
		service.generateProject(projectInfo, new AsyncCallbackAdapter<Boolean>(this)
		{
			@Override
            public void onComplete(Boolean result)
            {
				String title;
				String message;
				
				if (result!= null && result)
				{
					title = messages.generateAppSuccessTitle();
					message = messages.generateAppSuccessMessage();
				}
				else
				{
					title = messages.generateAppFailureTitle();
					message = messages.generateAppFailureMessage();
				}
				
				ProgressDialog.hide();
				
				MessageBox.show(title, message, new OkHandler()
				{
					public void onOk(OkEvent event)
					{
						back();
					}
				});
            }
			
			@Override
			public void onError(Throwable e)
			{
				ProgressDialog.hide();
				super.onError(e);
			}
		});
	}

	@Expose
	public void back()
	{
		Window.Location.assign(Screen.appendDebugParameters("index.html"));
	}	

	@Expose
	public void onSummaryEnter()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(true);
	}	

	@Expose
	public void onSummaryLeave()
	{
		screen.getQuickstartWizard().getControlBar().getCommand(WizardControlBar.FINISH_COMMAND).setEnabled(false);
	}
	
	@Expose
	public void onIdeInfoLeave()
	{
		projectInfo.setWorkspaceDir(outputDir);
	}
	
	@Expose
	public void showProjectFormHelp(ClickEvent evt)
	{
		Image img = (Image) evt.getSource();
		DialogBox dialog = new DialogBox(true);
		dialog.setWidth("300");
		dialog.setWidget(new Label(img.getTitle()));
		dialog.setStyleName("HelpDialog");
		dialog.showRelativeTo(img);
	}	
	
	@Expose
	public void projectLayoutChange()
	{
		String selectedLayout = screen.getProjectLayout().getValue();
		boolean visible = (!StringUtils.isEmpty(selectedLayout) && "MODULE_APP".equals(selectedLayout));
		screen.getProjectInfo().getRowFormatter().setVisible(6, visible);
		boolean gadgetVisible = (!StringUtils.isEmpty(selectedLayout) && "GADGET_APP".equals(selectedLayout));
		screen.getQuickstartWizard().setStepEnabled("gagdetInfoStep", gadgetVisible);
	}
	
	@Expose
	public void onFeaturesChange()
	{
		List<Item> itens = screen.getFeatures().getRightItens();
		if (itens != null)
		{
			StringBuilder str = new StringBuilder();
			boolean needsComma = false;
			for (Item item : itens)
            {
				if (needsComma)
				{
					str.append(",");
				}
				needsComma = true;
	            str.append(item.getValue());
            }
			
			projectInfo.getGadgetInfo().setFeatures(str.toString());
		}
	}

	/**
	 * Populates the project layout listbox.
	 */
	private void populateProjectLayoutList()
	{
		screen.getProjectLayout().clear();
		
		List<String[]> allProjectLayouts = projectInfo.getAllProjectLayouts();
		for (String[] layout : allProjectLayouts)
		{
			screen.getProjectLayout().addItem(layout[0], layout[1]);
		}
	}
}
