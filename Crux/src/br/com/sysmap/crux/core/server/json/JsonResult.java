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

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.json.JSONObject;

public class JsonResult
{
    private Object result = null;
    private int errorCode;
    private String dtoChanges;
    
    public final static int CODE_SUCCESS = 0;
    public final static int CODE_REMOTE_EXCEPTION = 490;
    public final static int CODE_ERR_PARSE = 590;
    public final static int CODE_ERR_NOMETHOD = 591;
    public final static int CODE_ERR_UNMARSHALL = 592;
    public final static int CODE_ERR_MARSHALL = 593;
    public final static int CODE_ERR_METHOD = 594;
    public final static int CODE_ERR_VALIDATION = 595;
    public final static JsonResult ERR_PARSE = new JsonResult(CODE_ERR_PARSE, "couldn't parse request arguments");
    public final static JsonResult ERR_NOMETHOD = new JsonResult(CODE_ERR_NOMETHOD, "method not found (session may have timed out)");
    

    public JsonResult(int errorCode, Object o)
    {
        this.errorCode = errorCode;
        this.result = o;
    }
    
    public String toString()
    {
        JSONObject o = new JSONObject();
        if (errorCode == CODE_SUCCESS)
        {
            o.put("result", result);
            if (dtoChanges != null && dtoChanges.length() > 0)
            {
                o.put("dtoChanges", dtoChanges);
            }
        }
        else if (errorCode == CODE_REMOTE_EXCEPTION)
        {
            Exception e = (Exception) result;
            CharArrayWriter caw = new CharArrayWriter();
            e.printStackTrace(new PrintWriter(caw));
            JSONObject err = new JSONObject();
            err.put("code", new Integer(errorCode));
            err.put("msg", e.getMessage());
            err.put("trace", caw.toString());
            o.put("error", err);
        }
        else
        {
            JSONObject err = new JSONObject();
            err.put("code", new Integer(errorCode));
            err.put("msg", result);
            o.put("error", err);
        }
        return o.toString();
    }

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDtoChanges() {
		return dtoChanges;
	}

	public void setDtoChanges(String dtoChanges) {
		this.dtoChanges = dtoChanges;
	}
}
