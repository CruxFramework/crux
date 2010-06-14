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
package br.com.sysmap.crux.core.client.controller.document.invoke;

/**
 * A toolkit for escaping special characters during cross document
 * communication.
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class SerializationEscapeUtils
{
	/**
	 * Number of escaped JS Chars.
	 */
	private static final int NUMBER_OF_JS_ESCAPED_CHARS = 128;

	/**
	 * A list of any characters that need escaping when printing a JavaScript
	 * string literal. Contains a 0 if the character does not need escaping,
	 * otherwise contains the character to escape with.
	 */
	private static final char[] JS_CHARS_ESCAPED = new char[NUMBER_OF_JS_ESCAPED_CHARS];

	/**
	 * This defines the character used by JavaScript to mark the start of an
	 * escape sequence.
	 */
	private static final char JS_ESCAPE_CHAR = '\\';

	/**
	 * This defines the character used to enclose JavaScript strings.
	 */
	private static final char JS_QUOTE_CHAR = '\"';

	/**
	 * Index into this array using a nibble, 4 bits, to get the corresponding
	 * hexa-decimal character representation.
	 */
	private static final char NIBBLE_TO_HEX_CHAR[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static final char NON_BREAKING_HYPHEN = '\u2011';

	static
	{
		/*
		 * NOTE: The JS VM in IE6 & IE7 do not interpret \v correctly. They
		 * convert JavaScript Vertical Tab character '\v' into 'v'. As such, we
		 * do not use the short form of the unicode escape here.
		 */
		JS_CHARS_ESCAPED['\u0000'] = '0';
		JS_CHARS_ESCAPED['\b'] = 'b';
		JS_CHARS_ESCAPED['\t'] = 't';
		JS_CHARS_ESCAPED['\n'] = 'n';
		JS_CHARS_ESCAPED['\f'] = 'f';
		JS_CHARS_ESCAPED['\r'] = 'r';

		JS_CHARS_ESCAPED[JS_ESCAPE_CHAR] = JS_ESCAPE_CHAR;
		JS_CHARS_ESCAPED[JS_QUOTE_CHAR] = JS_QUOTE_CHAR;
	}

	/**
	 * Quotes and escapes a string
	 * 
	 * @param raw
	 * @return
	 */
	public String quoteAndScape(String raw)
	{
		return "\"" + escape(raw) + "\"";
	}

	/**
	 * Escapes a string.
	 * 
	 * @param raw
	 * @return
	 */
	private static String escape(String raw)
	{
		StringBuilder strb = null;

		if (raw != null)
		{
			for (int i = 0; i < raw.length(); i++)
			{
				char c = raw.charAt(i);
				if (needsUnicodeEscape(c))
				{
					if (strb == null)
					{
						strb = new StringBuilder();
						strb.append(raw.substring(0, i));
					}

					strb.append(unicodeEscape(c));
				}
				else
				{
					if (strb != null)
					{
						strb.append(c);
					}
				}
			}
		}

		if (strb != null)
		{
			return strb.toString();
		}
		else
		{
			return raw;
		}
	}

	/**
	 * Returns <code>true</code> if the character requires the \\uXXXX unicode
	 * character escape sequence. This is necessary if the raw character could
	 * be consumed and/or interpreted as a special character when the JSON
	 * encoded response is evaluated. For example, 0x2028 and 0x2029 are
	 * alternate line endings for JS per ECMA-232, which are respected by
	 * Firefox and Mozilla.
	 * <p>
	 * Notes:
	 * <ol>
	 * <li>The following cases are a more conservative set of cases which are
	 * are in the future proofing space as opposed to the required minimal set.
	 * We could remove these and still pass our tests.
	 * <ul>
	 * <li>UNASSIGNED - 6359</li>
	 * <li>NON_SPACING_MARK - 530</li>
	 * <li>ENCLOSING_MARK - 10</li>
	 * <li>COMBINING_SPACE_MARK - 131</li>
	 * <li>SPACE_SEPARATOR - 19</li>
	 * <li>CONTROL - 65</li>
	 * <li>PRIVATE_USE - 6400</li>
	 * <li>DASH_PUNCTUATION - 1</li>
	 * <li>Total Characters Escaped: 13515</li>
	 * </ul>
	 * </li>
	 * <li>The following cases are the minimal amount of escaping required to
	 * prevent test failure.
	 * <ul>
	 * <li>LINE_SEPARATOR - 1</li>
	 * <li>PARAGRAPH_SEPARATOR - 1</li>
	 * <li>FORMAT - 32</li>
	 * <li>SURROGATE - 2048</li>
	 * <li>Total Characters Escaped: 2082</li></li>
	 * </ul> </li>
	 * </ol>
	 * 
	 * @param ch
	 *            character to check
	 * @return <code>true</code> if the character requires the \\uXXXX unicode
	 *         character escape
	 */
	private static boolean needsUnicodeEscape(char ch)
	{
		switch (ch)
		{
			case ' ':
				// ASCII space gets caught in SPACE_SEPARATOR below, but does
				// not need to be escaped
				return false;
			case JS_QUOTE_CHAR:
			case JS_ESCAPE_CHAR:
				// these must be quoted or they will break the protocol
				return true;
			case NON_BREAKING_HYPHEN:
				// This can be expanded into a break followed by a hyphen
				return true;
			default:
				switch (Character.getType(ch))
				{
					// Conservative
					case Character.COMBINING_SPACING_MARK:
					case Character.ENCLOSING_MARK:
					case Character.NON_SPACING_MARK:
					case Character.UNASSIGNED:
					case Character.PRIVATE_USE:
					case Character.SPACE_SEPARATOR:
					case Character.CONTROL:

						// Minimal
					case Character.LINE_SEPARATOR:
					case Character.FORMAT:
					case Character.PARAGRAPH_SEPARATOR:
					case Character.SURROGATE:
						return true;

					default:
						break;
				}
				break;
		}
		return false;
	}

	/**
	 * Writes a safe escape sequence for a character. Some characters have a
	 * short form, such as \n for U+000D, while others are represented as \\xNN
	 * or \\uNNNN.
	 * 
	 * @param ch
	 *            character to unicode escape
	 * @param charVector
	 *            char vector to receive the unicode escaped representation
	 */
	private static String unicodeEscape(char ch)
	{
		String escaped = "" + JS_ESCAPE_CHAR;

		if (ch < NUMBER_OF_JS_ESCAPED_CHARS && JS_CHARS_ESCAPED[ch] != 0)
		{
			escaped += JS_CHARS_ESCAPED[ch];
		}
		else if (ch < 256)
		{
			escaped += 'x';
			escaped += NIBBLE_TO_HEX_CHAR[(ch >> 4) & 0x0F];
			escaped += NIBBLE_TO_HEX_CHAR[ch & 0x0F];
		}
		else
		{
			escaped += 'u';
			escaped += NIBBLE_TO_HEX_CHAR[(ch >> 12) & 0x0F];
			escaped += NIBBLE_TO_HEX_CHAR[(ch >> 8) & 0x0F];
			escaped += NIBBLE_TO_HEX_CHAR[(ch >> 4) & 0x0F];
			escaped += NIBBLE_TO_HEX_CHAR[ch & 0x0F];
		}

		return escaped;
	}
}