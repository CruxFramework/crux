/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.collection.FastList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class JsUtils
{
	/**
	 * Append all values contained in input array into the output array 
	 * @param in
	 * @param out
	 */
	public static native void copyValues(JsArrayMixed input, JsArrayMixed output)/*-{
		for (var i=0; i< input.length; i++)
		{
			output.push(input[i]);
		}
	}-*/;
	
	/**
	 * Create a JSONValue Object from a native javascript object
	 * @param object
	 * @return
	 */
	public static JSONValue toJSONValue(JavaScriptObject object)
	{
		if (object == null)
		{
			return JSONNull.getInstance();
		}
		if (isArray(object))
		{
			return new JSONArray(object);
		}
		return new JSONObject(object);
	}

	/**
	 * Extract the associated native javascript object from the given json string
	 * @param jsonValue
	 * @return
	 */
	public static <T extends JavaScriptObject> T fromString(String encodedObject)
	{
		if (StringUtils.isEmpty(encodedObject))
		{
			return null;
		}
		if (encodedObject.startsWith("data:"))
		{
			return FileUtils.fromDataURI(encodedObject).cast();
		}
		return fromJSONValue(encodedObject);
	}
	
	
	/**
	 * Extract the associated native javascript object from the given json string
	 * @param jsonValue
	 * @return
	 */
	public static <T extends JavaScriptObject> T fromJSONValue(String encodedObject)
	{
		JSONValue jsonValue = JSONParser.parseStrict(encodedObject);
		return fromJSONValue(jsonValue).cast();
	}

	/**
	 * Extract the associated native javascript object from the given json object
	 * @param jsonValue
	 * @return
	 */
	public static JavaScriptObject fromJSONValue(JSONValue jsonValue)
	{
		if (jsonValue.isNull() != null)
		{
			return null;
		}
		JSONArray jsonArray = jsonValue.isArray();
		if (jsonArray != null)
		{
			return jsonArray.getJavaScriptObject();
		}
		return jsonValue.isObject().getJavaScriptObject();
	}
	
	/**
	 * Check if the given native object represents an array
	 * @param obj
	 * @return
	 */
	public static native boolean isArray(JavaScriptObject obj)/*-{
		return (Object.prototype.toString.call(obj) === '[object Array]');
	}-*/;
	
	/**
	 * Return true if given object has a property with specified name. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native boolean hasPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return (object !== undefined);
    }-*/;

	
	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param output an array where the output will be written
	 */
	public static void readPropertyValue(JavaScriptObject object, String property, JsArrayMixed output)
	{
		readPropertyValue(object, property, output, true);
	}

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param output an array where the output will be written
	 * @param includeNull if true, includes null values on output array
	 */
	public static native void readPropertyValue(JavaScriptObject object, String property, JsArrayMixed output, boolean includeNull)/*-{
		function getDescendantProp(obj, desc) {
		    var arr = desc.split(".");
		    while(arr.length && (obj = obj[arr.shift()]));
		    return obj;
		}
		var ret = getDescendantProp(object, property);
		if (includeNull || (ret != null && ret !== undefined))
		{
			output.push(ret);    
		}
    }-*/;
	
	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native String readStringPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native int readIntPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native double readDoublePropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native boolean readBooleanPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
		if (typeof object == "string")
		{
			return (object == 'true');
		}
	    return object;
    }-*/;

	/**
	 * Read properties from native javascript objects. 
	 * @param object the Object where the property will be read from. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 */
	public static native JavaScriptObject readObjectPropertyValue(JavaScriptObject object, String property)/*-{
	    var arr = property.split(".");
	    while(arr.length && (object = object[arr.shift()]));
	    return object;
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input an array containing the property value.
	 * @param extractArrayContent It true, the first element from input array is used as property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, JsArrayMixed input, boolean extractArrayContent)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
		    if (input)
		    {
		    	if (extractArrayContent && input.length > 0)
		    	{
		    		obj[arr.shift()] = input[0];
		    	}
		    	else if (input.length > 0)
		    	{
		    		obj[arr.shift()] = input;
		    	}
		    	else
		    	{
		    		obj[arr.shift()] = null;
		    	}
		    }
		    else
		    {
		    	obj[arr.shift()] = null;
		    }
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, String input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, double input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, boolean input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	/**
	 * Write property to native javascript objects. 
	 * @param object the Object where the property will be written. 
	 * @param property the name of the property. You can pass inner properties using dot notation (prop1.prop2)
	 * @param input the property value.
	 */
	public static native void writePropertyValue(JavaScriptObject object, String property, JavaScriptObject input)/*-{
		var obj = object;
		var arr = property.split(".");
		while((arr.length-1) && (obj = obj[arr.shift()]));
	    if (obj)
	    {
    		obj[arr.shift()] = input;
	    }
    }-*/;

	public static String[] toArray(JsArrayString jSArray)
	{
		String[] result = new String[jSArray.length()];
		for (int i = 0; i < jSArray.length(); i++)
		{
			result[i] = jSArray.get(i);
		}
		return result;
	}

	public static List<String> toList(JsArrayString jSArray)
	{
		List<String> result = new ArrayList<String>(jSArray.length());
		for (int i = 0; i < jSArray.length(); i++)
		{
			result.add(jSArray.get(i));
		}
		return result;
	}

	public static FastList<String> toFastList(JsArrayString jSArray)
	{
		FastList<String> result = new FastList<String>();
		for (int i = 0; i < jSArray.length(); i++)
		{
			result.add(jSArray.get(i));
		}
		return result;
	}

	public static JsArrayString toJsArray(List<String> list)
	{
		JsArrayString js = JsArrayString.createArray().cast();
		for (int i = 0; i < list.size(); i++)
		{
			js.set(i, list.get(i));
		}
		return js;
	}

	public static JsArrayString toJsArray(String[] args)
	{
		JsArrayString js = JsArrayString.createArray().cast();
		for (int i = 0; i < args.length; i++)
		{
			js.set(i, args[i]);
		}
		return js;
	}

	public static native String escape(String input)/*-{
		return escape(input);
	}-*/;

	public static native String unescape(String input)/*-{
		return unescape(input);
	}-*/;
}
