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
package org.cruxframework.crux.core.client.encoder;

import com.google.gwt.core.client.GWT;

/**
 * Cross browser Base64 encoder / decoder.
 * This class consider a "string" where each character represents an 8-bit byte.
 * If you pass a string containing characters that can't be represented in 8 bits, it will probably break.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Base64
{
	private static Impl instance;
	
	private Base64(){}

	/**
	 * Encode the given input into a base64 output. 
	 * Since DOMStrings are 16-bit-encoded strings, in most browsers calling this on a Unicode string will cause a Character 
	 * Out Of Range exception if a character exceeds the range of a 8-bit ASCII-encoded character. There are two possible methods to solve this problem:
	 * <p>the first one is to escape the whole string and then encode it;</p>
	 * <p>the second one is to convert the UTF-16 DOMString to an UTF-8 array of characters and then encode it.</p>
	 * 
	 * @param input
	 * @return
	 */
	public static String encode(String input)
	{
		return getImplementation().encode(input);
	}
	
	/**
	 * Decode the given base64 input. 
	 * This method returns a "string" where each character represents an 8-bit byte. 
	 * 
	 * @param input
	 * @return
	 */
	public static String decode(String input)
	{
		return getImplementation().decode(input);
	}

	/**
	 * Gets the encoder implementation
	 * @return
	 */
	private static Impl getImplementation()
	{
		if (instance == null)
		{
			instance = GWT.create(Impl.class);
		}
		return instance;
	}
	
	/**
	 * Implementation contract
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static interface Impl
	{
		String encode(String input);
		String decode(String input);
	}
	
	/**
	 * Implementation used by browsers that support atob and btoa functions.
	 * @author Thiago da Rosa de Bustamante
	 */
	static class NativeImpl implements Impl
	{
		@Override
        public native String encode(String input)/*-{
	        return btoa(input);
        }-*/;

		@Override
        public native String decode(String input)/*-{
	        return atob(input);
        }-*/;
	}
	
	/**
	 * Implementation used by browsers that does not support atob and btoa functions.
	 * @author Thiago da Rosa de Bustamante
	 */
	static class EmulatedImpl implements Impl
	{
		@Override
        public native String encode(String input)/*-{
		  var o1, o2, o3, bits, h1, h2, h3, h4, e=[], pad = '', c, plain, coded;
		  var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		   
		  plain = input;
		  
		  c = plain.length % 3;  // pad string to length of multiple of 3
		  if (c > 0) { while (c++ < 3) { pad += '='; plain += '\0'; } }
		  // note: doing padding here saves us doing special-case packing for trailing 1 or 2 chars
		   
		  for (c=0; c<plain.length; c+=3) {  // pack three octets into four hexets
		    o1 = plain.charCodeAt(c);
		    o2 = plain.charCodeAt(c+1);
		    o3 = plain.charCodeAt(c+2);
		      
		    bits = o1<<16 | o2<<8 | o3;
		      
		    h1 = bits>>18 & 0x3f;
		    h2 = bits>>12 & 0x3f;
		    h3 = bits>>6 & 0x3f;
		    h4 = bits & 0x3f;
		
		    // use hextets to index into code string
		    e[c/3] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);
		  }
		  coded = e.join(''); 
		  
		  // replace 'A's from padded nulls with '='s
		  coded = coded.slice(0, coded.length-pad.length) + pad;
		   
		  return coded;
        }-*/;

		@Override
        public native String decode(String input)/*-{
		  var o1, o2, o3, h1, h2, h3, h4, bits, d=[], plain, coded;
		  var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		
		  coded = input;
		  
		  for (var c=0; c<coded.length; c+=4) {  // unpack four hexets into three octets
		    h1 = b64.indexOf(coded.charAt(c));
		    h2 = b64.indexOf(coded.charAt(c+1));
		    h3 = b64.indexOf(coded.charAt(c+2));
		    h4 = b64.indexOf(coded.charAt(c+3));
		      
		    bits = h1<<18 | h2<<12 | h3<<6 | h4;
		      
		    o1 = bits>>>16 & 0xff;
		    o2 = bits>>>8 & 0xff;
		    o3 = bits & 0xff;
		    
		    d[c/4] = String.fromCharCode(o1, o2, o3);
		    // check for padding
		    if (h4 == 0x40) d[c/4] = String.fromCharCode(o1, o2);
		    if (h3 == 0x40) d[c/4] = String.fromCharCode(o1);
		  }
		  plain = d.join(''); 
		  
		  return plain; 
	  	}-*/;
	}
}
