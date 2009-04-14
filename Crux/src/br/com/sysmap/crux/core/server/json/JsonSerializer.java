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
package br.com.sysmap.crux.core.server.json;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONTokener;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

import com.metaparadigm.jsonrpc.JSONSerializer;
import com.metaparadigm.jsonrpc.SerializerState;
import com.metaparadigm.jsonrpc.UnmarshallException;

public class JsonSerializer extends JSONSerializer
{
    private static final Log logger = LogFactory.getLog(JsonSerializer.class);
    private static final long serialVersionUID = 170925728963033916L;
    private static JsonSerializer instance = new JsonSerializer();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

    
    public static JsonSerializer getInstance()
    {
        return instance;
    }
    
    public JsonSerializer()
    {
        super();
        
        try 
        {
            registerDefaultSerializers();
        } 
        catch (Exception e) 
        {
            logger.error(messages.jsonSerializerRegisterError(e.getLocalizedMessage()), e);
        }
    }
    
    /**
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object objMarshall(Object obj) throws Exception
    {
        return getInstance().marshall( new SerializerState(), obj);      
    }
    
    /**
     * 
     * @param obj
     * @return
     * @throws Exception 
     */
    public static String serialize(Object obj) throws Exception 
    {
        JsonResult o = new JsonResult(JsonResult.CODE_SUCCESS,objMarshall(obj));
        return o.toString();
    	//return objMarshall(obj).toString();
    }
    
    /**
     * 
     * @param str
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object deserialize(String str, Class<?> clazz) throws Exception
    {
        Object json;
        try
        {
            json = new JSONTokener(str).nextValue();
        }
        catch (ParseException e)
        {
            throw new UnmarshallException(messages.jsonSerializerParserError());
        }
        return getInstance().unmarshall(new SerializerState(), clazz, json);
    }
}
