package br.com.sysmap.crux.module.server.scanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.module.CruxModuleHandler;
import br.com.sysmap.crux.module.ModulesAdminMessages;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class SVNScanner
{
	private static final Log logger = LogFactory.getLog(SVNScanner.class);
	private static ModulesAdminMessages messages = MessagesFactory.getMessages(ModulesAdminMessages.class);
	private static SVNScanner instance = new SVNScanner();

	private DocumentBuilder documentBuilder;
	
	static 
	{
		DAVRepositoryFactory.setup();
	}

	
	/**
	 * 
	 * @return
	 */
	public static SVNScanner getInstance()
	{
		return instance;
	}
	
	/**
	 * 
	 */
	private SVNScanner()
	{
		try
		{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new SVNScannerException(messages.svnScannerErrorBuilderCanNotBeCreated(), e);
		}
	}
	
	/**
	 * 
	 */
	public void scanArchives(SVNContext context)
	{
		try
		{
			Iterator<SVNRepository> repositories = context.iterateRepositories();
			while (repositories.hasNext())
			{
				createEntriesList(repositories.next(), "", context);
			}
		}
		catch (Exception e)
		{
			throw new SVNScannerException(messages.svnScannerErrorScanningFiles(), e);
		}
	}

	/**
	 * 
	 * @param repository
	 * @param path
	 * @throws SVNException
	 */
	protected void createEntriesList(SVNRepository repository, String path, SVNContext context) throws SVNException 
	{
		Collection<?> entries = repository.getDir(path, -1 , null , (Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) 
		{
			SVNDirEntry entry = (SVNDirEntry) iterator.next();

        	String entryString = entry.getURL().toString();
            if (entryString.endsWith(".gwt.xml"))
            {
            	processModuleDescriptorFile(repository, entry, context);
            }			
			if (entry.getKind() == SVNNodeKind.DIR) 
			{
				createEntriesList(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName(), context);
			}
		}
	}

	/**
	 * 
	 * @param repository
	 * @param entry
	 */
	private void processModuleDescriptorFile(SVNRepository repository, SVNDirEntry entry, SVNContext context)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			String fileName = entry.getURL().toString().replace(repository.getLocation().toString()+"/", "");
			repository.getFile(fileName, -1, null, out);
			InputStream in = new ByteArrayInputStream(out.toByteArray());
			Document module = documentBuilder.parse(in);
			
			context.registerModule(new URL(entry.getURL().toString()), getModuleName(entry.getRelativePath()), module, getCruxModuleDescriptor(repository, fileName));
		}
		catch (Exception e)
		{
			logger.error(messages.svnScannerErrorProcessingModuleFile(getModuleName(entry.getRelativePath())), e);
		}
	}
	
	private br.com.sysmap.crux.module.ModuleInfo getCruxModuleDescriptor(SVNRepository repository, String fileName) throws SAXException, IOException, SVNException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		repository.getFile(fileName.replace(".gwt.xml", ".module.xml"), -1, null, out);
		
		InputStream in = new ByteArrayInputStream(out.toByteArray());
		return CruxModuleHandler.parseModuleDescriptor(in);
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String getModuleName(String fileName)
	{
		return fileName.replace('/', '.');
	}
}
