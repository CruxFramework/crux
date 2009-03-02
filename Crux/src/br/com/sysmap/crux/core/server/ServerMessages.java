package br.com.sysmap.crux.core.server;
/**
 * Contains all server messages used by Crux Framework
 * @author Thiago
 *
 */
public interface ServerMessages 
{
	String annotationScannerBuildIndex();
	String annotationScannerBuildIndexError(String errMsg);
	String annotationScannerIndexNotFound();
	String autoResponseRenderComponentError(String errMsg);
	String autoResponseRenderPhaseError(String errMsg);
	String clientHandlersCallbackInitializeError(String errMsg);
	String clientHandlersHandlerInitializeError(String errMsg);
	String componentConfigParserError(String errMsg);
	String componentConfigScannerInitializationError(String errMsg);
	String componentParserImplComponentPropertyError(String property, String componentId);
	String controllersInitializeError(String errMsg);
	String dispatchPhasePropertyNotBound(String property);
	String controllerFactoryInitializerUsingDefaultFactory();
	String initializerListenerClientFormattersInitialized();
	String initializerListenerClientModulesInitialized();
	String initializerListenerComponentsRegistered();
	String initializerListenerControllersRegistered();
	String initializerListenerScreenFactoryInitialized();
	String jsonSerializerParserError();
	String jsonSerializerRegisterError(String errMesg);
	String lifeCycleProcessRequestError(String errMsg);
	String lifeCycleRegisterPhaseError(String errMsg);
	String lifeCycleRegisterPhases(String className);
	String parametersBindPhaseErrorCreatingScreen(String errMsg);
	String parametersBindPhaseErrorPopulatingBean(String parName, String errMsg);
	String parametersBindPhaseInvalidScreenRequested(String screenId);
	String rpcResponseRenderPhaseError(String errMsg);
	String rpcResponseRenderComponentError(String errMsg);
	String scannerURLSErrorSearchingClassesDir(String errMsg);
	String scannerURLSErrorSearchingLibDir(String errMsg);
	String screenFactoryCheckFileUpdateError(String errMsg);
	String screenFactoryComponentIdRequired();
	String screenFactoryErrorCreateComponent(String componentId);
	String screenFactoryErrorCreateComponentParent(String parentId, String componentId);
	String screenFactoryErrorRetrievingScreen(String screenId, String errMsg);
	String screenFactoryGenericErrorCreateComponent(String screenId, String errMsg);
	String screenFactoryInvalidComponentParent(String componentId);
	String screenFactoryNotInitialized();
	String screenFactoryFormatterNotFound(String formatter);
	String screenFactoryErrorCreatingFormatter(String formatter);
	String screenFactoryDuplicateServerBind(String property);
	String screenFactoryScreeResourceNotFound(String screenId);
	String screenStateManagerErrorCloningScreen(String screenId, String errMsg);
	String screenStateManagerInitializerUsingDefaultFactory();
	String screenPropertyError(String property, String componentId);
	String formattersFormatterInitializeError(String errMsg);
}
