/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.module.server.scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.module.ModulesAdminMessages;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class SVNRepositories
{
	private static Set<String> repositories = Collections.synchronizedSet(new TreeSet<String>());
	private static ModulesAdminMessages messages = MessagesFactory.getMessages(ModulesAdminMessages.class);
	private static final Log logger = LogFactory.getLog(SVNRepositories.class);
	
	static 
	{
		DAVRepositoryFactory.setup();
	}
	
	/**
	 * 
	 * @return
	 */
	public static Iterator<String> iterateRepositories()
	{
		return repositories.iterator();
	}

	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public static void registerRepository(String url)
	{
		try
		{
			new URL(url); // check syntax
			repositories.add(url);
		}
		catch (MalformedURLException e)
		{
			logger.error(messages.svnRepositoriesErrorRegistringRepository(url), e);
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public static SVNRepository authenticateInRepository(String url, String user, String password)
	{
		try
		{
			new URL(url); // check syntax
			
			if (repositories.contains(url))
			{
				SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
				ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( user , password );
				repository.setAuthenticationManager( authManager );

				SVNNodeKind nodeKind = repository.checkPath( "" ,  -1 );
				if (nodeKind != SVNNodeKind.DIR)
				{
					repositories.remove(url);
					logger.error(messages.svnRepositoriesDirExpected(url));
					throw new SVNScannerException(messages.svnRepositoriesDirExpected(url));
				}
				return repository;
			}
			else
			{
				logger.error(messages.svnRepositoryNotRegistered(url));
				throw new SVNScannerException(messages.svnRepositoryNotRegistered(url));
			}
		}
		catch (MalformedURLException e)
		{
			logger.error(messages.svnRepositoriesErrorRegistringRepository(url), e);
			throw new SVNScannerException(messages.svnRepositoriesErrorRegistringRepository(url), e);
		}
		catch (Exception e)
		{
			logger.info(messages.svnRepositoriesErrorAuthenticatingUser(user, url), e);
			throw new SVNScannerException(messages.svnRepositoriesErrorAuthenticatingUser(user, url), e);
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public static SVNRepository registerRepository(String url, String user, String password)
	{
		registerRepository(url);
		return authenticateInRepository(url, user, password);
	}
}
