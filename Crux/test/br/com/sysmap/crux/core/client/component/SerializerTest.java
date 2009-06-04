package br.com.sysmap.crux.core.client.component;

import java.util.Date;

import com.google.gwt.junit.client.GWTTestCase;

public class SerializerTest extends GWTTestCase 
{
	public String getModuleName() 
	{
		return "br.com.sysmap.crux.core.Crux";
	}

	public void testSerializeNumber() 
	{
		Integer in = new Integer(3);
		try
		{
			String out = ModuleComunicationSerializer.serialize(in);
			System.out.println(out);
			//assertEquals("3", out);
		}
		catch (ModuleComunicationException e)
		{
			fail(e.getMessage());
		}
	}

	public void testSerializeString() 
	{
		String in = "str<;\\||&>сс";
		try
		{
			String out = ModuleComunicationSerializer.serialize(in);
			System.out.println(out);
			//assertEquals("3", out);
		}
		catch (ModuleComunicationException e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testSerializeBoolean() 
	{
		Boolean in = true;
		try
		{
			String out = ModuleComunicationSerializer.serialize(in);
			System.out.println(out);
			//assertEquals("3", out);
		}
		catch (ModuleComunicationException e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testSerializeDate() 
	{
		Date in = new Date();
		try
		{
			String out = ModuleComunicationSerializer.serialize(in);
			System.out.println(out);
			//assertEquals("3", out);
		}
		catch (ModuleComunicationException e)
		{
			fail(e.getMessage());
		}
	}
	
	public void testSerializeArray() 
	{
		String[] in = {"teste simples"};
		try
		{
			String out = ModuleComunicationSerializer.serialize(in);
			System.out.println(out);
			//assertEquals("3", out);
		}
		catch (ModuleComunicationException e)
		{
			fail(e.getMessage());
		}
	}
}
