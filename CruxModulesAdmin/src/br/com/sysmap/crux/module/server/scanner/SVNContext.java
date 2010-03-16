package br.com.sysmap.crux.module.server.scanner;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.io.SVNRepository;
import org.w3c.dom.Document;

import br.com.sysmap.crux.module.client.dto.ModuleInfo;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class SVNContext
{
	private Set<SVNRepository> repositories = new HashSet<SVNRepository>();
	private SVNModules modules;
	
	/**
	 * 
	 * @return
	 */
	Iterator<SVNRepository> iterateRepositories()
	{
		return repositories.iterator();
	}
	
	/**
	 * 
	 * @param moduleDescriptor
	 * @param moduleFullName
	 * @param moduleDocument
	 * @param moduleInfo
	 * @return
	 */
	ModuleInfo registerModule(URL moduleDescriptor, String moduleFullName, Document moduleDocument, br.com.sysmap.crux.module.ModuleInfo moduleInfo)
	{
		return modules.registerModule(moduleDescriptor, moduleFullName, moduleDocument, moduleInfo);
	}

	/**
	 * 
	 * @param modules
	 */
	void setModules(SVNModules modules)
	{
		this.modules = modules;
	}
	
	public void authenticate(String url, String user, String password)
	{
		SVNRepository repository = SVNRepositories.authenticateInRepository(url, user, password);
		repositories.add(repository);
	}
}
