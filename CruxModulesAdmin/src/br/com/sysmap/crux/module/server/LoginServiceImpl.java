package br.com.sysmap.crux.module.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.sysmap.crux.core.server.dispatch.SessionAware;
import br.com.sysmap.crux.module.client.dto.Repository;
import br.com.sysmap.crux.module.client.remote.LoginException;
import br.com.sysmap.crux.module.client.remote.LoginService;
import br.com.sysmap.crux.module.server.scanner.SVNContext;
import br.com.sysmap.crux.module.server.scanner.SVNModules;
import br.com.sysmap.crux.module.server.scanner.SVNRepositories;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class LoginServiceImpl implements LoginService, SessionAware
{
	private HttpSession session;

	/**
	 * 
	 */
	public void setSession(HttpSession session)
	{
		this.session = session;
	}

	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public void authenticate(String url, String user, String password) throws LoginException
	{
		try
		{
			SVNModules modules = (SVNModules) session.getAttribute("SVNModules");
			if (modules == null)
			{
				session.setAttribute("SVNModules", new SVNModules(new SVNContext()));
				modules = (SVNModules) session.getAttribute("SVNModules");
			}
			modules.getContext().authenticate(url, user, password);
		}
		catch (Exception e)
		{
			throw new LoginException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param urls
	 * @param user
	 * @param password
	 */
	public void authenticate(String[] urls, String[] users, String[] passwords) throws LoginException
	{
		if (urls != null && users != null && passwords != null && 
				urls.length == users.length && urls.length == passwords.length)
		{
			for (int i = 0; i < urls.length; i++)
			{
				authenticate(urls[i], users[i], passwords[i]);
			}
		}
	}

	/**
	 * 
	 */
	public Repository[] getRegisteredRepositories()
	{
		Iterator<String> repositories = SVNRepositories.iterateRepositories();
		List<Repository> result = new ArrayList<Repository>();
		
		while(repositories.hasNext())
		{
			result.add(new Repository(repositories.next()));
		}
		
		return result.toArray(new Repository[result.size()]);
	}
}
