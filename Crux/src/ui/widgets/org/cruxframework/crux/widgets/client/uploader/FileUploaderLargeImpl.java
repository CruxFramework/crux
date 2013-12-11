/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.uploader;

import org.cruxframework.crux.widgets.client.progressbar.ProgressBar;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;

/**
 * An HTML5 based file uploader widget.
 * 
 * @author Thiago da Rosa de Bustamante
 */
class FileUploaderLargeImpl extends AbstractFileUploader
{
	private FocusPanel  dropArea;

	@Override
	protected void initializeWidgets()
    {
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("crux-FileUploader");
		filesPanel = initFilesPanel();
		if (DropEvent.isSupported())
		{
			dropArea = initDropArea();
			mainPanel.add(dropArea);
			dropArea.add(filesPanel);
		}
		else
		{
			mainPanel.add(filesPanel);
		}
		FlowPanel buttonsPanel = initButtonsPanel();
		mainPanel.add(buttonsPanel);
		FlowPanel clearDiv = new FlowPanel();
		clearDiv.getElement().getStyle().setProperty("clear", "both");
		mainPanel.add(clearDiv);
    }

	protected FocusPanel initDropArea()
	{
		final FocusPanel dropArea = new FocusPanel();
		dropArea.setStyleName("dropArea");
		dropArea.addDragOverHandler(new DragOverHandler()
		{
			@Override
			public void onDragOver(DragOverEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.addStyleDependentName("hover");
			}
		});
		dropArea.addDragLeaveHandler(new DragLeaveHandler()
		{
			@Override
			public void onDragLeave(DragLeaveEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
			}
		});
		dropArea.addDropHandler(new DropHandler()
		{
			@Override
			public void onDrop(DropEvent event)
			{
				event.stopPropagation();
				event.preventDefault();
				dropArea.removeStyleDependentName("hover");
				processFiles(event.getNativeEvent());
			}
		});
		return dropArea;
	}

	@Override
	protected void onSelectFile(ChangeEvent event)
	{
	    super.onSelectFile(event);
	    if (dropArea != null)
	    {
	    	dropArea.removeStyleDependentName("hover");
	    }
	}
	
	protected ProgressBar createProgressBar()
	{
		ProgressBar progressBar = super.createProgressBar();
		progressBar.getElement().getStyle().setFloat(Float.RIGHT);
		return progressBar;
	}
}
