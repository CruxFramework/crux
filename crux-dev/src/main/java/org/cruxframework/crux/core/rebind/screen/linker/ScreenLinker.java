/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.linker;

import java.util.SortedSet;

import org.cruxframework.crux.core.utils.StreamUtils;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@LinkerOrder(Order.PRE)
@Shardable
public class ScreenLinker extends AbstractLinker 
{

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation) throws UnableToCompleteException
	{
		if (!onePermutation)
		{
			SortedSet<EmittedArtifact> resources = artifacts.find(EmittedArtifact.class);

			for (EmittedArtifact resource: resources)
			{
				if (resource.getVisibility().equals(Visibility.Private) && 
						resource.getPartialPath().endsWith(".crux.xml"))
				{
					try
					{
						String resourcePath = resource.getPartialPath().replace(".crux.xml", ".html");
						String content = StreamUtils.readAsUTF8(resource.getContents(logger));
						EmittedArtifact manifest = emitString(logger, content, "../"+resourcePath);
						artifacts.add(manifest);
					}
					catch(Exception e)
					{
						logger.log(Type.ERROR, "Error generating HTML for crux screen ["+resource.getPartialPath()+"]", e);
						throw new UnableToCompleteException();
					}
				}
			}
		}

		return super.link(logger, context, artifacts, onePermutation);
	}

	@Override
	public String getDescription()
	{
		return "Generate the html host files for crux screens.";
	}
}
