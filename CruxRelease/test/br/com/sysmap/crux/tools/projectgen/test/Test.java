package br.com.sysmap.crux.tools.projectgen.test;

import java.io.File;

import br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator;
import junit.framework.TestCase;

/**
 * TODO - Gess� - Comment this
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class Test extends TestCase
{
	/**
	 * @throws Exception
	 */
	public void testProjectGeneration() throws Exception
	{
		File workspaceDir = new File("C:/Desenvolvimento/Java/Workspaces/Crux");
		new CruxProjectGenerator(workspaceDir).generate();
	}	
}
