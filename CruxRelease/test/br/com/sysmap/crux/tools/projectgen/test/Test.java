package br.com.sysmap.crux.tools.projectgen.test;

import junit.framework.TestCase;
import br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class Test extends TestCase
{
	/**
	 * @throws Exception
	 */
	public void testProjectGeneration() throws Exception
	{
		String workspaceDir = "C:/Desenvolvimento/Java/Workspaces/Crux";
		CruxProjectGenerator.main(new String[]{workspaceDir});
	}	
}
