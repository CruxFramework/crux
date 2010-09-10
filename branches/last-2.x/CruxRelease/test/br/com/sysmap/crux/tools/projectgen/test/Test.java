package br.com.sysmap.crux.tools.projectgen.test;

import junit.framework.TestCase;
import br.com.sysmap.crux.tools.projectgen.CruxProjectGenerator;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé
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
