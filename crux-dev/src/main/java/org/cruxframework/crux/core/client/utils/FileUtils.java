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

import org.cruxframework.crux.core.client.encoder.Base64;
import org.cruxframework.crux.core.client.file.Blob;

import com.google.gwt.typedarrays.client.ArrayBufferNative;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Uint8Array;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class FileUtils
{
	/**
	 * Get the file extension based in the filename
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName)
	{
		String extension = "";

		if(fileName == null || fileName == "")
		{
			return extension;
		}

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p)
		{
			extension = fileName.substring(i+1);
		}

		return extension;
	}
	
	/**
	 * Create a new Blob from the dataURI string
	 * @param dataURI
	 * @return
	 */
	public static Blob fromDataURI(String dataURI)
	{
		String[] strings = dataURI.split(",");
		
		String byteString = getByteString(strings[1], strings[0]);
		String mimeString = strings[0].split(":")[1].split(";")[0];

		int length = byteString.length();
		ArrayBuffer ab = ArrayBufferNative.create(length);
		Uint8Array ia = Uint8ArrayNative.create(ab);

		for (int i = 0; i < length; i++) 
		{
			ia.set(i, StringUtils.charCodeAt(byteString, i));
		}

		return Blob.createIfSupported(ab, mimeString);
	}

	/*
	 * Create a new File from the dataURI string
	 * @param dataURI
	 * @param fileName
	 * @return
	 * TODO check status of https://bugzilla.mozilla.org/show_bug.cgi?id=819900
	 */
//	public static File fromDataURI(String dataURI, String fileName)
//	{
//		return File.createIfSupported(fromDataURI(dataURI), fileName);
//	}
	
	/**
	 *  convert base64/URLEncoded data component to raw binary data held in a string
	 * @param str
	 * @return
	 */
	private static String getByteString(String str, String mimeDecl)
	{
		String byteString;
		if (mimeDecl.indexOf("base64") >= 0)
		{
			byteString = Base64.decode(str);
		}
		else
		{
			byteString = JsUtils.unescape(str);
		}
		return byteString;
	};
}
