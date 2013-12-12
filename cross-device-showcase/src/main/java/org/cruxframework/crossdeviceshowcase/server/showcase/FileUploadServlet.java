package org.cruxframework.crossdeviceshowcase.server.showcase;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileUploadServlet
 */
@WebServlet(name="upload-endpoint", urlPatterns="/upload-endpoint")
public class FileUploadServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1183019676233475190L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		System.out.println("Upload received:" + request.getInputStream().read());
	}
}
