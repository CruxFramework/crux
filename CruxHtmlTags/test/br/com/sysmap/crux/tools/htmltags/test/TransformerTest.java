package br.com.sysmap.crux.tools.htmltags.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import junit.framework.TestCase;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.tools.htmltags.CruxToHtmlTransformer;
import br.com.sysmap.crux.tools.htmltags.WidgetTagConfig;

/**
 * Test for CruxToHtmlTransformer
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class TransformerTest extends TestCase
{
	private static final TransformerTestConfigs conf = MessagesFactory.getMessages(TransformerTestConfigs.class);

	@Override
	protected void setUp() throws Exception
	{
		CruxToHtmlTransformer.setForceIndent(true);
		WidgetTagConfig.initializeWidgetConfig(new URL[]{new File(conf.xsltDir()).toURI().toURL()});
	}
	
	/**
	 * Tests the transformer for all widgets 
	 * @throws Exception 
	 */
	public void testAll() throws Exception
	{
		String workingDir = conf.workingDir();
		File inputDir = new File(workingDir + "/input");
		String outputDir = workingDir + "/output";
		File[] files = inputDir.listFiles();
		
		for (File file : files)
		{
			if(file.getName().endsWith(".xml"))
			{
				FileOutputStream out = null;
				
				try
				{
					String inputFile = file.getAbsolutePath();
					String outputFileName = outputDir + "/" + file.getName().replace(".xml", ".html");
					File resultFile = new File(outputFileName);
					if(!resultFile.exists())
					{
						resultFile.createNewFile();
					}
					
					out = new FileOutputStream(resultFile);
					
					CruxToHtmlTransformer.generateHTML(inputFile, out);
				}
				finally
				{
					if(out != null)
					{
						out.close();
					}
				}				
			}
		}
	}
}
