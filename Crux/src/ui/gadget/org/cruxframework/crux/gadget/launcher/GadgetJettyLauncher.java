/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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
package org.cruxframework.crux.gadget.launcher;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.cruxframework.crux.core.server.launcher.CruxJettyLauncher;
import org.mortbay.jetty.AbstractConnector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.log.Log;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.util.InstalledHelpInfo;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetJettyLauncher extends CruxJettyLauncher
{
	private int shindigJettyPort = 8080;
	private String shindigBindAddress = "localhost";
	
	public void setShindigBindAddress(String bindAddress) 
	{
		this.shindigBindAddress = bindAddress;
	}

	@Override
	public ServletContainer start(TreeLogger logger, int port, File appRootDir) throws Exception
	{
		ServletContainer servletContainer = super.start(logger, port, appRootDir);
		
		TreeLogger branch = logger.branch(TreeLogger.TRACE,
				"Starting Shindig Jetty on port " + shindigJettyPort, null);

		// Setup our branch logger during startup.
		Log.setLog(new JettyTreeLogger(branch));

		AbstractConnector connector = getConnector(logger);
		if (shindigBindAddress != null) {
			connector.setHost(shindigBindAddress);
		}
		connector.setPort(shindigJettyPort);

		// Don't share ports with an existing process.
		connector.setReuseAddress(false);

		// Linux keeps the port blocked after shutdown if we don't disable this.
		connector.setSoLingerTime(0);

		Server server = new Server();
		server.addConnector(connector);

		RequestLogHandler logHandler = new RequestLogHandler();
		logHandler.setRequestLog(new JettyRequestLogger(logger, getBaseLogLevel()));
		logHandler.setHandler(getShindigApplication(logger, new File(".", "shindig.war")));
		server.setHandler(logHandler);
		server.start();
		server.setStopAtShutdown(true);
		
		// Now that we're started, log to the top level logger.
		Log.setLog(new JettyTreeLogger(logger));
		
		return servletContainer;
	}
	
	/**
	 * Create a jetty handler for each application located on the appDir directory.
	 * <p>
	 * Applications must be deployed as an .war package.
	 * @param logger devMode logger.
	 * @param appFile directory that contains the application to be installed.
	 * @return
	 */
	protected WebAppContext getShindigApplication(TreeLogger logger, File appFile) throws Exception
	{
		String webappPath = appFile.getCanonicalPath();
		WebAppContext webAppContext = new WebAppContextWithReload(logger, webappPath, "/");
	    webAppContext.setConfigurationClasses(__dftConfigurationClasses);
		return webAppContext;
	}

	// GWT code, copied here because it is not visible
	private static final String PROPERTY_NOWARN_WEBAPP_CLASSPATH = "gwt.nowarn.webapp.classpath";
	  
	private TreeLogger.Type baseLogLevel = TreeLogger.INFO;

	private final Object privateInstanceLock = new Object();


	public void setBaseRequestLogLevel(TreeLogger.Type baseLogLevel) {
		synchronized (privateInstanceLock) {
			this.baseLogLevel = baseLogLevel;
		}
	}

	private TreeLogger.Type getBaseLogLevel() {
		synchronized (privateInstanceLock) {
			return this.baseLogLevel;
		}
	}	  

	/**
	 * GWT class, copied here because it is private
	 * 
	 * 
	 * A {@link WebAppContext} tailored to GWT hosted mode. Features hot-reload
	 * with a new {@link WebAppClassLoader} to pick up disk changes. The default
	 * Jetty {@code WebAppContext} will create new instances of servlets, but it
	 * will not create a brand new {@link ClassLoader}. By creating a new {@code
	 * ClassLoader} each time, we re-read updated classes from disk.
	 * 
	 * Also provides special class filtering to isolate the web app from the GWT
	 * hosting environment.
	 */
	protected static final class WebAppContextWithReload extends WebAppContext {

		/**
		 * Specialized {@link WebAppClassLoader} that allows outside resources to be
		 * brought in dynamically from the system path. A warning is issued when
		 * this occurs.
		 */
		private class WebAppClassLoaderExtension extends WebAppClassLoader {

			private static final String META_INF_SERVICES = "META-INF/services/";

			public WebAppClassLoaderExtension() throws IOException {
				super(bootStrapOnlyClassLoader, WebAppContextWithReload.this);
			}

			@Override
			public URL findResource(String name) {
				// Specifically for META-INF/services/javax.xml.parsers.SAXParserFactory
				String checkName = name;
				if (checkName.startsWith(META_INF_SERVICES)) {
					checkName = checkName.substring(META_INF_SERVICES.length());
				}

				// For a system path, load from the outside world.
				URL found;
				if (isSystemPath(checkName)) {
					found = systemClassLoader.getResource(name);
					if (found != null) {
						return found;
					}
				}

				// Always check this ClassLoader first.
				found = super.findResource(name);
				if (found != null) {
					return found;
				}

				// See if the outside world has it.
				found = systemClassLoader.getResource(name);
				if (found == null) {
					return null;
				}

				// Warn, add containing URL to our own ClassLoader, and retry the call.
				String warnMessage = "Server resource '"
					+ name
					+ "' could not be found in the web app, but was found on the system classpath";
				if (!addContainingClassPathEntry(warnMessage, found, name)) {
					return null;
				}
				return super.findResource(name);
			}

			/**
			 * Override to additionally consider the most commonly available JSP and
			 * XML implementation as system resources. (In fact, Jasper is in gwt-dev
			 * via embedded Tomcat, so we always hit this case.)
			 */
			@Override
			public boolean isSystemPath(String name) {
				name = name.replace('/', '.');
				return super.isSystemPath(name)
				|| name.startsWith("org.apache.jasper.")
				|| name.startsWith("org.apache.xerces.");
			}

			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				// For system path, always prefer the outside world.
				if (isSystemPath(name)) {
					try {
						return systemClassLoader.loadClass(name);
					} catch (ClassNotFoundException e) {
					}
				}

				try {
					return super.findClass(name);
				} catch (ClassNotFoundException e) {
					// Don't allow server classes to be loaded from the outside.
					if (isServerPath(name)) {
						throw e;
					}
				}

				// See if the outside world has a URL for it.
				String resourceName = name.replace('.', '/') + ".class";
				URL found = systemClassLoader.getResource(resourceName);
				if (found == null) {
					return null;
				}

				// Warn, add containing URL to our own ClassLoader, and retry the call.
				String warnMessage = "Server class '"
					+ name
					+ "' could not be found in the web app, but was found on the system classpath";
				if (!addContainingClassPathEntry(warnMessage, found, resourceName)) {
					throw new ClassNotFoundException(name);
				}
				return super.findClass(name);
			}

			private boolean addContainingClassPathEntry(String warnMessage,
					URL resource, String resourceName) {
				TreeLogger.Type logLevel = (System.getProperty(PROPERTY_NOWARN_WEBAPP_CLASSPATH) == null)
				? TreeLogger.WARN : TreeLogger.DEBUG;
				TreeLogger branch = logger.branch(logLevel, warnMessage);
				String classPathURL;
				String foundStr = resource.toExternalForm();
				if (resource.getProtocol().equals("file")) {
					assert foundStr.endsWith(resourceName);
					classPathURL = foundStr.substring(0, foundStr.length()
							- resourceName.length());
				} else if (resource.getProtocol().equals("jar")) {
					assert foundStr.startsWith("jar:");
					assert foundStr.endsWith("!/" + resourceName);
					classPathURL = foundStr.substring(4, foundStr.length()
							- (2 + resourceName.length()));
				} else {
					branch.log(TreeLogger.ERROR,
							"Found resouce but unrecognized URL format: '" + foundStr + '\'');
					return false;
				}
				branch = branch.branch(logLevel, "Adding classpath entry '"
						+ classPathURL + "' to the web app classpath for this session",
						null, new InstalledHelpInfo("webAppClassPath.html"));
				try {
					addClassPath(classPathURL);
					return true;
				} catch (IOException e) {
					branch.log(TreeLogger.ERROR, "Failed add container URL: '"
							+ classPathURL + '\'', e);
					return false;
				}
			}
		}

		/**
		 * Parent ClassLoader for the Jetty web app, which can only load JVM
		 * classes. We would just use <code>null</code> for the parent ClassLoader
		 * except this makes Jetty unhappy.
		 */
		private final ClassLoader bootStrapOnlyClassLoader = new ClassLoader(null) {
		};

		private final TreeLogger logger;

		/**
		 * In the usual case of launching {@link com.google.gwt.dev.DevMode}, this
		 * will always by the system app ClassLoader.
		 */
		private final ClassLoader systemClassLoader = Thread.currentThread().getContextClassLoader();

		private WebAppClassLoaderExtension classLoader;

		@SuppressWarnings("unchecked")
		private WebAppContextWithReload(TreeLogger logger, String webApp,
				String contextPath) {
			super(webApp, contextPath);
			this.logger = logger;

			// Prevent file locking on Windows; pick up file changes.
			getInitParams().put(
					"org.mortbay.jetty.servlet.Default.useFileMappedBuffer", "false");

			// Since the parent class loader is bootstrap-only, prefer it first.
			setParentLoaderPriority(true);
		}

		@Override
		protected void doStart() throws Exception {
			classLoader = new WebAppClassLoaderExtension();
			setClassLoader(classLoader);
			super.doStart();
		}

		@Override
		protected void doStop() throws Exception {
			super.doStop();

			Class<?> jdbcUnloader = classLoader.loadClass("com.google.gwt.dev.shell.jetty.JDBCUnloader");
			java.lang.reflect.Method unload = jdbcUnloader.getMethod("unload");
			unload.invoke(null);

			setClassLoader(null);
			classLoader.destroy();
		}
	}
}
