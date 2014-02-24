/*
 * Copyright 2008 Google Inc.
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gadget.client.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Extends this interface to provide Gadget informations, like user preferences, 
 * required features and supported locales.
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public interface GadgetInfo
{
	/**
	 * Specifies the type of content and the list of views.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ContentType
	{
		String type() default "html";

		String[] views();
	}

	/**
	 * This annotation allows <code>Locale</code> tags to be added to the module
	 * specification.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = {})
	public @interface GadgetLocale
	{
		/*
		 * NB: The Target annotation above means this annotation can't be
		 * applied to any Java element, but is only usable as a value in an
		 * annotation property.
		 */

		/**
		 * The two letter country code associated with the locale.
		 * 
		 * Optional, but one of either {@link #lang()} or {@link #country()}
		 * must be specified. If omitted, the country default is
		 * <code>ALL</code>.
		 */
		String country() default "";

		/**
		 * The language associated with the locale.
		 * 
		 * Optional, but one of either {@link #lang()} or {@link #country()}
		 * must be specified. If omitted, the lang default is <code>*</code>.
		 */
		String lang() default "";

		/**
		 * The direction of the gadget.
		 * 
		 * Optional. The default is <code>LTR</code>.
		 */
		LanguageDirection language_direction() default LanguageDirection.UNSPECIFIED;

		/**
		 * A URL that points to a message bundle. Message bundles are XML files
		 * that contain the translated strings for a given locale. For more
		 * information, see Gadgets and Internationalization.
		 * 
		 * Optional.
		 */
		String messages() default "";
	}

	/**
	 * Defines the preferences associated with the gadget.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ModulePrefs
	{
		/**
		 * Optional string that lists the author of the gadget.
		 */
		String author() default "";

		/**
		 * For the <a href="http://google.com/ig/authors">authors</a> page, a
		 * statement about yourself (try to keep to ~500 characters).
		 */
		String author_aboutme() default "";

		/**
		 * For the <a href="http://google.com/ig/authors">authors</a> page, an
		 * optional string such as "Google" that indicates the author's
		 * affiliation. This attribute is required for gadgets that are included
		 * in the content directory.
		 */
		String author_affiliation() default "";

		/**
		 * Optional string that provides the gadget author's email address. You
		 * can use any email system, but you should not use a personal email
		 * address because of spam. One approach is to use an email address of
		 * the form helensmith.feedback+coolgadget@gmail.com in your gadget
		 * spec.
		 * 
		 * Gmail drops everything after the plus sign (+), so this email address
		 * is interpreted as helensmith.feedback@gmail.com.
		 */
		String author_email() default "";

		/**
		 * For the <a href="http://google.com/ig/authors">authors</a> page, a
		 * link to your website, blog, etc.
		 */
		String author_link() default "";

		/**
		 * The author's geographical location, such as
		 * "Mountain View, CA, USA ".
		 */
		String author_location() default "";

		/**
		 * For the <a href="http://google.com/ig/authors">authors</a> page, a
		 * URL to a photo (70x100 PNG format preferred, but JPG/GIF are also
		 * supported).
		 */
		String author_photo() default "";

		/**
		 * For the <a href="http://google.com/ig/authors">authors</a> page, a
		 * quote you'd like to include (try to keep to ~300 characters).
		 */
		String author_quote() default "";

		/**
		 * Optional string that tells the gadget category.
		 */
		String category() default "";

		/**
		 * Optional string that tells the gadget secondary category.
		 */
		String category2() default "";

		/**
		 * Optional string that describes the gadget.
		 */
		String description() default "";

		/**
		 * Optional string that provides the title that should be displayed for
		 * your gadget in the content directory. Should contain only the literal
		 * text to be displayed, not <a href=
		 * "http://code.google.com/apis/gadgets/docs/legacy/basic.html#UP_MP"
		 * >user preference substitution variables</a>. This is because the
		 * content directory displays a static view of your gadget, and
		 * therefore can't perform the necessary substitution to produce a
		 * reasonable title. For example, if your gadget's title is
		 * "Friends for __UP_name__", the directory is not able to perform the
		 * substitution to provide a reasonable value for " __UP_name__". So you
		 * might set your directory_title to be simply "Friends".
		 */
		String directory_title() default "";

		/**
		 * Optional positive integer that specifies the height of the area in
		 * which the gadget runs. The default height is 200.
		 */
		int height() default 200;

		/**
		 * The icon property specifies a 16px x 16px image that containers can associate 
		 * with a particular gadget (for example, the container might display the icon 
		 * next to the gadget's name in the left nav bar).
		 * @return
		 */
		String icon() default "";
		
		
		/**
		 * Specify gadget locale support
		 * 
		 */
		GadgetLocale[] locales() default {};

		/**
		 * Optional boolean that specifies whether the aspect ratio
		 * (height-to-width ratio) of the gadget is maintained when the browser
		 * is resized. Gadgets that can automatically scale vertically should
		 * set this to true, but gadgets which have a fixed height should set
		 * this to false.
		 */
		boolean scaling() default true;

		/**
		 * Optional string that gives the URL of a gadget screenshot. This must
		 * be an image on a public web site that is not blocked by robots.txt.
		 * PNG is the preferred format, though GIF and JPG are also acceptable.
		 * Gadget screenshots should be 280 pixels wide. The height of the
		 * screenshot should be the "natural" height of the gadget when it's in
		 * use. For more discussion of screenshot guidelines, see <a href=
		 * "http://code.google.com/apis/gadgets/docs/legacy/publish.html#Submitting"
		 * >Publishing to the Content Directory</a>.
		 */
		String screenshot() default "";

		/**
		 * Optional boolean that provides vertical and/or horizontal scrollbars
		 * if the content exceeds the space provided. If <code>false</code>,
		 * then the content is clipped to the height and width provided (not
		 * that width is not configurable).
		 */
		boolean scrolling() default false;

		/**
		 * Optional boolean that specifies whether users can add a gadget
		 * multiple times from a directory. The default is <code>true</code>,
		 * meaning that by default, gadgets can only be added once. Directories
		 * can handle this attribute however they choose. For example, the
		 * content directory handles <code>singleton="true"</code> by graying
		 * out and displaying the text "Added" for gadgets that have already
		 * been added. Note that changes to this attribute may not be picked up
		 * by directories right away.
		 * 
		 * This attribute doesn't prevent users from adding gadgets multiple
		 * times through the developer gadget or Add by URL. Consequently, you
		 * still need to write your gadget to support multiple instances.
		 */
		boolean singleton() default true;

		/**
		 * Optional string that gives the URL of a gadget thumbnail. This must
		 * be an image on a public web site that is not blocked by robots.txt.
		 * PNG is the preferred format, though GIF and JPG are also acceptable.
		 * Gadget thumbnails should be 120x60 pixels. For more discussion of
		 * thumbnail guidelines, see <a href=
		 * "http://code.google.com/apis/gadgets/docs/legacy/publish.html#Submitting"
		 * >Publishing to the Content Directory.</a>
		 */
		String thumbnail() default "";

		/**
		 * Optional string that provides the title of the gadget. This title is
		 * displayed in the gadget title bar on iGoogle. If this string contains
		 * user preference substitution variables and you are planning to submit
		 * your gadget to the content directory, you should also provide a
		 * directory_title for directory display.
		 */
		String title() default "";

		/**
		 * Optional string that provides a URL that the gadget title links to.
		 * For example, you could link the title to a webpage related to the
		 * gadget.
		 */
		String title_url() default "";

		/**
		 * Optional positive integer that specifies the width of the area in
		 * which the gadget runs. This setting only applies to syndicated
		 * gadgets.
		 */
		int width() default 320;
	}

	/**
	 * Annotation to turn on long filename generation.
	 * 
	 * Long manifest names include the entire package to guarantee a unique name
	 * gets generated in case there are multiple gadgets in the compilation with
	 * the same class name.
	 * 
	 * For now, the linker will complain if this annotation is missing.
	 * Eventually, the default will be to create short filenames and
	 * to not require this annotation.
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface UseLongManifestName
	{
		/**
		 * Set to <code>false</code> to enable short filenames.
		 */
		boolean value() default true;
	}

	/**
	 * Annotation to define custom UserPreferences.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface UserPreferences
	{
		Class<? extends org.cruxframework.crux.gadget.client.features.UserPreferences> value() 
		        default org.cruxframework.crux.gadget.client.features.UserPreferences.class;
	}
	
	/**
	 * @return the UserPreferences object
	 */
	org.cruxframework.crux.gadget.client.features.UserPreferences getUserPreferences();	
}
