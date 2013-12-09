/**
 * 
 */
package org.cruxframework.crossdeviceshowcase.client;

import org.cruxframework.crossdeviceshowcase.client.data.Person;
import org.cruxframework.crux.core.client.screen.views.BindableView;
import org.cruxframework.crux.core.client.screen.views.ViewAccessor;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Views extends ViewAccessor
{
	BindableView<Person> person();
}
