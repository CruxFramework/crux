/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.client.serializer;

import br.com.sysmap.crux.core.client.screen.CruxSerializable;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
public class BooleanSerializer implements CruxSerializable
{
    private static final long serialVersionUID = 1L;

	public Object deserialize(String serializedData)
	{
		return new Boolean(serializedData);
	}

	public Object[] newArray(int size)
	{
		return new Boolean[size];
	}

	public String serialize()
	{
		return null;
	}

}
