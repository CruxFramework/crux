package org.cruxframework.crossdeviceshowcase.server.showcase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Explores the SVN folders searching for files to be shown
 * @author Gesse Dafe
 */
public class SVNExplorer
{
	private static final String NOT_FOUND = "";
	private static final long TOKEN = new Date().getTime(); 
	private static final Log log = LogFactory.getLog(SVNExplorer.class);
	
	private static final String SVN_PATH = 	"http://crux-framework.googlecode.com/svn/trunk/cross-device-showcase/src/";
	
	private static final String MAIN_VIEWS_PATH	 =	SVN_PATH + "main/resources/org/cruxframework/crossdeviceshowcase/public/views/samples/";
	private static final String CONTROLLERS_PATH = 	SVN_PATH + "main/java/org/cruxframework/crossdeviceshowcase/client/controller/samples/";
	private static final String ASYNC_PATH = 		SVN_PATH + "main/java/org/cruxframework/crossdeviceshowcase/client/remote/samples/";
	private static final String SERVER_PATH = 		SVN_PATH + "main/java/org/cruxframework/crossdeviceshowcase/server/samples/";
	
	private static Map<String, String> cachedResources = new ConcurrentHashMap<String,String>();
	
	private static Pattern SVN_ENTRY_PATTERN = Pattern.compile("\\<li\\>\\<a href\\=\"[^\"]+\"\\>([^\\<]+)\\</a\\>\\</li\\>");
		
	/**
	 * Lists all files associated to a given sample view.
	 * @param viewName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static List<String> listSourceFilesForView(String viewName) throws FileNotFoundException
	{
		List<String> paths = new ArrayList<String>();
		
		List<String> views = getFolderEntries(MAIN_VIEWS_PATH + viewName.toLowerCase()); 
		List<String> controllers = getFolderEntries(CONTROLLERS_PATH + viewName.toLowerCase());
		List<String> asyncs = getFolderEntries(ASYNC_PATH + viewName.toLowerCase());
		List<String> services = getFolderEntries(SERVER_PATH + viewName.toLowerCase());
		
		paths.addAll(views);
		paths.addAll(controllers);
		paths.addAll(asyncs);
		paths.addAll(services);
		paths.addAll(views);
		
		return paths;
	}
	
	/**
	 * Retrieves a source file from SVN
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getSourceFile(String path) throws FileNotFoundException
	{
		return readURL(path, false);
	}
	
	/**
	 * Lists all file entries in a svn folder.
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	private static List<String> getFolderEntries(String path) throws FileNotFoundException
	{
		List<String> entries = new ArrayList<String>();
		String text = readURL(path, true);
		if(text != null)
		{
			Matcher matcher = SVN_ENTRY_PATTERN.matcher(text);
			while(matcher.find())
			{
				String entry = matcher.group(1);
				if(isValidSvnEntry(entry))
				{
					entries.add(path + (!entry.startsWith("/") ? "/" : "") + entry);
				}
			}		
		}
		return entries;
	}
	
	/**
	 * Reads an URL from SVN
	 * @param url
	 * @param nullWhenNotFound
	 * @return
	 * @throws FileNotFoundException 
	 */
	private static String readURL(String url, boolean nullWhenNotFound) throws FileNotFoundException
	{
		String result = cachedResources.get(url);
		
		if(result != null)
		{
			if(result.equals(NOT_FOUND))
			{
				result = null;
			}
		}
		else
		{
			try
			{
				result = readStreamFromURL(url);
			}
			catch(Throwable t)
			{
				if(!nullWhenNotFound || !(t instanceof FileNotFoundException))
				{
					log.error(t.getMessage(), t);
					throw new FileNotFoundException("File not found: " + url);
				}
			}
			
			if(result != null && result.contains("<title>404 Not Found</title>"))
			{
				result = null;
			}
			
			cachedResources.put(url, result != null ? result : NOT_FOUND);
		}		

		if(result == null && !nullWhenNotFound)
		{
			throw new FileNotFoundException("File not found: " + url);
		}
		
		return result;
	}

	/**
	 * Opens a stream from an URL and reads it as a string
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static String readStreamFromURL(String url) throws Exception
	{
		BufferedReader reader = null;

		try
		{
			URL resourceURL = new URL(url + "?" + TOKEN); 
			reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), Charset.forName("UTF-8")));
			
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) 
			{
				builder.append(line.replace("\t", "    ") + "\n");
			}
			
			return builder.toString();
		}
		finally
		{
			if (reader != null)
			{
				try {reader.close();} catch (IOException e) {log.error(e.getMessage(),e);}
			}
		}
	}
	
	/**
	 * Returns true is the entry represents a single file.
	 * @param entry
	 * @return
	 */
	private static boolean isValidSvnEntry(String entry)
	{
		return entry != null && entry.contains(".") && !entry.equals("..");
	}
}