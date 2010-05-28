package br.com.sysmap.crux.module.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import br.com.sysmap.crux.core.client.controller.Parameter;
import br.com.sysmap.crux.core.client.controller.ParameterObject;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.ClientControllers;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.screen.datasource.DataSources;
import br.com.sysmap.crux.core.rebind.screen.formatter.Formatters;
import br.com.sysmap.crux.core.rebind.screen.serializable.Serializers;
import br.com.sysmap.crux.core.server.dispatch.SessionAware;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.module.CruxModule;
import br.com.sysmap.crux.module.CruxModuleBridge;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.ModulesAdminMessages;
import br.com.sysmap.crux.module.client.dto.Controller;
import br.com.sysmap.crux.module.client.dto.CruxSerializable;
import br.com.sysmap.crux.module.client.dto.Datasource;
import br.com.sysmap.crux.module.client.dto.Formatter;
import br.com.sysmap.crux.module.client.dto.ModuleInfo;
import br.com.sysmap.crux.module.client.dto.ModuleInformation;
import br.com.sysmap.crux.module.client.dto.ModuleRef;
import br.com.sysmap.crux.module.client.dto.Page;
import br.com.sysmap.crux.module.client.dto.PageParameter;
import br.com.sysmap.crux.module.client.remote.ModuleInfoException;
import br.com.sysmap.crux.module.client.remote.ModuleInfoService;
import br.com.sysmap.crux.module.server.scanner.SVNModules;
import br.com.sysmap.crux.module.server.scanner.SVNRepositories;
import br.com.sysmap.crux.module.validation.CruxModuleValidator;
import br.com.sysmap.crux.module.validation.CruxModuleVersionCheckerInitializer;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ModuleInfoServiceImpl implements ModuleInfoService, SessionAware
{
	private static ModulesAdminMessages messages = MessagesFactory.getMessages(ModulesAdminMessages.class);
	
	private HttpSession session = null;

	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getSerializables(java.lang.String)
	 */
	public CruxSerializable[] getSerializables(String module) throws ModuleInfoException
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		List<CruxSerializable> result = new ArrayList<CruxSerializable>();
		if (cruxModule != null)
		{
			Iterator<String> serializables = Serializers.iterateSerializables();
			while (serializables.hasNext())
			{
				String serializableName = serializables.next();
				Class<?> serializableClass = Serializers.getCruxSerializable(serializableName);
				String moduleBasePackage = cruxModule.getGwtModule().getFullName();
				moduleBasePackage = moduleBasePackage.substring(0, moduleBasePackage.lastIndexOf('.'));
				
				String[] sources = cruxModule.getGwtModule().getSources();
				
				for (String source : sources)
				{
					if (serializableClass.getName().startsWith(moduleBasePackage+"."+source))
					{
						CruxSerializable serializable = createSerializableDTO(serializableName, serializableClass);
						result.add(serializable);
						break;
					}
				}
			}
		}
		return result.toArray(new CruxSerializable[result.size()]);
	}
	
	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getFormatters(java.lang.String)
	 */
	public Formatter[] getFormatters(String module) throws ModuleInfoException
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		List<Formatter> result = new ArrayList<Formatter>();
		if (cruxModule != null)
		{
			Iterator<String> formatters = Formatters.iterateFormatters();
			while (formatters.hasNext())
			{
				String formatterName = formatters.next();
				Class<?> formatterClass = Formatters.getFormatter(formatterName);
				String moduleBasePackage = cruxModule.getGwtModule().getFullName();
				moduleBasePackage = moduleBasePackage.substring(0, moduleBasePackage.lastIndexOf('.'));
				
				String[] sources = cruxModule.getGwtModule().getSources();
				
				for (String source : sources)
				{
					if (formatterClass.getName().startsWith(moduleBasePackage+"."+source))
					{
						Formatter formatter = createFormatterDTO(formatterName, formatterClass);
						result.add(formatter);
						break;
					}
				}
			}
		}
		return result.toArray(new Formatter[result.size()]);
	}

	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getPages(java.lang.String)
	 */
	public Page[] getPages(String module) throws ModuleInfoException
	{
		String[] pages = CruxModuleHandler.getCruxModule(module).getPages();

		Page[] result = new Page[pages != null?pages.length:0];
		for (int i=0; i< result.length; i++)
		{
			result[i] = new Page(pages[i]);
		}

		return result;
	}

	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getPageParameters(java.lang.String, java.lang.String)
	 */
	public PageParameter[] getPageParameters(String module, String page) throws ModuleInfoException
	{
		Set<PageParameter> parameters = new HashSet<PageParameter>();
		String currentModule = CruxModuleBridge.getInstance().getCurrentModule(); 
		try
		{
			CruxModuleBridge.getInstance().registerCurrentModule(module);
			Screen screen = ScreenFactory.getInstance().getScreen(page);
			
			Iterator<String> controllers = screen.iterateControllers();
			while(controllers.hasNext())
			{
				String controllerName = controllers.next();
				Class<?> clientHandler = ClientControllers.getClientHandler(controllerName);
				populateParameters(clientHandler, parameters);
			}
			Iterator<String> datasources = screen.iterateDataSources();
			while(datasources.hasNext())
			{
				String datasourcerName = datasources.next();
				Class<?> dataSourceClass = DataSources.getDataSource(datasourcerName);
				populateParameters(dataSourceClass, parameters);
			}
		}
		catch (ScreenConfigException e)
		{
			throw new ModuleInfoException(messages.moduleInfoErrorRetrievingScreen(page));
		}
		finally
		{
			CruxModuleBridge.getInstance().registerCurrentModule(currentModule);
		}

		return parameters.toArray(new PageParameter[parameters.size()]);
	}
	
	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getDatasources(java.lang.String)
	 */
	public Datasource[] getDatasources(String module) throws ModuleInfoException
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		List<Datasource> result = new ArrayList<Datasource>();
		if (cruxModule != null)
		{
			Iterator<String> datasourcess = DataSources.iterateDataSources();
			while (datasourcess.hasNext())
			{
				String datasourceName = datasourcess.next();
				Class<?> datasourceClass = DataSources.getDataSource(datasourceName);
				String moduleBasePackage = cruxModule.getGwtModule().getFullName();
				moduleBasePackage = moduleBasePackage.substring(0, moduleBasePackage.lastIndexOf('.'));
				
				String[] sources = cruxModule.getGwtModule().getSources();
				
				for (String source : sources)
				{
					if (datasourceClass.getName().startsWith(moduleBasePackage+"."+source))
					{
						Datasource datasource = createDatasourceDTO(datasourceName, datasourceClass);
						result.add(datasource);
						break;
					}
				}
			}
		}
		return result.toArray(new Datasource[result.size()]);
	}
	
	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getControllers(java.lang.String)
	 */
	public Controller[] getControllers(String module) throws ModuleInfoException
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		List<Controller> result = new ArrayList<Controller>();
		if (cruxModule != null)
		{
			Iterator<String> clientHandlers = ClientControllers.iterateClientHandler();
			while (clientHandlers.hasNext())
			{
				String controllerName = clientHandlers.next();
				Class<?> clientHandler = ClientControllers.getClientHandler(controllerName);
				String moduleBasePackage = cruxModule.getGwtModule().getFullName();
				moduleBasePackage = moduleBasePackage.substring(0, moduleBasePackage.lastIndexOf('.'));
				
				String[] sources = cruxModule.getGwtModule().getSources();
				
				for (String source : sources)
				{
					if (clientHandler.getName().startsWith(moduleBasePackage+"."+source))
					{
						Controller controller = createControllerDTO(controllerName, clientHandler);
						result.add(controller);
						break;
					}
				}
			}
		}
		return result.toArray(new Controller[result.size()]);
	}


	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getModuleNames()
	 */
	public String[] getModuleNames() throws ModuleInfoException
	{
		Iterator<CruxModule> cruxModules = CruxModuleHandler.iterateCruxModules();
		List<String> result = new ArrayList<String>();
		while (cruxModules.hasNext())
		{
			CruxModule module = cruxModules.next();
			result.add(module.getName());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getModuleInfo(java.lang.String)
	 */
	public ModuleInformation getModuleInfo(String module) throws ModuleInfoException
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(module);
		ModuleInfo moduleInfo = new ModuleInfo();
		
		moduleInfo.setName(cruxModule.getName());
		moduleInfo.setVersion(cruxModule.getInfo().getVersion());
		moduleInfo.setDescription(cruxModule.getInfo().getDescription());
		moduleInfo.setStartPage(cruxModule.getInfo().getStartPage());

		br.com.sysmap.crux.module.ModuleRef[] cruxRequiredModules = cruxModule.getRequiredModules();
		if (cruxRequiredModules != null && cruxRequiredModules.length > 0)
		{
			ModuleRef[] requiredModules = new ModuleRef[cruxRequiredModules.length];
			for (int i=0; i< cruxRequiredModules.length; i++)
			{
				br.com.sysmap.crux.module.ModuleRef ref = cruxRequiredModules[i];
				ModuleRef moduleRef = new ModuleRef();
				
				moduleRef.setName(ref.getName());
				moduleRef.setMinVersion(ref.getMinVersion());
				moduleRef.setMaxVersion(ref.getMaxVersion());

				CruxModule refModule = CruxModuleHandler.getCruxModule(ref.getName());
				if (refModule == null)
				{
					moduleRef.setStatusVersion(false);
				}
				else
				{
					boolean statusVersion = CruxModuleValidator.checkMinVerion(refModule, ref.getMinVersion()) && 
											CruxModuleValidator.checkMaxVerion(refModule, ref.getMaxVersion());
					moduleRef.setStatusVersion(statusVersion);
				}
				requiredModules[i] = moduleRef;
			}
			moduleInfo.setRequiredModules(requiredModules);
		}
		else
		{
			moduleInfo.setRequiredModules(new ModuleRef[0]);
		}
		
		ModuleInformation moduleInformation = new ModuleInformation();
		moduleInformation.setModuleInfo(moduleInfo);
		moduleInformation.setAuthenticated(isUserAuthenticated());

		Iterator<String> repositories = SVNRepositories.iterateRepositories();
		moduleInformation.setHasRepositories(repositories.hasNext());
		
		return moduleInformation;
	}

	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getDependentModules(java.lang.String)
	 */
	public ModuleInfo[] getDependentModules(String module) throws ModuleInfoException
	{
		String version = getModuleVersion(module);

		return getDependentModules(module, version);
	}
	
	/**
	 * @see br.com.sysmap.crux.module.client.remote.ModuleInfoService#getDependentModules(java.lang.String, java.lang.String)
	 */
	public ModuleInfo[] getDependentModules(String module, String version) throws ModuleInfoException
	{
		Iterator<ModuleInfo> modules = ((SVNModules)session.getAttribute("SVNModules")).iterateModulesInfo(); //TODO: rever questao de autenticacao previa
		List<ModuleInfo> dependentModules = new ArrayList<ModuleInfo>();

		while (modules.hasNext())
		{
			ModuleInfo moduleInfo = modules.next();
			
			if (isDependent(moduleInfo, module, version))
			{
				dependentModules.add(moduleInfo);
			}
		}
		
		return dependentModules.toArray(new ModuleInfo[dependentModules.size()]);
	}

	/**
	 * @return
	 */
	private boolean isUserAuthenticated()
	{
		SVNModules modules = (SVNModules) session.getAttribute("SVNModules");
		return (modules != null);
	}	
	
	/**
	 * 
	 * @param mod
	 * @param module
	 * @return
	 */
	private boolean isDependent(ModuleInfo moduleInfo, String module, String version)
	{
		for (ModuleRef ref : moduleInfo.getRequiredModules())
		{
			if (ref.getName().equals(module))
			{
				if (CruxModuleVersionCheckerInitializer.getVersionChecker().checkMinVersion(ref.getMinVersion(), version) &&
				    CruxModuleVersionCheckerInitializer.getVersionChecker().checkMaxVersion(ref.getMaxVersion(), version))
				{
					return true;
				}
			}
		}
		
		return false; 
	}
	
	/**
	 * 
	 * @param moduleName
	 * @return
	 */
	private String getModuleVersion(String moduleName)
	{
		CruxModule cruxModule = CruxModuleHandler.getCruxModule(moduleName);
		if (cruxModule != null)
		{
			return cruxModule.getInfo().getVersion();
		}
		return null;
	}

	/**
	 * @param serializableName
	 * @param serializableClass
	 * @return
	 */
	private CruxSerializable createSerializableDTO(String serializableName, Class<?> serializableClass)
	{
		CruxSerializable serializable = new CruxSerializable();
		serializable.setName(serializableName);
		serializable.setClassName(serializableClass.getName());
		
		return serializable;
	}
	
	/**
	 * @param formatterName
	 * @param formatterClass
	 * @return
	 */
	private Formatter createFormatterDTO(String formatterName, Class<?> formatterClass)
	{
		Formatter formatter = new Formatter();
		formatter.setName(formatterName);
		formatter.setClassName(formatterClass.getName());
		
		return formatter;
	}
	
	/**
	 * 
	 * @param dataSourceName
	 * @param dataSourceClass
	 * @return
	 */
	private Datasource createDatasourceDTO(String dataSourceName, Class<?> dataSourceClass)
	{
		Datasource datasource = new Datasource();
		datasource.setName(dataSourceName);
		datasource.setClassName(dataSourceClass.getName());
		
		DataSource datasourceAnnot = dataSourceClass.getAnnotation(DataSource.class);
		
		datasource.setAutoBind(datasourceAnnot.autoBind());
		
		return datasource;
	}
	
	/**
	 * 
	 * @param controllerName
	 * @param clientHandler
	 * @return
	 */
	private Controller createControllerDTO(String controllerName, Class<?> clientHandler)
	{
		Controller controller = new Controller();
		controller.setName(controllerName);
		controller.setClassName(clientHandler.getName());
		
		br.com.sysmap.crux.core.client.controller.Controller controllerAnnot = 
			clientHandler.getAnnotation(br.com.sysmap.crux.core.client.controller.Controller.class);
		
		controller.setAutoBind(controllerAnnot.autoBind());
		controller.setStateful(controllerAnnot.statefull());
		controller.setLazy(controllerAnnot.lazy());
		controller.setFragment(controllerAnnot.fragment());
		
		return controller;
	}

	/**
	 * @param clientHandler
	 * @param parameters
	 */
	private void populateParameters(Class<?> clientHandler, Set<PageParameter> parameters)
	{
		Field[] declaredFields = ClassUtils.getDeclaredFields(clientHandler);
		
		for (Field field : declaredFields)
		{
			if (field.getType().getAnnotation(ParameterObject.class) != null)
			{
				parameters.addAll(getParameters(field));
			}
			else if (field.getAnnotation(Parameter.class) != null)
			{
				parameters.add(getParameter(field));
			}
		}
	}

	/**
	 * @param field
	 * @return
	 */
	private Set<PageParameter> getParameters(Field field)
	{
		Set<PageParameter> result = new HashSet<PageParameter>();
		Class<?> type = field.getType();
		ParameterObject parameterObjectAnnot = type.getAnnotation(ParameterObject.class);
		Field[] dtoFields = ClassUtils.getDeclaredFields(type);
		for (Field dtoField : dtoFields)
		{
			if (parameterObjectAnnot.bindParameterByFieldName() || (field.getAnnotation(Parameter.class) != null))
			{
				result.add(getParameter(dtoField));
			}
		}
		
		return result;
	}
	
	
	/**
	 * @param field
	 * @return
	 */
	private PageParameter getParameter(Field field)
	{
		Parameter parameterAnnot = field.getAnnotation(Parameter.class);
		PageParameter pageParameter = new PageParameter();
		if (parameterAnnot != null)
		{
			if (parameterAnnot.value().length() == 0)
			{
				pageParameter.setName(field.getName());
			}
			else
			{
				pageParameter.setName(parameterAnnot.value());
			}
			pageParameter.setRequired(parameterAnnot.required());
		}
		else
		{
			pageParameter.setName(field.getName());
		}

		pageParameter.setType(field.getType().getName());
		return pageParameter;
	}
	
	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.SessionAware#setSession(javax.servlet.http.HttpSession)
	 */
	public void setSession(HttpSession session)
	{
		this.session = session;
	}
}
