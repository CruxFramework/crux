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
package org.cruxframework.crux.tools.parameters;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.utils.StringUtils;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConsoleParametersProcessor
{
	private Map<String, ConsoleParameter> supportedParameters = new HashMap<String, ConsoleParameter>();
	private List<ConsoleParameter> orderedParameters = new ArrayList<ConsoleParameter>();
	private Set<String> requiredParameters = new HashSet<String>();
	private LinkedList<ConsoleParameter> orderedNoFlagsParameters = new LinkedList<ConsoleParameter>();
	private final PrintStream out;
	private final String programName;
	private final boolean printErrors;
	private int maxParameterNameLength = 0;
	
	
	/**
	 * @param programName
	 */
	public ConsoleParametersProcessor(String programName)
	{
		this(programName, System.out, true);
	}
	
	/**
	 * @param programName
	 */
	public ConsoleParametersProcessor(String programName, PrintStream out, boolean printErrors)
	{
		this.out = out;
		this.programName = programName;
		this.printErrors = printErrors;
	}

	/**
	 * @param parameter
	 */
	public void addSupportedParameter(ConsoleParameter parameter)
	{
		supportedParameters.put(parameter.getName(), parameter);
		orderedParameters.add(parameter);
		if (parameter.isRequired())
		{
			requiredParameters.add(parameter.getName());
		}
		if (!parameter.isFlagParameter())
		{
			orderedNoFlagsParameters.add(parameter);
		}
		this.maxParameterNameLength = Math.max(parameter.getName().length(), this.maxParameterNameLength);
	}
	
	/**
	 * @param out
	 */
	public void showsUsageScreen()
	{
		showCommandLineDescription();
		out.println("--------------------");
		out.println("Program parameters:"); 
		for (ConsoleParameter parameter : orderedParameters)
		{
			String name = parameter.getName();
			out.println(StringUtils.rpad(name, this.maxParameterNameLength+2, ' ') + "- "+parameter.getDescription());
		}
	}
	
	/**
	 * @param commandLineArgs
	 * @return
	 */
	public Map<String, ConsoleParameter> processConsoleParameters(String[] commandLineArgs)
	{
		Map<String, ConsoleParameter> parameters = new HashMap<String, ConsoleParameter>();
		
		try
		{
			Set<String> processedParameters = new HashSet<String>();
			
			if (commandLineArgs != null)
			{
				for (int i=0; i < commandLineArgs.length; i++)
				{
					String cmd = commandLineArgs[i];
					if (!StringUtils.isEmpty(cmd))
					{
						ConsoleParameter parameter;
						if (supportedParameters.containsKey(cmd))
						{
							parameter = (ConsoleParameter) supportedParameters.get(cmd).clone();

							if (parameter.isFlagParameter())
							{
								i = processFlagParameter(commandLineArgs, parameters, processedParameters, i, cmd, parameter);
							}
							else
							{
								parameter = processNonFlagParameter(processedParameters, cmd);
								parameters.put(parameter.getName(), parameter);
							}
						}
						else
						{
							parameter = processNonFlagParameter(processedParameters, cmd);
							parameters.put(parameter.getName(), parameter);
						}
					}
				}
			}
			
			checkRequiredParameters(processedParameters);
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return parameters;
	}

	/**
	 * @param processedParameters
	 */
	private void checkRequiredParameters(Set<String> processedParameters)
	{
		Iterator<String> requiredParams = requiredParameters.iterator();
		while (requiredParams.hasNext())
		{
			String par = requiredParams.next();
			if (!processedParameters.contains(par))
			{
				handleProcessingError("Required Parameter <"+par+"> not found.");
			}
		}
	}

	/**
	 * @param processedParameters
	 * @param cmd
	 * @throws CloneNotSupportedException
	 */
	private ConsoleParameter processNonFlagParameter(Set<String> processedParameters, String cmd) throws CloneNotSupportedException
	{
		if (orderedNoFlagsParameters.size()>0)
		{
			ConsoleParameter parameter = (ConsoleParameter) orderedNoFlagsParameters.removeFirst().clone();
			if (parameter == null)
			{
				handleProcessingError("Invalid Parameter found: "+cmd+".");

			}
			parameter.setValues(new String[]{cmd});
			processedParameters.add(parameter.getName());
			return parameter;
		}
		else
		{
			handleProcessingError("No more parameter expected. Found :"+cmd);
			return null;
		}
	}

	/**
	 * @param commandLineArgs
	 * @param parameters
	 * @param processedParameters
	 * @param i
	 * @param cmd
	 * @param parameter
	 * @return
	 */
	private int processFlagParameter(String[] commandLineArgs, Map<String, ConsoleParameter> parameters, Set<String> processedParameters, int i, String cmd, ConsoleParameter parameter)
	{
		if (processedParameters.contains(cmd))
		{
			handleProcessingError("Duplicated Parameter found: "+cmd+".");
		}

		parameters.put(parameter.getName(), parameter);

		i = processParameterOptions(commandLineArgs, i, parameter);

		processedParameters.add(cmd);
		return i;
	}

	/**
	 * @param commandLineArgs
	 * @param actualIndex
	 * @param parameter
	 * @return
	 */
	private int processParameterOptions(String[] commandLineArgs, int actualIndex, ConsoleParameter parameter)
	{
		if (parameter.hasOptions())
		{
			Iterator<ConsoleParameterOption> options = parameter.iterateOptions();
			List<String> values = new ArrayList<String>();
			while (options.hasNext())
			{
				ConsoleParameterOption option = options.next();
				if (actualIndex == commandLineArgs.length -1)
				{
					handleProcessingError("Required Parameter option not found: "+option.getName()+".");
				}
				String value = commandLineArgs[++actualIndex];
				if (StringUtils.isEmpty(value))
				{
					handleProcessingError("Required Parameter option not found: "+option.getName()+".");
				}
				values.add(value);
			}
			parameter.setValues(values.toArray(new String[values.size()]));
		}
		return actualIndex;
	}
	
	/**
	 * 
	 */
	private void showCommandLineDescription()
	{
		out.print("Usage: "+programName+ " "); 
		
		boolean first = true;
		for (ConsoleParameter parameter : orderedParameters)
		{
			if (!first)
			{
				out.print(" ");
			}
			
			if (parameter.isRequired())
			{
				out.print(parameter.getName());
				if (parameter.hasOptions())
				{
					out.print(" "+getParameterOptionsString(parameter));
				}
			}
			else
			{
				out.print("["+parameter.getName());
				if (parameter.hasOptions())
				{
					out.print(" "+getParameterOptionsString(parameter));
				}
				out.print("]");
			}
			first = false;
		}
		out.println();
	}

	/**
	 * @param parameter
	 * @return
	 */
	private String getParameterOptionsString(ConsoleParameter parameter)
	{
		if (!parameter.hasOptions())
		{
			return "";
		}
		StringBuilder builder = new StringBuilder("<");

		Iterator<ConsoleParameterOption> options = parameter.iterateOptions();
		boolean first = true;
		while (options.hasNext())
		{
			ConsoleParameterOption option = options.next();
			
			if (!first)
			{
				builder.append(",");
			}

			builder.append(option.getName());
			first = false;
		}
		builder.append(">");
		
		return builder.toString();
	}

	/**
	 * @param cmd
	 */
	private void handleProcessingError(String message)
	{
		if (printErrors)
		{
			out.println(message); 
			out.println();
			showsUsageScreen();
		}
		
		throw new ConsoleParametersProcessingException(message);
	}
	
	public static void main(String[] args)
	{
		ConsoleParametersProcessor processor = new ConsoleParametersProcessor("meuPrograma");
		
		processor.addSupportedParameter(new ConsoleParameter("par1", "Este é o parâmetro 1 lalala ksdasd eras"));
		processor.addSupportedParameter(new ConsoleParameter("-par2", "Este é o parâmetro 2 acas", true, true));
		processor.addSupportedParameter(new ConsoleParameter("par3", "Este é o parâmetro 3  asdas dsad asd sad sadsadsad", false, false));

		ConsoleParameter parameter = new ConsoleParameter("-par4", "Este é o parâmetro 4 s", true, true);
		parameter.addParameterOption(new ConsoleParameterOption("option1", "Lalalalala asd sad asd "));
		parameter.addParameterOption(new ConsoleParameterOption("option2", "Lalalalala"));
		processor.addSupportedParameter(parameter);
		
		processor.showsUsageScreen();
		System.out.println();
		System.out.println();
		String[] params = "-par4 lalala par1  par1 -par3 -par2 ".split(" ");
		Map<String, ConsoleParameter> parameters = processor.processConsoleParameters(params);
		
		for (ConsoleParameter consoleParameter : parameters.values())
		{
			System.out.println("parameter accepted: "+ consoleParameter.getName());
			if (consoleParameter.hasOptions())
			{
				Iterator<ConsoleParameterOption> options = consoleParameter.iterateOptions();
				int i=0;
				while (options.hasNext())
				{
					System.out.println("	Parameter Option <"+options.next().getName()+">: " + consoleParameter.getValues()[i++]);
				}
			}
			else if (!consoleParameter.isFlagParameter())
			{
				System.out.println("	Non Flag Parameter Value "+consoleParameter.getValue());
			}
		}
		
	}
}
