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

import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JsArrayInteger;

/**
 * MD5 Message Digest Algorithm, as defined in RFC 1321.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class MD5
{
	/**
	 * Generate a MD5 hash and output it as an hex string
	 * @param s valid UTF-16 string
	 * @return 
	 */
	public static String hexMD5(String s)
	{ 
		return StringUtils.toHEXString(rawMD5(StringUtils.toUTF8(s))); 
	}
	
	/**
	 * Generate a MD5 hash and output it as a base64 string
	 * @param s valid UTF-16 string
	 * @return 
	 */
	public static String base64MD5(String s)    
	{ 
		return Base64.encode(rawMD5(StringUtils.toUTF8(s))); 
	}
	
	/**
	 * Calculate the HMAC-MD5, of a key and some data (raw strings)
	 * and output it as an hex4 string
	 */
	public static String hexHmacMD5(String k, String d)
	{ 
		return StringUtils.toHEXString(rawHmacMD5(StringUtils.toUTF8(k), StringUtils.toUTF8(d))); 
	}
	
	/**
	 * Calculate the HMAC-MD5, of a key and some data (raw strings)
	 * and output it as a base64 string
	 */
	public static String base64HmacMD5(String k, String d)
	{ 
		return Base64.encode(rawHmacMD5(StringUtils.toUTF8(k), StringUtils.toUTF8(d))); 
	}
	
	/**
	 * Calculate the HMAC-MD5, of a key and some data (raw strings)
	 */
	public static String  rawHmacMD5(String key, String data)
	{
		JsArrayInteger bkey = rstr2binl(key);
		if(bkey.length() > 16) bkey = binl2md5(bkey, key.length() * 8);

		JsArrayInteger ipad = JsArrayInteger.createArray().cast();
		JsArrayInteger opad = JsArrayInteger.createArray().cast();
		ipad.setLength(16);
		opad.setLength(16);
		for(int i = 0; i < 16; i++)
		{
			ipad.set(i, bkey.get(i) ^ 0x36363636);
			opad.set(i, bkey.get(i) ^ 0x5C5C5C5C);
		}

		JsArrayInteger hash = binl2md5(concat(ipad, rstr2binl(data)), 512 + data.length() * 8);
		return binl2rstr(binl2md5(concat(opad, hash), 512 + 128));
	}
	
	/**
	 * Calculate the MD5 of a raw string
	 */
	public static String rawMD5(String s)
	{
  		return binl2rstr(binl2md5(rstr2binl(s), s.length() * 8));
	}

	/**
	 * Concatenate two arrays.
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static native JsArrayInteger concat(JsArrayInteger array1, JsArrayInteger array2)/*-{
		return array1.concat(array2);
	}-*/;

	/**
	 * Convert a raw string to an array of little-endian words
	 * Characters >255 have their high-byte silently ignored.
	 */
	private static native JsArrayInteger rstr2binl(String input)/*-{
	  var output = Array(input.length >> 2);
	  for(var i = 0; i < output.length; i++)
	    output[i] = 0;
	  for(var i = 0; i < input.length * 8; i += 8)
	    output[i>>5] |= (input.charCodeAt(i / 8) & 0xFF) << (i%32);
	  return output;
	}-*/;

	/**
	 * Convert an array of little-endian words to a string
	 */
	private static native String binl2rstr(JsArrayInteger input)/*-{
	  var output = "";
	  for(var i = 0; i < input.length * 32; i += 8)
	    output += String.fromCharCode((input[i>>5] >>> (i % 32)) & 0xFF);
	  return output;
	}-*/;
	
	/**
	 * Calculate the MD5 of an array of little-endian words, and a bit length.
	 */
	private static native JsArrayInteger binl2md5(JsArrayInteger x, int len)/*-{

		// These functions implement the four basic operations the algorithm uses.
		function md5_cmn(q, a, b, x, s, t)
		{
		  return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s),b);
		}
		function md5_ff(a, b, c, d, x, s, t)
		{
		  return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
		}
		function md5_gg(a, b, c, d, x, s, t)
		{
		  return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
		}
		function md5_hh(a, b, c, d, x, s, t)
		{
		  return md5_cmn(b ^ c ^ d, a, b, x, s, t);
		}
		function md5_ii(a, b, c, d, x, s, t)
		{
		  return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
		}
	
		// Add integers, wrapping at 2^32. This uses 16-bit operations internally
		// to work around bugs in some JS interpreters.
		function safe_add(x, y)
		{
		  var lsw = (x & 0xFFFF) + (y & 0xFFFF);
		  var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
		  return (msw << 16) | (lsw & 0xFFFF);
		}
	
		// Bitwise rotate a 32-bit number to the left.
		function bit_rol(num, cnt)
		{
		  return (num << cnt) | (num >>> (32 - cnt));
		}	
	  
		// append padding 
		x[len >> 5] |= 0x80 << ((len) % 32);
		x[(((len + 64) >>> 9) << 4) + 14] = len;
		
		var a =  1732584193;
		var b = -271733879;
		var c = -1732584194;
		var d =  271733878;
		
		for(var i = 0; i < x.length; i += 16)
		{
		  var olda = a;
		  var oldb = b;
		  var oldc = c;
		  var oldd = d;
		
		  a = md5_ff(a, b, c, d, x[i+ 0], 7 , -680876936);
		  d = md5_ff(d, a, b, c, x[i+ 1], 12, -389564586);
		  c = md5_ff(c, d, a, b, x[i+ 2], 17,  606105819);
		  b = md5_ff(b, c, d, a, x[i+ 3], 22, -1044525330);
		  a = md5_ff(a, b, c, d, x[i+ 4], 7 , -176418897);
		  d = md5_ff(d, a, b, c, x[i+ 5], 12,  1200080426);
		  c = md5_ff(c, d, a, b, x[i+ 6], 17, -1473231341);
		  b = md5_ff(b, c, d, a, x[i+ 7], 22, -45705983);
		  a = md5_ff(a, b, c, d, x[i+ 8], 7 ,  1770035416);
		  d = md5_ff(d, a, b, c, x[i+ 9], 12, -1958414417);
		  c = md5_ff(c, d, a, b, x[i+10], 17, -42063);
		  b = md5_ff(b, c, d, a, x[i+11], 22, -1990404162);
		  a = md5_ff(a, b, c, d, x[i+12], 7 ,  1804603682);
		  d = md5_ff(d, a, b, c, x[i+13], 12, -40341101);
		  c = md5_ff(c, d, a, b, x[i+14], 17, -1502002290);
		  b = md5_ff(b, c, d, a, x[i+15], 22,  1236535329);
		
		  a = md5_gg(a, b, c, d, x[i+ 1], 5 , -165796510);
		  d = md5_gg(d, a, b, c, x[i+ 6], 9 , -1069501632);
		  c = md5_gg(c, d, a, b, x[i+11], 14,  643717713);
		  b = md5_gg(b, c, d, a, x[i+ 0], 20, -373897302);
		  a = md5_gg(a, b, c, d, x[i+ 5], 5 , -701558691);
		  d = md5_gg(d, a, b, c, x[i+10], 9 ,  38016083);
		  c = md5_gg(c, d, a, b, x[i+15], 14, -660478335);
		  b = md5_gg(b, c, d, a, x[i+ 4], 20, -405537848);
		  a = md5_gg(a, b, c, d, x[i+ 9], 5 ,  568446438);
		  d = md5_gg(d, a, b, c, x[i+14], 9 , -1019803690);
		  c = md5_gg(c, d, a, b, x[i+ 3], 14, -187363961);
		  b = md5_gg(b, c, d, a, x[i+ 8], 20,  1163531501);
		  a = md5_gg(a, b, c, d, x[i+13], 5 , -1444681467);
		  d = md5_gg(d, a, b, c, x[i+ 2], 9 , -51403784);
		  c = md5_gg(c, d, a, b, x[i+ 7], 14,  1735328473);
		  b = md5_gg(b, c, d, a, x[i+12], 20, -1926607734);
		
		  a = md5_hh(a, b, c, d, x[i+ 5], 4 , -378558);
		  d = md5_hh(d, a, b, c, x[i+ 8], 11, -2022574463);
		  c = md5_hh(c, d, a, b, x[i+11], 16,  1839030562);
		  b = md5_hh(b, c, d, a, x[i+14], 23, -35309556);
		  a = md5_hh(a, b, c, d, x[i+ 1], 4 , -1530992060);
		  d = md5_hh(d, a, b, c, x[i+ 4], 11,  1272893353);
		  c = md5_hh(c, d, a, b, x[i+ 7], 16, -155497632);
		  b = md5_hh(b, c, d, a, x[i+10], 23, -1094730640);
		  a = md5_hh(a, b, c, d, x[i+13], 4 ,  681279174);
		  d = md5_hh(d, a, b, c, x[i+ 0], 11, -358537222);
		  c = md5_hh(c, d, a, b, x[i+ 3], 16, -722521979);
		  b = md5_hh(b, c, d, a, x[i+ 6], 23,  76029189);
		  a = md5_hh(a, b, c, d, x[i+ 9], 4 , -640364487);
		  d = md5_hh(d, a, b, c, x[i+12], 11, -421815835);
		  c = md5_hh(c, d, a, b, x[i+15], 16,  530742520);
		  b = md5_hh(b, c, d, a, x[i+ 2], 23, -995338651);
		
		  a = md5_ii(a, b, c, d, x[i+ 0], 6 , -198630844);
		  d = md5_ii(d, a, b, c, x[i+ 7], 10,  1126891415);
		  c = md5_ii(c, d, a, b, x[i+14], 15, -1416354905);
		  b = md5_ii(b, c, d, a, x[i+ 5], 21, -57434055);
		  a = md5_ii(a, b, c, d, x[i+12], 6 ,  1700485571);
		  d = md5_ii(d, a, b, c, x[i+ 3], 10, -1894986606);
		  c = md5_ii(c, d, a, b, x[i+10], 15, -1051523);
		  b = md5_ii(b, c, d, a, x[i+ 1], 21, -2054922799);
		  a = md5_ii(a, b, c, d, x[i+ 8], 6 ,  1873313359);
		  d = md5_ii(d, a, b, c, x[i+15], 10, -30611744);
		  c = md5_ii(c, d, a, b, x[i+ 6], 15, -1560198380);
		  b = md5_ii(b, c, d, a, x[i+13], 21,  1309151649);
		  a = md5_ii(a, b, c, d, x[i+ 4], 6 , -145523070);
		  d = md5_ii(d, a, b, c, x[i+11], 10, -1120210379);
		  c = md5_ii(c, d, a, b, x[i+ 2], 15,  718787259);
		  b = md5_ii(b, c, d, a, x[i+ 9], 21, -343485551);
		
		  a = safe_add(a, olda);
		  b = safe_add(b, oldb);
		  c = safe_add(c, oldc);
		  d = safe_add(d, oldd);
		}
		return Array(a, b, c, d);
	}-*/;
}
