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
package org.cruxframework.crux.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gwt.user.server.Base64Utils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class EncryptUtils
{
	public static String hash(String s) throws NoSuchAlgorithmException
	{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(s.getBytes());
		return Base64Utils.toBase64(digest.digest());
	}
}
