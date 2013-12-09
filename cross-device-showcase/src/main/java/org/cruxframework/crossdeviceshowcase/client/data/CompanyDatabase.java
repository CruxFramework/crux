/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client.data;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.db.Database;
import org.cruxframework.crux.core.client.db.DatabaseErrorHandler;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef;
import org.cruxframework.crux.core.client.db.annotation.DatabaseDef.ObjectStoreDef;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DatabaseDef(name="CruxCompanyDatabase", version=1, defaultErrorHandler=CompanyDatabase.ErrorHandler.class, 
	 objectStores={@ObjectStoreDef(targetClass=Person.class)}
)
public interface CompanyDatabase extends Database
{
	public static class ErrorHandler implements DatabaseErrorHandler
	{
		@Override
		public void onError(String message) 
		{
			Crux.getErrorHandler().handleError(message);
		}

		@Override
		public void onError(String message, Throwable t) 
		{
			Crux.getErrorHandler().handleError(message, t);
		}
	}
}
