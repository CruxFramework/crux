package br.com.sysmap.crux.showcase.client;

import java.util.Date;

public class MyDto
{
	protected String valor1;
	public Integer valor2;
	protected Date teste;
	
	public String getValor1()
	{
		return valor1;
	}
	public void setValor1(String valor1)
	{
		this.valor1 = valor1;
	}
	public Date getTeste()
	{
		return teste;
	}
	public void setTeste(Date teste)
	{
		this.teste = teste;
	}
}
